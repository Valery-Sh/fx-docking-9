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

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.vns.javafx.dock.api.bean.BeanAdapter;

/**
 *
 * @author Olga
 */
@DefaultProperty("categories")
public class BeanDescriptor {

    private BeanAdapter adapter;

    //private final ReadOnlyObjectWrapper<Class<?>> beanClassWrapper = new ReadOnlyObjectWrapper<>();
    private final StringProperty type = new SimpleStringProperty();

    private final ObservableList<PropertyDescriptor> propertyDescriptors = FXCollections.observableArrayList();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableMap<String, PropertyDescriptor> propertiesMap = FXCollections.observableHashMap();

    public BeanDescriptor() {
        init();
    }

    private void init() {
        categories.addListener(this::categoriesChange);
    }

    public ObservableMap<String, PropertyDescriptor> getPropertiesMap() {
        return propertiesMap;
    }

    private void categoriesChange(ListChangeListener.Change<? extends Category> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(c -> {
                    c.getSections().clear();
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(c -> {
                    c.setBeanDescriptor(this);
                    c.getSections().forEach(s -> {
                        s.getPropertyDescriptors().forEach(pd -> {
                            propertiesMap.put(pd.getName(), pd);
                        });
                    });
                });
            }
        }
    }

    public Category getCategory(String id) {
        Category retval = null;
        for (Category c : getCategories()) {
            if (c.getId().equals(id)) {
                retval = c;
                break;
            }
        }
        return retval;
    }

    public ObservableList<Category> getCategories() {
        return categories;
    }

    /*    public ObservableList<PropertyDescriptor> getExposedProperties() {
        return exposedProperties;
    }
     */
    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        if (propertyName == null) {
            return null;
        }
        return propertiesMap.get(propertyName);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String beanClassName) {
        this.type.set(beanClassName);
    }

    protected PropertyEditor getPropertyEditor(String className) {
        return null;
    }

    protected PropertyEditor getPropertyEditor(Class<? extends PropertyEditor> editoeClass) {
        return null;
    }

    protected PropertyEditor getPropertyEditor(PropertyDescriptor pd) {
        return null;
    }
}
