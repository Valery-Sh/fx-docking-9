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

import org.vns.javafx.scene.control.editors.binding.MarginBinding;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Priority;
import org.vns.javafx.MainLookup;

import org.vns.javafx.scene.control.editors.PrimitivePropertyEditor.IntegerPropertyEditor;

/**
 *
 * @author Olga
 */
public abstract class ConstraintPropertyEditorFactory {

    public static ConstraintPropertyEditorFactory getDefault() {
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
     * @param propertyName the name of the property
     * @param beanParentClass the class of the parent node
     * @return the object of type {@code StaticConstraintPropertyEditor }
     */
    public abstract StaticConstraintPropertyEditor getEditor(String propertyName, Class<?> beanParentClass);// {

    //public abstract boolean hasEditor(String propertyName, Class<?>... propertyType);// {

    //public abstract PropertyEditor getEditor(String propertyType, String propertyName);// {    
//        return null;
//    }  
    public static class DefaultEditorFactory extends ConstraintPropertyEditorFactory {

        @Override
        public StaticConstraintPropertyEditor getEditor(String propertyName, Class<?> beanParentClass) {
            List<? extends ConstraintPropertyEditorFactory> list = MainLookup.lookupAll(ConstraintPropertyEditorFactory.class);
            StaticConstraintPropertyEditor retval = null;
            for (ConstraintPropertyEditorFactory f : list) {
                retval = f.getEditor(propertyName, beanParentClass);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }

            if ( "margin".equals(propertyName) && MarginBinding.isSupported(beanParentClass) ) {
                return new InsetsPropertyEditor(propertyName);
            }
            if ( "anchor".equals(propertyName) && MarginBinding.isSupported(beanParentClass) ) {
                return new InsetsPropertyEditor(propertyName);
            }
            if ( "hgrow".equals(propertyName) ) {
                return new EnumPropertyEditor(propertyName,Priority.class);
            }
            
            if ( "vgrow".equals(propertyName) ) {
                return new EnumPropertyEditor(propertyName,Priority.class);
            }
            if ( "alignment".equals(propertyName) ) {
                return new EnumPropertyEditor(propertyName,Pos.class);
            }
            if ( "valignment".equals(propertyName) ) {
                return new EnumPropertyEditor(propertyName,VPos.class);
            }
            if ( "halignment".equals(propertyName) ) {
                return new EnumPropertyEditor(propertyName,HPos.class);
            }
            if ( "rowIndex".equals(propertyName) || "columnIndex".equals(propertyName)) {
                return new IntegerPropertyEditor(propertyName);
            }
            if ( "rowSpan".equals(propertyName) ||"columnSpan".equals(propertyName)) {
                return new GridRowColumnSpanPropertyEditor(propertyName);
            }
            
            return retval;
        }

        public static final ConstraintPropertyEditorFactory getInstance() {
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

        private static class SingletonInstance {

            private static final ConstraintPropertyEditorFactory INSTANCE = new DefaultEditorFactory();
        }
    }
}
