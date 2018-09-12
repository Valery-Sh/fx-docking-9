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
package org.vns.javafx.dock.api.designer.bean.editor;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
public class SliderPropertyEditor extends Control implements PropertyEditor<Number> {

    private DecimalPropertyEditor textField;
    private Slider slider;
    private Property<Number> boundValue;
    
    private final DoubleProperty sliderBoundValue = new SimpleDoubleProperty();
    private final ChangeListener<? super Number>  sliderBoundValueListener  = (v,ov,nv) -> {
         boundValue.setValue(nv);
    };      
    public SliderPropertyEditor() {
        this(0,1,1);
    }

    public SliderPropertyEditor(double min, double max, int value) {
        //textField = new DecimalPropertyEditor(min, max,2);
        textField = new DecimalPropertyEditor(Double.MIN_VALUE, Double.MAX_VALUE,value);
        textField.getStringTransformers().add(src -> {
            String retval = src;
            src = src.trim();
            Double dv;
            if (src.isEmpty() || src.equals(".")) {
                dv = min;
            } else {
                dv = Double.valueOf(src);
            }
            if (dv.longValue() == dv.doubleValue()) {
                return String.valueOf(dv.longValue());
            }
            return textField.stringOf(dv);
        });       
        //textField.setScale(2, RoundingMode.HALF_UP);
        //textField.setR(2, RoundingMode.HALF_UP);
        sliderBoundValue.set(value);
        slider = new Slider(min, max, value);
        init();
    }

    private void init() {
        getStyleClass().add("slider-editor");
        slider.disableProperty().addListener((v, ov, nv) -> {
            textField.setEditable(!nv);
        });
        textField.disableProperty().addListener((v, ov, nv) -> {
            slider.setDisable(!nv);
        });
    }
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
    
    public DecimalPropertyEditor getTextField() {
        return textField;
    }

    public Slider getSlider() {
        return slider;
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new SliderEditorSkin(this);
    }

    @Override
    public void bind(Property<Number> property) {
        textField.setEditable(false);
        setEditable(false);

        property.addListener((v, ov, nv) -> {
            if (property.getValue().doubleValue() < slider.getMin()) {
                property.setValue(slider.getMin());
            } else if (property.getValue().doubleValue() > slider.getMax()) {
                property.setValue(slider.getMax());
            }
        });
        
        textField.bind(property);
        slider.valueProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<Number> property) {
        
        boundValue = property;
        textField.setEditable(true);
        
        property.addListener((v, ov, nv) -> {
            double dv = (double)nv;
            if (property.getValue().doubleValue() < slider.getMin()) {
                dv = slider.getMin();
            } else if (property.getValue().doubleValue() > slider.getMax()) {
                dv = slider.getMax();
            }
            sliderBoundValue.removeListener(sliderBoundValueListener);
            sliderBoundValue.set(dv);
            sliderBoundValue.addListener(sliderBoundValueListener);
            
        });
        sliderBoundValue.addListener(sliderBoundValueListener);
        textField.setRealTimeBinding(true);
        textField.bindBidirectional(property);
        //property.bindBidirectional(slider.valueProperty());
        sliderBoundValue.bindBidirectional(slider.valueProperty());
        
    }

    @Override
    public void unbind() {
        textField.unbind();
        slider.valueProperty().unbind();
    }

    @Override
    public boolean isEditable() {
        return getTextField().isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        getTextField().setEditable(editable);
    }

    @Override
    public boolean isBound() {
        return slider.valueProperty().isBound() || textField.isBound();
    }

    public static class SliderEditorSkin extends SkinBase<SliderPropertyEditor> {

        private GridPane grid;

        public SliderEditorSkin(SliderPropertyEditor control) {
            super(control);
            grid = new GridPane();
            grid.add(getSkinnable().getTextField(), 0, 0);
            grid.add(getSkinnable().getSlider(), 1, 0);
            Platform.runLater(() -> {
                ColumnConstraints column1 = new ColumnConstraints(getTextFieldWidth());
                ColumnConstraints column2 = new ColumnConstraints(60, 120, Double.MAX_VALUE);
                column2.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().addAll(column1, column2);
            });
            grid.setHgap(15);
            getChildren().add(grid);
        }

        private double getTextFieldWidth() {
            Text tx = new Text("99999");
            tx.setFont(getSkinnable().getTextField().getFont());
            return tx.getLayoutBounds().getWidth()
                    + getSkinnable().getTextField().getInsets().getLeft()
                    + getSkinnable().getTextField().getInsets().getRight();

        }
    }// Skin

}//SliderEditor
