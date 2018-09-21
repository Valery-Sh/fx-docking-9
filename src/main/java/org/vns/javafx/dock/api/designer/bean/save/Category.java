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
package org.vns.javafx.dock.api.designer.bean.save;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Olga
 */
@DefaultProperty("items")
public class Category  extends Descriptor<Section> {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    //private final ObservableList<Section> sections = FXCollections.observableArrayList();
    
    private final ReadOnlyObjectWrapper<PropertyPaneDescriptor> propertyPaneDescriptorWrapper = new ReadOnlyObjectWrapper<>();

    public Category() {
        init();
    }

    private void init() {
        //sections.addListener(this::sectionsChange);
    }

    private void sectionsChange(ListChangeListener.Change<? extends Section> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(s -> {
                    s.getItems().clear();
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(s -> {
                    s.setCategory(this);
                    /*                   if (getPropertyPaneDescriptor() != null) {
                        s.getPropertyPaneDescriptors().forEach(pd -> {
                            //getBeanDescriptor().getPropertiesMap().put(pd.getName(), pd);

                        });

                    }
                     */
                });
            }
        }
    }

    public ReadOnlyObjectProperty<PropertyPaneDescriptor> propertyPaneDescriptorProperty() {
        return propertyPaneDescriptorWrapper.getReadOnlyProperty();
    }

    public PropertyPaneDescriptor getPropertyPaneDescriptor() {
        return propertyPaneDescriptorWrapper.getValue();
    }

    protected void setPropertyPaneDescriptor(PropertyPaneDescriptor pd) {
        propertyPaneDescriptorWrapper.setValue(pd);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String id) {
        this.name.set(id);
    }

    public Section getSection(String id) {
        Section retval = null;
        for (Section s : getSections()) {
            if (s.getName().equals(id)) {
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
        return getItems();
    }

    public Category getCopyFor(Class<?> clazz, PropertyPaneDescriptor ppd) {
        Category cat = new Category();
        cat.setPropertyPaneDescriptor(ppd);
        cat.setDisplayName(getDisplayName());
        cat.setName(getName());

        for (Section sec : getItems()) {
            cat.getItems().add(sec.getCopyFor(clazz, ppd, cat));
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retval = true;
        if (obj == null || !(obj instanceof Category)) {
            retval = false;
        }
        if (getName() == null && ((Category) obj).getName() == null) {
            retval = true;
        } else if (!getName().equals(((Category) obj).getName())) {
            retval = false;
        }
        return retval;
    }
    public static int AFTER = 0;
    public static int BEFORE = 2;
    public int indexByName(String sectionName) {
        int retval = -1;
        Section sec = getByName(sectionName);
        if ( sec != null ) {
            retval = getItems().indexOf(sec);
        }
        return retval;
    }
    public Section getByName(String sectionName) {
        Section retval = null;
        for (Section sec : getItems()) {
            if (sec.getName().equals(sectionName)) {
                retval = sec;
                break;
            }
        }
        return retval;
    }

    protected Section addSectionAfter(String secPosName, String sec, String displayName) {
        return addSection(AFTER, secPosName, sec, displayName);
    }

    protected Section addSectionBefore(String secPosName, String sec, String displayName) {
        return addSection(BEFORE, secPosName, sec, displayName);
    }

    protected Section addSection(int pos, String secPosName, String sec, String displayName) {
        int idx = getItems().size();
        for (int i = 0; i < getItems().size(); i++) {
            if (getItems().get(i).getName().equals(secPosName)) {
                idx = i;
                break;
            }
        }
        Section retval = null;
        for (Section s : getItems() ) {
            if (s.getName().equals(sec)) {
                retval = s;
                retval.setName(sec);
                retval.setDisplayName(displayName);
                break;
            }
        }
        if (retval == null) {
            retval = new Section();
            retval.setName(sec);
            retval.setDisplayName(displayName);
            if ( idx >= 0 && pos == BEFORE) {
                getItems().add(idx,retval);
            } else if ( idx >= 0 && pos == AFTER)  {
                getItems().add(idx++,retval);
            } else {
                getItems().add(retval);
            }
            
        }
        return retval;

    }

    protected Section addSection(String sec, String displayName) {
        Section retval = null;
        for (Section s : getItems()) {
            if (s.getName().equals(sec)) {
                retval = s;
                break;
            }
        }
        if (retval == null) {
            retval = new Section();
            retval.setName(sec);
            retval.setDisplayName(displayName);
            getItems().add(retval);
        }
        
        return retval;
    }

}
