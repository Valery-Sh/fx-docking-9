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
package org.vns.javafx.dock.api.designer.bean;

import java.util.List;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Olga
 */
public class PropertyEditorFactory {

    public static PropertyEditorFactory getDefault() {
        return DefaultEditorFactory.getInstance();
    }
    /**
     * 
     * @param propertyType the type of the property
     * @param bean the bean the property belongs to
     * @return the new property editor instance for the specified parameters
     */
    protected PropertyEditor getEditor(Class<?> propertyType, Object bean, String propertyName ) {
        return null;
    }
    
    
    public static class DefaultEditorFactory extends PropertyEditorFactory {
        
        @Override
        public PropertyEditor getEditor(Class<?> propertyType,Object bean,String propertyName ) {
            List<? extends PropertyEditorFactory> list = DesignerLookup.lookupAll(PropertyEditorFactory.class);
            PropertyEditor retval = null;
            for ( PropertyEditorFactory f : list) {
                retval = f.getEditor(propertyType, bean, propertyName);
                if ( retval != null) {
                    break;
                }
            }
            if ( retval != null) {
                return retval;
            }
            
            if ( propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)  ) {
                return new BooleanPropertyEditor();
            } else if ( propertyType.equals(Character.class) || propertyType.equals(char.class)  ) {
                 return new CharacterTextField();
            } else if ( propertyType.equals(Byte.class) || propertyType.equals(byte.class)  ) {
                return new ByteTextField();
            } else if ( propertyType.equals(Short.class) || propertyType.equals(short.class)  ) {
                return new ShortTextField();
            } else if ( propertyType.equals(Integer.class) || propertyType.equals(int.class)  ) {
                return new IntegerTextField();
            } else if ( propertyType.equals(Long.class) || propertyType.equals(long.class)  ) {
                return new LongTextField();
            } else if ( propertyType.equals(Float.class) || propertyType.equals(float.class)  ) {
                return new FloatTextField();
            } else if ( "opacity".equals(propertyName) && ((propertyType.equals(Double.class) || propertyType.equals(double.class)))  ) {
                return new SliderEditor(0,1,1);
            }
            else if ( propertyType.equals(Double.class) || propertyType.equals(double.class)  ) {
                return new DoubleTextField();
            } else if ( propertyType.equals(String.class)) {
                return new StringTextField();
            } else if ( propertyType.isEnum()) {
                return new EnumPropertyEditor(propertyType);
            }
            return retval;
        }
        
        public static final PropertyEditorFactory getInstance() {
            return SingletonInstance.INSTANCE;
        }


        private static class SingletonInstance {
            private static final PropertyEditorFactory INSTANCE = new DefaultEditorFactory();
        }
    }
}
