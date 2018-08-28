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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Valery
 */
public abstract class AbstractPropertyEditor<E> extends StringTextField implements PropertyEditor<E> {

    private ObservableValue boundValue;
    
    private StringConverter<E> stringConverter;

    public AbstractPropertyEditor() {
        init();
    }

    private void init() {
        stringConverter = createStringConverter();
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

        //setValueIfBlank("0");
    
        addValidators();
        addFilterValidators();
    }
    protected void addValidators() {
        
    }
    protected void addFilterValidators() {
        
    }

    public StringConverter<E> getStringConverter() {
        return stringConverter;
    }
    
    private boolean checkValidators(E dv) {
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


    @Override
    public void bind(Property property) {
        unbind();
        setEditable(true);
        setEditable(false);

        this.boundValue = (ObservableValue<E>) property;
//        rightValueProperty().bind(boundValue.asString());
    }
    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);
        boundValue = (ObservableValue<E>) property;
        boundValue.addListener((v, ov, nv) -> {
            if (!checkValidators((E)nv)) {
                Platform.runLater(() -> {
                    setBoundValue((E) ov);
                });
            }
        });
        this.boundValue = (ObservableValue<E>) property;
        rightValueProperty().bindBidirectional(property, stringConverter);
    }

    public ObservableValue boundValueProperty() {
        return boundValue;
    }
    public  E getBoundValue() {
        return (E)boundValue.getValue();
    }
    public abstract void setBoundValue(E boundValue);
    
    public abstract StringConverter<E> createStringConverter();
    public abstract String stringOf(E dv);

    @Override
    public void unbind() {
        rightValueProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return rightValueProperty().isBound();

    }

/*    public static class Converter extends DoubleStringConverter {

        private final AbstractPropertyEditor textField;

        public Converter(AbstractPropertyEditor textField) {
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
*/
}//class AbstractPropertyEditor
