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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class TabPaneContext extends TargetContext implements ObjectReceiver {

    public TabPaneContext(Node targetNode) {
        super(targetNode);
        init();
    }

    public TabPaneContext(Dockable dockable) {
        super(dockable);
        init();
    }

    private void init() {
        TabPane pane = (TabPane) getTargetNode();
        pane.getTabs().forEach(tab -> {
            tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
        });
        pane.getTabs().addListener(new TabsChangeListener());
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new TabPanePositonIndicator(this));
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        boolean retval = false;
        DragContainer dc = dockable.getContext().getDragContainer();
        //Object v = dc.getValue();

        if (dc != null && (dc.isValueDockable())) {
            retval = true;
        } else if ((dc.getValue() instanceof Tab) && !dc.isValueDockable()) {
            retval = true;
        }
        //System.err.println("*************************** IS ACEPTABLE retval = " + retval);

        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Dockable d = dockable;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc.getValue() != null) {
            if (!dc.isValueDockable() && (dc.getValue() instanceof Tab)) {
                dock(mousePos, (Tab) dc.getValue(), dockable);
                return;
            }
            d = Dockable.of(dc.getValue());
        }
        if (isDocked(d.node())) {
//            System.err.println("TargetContext isDocked == TRUE for node = " + d.node());
            return;
        }
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
            d.getContext().setTargetContext(this);
        }
    }

    public void dock(Point2D mousePos, Tab tab, Dockable dockable) {
        Node placeholder = dockable.getContext().getDragContainer().getPlaceholder();
        Window window = null;
        if (placeholder != null) {
            window = placeholder.getScene().getWindow();
        } else {
            window = dockable.node().getScene().getWindow();
        }

//        System.err.println("winndow = " + window);
        if (doDock(mousePos, tab) && window != null) {
            if ((window instanceof Stage)) {
                ((Stage) window).close();
            } else {
                window.hide();
            }
            //dockable.getContext().setFloating(false);
            //d.getContext().setTargetContext(this);
        }
    }

    protected boolean doDock(Point2D mousePos, Tab tab) {
        boolean retval = false;
        int idx = -1;
        TabPaneHelper helper = new TabPaneHelper(this);
        TabPane pane = (TabPane) getTargetNode();
        if (helper.getHeaderArea(mousePos.getX(), mousePos.getY()) != null) {
            idx = pane.getTabs().size();
            
            Bounds ctrlButtonsBounds = helper.controlButtonBounds(mousePos.getX(), mousePos.getY());
            System.err.println("CONTROL BUTTON = " + ctrlButtonsBounds);
            if (ctrlButtonsBounds == null) {
                Tab t = helper.getTab(mousePos.getX(), mousePos.getY());
                if (t != null) {
                    idx = pane.getTabs().indexOf(t);
                }
            }
        }
        if (idx >= 0) {
            pane.getTabs().add(idx, tab);
            retval = true;
        }
        return retval;
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        boolean retval = true;

        return retval;
    }

    @Override
    public Object getRestorePosition(Dockable dockable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(Node dockNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dockObject(Point2D mousePos, Dockable carrier) {
        DragContainer dc = carrier.getContext().getDragContainer();
        if (dc.getValue() != null && (dc.getValue() instanceof Tab)) {
            ((TabPane) getTargetNode()).getTabs().add((Tab) dc.getValue());
        }
    }

    @Override
    public void undockObject(Dockable carrier) {
        DragContainer dc = carrier.getContext().getDragContainer();
        if (dc.getValue() != null && (dc.getValue() instanceof Tab)) {
            ((TabPane) getTargetNode()).getTabs().remove(dc.getValue());
        }
    }

    //
    //
    //
/*    public int getTabIndex(Node tabNode) {
        int retval = -1;
        TabPane pane = (TabPane) getTargetNode();
        String style = getUUIDStyle(tabNode);
        if (style != null) {
            for (Tab tab : pane.getTabs()) {
                String tabStyle = getUUIDStyle(tab);
                if (style.equals(tabStyle)) {
                    retval = pane.getTabs().indexOf(tab);
                    break;
                }
            }
        }
        return retval;
    }
*/
/*    public Tab getTabBy(Node tabNode) {
        int idx = getTabIndex(tabNode);
        if (idx < 0) {
            return null;
        }
        return ((TabPane) getTargetNode()).getTabs().get(idx);
    }
*/
/*    private String getUUIDStyle(Node node) {
        String retval = null;
        for (String s : node.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                retval = s;
                break;
            }
        }
        return retval;
    }

    private String getUUIDStyle(Tab tab) {
        String retval = null;
        for (String s : tab.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                retval = s;
                break;
            }
        }
        return retval;
    }

    protected Node getHeaderArea(double screenX, double screenY) {

        Node retval = getTargetNode().lookup(".tab-header-area");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        //System.err.println("getHeaderArea retvale=" + retval);
        return retval;
    }

    protected Node getControlButtonsTab(double screenX, double screenY) {

        Node retval = getTargetNode().lookup(".control-buttons-tab ");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        //System.err.println("getHeaderArea retvale=" + retval);
        return retval;
    }

    protected Node getHeadersRegion(double screenX, double screenY) {

        Node retval = getTargetNode().lookup(".headers-region");
        //System.err.println("getHeaderArea node=" + retval);

        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        //System.err.println("getHeaderArea retval=" + retval);

        return retval;
    }
*/
/*    protected Tab getTab(double screenX, double screenY) {
        Tab retval = null;
        Set<Node> set = getTargetNode().lookupAll(".tab");
        Node tabNode = null;
        for (Node node : set) {
            if (DockUtil.contains(node, screenX, screenY)) {
                tabNode = node;
                break;
            }
        }
        String style = null;
        if (tabNode != null) {
            for (String s : tabNode.getStyleClass()) {
                if (s.startsWith("tab-uuid-")) {
                    style = s;
                    break;
                }
            }
            TabPane pane = (TabPane) getTargetNode();
            if (style != null) {
                for (Tab tab : pane.getTabs()) {
                    for (String s : tab.getStyleClass()) {
                        if (s.equals(style)) {
                            retval = tab;
                            break;
                        }
                    }
                }
            }
        }
        return retval;
    }
*/
    public static class TabsChangeListener implements ListChangeListener<Tab> {

        @Override
        public void onChanged(Change<? extends Tab> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Tab> list = change.getRemoved();

                    for (Tab tab : list) {
                        String uuidStyle = null;
                        for (String style : tab.getStyleClass()) {
                            if (style.startsWith("tab-uuid-")) {
                                uuidStyle = style;
                                break;
                            }
                        }
                        if (uuidStyle != null) {
                            tab.getStyleClass().remove(uuidStyle);
                        }
                        //targetContext.undock(d.node());
                    }

                }
                if (change.wasAdded()) {
                    List<? extends Tab> list = change.getAddedSubList();
                    for (Tab tab : list) {
                        tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
                    }
                }

            }//while
        }
    }

    public static class TabPanePositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private TabPaneHelper helper;

        public TabPanePositonIndicator(TargetContext context) {
            super(context);
            helper = new TabPaneHelper((TabPaneContext) context);
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            //getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);

            getTargetContext().getLookup().lookup(IndicatorPopup.class).show(getTargetContext().getTargetNode(), screenX, screenY);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane p = new Pane();
            p.getStyleClass().add("drag-pane-indicator");
            return p;
        }

        //@Override
        protected String getStylePrefix() {
            return "dock-indicator";
        }

        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                //tabDockPlace.getStyleClass().addAll("dock-place", "tab-pane-target");
                tabDockPlace.getStyleClass().addAll("tab-place");
                getIndicatorPane().getChildren().add(tabDockPlace);
//                 getIndicatorPane().setStyle("-fx-background-color: aqua");
            }
            return tabDockPlace;
        }

        @Override
        public void hideDockPlace() {
            getDockPlace().setVisible(false);
            getTabDockPlace().setVisible(false);
        }

        @Override
        public void showDockPlace(double x, double y) {
            System.err.println("ShowDockPlace: x=" + x + "; y=" + y);
            TabPaneContext ctx = ((TabPaneContext) getTargetContext());
            TabPane pane = (TabPane) getTargetContext().getTargetNode();
            Bounds tabBounds = helper.tabBounds(x, y);;
            Bounds headerAreaBounds = helper.headerAreaBounds(x, y);
            Bounds controlBounds = helper.controlButtonBounds(x, y);

            if (controlBounds != null && !pane.getTabs().isEmpty()) {
                Bounds lastTabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                tabBounds = controlBounds;
                double delta = Math.max(lastTabBounds.getWidth() / 3, 10);
                tabBounds = new BoundingBox(tabBounds.getMinX() - delta, lastTabBounds.getMinY(), tabBounds.getWidth() + delta, lastTabBounds.getHeight());
            } else if (tabBounds == null && !pane.getTabs().isEmpty() && headerAreaBounds != null) {
                tabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                tabBounds = new BoundingBox(tabBounds.getMinX() + (tabBounds.getWidth() / 3) * 2, tabBounds.getMinY(), tabBounds.getWidth(), tabBounds.getHeight());
            }

            /*            Bounds tabBounds = null;
            Tab tab = helper.getTab(x, y);
            if (tab != null) {
                System.err.println("ShowDockPlace: tab=" + tab);
                Node node = helper.getTabNode(tab);
                tabBounds = node.localToScreen(node.getLayoutBounds());
            }
            System.err.println("ShowDockPlace: tabBounds=" + tabBounds);                
            
            //Bounds tabBounds = null; //ctx.getHelper().screenBounds(x, y);
             */
            if (tabBounds == null) {
                ((Rectangle) getDockPlace()).setVisible(false);
                ((Rectangle) getTabDockPlace()).setVisible(false);
                System.err.println("ShowDockPlace: RETURN");
                return;
            }

            //double tabsHeight = 0;//ctx.getHelper().getTabAreaHeight();
            Rectangle tabPlace = (Rectangle) getTabDockPlace();

            if (!pane.getTabs().isEmpty()) {
                tabPlace.setWidth(tabBounds.getWidth());
                Bounds b = tabPlace.getParent().screenToLocal(tabBounds);
                tabPlace.setHeight(tabBounds.getHeight());
                //
                // idx may be equal to size => the mouse is after last tab
                //
                tabPlace.setX(b.getMinX());
                tabPlace.setY(b.getMinY());
                tabPlace.setVisible(true);
                System.err.println("TabPlace = " + tabPlace);
                tabPlace.toFront();
            } else {
                tabPlace.setVisible(false);
            }

            tabPlace.strokeDashOffsetProperty().set(0);
            if (tabPlace.isVisible()) {
                Timeline placeTimeline = new Timeline();
                placeTimeline.setCycleCount(Timeline.INDEFINITE);
                KeyValue kv = new KeyValue(tabPlace.strokeDashOffsetProperty(), 12);
                KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
                placeTimeline.getKeyFrames().add(kf);
                placeTimeline.play();
            }
        }
    }

}
