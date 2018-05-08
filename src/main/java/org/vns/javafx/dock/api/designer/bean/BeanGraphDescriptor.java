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

import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditorFactory;
import java.util.Set;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import org.vns.javafx.dock.api.bean.BeanAdapter;

@DefaultProperty("beanDescriptors")
public class BeanGraphDescriptor extends Control {

    private final ObservableList<BeanDescriptor> descriptors = FXCollections.observableArrayList();

    public BeanGraphDescriptor() {
        init();
    }

    private void init() {
        descriptors.addListener(this::descriptorsChange);
    }

    public ObservableList<BeanDescriptor> getBeanDescriptors() {
        return descriptors;
    }

    private void descriptorsChange(Change<? extends BeanDescriptor> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(bd -> {
                    bd.getCategories().clear();
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
    public BeanDescriptor register(Object bean) {
        if (bean == null) {
            return null;
        }
        Class<?> beanClass = bean.getClass();
        if (beanClass == null) {
            return null;
        }

        BeanGraphDescriptor gd = BeanDescriptorRegistry.getGraphDescriptor();
        String className = beanClass.getName();
        System.err.println("BEAN CLASS = " + className + "; beanDescr size()=" + gd.getBeanDescriptors().size());

        BeanDescriptor retval = null;
        for (BeanDescriptor bd : gd.getBeanDescriptors()) {
            // System.err.println("bd class = " + bd.getType());
            if (className.equals(bd.getType())) {
                retval = bd;
                //   System.err.println("   --- bd  = " + bd);

                break;
            }
        }

        if (retval != null) {
            return retval;
        }
        //
        // Try getBeanDescriptor descriptor for one of the super claasses
        //

        Class superClass = beanClass.getSuperclass();

        while (superClass != null && !superClass.equals(Object.class)) {
            System.err.println("superClass = " + superClass.getName());
            retval = getBeanDescriptor(superClass.getName());
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
            BeanDescriptor bd = new BeanDescriptor();
            bd.setType(beanClass.getName());
            Category c = new Category();
            c.setId("properties");
            c.setDisplayName("Properties");
            Section s = new Section();
            s.setId("extras");
            s.setDisplayName("Extras");
            c.getSections().add(s);
            bd.getCategories().add(c);
            getBeanDescriptors().add(bd);
            System.err.println("NAMES size = " + names.size());
            names.forEach(name -> {
                if ( name.equals("nodeOrientation") ) {
                    System.err.println("ba.getType(name) = " + ba.getType(name));
                }
                if (PropertyEditorFactory.getDefault().getEditor(ba.getType(name), bean, name) != null) {
                    System.err.println("   --- has editor prop = " + name + "; " + PropertyEditorFactory.getDefault().getEditor(ba.getType(name), bean,name));
                    PropertyDescriptor pd = new PropertyDescriptor();
                    pd.setName(name);
                    s.getPropertyDescriptors().add(pd);
                    size++;
                }
            });
            retval = bd;
        }
        long start6 = System.currentTimeMillis();
        System.err.println("EXPOSED size = " +size);
        // System.err.println("(start6-start3) = " + (start6 - start3));
        // System.err.println("(start5-start4) = " + (start5 - start4));
        // System.err.println("(start6-start5) = " + (start6 - start5));

        // System.err.println("   --- retval.type = " + retval.getType());
        for (String n : names) {
            //   System.err.println("   --- prop = " + n);
        }
        return retval;
    }

    public BeanDescriptor getBeanDescriptor(Object bean) {
        if (bean == null) {
            return null;
        }
        return getBeanDescriptor(bean.getClass().getName());
    }

    public BeanDescriptor getBeanDescriptor(String className) {
        BeanDescriptor retval = null;
        for (BeanDescriptor bd : getBeanDescriptors()) {
            if (className.equals(bd.getType())) {
                retval = bd;
                break;
            }
        }
        return retval;
    }

    public static boolean contains(String propertyName, ObservableList<PropertyDescriptor> pds) {
        boolean retval = false;
        for (PropertyDescriptor pd : pds) {
            if (propertyName.equals(pd.getName())) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public static ObservableList<PropertyDescriptor> getPropertyDescriptors(BeanDescriptor bd) {
        ObservableList<PropertyDescriptor> retval = FXCollections.observableArrayList();
        for (Category c : bd.getCategories()) {
            for (Section s : c.getSections()) {
                retval.addAll(s.getPropertyDescriptors());
            }

        }
        return retval;
    }

}
