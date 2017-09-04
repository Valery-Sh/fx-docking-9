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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class NodeResizer2 {


    private double savePrefWidth;
    private double savePrefHeight;

    private WindowType windowType;

    private Window nodeWindow;
    private boolean applyTranslateXY;

    private boolean hideOnMouseRelease;

    private ObjectProperty<Window> window = new SimpleObjectProperty<>();

    private Region node;
    private NodeLayout nodeLayout;

    public enum WindowType {
        //APPLY_TRANSLATE_TRANSFORS,
        //HIDE_POPUP_ON_MOUSE_RELEASE,
        STAGE,
        POPUPCONTROL,
        POPUP
    }

    public NodeResizer2(Region node) {
        this.node = node;
        this.nodeLayout = new NodeLayout(node);
        //
        // set defalts 
        //
        this.applyTranslateXY = false;
        this.hideOnMouseRelease = false;
        this.windowType = WindowType.POPUPCONTROL;

        init();
    }

    private void init() {
    }

    /*    public double getWindowMinWidth() {
        double retval = -1;
        if (windowType == windowType.STAGE) {
            retval = ((Stage) getWindow()).getMinWidth();
        } else if (windowType == windowType.POPUPCONTROL) {
            retval = ((PopupControl) getWindow()).getMinWidth();
        }
        return retval;
    }

    public double getWindowMinHeight() {
        double retval = -1;
        if (windowType == windowType.STAGE) {
            retval = ((Stage) getWindow()).getMinHeight();
        } else if (windowType == windowType.POPUPCONTROL) {
            retval = ((PopupControl) getWindow()).getMinHeight();
        }
        return retval;

    }
     */
    protected void bindWindowPosition(Window owner) {
        owner.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                windowBounds(getWindow(), getNode());
            }
        });
        owner.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                windowBounds(getWindow(), getNode());
            }
        });

    }

    protected void bindNodeDimensions() {
        Rectangle r;
        
        getNode().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                windowBounds(getWindow(), getNode());
            }
        });
        getNode().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                windowBounds(getWindow(), getNode());
            }
        });

    }

    protected Stage createStage() {
        Stage stage = new Stage();
        setWindow(stage);
        stage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red;");
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
        nodeWindow = getNode().getScene().getWindow();
        ((Stage) getWindow()).initOwner(nodeWindow);

        ((Stage) getWindow()).show();

        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());

        stage.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        stage.setMaxWidth(root.maxWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMaxHeight(root.maxHeight(DockUtil.widthOf(getNode())) + insetsHeight);

        bindWindowPosition(nodeWindow);
        bindNodeDimensions();

        windowBounds(stage, getNode());

        return stage;
    }

    protected PopupControl createPopupControl() {
        PopupControl popup = new PopupControl();
        setWindow(popup);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red;");
        root.applyCss();
        popup.getScene().setRoot(root);
        //window().set(createPopupControl());
        ((PopupControl) getWindow()).show(getNode().getScene().getWindow());

        //popup.setOnShowing(v -> {
        double borderWidth = 0;
        double borderHeight = 0;
        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        System.err.println("IPOPUP INSETS w= " + borderWidth);
        System.err.println("IPOPUP INSETS h= " + borderHeight);
        //popup.getScene().setFill(null);
        //root.prefWidthProperty().bind(getNode().prefWidthProperty().add(20));
        //root.prefHeightProperty().bind(getNode().prefHeightProperty().add(20));
        //popup.widthProperty()
        //});

        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
//        getNode().setPrefWidth(b.getWidth());
//        getNode().setPrefHeight(b.getHeight());
        root.prefWidthProperty().bind(getNode().prefWidthProperty().add(borderWidth));
        root.prefHeightProperty().bind(getNode().prefHeightProperty().add(borderHeight));

        root.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        root.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        //root.setMaxWidth(getNode().getPrefWidth());
        //root.setMaxHeight(getNode().getPrefHeight());

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
        System.err.println("START INSETS = " + ((Region) getNode().getScene().getRoot()).getInsets());
        //new MouseResizeHandler(getWindow(), getNode(), isApplyTranslateXY());
        new MouseResizeHandler(this);
        return getWindow();
    }

    public void hide() {
        if (getWindow() != null) {
            ((Window) getWindow()).hide();
        }
    }

    public void useStageForResizeBorder() {

    }

    public void usePopupControlForResizeBorder() {

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
            window.setWidth(b.getWidth() + borderWidth);
            window.setHeight(b.getHeight() + borderHeight);
        } else {
            //root.setPrefWidth(b.getWidth() + borderWidth);
            //root.setPrefHeight(b.getHeight() + borderHeight);
        }
        //} else {

        //}
        return b;
    }

    protected static void adjustWidth(Window window, Region node) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        double borderWidth = 0;
        double borderHeight = 0;

        Region root = (Region) window.getScene().getRoot();
        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        //window.sizeToScene();
        //window.setWidth(b.getWidth() + borderWidth);
        //window.setHeight(b.getHeight() + borderHeight);
    }

    public ObjectProperty<Window> window() {
        return window;
    }

    /*    public <T> T getWindow() {
        return (T) window.get();
    }

    public <T> void setWindow(T window) {
        this.window.set((Window) window);
    }
     */
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

    public double getSavedPrefWidth() {
        return savePrefWidth;
    }

    public void savePrefWidth(double savePrefWidth) {
        this.savePrefWidth = savePrefWidth;
    }

    public double getSavedPrefHeight() {
        return savePrefHeight;
    }

    public void savePrefHeight(double savePrefHeight) {
        this.savePrefHeight = savePrefHeight;
    }

    public static class MouseResizeHandler implements EventHandler<MouseEvent> {

        private Cursor[] supportedCursors = new Cursor[]{
            Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
            Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
        };

        private boolean cursorSupported = false;
        private NodeResizer2 nodeResizer;

        private WindowResizer windowResizer;

        private double translateX;
        private double translateY;
        private Cursor saveCursor;
        private boolean applyTranskateXY;

        public MouseResizeHandler(NodeResizer2 nodeResizer) {
            this.nodeResizer = nodeResizer;
            init();
        }

        /*        public MouseResizeHandler(Window window, Node node) {
            this(window, node, false);
        }

        public MouseResizeHandler(Window window, Node node, boolean applyTranslateXY) {
            this.window = window;
            this.node = node;
            this.applyTranskateXY = applyTranslateXY;
            init();
        }

        public MouseResizeHandler(Window window) {
            this.window = window;
            init();
        }
         */
        private void init() {

            translateX = getNode().getTranslateX();
            translateY = getNode().getTranslateY();
            nodeResizer.savePrefWidth(getNode().getPrefWidth());
            nodeResizer.savePrefHeight(getNode().getPrefHeight());
            //windowResizer = new StageKindlResizerImpl(getWindow(), getNode());
            windowResizer = new StageKindlResizerImpl(nodeResizer);

            getWindow().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
            getWindow().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            getWindow().addEventFilter(MouseEvent.MOUSE_MOVED, this);
            getWindow().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        }

        protected Window getWindow() {
            return nodeResizer.getWindow();
        }

        public Region getNode() {
            return nodeResizer.getNode();
        }

        public WindowResizer getResizer() {
            return windowResizer;
        }

        public Cursor[] getSupportedCursors() {
            return supportedCursors;
        }

        public void setSupportedCursors(Cursor[] supportedCursors) {
            this.supportedCursors = supportedCursors;
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Cursor c = StageKindlResizerImpl.cursorBy(ev, (Region) getWindow().getScene().getRoot());

                if (!isCursorSupported(c)) {
                    getWindow().getScene().setCursor(Cursor.DEFAULT);
                } else {
                    getWindow().getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                //Cursor c = StageKindlResizerImpl.cursorBy(ev, getResizableNode());
                saveCursor = StageKindlResizerImpl.cursorBy(ev, (Region) getWindow().getScene().getRoot());
                if (!applyTranskateXY) {
                    translateX = getNode().getTranslateX();
                    translateY = getNode().getTranslateY();
                }

                //node.setManaged(false);
//                System.err.println("2 FloatWindowView MouseHandler. mousepressed cursor = " + c);                
                cursorSupported = isCursorSupported(saveCursor);
//                System.err.println("2 FloatWindowView MouseHandler. isCursorSupported = " + cursorSupported);                
                if (!cursorSupported) {
                    getWindow().getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                getResizer().start(ev, getWindow(), getWindow().getScene().getCursor(), getSupportedCursors());
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
//                System.err.println("3 FloatWindowView MouseHandler. isCursorSupported = " + cursorSupported);                

                if (!cursorSupported) {
                    return;
                }
                if (!getResizer().isStarted()) {
//                System.err.println("4 FloatWindowView MouseHandler. windowResizer = " + popup.getResizer());                
//                System.err.println("4 FloatWindowView befor show MouseHandler. cursor = " + popup.getFloatingWindow().getScene().getCursor());                                

                    getResizer().start(ev, getWindow(), getWindow().getScene().getCursor(), getSupportedCursors());
                } else {
                    Platform.runLater(() -> {
                        getResizer().resize(ev);
                    });

                }
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (!isApplyTranskateXY()) {
                    double tX = getNode().getTranslateX();
                    double tY = getNode().getTranslateY();

                    getNode().setTranslateX(translateX);
                    getNode().setTranslateY(translateY);

                    double oldLX = getNode().getLayoutX();
                    double oldLY = getNode().getLayoutY();

                    getNode().setLayoutX(getNode().getLayoutX() + tX);
                    getNode().setLayoutY(getNode().getLayoutY() + tY);

                    getNode().getParent().layout();
                    System.err.println("oldLX = " + oldLX);
                    System.err.println("newLX = " + getNode().getLayoutX());
                    if (saveCursor == Cursor.W_RESIZE || saveCursor == Cursor.N_RESIZE) {
                        if (Math.abs(getNode().getLayoutX() - oldLX) <= 2 && Math.abs(getNode().getLayoutY() - oldLY) <= 2) {
                            //getNode().setPrefWidth(nodeResizer.getSavedPrefWidth());
                            //getNode().setPrefWidth(getNode().getWidth());
                            //getNode().setPrefHeight(nodeResizer.getSavedPrefHeight());
                            //getNode().getParent().layout();
                        }
                    }
                    getNode().setPrefWidth(getNode().getWidth());
                    windowBounds(getWindow(), getNode());
                    commitResize();
                } else {
                    windowBounds(getWindow(), getNode());
                    commitResize();
                }

            }
        }

        protected void commitResize() {
            if (nodeResizer.hideOnMouseRelease) {
                getWindow().hide();
            } else if (getWindow() instanceof PopupControl) {
                adjustWidth(getWindow(), getNode());
            }
        }

        public void setCursorSupported(boolean cursorSupported) {
            this.cursorSupported = cursorSupported;
        }

        public boolean isApplyTranskateXY() {
            return applyTranskateXY;
        }

        public void setApplyTranskateXY(boolean applyTranskateXY) {
            this.applyTranskateXY = applyTranskateXY;
        }

        public boolean isCursorSupported(Cursor cursor) {
//            System.err.println("isCursorSupported cursor = " + cursor);            
            if (cursor == null || cursor == Cursor.DEFAULT) {
                return false;
            }
            boolean retval = false;
//            System.err.println("isCursorSupported popup = " + popup);            

//            System.err.println("isCursorSupported popup.getSupportedCursors().len = " + popup.getSupportedCursors().length);
            for (Cursor c : getSupportedCursors()) {
                if (c == cursor) {
                    retval = true;
                    break;
                }
            }
//            System.err.println("isCursorSupported retval = " + retval);            

            return retval;
        }

    }//class MouseResizeHandler

    public static class StageKindlResizerImpl implements WindowResizer {

        private final DoubleProperty mouseX = new SimpleDoubleProperty();
        private final DoubleProperty mouseY = new SimpleDoubleProperty();

        private Cursor cursor;
        //private Window window;
        private NodeResizer2 nodeResizer;

        private Region node;

        public NodeResizer2 getNodeResizer() {
            return nodeResizer;
        }

        public Region getNode() {
            return node;
        }
        private Window window;
        private final Set<Cursor> cursorTypes = new HashSet<>();

        public StageKindlResizerImpl(NodeResizer2 nodeResizer) {
            this(nodeResizer.getWindow(), nodeResizer.getNode());
            this.nodeResizer = nodeResizer;
        }

        public StageKindlResizerImpl(Window window, Region node) {
            this.window = window;
            this.node = node;
            Collections.addAll(cursorTypes,
                    Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
                    Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE);
        }

        protected Window getWindow() {
            return window;
        }

        private void setCursorTypes(Cursor... cursors) {
            cursorTypes.clear();
            Collections.addAll(this.cursorTypes, cursors);
        }
//static int COUNT = 0;
        @Override
        public void resize(double x, double y) {
            if (!node.isManaged()) {
                resizeUnmanaged(x, y);
            } else {
                resizeManaged(x, y);
            }

        }

        public void resizeManaged(double x, double y) {
            double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;

            double curX = mouseX.get();
            double curY = mouseY.get();
            if (cursor == Cursor.S_RESIZE) {
                hDelta = y - this.mouseY.get();
                curY = y;
            } else if (cursor == Cursor.E_RESIZE) {
                wDelta = x - this.mouseX.get();
                curX = x;
            } else if (cursor == Cursor.N_RESIZE) {
                hDelta = this.mouseY.get() - y;
                yDelta = -hDelta;
                curY = y;
            } else if (cursor == Cursor.W_RESIZE) {
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                curX = x;
            } else if (cursor == Cursor.SE_RESIZE) {
                hDelta = y - this.mouseY.get();
                curY = y;
                wDelta = x - this.mouseX.get();
                curX = x;

            } else if (cursor == Cursor.NE_RESIZE) {
                hDelta = this.mouseY.get() - y;
                wDelta = x - this.mouseX.get();
                yDelta = -hDelta;
                curX = x;
                curY = y;
            } else if (cursor == Cursor.SW_RESIZE) {
                hDelta = y - this.mouseY.get();
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                curX = x;
                curY = y;
            } else if (cursor == Cursor.NW_RESIZE) {
                hDelta = this.mouseY.get() - y;
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                yDelta = -hDelta;
                curX = x;
                curY = y;
            }
            Region root = (Region) getWindow().getScene().getRoot();
            root.setMaxWidth(Double.MAX_VALUE);
            NodeLayout nodeLayout = getNodeResizer().nodeLayout;
            //System.err.println("1 w = (node.getWidth() = " + node.getWidth());
            //System.err.println("2 w = node.minWidth(-1) = " + node.minWidth(-1));
            //System.err.println("3 xDelta = " + xDelta);
            if (wDelta + getWindow().getWidth() > getMinWidth()) {
                if ((node.getWidth() > node.minWidth(-1) || xDelta <= 0)) {
                    double nodeNewX = node.getBoundsInParent().getMinX() - node.getLayoutX();
                    if (cursor == Cursor.W_RESIZE) {
                        if (wDelta < 0 && nodeLayout.getPrefWidth() > nodeLayout.getWidth()) {

                        } else if (wDelta < 0 && wDelta + node.getPrefWidth() > nodeLayout.actualMinWidth()
                                && nodeLayout.getPrefWidth() == node.getWidth()) {
                            System.err.println("1 wDelta =  = " + wDelta);
                            System.err.println("2 node.geWidth = " + node.getWidth());
                            System.err.println("3 layout.actualMaxWidth = " + nodeLayout.actualMaxWidth());
                            System.err.println("3.1 node.getMaxWidth = " + node.getMaxWidth());
                            System.err.println("3.2 node.maxWidth(-1) = " + node.maxWidth(-1));
                            System.err.println("3.3 node.maxWidth(h) = " + node.maxWidth(node.getMinHeight()));
                            System.err.println("3.4 node.maxWidth(h) = " + node.maxWidth(node.getHeight()));

                            System.err.println("----------------------------------------------");

                            node.setTranslateX(nodeNewX + xDelta);
                            node.setPrefWidth(wDelta + node.getPrefWidth());
                        } else if (wDelta < 0 && wDelta + node.getPrefWidth() > nodeLayout.actualMinWidth()
                                && nodeLayout.getPrefWidth() > node.getWidth()) {
                            System.err.println("--.1 wDelta =  = " + wDelta);
                            System.err.println("--- 2 node.geWidth = " + node.getWidth());
                            System.err.println("--- 3 layout.actualMaxWidth = " + nodeLayout.actualMaxWidth());
                            System.err.println("--- 3.1 node.getMaxWidth = " + node.getMaxWidth());
                            System.err.println("--- 3.2 node.maxWidth(-1) = " + node.maxWidth(-1));
                            System.err.println("--- 3.3 node.maxWidth(h) = " + node.maxWidth(node.getMinHeight()));
                            System.err.println("--- 3.4 node.maxWidth(h) = " + node.maxWidth(node.getHeight()));

                            System.err.println("----------------------------------------------");
                        } else if (wDelta > 0 && nodeLayout.getPrefWidth() <= nodeLayout.getWidth() && node.getWidth() < nodeLayout.actualMaxWidth()) {
                            System.err.println("3 wDelta =  = " + wDelta);
                            System.err.println("4 node.geWidth = " + node.getWidth());
                            System.err.println("5 layout.actualMaxWidth = " + nodeLayout.actualMaxWidth());

                            System.err.println("----------------------------------------------");
                            node.setTranslateX(nodeNewX + xDelta);
                            node.setPrefWidth(wDelta + node.getPrefWidth());
                        } else if (wDelta > 0 && nodeNewX + xDelta > 0) {
                            System.err.println("6 wDelta =  = " + wDelta);
                            System.err.println("7 node.geWidth = " + node.getWidth());
                            System.err.println("8 layout.actualMaxWidth = " + nodeLayout.actualMaxWidth());
                            System.err.println("----------------------------------------------");
                            node.setTranslateX(nodeNewX + xDelta);
                            node.setPrefWidth(wDelta + node.getPrefWidth());

                        }
                    } else {
                        node.setPrefWidth(wDelta + node.getPrefWidth());
                        nodeResizer.savePrefWidth(wDelta + node.getPrefWidth());

                        /*                        if (wDelta > 0 && (nodeLayout.getPrefWidth() == -1 || Math.round( Math.abs(nodeLayout.getPrefWidth() - node.getWidth())) <= 1) ) {
                            node.setPrefWidth(wDelta + node.getPrefWidth());
                            nodeResizer.savePrefWidth(wDelta + node.getPrefWidth());
                            System.err.println(" EQUALS");
                            System.err.println("wDelta =  = " + wDelta);
                            System.err.println("node.geWidth = " + node.getWidth());
                            System.err.println("layout.getPrefWidth = " + nodeLayout.getPrefWidth());
                            System.err.println("round node.geWidth = " + Math.round(node.getWidth()));
                            System.err.println("round layout.getPrefWidth = " + Math.round(nodeLayout.getPrefWidth()));
                            
                            System.err.println("----------------------------------------------");
                        } else if ( wDelta < 0 ) {
                            node.setPrefWidth(wDelta + node.getPrefWidth());
                            nodeResizer.savePrefWidth(wDelta + node.getPrefWidth());
                        } else {
                            System.err.println(" NOT EQUALS");
                            System.err.println("wDelta =  = " + wDelta);
                            System.err.println("node.geWidth = " + node.getWidth());
                            System.err.println("layout.getPrefWidth = " + nodeLayout.getPrefWidth());
                            System.err.println("round node.geWidth = " + Math.round(node.getWidth()));
                            System.err.println("round layout.getPrefWidth = " + Math.round(nodeLayout.getPrefWidth()));
                            System.err.println("----------------------------------------------");
                            
                            //node.setPrefWidth(node.getWidth());
                            //nodeResizer.savePrefWidth(node.getPrefWidth());
                        }
                        node.setPrefWidth(wDelta + node.getPrefWidth());
                        nodeResizer.savePrefWidth(wDelta + node.getPrefWidth());
                        node.getParent().layout();
                        node.setPrefWidth(node.getWidth());
                         */
                    }

                    mouseX.set(curX);
                }
            }

            if (hDelta + getWindow().getHeight() > getMinHeight()) {
                if ((node.getHeight() > node.minHeight(-1) || yDelta <= 0)) {
                    double nodeNewY = node.getBoundsInParent().getMinY() - node.getLayoutY();
                    if (cursor == Cursor.N_RESIZE) {
                        System.err.println("NNNNNNNNNNNNNNNNNNNNN");
                        node.setTranslateY(nodeNewY + yDelta);
                        node.setPrefHeight(hDelta + node.getPrefHeight());
                    } else {
                        node.setPrefHeight(hDelta + node.getPrefHeight());
                        nodeResizer.savePrefHeight(hDelta + node.getPrefHeight());
                    }

                    // double h = ((Region) node).getHeight();
                    mouseY.set(curY);
                }
            }
            //node.autosize();
            //node.getParent().layout();
            windowBounds(window, (Region) node);
        }

        public void resizeUnmanaged(double x, double y) {
            double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;

            double curX = mouseX.get();
            double curY = mouseY.get();
            if (cursor == Cursor.S_RESIZE) {
                hDelta = y - this.mouseY.get();
                curY = y;
            } else if (cursor == Cursor.E_RESIZE) {
                wDelta = x - this.mouseX.get();
                curX = x;
            } else if (cursor == Cursor.N_RESIZE) {
                hDelta = this.mouseY.get() - y;
                yDelta = -hDelta;
                curY = y;
            } else if (cursor == Cursor.W_RESIZE) {
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                curX = x;
            } else if (cursor == Cursor.SE_RESIZE) {
                hDelta = y - this.mouseY.get();
                curY = y;
                wDelta = x - this.mouseX.get();
                curX = x;

            } else if (cursor == Cursor.NE_RESIZE) {
                hDelta = this.mouseY.get() - y;
                wDelta = x - this.mouseX.get();
                yDelta = -hDelta;
                curX = x;
                curY = y;
            } else if (cursor == Cursor.SW_RESIZE) {
                hDelta = y - this.mouseY.get();
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                curX = x;
                curY = y;
            } else if (cursor == Cursor.NW_RESIZE) {
                hDelta = this.mouseY.get() - y;
                wDelta = this.mouseX.get() - x;
                xDelta = -wDelta;
                yDelta = -hDelta;
                curX = x;
                curY = y;
            }

            Region root = (Region) getWindow().getScene().getRoot();
            root.setMaxWidth(Double.MAX_VALUE);

            Bounds oldBounds = node.getBoundsInParent();

            double oldX = oldBounds.getMinX();
            double oldY = oldBounds.getMinY();

            double oldWidth = node.getWidth();
            double oldHeight = node.getHeight();

            double newX = oldX + xDelta;
            //double Y = node.getBoundsInParent().getMinY() - node.getLayoutY();                    
            double newY = oldY + yDelta;
            //((Region) node).setTranslateX(newX + xDelta);
            //((Region) node).setPrefWidth(wDelta + ((Region) node).getPrefWidth());
            //System.err.println("NODE isManaged=" + node.isManaged());
            double newWidth = wDelta + oldWidth;
            double newHeight = hDelta + oldHeight;

            node.resizeRelocate(newX, newY, newWidth, newHeight);
            windowBounds(window, (Region) node);
            mouseX.set(curX);
            mouseY.set(curY);
        }
        
        public boolean isResizableByWidth(double wDelta, double xDelta) {
            boolean retval = true;
            if (cursor != Cursor.W_RESIZE) {
                return true;
            }
            if (wDelta == 0) {
                return false;
            }
            if (wDelta > 0) {
                // LEFT Direction
                return isLeftResizable(wDelta, xDelta);
            }

            return retval;
        }

        private boolean isLeftResizable(double wDelta, double xDelta) {
            boolean retval = false;
            double nodeNewX = node.getBoundsInParent().getMinX() - node.getLayoutX();

            NodeLayout nl = getNodeResizer().nodeLayout;
            if (nl.getPrefWidth() <= nl.getWidth() && node.getWidth() < nl.actualMaxWidth()) {
                System.err.println("3 wDelta =  = " + wDelta);
                System.err.println("4 node.geWidth = " + node.getWidth());
                System.err.println("5 layout.actualMaxWidth = " + nl.actualMaxWidth());

                System.err.println("----------------------------------------------");
                retval = true;
//                node.setTranslateX(nodeNewX + xDelta);
//                node.setPrefWidth(wDelta + node.getPrefWidth());
            } else if (nodeNewX + xDelta > 0) {
                System.err.println("6 wDelta =  = " + wDelta);
                System.err.println("7 node.geWidth = " + node.getWidth());
                System.err.println("8 layout.actualMaxWidth = " + nl.actualMaxWidth());
                System.err.println("----------------------------------------------");
                node.setTranslateX(nodeNewX + xDelta);
                node.setPrefWidth(wDelta + node.getPrefWidth());

            }
            return retval;
        }

        protected double getMinWidth() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMinWidth();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMinWidth();
            }
            return retval;
        }

        protected double getMinHeight() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMinHeight();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMinHeight();
            }
            return retval;
        }

        protected double getMaxWidth() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMaxWidth();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMaxWidth();
            }
            return retval;
        }

        protected double getMaxHeight() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMaxHeight();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMaxHeight();
            }
            return retval;
        }

        @Override
        public void resize(MouseEvent ev) {
            resize(ev.getScreenX(), ev.getScreenY());
        }

        @Override
        public boolean isStarted() {
            return getWindow() != null;
        }

        //@Override
        //public void show(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {
        //}
        @Override
        public void start(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {

            setCursorTypes(supportedCursors);
            this.mouseX.set(ev.getScreenX());
            this.mouseY.set(ev.getScreenY());

            this.cursor = cursor;
            this.window = stage;
            Region r = (Region) window.getScene().getRoot();
        }

        public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
            boolean e, w, n, s;
            Cursor cursor = Cursor.DEFAULT;
            w = nodeX < left;
            e = nodeX > width - right;
            n = nodeY < top;
            s = nodeY > height - bottom;
            if (w) {
                if (n) {
                    cursor = Cursor.NW_RESIZE;
                } else if (s) {
                    cursor = Cursor.SW_RESIZE;
                } else {
                    cursor = Cursor.W_RESIZE;
                }
            } else if (e) {
                if (n) {
                    cursor = Cursor.NE_RESIZE;
                } else if (s) {
                    cursor = Cursor.SE_RESIZE;
                } else {
                    cursor = Cursor.E_RESIZE;
                }
            } else if (n) {
                cursor = Cursor.N_RESIZE;
            } else if (s) {
                cursor = Cursor.S_RESIZE;
            }
            Cursor retval = Cursor.DEFAULT;
            for (Cursor c : supported) {
                if (c.equals(cursor)) {
                    retval = cursor;
                    break;
                }
            }
            return cursor;
        }

        public static Cursor cursorBy(MouseEvent ev, double width, double height, double left, double right, double top, double bottom) {
            Window w = (Window) ev.getSource();
            double x = ev.getX();
            double y = ev.getY();
//            if (w instanceof Stage) {
            return cursorBy(x, ev.getY(), width, height, left, right, top, bottom);
            /*            } else {
                x -= left;
                y -= top;
            }
            return cursorBy(x, y, width, height, left, right, top, bottom);
             */
        }

        public static Cursor cursorBy(MouseEvent ev, Region r) {
            double x, y, w, h;

            Insets ins = r.getInsets();
            //test(ev, r);
            //System.err.println("INSETS " + ins);
            if (ins == Insets.EMPTY) {
                System.err.println("EMPTY INSETS " + ins);
                return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
            }
            //if (ev.getSource() instanceof Stage) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
            //} else {

            //}
            //return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
        }

        public Cursor getCursor() {
            return cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        public DoubleProperty mouseXProperty() {
            return mouseX;
        }

        public DoubleProperty mouseYProperty() {
            return mouseY;
        }

        public Double getMouseX() {
            return mouseX.get();
        }

        public Double getMouseY() {
            return mouseY.get();
        }

        public void setMouseX(Double mX) {
            this.mouseX.set(mX);
        }

        public void setMouseY(Double mY) {
            this.mouseY.set(mY);
        }

    }

    public static class NodeLayout {

        private final Region node;
        private final DoubleProperty width = new SimpleDoubleProperty(-1);
        private final DoubleProperty height = new SimpleDoubleProperty(-1);
        private final DoubleProperty minWidth = new SimpleDoubleProperty(-1);
        private final DoubleProperty minHeight = new SimpleDoubleProperty(-1);
        private final DoubleProperty maxWidth = new SimpleDoubleProperty(-1);
        private final DoubleProperty maxHeight = new SimpleDoubleProperty(-1);
        private final DoubleProperty prefWidth = new SimpleDoubleProperty(-1);
        private final DoubleProperty prefHeight = new SimpleDoubleProperty(-1);

        public NodeLayout(Region node) {
            this.node = node;
            init();
        }

        private void init() {
            width.bind(node.widthProperty());
            minWidth.bind(node.minWidthProperty());
            maxWidth.bind(node.maxWidthProperty());
            prefWidth.bind(node.prefWidthProperty());

            height.bind(node.heightProperty());
            minHeight.bind(node.minHeightProperty());
            maxHeight.bind(node.maxHeightProperty());
            prefHeight.bind(node.maxHeightProperty());

        }

        public DoubleProperty widthProperty() {
            return width;
        }

        public double getWidth() {
            return width.get();
        }

        public void setWidth(double width) {
            this.width.set(width);
        }

        public DoubleProperty heightProperty() {
            return height;
        }

        public double getHeight() {
            return height.get();
        }

        public void setHeight(double height) {
            this.height.set(height);
        }
/////

        public DoubleProperty minWidthProperty() {
            return minWidth;
        }

        public double getMinWidth() {
            return minWidth.get();
        }

        public void setMinWidth(double minWidth) {
            this.minWidth.set(minWidth);
        }

        public DoubleProperty minHeightProperty() {
            return minHeight;
        }

        public double getMinHeight() {
            return minHeight.get();
        }

        public void setMinHeight(double minHeight) {
            this.minHeight.set(minHeight);
        }

///
        public DoubleProperty maxWidthProperty() {
            return maxWidth;
        }

        public double getMaxWidth() {
            return maxWidth.get();
        }

        public void setMaxWidth(double maxWidth) {
            this.maxWidth.set(maxWidth);
        }

        public DoubleProperty maxHeightProperty() {
            return maxHeight;
        }

        public double getMaxHeight() {
            return maxHeight.get();
        }

        public void setMaxHeight(double maxHeight) {
            this.maxHeight.set(maxHeight);
        }

        public DoubleProperty prefWidthProperty() {
            return prefWidth;
        }

        public double getPrefWidth() {
            return prefWidth.get();
        }

        public void setPrefWidth(double prefWidth) {
            this.prefWidth.set(prefWidth);
        }

        public DoubleProperty prefHeightProperty() {
            return prefHeight;
        }

        public double getPrefHeight() {
            return prefHeight.get();
        }

        public void setPrefHeight(double prefHeight) {
            this.prefHeight.set(prefHeight);
        }

        public double prefWidth() {
            return prefDimension().getWidth();
        }

        public double minWidth() {
            return minDimension().getWidth();
        }

        public double maxWidth() {
            return maxDimension().getWidth();
        }

        public double prefHeight() {
            return prefDimension().getHeight();
        }

        public double maxHeight() {
            return maxDimension().getHeight();
        }

        public double minHeight() {
            return minDimension().getHeight();
        }

        public Dimension2D prefDimension() {
            Orientation contentBias = node.getContentBias();
            double prefW;
            double prefH;

            if (null == contentBias) {
                // contentBias is null
                prefW = node.prefWidth(-1);
                prefH = node.prefHeight(-1);
            } else {
                switch (contentBias) {
                    case HORIZONTAL:
                        prefW = node.prefWidth(-1);
                        prefH = node.prefHeight(prefW);
                        break;
                    case VERTICAL:
                        prefH = node.prefHeight(-1);
                        prefW = node.prefWidth(prefH);
                        break;
                    default:
                        // contentBias is null
                        prefW = node.prefWidth(-1);
                        prefH = node.prefHeight(-1);
                        break;
                }
            }
            return new Dimension2D(prefW, prefH);
        }

        public Dimension2D minDimension() {
            Orientation contentBias = node.getContentBias();
            double w;
            double h;

            if (null == contentBias) {
                // contentBias is null
                w = node.minWidth(-1);
                h = node.minHeight(-1);
            } else {
                switch (contentBias) {
                    case HORIZONTAL:
                        w = node.minWidth(-1);
                        h = node.minHeight(w);
                        break;
                    case VERTICAL:
                        h = node.minHeight(-1);
                        w = node.minWidth(h);
                        break;
                    default:
                        // contentBias is null
                        w = node.minWidth(-1);
                        h = node.minHeight(-1);
                        break;
                }
            }
            return new Dimension2D(w, h);

        }

        public Dimension2D maxDimension() {
            Orientation contentBias = node.getContentBias();
            double w;
            double h;

            if (null == contentBias) {
                // contentBias is null
                w = node.maxWidth(-1);
                h = node.maxHeight(-1);
            } else {
                switch (contentBias) {
                    case HORIZONTAL:
                        w = node.maxWidth(-1);
                        h = node.maxHeight(w);
                        break;
                    case VERTICAL:
                        h = node.maxHeight(-1);
                        w = node.maxWidth(h);
                        break;
                    default:
                        // contentBias is null
                        w = node.maxWidth(-1);
                        h = node.maxHeight(-1);
                        break;
                }
            }
            return new Dimension2D(w, h);

        }

        public double actualMaxWidth() {
            if (node.getMaxWidth() < 0) {
                return maxWidth();
            } else {
                return node.getMaxWidth();
            }
        }

        public double actualMinWidth() {
            if (node.getMinWidth() < 0) {
                return minWidth();
            } else {
                return node.getMinWidth();
            }
        }

    }//class NodeLayout

}
