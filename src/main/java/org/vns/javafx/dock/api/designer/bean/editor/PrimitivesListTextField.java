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
import java.util.function.UnaryOperator;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class PrimitivesListTextField<E> extends TextField implements PropertyEditor<ObservableList> {

    private final Property<ObservableList<E>> value = initValueProperty();
    private Predicate<String> validator;

    public PrimitivesListTextField() {
        getStyleClass().add("list-text-field-editor");
    }


    protected final UnaryOperator<TextFormatter.Change> filter = change -> {
        if (isAcceptable(change.getControlNewText())) {
            return change;
        } else {
            return null;
        }
    };

    protected abstract boolean isAcceptable(String txt);

    protected abstract Property<ObservableList<E>> initValueProperty();

    public Property<ObservableList<E>> valueProperty() {
        return value;
    }

    public ObservableList<E> getValue() {
        return value.getValue();
    }

    public void setValue(ObservableList<E> value) {
        if (!valueProperty().isBound()) {
            this.value.setValue(value);
        }
    }
    @Override
    public void bind(Property property) {
    }

    @Override
    public void bindBidirectional(Property property) {
    }

    public Predicate<String> getValidator() {
        return validator;
    }

    public void setValidator(Predicate<String> validator) {
        this.validator = validator;
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
