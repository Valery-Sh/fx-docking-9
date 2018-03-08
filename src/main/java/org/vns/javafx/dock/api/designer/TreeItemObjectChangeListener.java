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
package org.vns.javafx.dock.api.designer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery Shyshkin
 */
public class TreeItemObjectChangeListener implements ChangeListener {

    private final TreeItemEx treeItem;
    private final String propertyName;

    public TreeItemObjectChangeListener(TreeItemEx treeItem, String propertyName) {
        this.treeItem = treeItem;
        this.propertyName = propertyName;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        Property prop = treeItem.getProperty(propertyName);
//        System.err.println("TreeItemObjectChangeListener: propName = " + propertyName);
//        System.err.println("   --- oldValue = " + oldValue);
//        System.err.println("   --- newValue = " + newValue);
        TreeItemEx propTreeItem = treeItem.getTreeItem(propertyName);
        int insertPos = propTreeItem == null ? 0 : treeItem.getInsertPos(propertyName);
        if (propTreeItem == null) {
            if (oldValue == null && newValue != null) {
                TreeItemEx item = new TreeItemBuilder().build(newValue, prop);
                treeItem.getChildren().add(insertPos, item);
            }
        } else {
            
            TreeItem p = propTreeItem.getParent();
            
            if (oldValue != null && newValue == null) {
                if ( ( (prop instanceof NodeContent) && ((NodeContent)prop).isHideWhenNull()) ) {
                    p.getChildren().remove(propTreeItem);
                    
                } else {
                    
                    TreeItemEx item = new TreeItemBuilder().build(newValue,prop);
                    p.getChildren().set(p.getChildren().indexOf(propTreeItem), item);
                }
            }  else if (oldValue == null && newValue != null) {
                // May be is NodeContent and not hidden when null
                TreeItemEx item = new TreeItemBuilder().build(newValue,prop);
                p.getChildren().set(p.getChildren().indexOf(propTreeItem), item);
            } else if (oldValue != null && newValue != null) {
                TreeItemEx item = new TreeItemBuilder().build(newValue,prop);
                p.getChildren().set(p.getChildren().indexOf(propTreeItem), item);
            }
        }
    }

    
    protected Object getPropertyValue() {
        Object retval = null;
        return retval;
    }

    protected TreeItemEx getPropertyTreeItem() {
        TreeItemEx retval = null;
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(treeItem.getValue());

        return retval;
    }
}
