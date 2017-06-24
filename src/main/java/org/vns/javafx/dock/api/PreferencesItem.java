/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class PreferencesItem {
    private TreeItem treeItem;
    private Object itemObject;
    private Map<String,String> properties = FXCollections.observableHashMap();

    public PreferencesItem(TreeItem treItem, Object itemObject) {
        this.itemObject = itemObject;
        this.treeItem = treItem;
        treeItem.setExpanded(true);
    }
    
    public Object getItemObject() {
        return itemObject;
    }

    public void setItemObject(Object itemObject) {
        this.itemObject = itemObject;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( (itemObject instanceof Node)  ) {
            sb.append("id=" + ((Node)itemObject).getId() + " ");
        }
        sb.append("type : " + itemObject.getClass().getSimpleName());
        if ( String.class.equals(itemObject.getClass())) {
            sb.append(" (")
                .append((String)itemObject)
                .append(")");                    
        }
        if ( ! getProperties().isEmpty() ) {
            sb.append(" [")
                    .append(getProperties())
                    .append(" ]");
        }
        return sb.toString();
    }
}
