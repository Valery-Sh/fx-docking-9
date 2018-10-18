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

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;


/**
 *
 * @author Valery Shyshkin
 * @param <T> the type of the propertyValue
 */
public interface ListPropertyEditor<T> extends PropertyEditor<T>{
    void bind(ObservableList<T> property);
    void bindBidirectional(ObservableList<T> property);
    
    @Override
    default void bind(ReadOnlyProperty<T> property) {
        if ( ! (property instanceof ListProperty) ) {
            return;
        }
        bind( ((ListProperty<T>)property).get());
    }

    
    @Override
    default void bindBidirectional(Property<T> property) {
        if ( ! (property instanceof ListProperty) ) {
            return;
        }
        bindBidirectional(((ListProperty<T>)property).get());

    }
    
    void unbind();
    boolean isEditable();
    void setEditable(boolean editable);
    boolean isBound();
}
