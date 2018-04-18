/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.controls;

import org.vns.javafx.dock.*;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery
 */
public class DemoDockBorderPane implements DockLayout {

    private BorderPane targetPane;
    private LayoutContext targetController;

    public DemoDockBorderPane(BorderPane targetPane) {
        this.targetPane = targetPane;
    }

    @Override
    public Region layoutNode() {
        return targetPane;
    }

    @Override
    public LayoutContext getLayoutContext() {
        if (targetController == null) {
            targetController = new DockBorderPaneController(targetPane);
        }
        return targetController;
    }

    public class DockBorderPaneController extends LayoutContext {

        public DockBorderPaneController(Region dockPane) {
            super(dockPane);
        }

        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);
            //
            // Test is we drag dockable or the value of a dragContainer 
            //
/*            if (contains(d.node()) && d == dockable) {
                return;
            } else if (contains(d.node())) {
                LayoutContext tc = d.getContext().getLayoutContext();
                if (tc != null && isDocked(tc, d)) {
                    tc.undock(d.node());
                }
            }
*/
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
            BorderPane target = (BorderPane) getLayoutNode();
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
        public void remove(Object obj) {
            System.err.println("REMOVE ____________________");
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /*        @Override
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
         */
 /*        @Override
        public boolean restore(Dockable dockable) {
            return false;
        }
         */
    }

    public static class BorderPanePositionIndicator extends PositionIndicator {

        public BorderPanePositionIndicator(LayoutContext targetController) {
            super(targetController);
        }

        @Override
        protected Pane createIndicatorPane() {
            BorderPane borderPane = (BorderPane) getLayoutContext().getLayoutNode();
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
            BorderPane target = (BorderPane) getLayoutContext().getLayoutNode();

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
