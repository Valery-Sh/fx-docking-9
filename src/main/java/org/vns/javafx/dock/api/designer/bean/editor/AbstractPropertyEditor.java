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

import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public abstract class AbstractPropertyEditor<E> extends StringTextField implements PropertyEditor<E> {


    private Property<E> boundProperty;

    private boolean realTimeBinding;

    private StringConverter<E> stringConverter;

    public boolean isRealTimeBinding() {
        return realTimeBinding;
    }

    public void setRealTimeBinding(boolean realTimeBinding) {
        this.realTimeBinding = realTimeBinding;
    }

  
    public AbstractPropertyEditor() {
        init();
    }

    private void init() {
        stringConverter = createBindingStringConverter();
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

  
    @Override
    public void bind(Property property) {
        unbind();
        //setEditable(true);
        setEditable(false);

        this.boundProperty = property;
        StringProperty sp = isRealTimeBinding() ? textProperty() : lastValidTextProperty();
        if (property instanceof StringExpression) {
            sp.bind(property);
        } else {
            sp.bind(asString(property));
        }
        createContextMenu(property);
    }

    @Override
    public void bindBidirectional(Property property) {
        System.err.println("AbstractPropertyEditor bindBidirectional");
        unbind();
        setEditable(true);
        //0909boundPropertyOld.set((ObservableValue<E>) property);
        this.boundProperty = property;
        /*        boundPropertyOld.get().addListener((v, ov, nv) -> {
            System.err.println("CHANGE LISTENER: ov = " + ov + "; nv = " + nv + "; bondValue=" + boundPropertyOld.getValue());
            if (!checkValidators((E) nv)) {
                Platform.runLater(() -> {
                    System.err.println("CHANGE LISTENER: RUN LATER before set old bound value ov = " + ov + " nv = " + nv);
//                    boundPropertyOld.get()
                    setBoundValue((E) ov);
                    System.err.println("CHANGE LISTENER: RUN LATER after set old bound value ov = " + ov + " nv = " + nv);
                });
            }
        });
         */

        //lastValidTextProperty().bindBidirectional(property, stringConverter);
        if (isRealTimeBinding()) {
            textProperty().bindBidirectional(property, stringConverter);
        } else {
            lastValidTextProperty().bindBidirectional(property, stringConverter);
        }
        createContextMenu(property);
    }

    /*0909    public ObjectProperty<ObservableValue> boundPropertyProperty() {
        return boundPropertyOld;
    }

    public ObservableValue<E> getBoundProperty() {
        return boundPropertyOld.get();
    }

    public void setBoundProperty(ObservableValue<E> boundProperty) {
        this.boundPropertyOld.set(boundProperty);
    }
     */
//    public abstract void setBoundValue(E boundValue);
    public abstract E valueOf(String txt);

    public Property<E> getBoundProperty() {
        return boundProperty;
    }

    protected void setBoundProperty(Property<E> boundProperty) {
        this.boundProperty = boundProperty;
    }

    public StringConverter<E> createBindingStringConverter() {
        return new BindingStringConverter(this);
    }

    protected void createContextMenu(Property property) {
    }

    public String stringOf(E value) {
        String retval = value == null ? "" : value.toString();
        if (value == null && getNullSubstitution() != null) {
            retval = getNullSubstitution();
        }
        return retval;
    }

    protected abstract StringBinding asString(Property property);

    @Override
    public void unbind() {
        lastValidTextProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return lastValidTextProperty().isBound();

    }

    public static class BindingStringConverter<T> extends StringConverter<T> {

        private final AbstractPropertyEditor editor;

        public BindingStringConverter(AbstractPropertyEditor textField) {
            this.editor = textField;
        }

        protected T getBoundValue() {
            return (T) getEditor().getBoundProperty().getValue();
        }

        public AbstractPropertyEditor getEditor() {
            return editor;
        }

        @Override
        public String toString(T dv) {
            if (dv == null && editor.getNullSubstitution() != null) {
                return editor.getNullSubstitution();
            }
            return editor.stringOf(dv);
        }

        @Override
        public T fromString(String tx) {
            System.err.println("fromString _______________________");
            T retval;
            if (getEditor().hasErrorItems()) {
                retval = getBoundValue();
            } else {
                retval = (T) editor.valueOf(tx);
            }
            return retval;
        }
    }//class BindingStringConverter

}//class AbstractPropertyEditor
