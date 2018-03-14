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

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.designer.TreeItemEx.ItemType;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;

/**
 *
 * @author Valery
 */
public class TreeItemListObjectChangeListener implements ListChangeListener {

    private final TreeItemEx treeItem;
    private final String propertyName;

    public TreeItemListObjectChangeListener(TreeItemEx treeItem, String propertyName) {
        this.treeItem = treeItem;
        this.propertyName = propertyName;
    }

    @Override
    public void onChanged(Change change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List list = change.getRemoved();
                if (!list.isEmpty()) {
                }
                for (Object elem : list) {
                    TreeItemEx toRemove = null;
                    for (TreeItem it : treeItem.getChildren()) {
                        if (((TreeItemEx) it).getItemType() == ItemType.LIST) {
                            for (TreeItem ith : ((TreeItemEx) it).getChildren()) {
                                if (((TreeItemEx) ith).getValue() == elem) {
                                    toRemove = (TreeItemEx) ith;
                                    it.getChildren().remove(toRemove);
                                    return;
                                }
                            }
                        }
                        if (((TreeItemEx) it).getValue() == elem) {
                            toRemove = (TreeItemEx) it;
                            break;
                        }
                    }

                    treeItem.getChildren().remove(toRemove);
                    //13.03DockRegistry.lookup(Selection.class).removeSelected(toRemove.getValue());
                }

            }
            if (change.wasAdded()) {
                List list = change.getAddedSubList();
                List itemList = new ArrayList();

                list.stream().map((elem) -> new TreeItemBuilder().build(elem)).forEachOrdered((it) -> {
                    itemList.add(it);
                });
//                System.err.println("TreeItemListChangeListe: onChanged: itemList.size() = " + itemList.size() + "; list.size()=" + list.size());
                treeItem.getChildren().addAll(change.getFrom(), itemList);
                //Selection sel = DockRegistry.lookup(Selection.class);
                //DockRegistry.lookup(Selection.class).removeSelected();  
//                System.err.println("TreeItemListChangeListe: onChanged: setSelected = " + list.get(list.size() - 1));
                //sel.setSelected(list.get(list.size() - 1));
                NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
                if (nf != null && (list.get(list.size() - 1)) instanceof Node)  {
                    nf.show((Node) list.get(list.size() - 1));
                }
            }
        }//while
    }
}
