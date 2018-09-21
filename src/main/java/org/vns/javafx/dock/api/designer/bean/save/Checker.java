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

import org.vns.javafx.dock.api.designer.bean.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneDescriptorRegistry.Introspection;
import static org.vns.javafx.dock.api.designer.bean.PropertyPaneDescriptorRegistry.getPropertyPaneDescriptor;

/**
 *
 * @author Valery
 */
public class Checker {

    private final Class<?> beanClass;
    private final PropertyPaneDescriptorRegistry registry;

    public Checker(PropertyPaneDescriptorRegistry registry, Class<?> beanClass) {
        this.registry = registry;
        this.beanClass = beanClass;
    }
    public String createCodeCategory() {
        final StringBuilder code = new StringBuilder();
        code.append("add(");
        getIntrospectionEventPropertyNames().forEach(ev -> {
            code.append('"')
                    .append(ev)
                    .append('"')
                    .append(',');
        });
        if ( code.length() > "add(".length() ) {
            code.deleteCharAt(code.length() - 1);
            code.append(')')
                    .append(';');
        } else {
            code.delete(0, code.length());
        }
        return code.toString();
    }
    public void printIntrospectionCheck() {
        List<String> descrNames = getPropertyNames();
        List<String> intrNames = getIntrospectionPropertyNames();
        System.err.println("Start From Descriptors: not found in introspection");
        System.err.println("----------------------------------------------------");
        descrNames.forEach(name -> {
            if (!intrNames.contains(name)) {
                System.err.println(" name = " + name);
            }
        });
        System.err.println("----------------------------------------------------");
        System.err.println("Start From introspection: not found in fxProperties");
        System.err.println("----------------------------------------------------");
        intrNames.forEach(name -> {
            if (!descrNames.contains(name)) {
                System.err.println(" name = " + name);
            }
        });

    }

    public List<String> getPropertyNames() {
        PropertyPaneDescriptor ppd = registry.getPropertyPaneDescriptor(beanClass);
        ObservableList<String> list = FXCollections.observableArrayList();
        System.err.println("===================================================");

        ppd.getCategories().forEach(cat -> {
            cat.getSections().forEach(sec -> {
                sec.getFXProperties().forEach(pd -> {
                    list.add(pd.getName());
                });
            });
        });
        //System.err.println("Bean Class Name: " + beanClassName + "; size = " + list.size());
        List<String> retval = list.sorted();
//        toPrint.forEach(s -> {
//            System.err.println(s);
//        });
        return retval;

    }
    public void printPropertyDisplayNames() {
        PropertyPaneDescriptor ppd = registry.getPropertyPaneDescriptor(beanClass);
        System.err.println("=== display names ================================================");

        ppd.getCategories().forEach(cat -> {
            cat.getSections().forEach(sec -> {
                sec.getFXProperties().forEach(pd -> {
                    System.err.println(pd.getDisplayName());;
                });
            });
        });

    }

    public List<String> getIntrospectionPropertyNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        Introspection intr = registry.introspect(beanClass);
        intr.getPropertyDescriptors().values().forEach(pd -> {
            list.add(pd.getName());
        });
        List<String> retval = list.sorted();
//        toPrint.forEach(s -> {
//            System.err.println(s);
//        });
        return retval;

    }
    public List<String> getIntrospectionEventPropertyNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        Introspection intr = registry.introspect(beanClass);
        intr.getEventPropertyDescriptors().values().forEach(pd -> {
            list.add(pd.getName());
        });
        List<String> retval = list.sorted();
//        toPrint.forEach(s -> {
//            System.err.println(s);
//        });
        return retval;

    }
    
}
