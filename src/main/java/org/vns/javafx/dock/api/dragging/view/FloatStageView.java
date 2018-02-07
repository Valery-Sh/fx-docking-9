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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.ObjectReceiver;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery
 */
public class FloatStageView implements FloatWindowView {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private ObjectProperty<Window> floatingWindow = new SimpleObjectProperty<>();

    private ObjectProperty value = new SimpleObjectProperty();

    private Pane rootPane;

    private final DockableContext dockableContext;

    private WindowResizer resizer;

    private final MouseResizeHandler mouseResizeHanler;

    private final BooleanProperty floating = createFloatingProperty();

    private double minWidth = -1;
    private double minHeight = -1;

    private Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    public FloatStageView(Dockable dockable) {
        this.dockableContext = dockable.getDockableContext();
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
    public Pane getRootPane() {
        return rootPane;
    }

    protected void setRootPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public DockableContext getDockableContext() {
        return dockableContext;
    }

    @Override
    public WindowResizer getResizer() {
        return resizer;
    }

    /*    public void setResizer(FloatWindowResizer resizer) {
        this.resizer = resizer;
    }
     */
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

/*    public ObjectProperty<Window> stageProperty() {
        return this.floatingWindow;
    }
*/
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
        toMark.getScene().getRoot().getStyleClass().add(FLOATWINDOW);
        floatingWindow.set(toMark);
    }

/*    protected Node node() {
        return dockableContext.dockable().node();
    }
*/
    @Override
    public Dockable getDockable() {
        return dockableContext.dockable();
    }

    //==========================
    //
    //==========================
/*    public void makeFloating() {
        if (node() == null) {
            return;
        }
        make(getDockable());
    }
*/
    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }

    @Override
    public Window make(Dockable dockable, boolean show) {
        System.err.println("FLOATSTAGEVIEW");
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        Object v = dc.getValue();

        if (v != null && !(dc.isValueDockable())) {
            return make(dockable, v, show);
        } else if (dc.isValueDockable()) {
            return make(dockable, Dockable.of(v), show);
        }

        setSupportedCursors(DEFAULT_CURSORS);

        Node node = dockable.node();
        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.getDockableContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.getDockableContext().isDocked() && getTargetContext(dockable).getTargetNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getTargetContext(dockable).getTargetNode());
            if (DockUtil.getOwnerWindow(dockable.node()) != targetNodeWindow) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);
                getTargetContext(dockable).undock(dockable.node());
                return dockable.node().getScene().getWindow();
            }
        }

        if (dockable.getDockableContext().isDocked()) {
            getTargetContext(dockable).undock(dockable.node());
        }

        Stage stage = new Stage();
        DockRegistry.register(stage);

        stage.setTitle("FLOATING STAGE");
        Node lastDockPane = getTargetContext(dockable).getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            stage.initOwner(lastDockPane.getScene().getWindow());
        }

        stage.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOATWINDOW);
        borderPane.getStyleClass().add(FLOATVIEW);
        
        rootPane = borderPane;

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (stage != null) {
                    stage.close();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        borderPane.getStyleClass().add("dock-node-border");
        //borderPane.setStyle("-fx-background-color: red");
        borderPane.setCenter(node);

        Scene scene = new Scene(borderPane);
        scene.setCursor(Cursor.HAND);
        //floatingProperty.set(true);

        stage.setScene(scene);
        markFloating(stage);

        node.applyCss();
        borderPane.applyCss();
        Insets insetsDelta = borderPane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        stage.setX(stagePosition.getX() - insetsDelta.getLeft());
        stage.setY(stagePosition.getY() - insetsDelta.getTop());

        stage.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        stage.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        stage.sizeToScene();
        stage.setAlwaysOnTop(true);
        if (show) {
            stage.show();
        }
        dockable.node().parentProperty().addListener(pcl);
        return stage;
    }

    /**
     * Makes a window when the dragContainer is not null and is not dockable
     *
     * @param dockable ??
     * @param transp ??
     * @param show ??
     * @return ??
     */
    protected Window make(Dockable dockable, Object dragged, boolean show) {
        System.err.println("MAKE make(Dockable dockable, Object dragged)");
        setSupportedCursors(DEFAULT_CURSORS);

        DockableContext context = dockable.getDockableContext();
        Point2D p = context.getLookup().lookup(MouseDragHandler.class).getStartMousePos();

        Point2D screenPoint = dockable.node().localToScreen(p);

        TargetContext tc = context.getTargetContext();
        if (tc instanceof ObjectReceiver) {
            ((ObjectReceiver)tc).undockObject(dockable);
            if (context.getDragContainer().getFloatingWindow() != null && context.getDragContainer().getFloatingWindow().isShowing()) {
                return context.getDragContainer().getFloatingWindow();
            }
        }

        Stage stage = new Stage();
        DockRegistry.register(stage);

        stage.setTitle("FLOATING STAGE");

        stage.initStyle(stageStyle);

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOATWINDOW);
        borderPane.getStyleClass().add(FLOATVIEW);
        
        rootPane = borderPane;
        
        Node node = context.getDragContainer().getGraphic();
        borderPane.setCenter(node);

        Scene scene = new Scene(borderPane);
        
        scene.setCursor(Cursor.HAND);

        stage.setScene(scene);
        markFloating(stage);

        borderPane.setStyle("-fx-background-color: transparent");
        
/*        Insets insetsDelta = borderPane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        stage.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        stage.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);
*/
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        stage.sizeToScene();
        stage.setAlwaysOnTop(true);
        if (show) {
            stage.show();
        }
        return stage;

    }

    /**
     * Makes a window when the dragContainer is not null and is dragged
     *
     * @param dragged ??
     * @param transp ??
     * @param show ??
     * @return ??
     */
    protected Window make(Dockable dockable, Dockable dragged, boolean show) {
        System.err.println("MAKE make(Dockable dockable, Dockable dragged)");
        setSupportedCursors(DEFAULT_CURSORS);

        Node node = dragged.node();
        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dragged.getDockableContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        DockableContext draggedContext = dragged.getDockableContext();

        if (draggedContext.isDocked() && getTargetContext(dragged).getTargetNode() != null) {
            System.err.println("make 1");
            //Window w = dockable.getDockableContext().getTargetContext().getTargetNode().getScene().getWindow();
            Window targetNodeWindow = DockUtil.getOwnerWindow(getTargetContext(dragged).getTargetNode());
            //Window targetNodeWindow = DockUtil.getOwnerWindow(dragged.node());//getTargetContext(dragged).getTargetNode().getScene().getWindow();
            //if (dragged.node().getScene() != null && dragged.node().getScene().getWindow() != targetNodeWindow) {
            if (DockUtil.getOwnerWindow(dragged.node()) != targetNodeWindow) {
                rootPane = (Pane) dragged.node().getScene().getRoot();
                markFloating(dragged.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);

                getTargetContext(dragged).undock(dragged.node());
                System.err.println("make 2");
                return dragged.node().getScene().getWindow();
            }
        }
        if (dragged.getDockableContext().isDocked()) {
            getTargetContext(dragged).undock(dragged.node());
        }

        Stage stage = new Stage();
        DockRegistry.register(stage);

        stage.setTitle("FLOATING STAGE");
        Node lastDockPane = getTargetContext(dragged).getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            stage.initOwner(lastDockPane.getScene().getWindow());
        }

        stage.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOATWINDOW);
        borderPane.getStyleClass().add(FLOATVIEW);

        rootPane = borderPane;

        //borderPane.getProperties().put(FLOATVIEW_UUID, dockable);
        
        //Rectangle r = new Rectangle(75, 30);
        //r.setFill(Color.YELLOW);
        //borderPane.setCenter(r);

        //DockPane dockPane = new DockPane();
        //StackPane dockPane = new StackPane();
        //borderPane.setStyle("-fx-background-color: aqua");
        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (stage != null) {
                    stage.close();
                }
                dragged.node().parentProperty().removeListener(this);
            }
        };

        //
        // Prohibit to use as a dock target
        //
        //dockPane.setUsedAsDockTarget(false);
        //dockPane.getItems().add(dragged.node());
        //dockPane.getChildren().add(node);
        //borderPane.getStyleClass().clear();
        borderPane.getStyleClass().add("dock-node-border");

        borderPane.setCenter(node);

        Scene scene = new Scene(borderPane);
        
        scene.setCursor(Cursor.HAND);
        //floatingProperty.set(true);

        stage.setScene(scene);
        markFloating(stage);

        node.applyCss();
        borderPane.applyCss();
        //dockPane.applyCss();
        Insets insetsDelta = borderPane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        stage.setX(stagePosition.getX() - insetsDelta.getLeft());
        stage.setY(stagePosition.getY() - insetsDelta.getTop());

        stage.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        stage.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer();
        stage.sizeToScene();
        stage.setAlwaysOnTop(true);
        if (show) {
            stage.show();
        }
        dragged.node().parentProperty().addListener(pcl);
        return stage;
    }

    protected TargetContext getTargetContext(Dockable d) {
        return d.getDockableContext().getTargetContext();
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
        if (dockable.node().getScene() == null || dockable.node().getScene().getWindow() == null) {
            //return;
        }
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }

}//class FloatWindowBuilder
