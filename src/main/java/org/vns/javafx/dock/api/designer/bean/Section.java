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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty("items")
public class Section extends AbstractNamedItem implements NamedItemList<BeanProperty> {

//    private final StringProperty name = new SimpleStringProperty();
//    private final StringProperty displayName = new SimpleStringProperty();
    private final ObservableList<BeanProperty> properties = FXCollections.observableArrayList();
//    private ReadOnlyObjectWrapper<Category> categoryWrapper = new ReadOnlyObjectWrapper<>();

    public Section() {
        super();
    }
    public Section(String name, String displayName) {
        super(name,displayName);

//        this.name.set(name);
//        this.displayName.set(displayName);
   
    }

    public Section(String name) {
        super(name);

//        this.name.set(name);
//        this.displayName.set(displayName);
   
    }
    
    private void init() {
        //!!!!!!!!!!!!!!!properties.addListener(this::propertiesChange);
        
    }

/*    public ReadOnlyObjectProperty<Category> categoryProperty() {
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
*/
/*    private void propertiesChange(ListChangeListener.Change<? extends BeanModel> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(pd -> {
                    //getBeanDescriptor().getPropertiesMap().remove(pd.getName());
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(pd -> {
                    if (getBeanDescriptor() != null) {
                        //getBeanDescriptor().getPropertiesMap().put(pd.getName(), pd);
                    }
                });
            }
        }
    }
*/
/*    public StringProperty nameProperty() {

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
*/
    @Override
    public ObservableList<BeanProperty> getItems() {
        return properties;
    }

    public Section getCopyFor(Class<?> clazz, BeanModel ppd, Category cat) {
        Section sec = new Section();
        sec.setDisplayName(getDisplayName());
        sec.setName(getName());
        for (BeanProperty pd : properties) {
            sec.getItems().add(pd.getCopyFor(clazz, ppd, cat, sec));
        }
        return sec;
    }

}
