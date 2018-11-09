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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.vns.javafx.MainLookup;
import org.vns.javafx.scene.control.paint.skin.StopPaneSkin;

/**
 *
 * @author Nastia
 */
public class StopPane extends Control {

    private final ObjectProperty<Stop[]> stops = new SimpleObjectProperty<>();
    private final ObjectProperty<Paint> currentPaint = new SimpleObjectProperty<>();

    private final Pane content;

    private final ColorChooserPane colorChooserPane;

    public StopPane(ColorChooserPane colorChooserPane) {
        this.colorChooserPane = colorChooserPane;
        this.content = new StackPane();
        init();
    }

    private void init() {
        getStyleClass().add("stop-pane");
        //content.getStyleClass().add("content");
    }

    public ObjectProperty<Paint> currentPaintProperty() {
        return currentPaint;
    }

    public Paint getCurrentPaint() {
        return currentPaint.get();
    }

    public void setCurrentPaint(Paint paint) {
        this.currentPaint.set(paint);
    }

    public ColorChooserPane getColorChooserPane() {
        return colorChooserPane;
    }

    public Color getChosenColor() {
        return (Color) colorChooserPane.getColorPane().getChosenColor();
    }

    public ObjectProperty<Stop[]> stopsProperty() {
        return stops;
    }

    public Stop[] getStops() {
        return stops.get();
    }

    public void setStops(Stop[] stops) {
        this.stops.set(stops);
    }

    protected Pane getContent() {
        return content;
    }

    @Override
    public String getUserAgentStylesheet() {
        return ColorPane.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new StopPaneSkin(this);
    }


}//StopPane
