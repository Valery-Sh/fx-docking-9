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
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(treeItem.getValue());
        int idx = -1;
        for (int i = 0; i < nd.getProperties().size(); i++) {
            if (propertyName.equals(nd.getProperties().get(i).getName())) {
                idx = i;
                break;
            }
        }
        Property prop = nd.getProperties().get(idx);

        TreeItemEx propTreeItem = null;
        int insertPos = -1;  //use it when propTreeItem is null
        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            TreeItemEx it = (TreeItemEx) treeItem.getChildren().get(i);
            if (idx == it.getIndex()) {
                propTreeItem = it;
                break;
            }
            if (it.getIndex() >= insertPos && i < idx) {
                insertPos = i;
            }
        }
        if (propTreeItem == null) {
            if (insertPos == -1) {
                insertPos = 0;
            } else {
                insertPos++;
            }

        }
        if (propTreeItem == null) {
            if (oldValue == null && newValue != null) {
                TreeItemEx item = new TreeItemBuilder().build(newValue, prop);
                treeItem.getChildren().add(insertPos, item);
            }
        } else {
            
            TreeItem p = propTreeItem.getParent();
            
            if (oldValue != null && newValue == null) {
                propTreeItem.getChildren().clear();
                if ( (prop instanceof Content) || ( (prop instanceof Placeholder) && ((Placeholder)prop).isHideNull()) ) {
                    ///!!! must consider root node
                    p.getChildren().remove(propTreeItem);
                    
                }
            }  else if (oldValue == null && newValue != null) {
                // May be is Placeholder and not hidden when null
                TreeItemEx item = new TreeItemBuilder().build(newValue,prop);
                p.getChildren().set(p.getChildren().indexOf(propTreeItem), item);
            } else if (oldValue != null && newValue != null) {
                //23TreeItemEx item = treeItem.treeItemOf(oldValue);
                //23if (item != null) {
                    //23TreeViewEx.updateOnMove(item);
                //23}
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
