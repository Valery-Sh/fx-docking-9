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

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Skin;
import javafx.scene.layout.GridPane;
import org.vns.javafx.scene.control.editors.skin.ButtonTypeComboBoxPropertyEditorSkin;

/**
 *
 * @author Valery Shyskin
 */
public class ButtonTypeComboBoxPropertyEditor extends ObservableListPropertyEditor<ButtonType>{
    

    public ButtonTypeComboBoxPropertyEditor() {
        this(null);
    }
    public ButtonTypeComboBoxPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getStyleClass().add("button-type-editor");
        setStringConverter(new ObservableListItemStringConverter(this,ButtonType.class));            
        getTextField().setSeparator(",");
        getTextField().setDefaultValue("");
        getTextField().setEmptySubstitution("");
        getTextField().setNullable(false);

    }


    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new ButtonTypeComboBoxPropertyEditorSkin(this);
    }

    @Override
    protected Node createEditorNode() {
        super.createEditorNode();
        return new GridPane();
    }

}//Control
