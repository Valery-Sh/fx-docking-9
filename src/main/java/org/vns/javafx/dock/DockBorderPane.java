/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockBorderPane extends BorderPane implements DockTarget {

    //private BorderPane targetPane;
    private TargetContext targetContext;

    public DockBorderPane() {
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        if (targetContext == null) {
            targetContext = new BorderPaneContext(this);
        }
        return targetContext;
    }

    public static class BorderPaneContext extends TargetContext {

        public BorderPaneContext(Node dockPane) {
            super(dockPane);
            init();
        }
        private void init() {
            BorderPane pane = (BorderPane) getTargetNode();
            pane.topProperty().addListener( (ov, oldValue, newValue) -> {
                if ( oldValue != null ) {
                    undock(oldValue);
                }
                if ( newValue != null ) {
                    commitDock(newValue);
                }
            });
            pane.rightProperty().addListener( (ov, oldValue, newValue) -> {
                if ( oldValue != null ) {
                    undock(oldValue);
                }
                if ( newValue != null ) {
                    commitDock(newValue);
                }
            });
            pane.bottomProperty().addListener( (ov, oldValue, newValue) -> {
                if ( oldValue != null ) {
                    undock(oldValue);
                }
                if ( newValue != null ) {
                    commitDock(newValue);
                }
            });
            
            pane.leftProperty().addListener( (ov, oldValue, newValue) -> {
                if ( oldValue != null ) {
                    undock(oldValue);
                }
                if ( newValue != null ) {
                    commitDock(newValue);
                }
            });
            
        }
        @Override
        protected void initLookup(ContextLookup lookup) {
            super.initLookup(lookup);
            lookup.putUnique(PositionIndicator.class,new BorderPanePositionIndicator(this));
        }
        
        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            BorderPane target = (BorderPane) getTargetNode();
            BorderPane bp = (BorderPane) getPositionIndicator().getIndicatorPane();
            
            if (target.getTop() == null && DockUtil.contains(bp.getTop(), mousePos.getX(), mousePos.getY())) {
                target.setTop(node);
            } else if (target.getRight() == null && DockUtil.contains(bp.getRight(), mousePos.getX(), mousePos.getY())) {
                target.setRight(node);
            } else if (target.getBottom() == null && DockUtil.contains(bp.getBottom(), mousePos.getX(), mousePos.getY())) {
                target.setBottom(node);
            } else if (target.getLeft() == null && DockUtil.contains(bp.getLeft(), mousePos.getX(), mousePos.getY())) {
                target.setLeft(node);
            } else if (target.getCenter() == null && DockUtil.contains(bp.getCenter(), mousePos.getX(), mousePos.getY())) {
                target.setCenter(node);
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

    public static class BorderPanePositionIndicator extends PositionIndicator {

        public BorderPanePositionIndicator(TargetContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
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

            /*        BorderPane.setAlignment(topNode, Pos.CENTER);
        BorderPane.setAlignment(rightNode, Pos.CENTER);
        BorderPane.setAlignment(bottomNode, Pos.CENTER);
        BorderPane.setAlignment(leftNode, Pos.CENTER);
        BorderPane.setAlignment(centerNode, Pos.CENTER);
             */
            indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
            //indicator.setMouseTransparent(false);
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;
            BorderPane target = (BorderPane) getTargetContext().getTargetNode();

            BorderPane bp = (BorderPane) getIndicatorPane();

            if (target.getTop() == null && DockUtil.contains(bp.getTop(), x, y)) {
                adjustPlace(bp.getTop());
            } else if (target.getRight() == null && DockUtil.contains(bp.getRight(), x, y)) {
                adjustPlace(bp.getRight());
            } else if (target.getBottom() == null && DockUtil.contains(bp.getBottom(), x, y)) {
                adjustPlace(bp.getBottom());
            } else if (target.getLeft() == null && DockUtil.contains(bp.getLeft(), x, y)) {
                adjustPlace(bp.getLeft());
            } else if (target.getCenter() == null && DockUtil.contains(bp.getCenter(), x, y)) {
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

}
