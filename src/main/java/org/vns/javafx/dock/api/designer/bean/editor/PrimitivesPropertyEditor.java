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

import java.util.function.UnaryOperator;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public abstract class PrimitivesPropertyEditor<E> extends TextField implements PropertyEditor {

    private final Property<E> value = initValueProperty();
    
    public PrimitivesPropertyEditor() {
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

    String toString(ObservableList list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ObservableList fromString(String txt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public abstract static class NumberTextField<T> extends PrimitivesPropertyEditor<Number> {

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

}
