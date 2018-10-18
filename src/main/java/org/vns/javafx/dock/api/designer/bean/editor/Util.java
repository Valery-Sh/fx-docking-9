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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import org.vns.javafx.dock.api.designer.BrowserService;

/**
 *
 * @author Nastia
 */
public class Util {

    public static final String CAMELCASE_OR_UNDERSCORE
            = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    public static String toDisplayName(String string, String... except) {
        StringBuilder sb = new StringBuilder();
        String[] split = string.split(CAMELCASE_OR_UNDERSCORE);
        for (String word : split) {
            if (!word.isEmpty()) {
                if (sb.length() == 2) {
                    sb.deleteCharAt(1);
                    if (Character.isLowerCase(string.charAt(0))) {
                        sb.deleteCharAt(0);
                        sb.append(string.charAt(0));
                    }
                    sb.append(word)
                            .append(' ');
                } else if (word.length() == 1) {
                    if (sb.length() != 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append(word.toUpperCase())
                            .append(' ');
                } else {
                    sb.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1))
                            .append(' ');
                }
            }
        }

        return sb.toString().trim();
    }

    public static void showInBrowser(PropertyEditor editor) {
        if (editor == null || editor.getBoundProperty() == null) {
            return;
        }
        ReadOnlyProperty boundProperty = editor.getBoundProperty();
        if (boundProperty == null || boundProperty.getBean() == null) {
            return;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(boundProperty.getBean().getClass());
            Method method = null;
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (boundProperty.getName().equals(pd.getName())) {
                    method = pd.getReadMethod();
                    break;
                }
            }
            if (method == null) {
                return;
            }
            String rdmethod = method.getName();
            String origin = boundProperty.getBean().getClass().getName();
            Class objClass = boundProperty.getBean().getClass();
            while (!Object.class.equals(objClass)) {
                try {
                    Method m = objClass.getMethod(rdmethod, new Class[0]);
                    if (Modifier.isPublic(m.getModifiers())) {
                        origin = objClass.getName();
                    }
                    objClass = objClass.getSuperclass();
                } catch (NoSuchMethodException | SecurityException ex) {
                    break;
                }
            }
            origin = origin.replace('.', '/');
            BrowserService.getInstance().showDocument(PropertyEditor.HYPERLINK + origin + ".html#" + rdmethod + "--");
        } catch (IntrospectionException ex) {
            Logger.getLogger(AbstractPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<String> getFontStyles(String family, double size) {
        List<String> styles = new ArrayList<>(0);
        for (FontWeight w : FontWeight.values()) {
            for (FontPosture p : FontPosture.values()) {
                Font f = Font.font(family, w, p, size);
                if (family.equals(f.getFamily()) && size == f.getSize() && !styles.contains(f.getStyle())) {
                    styles.add(f.getStyle());
                }
            }
        }
        return styles;
    }

    public static Font getFont(String family, String style, double size) {
        Font font = Font.font(family, size);
        if (!family.equals(font.getFamily())) {
            font = Font.font(size);
        }
        for (FontWeight w : FontWeight.values()) {

            for (FontPosture p : FontPosture.values()) {
                Font f = Font.font(family, w, p, size);
                if (family.equals(f.getFamily()) && size == f.getSize() && style.equals(f.getStyle())) {
                    font = f;
                    break;
                }
            }
        }
        return font;
    }

    
    public static Pair<FontWeight, FontPosture> getFontStyle(String family, String style, double size) {
        Pair<FontWeight, FontPosture> retval = null;
        for (FontWeight w : FontWeight.values()) {

            for (FontPosture p : FontPosture.values()) {

                Font f = Font.font(family, w, p, size);
                if (!style.equals(f.getStyle())) {
                    retval = new Pair<FontWeight, FontPosture>(w, p);
                    System.err.println("WEIGHT = " + w);
                    System.err.println("POSTURE = " + p);
                    break;
                }
            }
            if (retval != null) {
                break;
            }
        }
        return retval;
    }

    public static Pair<FontWeight, FontPosture> getFontStyle(Font font) {
        return getFontStyle(font.getFamily(), font.getStyle(), font.getSize());
    }
    /**
     * Tests whether the string specified by the first parameter starts with 
     * the string specified by the second parameter.
     * 
     * @param word the string to be tested against the second parameter
     * @param substr the testing value
     * @return true if the test successful. false otherwise
     */
    public static boolean startsWith(String word, String substr) {
        if ( word == null && substr == null ) {
            return true;
        }
        if ( word == null || substr == null ) {
            return false;
        }
        if ( word.startsWith(substr) ) {
            return true;
        }
        return false;
    }
}
