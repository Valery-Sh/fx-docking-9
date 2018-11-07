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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 *
 * @author Nastia
 */
public class RadialGradientPane extends GradientPane {

    private final DoubleProperty radius = new SimpleDoubleProperty(0.5);
    private final DoubleProperty focusDistance = new SimpleDoubleProperty(0);
    private final DoubleProperty focusAngle = new SimpleDoubleProperty(180);

    public RadialGradientPane() {
        this(null);
    }

    public RadialGradientPane(RadialGradient currentPaint) {
        super(currentPaint);
        init();
    }

    /*    public RadialGradientPane(ColorChooserPane colorChooserPane) {
        super(colorChooserPane);
        init();
    }
     */
    private void init() {
        getStyleClass().add("radial-gradient");
        if (getCurrentPaint() == null) {
            setCurrentPaint(createDefaultGradient());
        }
        updateValues();

    }

    public void currentPaintChanged(Paint paint) {
        if (!(paint instanceof RadialGradient)) {
            return;
        }
        setCurrentPaint(paint);
        updateValues();

    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public double getRadius() {
        return radius.get();
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public DoubleProperty focusDistanceProperty() {
        return focusDistance;
    }

    public double getFocusDistance() {
        return focusDistance.get();
    }

    public void setFocusDistance(double focusDistance) {
        this.focusDistance.set(focusDistance);
    }

    public DoubleProperty focusAngleProperty() {
        return focusAngle;
    }

    public double getFocusAngle() {
        return focusAngle.get();
    }

    public void setFocusAngle(double focusAngle) {
        this.focusAngle.set(focusAngle);
    }

    @Override
    protected Paint createDefaultGradient() {
        return new RadialGradient(getFocusAngle(), getFocusDistance(),
                getTopValue(), getLeftValue(),
                getRadius(),
                isProportional(),
                getCycleMethod(),
                getStops()
        );
    }
    @Override
    protected void updateValues() {
        RadialGradient g = (RadialGradient) getCurrentPaint();
        setTopValue(g.getCenterX());
        setLeftValue(g.getCenterY());
        setProportional(g.isProportional());
        setCycleMethod(g.getCycleMethod());
        setFocusAngle(g.getFocusAngle());
        setFocusDistance(g.getFocusDistance());
        setRadius(g.getRadius());
        Stop[] stops = g.getStops().toArray(new Stop[0]);
        setStops(stops);
        
        setChosenPaint(new RadialGradient(g.getFocusAngle(),g.getFocusDistance(),
                g.getCenterX(), g.getCenterY(), g.getRadius(),
                g.isProportional(),g.getCycleMethod(),g.getStops()));
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RadialGradientPaneSkin(this);
    }

    public static class RadialGradientPaneSkin extends GradientPaneSkin {

        private Slider radiusSlider;
        private Slider focusDistanceSlider;
        private Slider focusAngleSlider;
        private RadialGradientPane control;
        public RadialGradientPaneSkin(RadialGradientPane control) {
            super(control);
            this.control = control;
            init();
        }

        private void init() {
            GridPane propPane = getPropertiesPane();
            //
            // Add radius
            //
            Label title = new Label("radius");
            TextField radiusField = new TextField();
            Util.createTextFormatter(radiusField, 0, 1, 2);
            radiusSlider = new Slider(0, 1, 0.5);
            focusDistanceSlider = new Slider(-1, 1, 0);
            focusAngleSlider = new Slider(-180, 180, 0);
            /*            radiusSlider.setMaxWidth(50);
            focusDistanceSlider.setMaxWidth(50);
            focusAngleSlider.setMaxWidth(50);
             */
            //HBox hb = new HBox(radiusField,radiusSlider);
            propPane.add(title, 0, 2);
            //propPane.add(hb, 1, 2);
            propPane.add(radiusField, 1, 2);
            propPane.add(radiusSlider, 2, 2);
            GridPane.setHgrow(radiusSlider, Priority.ALWAYS);
            //
            // Add focusDistance
            //    
            title = new Label("focusDistance");
            TextField focusDistanceField = new TextField();
            Util.createTextFormatter(focusDistanceField, -1, 1, 2);
            propPane.add(title, 0, 3);
            //hb = new HBox(focusDistanceField,focusDistanceSlider);
            //propPane.add(hb, 1, 3);
            propPane.add(focusDistanceField, 1, 3);
            propPane.add(focusDistanceSlider, 2, 3);
            //
            // Add focusAngle
            //    
            title = new Label("focusAngle");
            TextField focusAngleField = new TextField();
            Util.createTextFormatter(focusAngleField, -180, 180, 2);
            propPane.add(title, 0, 4);
            //AnchorPane anchor  = new AnchorPane(focusAngleField,focusAngleSlider);
            //AnchorPane.setLeftAnchor(focusAngleField, 0d);
            //AnchorPane.setRightAnchor(focusAngleSlider, 0d);
            //propPane.add(anchor, 1, 4);

            propPane.add(focusAngleField, 1, 4);
            propPane.add(focusAngleSlider, 2, 4);
            //
            // Sliders
            //
            getRightSlider().setVisible(false);
            getRightSlider().setDisable(true);
            getBottomSlider().setVisible(false);
            getBottomSlider().setDisable(true);

            //
            // centerX
            //
            getTopSlider().setMin(0);
            getTopSlider().setMax(1);
            getTopSlider().setValue(0.5);

            Tooltip tooltip1 = new Tooltip();
            //tooltip1.setMinWidth(75);
            //tooltip1.setMinHeight(30);
            tooltip1.setOnShown(e -> {
                tooltip1.setText("centerX = " + getTopSlider().getValue());
            });
            getTopSlider().setTooltip(tooltip1);

            //
            // centerY
            //
            getLeftSlider().setMin(0);
            getLeftSlider().setMax(1);
            getLeftSlider().setValue(0.5);

            Tooltip tooltip2 = new Tooltip();
            tooltip2.setOnShowing(e -> {
                tooltip2.setText("centerY = " + getLeftSlider().getValue());
            });
            getLeftSlider().setTooltip(tooltip2);

            //
            // Bind TextFields
            //
            radiusField.textProperty().bindBidirectional(radiusSlider.valueProperty(), Util.doubleStringConverter(2));
            focusAngleField.textProperty().bindBidirectional(focusAngleSlider.valueProperty(), Util.doubleStringConverter(2));
            focusDistanceField.textProperty().bindBidirectional(focusDistanceSlider.valueProperty(), Util.doubleStringConverter(2));

            //radiusField.maxWidthProperty().bind(getCycleMethodBox().widthProperty());
            //focusAngleField.maxWidthProperty().bind(getCycleMethodBox().widthProperty());
            //focusDistanceField.maxWidthProperty().bind(getCycleMethodBox().widthProperty());
            
            //
            // ChangeListeners to update radialGradien
            //
            radiusSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            focusAngleSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            focusDistanceSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
        }

        @Override
        protected Paint createGradient() {
            RadialGradientPane rp = (RadialGradientPane) getSkinnable();
            return new RadialGradient(/*focusAngleSlider.getValue(), focusDistanceSlider.getValue(),
                    topSlider.getValue(), leftSlider.getValue(),
                    radiusSlider.getValue(),
*/                    
                    rp.getFocusAngle(), rp.getFocusDistance(),
                    rp.getTopValue(), rp.getLeftValue(),
                    rp.getRadius(),
                    rp.isProportional(),
                    rp.getCycleMethod(),
                    rp.getStops()
            );

        }
    }//skin GradientPane
}
