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
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.designer.TreeItemEx.ItemType;
import org.vns.javafx.dock.api.DragContainer;

/**
 *
 * @author Olga
 */
public class AutoSaveRestore2 implements SaveRestore {

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
    private boolean saved;

    @Override
    public boolean isSaved() {
        return saved;
    }

    @Override
    public void save(Object toSave) {
        Object obj = toSave;
        if (toSave instanceof TreeItemEx) {
            obj = ((TreeItemEx) toSave).getValue();
        }

        System.err.println("AutoSaveRestore save 1 obj = " + obj);
        saved = false;
        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        System.err.println("AutoSaveRestore save 1.0");
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
        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv == null) {
            return;
        }
//        clear();
        this.objectToSave = obj;
        if (obj == null) {
            return;
        }
        System.err.println("AutoSaveRestore save 2");
        objectItem = EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        if (objectItem == null) {
            return;
        }
        System.err.println("AutoSaveRestore save 3 objectItem.value = " + objectItem.getValue());
        parentItem = (TreeItemEx) objectItem.getParent(); // may be null for root item    
        listIndex = parentItem.getChildren().indexOf(objectItem);
        System.err.println("AutoSaveRestore save 3 objectItem.parent = " + objectItem.getParent());
        if (parentItem == null) {
            return;
        }
        saved = true;
        propertyName = objectItem.getPropertyName();
        System.err.println("AutoSaveRestore save 4");
    }

    @Override
    public void save(Object obj, int listIndex) {
        System.err.println("AutoSaveRestore save(idx) 1 obj = " + obj);
        saved = false;
        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        System.err.println("AutoSaveRestore save(idx) 2");
        if (dragInitiator == PALETTE_PANE) {
            objectToSave = null;
            clear();
            return;
        }
        System.err.println("AutoSaveRestore save(idx) 3");
        if (dragInitiator == TRASH_TRAY) {
            this.listIndex = listIndex;
            return;
        }
        //
        // Not removes from PalettePane or TrashTray. So we save the position in SceneGraphView
        //

        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv == null) {
            return;
        }
        System.err.println("AutoSaveRestore save(idx) 4");
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
        System.err.println("AutoSaveRestore save(idx) 5");
        parentItem = (TreeItemEx) objectItem.getParent(); // may be null for root item        '
        if (parentItem == null) {
            return;
        }
        System.err.println("AutoSaveRestore save(idx) 6");
        saved = true;
        propertyName = objectItem.getPropertyName();
    }

    @Override
    public void restore(Object obj) {
        if (objectToSave == null || obj != objectToSave) {
            return;
        }
        if (dragInitiator == PALETTE_PANE) {
            return;
        }
        //
        // Not removes from PalettePane or TrashTray. So we save the position in SceneGraphView
        //

        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        TreeItemEx item = (sgv == null || obj == null) ? null : EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        TrashTray tray = DockRegistry.lookup(TrashTray.class);
        if (dragInitiator == TRASH_TRAY) {
            if (item != null) {
                return;
            }
            if (tray != null && !tray.contains(obj)) {
                if (listIndex >= 0) {
                    tray.add(listIndex, obj);
                }
            }
            return;
        } else if (tray != null && tray.contains(obj)) {
            return;
        }

        if (item != null) {
            if (verify(item, objectItem)) {
                restoreExpanded(item, objectItem);
                item.setExpanded(objectItem.isExpanded());
            }
            return;
        }
        System.err.println("AutoSaveRestore restore 1");

        if (parentItem == null) {
            return;
        }
        System.err.println("AutoSaveRestore restore 2");

        if (listIndex < 0) {
            return;
        }
        System.err.println("AutoSaveRestore restore 3");

        if (parentItem.getItemType() == ItemType.LIST) {
            TreeItemEx p = (TreeItemEx) parentItem.getParent();
            BeanAdapter ba = new BeanAdapter(p.getValue());
            List list = (List) ba.get(parentItem.getPropertyName());
            list.add(listIndex, obj);
        } else if (parentItem.getItemType() == ItemType.ELEMENT && propertyName != null) {
            BeanAdapter ba = new BeanAdapter(parentItem.getValue());
            ba.put(propertyName, obj);

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

        System.err.println("AutoSaveRestore restore 4");

        item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        if (verify(item, objectItem)) {
            System.err.println("AutoSaveRestore restore 5");

            restoreExpanded(item, objectItem);
            item.setExpanded(objectItem.isExpanded());
        }
        System.err.println("AutoSaveRestore restore 6");

    }

    protected boolean verify(TreeItemEx item, TreeItemEx objItem) {
        if (item == null || objItem == null) {
            return false;
        }
        for (int i = 0; i < objItem.getChildren().size(); i++) {
            if (item.getChildren().size() != objItem.getChildren().size()) {
                return false;
            }
            if (item.getChildren().get(i).getValue() != objItem.getChildren().get(i).getValue()) {
                return false;
            }
            if (!verify((TreeItemEx) item.getChildren().get(i), (TreeItemEx) objItem.getChildren().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void restoreExpanded(Object obj) {
        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        TreeItemEx item = (sgv == null || obj == null) ? null : EditorUtil.findTreeItemByObject(sgv.getTreeView(), obj);
        if (item == null || obj != objectToSave || objectItem == null) {
            return;
        }
        if (verify(item, objectItem)) {
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
        saved = false;
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
        System.err.println("AutoSaveRestore add 1");
        clear();
        if (dockable == null || dockable.getContext().getLayoutContext() == null || dockable.getContext().getLayoutContext().getLayoutNode() == null) {
            return;
        }

        objectToSave = dockable.getContext().getDragValue();
        System.err.println("AutoSaveRestore 2 toSave add = " + objectToSave);
        Node node = dockable.getContext().getLayoutContext().getLayoutNode();
        System.err.println("AutoSaveRestore 3 node add = " + node);
        if (node instanceof PalettePane) {
            dragInitiator = PALETTE_PANE;
        } else if (node instanceof TrashTray) {
            dragInitiator = TRASH_TRAY;
        }
        save(objectToSave);
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

    public Object getObject(TreeViewEx tv) {
        Dockable d = Dockable.of(tv);
        DragContainer dc = d.getContext().getDragContainer();
        return dc == null ? null : dc.getValue();
    }
}
