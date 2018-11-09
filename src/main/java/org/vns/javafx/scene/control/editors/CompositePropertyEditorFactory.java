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

import java.util.List;
import org.vns.javafx.MainLookup;

/**
 *
 * @author Olga
 */
public abstract class CompositePropertyEditorFactory {

    public static CompositePropertyEditorFactory getDefault() {
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
    public abstract PropertyEditor getEditor(String propertyName, Class<?> propertyClass,Class<?> beanParentClass);// {

 
    public static class DefaultEditorFactory extends CompositePropertyEditorFactory {

        @Override
        public PropertyEditor getEditor(String propertyName, Class<?> propertyClass,Class<?> beanParentClass) {
            List<? extends CompositePropertyEditorFactory> list = MainLookup.lookupAll(CompositePropertyEditorFactory.class);
            PropertyEditor retval = null;
            for (CompositePropertyEditorFactory f : list) {
                retval = f.getEditor(propertyName, propertyClass, beanParentClass);
                if (retval != null) {
                    break;
                }
            }
            if (retval != null) {
                return retval;
            }
            return EffectPropertyHelper.createTreePaneItem(propertyName);
        }

        public static final CompositePropertyEditorFactory getInstance() {
            return SingletonInstance.INSTANCE;
        }


        private static class SingletonInstance {

            private static final CompositePropertyEditorFactory INSTANCE = new DefaultEditorFactory();
        }
    }
}
