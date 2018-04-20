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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public class IntegerTextField extends PrimitivesTextField.NumberTextField<Integer> {

    public IntegerTextField() {
        this(null, null);
    }

    public IntegerTextField(Integer minValue, Integer maxValue) {
        this(0, minValue, maxValue);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public IntegerTextField(Integer defaultValue) {
        this(defaultValue, null, null);
    }

    public IntegerTextField(Integer defaultValue, Integer minValue, Integer maxValue) {
        setDefaultValue(defaultValue);
        setMinValue(minValue);
        setMaxValue(maxValue);
        init();
    }

    private void init() {
        getStyleClass().add("integer-text-field");
        int d = getDefaultValue() == null ? 0 : getDefaultValue();
        Converter c = new Converter(this, getDefaultValue());
        valueProperty().addListener((v, ov, nv) -> {
            if (!((Integer) nv == 0 && ("-".equals(getText()) || getText().isEmpty()))) {
                setText(c.toString((Integer) nv));
            }
        });
        textProperty().addListener((v, ov, nv) -> {
            if (!"-".equals(nv) && !"".equals(nv)) {
                setValue(c.fromString(nv));
            } else if ("".equals(nv) || "-".equals(nv)) {
                setValue(0);
            }
        });
        TextFormatter<Integer> formatter = new TextFormatter<Integer>(c, d, filter);
        setTextFormatter(formatter);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
    
    protected String getPattern() {
        return "0|-?([1-9][0-9]*)?";
    }

    @Override
    protected boolean isAcceptable(String txt) {
        if (txt == null) {
            return false;
        }
        if (txt.isEmpty() || "-".equals(txt)) {
            return true;
        }

        if (txt.matches(getPattern()) && Long.parseLong(txt) <= Integer.MAX_VALUE && Long.parseLong(txt) >= Integer.MIN_VALUE) {
            if (getMinValue() == null && getMaxValue() == null) {
                return true;
            }
            if (getMinValue() != null && Integer.parseInt(txt) < getMinValue()) {
                return false;
            }
            if (getMaxValue() != null && Integer.parseInt(txt) > getMaxValue()) {
                return false;
            }
            return true;
        }
        return false;

    }

    @Override
    protected Property<Number> initValueProperty() {
        return new SimpleIntegerProperty();
    }

    public static class Converter extends IntegerStringConverter {

        private final Integer defaultValue;
        private final IntegerTextField textField;

        public Converter(IntegerTextField textField, Integer defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString(Integer v) {
            if (v == null && defaultValue == null) {
                return "";
            } else if (v == null && defaultValue != null) {
                return "0";
            } else if (v == 0 && defaultValue == null) {
                return "";
            } else if (v == 0) {
                return "0";
            } else {
                return v.toString();
            }
        }

        @Override
        public Integer fromString(String tx) {
            System.err.println("fromString tx = " + tx);
            Integer v = 0;
            if (!"-".equals(tx.trim()) && !tx.trim().isEmpty()) {
                v = super.fromString(tx);
            } else if ("-".equals(tx.trim()) || tx.trim().isEmpty()) {
                if (defaultValue != null) {
                    textField.setText("0");
                } else if ("-".equals(tx.trim())) {
                    //textField.setText("");
                }
            }
            return v;
        }
    }

}
