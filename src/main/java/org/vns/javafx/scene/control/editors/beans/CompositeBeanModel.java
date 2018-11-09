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

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
@DefaultProperty("propertyItems")
public class CompositeBeanModel extends BeanModel {

    public CompositeBeanModel() {
        init();
    }
    Category cat;
    Section sec;
    private void init() {
        cat = new Category("properties");
        getItems().add(cat);
        sec = new Section("specific");
        cat.getItems().add(sec);
        getPropertyItems().addListener((ListChangeListener.Change<?extends BeanProperty> change)  -> {
        while (change.next()) {
            if (change.wasPermutated()) {
            } else if (change.wasUpdated()) {
/*                List<TreePaneItem> list = (List<TreePaneItem>) change.getList().subList(change.getFrom(), change.getTo());
                list.forEach(it -> {
                    it.setParentItem(this);
                });
*/
            } else if (change.wasReplaced()) {
  /*              change.getRemoved().forEach(it -> {
                    TreePane tp = it.getTreePane();
                    if (tp != null) {
                        tp.getTogleGroup().getToggles().remove(it.getTextButton());
                    }
                    it.setParentItem(null);
                });
                change.getAddedSubList().forEach(it -> {
                    it.setParentItem(this);
                    updateMenuButton();
                    int level = it.getLevel();
                    Insets ins = it.getPadding();
                    //it.setPadding(new Insets(ins.getTop(), ins.getRight(), ins.getBottom(), 10));
                    TreePane tp = it.getTreePane();
                    if (tp != null) {
                        tp.getTogleGroup().getToggles().add((Toggle) it.getTextButton());
                    }

                });
*/                
            } else {
              if (change.wasRemoved()) {
  /*                  change.getRemoved().forEach(it -> {
                        it.setParentItem(null);
                        TreePane tp = it.getTreePane();
                        if (tp != null) {
                            tp.getTogleGroup().getToggles().remove((Toggle) it.getTextButton());
                        }
                    });
*/
                } else if (change.wasAdded()) {
                    change.getAddedSubList().forEach(it -> {

                    });
                }
            }
        }
            
        });
    }
    public ObservableList<BeanProperty> getPropertyItems() {
        //return getItems().get(0).getItems().get(0).getItems();
        return sec.getItems();
    }
/*    @Override
    public BeanModel getCopyFor(Class<?> clazz) {
        System.err.println("CompositeBeanModel: clazz = " + clazz.getName());
        BeanModel ppd = new CompositeBeanModel();
        for (Category c : getItems()) {
            ppd.getItems().add(c.getCopyFor(clazz, ppd));
        }
        return ppd;
    }
*/    
}
