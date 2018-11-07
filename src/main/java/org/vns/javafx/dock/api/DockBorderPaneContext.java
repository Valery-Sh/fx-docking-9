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
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockBorderPaneContext extends LayoutContext {

    public DockBorderPaneContext(Node dockPane) {
        super(dockPane);
        init();
    }

    private void init() {
        BorderPane pane = (BorderPane) getLayoutNode();
        pane.topProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });
        pane.rightProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });
        pane.bottomProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });

        pane.leftProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });

    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new BorderPanePositionIndicator(this));
    }

    /*        @Override
        protected boolean isDocked(Node node) {
            return ((BorderPane) getLayoutNode()).getChildren().contains(node);
        }
     */
    @Override
    public boolean contains(Object obj) {
        return ((BorderPane) getLayoutNode()).getChildren().contains(obj);
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

        if (contains(d.node()) && d == dockable) {
            return;
        } else if (contains(d.node())) {
            LayoutContext tc = d.getContext().getLayoutContext();
            if (tc != null && isDocked(tc, d)) {
                tc.undock(dockable);
            }
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

    @Override
    public void remove(Object obj) {
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        BorderPane target = (BorderPane) getLayoutNode();
        if (dockNode == target.getTop()) {
            target.setTop(null);
        } else if (dockNode == target.getRight()) {
            target.setRight(null);
        } else if (dockNode == target.getBottom()) {
            target.setBottom(null);
        } else if (dockNode == target.getLeft()) {
            target.setLeft(null);
        } else if (dockNode == target.getCenter()) {
            target.setCenter(null);
        }

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

    public static class BorderPanePositionIndicator extends PositionIndicator {

        public BorderPanePositionIndicator(LayoutContext targetContext) {
            super(targetContext);
            //getStyleClass().add("");
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
            //topNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            //rightNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            rightNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            rightNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            //leftNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
            leftNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            leftNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));

            bottomNode.prefWidthProperty().bind(targetPane.widthProperty());
            bottomNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));
            //bottomNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            centerNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            centerNode.prefWidthProperty().bind(targetPane.widthProperty().divide(2));
            //centerNode.setStyle("-fx-border-color: black; -fx-border-width:1.5; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");

            BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode) {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            
            indicator.getStyleClass().add("border-pane-indicator");
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
        }

        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

    } //PositionIndicator

}//BorderPaneContext

