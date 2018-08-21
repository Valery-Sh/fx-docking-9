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

import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shishkin
 */
public class BytePropertyEditor extends IntegerPropertyEditor {

    public BytePropertyEditor() {
        this((byte) 0, null, null);
    }

    /**
     *
     * @param defaultValue if null then an empty String value will be shown
     */
    public BytePropertyEditor(Byte defaultValue) {
        this(defaultValue, null, null);
    }

    public BytePropertyEditor(Byte minValue, Byte maxValue) {
        this((byte) 0, minValue, maxValue);
    }

    public BytePropertyEditor(Byte defaultValue, Byte minValue, Byte maxValue) {
        super(defaultValue == null ? null : defaultValue.intValue(),
                minValue == null ? null : minValue.intValue(), maxValue == null ? null : maxValue.intValue());
    }

/*    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
*/
    @Override
    protected boolean isAcceptable(String txt) {
        if (txt == null) {
            return false;
        }
        if (txt.isEmpty() || "-".equals(txt)) {
            return true;
        }

        if (txt.matches(getPattern()) && Long.parseLong(txt) <= Byte.MAX_VALUE && Long.parseLong(txt) >= Byte.MIN_VALUE) {
            System.err.println("Byte: txt = " + txt);
            System.err.println("Byte: Long.parseLong(txt) = " + Long.parseLong(txt));
            System.err.println("Byte: getMinValue = " + getMinValue());
            System.err.println("Byte: getMaxValue = " + getMaxValue());
            if (getMinValue() == null && getMaxValue() == null) {
                return true;
            }
            if (getMinValue() != null && Integer.parseInt(txt) < getMinValue()) {
                return false;
            }
            if (getMaxValue() != null && Integer.parseInt(txt) > getMaxValue()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
