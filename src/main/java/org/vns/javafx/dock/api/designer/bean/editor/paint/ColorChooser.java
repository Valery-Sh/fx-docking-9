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

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.beans.binding.StringBinding;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Nastia
 */
public class ColorChooser extends Control {
    
    private StackPane content;
    
    private ColorPane colorPane;
    private HueBar hueBar;

    public ColorChooser() {
        this(Color.TRANSPARENT);
    }

    public ColorChooser(Color currentColor) {
        content = new StackPane();
        init(currentColor);
    }

    private void init(Color currentColor) {
        getStyleClass().add("color-chooser");
        content.getStyleClass().add("content");
        colorPane = new ColorPane(currentColor);
        hueBar = new HueBar(colorPane);
    }

    public StackPane getContent() {
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
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ColorChooserSkin(this);
    }

    public static class ColorChooserSkin extends SkinBase<ColorChooser> {

        private GridPane grid;
        ColorChooser control;
        Rectangle shapeView = new Rectangle();

        TextField chosenHSLA;   // no alpha
        Slider alphaSlider;    // opacity
        TextField alphaValue;  // opacity value

        ColorPane colorPane;
        double hgap = 5;
        double vgap = 3;

        public ColorChooserSkin(ColorChooser control) {
            super(control);
            this.control = control;
            colorPane = control.getColorPane();
            grid = new GridPane();
            chosenHSLA = new TextField();
            chosenHSLA.setEditable(false);
            chosenHSLA.getStyleClass().add("chosen-hsla");
            shapeView.getStyleClass().add("shape-view");
            shapeView.widthProperty().bind(chosenHSLA.prefWidthProperty());
            shapeView.heightProperty().bind(
                    //colorPane.prefHeightProperty().subtract(chosenHSLA.prefHeightProperty().add(vgap))
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

            createTextFormatter(alphaValue, 0, 1, 2);

            alphaValue.textProperty().bindBidirectional(alphaSlider.valueProperty(), doubleStringConverter(2));
            colorPane.alphaProperty().bind(alphaSlider.valueProperty().multiply(100));
            
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
            
            
            grid.setHgap(hgap);
            grid.setVgap(vgap);
            GridPane.setValignment(chosenHSLA, VPos.BOTTOM);
            //GridPane.setFillHeight(shapeView,true);
            GridPane.setValignment(shapeView, VPos.TOP);
            grid.add(colorPane, 0, 0, 1, 2);
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
            //grid.add(hsbGrid,0,4);
            
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
            control.getContent().getChildren().add(grid);
            getChildren().add(control.getContent());
            

        }

        private StringBinding stringBinding(String format) {

            StringBinding bnd = new StringBinding() {
                {
                    //               bind(colorPane.chosenColorProperty());
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
//                            retval = Double.toString(((Color) shapeView.getFill()).getGreen());
                            dv = ((Color) shapeView.getFill()).getGreen()*255;
                            retval = Long.toString(dv.longValue());
                            
                            break;
                        case "blue":
                            //retval = Double.toString(((Color) shapeView.getFill()).getBlue());
                            dv = ((Color) shapeView.getFill()).getBlue()*255;
                            retval = Long.toString(dv.longValue());
                            
                            break;
                        case "hue":
                            dv = colorPane.getHue();
/*                            String s = Double.toString(dv);
                            int idx = s.indexOf(".");
                            if ( idx > 0 ) {
                                retval = s.substring(0,idx);
                            } else {
                                retval = Long.toString(dv.longValue());
                            }
*/
                            retval = Long.toString(dv.longValue());
                            break;
                        case "saturation":
                            retval = doubleStringConverter(2).toString(((Color) shapeView.getFill()).getSaturation());
                            //retval = Double.toString(((Color) shapeView.getFill()).getSaturation());
                            break;
                        case "brightness":
                            retval = doubleStringConverter(2).toString(((Color) shapeView.getFill()).getBrightness());
                            //retval = Double.toString(((Color) shapeView.getFill()).getBrightness());
                            break;

                    }//switch
                    return retval;
                }
            };

            return bnd;
        }

        private StringConverter<Number> doubleStringConverter(final int scale) {
            DoubleStringConverter ds = new DoubleStringConverter();
            return new StringConverter<Number>() {
                @Override
                public String toString(Number value) {
                    Double dv = (Double) value;
                    if (value == null) {
                        dv = 0d;
                    }
                    return String.format("%1$,." + scale + "f", dv);
                }

                @Override
                public Double fromString(String string) {
                    if (string == null) {
                        string = "";
                    }
                    return ds.fromString(string);
                }

            };

        }

        private TextFormatter createTextFormatter(final TextField txtField, double minValue, double maxValue, int scale) {
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String str = change.getControlNewText();

                if (str.isEmpty()) {// || Pattern.matches("[+-]?\\d+\\.?(\\d+)?", txtField.getText())) {
                    return change;
                }
                if (validate(str, minValue, maxValue, scale)) {
                    return change;
                }
                return null;
            };
            TextFormatter<Double> f = new TextFormatter(doubleStringConverter(scale), minValue, filter);
            txtField.setTextFormatter(f);
            return f;
        }

        public boolean validate(String item, double minValue, double maxValue, int scale) {
            String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
            if (scale == 0) {
                regExp = "([+-]?)|([+-]?\\d+)";
            } else if (scale > 0) {
                regExp = "([+-]?)|([+-]?\\d+\\.?(\\d{0," + scale + "})?)";
            }
            boolean retval = item.trim().isEmpty();

            if (!retval) {
                retval = Pattern.matches(regExp, item);
            }
            if (retval) {
                Double dv = 0d;
                if (!item.trim().isEmpty() && !item.trim().equals("-") && !item.trim().equals("+")) {
                    dv = Double.valueOf(item);
                }
                retval = dv >= minValue && dv <= maxValue;
            }
            return retval;
        }

        public boolean validate(String item, int scale) {
            return validate(item, -Double.MAX_VALUE, Double.MAX_VALUE, scale);
        }

    }//ColorChooserSkin
}//ColorChooser
