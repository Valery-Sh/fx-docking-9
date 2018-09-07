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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public abstract class AbstractPropertyEditor<E> extends StringTextField implements PropertyEditor<E> {

    private final ObjectProperty<ObservableValue> boundProperty = new SimpleObjectProperty<>();
    //private E oldBoundValue;
    protected ChangeListener<? super E> boubdValueChangeListener = (v, ov, nv) -> {
        System.err.println("CHANGE LISTENER: ov = " + ov + "; nv = " + nv + "; bondValue=" + boundProperty.getValue());
        if (!checkValidators((E) nv)) {
            Platform.runLater(() -> {
                System.err.println("CHANGE LISTENER: RUN LATER before set old bound value ov = " + ov + " nv = " + nv);
                restoreValue((E) ov);
                System.err.println("CHANGE LISTENER: RUN LATER after set old bound value ov = " + ov + " nv = " + nv);
            });
        }
    };

    private StringConverter<E> stringConverter;
    
    private void restoreValue(E v) {
        System.err.println("1 sss getBoundValue = " + boundProperty.get().getValue() );
        //boundProperty.get().removeListener(boubdValueChangeListener);
        setBoundValue(v);
        System.err.println("2 sss getBoundValue = " + boundProperty.get().getValue());
        //boundProperty.get().addListener(boubdValueChangeListener);
    }
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
        //setEditable(true);
        setEditable(false);

        this.boundProperty.set((ObservableValue<E>) property);
        lastValidTextProperty().bind(asString(property));
        createContextMenu(property);
    }

    @Override
    public void bindBidirectional(Property property) {
        System.err.println("AbstractPropertyEditor bindBidirectional");
        unbind();
        setEditable(true);
        boundProperty.set((ObservableValue<E>) property);
/*        boundProperty.get().addListener((v, ov, nv) -> {
            System.err.println("CHANGE LISTENER: ov = " + ov + "; nv = " + nv + "; bondValue=" + boundProperty.getValue());
            if (!checkValidators((E) nv)) {
                Platform.runLater(() -> {
                    System.err.println("CHANGE LISTENER: RUN LATER before set old bound value ov = " + ov + " nv = " + nv);
//                    boundProperty.get()
                    setBoundValue((E) ov);
                    System.err.println("CHANGE LISTENER: RUN LATER after set old bound value ov = " + ov + " nv = " + nv);
                });
            }
        });
*/
        //lastValidTextProperty().bindBidirectional(property, stringConverter);
        textProperty().bindBidirectional(property, stringConverter);
        createContextMenu(property);
    }

    public ObjectProperty<ObservableValue> boundPropertyProperty() {
        return boundProperty;
    }

    public ObservableValue<E> getBoundProperty() {
        return boundProperty.get();
    }

    public void setBoundProperty(ObservableValue<E> boundProperty) {
        this.boundProperty.set(boundProperty);
    }

    public abstract void setBoundValue(E boundValue);

    public abstract StringConverter<E> createStringConverter();
    public abstract E valueOf(String txt);

    protected void createContextMenu(Property property) {
    }

    public String stringOf(E value) {
        String retval = value == null ? "" : value.toString();
        if ( value == null && getNullString() != null ) {
            retval = getNullString();
        }
        return value == null ? "" : value.toString();
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

    public static class Converter<T> extends StringConverter<T> {

        private final AbstractPropertyEditor editor;

        public Converter(AbstractPropertyEditor textField) {
            this.editor = textField;
        }

        protected T getBoundValue() {
            return (T) getEditor().getBoundProperty().getValue();
        }

//        protected abstract T valueOf(String txt);

        public AbstractPropertyEditor getEditor() {
            return editor;
        }

        @Override
        public String toString(T dv) {
            System.err.println("TO STRING dv = " + dv);
            if ( dv == null && editor.getNullString() != null ) {
                System.err.println("1 TO STRING dv = " + dv + "; getNullString() = " + editor.getNullString());
                return editor.getNullString();
            }
            return editor.stringOf(dv);
        }

        @Override
        public T fromString(String tx) {
            T retval;
            if (getEditor().hasErrorItems()) {
                System.err.println("getTextField().hasErrorItems() = " + getEditor().hasErrorItems());
                retval = getBoundValue();
            } else {
                retval = (T) editor.valueOf(tx);
            }
            return retval;
        }
    }//class Converter

}//class AbstractPropertyEditor
