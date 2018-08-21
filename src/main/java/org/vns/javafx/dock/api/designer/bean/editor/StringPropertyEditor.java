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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Olga
 */
public class StringPropertyEditor extends PrimitivesPropertyEditor<String>  {
    
    private String defaultValue;
    
    public StringPropertyEditor() {
        this("");
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public StringPropertyEditor(String defaultValue) {
        this.defaultValue = defaultValue;
        init();
    }

   private void init() {
       
        getStyleClass().add("string-text-field");

        valueProperty().addListener((v, ov, nv) -> {
            setText(nv);
        });
        textProperty().addListener((v, ov, nv) -> {
            setValue(nv);
        });
        setText(defaultValue == null ? "" : defaultValue);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void bind(Property property) {
        setEditable(false);
        setFocusTraversable(false);
        valueProperty().bind(property);
    }
 
    @Override
    public void bindBidirectional(Property property) {
        setEditable(true);
        setFocusTraversable(true);
        valueProperty().bindBidirectional(property);
    }

    @Override
    protected boolean isAcceptable(String txt) {
        return true;
    }

    @Override
    protected Property<String> initValueProperty() {
        return new SimpleStringProperty();
    }


        
}