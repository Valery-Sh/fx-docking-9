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

import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;

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

    private List<Boolean> downUpExpandedValues(TreeItemEx downItem) {
        List<Boolean> list = FXCollections.observableArrayList();
        TreeItemEx p = downItem;
        while (p != null) {
            list.add(p.isExpanded());
            p = (TreeItemEx) p.getParent();
        }
        return list;
    }

    private void restoreExpandedValues(TreeItemEx downItem, List<Boolean> expValues) {
        TreeItemEx p = downItem;

        int idx = 0;

        while (p != null) {
            p.setExpanded(expValues.get(idx));
            p = (TreeItemEx) p.getParent();
            idx++;
        }
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        Property prop = treeItem.getProperty(propertyName);
        TreeItemEx propItem = treeItem.getTreeItem(propertyName);
        int insertPos = propItem == null ? 0 : treeItem.getInsertPos(propertyName);
//        SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);

        if (propItem == null) {
            if (oldValue == null && newValue != null) {
                TreeItemEx item = new TreeItemBuilder().build(newValue, prop);
/*                if (item != null && sr != null && !sr.contains(newValue)) {
                    //
                    //changed outside and not by dragging 
                    //
                    treeItem.getChildren().add(insertPos, item);
                    item.setExpanded(false);
                } else 
*/                
                if (item != null ) {
                    treeItem.getChildren().add(insertPos, item);
                    if (nf != null && (newValue instanceof Node)) {
                        Platform.runLater(() -> {
                            nf.show((Node) newValue);
                        });
                    }

                }

            }
        } else {

            TreeItemEx propItemParent = (TreeItemEx) propItem.getParent();

            if (oldValue != null && newValue == null) {
                if (((prop instanceof NodeContent) && ((NodeContent) prop).isHideWhenNull())) {
/*                    if (sr != null) {
                        //savasr.save(oldValue);
                    }
*/
                    propItemParent.getChildren().remove(propItem);
                    //SceneView.reset(propItem);
                    

                } else {
  /*                  if (sr != null) {
                       //sava sr.save(oldValue);
                    }
*/
                    TreeItemEx item = new TreeItemBuilder().build(newValue, prop);
/*                    if ( item != null && sr != null && !sr.contains(newValue)) {
                        //
                        //changed outside and not by dragging 
                        //
                        propItemParent.getChildren().set(propItemParent.getChildren().indexOf(propItem), item);
                        item.setExpanded(false);
                    } else 
*/
                    if ( item != null ) {
                        item.setExpanded(false);
                        propItemParent.getChildren().set(propItemParent.getChildren().indexOf(propItem), item);
                    }
                }
                
            } else if ( newValue != null ) {
                // May be is NodeContent and not hidden when null
                TreeItemEx item = new TreeItemBuilder().build(newValue, prop);

/*                if (item != null && sr != null && !sr.contains(newValue)) {
                    //
                    //changed outside and not by dragging 
                    //
                    List<Boolean> expValues = downUpExpandedValues(propItemParent);
                    propItemParent.getChildren().set(propItemParent.getChildren().indexOf(propItem), item);
                    item.setExpanded(false);
                } else 
*/
                if ( item != null ){
                    item.setExpanded(false);
                    propItemParent.getChildren().set(propItemParent.getChildren().indexOf(propItem), item);
                    if (nf != null && (newValue instanceof Node)) {
                        Platform.runLater(() -> {
                            nf.show((Node) newValue);
                        });
                    }
                }

            }
        }
    }

/*    protected Object getPropertyValue() {
        Object retval = null;
        return retval;
    }
*/
}
