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
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class DecimalPropertyEditor extends TextFieldPropertyEditor<Double> {

    private final RoundingMode roundingMode = RoundingMode.HALF_UP;

    private final DoubleProperty minValue = new SimpleDoubleProperty();
    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private final IntegerProperty scale = new SimpleIntegerProperty();

    public DecimalPropertyEditor() {
        this(null, Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }

    public DecimalPropertyEditor(Double minValue, Double maxValue, Integer scale) {
        this(null, minValue, maxValue, scale);
    }

    public DecimalPropertyEditor(String name, Double minValue, Double maxValue, Integer scale) {
        super(name);
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
        getTextField().getStringTransformers().add(src -> {
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
        getTextField().getValidators().add(item -> {
            Double dv = Double.valueOf(item);
            boolean retval = dv >= getMinValue() && dv <= getMaxValue();
            return retval;
        });

    }

    @Override
    protected void addFilterValidators() {
        getTextField().getFilterValidators().add(item -> {
/*            String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
            String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d{0," + getScale() + "})?)";
            String regExp = "([+-]?)|([+-]?\\d+\\.?(\\d+)?)";
            if (getScale() == 0) {
                regExp = "0|-?([1-9][0-9]*)?";
                //regExp = "([+-]?)|([+-]?\\d+)";
            } else if (getScale() > 0) {
                regExp = "([+-]?)|([+-]?\\d+\\.?(\\d{0," + getScale() + "})?)";
            }
*/
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
            if (retval) {
                Double dv = 0d;
                if (!item.trim().isEmpty() && !item.trim().equals("-") && !item.trim().equals("+")) {
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

    public double getValue() {
        return Double.valueOf(getTextField().getText());
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
        return new TextFieldPropertyEditor.BindingStringConverter<>(this);
    }

    @Override
    protected StringBinding asString(ReadOnlyProperty property) {
        return ((DoubleProperty) property).asString();
    }

    @Override
    public Double valueOf(String txt) {
        if (txt.isEmpty()) {
            return 0d;
        }
        return Double.valueOf(stringOf(Double.valueOf(txt)));
    }

}//class DecimalPropertyEditor
