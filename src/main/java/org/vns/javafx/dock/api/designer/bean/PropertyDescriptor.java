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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import org.vns.javafx.dock.api.bean.BeanAdapter;

/**
 *
 * @author Olga
 */
public class PropertyDescriptor {
    
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private final StringProperty  editorClass = new SimpleStringProperty();
    private ReadOnlyObjectWrapper<Section> sectionWrapper = new ReadOnlyObjectWrapper<>();
    
    protected Class<? extends PropertyEditor> editorType;
    
    public PropertyDescriptor() {
        init();
    }
    private void init() {
        name.addListener((v,ov,nv) -> {
            if ( getDisplayName() == null && nv != null ) {
                setDisplayName(nv.substring(0,1).toUpperCase() + nv.substring(1));
            }
        });
    }


    public ReadOnlyObjectProperty<Section> categoryProperty() {
        return sectionWrapper.getReadOnlyProperty();
    }

    public Section getSection() {
        return sectionWrapper.getValue();
    }
    
    protected void setSection(Section section) {
        sectionWrapper.setValue(section);
    }    
    public StringProperty nameProperty() {
        return name;
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
    public BeanDescriptor getBeanDescriptor() {
        return getSection().getCategory().getBeanDescriptor();
    }

    
}
