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
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockBorderPane extends BorderPane implements DockTarget {

    //private BorderPane targetPane;
    private DockTargetController targetController;

    public DockBorderPane() {
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public DockTargetController targetController() {
        if (targetController == null) {
            targetController = new DockBorderPaneController(this);
        }
        return targetController;
    }

    public class DockBorderPaneController extends DockTargetController {

        public DockBorderPaneController(Region dockPane) {
            super(dockPane);
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
        protected PositionIndicator createPositionIndicator() {
            //return null;
            return new BorderPanePositionIndicator(this);
        }

        @Override
        public void remove(Node dockNode) {
            System.err.println("REMOVE ____________________");
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
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

    }

    public static class BorderPanePositionIndicator extends PositionIndicator {

        public BorderPanePositionIndicator(DockTargetController targetController) {
            super(targetController);
        }

        @Override
        protected Pane createIndicatorPane() {
            BorderPane borderPane = (BorderPane) getTargetController().getTargetNode();
            Label topNode = new Label("Top");
            Label rightNode = new Label("Right");
            Label bottomNode = new Label("Bottom");
            Label leftNode = new Label("Left");
            Label centerNode = new Label("Center");

            topNode.prefWidthProperty().bind(borderPane.widthProperty());
            topNode.prefHeightProperty().bind(borderPane.heightProperty().divide(4));
            topNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            rightNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            rightNode.prefHeightProperty().bind(borderPane.heightProperty().divide(2));
            rightNode.prefWidthProperty().bind(borderPane.widthProperty().divide(4));

            leftNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            leftNode.prefHeightProperty().bind(borderPane.heightProperty().divide(2));
            leftNode.prefWidthProperty().bind(borderPane.widthProperty().divide(4));

            bottomNode.prefWidthProperty().bind(borderPane.widthProperty());
            bottomNode.prefHeightProperty().bind(borderPane.heightProperty().divide(4));
            bottomNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            centerNode.prefHeightProperty().bind(borderPane.heightProperty().divide(2));
            centerNode.prefWidthProperty().bind(borderPane.widthProperty().divide(2));
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
            BorderPane target = (BorderPane) getTargetController().getTargetNode();

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
