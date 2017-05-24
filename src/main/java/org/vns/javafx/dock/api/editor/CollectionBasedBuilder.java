/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.FIRST;

/**
 *
 * @author Valery
 */
public interface CollectionBasedBuilder<T> extends TreeItemBuilder {

    List<T> getList(TreeItem<ItemValue> target);

    List<T> getList(Object obj);

    T getItem();
    //E getNode();

    default int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, T value) {
        if (place.getValue().getTreeItemObject() == value) {
            return -1;
        }
        TreeItem<ItemValue> sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;

        //E p = (E)target.getValue().getTreeItemObject();
        int valueIdx = getList(target).indexOf(value);

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST && valueIdx == 0) {
                idx = -1;
            } else if (q == FIRST) {
                idx = 0;
            } else if (valueIdx == getList(target).size()) {
                idx = -1;
            } else {
                idx = getList(target).size();
            }
        } else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            System.err.println(" -------------- 1 -- place.obj" + place.getValue().getTreeItemObject());
            System.err.println(" -------------- 2 -- target.obj" + target.getValue().getTreeItemObject());
            System.err.println(" -------------- 3 -- targetLevel = " + targetLevel);
            System.err.println(" -------------- 4 -- placeLevel = " + placeLevel);

            if (targetLevel - placeLevel == 1) {

            } else {
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {

                    parent = parent.getParent();
                    System.err.println("   --- parent = " + parent.getValue().getTreeItemObject());
                    System.err.println("   ---  dif = " + (targetLevel - treeView.getTreeItemLevel(parent)));
                }
            }
            System.err.println(" -------------- 2 " + parent.getValue().getTreeItemObject());
            /*            if (target.getChildren().indexOf(place) < target.getChildren().indexOf(sourceItem)
                    && target.getChildren().indexOf(sourceItem) >= 0) {
                idx = target.getChildren().indexOf(place);
            } else {
                idx = target.getChildren().indexOf(place) + 1;
            }
             */

            idx = target.getChildren().indexOf(parent);
            System.err.println("1 INDEX == " + idx);
        }
        System.err.println("2 INDEX == " + idx);
        return idx;
    }

    default int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place) {
        int idx = -1;

        //Pane p = (Pane) target.getValue().getTreeItemObject();
        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = getList(target).size();
            }
        } else {

            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            System.err.println("@ -------------- 1 -- place.obj" + place.getValue().getTreeItemObject());
            System.err.println("@ -------------- 2 -- target.obj" + target.getValue().getTreeItemObject());
            System.err.println("@ -------------- 3 -- targetLevel = " + targetLevel);
            System.err.println("@ -------------- 4 -- placeLevel = " + placeLevel);

            if (targetLevel - placeLevel == 1) {

            } else {
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {

                    parent = parent.getParent();
                    System.err.println("@   --- parent = " + parent.getValue().getTreeItemObject());
                    System.err.println("@   ---  dif = " + (targetLevel - treeView.getTreeItemLevel(parent)));
                }
            }
            System.err.println("@ -------------- 2 " + parent.getValue().getTreeItemObject());
            /*            if (target.getChildren().indexOf(place) < target.getChildren().indexOf(sourceItem)
                    && target.getChildren().indexOf(sourceItem) >= 0) {
                idx = target.getChildren().indexOf(place);
            } else {
                idx = target.getChildren().indexOf(place) + 1;
            }
             */

            idx = target.getChildren().indexOf(parent) + 1;
            System.err.println("@ INDEX == " + idx);

//            idx = target.getChildren().indexOf(place) + 1;
        }
        System.err.println("@INDEX == " + idx);
        return idx;
    }

    default TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        /*        if (dg == null || !(dg.getGestureSourceObject() instanceof Node)) {
            return retval;
        }
         */
        T value = (T) dg.getGestureSourceObject();
        DefaultTreeItemBuilder targetBuilder = target.getValue().getBuilder();
        //if (target != null && place != null && value != null && isAcceptable(target, value)) {
        if (target != null && place != null && value != null) {
            int idx = getIndex(treeView, target, place, value);
            if (idx < 0) {
                return null;
            }
            //Pane p = (Pane) ((ItemValue) target.getValue()).getTreeItemObject();
            //System.err.println("      --- 2 pane = " + p + "; pane.getChildren().contains = " + p.getChildren().contains(value));
            if (!getList(target).contains(value)) {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        targetBuilder.notifyObjectRemove(treeView, cell.getTreeItem());
                        targetBuilder.notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null && (dg.getGestureSourceObject() instanceof Node)) {
                    //Node node = (Node) dg.getGestureSourceObject();
                    TreeItem item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    if (item == null) {
                        return null;
                    }
                    targetBuilder.notifyObjectRemove(treeView, item);
                    targetBuilder.notifyTreeItemRemove(treeView, item);
                }

                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                target.getChildren().add(idx, retval);
                getList(target).add(idx, value);
            } else {
                //int idxSource = p.getChildren().indexOf(value);
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        targetBuilder.notifyObjectRemove(treeView, cell.getTreeItem());
                        targetBuilder.notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null && (dg.getGestureSourceObject() instanceof Node)) {
                    TreeItem item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    System.err.println("ACCEPT item = item");
                    if (item == null) {
                        return null;
                    }
                    targetBuilder.notifyObjectRemove(treeView, item);
                    targetBuilder.notifyTreeItemRemove(treeView, item);
                }
                idx = getIndex(treeView, target, place);
                System.err.println("ACCEPT idx = " + idx);
                //              idx = 0;///////!!!!!!!!!!!!!!!!!!!
                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                System.err.println("revval.value = " + value);
                target.getChildren().add(idx, retval);

                getList(target).add(idx, value);
            }
        }
        return retval;
    }

    default TreeItem build(Object obj) {
        TreeItem retval = null;
        List<T> children = getList(obj);
        //if (obj instanceof Pane) {
        //Pane pane = (Pane) obj;
        retval = createItem(obj);
        for (T node : children) {
            DefaultTreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(node);
            retval.getChildren().add(gb.build(node));
        }
        //}
        return retval;
    }

    @Override
    default void removeObject(Object parent, Object toRemove) {
        //if (parent instanceof Pane) {
        //((Pane) parent).getChildren().remove(toRemove);
        getList(parent).remove(toRemove);
        //}
    }

}
