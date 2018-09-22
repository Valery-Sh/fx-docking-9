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

import java.util.Set;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import javafx.scene.control.Control;
import org.vns.javafx.dock.api.bean.BeanAdapter;

@DefaultProperty("beanModels")
public class PropertyPaneModel extends Control {

    private final ObservableList<BeanModel> beanModels = FXCollections.observableArrayList();
    
    private Class<?> beanClass;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyPaneModel() {
        init();
    }

    private void init() {
        beanModels.addListener(this::modelsChange);
    }

    public ObservableList<BeanModel> getBeanModels() {
        return beanModels;
    }

    private void modelsChange(Change<? extends BeanModel> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(bd -> {
                });
            }
            if (change.wasAdded()) {

            }
        }
    }

    //
    //
    //
    int size = 0;
    public BeanModel register(Object bean) {
        if (bean == null) {
            return null;
        }
        Class<?> beanClass = bean.getClass();
        if (beanClass == null) {
            return null;
        }

        PropertyPaneModel gd = PropertyPaneModelRegistry.getPropertyPaneModel();
        String className = beanClass.getName();

        BeanModel retval = null;
        for (BeanModel bd : gd.getBeanModels()) {
            if (className.equals(bd.getBeanType())) {
                retval = bd;
                break;
            }
        }

        if (retval != null) {
            return retval;
        }
        //
        // Try getPropertyDescriptor descriptor for one of the super claasses
        //

        Class superClass = beanClass.getSuperclass();

        while (superClass != null && !superClass.equals(Object.class)) {
            retval = PropertyPaneModel.this.getBeanModel(superClass.getName());
            if (retval != null) {
                break;
            }
            superClass = superClass.getSuperclass();
        }
        long start3 = System.currentTimeMillis();
        long start4 = 0;
        long start5 = 0;
        
        Set<String> names = null;
        if (retval != null) {
            start4 = System.currentTimeMillis();
            BeanAdapter ba = new BeanAdapter(bean);
            names = BeanAdapter.getPropertyNames(beanClass, superClass);
            //names = BeanAdapter.getPropertyNames(beanClass, Object.class);
            start5 = System.currentTimeMillis();
            BeanModel bd = new BeanModel();
            //bd.setType(beanClass.getName());
            Category c = new Category();
            c.setName("properties");
            c.setDisplayName("Properties");
            Section s = new Section();
            s.setName("extras");
            s.setDisplayName("Extras");
            c.getItems().add(s);
//!!!!!!!!!            bd.getCategories().add(c);
            getBeanModels().add(bd);
/*            System.err.println("NAMES size = " + names.size());
            names.forEach(name -> {
                if ( name.equals("nodeOrientation") ) {
                    System.err.println("ba.getType(name) = " + ba.getType(name));
                }
                if (PropertyEditorFactory.getDefault().getEditor(ba.getType(name), bean, name) != null) {
                    System.err.println("   --- has editor prop = " + name + "; " + PropertyEditorFactory.getDefault().getEditor(ba.getType(name), bean,name));
                    PropertyModel pd = new PropertyModel();
                    pd.setName(name);
                    s.getProperties().add(pd);
                    size++;
                }
            });
*/            
            retval = bd;
        }
        long start6 = System.currentTimeMillis();
        //System.err.println("EXPOSED size = " +size);
        // System.err.println("(start6-start3) = " + (start6 - start3));
        // System.err.println("(start5-start4) = " + (start5 - start4));
        // System.err.println("(start6-start5) = " + (start6 - start5));

        // System.err.println("   --- retval.type = " + retval.getType());
        for (String n : names) {
            //   System.err.println("   --- prop = " + n);
        }
        return retval;
    }


    public BeanModel getBeanModel(String className) {
        BeanModel retval = null;
        for (BeanModel bd : getBeanModels()) {
            if (className.equals(bd.getBeanType())) {
                retval = bd;
                break;
            }
        }
        return retval;
    }
    public BeanModel getBeanModel(Class<?> beanClass) {
        BeanModel retval = null;
        for (BeanModel bd : getBeanModels()) {
            if (beanClass.equals(bd.getBeanType())) {
                retval = bd;
                break;
            }
        }
        return retval;
    }

    public static boolean contains(String propertyName, ObservableList<PropertyItem> pds) {
        boolean retval = false;
        for (PropertyItem pd : pds) {
            if (propertyName.equals(pd.getName())) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public static ObservableList<PropertyItem> getPropertyModels(PropertyItem bd) {
        ObservableList<PropertyItem> retval = FXCollections.observableArrayList();
/*!!!!!!!!!!!!!!!        bd.getCategories().forEach((c) -> {
            c.getSections().forEach((s) -> {
                retval.addAll(s.getFXProperties());
            });
        });
*/
        return retval;
    }


}
