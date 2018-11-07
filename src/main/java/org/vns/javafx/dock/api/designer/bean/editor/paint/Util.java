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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Nastia
 */
public class Util {

    public static StringConverter<Number> doubleStringConverter(final int scale) {
        StringConverter<Double> ds = new DoubleStringConverter();
        return new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                Double dv = (Double) value;
                if (value == null || dv == -0 || dv == -0.0 || dv == -0.00) {
                    dv = 0d;
                }
                return String.format("%1$,." + scale + "f", dv);
            }

            @Override
            public Double fromString(String string) {
                if (string == null || "-".equals(string) || "+".equals(string) || "-0.".equals(string)) {
                    string = "";
                }

                return ds.fromString(string);
            }
        };
    }

    public static TextFormatter createTextFormatter(final TextField txtField, double minValue, double maxValue, int scale) {
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
        TextFormatter<Double> f = new TextFormatter(Util.doubleStringConverter(scale), minValue, filter);
        txtField.setTextFormatter(f);
        return f;
    }

    public static boolean validate(String item, double minValue, double maxValue, int scale) {
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

    public static boolean validate(String item, int scale) {
        return validate(item, -Double.MAX_VALUE, Double.MAX_VALUE, scale);
    }

    public static Color newColorBy(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
    }
    public static LinearGradient newGradientBy(LinearGradient g) {
        return new LinearGradient(g.getStartX(), g.getStartY(), g.getEndX(), g.getEndY(),
                g.isProportional(), g.getCycleMethod(), g.getStops());
    }
    public static RadialGradient newGradientBy(RadialGradient g) {
        return new RadialGradient(g.getFocusAngle(), g.getFocusDistance(), 
                g.getCenterX(), g.getCenterY(), g.getRadius(), g.isProportional(), 
                g.getCycleMethod(), g.getStops());
    }

}
