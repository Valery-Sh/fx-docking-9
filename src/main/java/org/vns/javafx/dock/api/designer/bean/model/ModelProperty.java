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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.bean.BeanDescriptor;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;

/**
 *
 * @author Valery
 */
public class ModelProperty {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final BooleanProperty javaFxKind = new SimpleBooleanProperty(true);
    private final BooleanProperty readOnly = new SimpleBooleanProperty(false);
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private final StringProperty editorClass = new SimpleStringProperty();
    private final ReadOnlyObjectWrapper<PropertyGroup> propertyGroupWrapper = new ReadOnlyObjectWrapper<>();

    protected Class<? extends PropertyEditor> editorType;

    public ModelProperty() {
        init();
    }

    private void init() {
    }
    public ModelProperty getCopy() {
        ModelProperty retval = new ModelProperty();
        retval.setName(getName());
        retval.setDisplayName(getDisplayName());
        retval.setModifiable(isModifiable());
        retval.setReadOnly(isReadOnly());
        retval.setEditorClass(getEditorClass());
        retval.setJavaFxKind(isJavaFxKind());
        return retval;
    }
    public StringProperty nameProperty() {
        return name;
    }
    
    public BooleanProperty javaFxKindProperty() {
        return javaFxKind;
    }
    public boolean isJavaFxKind() {
        return javaFxKind.get();
    }
    public void setJavaFxKind(boolean kind) {
        javaFxKind.set(kind);
    }
    
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
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
    
    public BooleanProperty readOnlyProperty() {
        return readOnly;
    }
    
    public boolean isReadOnly() {
        return readOnly.get();
    }    
    public void setReadOnly(boolean ro) {
        readOnly.set(ro);
    }    
    
    public boolean isReadOnly(Class<?> beanClass) {
        BeanAdapter ba = new BeanAdapter(beanClass);
        return ba.isReadOnly(getName());
    }

    public boolean isReadOnly(Object bean) {
        BeanAdapter ba = new BeanAdapter(bean);
        return ba.isReadOnly(getName());
    }

    public BooleanProperty modifiableProperty() {
        return modifiable;
    }

    public boolean isModifiable() {
        return modifiable.get();
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable.set(modifiable);
    }

    public StringProperty editorClassProperty() {
        return editorClass;
    }

    public String getEditorClass() {
        return editorClass.get();
    }

    public void setEditorClass(String clazz) {
        editorClass.set(clazz);
    }

    public ReadOnlyObjectProperty<PropertyGroup> propertyGroupProperty() {
        return propertyGroupWrapper.getReadOnlyProperty();
    }

    public PropertyGroup getPropertyGroup() {
        return propertyGroupWrapper.getValue();
    }

    protected void setPropertyGroup(PropertyGroup group) {
        propertyGroupWrapper.setValue(group);
    }

    public PropertyPaneModel getPropertyPaneModel() {
        if (getPropertyGroup() == null) {
            return null;
        }
        return getPropertyGroup().getPropertyPaneModel();
    }

}
