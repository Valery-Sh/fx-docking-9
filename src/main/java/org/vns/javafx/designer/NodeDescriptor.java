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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
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
    private String annotationDefaultProperty;
    
    /**
     * Contains a name of the property which value can be used as a title in a
     * TreeItem
     */
    private String titleProperty;

    private final ObservableList<Property> properties = FXCollections.observableArrayList();

    public NodeDescriptor() {
        init();
    }

    private void init() {
        properties.addListener(this::propertiesChanged);
    }

    public void propertiesChanged(ListChangeListener.Change<? extends Property> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                List<Property> list = (List<Property>) change.getAddedSubList();
                for (Property elem : list) {
                    elem.setDescriptor(this);
                }
            }
        }//while
    }

    public ObservableList<Property> getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        try {
            Class clazz = Class.forName(type);
            DefaultProperty[] dp = (DefaultProperty[]) clazz.getAnnotationsByType(DefaultProperty.class);
            if (dp.length > 0) {
                annotationDefaultProperty = dp[0].value();
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("EXCEPTION");
            Logger.getLogger(NodeDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        String retval = defaultProperty;
        if ( defaultProperty == null && getProperty(annotationDefaultProperty) != null) {
            defaultProperty = annotationDefaultProperty;
        }
        return retval;
    }

    public void setDefaultProperty(String defaultProperty) {
        this.defaultProperty = defaultProperty;
    }

    public Property getProperty(String propertyName) {
        Property retval = null;
        for ( Property p : properties) {
            if ( p.getName().equals(propertyName)) {
                retval = p;
                break;
            }
        }
        return retval;
    }
    public int indexOf(String propertyName) {
        int retval = -1;
        for ( int i=0; i < properties.size(); i++) {
            
            if ( properties.get(i).getName().equals(propertyName)) {
                retval = i;
                break;
            }
        }
        return retval;
    }
    
    
    public static ItemType getItemType(TreeItemEx item) {
        ItemType retval = null;
        if (item.getParent() == null) {
            retval = ItemType.CONTENT;
        } else if (item.getParent().getParent() == null) {
            // item.getParent() is root Item
            NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(item.getParent().getValue());

        }
        return retval;
    }

}
