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
@DefaultProperty("modelProperties")
public class PropertyGroup {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<ModelProperty> modelProperties = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<Section> sectionWrapper = new ReadOnlyObjectWrapper<>();

    public PropertyGroup() {
        init();
    }

    private void init() {
        //     properties.addListener(this::propertiesChange);
    }

    public PropertyGroup getCopy() {
        PropertyGroup retval = new PropertyGroup();
        retval.setId(getId());
        retval.setDisplayName(getDisplayName());
        for (int i = 0; i < modelProperties.size(); i++) {
            retval.modelProperties.add(modelProperties.get(i).getCopy());
        }
        return retval;
    }

    public ReadOnlyObjectProperty<Section> sectionProperty() {
        return sectionWrapper.getReadOnlyProperty();
    }

    public Section getSection() {
        return sectionWrapper.getValue();
    }

    protected void setSection(Section section) {
        sectionWrapper.setValue(section);
    }

    public PropertyPaneModel getPropertyPaneModel() {
        if (getSection() == null) {
            return null;
        }
        return getSection().getPropertyPaneModel();
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

    public ObservableList<ModelProperty> getModelProperties() {
        return modelProperties;
    }

}
