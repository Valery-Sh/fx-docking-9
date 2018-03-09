/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY windowType, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.dragging.view;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public class NodeResizer implements EventHandler<MouseEvent> {

    private WindowType windowType;

    private Window nodeWindow;
    private boolean applyTranslateXY;

    private boolean hideOnMouseRelease;

    private final ObjectProperty<Window> window = new SimpleObjectProperty<>();

    private final Region node;

    private WindowResizer windowResizer;

    public enum WindowType {
        STAGE,
        POPUPCONTROL,
        POPUP
    }

    private final Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private boolean cursorSupported = false;

    private double translateX;
    private double translateY;
    private Cursor saveCursor;
    private boolean applyTranskateXY;

    public NodeResizer(Region node) {
        this.node = node;
        //
        // set defalts 
        //
        this.applyTranslateXY = false;
        this.hideOnMouseRelease = false;
        this.windowType = WindowType.POPUPCONTROL;

    }

    protected void bindWindowPosition(Window owner) {
        owner.xProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });
        owner.yProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });

    }

    protected void bindWindowDimensions() {

        getNode().widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });
        getNode().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });

    }

    protected Stage createStage() {
        System.err.println("NodeResizer: STAGE ");
        Stage stage = new Stage();
        stage.setOnShown(e -> {
            DockRegistry.register(stage, true); // true means exclude when searfor target window
        });
        stage.setOnHidden(e -> {
            DockRegistry.unregister(stage);
        });

        setWindow(stage);
        stage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        root.setId("nodeResizer " + getNode().getClass().getSimpleName());

        Border b = new NodeResizerBorder().getBorder();
        root.setBorder(b);
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
        nodeWindow = getNode().getScene().getWindow();
        ((Stage) getWindow()).initOwner(nodeWindow);
//        System.err.println("NodeResizer: ownerWindow = " + nodeWindow.getScene().getRoot().getId());

        ((Stage) getWindow()).show();

        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());

        /*        stage.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        stage.setMaxWidth(root.maxWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMaxHeight(root.maxHeight(DockUtil.widthOf(getNode())) + insetsHeight);
         */
        bindWindowPosition(nodeWindow);
        bindWindowDimensions();

        windowBounds(stage, getNode());

        return stage;
    }

    protected PopupControl createPopupControl_NEW() {
        System.err.println("NodeResizer: PopupControl ");
        PopupControl popup = new PopupControl();
        popup.setOnShown(e -> {
            DockRegistry.register(popup, true); // true means exclude when searfor target window
        });
        popup.setOnHidden(e -> {
            DockRegistry.unregister(popup);
        });

        setWindow(popup);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
        Border b = new NodeResizerBorder().getBorder();
        root.setBorder(b);

        root.applyCss();
        popup.getScene().setRoot(root);

        //((PopupControl) getWindow()).show(getNode().getScene().getWindow());
        Bounds nb = getNode().localToScreen(getNode().getBoundsInLocal());
        popup.show(getNode(), nb.getMinX(), nb.getMinX());

        ObjectProperty<Bounds> nodeScreenBounds = new SimpleObjectProperty<>();
        getNode().localToSceneTransformProperty().addListener((o, ov, nv) -> {
            nodeScreenBounds.set(getNode().localToScreen(getNode().getBoundsInLocal()));
        });
        nodeScreenBounds.addListener((o, ov, nv) -> {
            if ( popup != null && nv != null) {
                popup.setX(nv.getMinX());
                popup.setY(nv.getMinY());
            }
        });

        double borderWidth = 0;
        double borderHeight = 0;
        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());
        root.prefWidthProperty().bind(getNode().widthProperty().add(borderWidth));
        root.prefHeightProperty().bind(getNode().heightProperty().add(borderHeight));

        root.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        root.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);

        //getNode().setPrefWidth(getNode().getWidth());
        //getNode().setPrefHeight(getNode().getHeight());

        //bindWindowPosition(getNode().getScene().getWindow());
        windowBounds(getWindow(), getNode());

        nodeWindow = getNode().getScene().getWindow();

        return popup;
    }

    protected PopupControl createPopupControl() {
        System.err.println("NodeResizer: PopupControl ");
        PopupControl popup = new PopupControl();
        popup.setOnShown(e -> {
            DockRegistry.register(popup, true); // true means exclude when searfor target window
        });
        popup.setOnHidden(e -> {
            DockRegistry.unregister(popup);
        });

        setWindow(popup);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
        Border b = new NodeResizerBorder().getBorder();
        root.setBorder(b);

        root.applyCss();
        popup.getScene().setRoot(root);

        //((PopupControl) getWindow()).show(getNode().getScene().getWindow());
        Bounds nb = getNode().localToScreen(getNode().getBoundsInLocal());
        ((PopupControl) getWindow()).show(getNode(), nb.getMinX(), nb.getMinX());

        //popup.setOnShowing(v -> {
        double borderWidth = 0;
        double borderHeight = 0;
        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());

        root.prefWidthProperty().bind(getNode().widthProperty().add(borderWidth));
        root.prefHeightProperty().bind(getNode().heightProperty().add(borderHeight));

        root.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        root.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());

        bindWindowPosition(getNode().getScene().getWindow());

        windowBounds(getWindow(), getNode());

        nodeWindow = getNode().getScene().getWindow();

        return popup;
    }

    public Window show() {
        Window newOwner = getNode().getScene().getWindow();
        if (getWindow() != null && nodeWindow == newOwner) {
            if ((getWindow() instanceof Stage) && windowType != WindowType.STAGE) {
                hide();
            } else if (windowType == WindowType.STAGE) {
                ((Stage) getWindow()).show();
                return getWindow();
            } else if ((getWindow() instanceof PopupControl) && windowType != WindowType.POPUPCONTROL) {
                getWindow().hide();
            } else if (windowType == WindowType.POPUPCONTROL) {
                ((PopupControl) getWindow()).show(nodeWindow);
                return getWindow();
            } else if ((getWindow() instanceof Popup) && windowType != WindowType.POPUP) {
                getWindow().hide();
            } else if (windowType == WindowType.POPUP) {
                ((Popup) getWindow()).show(nodeWindow);
                return getWindow();
            }
        }

        if (windowType == WindowType.STAGE) {
            createStage();
        } else if (windowType == WindowType.POPUPCONTROL) {
            createPopupControl();
        }

        if (windowResizer == null) {
            windowResizer = new NodeResizeExecutor(this);
        }
        translateX = getNode().getTranslateX();
        translateY = getNode().getTranslateY();

        getWindow().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        getWindow().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        getWindow().addEventFilter(MouseEvent.MOUSE_MOVED, this);
        getWindow().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        return getWindow();
    }

    public void hide() {
        if (getWindow() != null) {
            ((Window) getWindow()).hide();
        }
    }

    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }

    public boolean isCursorSupported(Cursor cursor) {
        if (cursor == null || cursor == Cursor.DEFAULT) {
            return false;
        }
        boolean retval = false;
        for (Cursor c : getSupportedCursors()) {
            if (c == cursor) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public boolean isHideOnMouseRelease() {
        return hideOnMouseRelease;
    }

    public void setHideOnMouseRelease(boolean hideOnMouseRelease) {
        this.hideOnMouseRelease = hideOnMouseRelease;
    }

    protected static Bounds windowBounds(Window window, Region node) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        double borderWidth = 0;
        double borderHeight = 0;
        double borderX = 0;
        double borderY = 0;

        Region root = (Region) window.getScene().getRoot();
        if (root.getInsets() != null) {
            borderX = root.getInsets().getLeft();
            borderY = root.getInsets().getTop();

            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        window.setX(b.getMinX() - borderX);
        window.setY(b.getMinY() - borderY);

        if (window instanceof Stage) {
//            System.err.println("b.getWidth() = " + node.getWidth() + "; borderWidth = " + borderWidth);
//            System.err.println("b.getHeight() = " + node.getHeight() + "; borderHeight = " + borderHeight);

            window.setWidth(b.getWidth() + borderWidth);
            window.setHeight(b.getHeight() + borderHeight);
        }
        return b;
    }

    public ObjectProperty<Window> window() {
        return window;
    }

    public Window getWindow() {
        return window.get();
    }

    protected void setWindow(Window window) {
        this.window.set(window);
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
    }

    public boolean isApplyTranslateXY() {
        return applyTranslateXY;
    }

    public void setApplyFtranslateXY(boolean useTranslateXY) {
        this.applyTranslateXY = useTranslateXY;
    }

    public Region getNode() {
        return node;
    }

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
            Cursor c = NodeResizeExecutor.cursorBy(ev, (Region) getWindow().getScene().getRoot());

            if (!isCursorSupported(c)) {
                getWindow().getScene().setCursor(Cursor.DEFAULT);
            } else {
                getWindow().getScene().setCursor(c);
            }
            if (!c.equals(Cursor.DEFAULT)) {
                ev.consume();
            }

        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            saveCursor = NodeResizeExecutor.cursorBy(ev, (Region) getWindow().getScene().getRoot());
            if (!applyTranskateXY) {
                translateX = getNode().getTranslateX();
                translateY = getNode().getTranslateY();
            }

            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                getWindow().getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            windowResizer.start(ev, getWindow(), getWindow().getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            if (!cursorSupported) {
                return;
            }
            if (!windowResizer.isStarted()) {
                windowResizer.start(ev, getWindow(), getWindow().getScene().getCursor(), getSupportedCursors());
            } else {
                Platform.runLater(() -> {
                    windowResizer.resize(ev);
                });
            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (!isApplyTranslateXY()) {
                double tX = getNode().getTranslateX();
                double tY = getNode().getTranslateY();

                getNode().setTranslateX(translateX);
                getNode().setTranslateY(translateY);

                double oldLX = getNode().getLayoutX();
                double oldLY = getNode().getLayoutY();
                //
                // Do layoutXY. the node can be relocated (for instans it resides in a Pane)
                // it will be relocated
                //
                getNode().setLayoutX(getNode().getLayoutX() + tX);
                getNode().setLayoutY(getNode().getLayoutY() + tY);

                //getNode().getParent().layout();
                getNode().setPrefWidth(getNode().getWidth());
                windowBounds(getWindow(), getNode());
                commitResize();
            } else {
                windowBounds(getWindow(), getNode());
                commitResize();
            }

        }
    }

    public WindowResizer getResizer() {
        return windowResizer;
    }

    protected void commitResize() {
        if (hideOnMouseRelease) {
            getWindow().hide();
        }
    }
}
