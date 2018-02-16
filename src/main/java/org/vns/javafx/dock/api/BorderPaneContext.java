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
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class BorderPaneContext extends TargetContext {

        public BorderPaneContext(Node dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            BorderPane pane = (BorderPane) getTargetNode();
            pane.topProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    undock(oldValue);
                }
                if (newValue != null) {
                    commitDock(newValue);
                }
            });
            pane.rightProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    undock(oldValue);
                }
                if (newValue != null) {
                    commitDock(newValue);
                }
            });
            pane.bottomProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    undock(oldValue);
                }
                if (newValue != null) {
                    commitDock(newValue);
                }
            });

            pane.leftProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    undock(oldValue);
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

        @Override
        public boolean isDocked(Node node) {
            return ((BorderPane) getTargetNode()).getChildren().contains(node);
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
            //System.err.println("REMOVE ____________________");
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
                //!!!08
                if (DockRegistry.isDockable(node)) {
                    list.add(Dockable.of(node));
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

                indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
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
