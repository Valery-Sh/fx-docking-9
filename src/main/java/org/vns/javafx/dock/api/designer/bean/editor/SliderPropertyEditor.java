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
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Skin;
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
public class SliderPropertyEditor extends AbstractPropertyEditor<Number> {

    private DecimalPropertyEditor decimalEditor;

    private Slider slider;

    private final DoubleProperty sliderBoundValue = new SimpleDoubleProperty();

    private final ChangeListener<? super Number> sliderBoundValueListener = (v, ov, nv) -> {
        ((Property) getBoundProperty()).setValue(nv);
    };

    public SliderPropertyEditor() {
        this(0, 1, 1);
    }

    public SliderPropertyEditor(double min, double max, int value) {
        this(null, min, max, value);
    }

    public SliderPropertyEditor(String name, double min, double max, int value) {
        super(name);
        decimalEditor = new DecimalPropertyEditor(min, max, 2);

        //decimalEditor = new DecimalPropertyEditor(Double.MIN_VALUE, Double.MAX_VALUE, value);
        decimalEditor.getTextField().getStringTransformers().add(src -> {
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
            return decimalEditor.stringOf(dv);
        });
        //textField.setScale(2, RoundingMode.HALF_UP);
        //textField.setR(2, RoundingMode.HALF_UP);
        sliderBoundValue.set(value);
        slider = new Slider(min, max, value);
        //slider.
        init();
    }

    public StringTextField getTextField() {
        return decimalEditor.getTextField();
    }

    private void init() {
        getStyleClass().add("slider-editor");
        slider.disableProperty().addListener((v, ov, nv) -> {
            decimalEditor.setEditable(!nv);
        });
        decimalEditor.disableProperty().addListener((v, ov, nv) -> {
            slider.setDisable(!nv);
        });
        decimalEditor.bindBidirectional(slider.valueProperty());

    }

    @Override
    protected Node createEditorNode() {
        //super.createEditorNode();
        return new GridPane();
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public DecimalPropertyEditor getDecimalEditor() {
        return decimalEditor;
    }

    public Slider getSlider() {
        return slider;
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new SliderEditorSkin(this);
    }

    @Override
    public void bind(ReadOnlyProperty<Number> property) {
        decimalEditor.setEditable(false);
        unbind();
        setEditable(false);
        setBoundProperty(property);
        property.addListener((v, ov, nv) -> {
            if (property instanceof Property) {
                if (property.getValue().doubleValue() < slider.getMin()) {
                    ((Property) property).setValue(slider.getMin());
                } else if (property.getValue().doubleValue() > slider.getMax()) {
                    ((Property) property).setValue(slider.getMax());
                }
            }
        });

        decimalEditor.bind(property);
        slider.valueProperty().bind(property);
    }
    ChangeListener propertyListener = (v, ov, nv) -> {
        double dv = (double) nv;
        if ((Double) getBoundProperty().getValue() < slider.getMin()) {
            dv = slider.getMin();
        } else if ((Double) getBoundProperty().getValue() > slider.getMax()) {
            dv = slider.getMax();
        }
        sliderBoundValue.removeListener(sliderBoundValueListener);
        sliderBoundValue.set(dv);
        sliderBoundValue.addListener(sliderBoundValueListener);

    };

    @Override
    public void bindBidirectional(Property<Number> property) {

        //boundValue = property;
        unbind();
        decimalEditor.getTextField().setEditable(true);
        setBoundProperty(property);
        property.addListener(propertyListener);
        sliderBoundValue.addListener(sliderBoundValueListener);
//        decimalEditor.setRealTimeBinding(true);
//        decimalEditor.bindBidirectional(property);
        slider.valueProperty().bindBidirectional(property);
        decimalEditor.bindBidirectional(slider.valueProperty());

//        sliderBoundValue.bindBidirectional(slider.valueProperty());
        //slider.valueProperty().bindBidirectional(property);
    }

    @Override
    public void unbind() {
        sliderBoundValue.removeListener(sliderBoundValueListener);
        slider.valueProperty().unbind();
        decimalEditor.unbind();
        
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            getBoundProperty().removeListener(propertyListener);
            decimalEditor.getTextField().textProperty().unbindBidirectional((Property) getBoundProperty());
            decimalEditor.getTextField().lastValidTextProperty().unbindBidirectional((Property) getBoundProperty());
            slider.valueProperty().unbindBidirectional((Property) getBoundProperty());

        }
        setBoundProperty(null);
    }

    @Override
    public boolean isEditable() {
        return getDecimalEditor().isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        getDecimalEditor().setEditable(editable);
    }

    @Override
    public boolean isBound() {
        return slider.valueProperty().isBound() || decimalEditor.isBound() || getBoundProperty() != null;
    }

    public static class SliderEditorSkin extends BaseEditorSkin {

        private GridPane grid;
        private SliderPropertyEditor control;

        public SliderEditorSkin(SliderPropertyEditor control) {
            super(control);
            this.control = control;
            grid = (GridPane) getEditorNode();
            grid.add(control.getTextField(), 0, 0);
            grid.add(control.getSlider(), 1, 0);
            Platform.runLater(() -> {
                ColumnConstraints column1 = new ColumnConstraints(getTextFieldWidth());
                ColumnConstraints column2 = new ColumnConstraints(60, 120, Double.MAX_VALUE);
                column2.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().addAll(column1, column2);
            });
            grid.setHgap(15);
            //getChildren().add(grid);
        }

        private double getTextFieldWidth() {
            Text tx = new Text("99999");
            tx.setFont(control.getTextField().getFont());
            return tx.getLayoutBounds().getWidth()
                    + control.getTextField().getInsets().getLeft()
                    + control.getTextField().getInsets().getRight();

        }
    }// Skin

}//SliderEditor
