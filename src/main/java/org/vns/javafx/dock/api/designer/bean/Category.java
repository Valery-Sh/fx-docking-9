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
@DefaultProperty("sections")
public class Category {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<Section> sections = FXCollections.observableArrayList();
    private ReadOnlyObjectWrapper<BeanDescriptor> beanDescriptorWrapper = new ReadOnlyObjectWrapper<>();

    public Category() {
        init();
    }

    private void init() {
        sections.addListener(this::sectionsChange);
    }

    private void sectionsChange(ListChangeListener.Change<? extends Section> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(s -> {
                    s.getPropertyDescriptors().clear();
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(s -> {
                    s.setCategory(this);
                    if (getBeanDescriptor() != null) {
                        s.getPropertyDescriptors().forEach(pd -> {
                            getBeanDescriptor().getPropertiesMap().put(pd.getName(), pd);
                        });

                    }

                });
            }
        }
    }

    public ReadOnlyObjectProperty<BeanDescriptor> beanDescriptorProperty() {
        return beanDescriptorWrapper.getReadOnlyProperty();
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptorWrapper.getValue();
    }

    protected void setBeanDescriptor(BeanDescriptor beanDescriptor) {
        beanDescriptorWrapper.setValue(beanDescriptor);
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
