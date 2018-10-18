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

import java.lang.reflect.Method;
import org.vns.javafx.dock.api.designer.bean.editor.PropertyEditor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Valery Shyshkin
 */
public class BeanProperty  extends AbstractNamedItem {

//    private final StringProperty name = new SimpleStringProperty();
//    private final StringProperty displayName = new SimpleStringProperty();
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private final BooleanProperty rejected = new SimpleBooleanProperty(false);
    private final BooleanProperty constraint = new SimpleBooleanProperty(false);
    
//    private Method propertyMethod;
//    private Method readMethod;
//    private Method writetMethod;
//    private Class<?> propertyType;
    
    //private String originClass;
    
//    private final StringProperty  editorClass = new SimpleStringProperty();
//    private final ReadOnlyObjectWrapper<Section> sectionWrapper = new ReadOnlyObjectWrapper<>();

    protected Class<? extends PropertyEditor> editorType;

    public BeanProperty() {
    }

    public BeanProperty(String name, String displayName) {
        super(name, displayName);
    }

    public BeanProperty(String name) {
        super(name);
    }

    public BooleanProperty constraintProperty() {
        return constraint;
    }
    public boolean isConstraint() {
        return constraint.get();
    }
    public void setConstraint(boolean constraint) {
        this.constraint.set(constraint);
    }    
/*    public String getOriginClass() {
    return originClass;
    }
    public void setOriginClass(String originClass) {
    this.originClass = originClass;
    }
     */
/*    public ReadOnlyObjectProperty<Section> categoryProperty() {
    return sectionWrapper.getReadOnlyProperty();
    }
    public Section getSection() {
    return sectionWrapper.getValue();
    }
    protected void setSection(Section section) {
    sectionWrapper.setValue(section);
    }
     */
/*    public StringProperty nameProperty() {
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
    @Override
    public String getDisplayName() {
    return displayName.get();
    }
    @Override
    public void setDisplayName(String displayName) {
    this.displayName.set(displayName);
    }
     */
  
    public boolean isReadOnly() {
        //BeanAdapter ba = new BeanAdapter(beanClass);
        //return ba.isReadOnly(getName());
        return true;
    }

    /*    public boolean isReadOnly(Object bean) {
        BeanAdapter ba = new BeanAdapter(bean);
        return ba.isReadOnly(getName());
    }
     */
    public BooleanProperty modifiableProperty() {
        return modifiable;
    }

    public boolean isModifiable() {
        return modifiable.get();
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable.set(modifiable);
    }

    public BooleanProperty rejectedProperty() {
        return rejected;
    }

    public boolean isRejected() {
        return rejected.get();
    }

    public void setRejected(boolean rejected) {
        this.rejected.set(rejected);
    }

    /*    public StringProperty editorClassProperty() {
        return editorClass;
    }
    public String getEditorClass() {
        return editorClass.get();
    }
    public void setEditorClass(String clazz) {
        editorClass.set(clazz);
    }
     */
/*    public BeanModel getBeanModel() {
        return getSection().getCategory().getBeanModel();
    }
*/
/*    public Method getPropertyMethod() {
        return propertyMethod;
    }

    public void setPropertyMethod(Method propertyMethod) {
        this.propertyMethod = propertyMethod;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
    }

    public Method getWritetMethod() {
        return writetMethod;
    }

    public void setWritetMethod(Method writetMethod) {
        this.writetMethod = writetMethod;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }
*/
    public BeanProperty getCopyFor(Class<?> clazz, BeanModel ppd, Category cat, Section sec) {
        BeanProperty pd = new BeanProperty();
        pd.setName(getName());
        pd.setDisplayName(getDisplayName());
        pd.setModifiable(isModifiable());
        pd.setRejected(isRejected());
        pd.setConstraint(isConstraint());
        
        //pd.setOriginClass(originClass);
        //pd.setModifiable(isModifiable());
        //pd.setSection(sec);
        return pd;
    }

}
