
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
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
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
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
//@DefaultProperty("content")
public class HueBar extends Control {

    private static final PseudoClass VERTICAL_PSEUDO_CLASS = PseudoClass.getPseudoClass("vertical");

    private final ColorPane colorPane;

    private Pane content;

    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);

    public HueBar() {
        this(new ColorPane());
    }

    public HueBar(ColorPane colorPane) {
        this.colorPane = colorPane;
        init();
    }

    private void init() {
        getStyleClass().add("hue-bar");
        content = createContent();
        content.getStyleClass().add("content");
        //content.setManaged(false);

        orientation.addListener((v, ov, nv) -> {
            pseudoClassStateChanged(VERTICAL_PSEUDO_CLASS, nv.equals(Orientation.VERTICAL) ? true : false);
        });
    }

    public ColorPane getColorPane() {
        return colorPane;
    }

    protected Pane createContent() {
        return new Pane();
    }

    public Pane getContent() {
        return content;
    }

    /**
     * Returns orientation for the HueBar.
     *
     * @return The orientation for the HueBar.
     */
    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    /**
     * Returns orientation for the HueBar.
     *
     * @return The orientation for the HueBar.
     */
    public Orientation getOrientation() {
        return orientation.get();
    }

    /**
     * This property controls how the HueBar should be displayed to the user.
     *
     * @param orientation the orientation to be set.
     */
    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    /*    public ReadOnlyDoubleProperty getCorrection() {
        return correction;
    }

    public void setCorrection(ReadOnlyDoubleProperty correction) {
        this.correction = correction;
    }
     */
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new HueBarSkin(this);
    }

    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    public static class HueBarSkin extends SkinBase<HueBar> {

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
            /*            EventHandler<MouseEvent> mouseHandler1 = event -> {
                double size;
                double pos;
                pos = event.getX();
                size = colorPane.getContent().getWidth(); //*db.getValue();
//            hueIndicator.setCenterY((content.getHeight() + 2) / 2);
                double f = size / content.getWidth();
                if (pos < 13) {
                    colorPane.setHue(clamp((pos * f) / size) * 360);
                } else {
                    colorPane.setHue(clamp((pos * f) / size) * 360);// + hueIndicator.getRadius() * 2);
                }
                //colorPane.setHue(clamp((pos*f) / size) * 360);
                colorPane.updateChosenColor();

            };
             */
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
    }////ColorPaneSkin
}//ColorPane
