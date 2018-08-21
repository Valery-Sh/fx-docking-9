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

import org.vns.javafx.dock.api.designer.bean.editor.PrimitivesPropertyEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.LongStringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public class LongPropertyEditor extends PrimitivesPropertyEditor.NumberTextField<Long> {

    public LongPropertyEditor() {
        this(0L);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public LongPropertyEditor(Long defaultValue) {
        setDefaultValue(defaultValue);
        init();
    }

    private void init() {
        getStyleClass().add("long-text-field");
        long d = getDefaultValue() == null ? 0 : getDefaultValue();
        Converter c = new Converter(this, getDefaultValue());
        valueProperty().addListener((v, ov, nv) -> {
            if ( ! ((Long)nv == 0 && ("-".equals(getText()) || getText().isEmpty() )) ) {
                setText(c.toString((Long) nv));
            } 
        });
        textProperty().addListener((v, ov, nv) -> {
            if (!"-".equals(nv) && !"".equals(nv)) {
                setValue(c.fromString(nv));
            } else if ( "".equals(nv) || "-".equals(nv)) {
                setValue(0);
            }
        });
        TextFormatter<Long> formatter = new TextFormatter<Long>(c, d, filter);
        setTextFormatter(formatter);
    }

    
    protected String getPattern() {
        return "0|-?([1-9][0-9]*)?";
    }

    @Override
    protected boolean isAcceptable(String txt) {
        if ( txt == null ) {
            return false;
        }
        if ( txt.isEmpty() || "-".equals(txt) ) {
            return true;
        }
        
        boolean retval = false;
        if (txt.matches(getPattern())) {
            retval = true;
        }
        return retval;
    }

    @Override
    protected Property<Number> initValueProperty() {
        return new SimpleLongProperty();
    }

    public static class Converter extends LongStringConverter {

        private final Long defaultValue;
        private final LongPropertyEditor textField;

        public Converter(LongPropertyEditor textField, Long defaultValue) {
            this.textField = textField;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString(Long v) {
            if (v == null && defaultValue == null) {
                return "";
            } else if (v == null && defaultValue != null) {
                return "0";
            } else if (v == 0 && defaultValue == null) {
                return "";
            } else if (v == 0) {
                return "0";
            } else {
                return v.toString();
            }
        }

        @Override
        public Long fromString(String tx) {
            System.err.println("LONG fromString tx = " + tx);
            Long lv = 0L;
            if (!"-".equals(tx.trim()) && !tx.trim().isEmpty()) {
                lv = super.fromString(tx);
            } else if ("-".equals(tx.trim()) || tx.trim().isEmpty()) {
                if ( defaultValue != null ) {
                    textField.setText("0");
                } else if ( "-".equals(tx.trim()) ) {
                    //textField.setText("");
                }
            }
            return lv;
        }
    }

}
