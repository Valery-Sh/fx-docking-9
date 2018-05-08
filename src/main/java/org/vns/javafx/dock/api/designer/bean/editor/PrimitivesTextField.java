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

import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;
import java.util.function.UnaryOperator;
import javafx.beans.property.Property;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public abstract class PrimitivesTextField<E> extends TextField implements PropertyEditor {

    private final Property<E> value = initValueProperty();
    
    public PrimitivesTextField() {
        getStyleClass().add("text-field-editor");
    }    
    protected final UnaryOperator<TextFormatter.Change> filter = change -> {
        if (isAcceptable(change.getControlNewText())) {
            return change;
        } else {
            return null;
        }
    };

    protected abstract boolean isAcceptable(String txt);

    protected abstract Property<E> initValueProperty();

    public Property<E> valueProperty() {
        return value;
    }

    public E getValue() {
        return value.getValue();
    }

    public void setValue(E value) {
        if (!valueProperty().isBound()) {
            this.value.setValue(value);
        }
    }

    @Override
    public void unbind() {
        value.unbind();
    }

    @Override
    public boolean isBound() {
        return value.isBound();

    }
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public abstract static class NumberTextField<T> extends PrimitivesTextField<Number> {

        private T defaultValue;
        private T minValue;
        private T maxValue;
        
        protected T getDefaultValue() {
            return defaultValue;
        }

        protected void setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public T getMinValue() {
            return minValue;
        }

        public void setMinValue(T minValue) {
            this.minValue = minValue;
        }

        public T getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(T maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public void bind(Property property) {
            this.setEditable(false);
            this.setFocusTraversable(false);
            valueProperty().bind(property);
        }

        @Override
        public void bindBidirectional(Property property) {
            this.setEditable(true);
            this.setFocusTraversable(true);
            valueProperty().bindBidirectional(property);
        }
    }

/*    public abstract static class Converter<S extends Number> extends StringConverter<S> {

        private final S defaultValue;
        private final NumberTextField textField;

        public Converter(NumberTextField textField, S defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;

        }

        protected S getDefaultValue() {
            return defaultValue;
        }

        protected TextField gettextField() {
            return textField;
        }

    }
*/
}
