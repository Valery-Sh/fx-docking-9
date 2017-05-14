/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.panes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.PositionIndicator;

/**
 *
 * @author Valery
 */
public class BorderPanePositionIndicator extends PositionIndicator {

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
