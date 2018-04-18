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
package org.vns.javafx.dock.api.demo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.vns.javafx.dock.api.designer.bean.PropertyDescriptor;

/**
 *
 * @author Olga
 */
public class MyBean {

    private ObjectProperty<PropertyDescriptor> propertyDescriptor = new SimpleObjectProperty<>();
    private ObservableList<ObjectProperty<PropertyDescriptor>> list = FXCollections.observableArrayList();

    public ObjectProperty<PropertyDescriptor> propertyDescriptorProperty() {
        return propertyDescriptor;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor.get();
    }

    public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor.set(propertyDescriptor);
    }

}
