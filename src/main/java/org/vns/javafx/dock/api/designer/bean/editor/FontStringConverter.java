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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/**
 *
 * @author Valery Shyshkin
 */
public class FontStringConverter extends StringConverter<Font>{

    @Override
    public String toString(Font font) {
        
        long sz = Math.round(font.getSize());
        String str = font.getFamily() + " " + Long.toString(sz) + "px ";
        if ( font.getStyle() != null && ! font.getStyle().isEmpty() ) {
            str += "(" + font.getStyle() + ")";
        }
        return str;
    }

    @Override
    public Font fromString(String str) {
        
        Font font = Font.getDefault();
        
        String[] split = str.split(" ");
        int idx = -1;
        
        for ( int i=0; i < split.length; i++) {
            if ( split[i].endsWith("px") && startsWithDigits(split[i].substring(0,split[i].length()-2))) {
                idx = i;
                break;
            }
        }
        double sz = 0;
        
        if ( idx >= 0 ) {
            sz = Integer.parseInt(split[idx].substring(0,split[idx].indexOf("px")) );
        } else {
            return Font.font(str);
        }
        
        if ( idx == 0 && split.length == 1 ) {
            return Font.font(sz);
        } else if ( idx == 1 && split.length == 2 ) {
            return Font.font(split[0],sz);
        }
        String family = split[0];
        String style = "";
        for ( int i = 2; i < split.length; i++) {
            style += split[i];
        }
        
        String post = null;
        style = style.substring(1, style.length() - 1); // remove '(' and ')'
        if ( style.contains("Italic") ) {
            post = "ITALIC";
            style.replace("Italic", "");
        } else if ( style.contains("Regular") ) {
            post = "REGULAR";
            style.replace("Regular", "");
        }
        FontWeight fw = FontWeight.findByName(style.toUpperCase());
        FontPosture fp = FontPosture.findByName(style.toUpperCase());
        return Font.font(family, fw, fp, sz);
    }
    
    private boolean startsWithDigits(String str) {
        char[] chars = str.toCharArray();
        boolean retval = true;
        for ( char c : chars) {
            if ( ! Character.isDigit(c)) {
                retval = false;
                break;
            }
        }
        return retval;
    }
}
