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
package org.vns.javafx.dock.api.indicator;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.LayoutContextFactory;

/**
 *
 * @author Olga
 */
public class DefaultPositionIndicator extends PositionIndicator {

    public DefaultPositionIndicator(LayoutContext targetContext) {
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
        Pane indicator = new Pane();
        indicator.getStyleClass().add("default-position-indicator");
        indicator.setStyle("-fx-border-width: 1px; -fx-border-color: red");
        return indicator;
    }

    @Override
    public void showDockPlace(double x, double y) {

        boolean visible = true;

        Pane p = (Pane) getIndicatorPane();

        if (DockUtil.contains(p, x, y)) {
            //adjustPlace(p, x, y);
            adjustPlace(p);
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

    /*        protected void adjustPlace(Node pane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            //Point2D pt = pane.screenToLocal(x, y);
            LayoutContextFactory.ListBasedTargetContext ctx = (LayoutContextFactory.ListBasedTargetContext) getLayoutContext();
            Region targetPane = (Region) ctx.getLayoutNode();
            Node innerNode = null;
            for (int i = 0; i < ctx.getItems().size(); i++) {
                if (DockUtil.contains((Node) ctx.getItems().get(i), x, y)) {
                    innerNode = (Node) ctx.getItems().get(i);
                    break;
                }
            }

            Bounds b = pane.getLayoutBounds();

            if (innerNode != null) {
                b = innerNode.getBoundsInParent();
                if ((targetPane instanceof VBox) || (targetPane instanceof Accordion)) {
                    Bounds b1 = innerNode.localToScreen(innerNode.getBoundsInLocal());
                    r.setWidth(targetPane.getWidth());
                    r.setX(b.getMinX());
                    r.setHeight(b.getHeight() / 2);

                    if (y < b1.getMinY() + b.getHeight() / 2) {
                        r.setY(b.getMinY());
                    } else {
                        r.setY(b.getMinY() + b.getHeight() / 2);
                    }

                } else if (targetPane instanceof HBox) {
                    Bounds b1 = innerNode.localToScreen(innerNode.getBoundsInLocal());
                    r.setHeight(targetPane.getHeight());
                    r.setX(b.getMinX());
                    r.setWidth(b.getWidth() / 2);

                    if (x < b1.getMinX() + b.getWidth() / 2) {
                        r.setX(b.getMinX());
                    } else {
                        r.setX(b.getMinX() + b.getWidth() / 2);
                    }
                } else {
                    r.setWidth(b.getWidth());
                    r.setHeight(b.getHeight());

                    r.setX(b.getMinX());
                    r.setY(b.getMinY());
                }
            }
        }
     */
}
