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
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
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
public class FloatPopupControlView implements FloatWindowView {

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

    public FloatPopupControlView(Dockable dockable) {
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
        toMark.getScene().getRoot().getStyleClass().add(FLOAT_WINDOW);
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
        DragContainer dc = dockable.getContext().getDragContainer();
        Object v = null;
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
        Window owner = null;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.getContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.getContext().isDocked() && getTargetContext(dockable).getTargetNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getTargetContext(dockable).getTargetNode());
            if (DockUtil.getOwnerWindow(dockable.node()) != targetNodeWindow) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);
                getTargetContext(dockable).undock(dockable.node());
                return dockable.node().getScene().getWindow();
            }
        }
        if (dockable.getContext().isDocked()) {
            getTargetContext(dockable).undock(dockable.node());
        }

        final PopupControl popup = new PopupControl();
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOAT_WINDOW);
        borderPane.getStyleClass().add(FLOATVIEW);

        rootPane = borderPane;

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (popup != null) {
                    popup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        borderPane.getStyleClass().add("dock-node-border");
        borderPane.getStyleClass().add("float-popup-root");
        borderPane.setCenter(node);

        //Scene scene = new Scene(borderPane);
        //scene.setCursor(Cursor.HAND);
        popup.getScene().setRoot(borderPane);
        popup.getScene().setCursor(Cursor.HAND);
        markFloating(popup);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        popup.setX(stagePosition.getX() - insetsDelta.getLeft());
        popup.setY(stagePosition.getY() - insetsDelta.getTop());

        popup.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        popup.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);
        popup.getStyleClass().clear();
        popup.setOnShown(e -> {
            DockRegistry.register(popup);
        });
        popup.setOnHidden(e -> {
            DockRegistry.unregister(popup);
        });
        if (show) {
            popup.show(owner);
        }
        if (stageStyle == StageStyle.TRANSPARENT) {
            //scene.setFill(null);
        }

        addResizer();
        //popup.sizeToScene();

        dockable.node().parentProperty().addListener(pcl);
        return popup;
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
        //System.err.println("MAKE make(Dockable dockable, Object dragged)");
        setSupportedCursors(DEFAULT_CURSORS);

        DockableContext context = dockable.getContext();
        //Node node = context.getDragContainer().getGraphic();
        Node node = context.dockable().node();
        Window owner = null;
        
        System.err.println("0 PopupControl: make(Dockable dockable, Object dragged, boolean show)");
        
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }
        System.err.println("1 PopupControl: make(Dockable dockable, Object dragged, boolean show)");
        Point2D p = context.getLookup().lookup(MouseDragHandler.class).getStartMousePos();

        TargetContext tc = context.getTargetContext();
        if (tc instanceof ObjectReceiver) {
            ((ObjectReceiver) tc).undockObject(dockable);
            if (context.getDragContainer().getFloatingWindow(dockable) != null && context.getDragContainer().getFloatingWindow(dockable).isShowing()) {
                return context.getDragContainer().getFloatingWindow(dockable);
            }
        }

        PopupControl popup = new PopupControl();

        //stage.setTitle("FLOATING STAGE");
        //stage.initStyle(stageStyle);
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOAT_WINDOW);
        borderPane.getStyleClass().add(FLOATVIEW);

        rootPane = borderPane;
        node = context.getDragContainer().getPlaceholder();
        borderPane.setCenter(node);

        popup.getScene().setRoot(borderPane);
        popup.getScene().setCursor(Cursor.HAND);

        markFloating(popup);

        borderPane.setStyle("-fx-background-color: transparent");

        addResizer();
//        popup.sizeToScene();
//        popup.setAlwaysOnTop(true);
        popup.getStyleClass().clear();
        popup.setOnShown(e -> {
            DockRegistry.register(popup);
        });
        popup.setOnHidden(e -> {
            DockRegistry.unregister(popup);
        });
        if (show) {
            popup.show(owner);
        }
        return popup;

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
        //System.err.println("MAKE make(Dockable dockable, Dockable dragged)");
        setSupportedCursors(DEFAULT_CURSORS);
        Node node = dockable.node();
        Window owner = null;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
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
                rootPane = (Pane) dragged.node().getScene().getRoot();
                markFloating(dragged.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);

                getTargetContext(dragged).undock(dragged.node());
                return dragged.node().getScene().getWindow();
            }
        }
        if (dragged.getContext().isDocked()) {
            getTargetContext(dragged).undock(dragged.node());
        }

        PopupControl popup = new PopupControl();
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOAT_WINDOW);
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
                if (popup != null) {
                    popup.hide();
                }
                dragged.node().parentProperty().removeListener(this);
            }
        };
        borderPane.getStyleClass().add("dock-node-border");
        borderPane.getStyleClass().add("float-popup-root");
        node = dragged.node();
        borderPane.setCenter(node);

        //Scene scene = new Scene(borderPane);
        //scene.setCursor(Cursor.HAND);
        popup.getScene().setRoot(borderPane);
        popup.getScene().setCursor(Cursor.HAND);
        markFloating(popup);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        popup.setX(stagePosition.getX() - insetsDelta.getLeft());
        popup.setY(stagePosition.getY() - insetsDelta.getTop());

        popup.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        popup.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);
        popup.getStyleClass().clear();
        popup.setOnShown(e -> {
            DockRegistry.register(popup);
        });
        popup.setOnHidden(e -> {
            DockRegistry.unregister(popup);
        });
        if (show) {
            popup.show(owner);
        }
        if (stageStyle == StageStyle.TRANSPARENT) {
            //scene.setFill(null);
        }

        addResizer();

        dragged.node().parentProperty().addListener(pcl);
        return popup;
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
        //if (getDockable().getContext().isResizable()) {
        removeListeners(getDockable().getContext().dockable());
        addListeners(getFloatingWindow());

        //}
        setResizer(new PopupControlResizer(this));

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
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }

}//class FloatWindowBuilder
