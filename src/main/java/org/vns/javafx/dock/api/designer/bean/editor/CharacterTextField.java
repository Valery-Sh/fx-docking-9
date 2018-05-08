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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public class CharacterTextField extends TextField implements PropertyEditor<String> {

    private final Character defaultValue;
    private StringProperty value = new SimpleStringProperty();

    private final UnaryOperator<TextFormatter.Change> filter = change -> {
        
        String newText = change.getControlNewText();

        if (newText.length() <= 1) {
            return change;
        }
        return null;
    };

    public CharacterTextField() {
        this(null);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public CharacterTextField(Character defaultValue) {
        this.defaultValue = defaultValue;
        init();
    }

    private void init() {
        Converter c = new Converter(this, defaultValue);
        
        value.addListener((v, ov, nv) -> {
            System.err.println("$$$$ NV = " + nv);
            setText(nv);
        });
        textProperty().addListener((v, ov, nv) -> {
            //String c = nv == null || nv.isEmpty() ? "" : nv.substring(0,1);
            //setValue(c);
             c.fromString(nv);
        });
        String lv = defaultValue == null ? "" : defaultValue.toString();
        setTextFormatter(new TextFormatter<String>(c,lv,filter));
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
    public StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        if (!valueProperty().isBound()) {
            this.value.set(value);
        }
    }

    @Override
    public void bind(Property<String> property) {
        this.setEditable(false);
        this.setFocusTraversable(false);
        if ( defaultValue != null ) {
            property.setValue(defaultValue.toString());
        }        
        valueProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<String> property) {
        this.setEditable(true);
        this.setFocusTraversable(true);
        if ( defaultValue != null ) {
            property.setValue(defaultValue.toString());
        }        
        valueProperty().bindBidirectional(property);

    }

    @Override
    public void unbind() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 public static class Converter extends StringConverter {
        private final Character defaultValue;
        private final CharacterTextField textField;
        
        public Converter(CharacterTextField textField,Character defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;
        }
        
        @Override
        public String toString(Object dv) {
            if ( dv == null  && defaultValue == null) {
                textField.setValue("");
                return "";
            } else if ( dv == null  && defaultValue != null) {
                textField.setValue(defaultValue.toString());
                return "";
            } else if ( dv == "" && defaultValue == null ) {
                textField.setValue("");
                return "";
            } else if ( dv == "") {
                textField.setValue("");
                return "";
            } else {
                String c = dv == null || dv.toString().isEmpty() ? "" : dv.toString().substring(0,1);
                textField.setValue(c);
                return c;
            }
        }
        @Override
        public String fromString(String tx) {
            String s = tx == null || tx.isEmpty() ? "" : tx.substring(0,1);
            textField.setValue(s);
            return s;
        }        
    }
        
}
