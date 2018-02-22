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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.dragging.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery
 */
public class FloatStageView implements FloatWindowView {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private final ObjectProperty<Window> floatingWindow = new SimpleObjectProperty<>();

    private final ObjectProperty value = new SimpleObjectProperty();

    private Pane windowRoot;

    private final DockableContext dockableContext;

    private WindowResizer resizer;

    private final MouseResizeHandler mouseResizeHanler;

    private final BooleanProperty floating = createFloatingProperty();

    private Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    public FloatStageView(Dockable dockable) {
        this.dockableContext = dockable.getContext();
        mouseResizeHanler = new MouseResizeHandler(this);
    }

    @Override
    public void initialize() {
    }

    public MouseResizeHandler getMouseResizeHanler() {
        return mouseResizeHanler;
    }

    public StageStyle getStageStyle() {
        return stageStyle;
    }

    @Override
    public Pane getWindowRoot() {
        return windowRoot;
    }

    protected void setWindowRoot(Pane windowRoot) {
        this.windowRoot = windowRoot;
    }

    public DockableContext getDockableContext() {
        return dockableContext;
    }

    @Override
    public WindowResizer getResizer() {
        return resizer;
    }

    public void setStageStyle(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
    }

    @Override
    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }

    @Override
    public void setSupportedCursors(Cursor[] supportedCursors) {
        this.supportedCursors = supportedCursors;
    }

    @Override
    public ObjectProperty<Window> floatingWindowProperty() {
        return floatingWindow;
    }

    @Override
    public Window getFloatingWindow() {
        return floatingWindow.get();
    }

    protected void setFloatingWindow(Window window) {
        floatingWindow.set(window);
    }

    protected void markFloating(Window toMark) {
        toMark.getScene().getRoot().getStyleClass().add(FLOAT_WINDOW);
        floatingWindow.set(toMark);
    }

    @Override
    public Dockable getDockable() {
        return dockableContext.dockable();
    }

    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }

    @Override
    public Window make(Dockable dockable, boolean show) {
        DragContainer dc = dockable.getContext().getDragContainer();
        Object v;
        if (dc != null) {
            v = dc.getValue();
            if (v != null && !(dc.isValueDockable())) {
                return make(dockable, v, show);
            } else if (dc.isValueDockable()) {
                return make(dockable, Dockable.of(v), show);
            }
        }

        setSupportedCursors(DEFAULT_CURSORS);

        Node node = dockable.node();
        double nodeWidth = node.getBoundsInLocal().getWidth();
        double nodeHeight = node.getBoundsInLocal().getHeight();
        if (node instanceof Region) {
            nodeWidth = ((Region) node).getWidth();
            nodeHeight = ((Region) node).getHeight();
        }

        Point2D windowPos = node.localToScreen(0, 0);

        if (windowPos == null) {
            windowPos = new Point2D(400, 400);
        }

        Node titleBar = dockable.getContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.getContext().isDocked() && getTargetContext(dockable).getTargetNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getTargetContext(dockable).getTargetNode());
            if (DockUtil.getOwnerWindow(dockable.node()) != targetNodeWindow) {
                windowRoot = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);
                getTargetContext(dockable).undock(dockable.node());
                return dockable.node().getScene().getWindow();
            }
        }
        boolean saveSize = false;
        if (dockable.getContext().isDocked()) {
            if ((dockable.node() instanceof DockNode) && (getTargetContext(dockable).getTargetNode() instanceof DockPane)) {
                saveSize = true;
            }
            getTargetContext(dockable).undock(dockable.node());
        }

        Stage window = new Stage();
        DockRegistry.register(window);

        window.setTitle("FLOATING STAGE");
        Node lastDockPane = getTargetContext(dockable).getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            window.initOwner(lastDockPane.getScene().getWindow());
        }

        window.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        windowRoot = new StackPane();
        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);


        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (window != null) {
                    window.close();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        windowRoot.getStyleClass().add("dock-node-border");
        windowRoot.getChildren().add(node);

        Scene scene = new Scene(windowRoot);
        scene.setCursor(Cursor.HAND);
        window.setScene(scene);
        markFloating(window);

        node.applyCss();
        windowRoot.applyCss();
        if (saveSize) {
            Bounds bounds = new BoundingBox(windowPos.getX(), windowPos.getY(), nodeWidth, nodeHeight);
            FloatView.layout(window, bounds);
        }

        /*        Insets insetsDelta = windowRoot.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(stagePosition.getX() - insetsDelta.getLeft());
        window.setY(stagePosition.getY() - insetsDelta.getTop());

        window.setMinWidth(windowRoot.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        window.setMinHeight(windowRoot.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = windowRoot.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = windowRoot.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        windowRoot.setPrefWidth(prefWidth);
        windowRoot.setPrefHeight(prefHeight);
         */
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        //stage.sizeToScene();
        window.setAlwaysOnTop(true);

        // windowRoot.prefHeightProperty().bind(window.heightProperty());
        // windowRoot.prefWidthProperty().bind(window.widthProperty());
        if (show) {
            window.show();
        }
        dockable.node().parentProperty().addListener(pcl);
        return window;
    }

    /**
     * Makes a window when the dragContainer is not null and is not dockable
     *
     * @param dockable the object for which the window is to be created
     * @param dragged the dragged object
     * @param show true if the created window must be shown
     * @return the created window
     */
    protected Window make(Dockable dockable, Object dragged, boolean show) {
        setSupportedCursors(DEFAULT_CURSORS);

        DockableContext context = dockable.getContext();
        Point2D p = context.getLookup().lookup(MouseDragHandler.class).getStartMousePos();

/*        TargetContext tc = context.getTargetContext();
        if (tc instanceof ObjectReceiver) {
            ((ObjectReceiver) tc).undockObject(dockable);
            if (context.getDragContainer().getFloatingWindow(dockable) != null && context.getDragContainer().getFloatingWindow(dockable).isShowing()) {
                return context.getDragContainer().getFloatingWindow(dockable);
            }
        }
*/
        Stage window = new Stage();
        DockRegistry.register(window);

        window.setTitle("FLOATING STAGE");

        window.initStyle(stageStyle);

        windowRoot = new StackPane();
        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);

        //windowRoot = windowRoot;

        Node node = context.getDragContainer().getPlaceholder();
        windowRoot.getChildren().add(node);

        Scene scene = new Scene(windowRoot);

        scene.setCursor(Cursor.HAND);

        window.setScene(scene);
        markFloating(window);

        windowRoot.setStyle("-fx-background-color: transparent");

        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        //window.sizeToScene();
        window.setAlwaysOnTop(true);
        if (show) {
            window.show();
        }
        return window;

    }

    /**
     * Creates a window when the dragContainer is not null and is dragged
     *
     * @param dockable the object for which the window is to be created
     * @param dragged the dragged object
     * @param show true if the created window must be shown
     * @return the created window     */
    protected Window make(Dockable dockable, Dockable dragged, boolean show) {
        setSupportedCursors(DEFAULT_CURSORS);

        Node node = dragged.node();
        double nodeWidth = node.getBoundsInLocal().getWidth();
        double nodeHeight = node.getBoundsInLocal().getHeight();
        if (node instanceof Region) {
            nodeWidth = ((Region) node).getWidth();
            nodeHeight = ((Region) node).getHeight();
        }

        Point2D windowPos = node.localToScreen(0, 0);

        if (windowPos == null) {
            windowPos = new Point2D(400, 400);
        }
        Node titleBar = dragged.getContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        DockableContext draggedContext = dragged.getContext();

        if (draggedContext.isDocked() && getTargetContext(dragged).getTargetNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getTargetContext(dragged).getTargetNode());
            if (DockUtil.getOwnerWindow(dragged.node()) != targetNodeWindow) {
                windowRoot = (Pane) dragged.node().getScene().getRoot();
                markFloating(dragged.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);

                getTargetContext(dragged).undock(dragged.node());
                return dragged.node().getScene().getWindow();
            }
        }
        if (dragged.getContext().isDocked()) {
            getTargetContext(dragged).undock(dragged.node());
        }

        Stage window = new Stage();
        DockRegistry.register(window);

        window.setTitle("FLOATING STAGE");
        Node lastDockPane = getTargetContext(dragged).getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            window.initOwner(lastDockPane.getScene().getWindow());
        }

        window.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        windowRoot = new StackPane();
        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (window != null) {
                    window.close();
                }
                dragged.node().parentProperty().removeListener(this);
            }
        };

        windowRoot.getStyleClass().add("dock-node-border");

        //rootPane.setCenter(node);
        windowRoot.getChildren().add(node);

        Scene scene = new Scene(windowRoot);

        scene.setCursor(Cursor.HAND);
        window.setScene(scene);
        markFloating(window);

        node.applyCss();
        windowRoot.setStyle("-fx-background-color: aqua");
        windowRoot.applyCss();

        //Bounds bounds = new BoundingBox(windowPos.getX(), windowPos.getY(), nodeWidth, nodeHeight);
        //FloatView.layout(window, bounds);

        /*        Insets insetsDelta = windowRoot.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(stagePosition.getX() - insetsDelta.getLeft());
        window.setY(stagePosition.getY() - insetsDelta.getTop());

        window.setMinWidth(windowRoot.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        window.setMinHeight(windowRoot.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = windowRoot.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = windowRoot.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        windowRoot.setPrefWidth(prefWidth);
        windowRoot.setPrefHeight(prefHeight);
         */
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        //stage.sizeToScene();
        window.setAlwaysOnTop(true);
        if (show) {
            window.show();
        }
        dragged.node().parentProperty().addListener(pcl);
        return window;
    }

    protected TargetContext getTargetContext(Dockable d) {
        return d.getContext().getTargetContext();
    }

    @Override
    public Window make(Dockable dockable) {
        return make(dockable, true);
    }

    @Override
    public void addResizer() {
        if (dockableContext.isResizable()) {
            removeListeners(dockableContext.dockable());
            addListeners(getFloatingWindow());

        }
        setResizer(new StageResizer(this));

    }

    protected void setResizer(WindowResizer resizer) {
        this.resizer = resizer;
    }

    public ObjectProperty valueProperty() {
        return value;
    }

    @Override
    public Object getValue() {
        return value.get();
    }

    public void setValue(Object obj) {
        this.value.set(obj);
    }

    protected void addListeners(Window window) {
        window.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        window.addEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        window.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
    }

    public void removeListeners(Dockable dockable) {
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }

}//class FloatWindowBuilder
