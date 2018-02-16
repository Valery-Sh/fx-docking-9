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
package org.vns.javafx.dock.api.dragging.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

public class NodeLayout {

    private final Region node;
    private final DoubleProperty width = new SimpleDoubleProperty(-1);
    private final DoubleProperty height = new SimpleDoubleProperty(-1);
    private final DoubleProperty minWidth = new SimpleDoubleProperty(-1);
    private final DoubleProperty minHeight = new SimpleDoubleProperty(-1);
    private final DoubleProperty maxWidth = new SimpleDoubleProperty(-1);
    private final DoubleProperty maxHeight = new SimpleDoubleProperty(-1);
    private final DoubleProperty prefWidth = new SimpleDoubleProperty(-1);
    private final DoubleProperty prefHeight = new SimpleDoubleProperty(-1);

    public NodeLayout(Region node) {
        this.node = node;
        init();
    }

    private void init() {
        width.bind(node.widthProperty());
        minWidth.bind(node.minWidthProperty());
        maxWidth.bind(node.maxWidthProperty());
        prefWidth.bind(node.prefWidthProperty());

        height.bind(node.heightProperty());
        minHeight.bind(node.minHeightProperty());
        maxHeight.bind(node.maxHeightProperty());
        prefHeight.bind(node.maxHeightProperty());

    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }
/////

    public DoubleProperty minWidthProperty() {
        return minWidth;
    }

    public double getMinWidth() {
        return minWidth.get();
    }

    public void setMinWidth(double minWidth) {
        this.minWidth.set(minWidth);
    }

    public DoubleProperty minHeightProperty() {
        return minHeight;
    }

    public double getMinHeight() {
        return minHeight.get();
    }

    public void setMinHeight(double minHeight) {
        this.minHeight.set(minHeight);
    }

///
    public DoubleProperty maxWidthProperty() {
        return maxWidth;
    }

    public double getMaxWidth() {
        return maxWidth.get();
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth.set(maxWidth);
    }

    public DoubleProperty maxHeightProperty() {
        return maxHeight;
    }

    public double getMaxHeight() {
        return maxHeight.get();
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight.set(maxHeight);
    }

    public DoubleProperty prefWidthProperty() {
        return prefWidth;
    }

    public double getPrefWidth() {
        return prefWidth.get();
    }

    public void setPrefWidth(double prefWidth) {
        this.prefWidth.set(prefWidth);
    }

    public DoubleProperty prefHeightProperty() {
        return prefHeight;
    }

    public double getPrefHeight() {
        return prefHeight.get();
    }

    public void setPrefHeight(double prefHeight) {
        this.prefHeight.set(prefHeight);
    }

    public double prefWidth() {
        return prefDimension().getWidth();
    }

    public double minWidth() {
        return minDimension().getWidth();
    }

    public double maxWidth() {
        return maxDimension().getWidth();
    }

    public double prefHeight() {
        return prefDimension().getHeight();
    }

    public double maxHeight() {
        return maxDimension().getHeight();
    }

    public double minHeight() {
        return minDimension().getHeight();
    }

    public Dimension2D prefDimension() {
        Orientation contentBias = node.getContentBias();
        double prefW;
        double prefH;

        if (null == contentBias) {
            prefW = node.prefWidth(-1);
            prefH = node.prefHeight(-1);
        } else {
            switch (contentBias) {
                case HORIZONTAL:
                    prefW = node.prefWidth(-1);
                    prefH = node.prefHeight(prefW);
                    break;
                case VERTICAL:
                    prefH = node.prefHeight(-1);
                    prefW = node.prefWidth(prefH);
                    break;
                default:
                    prefW = node.prefWidth(-1);
                    prefH = node.prefHeight(-1);
                    break;
            }
        }
        return new Dimension2D(prefW, prefH);
    }

    public Dimension2D minDimension() {
        Orientation contentBias = node.getContentBias();
        double w;
        double h;

        if (null == contentBias) {
            w = node.minWidth(-1);
            h = node.minHeight(-1);
        } else {
            switch (contentBias) {
                case HORIZONTAL:
                    w = node.minWidth(-1);
                    h = node.minHeight(w);
                    break;
                case VERTICAL:
                    h = node.minHeight(-1);
                    w = node.minWidth(h);
                    break;
                default:
                    w = node.minWidth(-1);
                    h = node.minHeight(-1);
                    break;
            }
        }
        return new Dimension2D(w, h);

    }

    public Dimension2D maxDimension() {
        Orientation contentBias = node.getContentBias();
        double w;
        double h;

        if (null == contentBias) {
            w = node.maxWidth(-1);
            h = node.maxHeight(-1);
        } else {
            switch (contentBias) {
                case HORIZONTAL:
                    w = node.maxWidth(-1);
                    h = node.maxHeight(w);
                    break;
                case VERTICAL:
                    h = node.maxHeight(-1);
                    w = node.maxWidth(h);
                    break;
                default:
                    w = node.maxWidth(-1);
                    h = node.maxHeight(-1);
                    break;
            }
        }
        return new Dimension2D(w, h);

    }

    public double actualMaxWidth() {
        if (node.getMaxWidth() < 0) {
            return maxWidth();
        } else {
            return node.getMaxWidth();
        }
    }

    public double actualMinWidth() {
        if (node.getMinWidth() < 0) {
            return minWidth();
        } else {
            return node.getMinWidth();
        }
    }

}//class NodeLayout
