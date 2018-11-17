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
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.designer.TreeItemEx.ItemType;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.dragging.view.RectangleFrame;

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
                    SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
                    if (sr != null) {
                        sr.save(list.get(list.size() - 1), change.getTo());
                    }
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
                }

            }
            if (change.wasAdded()) {
                List list = change.getAddedSubList();
                List itemList = new ArrayList();
                list.forEach(elem -> {
                    TreeItemEx it = new TreeItemBuilder().build(elem);
                    it.setExpanded(false);
                    itemList.add(it);
                });

                treeItem.getChildren().addAll(change.getFrom(), itemList);
                //treeItem.getChildren().addAll(itemList);

                NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
                if (nf != null && (list.get(list.size() - 1)) instanceof Node)  {
                //if (nf != null && (list.get(0)) instanceof Node) {
                    //
                    // We apply Platform.runLater because a list do not 
                    // has to be a children but for instance for SplitPane it
                    // is an items and an added node may be not set into scene graph
                    // immeduately
                    //
                    //&&&nf.show((Node) list.get(list.size() - 1));
                    Node n = (Node) list.get(list.size() - 1);
                    if (!(n instanceof RectangleFrame) && !(n instanceof Circle)) {
                        nf.show((Node) list.get(list.size() - 1));
                        Platform.runLater(() -> {
                            nf.show((Node) list.get(list.size() - 1));
                        });
                    }
                    /*                  if ( ! filteredList.isEmpty()  ) {
                    nf.show((Node) filteredList.get(filteredList.size() - 1));
                    Platform.runLater(() -> {
                        nf.show((Node) filteredList.get(filteredList.size() - 1));
                    });
                    }
                     */
                }
            }
        }//while
    }
}
