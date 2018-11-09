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

/**
 *
 * @author Olga
 */
public abstract class BeanSpecificPropertyEditorFactory {


    /**
     * @param bean the bean which contains the specified property
     * @param propertyType the type of the property
     * @param propertyName the name of the property
     * @return the object of type {@code PropertyEditor }
     */
    public abstract PropertyEditor getEditor(Object bean,String propertyName, Class<?>... propertyType);// {

    public abstract boolean hasEditor(Object bean,String propertyName, Class<?>... propertyType);// {
}
