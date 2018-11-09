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
package org.vns.javafx.scene.control.editors.beans;

import java.util.Objects;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Olga
 */
@DefaultProperty("items")
public class Category extends AbstractNamedItem implements NamedItemList<Section> {

//    private final StringProperty name = new SimpleStringProperty();
//    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<Section> sections = FXCollections.observableArrayList();
//    private final ReadOnlyObjectWrapper<BeanModel> beanModelWrapper = new ReadOnlyObjectWrapper<>();

    public Category() {
        super();
    }

    public Category(String name) {
        super(name);
    }

    public Category(String name, String displayName) {
        super(name,displayName);
//        this.name.set(name);
//        this.displayName.set(displayName);
        
    }


/*    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
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


*/
    @Override
    public ObservableList<Section> getItems() {
        return sections;
    }

    public Category getCopyFor(Class<?> clazz, BeanModel ppd) {
        Category cat = new Category();
        //cat.setBeanModel(ppd);
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getName());
        hash = 97 * hash + Objects.hashCode(this.getDisplayName());
        return hash;
    }


}
