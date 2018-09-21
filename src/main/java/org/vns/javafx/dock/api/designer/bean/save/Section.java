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

import java.util.Arrays;
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
@DefaultProperty("items")
public class Section  extends Descriptor<FXProperty> {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    //private final ObservableList<FXProperty> properties = FXCollections.observableArrayList();
    private ReadOnlyObjectWrapper<Category> categoryWrapper = new ReadOnlyObjectWrapper<>();

    public Section() {
        init();
    }

    private void init() {
        //!!!!!!!!!!!!!!!properties.addListener(this::propertiesChange);
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

    public PropertyPaneDescriptor getPropertyPaneDescriptor() {
        if (getCategory() == null) {
            return null;
        }
        return getCategory().getPropertyPaneDescriptor();
    }

    private void propertiesChange(ListChangeListener.Change<? extends PropertyPaneDescriptor> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(pd -> {
                    //getBeanDescriptor().getPropertiesMap().remove(pd.getName());
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(pd -> {
                    /*!!!!!!!!!                    pd.setSection(this);
                    if (getBeanDescriptor() != null) {
                        //getBeanDescriptor().getPropertiesMap().put(pd.getName(), pd);
                    }
                     */
                });
            }
        }
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

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }


    public Section getCopyFor(Class<?> clazz, PropertyPaneDescriptor ppd, Category cat) {
        Section sec = new Section();
        sec.setCategory(cat);
        sec.setDisplayName(getDisplayName());
        sec.setName(getName());
        for (FXProperty pd : getItems()) {
            sec.getItems().add(pd.getCopyFor(clazz, ppd, cat, sec));
        }
        return this;
    }
    public static int AFTER = 0;
    public static int BEFORE = 2;

    protected FXProperty[] addAfter(String posPropName, String... props) {
        return add(AFTER, posPropName, props);
    }

    protected FXProperty[] addBefore(String posPropName, String... props) {
        return add(BEFORE, posPropName, props);
    }

    protected FXProperty[] add(int pos, String posPropName, String... props) {
        
        FXProperty[] retval = new FXProperty[props.length];
        int idx = indexByName(posPropName);
        
        for (int i = 0; i < props.length; i++) {

            String name;
            String displayName = null;
            if (!props[i].contains(":")) {
                name = props[i];
            } else {
                name = props[i].substring(0, props[i].indexOf(":"));
                displayName = props[i].substring(props[i].indexOf(":") + 1);

            }

            if (getByName(name) != null) {
                continue;
            }
            FXProperty pd = new FXProperty();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(this);
            retval[i] = pd;
 
        }
        if ( idx >= 0 && pos == AFTER) {
            idx++;
            getItems().addAll(idx, Arrays.asList(retval));
        } else if ( idx >= 0 && pos == BEFORE) {
            getItems().addAll(idx, Arrays.asList(retval));
        } else {
            getItems().addAll(retval);
        }
            
        return retval;
    }

    public FXProperty getByName(String propertyName) {
        FXProperty retval = null;
        for (FXProperty pd : getItems()) {
            if (pd.getName().equals(propertyName)) {
                retval = pd;
                break;
            }
        }
        return retval;
    }
    public int indexByName(String propertyName) {
        int retval = -1;
        FXProperty p = getByName(propertyName);
        if ( p != null ) {
            retval = getItems().indexOf(p);
        }
        return retval;
    }
    protected FXProperty[] add(String... props) {
        FXProperty[] retval = new FXProperty[props.length];

        for (int i = 0; i < props.length; i++) {
            String name;
            String displayName = null;
            if (!props[i].contains(":")) {
                name = props[i];
            } else {
                name = props[i].substring(0, props[i].indexOf(":"));
                displayName = props[i].substring(props[i].indexOf(":") + 1);

            }
            for (FXProperty pd : getItems()) {
                if (pd.getName().equals(name)) {
                    continue;
                }
            }
            FXProperty pd = new FXProperty();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(this);
            retval[i] = pd;
            //System.err.println("PD name = " + name);
            getItems().add(pd);
        }
        return retval;
    }
}
