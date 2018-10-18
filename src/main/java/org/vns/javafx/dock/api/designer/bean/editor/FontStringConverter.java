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

import javafx.scene.text.Font;
import javafx.util.StringConverter;

/**
 *
 * @author Valery Shyshkin
 */
public class FontStringConverter extends StringConverter<Font> {

    @Override
    public String toString(Font font) {

        long sz = Math.round(font.getSize());
        String str = font.getFamily() + " " + Long.toString(sz) + "px ";
        if (font.getStyle() != null && !font.getStyle().isEmpty() && !"Regular".equals(font.getStyle())) {
            str += "(" + font.getStyle() + ")";
        }
        return str;
    }

    @Override
    public Font fromString(String str) {

        Font font;
        str = str.trim();
        String style;
        String family;
        double size;

        int idx = str.indexOf("(");
        if (idx < 0) {
            style = "Regular";
            str = str.substring(0, str.length() - 2).trim();
        } else {
            style = str.substring(idx + 1, str.length() - 1).trim();
            str = str.substring(0, idx).trim();
            str = str.substring(0, str.length() - 2);

        }
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        idx = str.length() - 1;
        char c = chars[idx];
        while (Character.isDigit(c)) {
            sb.insert(0, c);
            c = chars[--idx];
        }
        size = Double.valueOf(sb.toString());
        family = str.substring(0, idx).trim();
        font = Util.getFont(family, style, size);

        return font;
    }

    public static class FamilyStringConverter extends StringConverter<Font> {

        private final FontPropertyEditor editor;

        public FamilyStringConverter(FontPropertyEditor editor) {
            this.editor = editor;
        }

        @Override
        public String toString(Font font) {
            return font.getFamily();
        }

        @Override
        public Font fromString(String family) {
            //
            // New font family. We check whether the existing style is actual.
            //
            Font retval;

            //String style = editor.getFontStyle().getTextField().getText();
            //double size = editor.getSize().getDecimalEditor().getValue();
            String style = ((Font)editor.getBoundProperty().getValue()).getStyle();
            double size = ((Font)editor.getBoundProperty().getValue()).getSize();
            return Util.getFont(family, style, size);
            
            //if (font.getFamily().equals(family) && font.getStyle().equals(editor.getFontStyle().getTextField().getText()) && font.getSize() == editor.getSize().getDecimalEditor().getValue()) {
            //if (font.getFamily().equals(family) && font.getStyle().equals(editor.getFontStyle().getTextField().getText()) && String.valueOf(font.getSize()).equals(editor.getSize().getTextField().getText()) ) {            
/*            if (font.getFamily().equals(family) && font.getStyle().equals(editor.getFontStyle().getTextField().getText()) && String.valueOf(font.getSize()).equals(size) ) {                        
                retval = font;
            } else {
                font = Font.font(family, size);
                if (font.getFamily().equals(family)) {
                    retval = font;
                }
                retval = Font.font(family);
            }
            return retval;
*/
        }

    }//FamilyStringConverter

    public static class StyleStringConverter extends StringConverter<Font> {

        private final FontPropertyEditor editor;

        public StyleStringConverter(FontPropertyEditor editor) {
            this.editor = editor;
        }

        @Override
        public String toString(Font font) {
            return font.getStyle();
        }

        @Override
        public Font fromString(String style) {
            //
            // New font family. We check whether the existing style is actual.
            //
            Font retval;

//            String family = editor.getFamily().getTextField().getText();
//            double size = editor.getSize().getDecimalEditor().getValue();
            String family = ((Font)editor.getBoundProperty().getValue()).getFamily();
            double size = ((Font)editor.getBoundProperty().getValue()).getSize();

            return Util.getFont(family, style, size);
/*            if (font.getFamily().equals(family) && font.getStyle().equals(editor.getFontStyle().getTextField().getText())) {
                retval = font;
            } else {
                //
                // In PropertyEditorPane it's not posible
                //

                font = Font.font(family, size);
                if (font.getFamily().equals(family)) {
                    retval = font;
                }
                retval = Font.font(family);
/
            }
            return retval;
*/
        }
    }//StyleStringConverter

    public static class SizeStringConverter extends StringConverter<Font> {

        private final FontPropertyEditor editor;

        public SizeStringConverter(FontPropertyEditor editor) {
            this.editor = editor;
        }

        @Override
        public String toString(Font font) {
            return String.valueOf(font.getSize());
        }

        @Override
        public Font fromString(String sz) {
            //
            // New font family. We check whether the existing style is actual.
            //
            Font retval;

            String family = ((Font)editor.getBoundProperty().getValue()).getFamily();
            String style = ((Font)editor.getBoundProperty().getValue()).getStyle();
            
            double size = Double.valueOf(sz);

            Font font = Util.getFont(family, style, size);
            if (font.getFamily().equals(family) && font.getStyle().equals(editor.getFontStyle().getTextField().getText())) {
                retval = font;
            } else {
                //
                // In PropertyEditorPane it's not posible
                //

                font = Font.font(family, size);
                if (font.getFamily().equals(family)) {
                    retval = font;
                }
                retval = Font.font(family);
            }
            return retval;
        }
    }//SizeStringConverter
}//FontStringConverter
