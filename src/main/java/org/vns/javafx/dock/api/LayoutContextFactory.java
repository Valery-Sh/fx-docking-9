/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Verion 2.0 (the "License");
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.api.LayoutContext.getValue;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class LayoutContextFactory {

    public LayoutContext getContext(Node targetNode) {
        if (DockRegistry.isDockLayout(targetNode)) {
            return DockRegistry.dockLayout(targetNode).getLayoutContext();
        }
        LayoutContext retval = null;
        if (targetNode instanceof StackPane) {
            retval = getStackPaneContext((StackPane) targetNode);
        } else if (targetNode instanceof VBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof HBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof BorderPane) {
            retval = new DockBorderPaneContext(targetNode);

            for (Node obj : ((BorderPane) targetNode).getChildren()) {
                DockableContext dc = null;
                if (Dockable.of(obj) != null) {
                    dc = Dockable.of(obj).getContext();
                } else {
                    dc = DockRegistry.makeDockable(obj).getContext();
                }
                dc.setLayoutContext(retval);

            }
        } else if ((targetNode instanceof SplitPane && !(targetNode instanceof DockSplitPane))) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof FlowPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof TabPane) {
            retval = new DockTabPane2Context(targetNode);
        } else if (targetNode instanceof TextFlow) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof AnchorPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof Pane) {
            retval = getPaneContext((Pane) targetNode);
        } else if (targetNode instanceof Accordion) {
            retval = new ListBasedTargetContext<TitledPane>(targetNode) {
                @Override
                public boolean isAcceptable(Dockable obj) {
                    boolean b = super.isAcceptable(obj);
                    if (b) {
                        Dockable dragged = obj;
                        Object v = getValue(obj);
                        if (Dockable.of(v) != null) {
                            dragged = Dockable.of(v);
                            b = (dragged.node() instanceof TitledPane);
                        }
                    }
                    return b;
                }
            };
        }
        return retval;
    }

    protected LayoutContext getStackPaneContext(StackPane pane) {
        LayoutContext lc = new StackPaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;
    }

    protected LayoutContext getPaneContext(Pane pane) {
        LayoutContext lc = new PaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;

    }

    protected LayoutContext getBorderPaneContext(BorderPane pane) {
        LayoutContext lc = new DockBorderPaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;
    }

    public static class StackPaneContext extends LayoutContext {

        public StackPaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((StackPane) getLayoutNode()).getChildren().addListener(new NodeListChangeListener(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new StackPositionIndicator(this));
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);

            dockable.getContext().getLayoutContext().undock(dockable);

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
            boolean retval = true;
            StackPane target = (StackPane) getLayoutNode();
            BorderPane bp = (BorderPane) getPositionIndicator().getIndicatorPane();
            if (DockUtil.contains(bp.getTop(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.TOP_CENTER);
            } else if (DockUtil.contains(bp.getRight(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER_RIGHT);

            } else if (DockUtil.contains(bp.getBottom(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.BOTTOM_CENTER);

            } else if (DockUtil.contains(bp.getLeft(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER_LEFT);

            } else if (DockUtil.contains(bp.getCenter(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER);
            } else {
                retval = false;
            }
            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            ((StackPane) getLayoutNode()).getChildren().remove(dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return ((StackPane) getLayoutNode()).getChildren().contains(obj);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getLayoutNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.isDockable(node)) {
                    list.add(Dockable.of(node));
                }
            });
            return list;
        }
    }

    public static class StackPositionIndicator extends PositionIndicator {

        public StackPositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {

            Pane targetPane = (Pane) getLayoutContext().getLayoutNode();
            Label topNode = new Label("Top");
            topNode.getStyleClass().add("top");
            Label rightNode = new Label("Right");
            rightNode.getStyleClass().add("right");
            Label bottomNode = new Label("Bottom");
            bottomNode.getStyleClass().add("bottom");
            Label leftNode = new Label("Left");
            leftNode.getStyleClass().add("left");
            Label centerNode = new Label("Center");
            centerNode.getStyleClass().add("center");

            topNode.prefWidthProperty().bind(targetPane.widthProperty());
            topNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));

            rightNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            rightNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            leftNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            leftNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            bottomNode.prefWidthProperty().bind(targetPane.widthProperty());
            bottomNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));

            centerNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            centerNode.prefWidthProperty().bind(targetPane.widthProperty().divide(2));

            BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode) {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.getStyleClass().add("stack-pane-indicator");
            topNode.setAlignment(Pos.CENTER);
            rightNode.setAlignment(Pos.CENTER);
            bottomNode.setAlignment(Pos.CENTER);
            leftNode.setAlignment(Pos.CENTER);
            centerNode.setAlignment(Pos.CENTER);

            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;

            BorderPane bp = (BorderPane) getIndicatorPane();

            if (DockUtil.contains(bp.getTop(), x, y)) {
                adjustPlace(bp.getTop());
            } else if (DockUtil.contains(bp.getRight(), x, y)) {
                adjustPlace(bp.getRight());
            } else if (DockUtil.contains(bp.getBottom(), x, y)) {
                adjustPlace(bp.getBottom());
            } else if (DockUtil.contains(bp.getLeft(), x, y)) {
                adjustPlace(bp.getLeft());
            } else if (DockUtil.contains(bp.getCenter(), x, y)) {
                adjustPlace(bp.getCenter());
            } else {
                visible = false;
            }

            getDockPlace().setVisible(visible);
        }

        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

    }

    public static class PaneContext extends LayoutContext {

        public PaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((Pane) getLayoutNode()).getChildren().addListener(new NodeListChangeListener(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new PanePositionIndicator(this));
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
            dockable.getContext().getLayoutContext().undock(dockable);

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

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Pane pane = (Pane) getLayoutNode();
            double x = node.getScene().getWindow().getX();
            double y = node.getScene().getWindow().getY();

            if (!DockUtil.contains(getLayoutNode(), x, y)) {
                return false;
            }

            Point2D pt = pane.screenToLocal(x, y);
            pane.getChildren().add(node);
            node.relocate(pt.getX(), pt.getY());

            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            ((Pane) getLayoutNode()).getChildren().remove(dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return ((Pane) getLayoutNode()).getChildren().contains(obj);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getLayoutNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.isDockable(node)) {
                    list.add(DockRegistry.dockable(node));
                }
            });
            return list;
        }

    }

    public static class PanePositionIndicator extends PositionIndicator {

        public PanePositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;
            Pane p = (Pane) getIndicatorPane();

            if (getIndicatorPopup().getDraggedNode() != null) {
                x = getIndicatorPopup().getDraggedNode().getScene().getWindow().getX();
                y = getIndicatorPopup().getDraggedNode().getScene().getWindow().getY();
                //Node n = getIndicatorPopup().getDraggedNode();
                //Bounds b = n.localToScreen(n.getBoundsInLocal());
                //Bounds b = n.localToScreen(n.getBoundsInLocal());
            }
            if (DockUtil.contains(p, x, y)) {
                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

        private void adjustPlace(Pane pane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            Point2D pt = pane.screenToLocal(x, y);
            Node draggedNode = getIndicatorPopup().getDraggedNode();
            if (draggedNode != null) {
                r.setWidth(draggedNode.getScene().getWindow().getWidth());
                r.setHeight(draggedNode.getScene().getWindow().getHeight());
                pt = pane.screenToLocal(draggedNode.getScene().getWindow().getX(), draggedNode.getScene().getWindow().getY());
                r.setX(pt.getX());
                r.setY(pt.getY());

            } else {
                r.setHeight(20);
                r.setWidth(75);
                r.setX(pt.getX());
                r.setY(pt.getY());
            }
        }

    }

    public static class ListBasedTargetContext<T extends Node> extends LayoutContext {

        private boolean listBased;
        private ObservableList<T> items;

        public ListBasedTargetContext(Node pane) {
            super(pane);
            init();
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new ListBasedPositionIndicator(this));
        }

        public boolean isListBased() {
            return listBased;
        }

        public ObservableList<T> getItems() {
            return items;
        }

        protected void setItems(ObservableList<T> items) {
            this.items = items;
        }

        protected ObservableList<T> getNodeList(Node node) {
            if ((getLayoutNode() instanceof Pane)) {
                return (ObservableList<T>) ((Pane) getLayoutNode()).getChildren();
            }
            if ((getLayoutNode() instanceof Accordion)) {
                return (ObservableList<T>) ((Accordion) getLayoutNode()).getPanes();
            }

            ObservableList<T> retval = null;

            Class<?> clazz = node.getClass();
            DefaultProperty a = clazz.getAnnotation(DefaultProperty.class);
            if (a == null) {
                return null;
            }
            String value = a.value();

            String methodName = "get" + value.substring(0, 1).toUpperCase() + value.substring(1);

            try {
                Method method = clazz.getMethod(methodName, new Class[0]);
                //if (method.getReturnType().isInstance(retval)) {
                retval = (ObservableList<T>) method.invoke(node);
                //}
            } catch (NoSuchMethodException | SecurityException ex) {
                return null;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(LayoutContextFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return retval;
        }

        private void init() {
            listBased = true;
            items = getNodeList(getLayoutNode());
            items.addListener(new NodeListChangeListener(this));
            items.forEach(it -> {
                DockableContext dc = null;
                if (Dockable.of(dc) != null) {
                    dc = Dockable.of(it).getContext();
                    dc.setLayoutContext(this);
                } else {
                    DockRegistry.makeDockable(it);
                    if (Dockable.of(it) != null) {
                        Dockable.of(it).getContext().setLayoutContext(this);
                    }
                }

            });
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);

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

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Node targetNode = getLayoutNode();

            if (!DockUtil.contains(targetNode, mousePos.getX(), mousePos.getY())) {
                return false;
            }
            int idx = -1;
            Node innerNode = TopNodeHelper.getTop(targetNode, mousePos.getX(), mousePos.getY(), n -> {return getItems().contains(n);} );
            if ( innerNode != null ) {
                idx = getItems().indexOf(innerNode);
            }
/*            for (int i = 0; i < items.size(); i++) {
                innerNode = items.get(i);
                if (DockUtil.contains(innerNode, mousePos.getX(), mousePos.getY())) {
                    idx = i;
                    break;
                }
            }
*/
            if (idx == -1) {
                items.add((T) node);
            } else if (((targetNode instanceof VBox) || ((targetNode instanceof Accordion)))) {
                Bounds b = DockUtil.getHalfBounds(Side.TOP, innerNode, mousePos.getX(), mousePos.getY());
                if (b != null && b.contains(mousePos)) {
                    items.add(idx, (T) node);
                } else {
                    b = DockUtil.getHalfBounds(Side.BOTTOM, innerNode, mousePos.getX(), mousePos.getY());
                    if (b != null && b.contains(mousePos)) {
                        items.add(idx + 1, (T) node);
                    }
                }
            } else if (targetNode instanceof HBox) {
                Bounds b = DockUtil.getHalfBounds(Side.LEFT, innerNode, mousePos.getX(), mousePos.getY());
                if (b != null && b.contains(mousePos)) {
                    items.add(idx, (T) node);
                } else {
                    b = DockUtil.getHalfBounds(Side.RIGHT, innerNode, mousePos.getX(), mousePos.getY());
                    if (b != null && b.contains(mousePos)) {
                        items.add(idx + 1, (T) node);
                    }
                }
            } else {
            }
            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            items.remove((T) dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return items.contains((T) obj);
        }

    }

    public static class ListBasedPositionIndicator extends PositionIndicator {

        public ListBasedPositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        public IndicatorPopup getIndicatorPopup() {
            IndicatorPopup ip = super.getIndicatorPopup();
            ((Region) ip.getTargetNode()).layout();
            ((Region) ip.getTargetNode()).requestLayout();
            return ip;
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("list-ind-pane");
            indicator.getStyleClass().add("list-based-indicator");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;

            Pane p = (Pane) getIndicatorPane();
            //System.err.println("showDockPlace DockUtil.contains(p, x, y) = " + DockUtil.contains(p, x, y));
            if (DockUtil.contains(p, x, y)) {

                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        protected void adjustPlace(Node node) {

            Pane p = getIndicatorPane();

            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());

            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());

        }

        protected void adjustPlace(Node indPane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            ListBasedTargetContext ctx = (ListBasedTargetContext) getLayoutContext();
            Region targetPane = (Region) ctx.getLayoutNode();
            System.err.println("targetPane.getHeight = " + targetPane.getHeight());
            System.err.println("targetPane.getWidth = " + targetPane.getWidth());
            Node innerNode = TopNodeHelper.getTop(ctx.getLayoutNode(), x, y, n -> {
                return ctx.getItems().contains(n);
            });
            System.err.println("innereNode = " + innerNode);
            //
            // We know that the indicatore pate never transformed
            //
            Bounds indBounds = indPane.getBoundsInLocal();
            Insets ins = getIndicatorPane().getInsets();

            if (innerNode != null) {
                Bounds screenBounds = innerNode.localToScreen(innerNode.parentToLocal(innerNode.getBoundsInParent()));
                Bounds nodeBounds = indPane.screenToLocal(screenBounds);

                if ((targetPane instanceof VBox) || (targetPane instanceof Accordion)) {
                    r.setX(ins.getLeft());
                    r.setWidth(indBounds.getWidth() - ins.getLeft() - ins.getRight());

                    if (nodeBounds.getHeight() < 20) {
                        r.setHeight(nodeBounds.getHeight() / 2);
                    } else {
                        r.setHeight(10);
                    }
                    if (y < screenBounds.getMinY() + screenBounds.getHeight() / 2) {
                        r.setY(nodeBounds.getMinY());
                    } else if (nodeBounds.getHeight() < 20) {
                        r.setY(nodeBounds.getMinY() + nodeBounds.getHeight() / 2);
                    } else {
                        r.setY(nodeBounds.getMinY() + nodeBounds.getHeight() - 10);
                    }

                } else if (targetPane instanceof HBox) {
                    r.setHeight(indBounds.getHeight() - ins.getTop() - ins.getBottom());
                    r.setY(ins.getTop());
                    if (nodeBounds.getWidth() < 20) {
                        r.setWidth(nodeBounds.getWidth() / 2);
                    } else {
                        r.setWidth(10);
                    }
                    if (x < screenBounds.getMinX() + nodeBounds.getWidth() / 2) {
                        r.setX(nodeBounds.getMinX());
                    } else if (nodeBounds.getWidth() < 20) {
                        r.setX(nodeBounds.getMinX() + (nodeBounds.getWidth() / 2));
                    } else {
                        r.setX(nodeBounds.getMinX() + nodeBounds.getWidth() - 10);
                    }
                } else {
                    r.setWidth(nodeBounds.getWidth());
                    r.setHeight(nodeBounds.getHeight());

                    r.setX(nodeBounds.getMinX());
                    r.setY(nodeBounds.getMinY());
                }
            }
        }
    }

    public static class NodeListChangeListener implements ListChangeListener<Node> {

        private final LayoutContext context;

        public NodeListChangeListener(LayoutContext context) {
            this.context = context;
        }

        @Override
        public void onChanged(ListChangeListener.Change<? extends Node> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Node> list = change.getRemoved();

                    for (Node d : list) {
                        if (DockRegistry.isDockable(d)) {
                            context.undock(Dockable.of(d));
                        }
                    }
                }
                if (change.wasAdded()) {
                    List<? extends Node> list = change.getAddedSubList();
                    for (Node n : list) {
                        if (Dockable.of(n) != null) {
                            context.commitDock(n);
                        }
                    }
                }
            }//while
        }
    }
}
