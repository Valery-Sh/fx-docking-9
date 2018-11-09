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
package org.vns.javafx.scene.control.paint.skin;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.scene.control.paint.binding.DoubleBinder;
import org.vns.javafx.scene.control.paint.ColorPane;
import org.vns.javafx.scene.control.paint.HueBar;
import static org.vns.javafx.scene.control.paint.HueBar.clamp;

/**
 *
 * @author Nastia
 */
public class HueBarSkin  extends SkinBase<HueBar> {

        private ColorPane colorPane;
        private final Pane content;
        private final Circle hueIndicator;

        public HueBarSkin(HueBar control) {
            super(control);
            colorPane = control.getColorPane();
            content = control.getContent();

            //hueIndicator = new Circle(6, Color.TRANSPARENT);
            hueIndicator = new Circle();
            hueIndicator.setFill(Color.TRANSPARENT);

            hueIndicator.setStroke(Color.WHITE);
            hueIndicator.setStrokeType(StrokeType.OUTSIDE);

            hueIndicator.setCenterY(0);
            hueIndicator.setCenterX(0);

            hueIndicator.setManaged(false);

            hueIndicator.setId("hue-bar-indicator");
            hueIndicator.getStyleClass().add("hue-indicator");

            hueIndicator.setMouseTransparent(true);
            hueIndicator.setCache(true);

            hueIndicator.radiusProperty().bind(
                    content.heightProperty().divide(2).subtract(hueIndicator.strokeWidthProperty().multiply(2))
            );
            /*            NumberBidirectionalBinding dbb = new NumberBidirectionalBinding(hueIndicator.centerXProperty(), colorPane.hueProperty());
                     hueIndicator.radiusProperty().addListener((v, ov, nv) -> {
                if ( dbb.isBound() ) {
                    dbb.unbind();
                }
                dbb.bindBidirectional((propChanging, value) -> {
                    Double retval;// = value;
                    double size = colorPane.getContent().getWidth() - 1;
                    double f = size / (content.getWidth() - 1);
                    double r = hueIndicator.getBoundsInParent().getWidth() / 2;
                    if (propChanging == 1) {
                        //
                        // centerX changing
                        //
                     
                        if (value <= 0) {
                            retval = r;
                        } else {
                            retval = (((value + r) / 360) * size) / f;
                        }
                    } else {
                        //
                        // colorPane hueProperty changing
                        //
                        retval = clamp(((value - r) * f) / size) * 360;
                    }
                    return retval;
                });

            });
             */
            DoubleBinder dbb = new DoubleBinder(hueIndicator.centerXProperty(), colorPane.hueProperty());
            hueIndicator.radiusProperty().addListener((v, ov, nv) -> {
                if (dbb.isBound()) {
                    dbb.unbind();
                }
                dbb.change(hueIndicator.centerXProperty(), value -> {
                    Double retval;// = value;
                    double size = colorPane.getContent().getWidth() - 1;
                    double f = size / (content.getWidth() - 1);
                    double r = hueIndicator.getBoundsInParent().getWidth() / 2;
                    //
                    // centerX changing
                    //
                    if (value <= 0) {
                        retval = r;
                    } else {
                        retval = (((value + r) / 360) * size) / f;
                    }
                    return retval;

                });
                dbb.change(colorPane.hueProperty(), value -> {
                    Double retval;// = value;
                    double size = colorPane.getContent().getWidth() - 1;
                    double f = size / (content.getWidth() - 1);
                    double r = hueIndicator.getBoundsInParent().getWidth() / 2;
                    //
                    // colorPane hueProperty changing
                    //
                    //return clamp(((value - r) * f) / size) * 360;

                    retval = clamp(((value - r) * f) / size) * 360;
                    return clamp(((value - r) * f) / size) * 360;

                });
            });

            hueIndicator.centerYProperty().bind(
                    content.heightProperty().add(2).divide(2)
            );

            EventHandler<MouseEvent> mouseHandler = event -> {
                double size;
                double pos;
                pos = event.getX();
                if (pos < 0) {
                    pos = 0;
                }

                size = colorPane.getContent().getBoundsInLocal().getWidth() - 1;
                double w = content.getWidth() - 1;

                if (pos > w) {
                    pos = w;
                }
                if (pos <= 0) {
                    pos = 0;
                }
                hueIndicator.setCenterX(pos + hueIndicator.getBoundsInParent().getWidth() / 2);
                colorPane.updateChosenColor();
            };
            content.setOnMouseDragged(mouseHandler);

            content.setOnMousePressed(mouseHandler);

            content.setBackground(
                    new Background(new BackgroundFill(createHueGradient(),
                            CornerRadii.EMPTY, Insets.EMPTY)));

            content.getChildren().setAll(hueIndicator);
            getChildren().add(content);
        }

        private static LinearGradient createHueGradient() {
            double offset;
            Stop[] stops = new Stop[255];
            for (int x = 0; x < 255; x++) {
                offset = (double) ((1.0 / 255) * x);
                int h = (int) ((x / 255.0) * 360);
                stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
            }
            return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
        }
    }//HueBarSkin
