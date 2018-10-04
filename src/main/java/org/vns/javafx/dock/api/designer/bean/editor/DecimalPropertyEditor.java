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
import java.util.regex.Pattern;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class DecimalPropertyEditor extends AbstractPropertyEditor<Double> {

    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    private final DoubleProperty minValue = new SimpleDoubleProperty();
    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private final IntegerProperty scale = new SimpleIntegerProperty();

    public DecimalPropertyEditor() {
        this(Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }

    public DecimalPropertyEditor(Double minValue, Double maxValue, Integer scale) {
        if (minValue.equals(Double.MIN_VALUE)) {
            this.minValue.set(-Double.MAX_VALUE);
        } else {
            this.minValue.set(minValue);
        }
        this.maxValue.set(maxValue);
        this.scale.set(scale);
        init();
    }

    private void init() {

        getStringTransformers().add(src -> {
            String retval = src;
            src = src.trim();
            Double dv;
            if (src.isEmpty() || src.equals(".")) {
                dv = getMinValue();
            } else {
                dv = Double.valueOf(src);
            }
            if (dv.longValue() == dv.doubleValue()) {
                return String.valueOf(dv.longValue());
            }
            return stringOf(dv);
        });

    }

    @Override
    protected void addValidators() {
        getValidators().add(item -> {

            Double dv = Double.valueOf(item);
            System.err.println("min()=" + Double.min(Double.MIN_VALUE, dv));
            boolean retval = dv >= getMinValue() && dv <= getMaxValue();
            System.err.println("MIN = " + getMinValue() + "; MAX=" + getMaxValue());
            System.err.println("validator item=" + dv + "; retval = " + retval);
            return retval;

        });
    }

    @Override
    protected void addFilterValidators() {
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
            }
            if (retval ) {
                Double dv = 0d;
                if ( !item.trim().isEmpty() && !item.trim().equals("-") && !item.trim().equals("+") ) {
                    dv = Double.valueOf(item);
                }
                retval = dv >= getMinValue() && dv <= getMaxValue();
            }
            return retval;
        });

    }

    @Override
    public String stringOf(Double dv) {
        BigDecimal bd = new BigDecimal(dv);
        bd = bd.setScale(getScale(), getRoundingMode());
        return bd.toString();
    }

/*0909    @Override
    public void setBoundValue(Double boundValue) {
        ((DoubleProperty) (Observable) getBoundProperty()).set(boundValue);
    }
*/
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

    @Override
    public StringConverter<Double> createBindingStringConverter() {
        //return new DoubleConverter(this);
        return new AbstractPropertyEditor.BindingStringConverter<>(this);
    }

    @Override
    protected StringBinding asString(Property property) {
        return ((DoubleProperty) property).asString();
    }

    @Override
    public Double valueOf(String txt) {
        if (txt.isEmpty()) {
            return 0d;
        }
        return Double.valueOf(stringOf(Double.valueOf(txt)));
    }

    /*    public static class DoubleConverter extends AbstractPropertyEditor.BindingStringConverter<Double> {
        public DoubleConverter(DecimalPropertyEditor textField) {
            super(textField);
        }
        @Override
        protected Double valueOf(String txt) {
            return (Double) getEditor().valueOf(txt);
            //return Double.valueOf(getEditor().stringOf(Double.valueOf(txt)));            
        }

    }//class DoubleConverter
     */
}//class DecimalPropertyEditor
