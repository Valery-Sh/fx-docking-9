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
package org.vns.javafx.dock.api.dragging.view;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery Shyskin
 */
public class ResizeShape extends Control {

    private Class<?> shapeClass;
    private final DoubleProperty centerX = new SimpleDoubleProperty(50);
    private final DoubleProperty centerY = new SimpleDoubleProperty(0);

    public ResizeShape() {
        this(Circle.class);
    }

    public ResizeShape(Class<?> shapeClass) {
        this.shapeClass = shapeClass;
        if (shapeClass == null) {
            this.shapeClass = Circle.class;
        }
        init();
    }

    private void init() {
        getStyleClass().add("shape-control");
        if (Rectangle.class.isAssignableFrom(getShapeClass())) {
            getStyleClass().add("rectangle-shape-control");
        } else if (Circle.class.isAssignableFrom(getShapeClass())) {
            getStyleClass().add("circle-shape-control");
        }
        

        setManaged(false);
        //centerXProperty().bin
    }

    public Class<?> getShapeClass() {
        return shapeClass;
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public double getCenterX() {
        return centerX.get();
    }

    public void setCenterX(double centerX) {
        this.centerX.set(centerX);
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    public double getCenterY() {
        return centerY.get();
    }

    public void setCenterY(double centerY) {
        this.centerY.set(centerY);
    }
    public static boolean isResizeShape(Shape shape) {
        if ( shape.getParent() instanceof ResizeShape ) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new ResizeShapeSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public static class ResizeShapeSkin extends SkinBase<ResizeShape> {

        ResizeShape ctrl;
        Shape shape;

        public ResizeShapeSkin(ResizeShape control) {
            super(control);
            this.ctrl = control;

            try {
                shape = (Shape) ctrl.getShapeClass().newInstance();
                shape.getStyleClass().add("resize-shape");
                bind();
                getChildren().add(shape);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ResizeShape.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        private void bind() {
            if (Circle.class.isAssignableFrom(ctrl.getShapeClass())) {
                bindCircle();
            } else if (Rectangle.class.isAssignableFrom(ctrl.getShapeClass())) {
                bindRectangle();
            }
        }

        private void bindCircle() {
            Circle c = (Circle) shape;
            shape.getStyleClass().add("circle");
            c.radiusProperty().bind(ctrl.prefWidthProperty().divide(2));
            ctrl.layoutXProperty().bind(ctrl.centerXProperty());
            ctrl.layoutYProperty().bind(ctrl.centerYProperty());
        }

        private void bindRectangle() {
            Rectangle r = (Rectangle) shape;
            shape.getStyleClass().add("rectangle");
            r.heightProperty().bind(ctrl.prefHeightProperty());
            r.widthProperty().bind(ctrl.prefWidthProperty());

            ctrl.layoutXProperty().bind(ctrl.centerXProperty().subtract(r.widthProperty().divide(2)));
            ctrl.layoutYProperty().bind(ctrl.centerYProperty().subtract(r.heightProperty().divide(2)));

        }

        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return shape.minWidth(height);
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return shape.minHeight(width);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return shape.prefWidth(height) + leftInset + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return shape.prefHeight(width) + topInset + bottomInset;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            //shape.resizeRelocate(x,y ,w, h);
            shape.resize(w, h);
        }
    }//skin
}
