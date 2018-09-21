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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
@DefaultProperty("items")
public class PropertyPaneDescriptor extends Descriptor<Descriptor> {

    private Class<?> beanType;
    private String beanClassName;
    private Object bean;
    private String name;
    //private final ObservableList<Category> categories = FXCollections.observableArrayList();


    public int indexByName(String categoryName) {
        int retval = -1;
        Category p = getByName(categoryName);
        if (p != null) {
            retval = getItems().indexOf(p);
        }
        return retval;
    }

    public Category getByName(String categoryName) {
        Category retval = null;
        for (Category pd : getItems()) {
            if (pd.getName().equals(categoryName)) {
                retval = pd;
                break;
            }
        }
        return retval;
    }
    private final ObservableList<Descriptor> descriptors = FXCollections.observableArrayList();

    public ObservableList<Descriptor> getDescriptors() {
        return descriptors;
    }
    
//    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

/*    public ObservableList<Category> getCategories() {
        return categories;
    }
*/
    public PropertyPaneDescriptor getCopyFor(Class<?> clazz) {
        PropertyPaneDescriptor ppd = new PropertyPaneDescriptor();
        for (Category c : getItems()) {
            ppd.getItems().add(c.getCopyFor(clazz, ppd));
        }
        return this;
    }
    protected void merge(PropertyPaneDescriptor mppd) {
        
    }
    protected Category addCategory(String cat, String displayName) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat)) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            retval = new Category();
            retval.setName(cat);
            retval.setDisplayName(displayName);
            categories.add(retval);
        }

        return retval;
    }

    protected Category addCategory(Category cat) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat.getName())) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            categories.add(cat);
        } else {
            retval.setDisplayName(cat.getDisplayName());
        }
        return retval;
    }

    protected Category addCategoryBefore(String beforeName, Category cat) {
        int idx = indexByName(beforeName);
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat.getName())) {
                retval = c;
                break;
            }
        }
        if (retval == null && idx >= 0) {
            categories.add(idx, cat);
            retval = cat;
        } else if (retval == null) {
            categories.add(cat);
            retval = cat;
        } else {
            retval.setDisplayName(cat.getDisplayName());
        }

        return retval;
    }

    protected Category addCategoryAfter(String beforeName, Category cat) {
        int idx = indexByName(beforeName);
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat.getName())) {
                retval = c;
                break;
            }
        }
        if (retval == null && idx >= 0) {
            categories.add(++idx, cat);
            retval = cat;
        } else if (retval == null) {
            categories.add(cat);
            retval = cat;
        } else {
            retval.setDisplayName(cat.getDisplayName());
        }

        return retval;
    }

/*    protected void InsertCategories(Category cat) {
        int idx = -1;
        if (cat instanceof InsertCategoriesBefore) {
            idx = indexByName(cat.getName());
            if (idx < 0) {
                for (Category c : ((InsertCategoriesBefore) cat).getInserted()) {
                    addCategory(c);
                }
            } else {
                String before = cat.getName();
                for (Category c : ((InsertCategoriesBefore) cat).getInserted()) {
                    addCategoryBefore(before, c);
                }
            }
        } else if (cat instanceof InsertCategoriesAfter) {
            idx = indexByName(cat.getName());
            if (idx < 0) {
                for (Category c : ((InsertCategoriesBefore) cat).getInserted()) {
                    addCategory(c);
                }
            } else {
                String after = cat.getName();
                for (Category c : ((InsertCategoriesBefore) cat).getInserted()) {
                    addCategoryBefore(after, c);
                    after = c.getName();
                }
            }
        } else {
            addCategory(cat);
        }

    }
*/

}
