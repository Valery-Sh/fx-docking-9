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
import java.util.List;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockTabPaneContext extends LayoutContext { //implements ObjectReceiver{

    public static final String SAVE_DRAGNODE_PROP = "UUID-100b8c98-1b22-4f18-959e-66c16aa3a588";

    private TabPaneHelper helper;

    public DockTabPaneContext(Node tabPane) {
        super((Region) tabPane);
        init();
    }

    private void init() {
        helper = new TabPaneHelper(this);
        getTabPane().getTabs().forEach(tab -> {
            getLayoutNode().getStyleClass().add("tab-uuid-" + UUID.randomUUID());
        });

        getTabPane().getTabs().addListener(new ListChangeListener<Tab>() {
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
                                undock(Dockable.of(d.getContent()));
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

    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new TabPanePositonIndicator(this));
    }

    public TabPaneHelper getHelper() {
        return helper;
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {

        DragContainer dc = dockable.getContext().getDragContainer();

        if (dc != null && (dc.getValue() instanceof Tab) && !dc.isValueDockable()) {
            return true;
        }

        return super.isAcceptable(dockable);
    }
    public TabPane getTabPane() {
        return (TabPane) getLayoutNode();
    }
    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Dockable d = dockable;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null && dc.getValue() != null) {
            if (!dc.isValueDockable() && (dc.getValue() instanceof Tab)) {
                dock(mousePos, (Tab) dc.getValue(), dockable);
                return;
            }
            d = Dockable.of(dc.getValue());
        }

        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, d.node()) && stage != null) {
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getContext().setLayoutContext(this);
        }
    }
    protected void dock(Point2D mousePos, Tab tab, Dockable dockable) {
        Node placeholder = dockable.getContext().getDragContainer().getPlaceholder();
        Window window = null;
        if (placeholder != null) {
            window = placeholder.getScene().getWindow();
        } else {
            window = dockable.node().getScene().getWindow();
        }
        if (doDock(mousePos, tab) && window != null) {
            if ((window instanceof Stage)) {
                ((Stage) window).close();
            } else {
                window.hide();
            }
        }
    }

    protected boolean doDock(Point2D mousePos, Tab tab) {
        boolean retval = false;
        int idx = -1;
        TabPaneHelper helper = new TabPaneHelper(this);
        TabPane pane = (TabPane) getLayoutNode();
        if (helper.getHeaderArea(mousePos.getX(), mousePos.getY()) != null) {

            idx = pane.getTabs().size();

            Bounds ctrlButtonsBounds = helper.controlButtonBounds(mousePos.getX(), mousePos.getY());
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
    public boolean contains(Object obj) {
        boolean retval = false;
        for (Tab tb : getTabPane().getTabs()) {
            if (tb.getContent() == obj) {
                retval = true;
                break;
            }
        }
        return retval;
    }
    protected boolean doDock(Point2D mousePos, Node node) {
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        Dockable dockable = Dockable.of(node);
        TabPane pane = (TabPane) getLayoutNode();

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
        tabLabel.setMouseTransparent(true);
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
        TabPane tabPane = (TabPane) getLayoutNode();

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
            DockableContext context = Dockable.of(node).getContext();
            context.setDragNode(newTab.getGraphic());
            if (context.getLayoutContext() == null || context.getLayoutContext() != this) {
                context.setLayoutContext(this);
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
        TabPane tabPane = (TabPane) getLayoutNode();

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

        if (DockRegistry.isDockable(node)) {
            DockableContext nodeHandler = Dockable.of(node).getContext();
            nodeHandler.setDragNode(tab.getGraphic());
            if (nodeHandler.getLayoutContext() == null || nodeHandler.getLayoutContext() != this) {
                nodeHandler.setLayoutContext(this);
            }
        }
    }

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
        Node tb = dockable.getContext().getTitleBar();
        if (tb == null) {
            return;
        }
        tb.getProperties().put("titleBarVisible", tb.isVisible());
        if (tb instanceof Region) {
            tb.getProperties().put("titleBarMinHeight", ((Region) tb).getMinHeight());
            tb.getProperties().put("titleBarPrefHeight", ((Region) tb).getPrefHeight());
        }
        dockable.node().getProperties().put("titleBar", tb);
        dockable.getContext().setTitleBar(null);
    }

    protected void hideContentTitleBar(Dockable dockable) {
        Node tb = dockable.getContext().getTitleBar();
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
    public void remove(Object obj) {
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        Tab tab = null;
        for (Tab tb : getTabPane().getTabs()) {
            if (tb.getContent() == dockNode) {
                tab = tb;
                break;
            }
        }
        if (tab != null) {
            showContentTitleBar(Dockable.of(dockNode));
            getTabPane().getTabs().remove(tab);
        }
    }

    public static class TabPanePositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private TabPaneHelper helper;

        public TabPanePositonIndicator(LayoutContext context) {
            super(context);
            helper = new TabPaneHelper(context);

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

        protected String getStylePrefix() {
            return "dock-indicator";
        }

        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                tabDockPlace.setId("tabDockPlace");
                tabDockPlace.getStyleClass().add("dock-place");
                //StyleUtil.styleDockPlace(tabDockPlace);
                getIndicatorPane().getChildren().add(0, tabDockPlace);
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
            DockTabPaneContext ctx = ((DockTabPaneContext) getLayoutContext());
            TabPane pane = (TabPane) getLayoutContext().getLayoutNode();

            Bounds tabBounds = helper.tabBounds(x, y);;
            Bounds headerAreaBounds = helper.headerAreaBounds(x, y);
            Bounds controlBounds = helper.controlButtonBounds(x, y);

            if (controlBounds != null && !pane.getTabs().isEmpty()) {
                Bounds lastTabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                //Bounds firstTabBounds = helper.tabBounds(pane.getTabs().get(0));
                double delta = 0;
                tabBounds = controlBounds;

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

                Bounds b = tabPlace.getParent().screenToLocal(tabBounds);

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

    public class TabPaneContextListener implements ChangeListener<LayoutContext> {

        private final Node saveDragNode;
        private final DockableContext dockableContext;

        public TabPaneContextListener(Node saveDragNode, DockableContext dockableContext) {
            this.saveDragNode = saveDragNode;
            this.dockableContext = dockableContext;
        }

        @Override
        public void changed(ObservableValue<? extends LayoutContext> observable, LayoutContext oldValue, LayoutContext newValue) {
            if (newValue != DockTabPaneContext.this) {
                dockableContext.setDragNode(saveDragNode);
            }
            if (oldValue != null) {
                dockableContext.layoutContexProperty().removeListener(this);
            }
        }

    }
}
