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
package org.vns.javafx.designer;

import org.vns.javafx.dock.api.editor.bean.BeanAdapter;

/**
 *
 * @author Valery Shyshkin
 */
public class ContentProperty {

    /**
     * <code>nodeObject</code> may be of any type not only <code>Node</code>
     */
    private NodeDescriptor nodeDescriptor;
    /**
     * Property name
     */
    private String name;
    private boolean placeholder;
    
    private String styleClass;
    /**
     * Used for empty placeholder
     */
    private String title; 
    /**
     * Used only for placeholder
     */
    //private Node icon;
    /**
     * Used only for placeholder
     */
    private boolean hideIfNull;

    private boolean defaultTarget;

    public ContentProperty() {
    }

    public ContentProperty(NodeDescriptor owner) {
        this.nodeDescriptor = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isHideIfNull() {
        return hideIfNull;
    }

    public void setHideIfNull(boolean hideIfNull) {
        this.hideIfNull = hideIfNull;
    }

    public NodeDescriptor getNodeObject() {
        return nodeDescriptor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNodeDescriptor(NodeDescriptor owner) {
        this.nodeDescriptor = owner;
    }
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

/*    public ContentProperty cloneFor(NodeDescriptor forDescr) {
        ContentProperty retval = new ContentProperty(forDescr);
        retval.setHideIfNull(this.isHideIfNull());
        retval.setName(this.getName());
        retval.setPlaceholder(this.isPlaceholder());
        retval.setTitle(this.getTitle());
        retval.setStyleClass(this.getStyleClass());
        return retval;
    }
*/

    public boolean isDefaultTarget() {
        return defaultTarget;
    }

    public void setDefaultTarget(boolean defaultTarget) {
        this.defaultTarget = defaultTarget;
    }
    
}
