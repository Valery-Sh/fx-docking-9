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

import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.vns.javafx.MainLookup;
import org.vns.javafx.scene.control.paint.skin.ColorChooserPaneSkin;

/**
 *
 * @author Nastia
 */
public class ColorChooserPane extends Control {
    
    private GridPane content;
    
    private ColorPane colorPane;
    private HueBar hueBar;

    public ColorChooserPane() {
        this(Color.TRANSPARENT);
    }

    public ColorChooserPane(Color currentColor) {
        content = new GridPane();
        init(currentColor);
    }

    private void init(Color currentColor) {
        getStyleClass().add("color-chooser");
        content.getStyleClass().add("content");
        colorPane = new ColorPane(currentColor);
        hueBar = new HueBar(colorPane);
        
    }
    public void setCurrentColor(Color color) {
        System.err.println("ColorChoosenPaint.setCurrentColor = " + color);
        colorPane.setCurrentColor(color);
    }
    public void currentPaintChanged(Paint paint) {
        if ( ! (paint instanceof Color)) {
            return;
        }
        setCurrentColor((Color) paint);
        colorPane.currentPaintChanged(paint);
    }
    public GridPane getContent() {
        return content;
    }

    public ColorPane getColorPane() {
        return colorPane;
    }

    public HueBar getHueBar() {
        return hueBar;
    }

    @Override
    public String getUserAgentStylesheet() {
        return ColorPane.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ColorChooserPaneSkin(this);
    }


}//ColorChooser
