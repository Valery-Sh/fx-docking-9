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
 * the property is (@code ObservableList}.
 * For example the {@code Pane} object has a property named {@code children}. 
 * This property is described by the object of this class.
 * 
 * @author Valery Shyshkin
 */
public class NodeList extends NodeProperty {

    public static String DEFAULT_STYLE_CLASS = "tree-item-list-header";;
    public static String DEFAULT_TITLE = "list of nodes";
    
    private boolean alwaysVisible;
    /**
     * Checks whether this object has visual representation.  
     * By default returns false.
     * @return true if this object has visual representation. false - otherwise 
     */
    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }
    /**
     * Sets the value to define whether this object has visual representation.  
     * @param  alwaysVisible the new value to be set
     */
    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }
    
}
