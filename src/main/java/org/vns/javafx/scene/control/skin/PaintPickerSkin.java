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
package org.vns.javafx.scene.control.skin;

import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.scene.control.paint.ColorPane;
import org.vns.javafx.scene.control.ContentComboBox;
import org.vns.javafx.scene.control.paint.PaintPane;
import org.vns.javafx.scene.control.PaintPicker;

/**
 *
 * @author Nastia
 */
public class PaintPickerSkin  extends SkinBase<PaintPicker> {

        ContentComboBox<Paint> combo;
        PaintPicker control;
        PaintPane paintPane;
        
        public PaintPickerSkin(PaintPicker control, ContentComboBox<Paint> combo, PaintPane paintPane) {
            super(control);
            this.control = control;
            //control.combo = new ContentComboBox();
            this.combo = combo;
            this.paintPane = paintPane;
            init();
          
            getChildren().add(combo);
        }

     
        private void init() {

            String lbText = "";
            Paint paint = control.getPaint();
            if ( paint == null ) {
                paint = Color.TRANSPARENT;
            }
            if (control.getPaint() instanceof Color) {
                if (ColorPane.COLORS.get((Color) paint) != null) {
                    lbText = ColorPane.COLORS.get((Color) paint).toUpperCase();
                } else {
                    lbText = ((Color) paint).toString();
                }
            } else if (paint instanceof LinearGradient) {
                lbText = "Linear Gradient";
            } else if (paint instanceof RadialGradient) {
                lbText = "Radial Gradient";
            }

            Label displayNode = new Label(lbText);

            combo.setDisplayNode(displayNode);

            displayNode.getStyleClass().add("display-node");
            Rectangle inner = new Rectangle(16, 12);

            inner.fillProperty().bindBidirectional(combo.valueProperty());
            inner.getStyleClass().clear();
            inner.getStyleClass().add("color-rect");
            displayNode.setGraphic(inner);
            //combo.setContent(paintPane);

            paintPane.setMouseTransparent(false);

            control.setPaint(paint);
            
            combo.valueProperty().bindBidirectional(paintPane.chosenPaintProperty());
            combo.setValue(paint);
            

            paintPane.chosenPaintProperty().addListener((v, ov, nv) -> {
                inner.setFill(nv);
                updateText(nv);
                //setValue(nv);
            });

            combo.valueProperty().addListener((v, ov, nv) -> {

       
                updateText(nv);
                System.err.println("PainPicker VALUE = " + combo.getValue());
                //setPaint(nv);

            });

        }

        private void updateText(Paint paint) {
            String text = "TRANSPARENT";
            if (paint != null) {
                if (paint instanceof LinearGradient) {
                    text = "Linear Gradient";
                } else if (paint instanceof RadialGradient) {
                    text = "Radial Gradient";
                } else if (paint instanceof Color) {
                    text = ColorPane.COLORS.get((Color) paint);
                    if (text == null) {
                        text = ((Color) paint).toString();
                    } else {
                        text = text.toUpperCase();
                    }
                }
            }

            ((Labeled) combo.getDisplayNode()).setText(text);
        }

  
        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.minWidth(height);
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.minHeight(width);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.prefWidth(height) + leftInset + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.prefHeight(width) + topInset + bottomInset;
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
            combo.resizeRelocate(x, y, w, h);
        }
    }//skin