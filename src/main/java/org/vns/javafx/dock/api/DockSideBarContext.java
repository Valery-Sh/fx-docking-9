/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api;

import org.vns.javafx.ContextLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.dragging.view.FloatPopupControlView2;
import org.vns.javafx.dock.api.dragging.view.FloatStageView2;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockSideBarContext extends LayoutContext {

    private final ObservableMap<Group, Container> itemMap = FXCollections.observableHashMap();
    private final ToolBar toolBar;

    public DockSideBarContext(Region dockPane, ToolBar toolBar) {
        super(dockPane);
        this.toolBar = toolBar;
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new SideBarPositonIndicator(this));
    }

    @Override
    protected void inititialize() {
        DockRegistry.start();
    }

    protected Container getItem(Dockable d) {
        Container retval = null;
        for (Container c : itemMap.values()) {
            if (c.getDockable() == d) {
                retval = c;
                break;
            }
        }
        return retval;
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    /*   @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        for (Container c : itemMap.values()) {
            if (c.getDockable().node() == node) {
                retval = true;
                break;
            }
        }
        return retval;
    }
     */
    @Override
    public boolean contains(Object obj) {
        if ((obj == null) || !(obj instanceof Node)) {
            return false;
        }
        boolean retval = false;
        for (Container c : itemMap.values()) {
            if (c.getDockable().node() == obj) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public ObservableList<Dockable> getDockables() {
        return ((DockSideBar) getLayoutNode()).getItems();
    }

    public void dock(Dockable dockable) {
        Object o = getValue(dockable);
        if (o == null || Dockable.of(o) == null) {
            return;
        }
        Dockable dragged = Dockable.of(o);
        doDock(null, dragged.node());
    }

    protected String getButtonText(Dockable d) {
        String txt = d.getContext().getTitle();
        if (txt == null && d.getContext().getProperties().getProperty("user-title") != null) {
            txt = d.getContext().getProperties().getProperty("user-title");
        } else if (txt == null && d.getContext().getProperties().getProperty("short-title") != null) {
            txt = d.getContext().getProperties().getProperty("short-title");
        }
        if (txt == null && d.node() instanceof Labeled) {
            txt = ((Labeled) d.node()).getText();
        }
        if (txt == null && d.node().getId() != null) {
            txt = d.node().getId();
        }
        if (txt == null || txt.trim().isEmpty()) {
            txt = d.node().getClass().getSimpleName();
        }
        return txt;
    }

    public boolean hasWindow(Dockable d) {
        boolean retval = false;
        if (d.node().getScene() != null && d.node().getScene().getWindow() != null) {
            retval = true;
        }
        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Object o = getValue(dockable);
        if (o == null || Dockable.of(o) == null) {
            return;
        }

        Dockable d = Dockable.of(o);
        //
        // Test is we drag dockable or the value of a dragContainer 
        //
/*        if (contains(d.node()) && d == dockable) {
            return;
        } else if (contains(d.node())) {
            LayoutContext tc = d.getContext().getLayoutContext();
            if (tc != null && isDocked(tc, d)) {
                tc.undock(d.node());
            }
        }
         */
        undock(dockable);

        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, d.node()) && stage != null) {
            //d.getContext().setFloating(false);
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getContext().setLayoutContext(this);
        }
    }

    protected boolean doDock(Point2D mousePos, Node node) {
        Dockable dockable = Dockable.of(node);

        Window priorWindow = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) {
            priorWindow = (Window) node.getScene().getWindow();
        }

        Button itemButton = new Button(getButtonText(dockable));

        itemButton.getStyleClass().add("item-button");

        int idx = -1;

        if (mousePos != null) {
            Node sb = findNode(toolBar.getItems(), mousePos.getX(), mousePos.getY());
            if (sb != null && (sb instanceof Group)) {
                idx = toolBar.getItems().indexOf(sb);
            } else if (sb == null && DockUtil.contains(toolBar, mousePos.getX(), mousePos.getY())) {
                idx = toolBar.getItems().size();
            } else {
                return false;
            }
        }
        Group item = new Group(itemButton);
        //itemButton.setContentDisplay(ContentDisplay.CENTER);
        Container container = new Container(dockable, (DockSideBar) getLayoutNode());
        getItemMap().put(item, container);
        itemButton.setRotate(((DockSideBar) getLayoutNode()).getRotation().getAngle());
        itemButton.setOnAction(a -> {
            a.consume();
            Window window = container.getPopup();
            if (window == null) {
                window = container.getFloatView().make(dockable, false);
                //window = (PopupControl) container.getFloatView().make(dockable, false);
                //window = (Stage) container.getFloatView().make(dockable, false);
                container.addMouseExitListener();
                container.setPopup(window);
                show(itemButton);
            } else if (!window.isShowing()) {
                show(itemButton);
            } else {
                window.hide();
            }
            for (Container c : getItemMap().values()) {
                if (c.getPopup() != null && c.getPopup() != window) {
                    c.getPopup().hide();
                }
            }
        });

        if (idx >= 0) {
            toolBar.getItems().add(idx, item);
        } else {
            toolBar.getItems().add(item);
        }
        DockableContext context = dockable.getContext();
        if (context.getLayoutContext() == null || context.getLayoutContext() != this) {
            context.setLayoutContext(this);
        }
        container.getFloatView().setSupportedCursors(getSupportedCursors());
        if (getLayoutNode().getScene() != null && getLayoutNode().getScene().getWindow() != null && getLayoutNode().getScene().getWindow().isShowing()) {
            container.adjustScreenPos();
        }
        if (priorWindow != null && (priorWindow instanceof Stage)) {
            ((Stage) priorWindow).close();
        } else if (priorWindow != null) {
            priorWindow.hide();
        }
        return true;
    }

    public Node findNode(List<Node> list, double x, double y) {
        Node retval = null;
        for (Node node : list) {
            //
            // Consider the the first element is a drag node
            //
            if (!(node instanceof Group) || list.indexOf(node) == 0) {
                continue;
            }

            Region r = (Region) ((Group) node).getChildren().get(0);

            if (DockUtil.contains(r, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    public Cursor[] getSupportedCursors() {
        List<Cursor> list = new ArrayList<>();
        switch (((DockSideBar) getLayoutNode()).getSide()) {
            case RIGHT:
                list.add(Cursor.W_RESIZE);
                break;
            case LEFT:
                list.add(Cursor.E_RESIZE);
                break;
            case BOTTOM:
                list.add(Cursor.N_RESIZE);
                break;
            case TOP:
                list.add(Cursor.S_RESIZE);
                break;
        }

        return list.toArray(new Cursor[0]);
    }

    protected void rotate(Button btn) {
        switch (((DockSideBar) getLayoutNode()).getSide()) {
            case RIGHT:
                btn.setRotate(90);
                break;
            case LEFT:
                btn.setRotate(-90);
                break;
            default:
                btn.setRotate(0);
                break;
        }
    }

    @Override
    public void remove(Object obj) {
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        Group r = null;
        for (Map.Entry<Group, Container> en : itemMap.entrySet()) {
            if (en.getValue().getDockable().node() == dockNode) {
                r = en.getKey();
                break;
            }
        }
        if (r != null) {
            itemMap.get(r).removeListeners();
            itemMap.get(r).getFloatView().setSupportedCursors(FloatView.DEFAULT_CURSORS);
            itemMap.remove(r);
            toolBar.getItems().remove(r);
            ((DockSideBar) getLayoutNode()).getItems().remove(Dockable.of(dockNode));
        }
    }

    public ObservableMap<Group, Container> getItemMap() {
        return itemMap;
    }

    public void show(Button btn) {
        Group group = (Group) btn.getParent();
        Container container = getItemMap().get(group);
        DockSideBar sb = (DockSideBar) getLayoutNode();

        if (container.getPopup() != null && !container.getPopup().isShowing()) {
            if (container.getPopup() instanceof PopupControl) {
                ((PopupControl) container.getPopup()).show(toolBar.getScene().getWindow());
            } else {
                if (!container.getPopup().isShowing() && ((Stage) container.getPopup()).getOwner() == null) {
                    ((Stage) container.getPopup()).initOwner(toolBar.getScene().getWindow());
                }

                ((Stage) container.getPopup()).show();

            }
        }
        container.changeSize();
    }

    public static class Container implements ChangeListener<Number> {

        private Window popup;

        private final Dockable dockable;
        private final FloatView windowBuilder;

        private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);
        private final BooleanProperty floatingProperty = new SimpleBooleanProperty(false);

        private EventHandler<MouseEvent> mouseExitEventListener;

        public Container(Dockable dockable, DockSideBar sideBar) {
            this.dockable = dockable;
            LayoutContext lc = dockable.getContext().getLayoutContext();
//            System.err.println("Container: layouContext= " + lc);
            if (sideBar.getLookup().lookup(FloatViewFactory.class) == null) {
                windowBuilder = new FloatStageView2(dockable);
            } else {
                windowBuilder = sideBar.getLookup().lookup(FloatViewFactory.class).getFloatView(dockable);
            }
        }

        public Window getPopup() {
            return popup;
        }

        public void setPopup(Window popup) {
            this.popup = popup;
        }

        public DockSideBar getSideBar() {
            return (DockSideBar) dockable.getContext().getLayoutContext().getLayoutNode();
        }

        public void addMouseExitListener() {
            mouseExitEventListener = this::mouseExited;
            dockable.node().getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public void removeMouseExitListener() {
            if (mouseExitEventListener == null) {
                return;
            }
            dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
            dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public Dockable getDockable() {
            return dockable;
        }

        protected void mouseExited(MouseEvent ev) {
            if ((ev.getSource() instanceof Window) && DockUtil.contains((Window) ev.getSource(), ev.getScreenX(), ev.getScreenY())) {
                ev.consume();
                return;
            }
            LayoutContext layoutContext = dockable.getContext().getLayoutContext();
            if (!ev.isPrimaryButtonDown() && !dockable.getContext().isFloating()) {
                if (((DockSideBar) layoutContext.getLayoutNode()).isHideOnExit()) {
                    dockable.node().getScene().getWindow().hide();
                }
            }
        }

        public void adjustScreenPos() {
            DockSideBarContext context = (DockSideBarContext) dockable.getContext().getLayoutContext();
            Window ownerStage = (Window) ((DockSideBar) context.getLayoutNode()).getScene().getWindow();

            ownerStage.xProperty().addListener(this);
            ownerStage.yProperty().addListener(this);
            ownerStage.widthProperty().addListener(this);
            ownerStage.heightProperty().addListener(this);

            changeSize();

        }

        public void removeListeners() {
            DockSideBarContext handler = (DockSideBarContext) dockable.getContext().getLayoutContext();
            Window ownerStage = (Window) ((DockSideBar) handler.getLayoutNode()).getScene().getWindow();
            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);
        }

        public void changeSide() {
            DockSideBarContext handler = (DockSideBarContext) dockable.getContext().getLayoutContext();
            windowBuilder.setSupportedCursors(handler.getSupportedCursors());

        }

        public void changeSize() {
            if (getPopup() == null) {
                return;
            }
            DockSideBar sb = (DockSideBar) dockable.getContext().getLayoutContext().getLayoutNode();
            if (!popup.isShowing()) {
                return;
            }
            Pane root = (Pane) popup.getScene().getRoot();
            Point2D pos = sb.localToScreen(0, 0);
            Insets ins = new Insets(0, 0, 0, 0);
            if (popup.getScene().getRoot() instanceof Region) {
                ins = ((Region) popup.getScene().getRoot()).getInsets();
            }
            if (popup instanceof PopupControl) {
                PopupControl window = (PopupControl) this.popup;
                switch (sb.getSide()) {
                    case TOP:
                        window.setAnchorX(pos.getX());
                        window.setAnchorY(pos.getY() + sb.getHeight());
                        root.setPrefWidth(sb.getWidth());
                        break;
                    case BOTTOM:
                        window.setAnchorX(pos.getX());
                        window.setAnchorY(pos.getY() - window.getHeight());
                        root.setPrefWidth(sb.getWidth());
                        break;
                    case RIGHT:
                        window.setAnchorY(pos.getY());
                        window.setAnchorX(pos.getX() - window.getWidth());
                        root.setPrefHeight(sb.getHeight());
                        break;
                    case LEFT:
                        window.setAnchorY(pos.getY() - ins.getTop());
                        window.setAnchorX(pos.getX() + sb.getWidth());
                        root.setPrefHeight(sb.getHeight());
                        break;
                }
            } else {
                Stage window = (Stage) this.popup;
                switch (sb.getSide()) {
                    case TOP:
                        window.setX(pos.getX());
                        window.setY(pos.getY() + sb.getHeight());
                        //root.setPrefWidth(sb.getWidth());
                        window.setWidth(sb.getWidth());
                        break;
                    case BOTTOM:
                        window.setX(pos.getX());
                        window.setY(pos.getY() - window.getHeight());
                        //root.setPrefWidth(sb.getWidth());
                        window.setWidth(sb.getWidth());
                        break;
                    case RIGHT:
                        window.setY(pos.getY());
                        window.setX(pos.getX() - window.getWidth());
                        //root.setPrefHeight(sb.getHeight());
                        window.setHeight(sb.getHeight());
                        break;
                    case LEFT:
                        window.setY(pos.getY() - ins.getTop());
                        window.setX(pos.getX() + sb.getWidth());
                        //root.setPrefHeight(sb.getHeight());
                        window.setHeight(sb.getHeight());
                        break;
                }

            }
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            changeSize();
        }

        public FloatView getFloatView() {
            return windowBuilder;
        }

    }

    public static class SideBarPositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private IndicatorPopup indicatorPopup;

        public SideBarPositonIndicator(LayoutContext context) {
            super(context);
        }

        @Override
        public IndicatorPopup getIndicatorPopup() {
            if (indicatorPopup == null) {
                indicatorPopup = getLayoutContext().getLookup().lookup(IndicatorPopup.class);
            }
            return indicatorPopup;
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane p = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            p.getStyleClass().add("drag-pane-indicator");
            return p;
        }

        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                tabDockPlace.getStyleClass().add("tab-place");
                //StyleUtil.styleTabPlace(tabDockPlace);
                getIndicatorPane().getChildren().add(tabDockPlace);
            }
            return tabDockPlace;
        }

        @Override
        public void hideDockPlace() {
            getDockPlace().setVisible(false);
            getTabDockPlace().setVisible(false);

        }

        private ToolBar getToolBar() {
            return ((DockSideBarContext) getLayoutContext()).getToolBar();
        }

        private static Node findLastVisibleNode(List<Node> list) {
            Node retval = null;
            for (Node node : list) {
                //
                // Consider the first element may be a drag node
                //
                if (!(node instanceof Group) || list.indexOf(node) == 0) {
                    continue;
                }

                Region r = (Region) ((Group) node).getChildren().get(0);
                if (r.localToScreen(r.getBoundsInLocal()) == null) {
                    continue;
                }
                retval = node;
            }
            return retval;
        }

        protected int indexOf(double x, double y) {
            int idx = -1;
            Node sb = ((DockSideBarContext) getLayoutContext()).findNode(getToolBar().getItems(), x, y);
            if (sb != null && (sb instanceof Group)) {
                idx = getToolBar().getItems().indexOf(sb);
            } else if (sb == null && DockUtil.contains(getToolBar(), x, y)) {
                idx = getToolBar().getItems().size();
            }
            return idx;
        }

        @Override
        public void showDockPlace(double x, double y) {
            ToolBar tb = getToolBar();

            int idx = indexOf(x, y);
            if (idx < 0) {
                return;
            }
            // double tbHeight = tb.getHeight();

            Rectangle dockPlace = (Rectangle) getDockPlace();

            if (tb.getOrientation() == Orientation.HORIZONTAL) {
                dockPlace.setHeight(tb.getHeight());
                dockPlace.setWidth(10);
            } else {
                dockPlace.setWidth(tb.getWidth());
                dockPlace.setHeight(10);
            }
            Point2D p = dockPlace.localToParent(0, 0);

            dockPlace.setX(p.getX());

            Node node = null;
            boolean before = false;

            if (idx == 0 && tb.getItems().isEmpty()) {
                dockPlace.setWidth(5);
            } else if (idx == tb.getItems().size()) {
                node = findLastVisibleNode(tb.getItems());
            } else {
                node = tb.getItems().get(idx);
                before = true;
            }
            double pos;
            if (node != null) {
                Bounds bnd = node.getBoundsInParent();
                if (tb.getOrientation() == Orientation.HORIZONTAL) {
                    if (before) {
                        pos = bnd.getMinX();
                    } else {
                        pos = bnd.getMinX() + bnd.getWidth();
                    }
                    dockPlace.setX(pos);
                    dockPlace.setY(0);
                } else {
                    if (before) {
                        pos = bnd.getMinY();
                    } else {
                        pos = bnd.getMinY() + bnd.getHeight();
                    }
                    dockPlace.setX(0);
                    dockPlace.setY(pos);

                }
            }
            dockPlace.setVisible(true);
            //dockPlace.toFront();
        }

    }

}//class
