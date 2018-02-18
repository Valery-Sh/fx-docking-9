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
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.save.DockTreeItemBuilderFactory;

/**
 *
 * @author Valery
 */
public class DockTabPaneContext extends TargetContext {

    public static final String SAVE_DRAGNODE_PROP = "UUID-100b8c98-1b22-4f18-959e-66c16aa3a588";

    private TabPaneHelper helper;

    public DockTabPaneContext(Node tabPane) {
        super((Region) tabPane);
        init();
    }

    private void init() {
        helper = new TabPaneHelper(this);
        getTargetNode().getTabs().forEach(tab -> {
            getTargetNode().getStyleClass().add("tab-uuid-" + UUID.randomUUID());
        });

        getTargetNode().getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Tab> change) {
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
                        }

                        for (Tab d : list) {
                            if (d.getContent() != null && Dockable.isDockable(d.getContent())) {
                                undock(d.getContent());
                            }
                        }

                    }
                    if (change.wasAdded()) {
                        for (Tab tab : change.getList()) {
                            tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
                        }
                        for (int i = change.getFrom(); i < change.getTo(); i++) {
                            Node node = change.getList().get(i).getContent();
                            if (node != null && DockRegistry.isDockable(node)) {
                                commitDock(i, change.getList().get(i));
                                commitDock(node);
                            }
                        }
                    }
                }//while
            }

        });

        //getLookup().putUnique(PositionIndicator.class,new TabPanePositonIndicator(this));
        // getLookup().putUnique(IndicatorPopup.class,new IndicatorPopup(this));
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new TabPanePositonIndicator(this));
        lookup.add(new DockTreeItemBuilderFactory());
    }

    public TabPaneHelper getHelper() {
        return helper;
    }

    @Override
    public TabPane getTargetNode() {
        return (TabPane) super.getTargetNode();
    }

    /**
     * For test purpose
     *
     * @return th elis of dockables
     */
/*    public ObservableList<Dockable> getDockables() {
        List<Dockable> list = FXCollections.observableArrayList();
        getTargetNode().getTabs().forEach(tab -> {
            //!!!08
            if (tab.getContent() != null && DockRegistry.isDockable(tab.getContent())) {
                list.add(Dockable.of(tab.getContent()));
            }
        });
        return (ObservableList<Dockable>) list;
    }
*/
    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        for (Tab tb : getTargetNode().getTabs()) {
            if (tb.getContent() == node) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        Dockable dockable = Dockable.of(node);
        TabPane pane = (TabPane) getTargetNode();
        //TabGraphic tabGraphic = new TabGraphic(dockable, pane);
        int idx = -1;
        Node headerArea = helper.getHeaderArea(mousePos.getX(), mousePos.getY());
        if (mousePos != null) {
            if (headerArea != null) {
                idx = pane.getTabs().size();
                if (helper.controlButtonBounds(mousePos.getX(), mousePos.getY()) == null) {
                    Tab tab = helper.getTab(mousePos.getX(), mousePos.getY());
                    if (tab != null) {
                        idx = pane.getTabs().indexOf(tab);
                    }
                }
            } 
        }
        if (idx < 0 && pane.getTabs().size() > 0) {
            return false;
        }
        if (idx < 0 && mousePos != null && !pane.localToScreen(pane.getBoundsInLocal()).contains(mousePos.getX(), mousePos.getY())) {
            return false;
        }
        String txt = getButtonText(dockable);
        if (txt.isEmpty()) {
            txt = " ... ";
        }

        Tab newTab = new Tab();
        Label tabLabel = new Label(txt);
        newTab.setGraphic(tabLabel);

        if (idx >= 0) {
            pane.getTabs().add(idx, newTab);
            pane.getTabs().get(idx).setContent(node);
        } else {
            pane.getTabs().add(newTab);
            pane.getTabs().get(pane.getTabs().indexOf(newTab)).setContent(node);
        }
        if (stage != null) {
            if (stage instanceof Stage) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
        }

        hideContentTitleBar(dockable);
        pane.getSelectionModel().select(newTab);

        if (DockRegistry.isDockable(node)) {
            DockableContext dockableContext = Dockable.of(node).getContext();
            Node saveDragNode = dockableContext.getDragNode();
            dockableContext.setDragNode(newTab.getGraphic());
            if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                dockableContext.setTargetContext(this);
            }
            dockableContext.targetContextProperty().addListener(new TabPaneContextListener(saveDragNode, dockableContext));
        }
        return true;
    }

    public boolean doDock(int idx, Node node) {
        if (idx < 0) {
            return false;
        }
        Stage stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            stage = (Stage) node.getScene().getWindow();
        }

        Dockable dockable = Dockable.of(node);
        TabPane tabPane = (TabPane) getTargetNode();

        String txt = getButtonText(dockable);
        if (txt.isEmpty()) {
            txt = " ... ";
        }

        Tab newTab = new Tab();
        Label tabLabel = new Label(txt);
        newTab.setGraphic(tabLabel);
        tabPane.getTabs().add(idx, newTab);
        tabPane.getTabs().get(idx).setContent(node);

        if (stage != null) {
            stage.close();
        }

        hideContentTitleBar(dockable);
        tabPane.getSelectionModel().select(newTab);

        if (DockRegistry.isDockable(node)) {
            DockableContext nodeHandler = Dockable.of(node).getContext();
            nodeHandler.setDragNode(newTab.getGraphic());
            if (nodeHandler.getTargetContext() == null || nodeHandler.getTargetContext() != this) {
                nodeHandler.setTargetContext(this);
            }
        }
        return true;
    }

    protected void commitDock(int idx, Tab tab) {
        if (idx < 0) {
            return;
        }
        Node node = null;
        if (tab.getContent() != null && DockRegistry.isDockable(tab.getContent())) {
            node = tab.getContent();
        } else {
            return;
        }
        Stage stage = null;

        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            stage = (Stage) node.getScene().getWindow();
        }

        Dockable dockable = Dockable.of(node);
        TabPane tabPane = (TabPane) getTargetNode();

        String txt = getButtonText(dockable);
        if (txt.isEmpty()) {
            txt = " ... ";
        }

        Label tabLabel = new Label(txt);
        tab.setGraphic(tabLabel);

        if (stage != null) {
            stage.close();
        }

        hideContentTitleBar(dockable);
        tabPane.getSelectionModel().select(tab);
        //02.02((Region) node).prefHeightProperty().bind(pane.heightProperty());
        //03.02((Region) node).prefWidthProperty().bind(pane.widthProperty());
        //!!!08
        if (DockRegistry.isDockable(node)) {
            DockableContext nodeHandler = Dockable.of(node).getContext();
            nodeHandler.setDragNode(tab.getGraphic());
            if (nodeHandler.getTargetContext() == null || nodeHandler.getTargetContext() != this) {
                nodeHandler.setTargetContext(this);
            }
        }
    }

    public void dock(Dockable dockable) {
        if (doDock(null, dockable.node())) {
            // dockable.getContext().setFloating(false);
        }
    }

    public void dock(Node node) {
        if (DockRegistry.isDockable(node)) {
            dock(Dockable.of(node));
        }
    }

    /*        @Override
        public PositionIndicator getPositionIndicator() {
            if (positionIndicator == null) {
                createPositionIndicator();
            }
            return positionIndicator;
        }
     */
    protected String getButtonText(Dockable d) {
        String txt = d.getContext().getTitle();
        if (d.getContext().getProperties().getProperty("user-title") != null) {
            txt = d.getContext().getProperties().getProperty("user-title");
        } else if (d.getContext().getProperties().getProperty("short-title") != null) {
            txt = d.getContext().getProperties().getProperty("short-title");
        } else if (d.node().getId() != null && d.node().getId().isEmpty()) {
            txt = d.node().getId();
        }
        if (txt == null || txt.trim().isEmpty()) {
            txt = "";
        }
        return txt;
    }

    public void saveContentTitleBar(Dockable dockable) {
        Region tb = dockable.getContext().getTitleBar();
        if (tb == null) {
            return;
        }
        tb.getProperties().put("titleBarVisible", tb.isVisible());
        tb.getProperties().put("titleBarMinHeight", tb.getMinHeight());
        tb.getProperties().put("titleBarPrefHeight", tb.getPrefHeight());
        dockable.node().getProperties().put("titleBar", tb);
        dockable.getContext().setTitleBar(null);
    }

    protected void hideContentTitleBar(Dockable dockable) {
        Region tb = dockable.getContext().getTitleBar();
        if (tb == null) {
            return;
        }
        saveContentTitleBar(dockable);
    }

    public void showContentTitleBar(Dockable dockable) {
        Region tb = (Region) dockable.node().getProperties().get("titleBar");
        if (tb == null) {
            return;
        }
        dockable.getContext().setTitleBar(tb);
    }

    @Override
    public void remove(Node dockNode) {
        Tab tab = null;
        for (Tab tb : getTargetNode().getTabs()) {
            if (tb.getContent() == dockNode) {
                tab = tb;
                break;
            }
        }
        if (tab != null) {
            showContentTitleBar(Dockable.of(dockNode));
            getTargetNode().getTabs().remove(tab);
        }
    }

    @Override
    public Object getRestorePosition(Dockable dockable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class TabPanePositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private TabPaneHelper helper;

        public TabPanePositonIndicator(TargetContext context) {
            super(context);
            helper = new TabPaneHelper(context);
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
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
                tabDockPlace.setId("tabDockPlace");
                tabDockPlace.getStyleClass().add("dock-place");
                
                getIndicatorPane().getChildren().add(0,tabDockPlace);
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
            DockTabPaneContext ctx = ((DockTabPaneContext) getTargetContext());
            TabPane pane = (TabPane) getTargetContext().getTargetNode();

            Bounds tabBounds = helper.tabBounds(x, y);;
            Bounds headerAreaBounds = helper.headerAreaBounds(x, y);
            Bounds controlBounds = helper.controlButtonBounds(x, y);

            if (controlBounds != null && !pane.getTabs().isEmpty()) {
                Bounds lastTabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                Bounds firstTabBounds = helper.tabBounds(pane.getTabs().get(0));
                double delta = 0;
                tabBounds = controlBounds;
                if ( ! tabBounds.intersects(firstTabBounds)) {
                    //delta = firstTabBounds.getWidth() / 2;
                }
              
                tabBounds = new BoundingBox(tabBounds.getMinX() - delta, lastTabBounds.getMinY(), tabBounds.getWidth() + delta, lastTabBounds.getHeight());

            } else if (tabBounds == null && !pane.getTabs().isEmpty() && headerAreaBounds != null) {
                tabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                tabBounds = new BoundingBox(tabBounds.getMinX() + (tabBounds.getWidth() / 3) * 2, tabBounds.getMinY(), tabBounds.getWidth(), tabBounds.getHeight());
            }
            if (tabBounds == null) {
                ((Rectangle) getDockPlace()).setVisible(false);
                ((Rectangle) getTabDockPlace()).setVisible(false);
                return;
            }

            Rectangle tabPlace = (Rectangle) getTabDockPlace();
            Rectangle dockPlace = (Rectangle) getDockPlace();

            dockPlace.setWidth(pane.getWidth());
            dockPlace.setHeight(pane.getHeight() / 2);
            Point2D p = dockPlace.localToParent(0, 0);

            dockPlace.setX(p.getX());
            dockPlace.setY(p.getY() + tabBounds.getHeight());

            dockPlace.setVisible(true);
            
            dockPlace.toFront();

            if (!pane.getTabs().isEmpty()) {
                tabPlace.setWidth(tabBounds.getWidth());
                tabPlace.setHeight(tabBounds.getHeight());
                
                //tabPlace.setX(tabBounds.getMinX());
                //tabPlace.setY(tabBounds.getMinY());
                
                Bounds b = tabPlace.getParent().screenToLocal(tabBounds);
                //tabPlace.setHeight(tabBounds.getHeight());
                //
                // idx may be equal to size => the mouse is after last tab
                //
                
                tabPlace.setX(b.getMinX());
                
                tabPlace.setY(b.getMinY());
                tabPlace.setVisible(true);
                
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

    public class TabPaneContextListener implements ChangeListener<TargetContext> {

        private final Node saveDragNode;
        private final DockableContext dockableContext;

        public TabPaneContextListener(Node saveDragNode, DockableContext dockableContext) {
            this.saveDragNode = saveDragNode;
            this.dockableContext = dockableContext;
        }

        @Override
        public void changed(ObservableValue<? extends TargetContext> observable, TargetContext oldValue, TargetContext newValue) {
            if (newValue != DockTabPaneContext.this) {
                dockableContext.setDragNode(saveDragNode);
            }
            if (oldValue != null) {
                dockableContext.targetContextProperty().removeListener(this);
            }
        }

    }
}
