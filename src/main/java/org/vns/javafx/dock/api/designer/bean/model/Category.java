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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Olga
 */
@DefaultProperty("sections")
public class Category {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<Section> sections = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<PropertyPaneModel> propertyPaneModelWrapper = new ReadOnlyObjectWrapper<>();

    public Category() {
        init();
    }

    private void init() {
    }

    public Category getCopy() {
        Category retval = new Category();
        retval.setId(getId());
        retval.setDisplayName(getDisplayName());
        for ( int i=0; i < sections.size(); i++ ) {
            retval.getSections().add(sections.get(i).getCopy());
        }
        return retval;
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

    public Section getSection(String id) {
        Section retval = null;
        for (Section s : getSections()) {
            if (s.getId().equals(id)) {
                retval = s;
                break;
            }
        }
        return retval;
    }
    public ReadOnlyObjectProperty<PropertyPaneModel> propertyPaneModelProperty() {
        return propertyPaneModelWrapper.getReadOnlyProperty();
    }

    public PropertyPaneModel getPropertyPaneModel() {
        return propertyPaneModelWrapper.getValue();
    }

    protected void setBeanDescriptor(PropertyPaneModel propertyPaneModel) {
        propertyPaneModelWrapper.setValue(propertyPaneModel);
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

    public ObservableList<Section> getSections() {
        return sections;
    }
}
