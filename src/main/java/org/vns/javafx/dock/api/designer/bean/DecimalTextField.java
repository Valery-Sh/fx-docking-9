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

import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Olga
 */
public class DecimalTextField extends DoubleTextField {

    private int scale = 1;
    private RoundingMode roundingMode = RoundingMode.HALF_UP;
    
    public DecimalTextField() {
        this(null, null);
    }

    public DecimalTextField(Double minValue, Double maxValue) {
        this(0d, minValue, maxValue);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public DecimalTextField(Double defaultValue) {
        this(defaultValue, null, null);
    }

    public DecimalTextField(Double defaultValue, Double minValue, Double maxValue) {
        super(defaultValue, minValue, maxValue);
    }
    
    public void setScale(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }
    
    @Override
    public DoubleStringConverter getConverter() {
        return new Converter(this, getDefaultValue() );
    }
    public int getScale() {
        return scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public static class Converter extends DoubleStringConverter {

        private final Double defaultValue;
        private final DecimalTextField textField;

        public Converter(DecimalTextField textField, Double defaultValue) {
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
 /*        if (dv.longValue() != dv.doubleValue()) {
                    return super.toString(dv);
                } else {
                    return String.valueOf(dv.longValue());
                }
  */
                if (dv.longValue() == dv.doubleValue()) {
                    return String.valueOf(dv.longValue());
                } else {
                    BigDecimal bd = new BigDecimal(dv);
                    bd = bd.setScale(textField.getScale(), textField.getRoundingMode());
                    return bd.toString();
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
