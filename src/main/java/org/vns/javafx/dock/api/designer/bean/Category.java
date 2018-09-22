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
public class Category implements NamedItemList<Section> {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<Section> sections = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<BeanModel> beanModelWrapper = new ReadOnlyObjectWrapper<>();

    public Category() {
        init();
    }

    public Category(String name, String displayName) {
        this();
        this.name.set(name);
        this.displayName.set(displayName);
        
    }

    private void init() {
        sections.addListener(this::sectionsChange);
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
                    /*                   if (getBeanModel() != null) {
                        s.getPropertyPaneDescriptors().forEach(pd -> {
                            //getBeanDescriptor().getPropertiesMap().put(pd.getName(), pd);

                        });

                    }
                     */
                });
            }
        }
    }

    public ReadOnlyObjectProperty<BeanModel> beanModelProperty() {
        return beanModelWrapper.getReadOnlyProperty();
    }

    public BeanModel getBeanModel() {
        return beanModelWrapper.getValue();
    }

    protected void setBeanModel(BeanModel pd) {
        beanModelWrapper.setValue(pd);
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
        for (Section s : getItems()) {
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

    public ObservableList<Section> getItems() {
        return sections;
    }

    public Category getCopyFor(Class<?> clazz, BeanModel ppd) {
        Category cat = new Category();
        cat.setBeanModel(ppd);
        cat.setDisplayName(getDisplayName());
        cat.setName(getName());

        for (Section sec : sections) {
            cat.getItems().add(sec.getCopyFor(clazz, ppd, cat));
        }
        return cat;
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

    public int indexByName(String sectionName) {
        int retval = -1;
        Section sec = getByName(sectionName);
        if (sec != null) {
            retval = sections.indexOf(sec);
        }
        return retval;
    }

    public Section getByName(String sectionName) {
        Section retval = null;
        for (Section sec : sections) {
            if (sec.getName().equals(sectionName)) {
                retval = sec;
                break;
            }
        }
        return retval;
    }

    protected void merge(ObservableList<Section> secs) {
        for (Section sec : secs) {
            updateBy(sec);
        }
    }

    protected void updateBy(Section sec) {
        int idx = indexByName(sec.getName());
        int pos = NOT_INSERT;
        List<Section> list = new ArrayList<>();
        if (sec instanceof InsertSectionsBefore) {
            pos = BEFORE;
            list = ((InsertSectionsBefore) sec).getSections();
        } else if (sec instanceof InsertSectionsAfter) {
            pos = AFTER;
            list = ((InsertSectionsAfter) sec).getSections();
        } else {
            list.add(sec);
        }

        if (idx < 0 || pos == NOT_INSERT) {
            for (Section c : list) {
                addItem(sections.size(), sec);
            }
        } else if (pos == BEFORE) {
            int size = sections.size();
            for (Section c : list) {
                addItem(idx, c);
                if (sections.size() > size) {
                    idx++;
                }
                size = sections.size();
            }
        } else if (pos == AFTER) {
            int size = sections.size();
            for (Section c : list) {
                addItem(idx, c);
                if (sections.size() > size) {
                    ++idx;
                }
                size = sections.size();
            }
        }
    }

    /*    public void addItemsAfter(Section after, Section... scs) {
        int idx = sections.indexOf(after);
        idx = idx < 0 ? sections.size() : idx + 1;

        for (Section sec : scs) {
            int size = sections.size();
            addItem(idx, sec);
            if ( size != sections.size()) {
                idx++;
            }
        }

    }
    public void addItemsBefore(Section before, Section... scs) {
        int idx = sections.indexOf(before);
        idx = idx < 0 ? sections.size() : idx;

        for (Section sec : scs) {
            int size = sections.size();
            addItem(idx, sec);
            if ( size != sections.size()) {
                idx++;
            }
        }

    }

    public Section addItem(int idx, Section sec) {
        Section retval = null;
        for (Section c : sections) {
            if (c.getName().equals(sec.getName())) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            sections.add(idx, sec);
            retval = sec;
            ObservableList<PropertyItem> props = FXCollections.observableArrayList();
            props.addAll(sec.getItems());
            sec.getItems().clear();
            sec.merge(props);
        } else {
            retval.setDisplayName(sec.getDisplayName());
        }
        return retval;
    }

    public void addItems(Section... scs) {
        addItems(sections.size(), scs);
    }

    public void addItems(int idx, Section... scs) {

        Section retval;
        for (Section sec : scs) {
            retval = null;
            for (Section c : sections) {
                if (c.getName().equals(sec.getName())) {
                    retval = c;
                    break;
                }
            }
            if (retval == null) {
                sections.add(idx++, sec);
                retval = sec;
                ObservableList<PropertyItem> props = FXCollections.observableArrayList();
                props.addAll(sec.getItems());
                sec.getItems().clear();
                sec.merge(props);
            } else {
                retval.setDisplayName(sec.getDisplayName());
            }
        }
        //return retval;
    }
     */
 /*    protected Section addSection(int pos, String secPosName, String sec, String displayName) {
        int idx = sections.size();
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getName().equals(secPosName)) {
                idx = i;
                break;
            }
        }
        Section retval = null;
        for (Section s : sections) {
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
            if (idx >= 0 && pos == BEFORE) {
                sections.add(idx, retval);
            } else if (idx >= 0 && pos == AFTER) {
                sections.add(idx++, retval);
            } else {
                sections.add(retval);
            }

        }
        return retval;

    }
     */
 /*
    protected Section addSection(String sec, String displayName) {
        Section retval = null;
        for (Section s : sections) {
            if (s.getName().equals(sec)) {
                retval = s;
                break;
            }
        }
        if (retval == null) {
            retval = new Section();
            retval.setName(sec);
            retval.setDisplayName(displayName);
            sections.add(retval);
        }

        return retval;
    }
     */
    @Override
    public void mergeChilds(Section sec) {
        ObservableList<PropertyItem> props = FXCollections.observableArrayList();
        props.addAll(sec.getItems());
        sec.getItems().clear();
        sec.merge(props);

    }
}
