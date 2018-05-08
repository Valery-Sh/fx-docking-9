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
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
//import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.StyleUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Valery
 */
public class FloatPopupControlView implements FloatWindowView {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private final ObjectProperty<Window> floatingWindow = new SimpleObjectProperty<>();

    private final ObjectProperty value = new SimpleObjectProperty();

    protected Pane windowRoot;

    private final DockableContext dockableContext;

    private Resizer resizer;

    private final MouseResizeHandler mouseResizeHanler;

    private final BooleanProperty floating = createFloatingProperty();

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
    public Resizer getResizer() {
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
            if (v != null && ((!dc.isValueDockable() || dc.isDragAsObject()))) {
                return make(dockable, v, show);
            } else if (dc.isValueDockable()) {
                return make(dockable, Dockable.of(v), show);
            }
        }

        setSupportedCursors(DEFAULT_CURSORS);

        Node node = dockable.node();
        //
        // Removes selected and then Removes all MMOUSE_CLICKED event handlers 
        // and filters of type SeectionListener
        //
        Selection.removeListeners(dockable);

        Window owner;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }

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

        if (isDocked(dockable) && getLayoutContext(dockable).getLayoutNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getLayoutContext(dockable).getLayoutNode());
            if (DockUtil.getOwnerWindow(dockable.node()) != targetNodeWindow) {
                windowRoot = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);
                Window retval = dockable.node().getScene().getWindow();
                //03.04getLayoutContext(dockable).undock(dockable.node());
                getLayoutContext(dockable).undock(dockable);
                return retval;
            }
        }

        boolean saveSize = false;

        if (isDocked(dockable)) {
            if ((dockable.node() instanceof DockNode) && (getLayoutContext(dockable).getLayoutNode() instanceof DockPane)) {
                saveSize = true;
            }
            LayoutContext oldLayoutContext = getLayoutContext(dockable);
            //03.04getLayoutContext(dockable).undock(dockable.node());
            getLayoutContext(dockable).undock(dockable);
            LayoutContext tc = dockable.getContext().getLayoutContext();
            // if (tc instanceof ScenePaneContext) {
            //     ((ScenePaneContext) tc).setRestoreContext(oldLayoutContext);
            // }

        }

//        if (dockable.getContext().isDocked()) {
//            getLayoutContext(dockable).undock(dockable.node());
//        }
        final PopupControl window = new PopupControl();

        //window.getScene().getStylesheets().add("org/vns/javafx/dock/api/resources/default.css");
        //window.getScene().getStylesheets().add(Dockable.class.getResource("resources/default.css").toExternalForm());
        //window.getScene().setUserAgentStylesheet(Dockable.class.getResource("resources/default.css").toExternalForm());
        //System.err.println("window.getScene() = " + window.getScene().getUserAgentStylesheet());
        window.getScene().getStylesheets().forEach(ss -> {
            //System.err.println("   --- sheet = " + ss);
        });
        window.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        window.setAutoFix(false);

        //Point2D stagePosition = windowPos;
        windowRoot = new StackPane() {
            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };
        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);
        /*windowRoot.setStyle("-fx-border-width: 1; -fx-border-color: red");*/

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (window != null) {
                    window.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        windowRoot.getStyleClass().add("float-window-root");
        //StyleUtil.styleFloatWindowRoot(windowRoot);
        windowRoot.getChildren().add(node);

        window.getScene().setRoot(windowRoot);
        window.getScene().setCursor(Cursor.HAND);
        markFloating(window);

        node.applyCss();
        windowRoot.applyCss();
        if (saveSize) {
            Bounds bounds = new BoundingBox(windowPos.getX(), windowPos.getY(), nodeWidth, nodeHeight);
            FloatView.layout(window, bounds);
        }

//        Bounds bounds = new BoundingBox(windowPos.getX(), windowPos.getY(), nodeWidth, nodeHeight);
//        FloatView.layout(window, bounds);
        //window.getStyleClass().clear();
        window.setOnShown(e -> {
//            window.getScene().getStylesheets().add("org/vns/javafx/dock/api/resources/default.css");
            DockRegistry.register(window);
        });

        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });

        if (show) {
            window.show(owner);
        }
        addResizer();

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
        Node node = context.dockable().node();
        Window owner = null;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }
        //double nodeWidth = ((Region) node).getWidth();
        //double nodeHeight = ((Region) node).getHeight();

        Point2D windowPos = node.localToScreen(0, 0);

        if (windowPos == null) {
            windowPos = new Point2D(400, 400);
        }
        /*03.04        Dockable d = Dockable.of(dragged);
        if (d != null && d.getContext().isDocked()) {
            getLayoutContext(d).undock(d.node());
        } else {
            DragContainer dc = context.getDragContainer();
            if (dc != null && dc.getDragSource() != null) {
                dc.getDragSource().removeValue(dockable);
            }
        }
         */
        getLayoutContext(dockable).undock(dockable);
        PopupControl window = new PopupControl();

        windowRoot = new StackPane() {
            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };

        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);

        node = context.getDragContainer().getPlaceholder();
        windowRoot.getChildren().add(node);

        window.getScene().setRoot(windowRoot);
        window.getScene().setCursor(Cursor.HAND);

        markFloating(window);

        windowRoot.setStyle("-fx-background-color: transparent");

        addResizer();

        window.getStyleClass().clear();
        window.setOnShown(e -> {
            DockRegistry.register(window);
        });
        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });
        if (show) {
            window.show(owner);
        }
        return window;

    }

    /**
     * Makes a window when the dragContainer is not null and is dragged
     *
     * @param dockable the object for which the window is to be created
     * @param dragged the dragged object
     * @param show true if the created window must be shown
     * @return the created window
     */
    protected Window make(Dockable dockable, Dockable dragged, boolean show) {
        setSupportedCursors(DEFAULT_CURSORS);
        Node node = dockable.node();
        Window owner = null;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }

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

        if (isDocked(dragged) && getLayoutContext(dragged).getLayoutNode() != null) {
            Window targetNodeWindow = DockUtil.getOwnerWindow(getLayoutContext(dragged).getLayoutNode());
            if (DockUtil.getOwnerWindow(dragged.node()) != targetNodeWindow) {
                windowRoot = (Pane) dragged.node().getScene().getRoot();
                markFloating(dragged.node().getScene().getWindow());
                setSupportedCursors(DEFAULT_CURSORS);

                //03.04getLayoutContext(dragged).undock(dragged.node());
                getLayoutContext(dockable).undock(dockable);
                return dragged.node().getScene().getWindow();
            }
        }
        if (isDocked(dragged)) {
            //LayoutContext oldLayoutContext = getLayoutContext(dragged);
            //03.04getLayoutContext(dragged).undock(dragged.node());
            getLayoutContext(dockable).undock(dockable);
            LayoutContext tc = dockable.getContext().getLayoutContext();
            //if (tc instanceof ScenePaneContext) {
            //    ((ScenePaneContext) tc).setRestoreContext(oldLayoutContext);
            //}
        }

        PopupControl window = new PopupControl();
        window.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        windowRoot = new StackPane() {
            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };
        windowRoot.getStyleClass().add(FLOAT_WINDOW);
        windowRoot.getStyleClass().add(FLOATVIEW);

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (window != null) {
                    window.hide();
                }
                dragged.node().parentProperty().removeListener(this);
            }
        };
        windowRoot.getStyleClass().add("float-window-root");
        //StyleUtil.styleFloatWindowRoot(windowRoot);
        node = dragged.node();
        windowRoot.getChildren().add(node);
        //rootPane.setCenter(node);

        window.getScene().setRoot(windowRoot);
        window.getScene().setCursor(Cursor.HAND);
        markFloating(window);

        node.applyCss();
        windowRoot.applyCss();

        window.getStyleClass().clear();
        window.setOnShown(e -> {
            DockRegistry.register(window);
        });
        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });
        if (show) {
            window.show(owner);
        }
        if (stageStyle == StageStyle.TRANSPARENT) {
            //scene.setFill(null);
        }

        addResizer();

        dragged.node().parentProperty().addListener(pcl);
        return window;
    }

    protected LayoutContext getLayoutContext(Dockable d) {
        return d.getContext().getLayoutContext();
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
        } else {
            removeListeners(dockableContext.dockable());
        }

        setResizer(new PopupControlResizer(this));

    }

    protected void setResizer(Resizer resizer) {
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
