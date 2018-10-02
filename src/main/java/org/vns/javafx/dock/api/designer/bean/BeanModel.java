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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
@DefaultProperty("items")
public class BeanModel extends AbstractNamedItem implements NamedItemList<Category> {

    private Class<?> beanType;
    private String beanClassName;
    private Object bean;
//    private String name;
//    private String displayName;    
    private final ObservableList<Category> categories = FXCollections.observableArrayList();

/*    public int indexByName(String categoryName) {
        int retval = -1;
        Category p = getByName(categoryName);
        if (p != null) {
            retval = categories.indexOf(p);
        }
        return retval;
    }

    public Category getByName(String categoryName) {
        Category retval = null;
        for (Category pd : categories) {
            if (pd.getName().equals(categoryName)) {
                retval = pd;
                break;
            }
        }
        return retval;
    }
*/
/*    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
*/
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

    @Override
    public ObservableList<Category> getItems() {
        return categories;
    }

    public BeanModel getCopyFor(Class<?> clazz) {
        BeanModel ppd = new BeanModel();
        for (Category c : categories) {
            ppd.getItems().add(c.getCopyFor(clazz, ppd));
        }
        return ppd;
    }

/*    protected void merge(ObservableList<Category> cts) {
        for (Category c : cts) {
            updateBy(c);
        }
    }
*/
    protected Category addCategory(String cat, String displayName) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat)) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            retval = new Category(cat, displayName);
            //retval.setName(cat);
            //retval.setDisplayName(displayName);
            categories.add(retval);
        }

        return retval;
    }

/*    private void addCategories(int idx, Category... cts) {
        Category retval = null;
        for (Category cat : cts) {

            for (Category c : categories) {
                if (c.getName().equals(cat.getName())) {
                    retval = c;
                    break;
                }
            }
            if (retval == null) {
                categories.add(idx, cat);
                retval = cat;
                ObservableList<Section> scs = FXCollections.observableArrayList();
                scs.addAll(cat.getItems());
                cat.getItems().clear();
                cat.merge(scs);
            } else {
                retval.setDisplayName(cat.getDisplayName());
            }
        }
        
    }

    private Category addCategory(int idx, Category cat) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat.getName())) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            categories.add(idx, cat);
            retval = cat;
            ObservableList<Section> scs = FXCollections.observableArrayList();
            scs.addAll(cat.getItems());
            cat.getItems().clear();
            cat.merge(scs);
        } else {
            retval.setDisplayName(cat.getDisplayName());
        }
        return retval;
    }
*/
    /*    protected Category addCategoryBefore(String beforeName, Category cat) {
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
     */
 /*    protected Category addCategoryAfter(String afterName, Category cat) {
        int idx = indexByName(afterName);
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
     */
/*    protected void updateBy(Category cat) {
        int idx = indexByName(cat.getName());
        int pos = NOT_INSERT;
        List<Category> list = new ArrayList<>();
        if (cat instanceof InsertCategoriesBefore) {
            if ( idx < 0 && cat.getName() != null) {
                return;
            } else if ( idx < 0 ) {
                idx = 0;
            }
            pos = BEFORE;
            list = ((InsertCategoriesBefore) cat).getCategories();
        } else if (cat instanceof InsertCategoriesAfter) {
            if ( idx < 0 && cat.getName() != null) {
                return;
            } else if ( idx < 0 ) {
                idx = categories.size();
            } else {
                idx++;
            }            
            pos = AFTER;
            list = ((InsertCategoriesAfter) cat).getCategories();
        } else {
            list.add(cat);
        }

        if (idx < 0 || pos == NOT_INSERT) {
            for (Category c : list) {
                addItems(categories.size() - 1, cat);
            }
        } else if (pos == BEFORE) {
            int size = categories.size();
            for (Category c : list) {
                addItems(idx, c);
                if (categories.size() > size) {
                    idx++;
                }
                size = categories.size();
            }
        } else if (pos == AFTER) {
            int size = categories.size();
            for (Category c : list) {
                addItems(idx, c);
                if (categories.size() > size) {
                    ++idx;
                }
                size = categories.size();
            }
        }

    }
*/
/*    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
*/

}
