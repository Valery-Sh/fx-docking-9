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

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.vns.javafx.designer.TreeItemEx.ItemType;

/**
 *
 * @author Valery
 */
@DefaultProperty("properties")
public class NodeDescriptor {
    
    private String type;
    private String styleClass;
    private String defaultProperty;        
    /**
     * Contains a name of the property which value can be used as a title 
     * in a TreeItem
     */
    private String titleProperty;
    
    private final ObservableList<Property> properties = FXCollections.observableArrayList();

    public NodeDescriptor() {
    }


    public ObservableList<Property> getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getTitleProperty() {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty) {
        this.titleProperty = titleProperty;
    }

    public String getDefaultProperty() {
        return defaultProperty;
    }

    public void setDefaultProperty(String defaultProperty) {
        this.defaultProperty = defaultProperty;
    }
    
    public static Property getProperty(String propertyName, TreeItemEx item) {
        Property retval = null;
        
        return retval;
    }
    public static ItemType getItemType(TreeItemEx item) {
        ItemType retval = null;
        if ( item.getParent() == null ) {
            retval = ItemType.CONTENT;
        } else if (item.getParent().getParent() == null ){
            // item.getParent() is root Item
            NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(item.getParent().getValue());
            
        }
        return retval;
    }
    
}
