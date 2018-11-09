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
package org.vns.javafx.scene.control.editors;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 *
 * @author Valery Shyshkin
 */
public class BooleanPropertyEditor extends AbstractPropertyEditor<Boolean> {

    public BooleanPropertyEditor() {
        this(null);
    }

    public BooleanPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
    }

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    public void bind(ReadOnlyProperty<Boolean> property) {
        setBoundProperty(property);
        setEditable(false);

        this.setFocusTraversable(false);
        ((CheckBox) getEditorNode()).selectedProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<Boolean> property) {
        setBoundProperty(property);
        setEditable(true);
        this.setFocusTraversable(true);
        ((CheckBox) getEditorNode()).selectedProperty().bindBidirectional(property);

    }

    @Override
    public void unbind() {
        ((CheckBox) getEditorNode()).selectedProperty().unbind();
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            ((CheckBox) getEditorNode()).selectedProperty().unbindBidirectional((Property) getBoundProperty());
        }
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return ((CheckBox) getEditorNode()).selectedProperty().isBound() || getBoundProperty() != null;

    }

    @Override
    protected Node createEditorNode() {
        return new CheckBox();
    }

}//BooleaPropertyEditor
