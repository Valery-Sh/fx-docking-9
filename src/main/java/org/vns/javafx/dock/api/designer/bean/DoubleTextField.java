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
package org.vns.javafx.dock.api.designer.bean;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import org.vns.javafx.dock.api.designer.bean.PrimitivesTextField.NumberTextField;

/**
 *
 * @author Olga
 */
public class DoubleTextField extends NumberTextField<Double> {

    private DoubleStringConverter converter;
    
    public DoubleTextField() {
        this(null, null);
    }

    public DoubleTextField(Double minValue, Double maxValue) {
        this(0d, minValue, maxValue);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public DoubleTextField(Double defaultValue) {
        this(defaultValue, null, null);
    }

    public DoubleTextField(Double defaultValue, Double minValue, Double maxValue) {
        setDefaultValue(defaultValue);
        setMinValue(minValue);
        setMaxValue(maxValue);
        init();
    }

    @Override
    protected boolean isAcceptable(String txt) {
        if (txt == null) {
            return false;
        }
        if (txt.isEmpty() || "-".equals(txt)) {
            return true;
        }

        if (txt.matches(getPattern())) {
            if (getMinValue() == null && getMaxValue() == null) {
                return true;
            }
            if (getMinValue() != null && Double.parseDouble(txt) < getMinValue()) {
                return false;
            }
            if (getMaxValue() != null && Double.parseDouble(txt) > getMaxValue()) {
                return false;
            }
            return true;
        }
        return false;

    }

    public DoubleStringConverter getConverter() {
        return converter;
    }

    public void setConverter(DoubleStringConverter converter) {
        this.converter = converter;
    }

    protected String getPattern() {
        return "0|-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
    }

    private void init() {
        getStyleClass().add("double-text-field");
        double d = getDefaultValue() == null ? 0 : getDefaultValue();
        //DoubleStringConverter c = getConverter();
        if ( getConverter() == null ) {
            converter = new Converter(this, getDefaultValue());
        }
        valueProperty().addListener((v, ov, nv) -> {
            if (!((Double) nv == 0 && ("-".equals(getText()) || getText().isEmpty() || getText().endsWith(".")))) {
                setText(getConverter().toString((Double) nv));
            }
        });
        textProperty().addListener((v, ov, nv) -> {
            if (!"-".equals(nv) && !"".equals(nv) && !nv.endsWith(".")) {
                setValue(getConverter().fromString(nv));
            } else if ("".equals(nv) || "-".equals(nv)) {
                setValue(0);
            }
            //c.fromString(nv);
        });
        setTextFormatter(new TextFormatter<>(getConverter(), d, filter));
    }

    @Override
    protected Property initValueProperty() {
        return new SimpleDoubleProperty();
    }
    
    public static class Converter extends DoubleStringConverter {

        private final Double defaultValue;
        private final DoubleTextField textField;

        public Converter(DoubleTextField textField, Double defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString(Double dv) {
            if (dv == null && defaultValue == null) {
                return "";
            } else if (dv == null && defaultValue != null) {
                return "0";
            } else if (dv == 0d && defaultValue == null) {
                return "";
            } else if (dv == 0d) {
                textField.setValue(0d);
                return "0";
            } else {
                if (dv.longValue() != dv.doubleValue()) {
                    return super.toString(dv);
                } else {
                    return String.valueOf(dv.longValue());
                }
            }
        }

        @Override
        public Double fromString(String tx) {
            Double d = 0d;
            if (!"-".equals(tx.trim()) && !tx.trim().isEmpty()) {
                d = super.fromString(tx);
            } else if ("-".equals(tx.trim()) || tx.trim().isEmpty()) {
                if (defaultValue != null) {
                    textField.setText("0");
                } else if ("-".equals(tx.trim())) {
                    //textField.setText("");
                }
            }
            return d;

            /*            Double d = super.fromString(tx);
            if ( d == null ) {
                textField.setValue(0d);
            } else {
                textField.setValue(d);
            }
            return d;
             */
        }
    }

}
