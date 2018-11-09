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

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Valery Shyshkin
 * @param <T> the type of the propertyValue
 */
public interface PropertyEditor<T> {

    public final String EDITOR_STYLE_CLASS = "editor-a796e7ef-bfda-4255-9a69-598be15d7571";
    public static final String HYPERLINK = "https://docs.oracle.com/javase/8/javafx/api/";

    String getName();

    void bind(ReadOnlyProperty<T> property);

    void bindBidirectional(Property<T> property);

    void unbind();

    boolean isEditable();

    void setEditable(boolean editable);

    boolean isBound();
    
    HyperlinkTitle getTitle();
    
    ReadOnlyProperty<T> getBoundProperty();
}
