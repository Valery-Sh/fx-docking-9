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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Valery
 */
public class DecimalPropertyEditor_OLD extends PrimitivePropertyEditor<Double> {

    private DoubleProperty boundValue = new SimpleDoubleProperty();

    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    private final DoubleProperty minValue = new SimpleDoubleProperty();
    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private final IntegerProperty scale = new SimpleIntegerProperty();

    public DecimalPropertyEditor_OLD() {
        this(Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }

    public DecimalPropertyEditor_OLD(Double minValue, Double maxValue, Integer scale) {
        this.minValue.set(minValue);
        this.maxValue.set(maxValue);
        this.scale.set(scale);
        init();
    }

    private void init() {
        stringConverter = new Converter(this);
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

        //setValueIfBlank("0");
        setFromStringTransformer(src -> {
            String retval = src;
            src = src.trim();
            if (src.isEmpty() || src.equals(".")) {
                return stringConverter.toString(getMinValue());
            }
            Double dv = Double.valueOf(src);
            /*            if ( dv < getMinValue() ) {
                dv = getMinValue();
            } else if ( dv > getMaxValue() ) {
                dv = getMaxValue();
            }
             */
            if (dv.longValue() == dv.doubleValue()) {
                return String.valueOf(dv.longValue());
            }
            return stringOf(dv);
//                    String e = new String(new char[getScale()]).replace("\0", "0");
        });

        getValidators().add(item -> {
            System.err.println("VALIDATOR item = " + item);
            /*      String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
            if (getScale() == 0) {
                regExp = "([+-]?\\d+)";
            }
                
            boolean retval = Pattern.matches(regExp, item.trim());
             */
//            boolean retval = true;
//            if ( retval ) {
            Double dv = Double.valueOf(item);
            boolean retval = dv >= getMinValue() && dv <= getMaxValue();
//            }
            return retval;

        });

        getFilterValidators().add(item -> {
            //item = item.trim();
            String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
            if (getScale() == 0) {
                regExp = "([+-]?)|([+-]?\\d+)";
            } else if (getScale() > 0) {
                regExp = "([+-]?)|([+-]?\\d+\\.?(\\d{0," + getScale() + "})?)";
            }
            boolean retval = item.trim().isEmpty();
            if (!retval) {
                retval = Pattern.matches(regExp, item);

                if (retval) {
                    try {
                        //stringConverter.fromString(item);
                    } catch (Exception ex) {

                    }
                }
            }

            return retval;
        });
    }

    private boolean checkValidators(Double dv) {
        boolean retval = true;
        String sv = stringOf(dv);
        for (Predicate<String> p : getValidators()) {
            if (!p.test(sv)) {
                retval = false;
                break;
            }
        }
        return retval;
    }

    public String stringOf(Double dv) {
        BigDecimal bd = new BigDecimal(dv);
        bd = bd.setScale(getScale(), getRoundingMode());
        return bd.toString();
    }

    @Override
    public void bind(Property property) {
        unbind();
        setEditable(true);
        setEditable(false);

        DoubleProperty dp = (DoubleProperty) property;
        if (dp.getValue() < getMinValue()) {
            dp.setValue(getMinValue());
        }
        if (dp.getValue() > getMaxValue()) {
            dp.setValue(getMaxValue());
        }
        this.boundValue = (DoubleProperty) property;
        rightValueProperty().bind(dp.asString());
    }

    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);
        boundValue = (DoubleProperty) property;
        if (boundValue.getValue() < getMinValue()) {
            boundValue.setValue(getMinValue());
        }
        if (boundValue.getValue() > getMaxValue()) {
            boundValue.setValue(getMaxValue());
        }
        boundValue.addListener((v, ov, nv) -> {
            if ((Double) nv < getMinValue() || (Double) nv > getMaxValue()) {
                Platform.runLater(() -> {
                    setBoundValue((Double) ov);
                });
            }
        });
        this.boundValue = (DoubleProperty) property;
        rightValueProperty().bindBidirectional(property, stringConverter);
    }

    public DoubleProperty boundValueProperty() {
        return boundValue;
    }

    public Double getBoundValue() {
        return boundValue.get();
    }

    public void setBoundValue(Double boundValue) {
        this.boundValue.set(boundValue);
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public IntegerProperty scaleProperty() {
        return scale;
    }

    public Double getMinValue() {
        return minValue.get();
    }

    public Double getMaxValue() {
        return maxValue.get();
    }

    public Integer getScale() {
        return scale.get();
    }

    public void setMinValue(Double minValue) {
        this.minValue.set(minValue);
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue.set(maxValue);
    }

    public void setScale(Integer scale) {
        this.scale.set(scale);
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public static class Converter extends DoubleStringConverter {

        private final DecimalPropertyEditor_OLD textField;

        public Converter(DecimalPropertyEditor_OLD textField) {
            this.textField = textField;
        }

        @Override
        public String toString(Double dv) {
            System.err.println("Convertor toString dv = " + dv);
            String retval;
            if (dv < textField.getMinValue() || dv > textField.getMaxValue()) {
                System.err.println("Convertor toString 2 " + stringOf(textField.getMinValue()));
                retval = stringOf(textField.getMinValue());
            } else {
                System.err.println("Convertor toString 3 " + stringOf(dv));
                retval = stringOf(dv);
            }
            //retval = stringOf(dv);
            System.err.println("   --- getRightValue() = " + textField.getRightValue());
            System.err.println("   --- valueOf(retval) = " + Double.valueOf(retval));
            //if (textField.getRightValue() != null && dv != Double.valueOf(retval)) {
            Platform.runLater(() -> {
                System.err.println("RUNLATER dv.toString() = " + dv.toString() + "; retval=" + retval);
                textField.setRightValue(dv.toString());
                textField.setRightValue(retval);
                textField.commitValue();

            });
            //}
            System.err.println("Convertor RETURN to String retval = " + retval);
            return retval;
        }

        public String stringOf(Double dv) {
            BigDecimal bd = new BigDecimal(dv);
            bd = bd.setScale(textField.getScale(), textField.getRoundingMode());
            return bd.toString();

            /*            if (dv.longValue() == dv.doubleValue()) {
                return String.valueOf(dv.longValue());
            } else {
            }
             */
        }

        @Override
        public Double fromString(String tx) {
            System.err.println("Convertor fromString 1 tx = " + tx);

            Double retval = 0d;
            //if (tx.trim().isEmpty()) {
            if (textField.hasErrorItems() ) {
                System.err.println("Convertor fromString hasErrors ");
                retval = textField.getBoundValue();
            } else {

                System.err.println("Convertor fromString double " + Double.valueOf(tx));
                retval = super.fromString(stringOf(Double.valueOf(tx)));
            }
            System.err.println("Convertor fromString 2 " + retval);
            return retval;
        }
    }//class Converter

}//class DecimalPropertyEditor_OLD
