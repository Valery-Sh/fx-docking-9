/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.FIRST;

/**
 *
 * @author Valery
 */
public class TabPaneItemBuilder extends DefaultTreeItemBuilder implements CollectionBasedBuilder<Tab> {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof TabPane) {
            TabPane pane = (TabPane) obj;
            retval = createItem((TabPane) obj);
            for (Tab tab : pane.getTabs()) {
                DefaultTreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(tab);
                retval.getChildren().add(gb.build(tab));
            }
        }
        return retval;
    }

    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Tab;
    }

/*    @Override
    public boolean isDragPlace(TreeItem<ItemValue> target, TreeItem<ItemValue> place, Object source) {
        return source instanceof Tab;
    }
*/
    @Override
    public TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {

        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null || !(dg.getGestureSourceObject() instanceof Tab)) {
            return retval;
        }
        Tab value = (Tab) dg.getGestureSourceObject();
        //if (target != null && place != null && isAcceptable(target, value)) {
        if (target != null && place != null) {
            int idx = getIndex(treeView, target, place);
            if (idx < 0) {
                return null;
            }

            TabPane p = (TabPane) ((ItemValue) target.getValue()).getTreeItemObject();
            if (!getList(target).contains(value)) {
                if (dg.getGestureSource() != null && (dg instanceof DragTreeCellGesture)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        notifyObjectRemove(treeView, cell.getTreeItem());
                        notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                }
                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                target.getChildren().add(idx, retval);
                getList(target).add(idx, value);
            } else {
                int idxSource = p.getTabs().indexOf(value);
                if (idx != idxSource) {
                    if (idxSource > idx) {
                        p.getTabs().remove(idxSource);
                    } else {
                        p.getTabs().remove(idxSource);
                        idx--;
                    }
                    if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                        TreeCell cell = (TreeCell) dg.getGestureSource();
                        if (cell.getTreeItem() instanceof TreeItemEx) {
                            notifyObjectRemove(treeView, cell.getTreeItem());
//                            cell.getTreeItem().getParent().getChildren().removeObject(cell.getTreeItem());
                            notifyTreeItemRemove(treeView, cell.getTreeItem());

                        }
                    }

                    retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                    target.getChildren().add(idx, retval);

                    getList(target).add(idx, value);

                }
            }
        }
        return retval;
    }

    @Override
    public void removeObject(Object parent, Object toRemove) {
        if (parent != null && (parent instanceof TabPane) && toRemove != null && (toRemove instanceof Tab)) {
            ((TabPane) parent).getTabs().remove((Tab) toRemove);
        }
    }

    @Override
    public void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {
        parent.getChildren().remove(toRemove);
    }

/*    protected int getIndex(TreeView treeView, TreeItem<ItemValue> parent, TreeItem<ItemValue> target) {
        int idx = -1;

        TabPane p = (TabPane) parent.getValue().getTreeItemObject();
        if (parent == target) {
            int q = target.getValue().getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = p.getTabs().size();
            }

        } else {
            if (parent.getValue().getTreeItemObject() instanceof Node) {
                int level = treeView.getTreeItemLevel(parent);
                TreeItem<ItemValue> it = EditorUtil.parentOfLevel(treeView, target, level + 1);
                Tab tab = (Tab) it.getValue().getTreeItemObject();
                idx = p.getTabs().indexOf(tab) + 1;
            }
        }
        return idx;
    }
*/

    @Override
    public List<Tab> getList(TreeItem<ItemValue> target) {
        return ((TabPane)target.getValue().getTreeItemObject()).getTabs();
    }

    @Override
    public Tab getItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Tab> getList(Object obj) {
        return ((TabPane)obj).getTabs();
    }
}//PaneItemBuilder

