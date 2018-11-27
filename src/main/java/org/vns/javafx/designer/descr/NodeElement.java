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
 * The base class used to describe a property/
 * 
 * @author Valery Shyshkin
 */
public abstract class NodeElement {
    private String styleClass;
    private String title; 
    /**
     * Returns the text which can be used as a style class
     * for visual representation of the element.
     * 
     * @return the text that can be used as a style class
     * for visual representation of the element.
     */
    public String getStyleClass() {
        return styleClass;
    }
    /**
     * Sets the text which can be used as a style class
     * for visual representation of the element.
     * @param styleClass the new styleClass to be set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    /**
     * Returns the text which can be used as a title
     * for visual representation of the element.
     * 
     * @return  the text which can be used as a title for visual representation 
     * of the element.
     */
    public String getTitle() {
        return title;
    }
    /**
     * Sets the text which can be used as a title
     * for visual representation of the element.
     * @param title the new title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
}
