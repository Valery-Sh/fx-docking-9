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

import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.BytePropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.CharacterPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.DoublePropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.FloatPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.IntegerPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.LongPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.ShortPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Olga
 */
public abstract class PropertyEditorFactory {

    public static PropertyEditorFactory getDefault() {
        return DefaultEditorFactory.getInstance();
    }

    /**
     *
     * @param propertyType the type of the property
     * @param bean the bean the property belongs to
     * @param propertyName the name of the property
     * @return the new property editor instance for the specified parameters
     */
    //public abstract PropertyEditor getEditor(Class<?> propertyType, Object bean, String propertyName);// {
//        return null;
//    }
    /**
     *
     * @param propertyType the type of the property
     * @param propertyName the name of the property
     * @return the object of type {@code PropertyEditor }
     */
    public abstract PropertyEditor getEditor(String propertyName, Class<?>... propertyType);// {

    public abstract boolean hasEditor(String propertyName, Class<?>... propertyType);// {

    //public abstract PropertyEditor getEditor(String propertyType, String propertyName);// {    
//        return null;
//    }  
    public static class DefaultEditorFactory extends PropertyEditorFactory {

        /*        public PropertyEditor getEditor(Class<?> propertyType, Object bean, String propertyName) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor retval = null;
            for (PropertyEditorFactory f : list) {
                //retval = f.getEditor(propertyType, bean, propertyName);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }

            if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                return new BooleanPropertyEditor();
            } else if (propertyType.equals(Character.class) || propertyType.equals(char.class)) {
                return new CharacterPropertyEditor();
            } else if (propertyType.equals(Byte.class) || propertyType.equals(byte.class)) {
                return new BytePropertyEditor();
            } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                return new ShortPropertyEditor();
            } else if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                return new IntegerPropertyEditor();
            } else if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                return new LongPropertyEditor();
            } else if (propertyType.equals(Float.class) || propertyType.equals(float.class)) {
                return new FloatPropertyEditor();
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))) {
                return new SliderPropertyEditor(0, 1, 1);
            } else if (propertyType.equals(Double.class) || propertyType.equals(double.class)) {
                return new DoublePropertyEditor();
            }  else if ("style".equals(propertyName) && propertyType.equals(String.class)) {
                return new StylePropertyEditor();
            } else if (propertyType.equals(String.class)) {
                return new StringPropertyEditor();
            } else if (propertyType.isEnum()) {
                return new EnumPropertyEditor(propertyType);
            } else if (propertyType.equals(Insets.class)) {
                return new InsetsPropertyEditor();
            } else if (propertyType.equals(Bounds.class)) {
                return new BoundsPropertyEditor();
            }
            return retval;
        }
         */
        @Override
        public PropertyEditor getEditor(String propertyName, Class<?>... propertyTypes) {
            Class<?> propertyType = propertyTypes[0];
            Class<?> genericType = null;
            if (propertyTypes.length >= 2) {
                genericType = propertyTypes[1];
            }
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor retval = null;
            for (PropertyEditorFactory f : list) {
                retval = f.getEditor(propertyName, propertyTypes);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }

            if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                retval = new BooleanPropertyEditor(propertyName);
            } else if (propertyType.equals(Character.class) || propertyType.equals(char.class)) {
                retval = new CharacterPropertyEditor(propertyName);
            } else if (propertyType.equals(Byte.class) || propertyType.equals(byte.class)) {
                retval = new BytePropertyEditor(propertyName);
            } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                retval = new ShortPropertyEditor(propertyName);
            } else if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                retval = new IntegerPropertyEditor(propertyName);
            } else if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                retval = new LongPropertyEditor(propertyName);
            } else if (propertyType.equals(Float.class) || propertyType.equals(float.class)) {
                retval = new FloatPropertyEditor(propertyName);
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))) {
                retval = new SliderPropertyEditor(propertyName,0, 1, 1);
            } else if (propertyType.equals(Double.class) || propertyType.equals(double.class)) {
                retval = new DoublePropertyEditor(propertyName);
            } else if ("style".equals(propertyName) && propertyType.equals(String.class)) {
                retval = new StylePropertyEditor(propertyName);
            } else if (propertyType.equals(String.class)) {
                retval = new StringPropertyEditor(propertyName);
            } else if (propertyType.isEnum()) {
                retval = new EnumPropertyEditor(propertyName,propertyType);
            } else if (propertyType.equals(Insets.class)) {
                retval = new InsetsPropertyEditor(propertyName);
            } else if (propertyType.equals(Bounds.class)) {
                retval = new BoundsPropertyEditor(propertyName);
            } else if (("styleClass".equals(propertyName) && propertyType.equals(ObservableList.class) && String.class.equals(genericType))) {
                retval = new StyleClassPropertyEditor(propertyName);
            } else if (("buttonTypes".equals(propertyName) && propertyType.equals(ObservableList.class) && ButtonType.class.equals(genericType))) {
                retval = new ButtonTypeComboBoxPropertyEditor(propertyName);
            } else if (propertyType.equals(Font.class)) {
                retval = new FontPropertyEditor(propertyName);
                //return new ComboText();
            }
            if ( retval != null ) {
              ((Node)retval).getStyleClass().add("it_is_pe");

            }
            return retval;
        }

        public static final PropertyEditorFactory getInstance() {
            return SingletonInstance.INSTANCE;
        }

        /*        public PropertyEditor getEditor(String propertyType, String propertyName) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor editor = null;
            for (PropertyEditorFactory f : list) {
       //         editor = f.getEditor(propertyType, propertyName);
                if (editor != null) {
                    break;
                }
            }
            if (editor != null) {
                return editor;
            }

            if (propertyType.equals(Boolean.class.getName()) || propertyType.equals(boolean.class.getName())) {
                return new BooleanPropertyEditor();
            } else if (propertyType.equals(Character.class.getName()) || propertyType.equals(char.class.getName())) {
                return new CharacterPropertyEditor();
            } else if (propertyType.equals(Byte.class.getName()) || propertyType.equals(byte.class.getName())) {
                return new BytePropertyEditor();
            } else if (propertyType.equals(Short.class.getName()) || propertyType.equals(short.class.getName())) {
                return new ShortPropertyEditor();
            } else if (propertyType.equals(Integer.class.getName()) || propertyType.equals(int.class.getName())) {
                return new IntegerPropertyEditor();
            } else if (propertyType.equals(Long.class.getName()) || propertyType.equals(long.class.getName())) {
                return new LongPropertyEditor();
            } else if (propertyType.equals(Float.class.getName()) || propertyType.equals(float.class.getName())) {
                return new FloatPropertyEditor();
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class.getName()) || propertyType.equals(double.class.getName())))) {
                return new SliderPropertyEditor(0, 1, 1);
            } else if (propertyType.equals(Double.class.getName()) || propertyType.equals(double.class.getName())) {
                return new DoublePropertyEditor();
            } else if ("style".equals(propertyName) && propertyType.equals(String.class.getName())) {
                return new StylePropertyEditor();
            }  else if (propertyType.equals(String.class.getName())) {
                return new StringPropertyEditor();
            } else if (propertyType.equals(Insets.class.getName())) {
                return new InsetsPropertyEditor();
            } else if (propertyType.equals(Bounds.class.getName())) {
                return new BoundsPropertyEditor();
            } else {
                try {
                    Class clazz = Class.forName(propertyType);
                    if (clazz.isEnum()) {
                        return new EnumPropertyEditor(clazz);
                    }
                } catch (ClassNotFoundException ex) {
                    //Logger.getLogger(PropertyEditorFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return editor;
        }
         */
        @Override
        public boolean hasEditor(String propertyName, Class<?>... propertyTypes) {
            Class<?> propertyType = propertyTypes[0];
            Class<?> genericType = null;
            if (propertyTypes.length >= 2) {
                genericType = propertyTypes[1];
            }
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            boolean retval = false;
            for (PropertyEditorFactory f : list) {
                retval = f.hasEditor(propertyName,propertyTypes);
                if (retval) {
                    break;
                }
            }
            if (retval) {
                return retval;
            }

            if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                retval = true;
            } else if (propertyType.equals(Character.class) || propertyType.equals(char.class)) {
                retval = true;
            } else if (propertyType.equals(Byte.class) || propertyType.equals(byte.class)) {
                retval = true;
            } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                retval = true;
            } else if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                retval = true;
            } else if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                retval = true;
            } else if (propertyType.equals(Float.class) || propertyType.equals(float.class)) {
                retval = true;
            } else if ("opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))) {
                retval = true;
            } else if (propertyType.equals(Double.class) || propertyType.equals(double.class)) {
                retval = true;
            } else if ("style".equals(propertyName) && propertyType.equals(String.class)) {
                retval = true;
            } else if (propertyType.equals(String.class)) {
                retval = true;
            } else if (propertyType.isEnum()) {
                retval = true;
            } else if (propertyType.equals(Insets.class)) {
                retval = true;
            } else if (propertyType.equals(Bounds.class)) {
                retval = true;
            } else if (("styleClass".equals(propertyName) && propertyType.equals(ObservableList.class) && String.class.equals(genericType))) {
                retval = true;
            } else if (("buttonTypes".equals(propertyName) && propertyType.equals(ObservableList.class) && ButtonType.class.equals(genericType))) {
                retval = true;
            } else if (propertyType.equals(Font.class)) {
                retval = true;
            }
            return retval;

        }

        private static class SingletonInstance {

            private static final PropertyEditorFactory INSTANCE = new DefaultEditorFactory();
        }
    }
}
