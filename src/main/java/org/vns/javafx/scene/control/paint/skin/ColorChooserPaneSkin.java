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

import javafx.beans.binding.StringBinding;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.scene.control.paint.binding.DoubleBinder;
import org.vns.javafx.scene.control.paint.Util;
import org.vns.javafx.scene.control.paint.ColorChooserPane;
import org.vns.javafx.scene.control.paint.ColorPane;

/**
 *
 * @author Nastia
 */
public class ColorChooserPaneSkin  extends SkinBase<ColorChooserPane> {

        private GridPane grid;
        ColorChooserPane control;
        Rectangle shapeView = new Rectangle();

        TextField chosenHSLA;   // no alpha
        Slider alphaSlider;    // opacity
        TextField alphaValue;  // opacity value

        ColorPane colorPane;

        public ColorChooserPaneSkin(ColorChooserPane control) {
            super(control);
            this.control = control;
            colorPane = control.getColorPane();
            //grid = new GridPane();
            grid = control.getContent();
            
            grid.getStyleClass().add("grid-pane");
            
            chosenHSLA = new TextField();
            chosenHSLA.setEditable(false);
            chosenHSLA.getStyleClass().add("chosen-hsla");
            shapeView.getStyleClass().add("shape-view");
            shapeView.widthProperty().bind(chosenHSLA.prefWidthProperty());
            shapeView.heightProperty().bind(
                    shapeView.widthProperty()
            );

            shapeView.fillProperty().bind(
                    colorPane.chosenColorProperty()
            );
            chosenHSLA.textProperty().bind(stringBinding("hsla"));

            alphaSlider = new Slider(0, 1, 1);
            alphaSlider.getStyleClass().add("alpha-slider");
            alphaSlider.setValue(colorPane.getAlpha() / 100);
            alphaValue = new TextField();
            alphaValue.getStyleClass().add("alpha-value");

            Util.createTextFormatter(alphaValue, 0, 1, 2);

            alphaValue.textProperty().bindBidirectional(alphaSlider.valueProperty(), Util.doubleStringConverter(2));
            
            //colorPane.alphaProperty().bind(alphaSlider.valueProperty().multiply(100));
            
            DoubleBinder alphaBinder = new DoubleBinder(colorPane.alphaProperty(), alphaSlider.valueProperty());
            alphaBinder.change(colorPane.alphaProperty(), value -> {return value*100;});
            alphaBinder.change(alphaSlider.valueProperty(), value -> {return value / 100;});
                    
            TextField hsbHueField = new TextField();
            hsbHueField.setEditable(false);
            TextField hsbSatField = new TextField();
            hsbSatField.setEditable(false);
            TextField hsbBrightField = new TextField();
            hsbBrightField.setEditable(false);            
            
            TextField rgbRedField = new TextField();
            rgbRedField.setEditable(false);
            TextField rgbGreenField = new TextField();
            rgbGreenField.setEditable(false);
            TextField rgbBlueField = new TextField();
            rgbBlueField.setEditable(false);
            
            
            //grid.setHgap(hgap); // set in CSS
            //grid.setVgap(vgap); // set in CSS
            
            GridPane.setValignment(chosenHSLA, VPos.BOTTOM);
            
            GridPane.setValignment(shapeView, VPos.TOP);
            grid.add(colorPane, 0, 0, 1, 2);
            GridPane.setHgrow(colorPane, Priority.ALWAYS);
            grid.add(shapeView, 1, 0);
            grid.add(chosenHSLA, 1, 1);
            grid.add(control.getHueBar(), 0, 2, 2, 1);
            grid.add(alphaSlider, 0, 3);
            grid.add(alphaValue, 1, 3);

            GridPane hsbGrid = new GridPane();
            hsbGrid.getStyleClass().add("hsb-grid");
            hsbGrid.setHgap(2);
            Label hsbTitle = new Label("hsb");
            hsbGrid.add(hsbTitle, 0, 0, 3,1);
            GridPane.setHalignment(hsbTitle, HPos.CENTER);
            hsbGrid.add(hsbHueField, 0, 1);
            hsbGrid.add(hsbSatField, 1, 1);
            hsbGrid.add(hsbBrightField, 2, 1);
            
            
            GridPane rgbGrid = new GridPane();
            rgbGrid.getStyleClass().add("rgb-grid");
            rgbGrid.setHgap(2);
            Label rgbTitle = new Label("rgb");
            rgbGrid.add(rgbTitle, 0, 0, 3,1);
            GridPane.setHalignment(rgbTitle, HPos.CENTER);
            rgbGrid.add(rgbRedField, 0, 1);
            rgbGrid.add(rgbGreenField, 1, 1);
            rgbGrid.add(rgbBlueField, 2, 1);
            
            AnchorPane anchor = new AnchorPane(hsbGrid,rgbGrid); 
            AnchorPane.setRightAnchor(rgbGrid, 0d);
            AnchorPane.setLeftAnchor(hsbGrid, 0d);
            grid.add(anchor,0,4,2,1);
            
            rgbRedField.textProperty().bind(stringBinding("red"));
            rgbGreenField.textProperty().bind(stringBinding("green"));
            rgbBlueField.textProperty().bind(stringBinding("blue"));
            
            hsbHueField.textProperty().bind(stringBinding("hue"));
            hsbSatField.textProperty().bind(stringBinding("saturation"));
            hsbBrightField.textProperty().bind(stringBinding("brightness"));
            
            getChildren().add(grid);
            

        }

        private StringBinding stringBinding(String format) {

            StringBinding bnd = new StringBinding() {
                {
                    bind(shapeView.fillProperty());
                }

                @Override
                protected String computeValue() {
                    String retval = shapeView.getFill().toString().replace("0x", "#");
                    Double dv;
                    
                    switch (format) {
                        case "rgb":
                            Color c = (Color) shapeView.getFill();
                            retval = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
                            break;
                        case "red":
                            dv = ((Color) shapeView.getFill()).getRed()*255;
                            retval = Long.toString(dv.longValue());
                            break;
                        case "green":
                            dv = ((Color) shapeView.getFill()).getGreen()*255;
                            retval = Long.toString(dv.longValue());
                            break;
                        case "blue":
                            dv = ((Color) shapeView.getFill()).getBlue()*255;
                            retval = Long.toString(dv.longValue());
                            break;
                        case "hue":
                            dv = colorPane.getHue();
                            retval = Long.toString(dv.longValue());
                            break;
                        case "saturation":
                            retval = Util.doubleStringConverter(2).toString(((Color) shapeView.getFill()).getSaturation());
                            break;
                        case "brightness":
                            retval = Util.doubleStringConverter(2).toString(((Color) shapeView.getFill()).getBrightness());
                            break;
                    }//switch
                    return retval;
                }
            };

            return bnd;
        }
    
}
