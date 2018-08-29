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

import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public abstract class AbstractPropertyEditor<E> extends StringTextField implements PropertyEditor<E> {

    private ObservableValue boundValue;
    //private E oldBoundValue;

    private StringConverter<E> stringConverter;

    public AbstractPropertyEditor() {
        init();
    }

    private void init() {
        stringConverter = createStringConverter();
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));

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
        lastValidTextProperty().bind(asString(property));
    }

    @Override
    public void bindBidirectional(Property property) {
        System.err.println("AbstractPropertyEditor bindBidirectional");
        unbind();
        setEditable(true);
        boundValue = (ObservableValue<E>) property;
/*        boundValue.addListener((v, ov, nv) -> {
        System.err.println("CHANGE LISTENER: ov = " + ov+ "; nv = " + nv + "; bondValue=" + boundValue.getValue());
            oldBoundValue = (E)nv;
        });        
*/        
        boundValue.addListener((v, ov, nv) -> {
            System.err.println("CHANGE LISTENER: ov = " + ov+ "; nv = " + nv + "; bondValue=" + boundValue.getValue());
            if (!checkValidators((E) nv)) {
                Platform.runLater(() -> {
                    System.err.println("CHANGE LISTENER: RUN LATER before set old bound valueov = " + ov);
                    setBoundValue((E) ov);
                    System.err.println("CHANGE LISTENER: RUN LATER after set old bound valueov = " + ov);
                });
            }
        });
      
/*        boundValue.addListener((Observable v) -> {
            E saveOldValue = oldBoundValue;
            if (!checkValidators((E) boundValue.getValue())) {
                Platform.runLater(() ->{
                    System.err.println("RUN LATER INV LISTENER: oldBoundValue = " + oldBoundValue+ "; saveOldValue = " + saveOldValue);
                    setBoundValue(saveOldValue);
                });
                
            }
            System.err.println("INV LISTENER: v = " + v+ "; boundValue = " + boundValue.getValue());
        });
*/
        boundValue = (ObservableValue<E>) property;
//        oldBoundValue = (E)boundValue.getValue();
        lastValidTextProperty().bindBidirectional(property, stringConverter);
    }

    public ObservableValue boundValueProperty() {
        return boundValue;
    }

    public E getBoundValue() {
        return (E) boundValue.getValue();
    }

    public abstract void setBoundValue(E boundValue);

    public abstract StringConverter<E> createStringConverter();

    public String stringOf(E value) {
        return value.toString();
    }
    
    protected abstract StringBinding  asString(Property property);
    

    @Override
    public void unbind() {
        lastValidTextProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return lastValidTextProperty().isBound();

    }

    public abstract static class Converter<T> extends StringConverter<T> {

        private final AbstractPropertyEditor textField;

        public Converter(AbstractPropertyEditor textField) {
            this.textField = textField;
        }

        protected T getBoundValue() {
            return (T)getTextField().getBoundValue();
        }
        protected abstract T valueOf(String txt);

        public AbstractPropertyEditor getTextField() {
            return textField;
        }

        @Override
        public String toString(T dv) {
            return textField.stringOf(dv);
        }

        @Override
        public T fromString(String tx) {
            T retval;
            if (getTextField().hasErrorItems()) {
                System.err.println("getTextField().hasErrorItems() = " + getTextField().hasErrorItems());
                retval = getBoundValue();
            } else {
                retval = valueOf(tx);
            }
            return retval;
        }
    }//class Converter

}//class AbstractPropertyEditor
