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
package org.vns.javafx.dock.api.designer.bean.model;

import org.vns.javafx.dock.api.designer.bean.*;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Olga
 */
@DefaultProperty("propertyDescriptors")
public class Section {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<PropertyGroup> propertyGroups = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<Category> categoryWrapper = new ReadOnlyObjectWrapper<>();

    public Section() {
        init();
    }

    private void init() {
        //     propertyGroups.addListener(this::propertiesChange);
    }

    public Section getCopy() {
        Section retval = new Section();
        retval.setId(getId());
        retval.setDisplayName(getDisplayName());
        for (int i = 0; i < propertyGroups.size(); i++) {
            retval.getPropertyGroups().add(propertyGroups.get(i).getCopy());
        }
        return retval;
    }

    public ReadOnlyObjectProperty<Category> categoryProperty() {
        return categoryWrapper.getReadOnlyProperty();
    }

    public Category getCategory() {
        return categoryWrapper.getValue();
    }

    protected void setCategory(Category category) {
        categoryWrapper.setValue(category);
    }

    public PropertyPaneModel getPropertyPaneModel() {
        if (getCategory() == null) {
            return null;
        }
        return getCategory().getPropertyPaneModel();
    }

    private void propertiesChange(ListChangeListener.Change<? extends PropertyPaneModel> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(pd -> {
                    //getBeanDescriptor().getPropertiesMap().remove(pd.getName());
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(pd -> {
                    //pd.setSection(this);
                    //if (getPropertyPaneModel() != null) {
                    //    getPropertyPaneModel().getPropertiesMap().put(pd.getName(), pd);
                    //}
                });
            }
        }
    }

    public StringProperty idProperty() {

        return id;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public ObservableList<PropertyGroup> getPropertyGroups() {
        return propertyGroups;
    }

}
