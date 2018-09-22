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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Valery Shyshkin
 */
public class PropertyItem  implements NamedItem {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private BooleanProperty rejected = new SimpleBooleanProperty(false);

    private Method propertyMethod;
    private Method readMethod;
    private Method writetMethod;
    private Class<?> propertyType;
//    private final StringProperty  editorClass = new SimpleStringProperty();
    private final ReadOnlyObjectWrapper<Section> sectionWrapper = new ReadOnlyObjectWrapper<>();

    protected Class<? extends PropertyEditor> editorType;

    public PropertyItem() {
        init();
    }

    private void init() {
        name.addListener((v, ov, nv) -> {
            if ( nv != null && nv.equals("focusTraversable")) {
                System.err.println(""); 
            }
            if (getDisplayName() == null && nv != null || nv != null && nv.equals(getDisplayName())) {
                setDisplayName(toDisplayName(nv));
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

    public static String toDisplayName(String propName) {
        char[] str = propName.toCharArray();
        StringBuilder sb = new StringBuilder();
        int startPos = 0;
        
        while ( true ) {
            int endPos = getFirstWordPos(str, startPos);
            str[startPos] = Character.toUpperCase(str[startPos]);
            for ( int i = startPos; i <= endPos; i++  )  {
                sb.append(str[i]);
            }                  
            if ( endPos == str.length - 1 ) {
                break;
            }
            sb.append(' ');
            startPos = endPos + 1;
        }
        return sb.toString().trim();
    }

    public static int getFirstWordPos(char[] str, int startPos) {
        int lastPos = startPos;
        //str[startPos] = Character.toUpperCase(str[startPos]);
        if (startPos == str.length - 1) {
            return lastPos;
        }
        //
        // Check whether first and cecond char are in upper case
        //
        
        if (Character.isUpperCase(str[startPos])&& Character.isUpperCase(str[startPos + 1]) ) {

            // try search lower case char
            for (int i = startPos + 1; i < str.length; i++) {
                if ( ! Character.isUpperCase(str[i]) ) {
                    lastPos = i - 1;
                    break;
                }
                lastPos = i;
            }
            return lastPos;
        }

        for (int i = startPos + 1; i < str.length; i++) {
            if (Character.isUpperCase(str[i])) {
                lastPos = i - 1;
                break;
            }
            lastPos = i;
        }
        return lastPos;
    }

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
    public BeanModel getBeanModel() {
        return getSection().getCategory().getBeanModel();
    }

    public Method getPropertyMethod() {
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

    public PropertyItem getCopyFor(Class<?> clazz, BeanModel ppd, Category cat, Section sec) {
        PropertyItem pd = new PropertyItem();
        pd.setName(getName());
        pd.setDisplayName(pd.getDisplayName());
        pd.setModifiable(isModifiable());
        //pd.setModifiable(isModifiable());
        pd.setSection(sec);
        return pd;
    }

}
