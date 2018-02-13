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
package org.vns.javafx.dock.incubator.designer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery Shyshkin
 */
public class TreeItemObjectChangeListener implements ChangeListener {

    private final TreeItemEx treeItem;
    private String propertyName;

    public TreeItemObjectChangeListener(TreeItemEx treeItem, String propertyName) {
        this.treeItem = treeItem;
        this.propertyName = propertyName;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        Property prop = treeItem.getProperty(propertyName);
        TreeItemEx propTreeItem = treeItem.getTreeItem(propertyName);
        //System.err.println("BEFORE GETINSERT POS propName=" + propertyName);
        int insertPos = propTreeItem == null ? 0 : treeItem.getInsertPos(propertyName);
        //System.err.println("AFTER GETINSERT POS  propName=" + propertyName);        
        if (propTreeItem == null) {
            if (oldValue == null && newValue != null) {
                TreeItemEx item = new TreeItemBuilder().build(newValue, prop);
                treeItem.getChildren().add(insertPos, item);
            }
        } else {
            
            TreeItem p = propTreeItem.getParent();
            
            if (oldValue != null && newValue == null) {
                //propTreeItem.getChildren().clear();
                if ( ( (prop instanceof NodeContent) && ((NodeContent)prop).isHideWhenNull()) ) {
                    ///!!! must consider root node
                    p.getChildren().remove(propTreeItem);
                    
                } else {
                    
                    TreeItemEx item = new TreeItemBuilder().build(newValue,prop);
                    //System.err.println("1) item.propName=" + item.getPropertyName());                    
                    //System.err.println("2) prop=" + prop);                                                            
                    //System.err.println("3) prop.name=" + prop.getName());                                        
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
