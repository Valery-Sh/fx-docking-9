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
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockTabPane2Context extends LayoutContext { //implements ObjectReceiver {

    public DockTabPane2Context(Node layoutNode) {
        super(layoutNode);
        init();
    }

    /*    public DockTabPane2Context(Dockable dockable) {
        super(dockable);
        init();
    }
     */
    private void init() {
        TabPane pane = (TabPane) getLayoutNode();
        pane.getTabs().forEach(tab -> {
            tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
            if (tab.getContent() != null) {
                commitDock(tab.getContent());
            }
            tab.contentProperty().addListener((o, oldValue, newValue) -> {
                if (newValue != null) {
                    commitDock(newValue);
                }
                if (Dockable.of(oldValue) != null) {
                    DockLayout.of(pane).getLayoutContext().undock(Dockable.of(oldValue));
                }
                if (newValue != null) {
                    DockLayout.of(pane).getLayoutContext().commitDock(newValue);
                }

            });
        });

        pane.getTabs().addListener(new TabsChangeListener(this));
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

        if (dc != null && (dc.getValue() instanceof Tab) && !dc.isValueDockable()) {
            retval = true;
        }
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
        //28.03if (isDocked(d.node())) {
        if (contains(d.node())) {
            return;
        }
        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (stage != null) {
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getContext().setLayoutContext(this);
        }
    }

    @Override
    public boolean contains(Object obj) {
        return ((TabPane) getLayoutNode()).getTabs().contains(obj);
    }

    protected void dock(Point2D mousePos, Tab tab, Dockable dockable) {
        Node placeholder = dockable.getContext().getDragContainer().getPlaceholder();
        Window window;// = null;
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
            if (tab.getContent() != null && Dockable.of(tab.getContent()) != null) {
                Dockable.of(tab.getContent()).getContext().setLayoutContext(this);
            }
            retval = true;
        }
        return retval;
    }

    @Override
    public void remove(Object obj) {
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        TabPane tp = (TabPane) getLayoutNode();
        for (Tab tab : tp.getTabs()) {
            if (tab.getContent() == dockNode) {
                tab.setContent(null);
            }
        }
    }

    public static class TabsChangeListener implements ListChangeListener<Tab> {

        private final DockTabPane2Context tabPaneContext;

        public TabsChangeListener(DockTabPane2Context tabPaneContext) {
            this.tabPaneContext = tabPaneContext;
        }

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
                        if (Dockable.of(tab.getContent()) != null) {
                            tabPaneContext.undock(Dockable.of(tab.getContent()));
                        }
                        //03.04//tabPaneContext.undock(tab.getContent());
                    }

                }
                if (change.wasAdded()) {
                    List<? extends Tab> list = change.getAddedSubList();
                    list.forEach((tab) -> {
                        tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
                        tabPaneContext.commitDock(tab.getContent());
                        tab.contentProperty().addListener((o, oldValue, newValue) -> {
                            //03.04tabPaneContext.undock(oldValue);
                            if (Dockable.of(oldValue) != null) {
                                tabPaneContext.undock(Dockable.of(oldValue));
                            }
                            if (newValue != null) {
                                tabPaneContext.commitDock(newValue);
                            }
                        });
                    });
                }

            }//while
        }
    }

    public static class TabPanePositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private final TabPaneHelper helper;

        public TabPanePositonIndicator(LayoutContext context) {
            super(context);
            helper = new TabPaneHelper((DockTabPane2Context) context);
        }

        /*        @Override
        public void showIndicatorPopup(double screenX, double screenY) {
            getLayoutContext().getLookup().lookup(IndicatorPopup.class).show(getLayoutContext().getTargetNode(), screenX, screenY);
        }
         */
        @Override
        protected Pane createIndicatorPane() {
            Pane p = new Pane();
            p.getStyleClass().add("drag-pane-indicator");
            return p;
        }

        protected String getStylePrefix() {
            return "dock-indicator";
        }

        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                tabDockPlace.getStyleClass().addAll("tab-place");
                getIndicatorPane().getChildren().add(tabDockPlace);
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
            DockTabPane2Context ctx = ((DockTabPane2Context) getLayoutContext());
            TabPane pane = (TabPane) getLayoutContext().getLayoutNode();
            Bounds tabBounds = helper.tabBounds(x, y);;
            Bounds headerAreaBounds = helper.headerAreaBounds(x, y);
            Bounds controlBounds = helper.controlButtonBounds(x, y);

            if (controlBounds != null && !pane.getTabs().isEmpty()) {
                Bounds lastTabBounds = helper.tabBounds(pane.getTabs().get(pane.getTabs().size() - 1));
                Bounds firstTabBounds = helper.tabBounds(pane.getTabs().get(0));
                double delta = 0;
                tabBounds = controlBounds;
                if (!tabBounds.intersects(firstTabBounds)) {
                    //delta = firstTabBounds.getWidth() / 2;
                }

                //double delta = Math.max(lastTabBounds.getWidth() / 3, 10);
                tabBounds = new BoundingBox(tabBounds.getMinX() - delta, lastTabBounds.getMinY(), tabBounds.getWidth() + delta, lastTabBounds.getHeight());
                //tabBounds = new BoundingBox(tabBounds.getMinX(), lastTabBounds.getMinY(), tabBounds.getWidth(), lastTabBounds.getHeight());                
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
