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
package org.vns.javafx.scene.control.paint;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.vns.javafx.scene.control.paint.skin.RadialGradientPaneSkin;

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
    public void updateValues() {
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

}
