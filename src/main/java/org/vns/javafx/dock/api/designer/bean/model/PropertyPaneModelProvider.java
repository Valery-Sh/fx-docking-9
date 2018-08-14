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
package org.vns.javafx.dock.api.designer.bean.model;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditorFactory;

/**
 *
 * @author Valery Shyshkin
 */
public class PropertyPaneModelProvider {

    /**
     * key - the fully qualified name of the bean class value an instance of the
     * class PropertyPaneModel
     */
    private ObservableMap<String, PropertyPaneModel> models = FXCollections.observableHashMap();

    private PropertyPaneModelProvider() {
        init();
    }

    private void init() {
        loadDefaultModels();
    }

    public static PropertyPaneModelProvider getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private void loadDefaultModels() {

    }

    public void addModel(String clazz, PropertyPaneModel model) {

    }

    public void removeModels(String... classes) {

    }

    public ObservableMap<String, PropertyPaneModel> getModels() {
        return models;
    }

    public PropertyPaneModel getModel(Class<?> beanClass) {
        PropertyPaneModel model = models.get(beanClass.getName());
        if (model != null) {
            return model;
        }
        Class superClass = beanClass.getSuperclass();
        while (!superClass.equals(Object.class)) {
            if (models.get(superClass.getName()) != null) {
                model = models.get(superClass.getName());
                break;
            }
            superClass = superClass.getSuperclass();
        }
        if (model != null) {
            model = model.getCopy();
        } else {
            model = new PropertyPaneModel();
        }
        List<String> names = new ArrayList<>();
        model.getCategories().forEach(c -> {
            c.getSections().forEach(s -> {
                s.getPropertyGroups().forEach(g -> {
                    g.getModelProperties().forEach(p -> {
                        names.add(p.getName());
                    });
                });
            });
        });
        ObservableList<ModelProperty> props = FXCollections.observableArrayList();
        try {
            PropertyDescriptor[] pds
                    = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
            //BeanAdapter ba = 
            for (PropertyDescriptor pd : pds) {
                if (names.contains(pd.getName())) {
                    continue;
                }
                if ( pd.getPropertyType().equals(ObservableList.class)) {
                    Type type = pd.getReadMethod().getGenericReturnType();
                    Type listType = BeanAdapter.getGenericListItemType(type);
                    System.err.println("pd.getClass=" + pd.getPropertyType().getSimpleName() + "; pd.getName=" + pd.getName() + "; type = " + listType.getTypeName() );                    
                }
                TypeVariable[] tv = pd.getPropertyType().getTypeParameters();
                if ( tv.length != 0) {
                    //System.err.println("pd.getClass=" + pd.getPropertyType().getSimpleName() + "; pd.getName=" + pd.getName() + "; typeVar = " + tv[0]);                    
                    //System.err.println("  --- gendecl = " + tv[0].getGenericDeclaration());                    
                }
                //System.err.println("pd.getClass=" + pd.getPropertyType().getSimpleName() + "; pd.getName=" + pd.getName() + "; typeName" + pd.getPropertyType().getTypeName());
                //if (BeanAdapter.getJavaFxPropertyMethod(beanClass, pd.getName()) == null) {
                //    System.err.println("PD.GETNAME = " + pd.getName() + "; writeMethod = " + pd.getWriteMethod());
                //}
                boolean javaFxKind = BeanAdapter.getJavaFxPropertyMethod(beanClass, pd.getName()) == null;
                boolean hasWriteMethod = pd.getWriteMethod() != null;
                if (javaFxKind && hasWriteMethod) {
//                    System.err.println("   --- PD.GETNAME = " + pd.getName());
                    continue;
                } 
                PropertyEditor editor = PropertyEditorFactory.getDefault().getEditor(pd.getPropertyType(), pd.getName());
                if (editor == null) {
                    continue;
                }
                ModelProperty p = new ModelProperty();
                p.setName(pd.getName());
                p.setDisplayName(pd.getName());
                p.setEditorClass(editor.getClass().getName());
                p.setReadOnly( ! hasWriteMethod );
                p.setJavaFxKind(javaFxKind);
                props.add(p);
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(PropertyPaneModelProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!props.isEmpty()) {
            Category c = new Category();
            Section s = new Section();
            PropertyGroup g = new PropertyGroup();
            c.setId("specific");
            c.setDisplayName("Specific");
            s.setId("all");
            s.setDisplayName("All");
            g.setId("all_specific");
            g.setDisplayName("All Specific");
            s.getPropertyGroups().add(g);
            for (ModelProperty mp : props) {
                g.getModelProperties().add(mp);
            }
            c.getSections().add(s);

            model.getCategories().add(c);
        }
        getModels().put(beanClass.getName(), model);
        return model;
    }

    private static class SingletonInstance {

        private static final PropertyPaneModelProvider INSTANCE = new PropertyPaneModelProvider();
    }//class SingletonInstance

}//class PropertyPaneModelProvider
