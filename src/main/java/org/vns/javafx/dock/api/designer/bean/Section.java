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

import java.util.ArrayList;
import java.util.List;
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
public class Section implements NamedItemList<PropertyItem> {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<PropertyItem> properties = FXCollections.observableArrayList();
    private ReadOnlyObjectWrapper<Category> categoryWrapper = new ReadOnlyObjectWrapper<>();

    public Section() {
        init();
    }
    public Section(String name, String displayName) {
        this();
        this.name.set(name);
        this.displayName.set(displayName);
   
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

    public BeanModel getBeanModel() {
        if (getCategory() == null) {
            return null;
        }
        return getCategory().getBeanModel();
    }

    private void propertiesChange(ListChangeListener.Change<? extends BeanModel> change) {
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

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String id) {
        this.name.set(id);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName.get();
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    @Override
    public ObservableList<PropertyItem> getItems() {
        return properties;
    }

    protected void merge(ObservableList<PropertyItem> props) {
        for (PropertyItem p : props) {
            updateBy(p);
        }
    }

    protected void updateBy(PropertyItem prop) {
        int idx = indexByName(prop.getName());
        int pos = NOT_INSERT;
        List<PropertyItem> list = new ArrayList<>();
        if (prop instanceof InsertBefore) {
            pos = BEFORE;
            list = ((InsertBefore) prop).getProperties();
        } else if (prop instanceof InsertAfter) {
            pos = AFTER;
            list = ((InsertAfter) prop).getProperties();
        } else {
            list.add(prop);
        }

        if (idx < 0 || pos == NOT_INSERT) {
            for (PropertyItem c : list) {
                addItem(properties.size() - 1, prop);
            }
        } else if (pos == BEFORE) {
            int size = properties.size();
            for (PropertyItem c : list) {
                addItem(idx, c);
                if (properties.size() > size) {
                    idx++;
                }
                size = properties.size();
            }
        } else if (pos == AFTER) {
            int size = properties.size();
            for (PropertyItem c : list) {
                addItem(idx, c);
                if (properties.size() > size) {
                    ++idx;
                }
                size = properties.size();
            }
        }

    }

/*    public PropertyItem addProperty(int idx, PropertyItem prop) {
        PropertyItem retval = null;
        for (PropertyItem p : properties) {
            if (p.getName().equals(prop.getName())) {
                retval = p;
                break;
            }
        }
        if (retval == null) {
            properties.add(idx, prop);
        } else {
            retval.setDisplayName(prop.getDisplayName());
        }
        return retval;
    }
    public void addProperties(PropertyItem... props) {
        addProperties(properties.size(), props);
    }
    public void addProperties(int idx, PropertyItem... props) {
        PropertyItem retval = null;
        for (PropertyItem prop : properties) {
            for (PropertyItem p : properties) {
                if (p.getName().equals(prop.getName())) {
                    retval = p;
                    break;
                }
            }
            if (retval == null) {
                properties.add(idx, prop);
            } else {
                retval.setDisplayName(prop.getDisplayName());
            }
        }
        //return retval;
    }
*/
    public Section getCopyFor(Class<?> clazz, BeanModel ppd, Category cat) {
        Section sec = new Section();
        sec.setCategory(cat);
        sec.setDisplayName(getDisplayName());
        sec.setName(getName());
        for (PropertyItem pd : properties) {
            sec.getItems().add(pd.getCopyFor(clazz, ppd, cat, sec));
        }
        return sec;
    }


    /*    protected PropertyModel[] add(int pos, String posPropName, String... props) {
        
        PropertyModel[] retval = new PropertyModel[props.length];
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
            PropertyModel pd = new PropertyModel();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(this);
            retval[i] = pd;
 
        }
        if ( idx >= 0 && pos == AFTER) {
            idx++;
            getProperties().addAll(idx, Arrays.asList(retval));
        } else if ( idx >= 0 && pos == BEFORE) {
            getProperties().addAll(idx, Arrays.asList(retval));
        } else {
            getProperties().addAll(retval);
        }
            
        return retval;
    }
     */
    public PropertyItem getByName(String propertyName) {
        PropertyItem retval = null;
        for (PropertyItem pd : properties) {
            if (pd.getName().equals(propertyName)) {
                retval = pd;
                break;
            }
        }
        return retval;
    }

    public int indexByName(String propertyName) {
        int retval = -1;
        PropertyItem p = getByName(propertyName);
        if (p != null) {
            retval = properties.indexOf(p);
        }
        return retval;
    }
    /*    protected void add(String... props) {
//        PropertyModel[] retval = new PropertyModel[props.length];

        for (int i = 0; i < props.length; i++) {
            String name;
            String displayName = null;
            if (!props[i].contains(":")) {
                name = props[i];
            } else {
                name = props[i].substring(0, props[i].indexOf(":"));
                displayName = props[i].substring(props[i].indexOf(":") + 1);

            }
            for (PropertyModel pd : properties) {
                if (pd.getName().equals(name)) {
                    continue;
                }
            }
            PropertyModel pd = new PropertyModel();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(this);
            //retval[i] = pd;
            //System.err.println("PD name = " + name);
            getProperties().add(pd);
        }
        //return retval;
    }
     */

    @Override
    public void mergeChilds(PropertyItem prop) {
    }
}
