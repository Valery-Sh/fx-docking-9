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

import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.TreeItemEx.ItemType;

/**
 *
 * @author Olga
 */
public class AutoSaveRestore implements SaveRestore {

    protected final static int PALETTE_PANE = 0;
    protected final static int TRASH_TRAY = 2;
    protected final static int SCENE_GRAPH = 4;

    private int dragInitiator = -1;

    private Object objectToSave;
    private TreeItemEx objectItem;
    private String propertyName;
    //private ItemType itemType;
    private TreeItemEx parentItem;
    private int listIndex;

    @Override
    public void save(Object obj) {

        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        if (dragInitiator == PALETTE_PANE) {
            objectToSave = null;
            clear();
            return;
        }
        if (dragInitiator == TRASH_TRAY) {
            listIndex = 0;
            return;
        }
        //
        // Not removes from PalettePane or TrashTray. So we save the position in SceneGraphView
        //
        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        if (sgv == null) {
            return;
        }
//        clear();
        this.objectToSave = obj;
        if (obj == null) {
            return;
        }

        objectItem = EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);

        if (objectItem == null) {
            return;
        }
        parentItem = (TreeItemEx) objectItem.getParent(); // may be null for root item        '
        if (parentItem == null) {
            return;
        }

        propertyName = objectItem.getPropertyName();
    }

    @Override
    public void save(Object obj, int listIndex) {
        //System.err.println("save onj = " + obj + "; listIndex=" + listIndex);
        //System.err.println("   --- objectToSave = " + objectToSave);
        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        //System.err.println("   --- dragIndicator = " + dragInitiator);

        if (dragInitiator == PALETTE_PANE) {
            objectToSave = null;
            clear();
            return;
        }
        if (dragInitiator == TRASH_TRAY) {
            //System.err.println("SAVE TRAS_TRAY listIndex = " + listIndex);
            this.listIndex = listIndex;
            //System.err.println("SAVE TRAS_TRAY listIndex = " + this.listIndex);
            return;
        }
        //
        // Not removes from PalettePane or TrashTray. So we save the position in SceneGraphView
        //

        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        if (sgv == null) {
            return;
        }
        //clear();
        this.listIndex = listIndex;
        if (obj == null) {
            return;
        }
        this.objectToSave = obj;

        objectItem = EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        if (objectItem == null) {
            return;
        }
        parentItem = (TreeItemEx) objectItem.getParent(); // may be null for root item        '
        if (parentItem == null) {
            return;
        }

        propertyName = objectItem.getPropertyName();
    }

    @Override
    public void restore(Object obj) {
        //System.err.println("1 restore");
        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        if (dragInitiator == PALETTE_PANE) {
            return;
        }
        //System.err.println("2 restore");
        //
        // Not removes from PalettePane or TrashTray. So we save the position in SceneGraphView
        //

        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        TreeItemEx item = (sgv == null || obj == null) ? null : EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        TrashTray tray = DockRegistry.lookup(TrashTray.class);
        if (dragInitiator == TRASH_TRAY) {
            if (item != null) {
                return;
            }
            //System.err.println("3 restore listIndex = " + listIndex + "; obj = " + obj + "; tray.contains = " + tray.contains(obj));
            if (tray != null && !tray.contains(obj)) {
                if (listIndex >= 0) {
              //      System.err.println("********* add");
                    tray.add(listIndex, obj);
                }
            }
            return;
        } else if (tray != null && tray.contains(obj) ) {
            return;
        }
        //System.err.println("4 restore listIndex = " + listIndex + "; obj = " + obj + "; tray.contains = " + tray.contains(obj));

        if (item != null) {
            if (verify(item, objectItem)) {
                restoreExpanded(item, objectItem);
                item.setExpanded(objectItem.isExpanded());
            }
            return;
        }

        if (parentItem == null) {
            return;
        }
        if (listIndex >= 0) {
            if (parentItem.getItemType() == ItemType.LIST) {
                TreeItemEx p = (TreeItemEx) parentItem.getParent();
                BeanAdapter ba = new BeanAdapter(p.getValue());
                List list = (List) ba.get(parentItem.getPropertyName());
                list.add(listIndex, obj);
            } else {
                NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(parentItem.getValue());
                BeanAdapter ba = new BeanAdapter(parentItem.getValue());
                for (Property p : nd.getProperties()) {
                    if (p instanceof NodeList) {
                        List list = (List) ba.get(p.getName());
                        if (list != null && list.size() >= listIndex) {
                            list.add(listIndex, obj);
                        }
                    }
                }
            }
        } else if (propertyName != null) {
            BeanAdapter ba = new BeanAdapter(parentItem.getValue());
            ba.put(propertyName, obj);
        }

        item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        if (verify(item, objectItem)) {
            restoreExpanded(item, objectItem);
            item.setExpanded(objectItem.isExpanded());
        }
    }

    protected boolean verify(TreeItemEx item, TreeItemEx objItem) {
        if (item == null || objItem == null) {
            return false;
        }
        for (int i = 0; i < objItem.getChildren().size(); i++) {
            if (item.getChildren().size() != objItem.getChildren().size()) {
//                System.err.println("verify size 1 = false");
                return false;
            }
            if (item.getChildren().get(i).getValue() != objItem.getChildren().get(i).getValue()) {
//                System.err.println("verify child(i) i = " + i);
                return false;
            }
            if (!verify((TreeItemEx) item.getChildren().get(i), (TreeItemEx) objItem.getChildren().get(i))) {
//                System.err.println("verify verify i = " + i);
                return false;
            }
        }
        return true;
    }

    @Override
    public void restoreExpanded(Object obj) {
//        System.err.println("0 restoreExpanded obj = " + obj);
        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        TreeItemEx item = (sgv == null || obj == null) ? null : EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
//        System.err.println("   --- item = " + item);
//        System.err.println("   --- object = " + objectToSave);
//        System.err.println("   --- objectItem = " + objectItem);
        if (item == null || obj != objectToSave || objectItem == null) {
            return;
        }
//        System.err.println("1 restoreExpanded");
        if (verify(item, objectItem)) {
//            System.err.println("2 restoreExpanded");
            restoreExpanded(item, objectItem);
            item.setExpanded(objectItem.isExpanded());
        }
    }

    protected void restoreExpanded(TreeItemEx item, TreeItemEx objItem) {
        for (int i = 0; i < objItem.getChildren().size(); i++) {
            if (item.getChildren().size() != objItem.getChildren().size()) {
                break;
            }
            item.getChildren().get(i).setExpanded(objItem.getChildren().get(i).isExpanded());
            restoreExpanded((TreeItemEx) item.getChildren().get(i), (TreeItemEx) objItem.getChildren().get(i));
        }

    }

    protected void restoreExpanded(ObservableList<TreeItem> itemList, ObservableList<TreeItem> objectItemList) {

    }

    private void clear() {
        dragInitiator = -1;
        listIndex = -1;
        parentItem = null;
        objectItem = null;
        propertyName = null;
        objectToSave = null;
    }

    /*    @Override
    public void add(Object toSave) {
        clear();
        this.objectToSave = toSave;
    }
     */
    @Override
    public void add(Dockable dockable) {
        clear();
        if (dockable == null || dockable.getContext().getLayoutContext() == null || dockable.getContext().getLayoutContext().getLayoutNode() == null) {
            return;
        }
        
        objectToSave = dockable.getContext().getDragValue();
        Node node = dockable.getContext().getLayoutContext().getLayoutNode();
        //Node node = dockable.node();
        //System.err.println("add dockable.dragValue = " + dockable.getContext().getDragValue());
        //System.err.println("   ---  node = " + node);
        if (node instanceof PalettePane) {
            dragInitiator = PALETTE_PANE;
        } else if (node instanceof TrashTray) {
            dragInitiator = TRASH_TRAY;
        }
        //System.err.println("   --- dragInitiator = " + dragInitiator);
        
    }

    @Override
    public void remove(Object toSave) {
        clear();
        objectToSave = null;
    }

    @Override
    public boolean contains(Object obj) {
        return obj != null && obj == objectToSave;
    }
}
