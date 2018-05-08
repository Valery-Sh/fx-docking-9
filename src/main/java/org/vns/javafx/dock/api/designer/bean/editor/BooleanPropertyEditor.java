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
import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.scene.control.CheckBox;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public class BooleanPropertyEditor extends CheckBox implements PropertyEditor<Boolean> {

    private static final PseudoClass EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("readonly");

    /*    public PrimitivesTextField() {
        System.err.println("PrimitveTextField Constructor");
        editableProperty().addListener((v, oldValue, newValue) -> {
            System.err.println("editableProperty changed: " + newValue);
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, ! newValue);
        });
    }
     */
    private final Boolean defaultValue;

    //private BooleanProperty value = new SimpleBooleanProperty();
    public BooleanPropertyEditor() {
        this((Boolean) null);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public BooleanPropertyEditor(Boolean defaultValue) {
        this.defaultValue = defaultValue;
        init();
    }

    private void init() {
        getStyleClass().add("check-box-field-editor");
        disableProperty().addListener((v, oldValue, newValue) -> {
            System.err.println("disableProperty changed: " + newValue);
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, newValue);
        });

    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public void bind(Property<Boolean> property) {

        boolean d = defaultValue == null ? property.getValue() : defaultValue;
        //this.setDisabled(true);
        setEditable(false);
        property.setValue(d);
        this.setFocusTraversable(false);
        selectedProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<Boolean> property) {
        boolean d = (defaultValue == null) ? property.getValue() : defaultValue;
        setEditable(true);
        //this.setDisabled(! isModifiable(property));
        property.setValue(d);

        this.setFocusTraversable(true);
        selectedProperty().bindBidirectional(property);

    }

    @Override
    public boolean isEditable() {
        return !isDisable();
    }

    @Override
    public void setEditable(boolean editable) {
        setDisable(!editable);
    }

    @Override
    public void unbind() {
        selectedProperty().unbind();
    }

    @Override
    public boolean isBound() {
        return selectedProperty().isBound();

    }
}
