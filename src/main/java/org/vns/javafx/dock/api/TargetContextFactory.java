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
package org.vns.javafx.dock.api;

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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
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
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.DockBorderPane.BorderPaneContext;
import org.vns.javafx.dock.DockTabPane.TabPaneContext;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class TargetContextFactory {

    TargetContext getContext(Node targetNode) {
        if (DockRegistry.isDockTarget(targetNode)) {
            return DockRegistry.dockTarget(targetNode).getTargetContext();
        }
        TargetContext retval = null;
        if (targetNode instanceof StackPane) {
            retval = getStackPaneContext((StackPane) targetNode);
        } else if (targetNode instanceof VBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof HBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof BorderPane) {
            retval = new BorderPaneContext(targetNode);
        } else if (targetNode instanceof VPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof HPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof SplitPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof FlowPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof TabPane) {
            retval = new TabPaneContext(targetNode);
        } else if (targetNode instanceof TextFlow) {
            retval = new ListBasedTargetContext(targetNode);
        }  else if (targetNode instanceof AnchorPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof Pane) {
            retval = getPaneContext((Pane) targetNode);
        }
        return retval;
    }

    protected TargetContext getStackPaneContext(StackPane pane) {
        return new StackPaneContext(pane);
    }

    protected TargetContext getPaneContext(Pane pane) {
        return new PaneContext(pane);
    }

    protected TargetContext getBorderPaneContext(BorderPane pane) {
        TargetContext retval = new DockBorderPane.BorderPaneContext(pane);
        //pane.setAlignment(pane, Pos.CENTER);
        return retval;
    }

    public static class StackPaneContext extends TargetContext {

        public StackPaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((StackPane) getTargetNode()).getChildren().addListener(new NodeListChangeListener(this)); 
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new StackPositionIndicator(this));
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            StackPane target = (StackPane) getTargetNode();
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

        /*    private boolean satisfies(Point2D mousePos, Node node, Pos pos) {
        DockUtil.contains(node, mousePos.getX(), mousePos.getY());
    }
         */
        @Override
        public void remove(Node dockNode) {
            System.err.println("REMOVE ____________________");
            ((StackPane)getTargetNode()).getChildren().remove(dockNode);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getTargetNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.instanceOfDockable(node)) {
                    list.add(DockRegistry.dockable(node));
                }
            });
            return list;
        }

        @Override
        public Object getRestorePosition(Dockable dockable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restore(Dockable dockable, Object restoreposition) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class StackPositionIndicator extends PositionIndicator {

        public StackPositionIndicator(TargetContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            System.err.println("createPositionIndicator");
            Pane targetPane = (Pane) getTargetContext().getTargetNode();
            Label topNode = new Label("Top");
            Label rightNode = new Label("Right");
            Label bottomNode = new Label("Bottom");
            Label leftNode = new Label("Left");
            Label centerNode = new Label("Center");

            topNode.prefWidthProperty().bind(targetPane.widthProperty());
            topNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));
            topNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            rightNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            rightNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            rightNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            leftNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            leftNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            leftNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            bottomNode.prefWidthProperty().bind(targetPane.widthProperty());
            bottomNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));
            bottomNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            centerNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            centerNode.prefWidthProperty().bind(targetPane.widthProperty().divide(2));
            centerNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode);
            topNode.setAlignment(Pos.CENTER);
            rightNode.setAlignment(Pos.CENTER);
            bottomNode.setAlignment(Pos.CENTER);
            leftNode.setAlignment(Pos.CENTER);
            centerNode.setAlignment(Pos.CENTER);

            indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;
            //BorderPane target = (BorderPane) getTargetContext().getTargetNode();

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
            /*        if (visible) {
            //dockAreaStrokeTimeline.stop();
            ((Rectangle) getDockPlace()).strokeDashOffsetProperty().set(0);
            Timeline placeTimeline = new Timeline();
            //dockAreaStrokeTimeline = new Timeline();
            placeTimeline.setCycleCount(Timeline.INDEFINITE);
            KeyValue kv = new KeyValue(((Rectangle) getDockPlace()).strokeDashOffsetProperty(), 12);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            placeTimeline.getKeyFrames().add(kf);
            placeTimeline.play();
        }
             */
        }
        //Timeline placeTimeline = new Timeline();

        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

    }

    public static class PaneContext extends TargetContext {

        public PaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {

            ((Pane) getTargetNode()).getChildren().addListener(new NodeListChangeListener(this));
/*            p.getChildren().addListener(new ListChangeListener<Node>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Node> change) {
                    while (change.next()) {
                        if (change.wasRemoved()) {
                            List<? extends Node> list = change.getRemoved();
                            for (Node d : list) {
                                if (DockRegistry.isDockable(d)) {
                                    undock(d);
                                }
                            }

                        }
                        if (change.wasAdded()) {
                            for (int i = change.getFrom(); i < change.getTo(); i++) {
                                if (DockRegistry.isDockable(change.getList().get(i))) {
                                    commitDock(change.getList().get(i));
                                }

                            }
                        }
                    }//while
                }

            });
*/            
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new PanePositionIndicator(this));
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Pane pane = (Pane) getTargetNode();
            double x = node.getScene().getWindow().getX();
            double y = node.getScene().getWindow().getY();

            if (!DockUtil.contains(getTargetNode(), x, y)) {
                return false;
            }

            Point2D pt = pane.screenToLocal(x, y);
            pane.getChildren().add(node);
            node.relocate(pt.getX(), pt.getY());

            return retval;
        }

        /*    private boolean satisfies(Point2D mousePos, Node node, Pos pos) {
        DockUtil.contains(node, mousePos.getX(), mousePos.getY());
    }
         */
        @Override
        public void remove(Node dockNode) {
            System.err.println("REMOVE ____________________");
            ((Pane) getTargetNode()).getChildren().remove(dockNode);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getTargetNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.instanceOfDockable(node)) {
                    list.add(DockRegistry.dockable(node));
                }
            });
            return list;
        }

        @Override
        public Object getRestorePosition(Dockable dockable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restore(Dockable dockable, Object restoreposition) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class PanePositionIndicator extends PositionIndicator {

        public PanePositionIndicator(TargetContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            System.err.println("createPositionIndicator");
//            Pane targetPane = (Pane) getTargetContext().getTargetNode();
            //Label label = new Label("        ");
            //label.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            Pane indicator = new Pane();
            //indicator.setStyle("-fx-background-color: yellow; -fx-border-width: 2px; -fx-border-color: red");
            indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;
            //BorderPane target = (BorderPane) getTargetContext().getTargetNode();

            Pane p = (Pane) getIndicatorPane();
//            Label label = (Label) p.getChildren().get(0);

            if (getIndicatorPpopup().getDraggedNode() != null) {
                x = getIndicatorPpopup().getDraggedNode().getScene().getWindow().getX();
                y = getIndicatorPpopup().getDraggedNode().getScene().getWindow().getY();
                System.err.println("node x=" + x + "; y=" + y);
                Node n = getIndicatorPpopup().getDraggedNode();
                Bounds b = n.localToScreen(n.getBoundsInLocal());
                System.err.println("   b x=" + b.getMinX() + "; y=" + b.getMinY());

            }
            if (DockUtil.contains(p, x, y)) {
                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            System.err.println("visible=" + visible);
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
            Node draggedNode = getIndicatorPpopup().getDraggedNode();
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

    public static class ListBasedTargetContext extends TargetContext {

        private boolean listBased;
        private ObservableList<Node> items;

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

        public ObservableList<Node> getItems() {
            return items;
        }

        protected void setItems(ObservableList<Node> items) {
            this.items = items;
        }

        protected ObservableList<Node> getNodeList(Node node) {
            if ((getTargetNode() instanceof Pane)) {
                return ((Pane) getTargetNode()).getChildren();
            }
            ObservableList<Node> retval = null;

            Class<?> clazz = node.getClass();
            DefaultProperty a = clazz.getAnnotation(DefaultProperty.class);
            if (a == null) {
                return null;
            }
            String value = a.value();
            String methodName = "get" + value.substring(0, 1).toUpperCase() + value.substring(1);
            try {
                Method method = clazz.getMethod(methodName, new Class[0]);
                if (method.getReturnType().isInstance(retval)) {
                    retval = (ObservableList<Node>) method.invoke(node);
                }
            } catch (NoSuchMethodException | SecurityException ex) {
                return null;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(TargetContextFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return retval;
        }

        private void init() {
            listBased = true;
            items = getNodeList(getTargetNode());
            items.addListener(new NodeListChangeListener(this));
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Node targetNode = (Pane) getTargetNode();

            if (!DockUtil.contains(targetNode, mousePos.getX(), mousePos.getY())) {
                return false;
            }
            int idx = -1;
            for (int i = 0; i < items.size(); i++) {
                Node n = items.get(i);
                if (DockUtil.contains(n, mousePos.getX(), mousePos.getY())) {
                    items.add(i, node);
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                items.add(node);
            }

            return retval;
        }

        @Override
        public void remove(Node dockNode) {
            items.remove(dockNode);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getTargetNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.instanceOfDockable(node)) {
                    list.add(DockRegistry.dockable(node));
                }
            });
            return list;
        }

        @Override
        public Object getRestorePosition(Dockable dockable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restore(Dockable dockable, Object restoreposition) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class ListBasedPositionIndicator extends PositionIndicator {

        public ListBasedPositionIndicator(TargetContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            System.err.println("createPositionIndicator");
//            Pane targetPane = (Pane) getTargetContext().getTargetNode();
            //Label label = new Label("        ");
            //label.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            Pane indicator = new Pane();
            //indicator.setStyle("-fx-background-color: yellow; -fx-border-width: 2px; -fx-border-color: red");
            indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;

            Pane p = (Pane) getIndicatorPane();

            if (DockUtil.contains(p, x, y)) {
                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        protected void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

        protected void adjustPlace(Node pane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            //Point2D pt = pane.screenToLocal(x, y);
            ListBasedTargetContext ctx = (ListBasedTargetContext) getTargetContext();
            Node innerNode = null;
            for (int i = 0; i < ctx.getItems().size(); i++) {
                if (DockUtil.contains(ctx.getItems().get(i), x, y)) {
                    innerNode = ctx.getItems().get(i);
                    break;
                }
            }

            Bounds b = pane.getLayoutBounds();
            System.err.println("innerNode = " + innerNode);
            if (innerNode != null) {
                b = innerNode.getBoundsInParent();
            }

            r.setWidth(b.getWidth());
            r.setHeight(b.getHeight());

            r.setX(b.getMinX());
            r.setY(b.getMinY());
        }

    }

   public static class NodeListChangeListener implements ListChangeListener<Node> {
        
        private final TargetContext context;

        public NodeListChangeListener(TargetContext context) {
            this.context = context;
        }
        
        @Override
        public void onChanged(ListChangeListener.Change<? extends Node> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Node> list = change.getRemoved();
                    for (Node d : list) {
                        if (DockRegistry.isDockable(d)) {
                            context.undock(d);
                        }
                    }

                }
                if (change.wasAdded()) {
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        if (DockRegistry.isDockable(change.getList().get(i))) {
                            context.commitDock(change.getList().get(i));
                        }
                    }
                }
            }//while
        }
    }
}