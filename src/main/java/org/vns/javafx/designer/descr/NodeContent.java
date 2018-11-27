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
package org.vns.javafx.designer.descr;


/**
 * The class is used to describe a property of the object when the type of 
 * the property is not an (@code ObservableList}.
 * For example the {@code Labeled} object has a property named {@code graphic}. 
 * this property is described by the object of this class.
 * 
 * @author Valery Shyshkin
 */
public class NodeContent extends NodeProperty {
    
    public static String DEFAULT_STYLE_CLASS = "node-insert-content";
    public static String DEFAULT_TITLE = "insert content";
    
    private boolean hideWhenNull;
    private boolean replaceable;
    /**
     * Create an instance of the class.
     */
    public NodeContent() {
    }
    /**
     * Checks whether this object has visual representation if the value that
     * this object describes is null.
     * @return true if this object has visual representation if the value that
     * this object describes is null. Otherwise return false
     */
    public boolean isHideWhenNull() {
        return hideWhenNull;
    }
    /**
     * Sets the boolean value which defines whether this object has visual representation if the value that
     * this object describes is null.
     * @param hideWhenNull the value to be set
     */
    public void setHideWhenNull(boolean hideWhenNull) {
        this.hideWhenNull = hideWhenNull;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }
    
    /**
     * Checks whether the parent node descriptor defines the default property
     * with the same name as this object has.
     * @return true if the parent node descriptor defines the default property 
     * with the same name as this object has. Otherwise returns false
     */  
    @Override
    public boolean isDefault() {
        return super.isDefault();
    }
}
