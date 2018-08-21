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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.FloatStringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivesPropertyEditor.NumberTextField;

/**
 *
 * @author Olga
 */
public class FloatPropertyEditor extends NumberTextField<Float> {

    public FloatPropertyEditor() {
        this(null, null);
    }

    public FloatPropertyEditor(Float minValue, Float maxValue) {
        this(0f, minValue, maxValue);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public FloatPropertyEditor(Float defaultValue) {
        this(defaultValue, null, null);
    }

    public FloatPropertyEditor(Float defaultValue, Float minValue, Float maxValue) {
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

    /*    @Override
    protected boolean isAcceptable(String txt) {

        if (txt == null) {
            return false;
        }
        if (txt.isEmpty() || "-".equals(txt)) {
            return true;
        }

        boolean retval = false;
        if (txt.matches(getPattern()) && Double.parseDouble(txt) <= Float.MAX_VALUE && Double.parseDouble(txt) >= -Float.MAX_VALUE) {
            retval = true;
        }
        return retval;
    }
     */
    protected String getPattern() {
        return "0|-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
    }

    private void init() {
        getStyleClass().add("float-text-field");
        float fv = getDefaultValue() == null ? 0 : getDefaultValue();
        Converter c = new Converter(this, getDefaultValue());
        valueProperty().addListener((v, ov, nv) -> {

            if (!((Float) nv == 0 && ("-".equals(getText()) || getText().isEmpty() || getText().endsWith(".")))) {
                setText(c.toString((Float) nv));
            }
            //setText(c.toString((Double)nv));
        });
        textProperty().addListener((v, ov, nv) -> {
            if (!"-".equals(nv) && !"".equals(nv) && !nv.endsWith(".")) {
                setValue(c.fromString(nv));
            } else if ("".equals(nv) || "-".equals(nv)) {
                setValue(0F);
            }
            //c.fromString(nv);
        });
        setTextFormatter(new TextFormatter<>(c, fv, filter));
    }

    @Override
    protected Property initValueProperty() {
        return new SimpleFloatProperty();
    }

    public static class Converter extends FloatStringConverter {

        private final Float defaultValue;
        private final FloatPropertyEditor textField;

        public Converter(FloatPropertyEditor textField, Float defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString(Float fv) {
            if (fv == null && defaultValue == null) {
                return "";
            } else if (fv == null && defaultValue != null) {
                return "0";
            } else if (fv == 0f && defaultValue == null) {
                return "";
            } else if (fv == 0f) {
                textField.setValue(0f);
                return "0";
            } else {
                if (fv.longValue() != fv.floatValue()) {
                    return super.toString(fv);
                } else {
                    return String.valueOf(fv.longValue());
                }
            }
        }

        @Override
        public Float fromString(String tx) {
            System.err.println("FLOAT fromString tx = " + tx);
            Float fv = 0f;
            if (!"-".equals(tx.trim()) && !tx.trim().isEmpty()) {
                fv = super.fromString(tx);
            } else if ("-".equals(tx.trim()) || tx.trim().isEmpty()) {
                if (defaultValue != null) {
                    textField.setText("0");
                } else if ("-".equals(tx.trim())) {
                    //textField.setText("");
                }
            }
            return fv;

        }
    }

}
