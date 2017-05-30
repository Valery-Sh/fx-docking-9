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
 * @param <T>
 */
public abstract class AbstractListBasedTreeItemBuilder<T> extends DefaultTreeItemBuilder {

    protected List<T> getList(TreeItem<ItemValue> target) {
        return getList(target.getValue().getTreeItemObject());
    }

    protected abstract List<T> getList(Object obj);

    /**
     * Tries to to find an object of type {@code TreeItem} in the specified 
     * {@link TreeViewEx } which corresponds to an object specified by the
     * {@code value} parameter.
     *
     * @param treeView the node to search in
     * @param target the target TreeItem where the new TreeItem should be place
     * as a children
     * @param place the object of type TreeItem which represents a drag target
     * TreeCell
     * @param value the object to search
     *
     * @return an index in the collection of children in the target TreeItem
     * used to insert a new TreeItem for the object specified by the value
     * parameter.
     *
     *
     */
    protected int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, T value) {
        if (target.getValue().getTreeItemObject() == value) {
            return -1;
        }
        TreeItem<ItemValue> sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;

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
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the las TreeItemof it's parent
                //
                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {
                    parent = parent.getParent();
                }
            }
            idx = target.getChildren().indexOf(parent) + 1;
        }
        return idx;
    }

    /**
     * Tries to calculate an index in the children collection of the item
     * specified by the parameter {@code target } where a new item can be
     * inserted.
     *
     * @param treeView the node to search in
     * @param target the target TreeItem where the new TreeItem should be place
     * as a children
     * @param place the object of type TreeItem which represents a drag target
     * TreeCell
     *
     * @return an index in the collection of children in the target TreeItem
     * used to insert a new TreeItem
     */
    protected int getIndex(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place) {
        int idx = -1;

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
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the las TreeItemof it's parent
                //

                while (treeView.getTreeItemLevel(parent) - targetLevel > 1) {
                    parent = parent.getParent();
                }
            }
            idx = target.getChildren().indexOf(parent) + 1;
        }
        return idx;
    }

    @Override
    public TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        System.err.println("accept target = " + target.getValue().getTreeItemObject());
        System.err.println("accept place  = " + place.getValue().getTreeItemObject());

        if (target != null && place != null && value != null) {
            int idx = getIndex(treeView, target, place, value);

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                TreeCell cell = (TreeCell) dg.getGestureSource();
                if (cell.getTreeItem() instanceof TreeItemEx) {
                    targetBuilder.notifyObjectRemove(treeView, cell.getTreeItem());
                    targetBuilder.notifyTreeItemRemove(treeView, cell.getTreeItem());
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                if (idx >= 0 && (value instanceof Node)) {
                    item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    if (item == null) {
                        return null;
                    }
                    targetBuilder.notifyObjectRemove(treeView, item);
                    targetBuilder.notifyTreeItemRemove(treeView, item);

                } else if (idx < 0) {
                    ChildrenNodeRemover r = (ChildrenNodeRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove(dg.getGestureSource());
                    }
                }
            }
            System.err.println("1) ACCEPT idx=" + idx + "; size=" + getList(target).size());
            
            idx = getIndex(treeView, target, place);
            retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
            target.getChildren().add(idx, retval);
            getList(target).add(idx, value);
            System.err.println("2) ACCEPT idx=" + idx + "; size=" + getList(target).size() + "; list=" + getList(target));
            int sz = ((Pane)target.getValue().getTreeItemObject()).getChildren().size();
            System.err.println("3) ACCEPT pane.size=" +sz);
            
        }
        return retval;
    }

/*    public TreeItem accept_OLD(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        System.err.println("accept target = " + target.getValue().getTreeItemObject());
        System.err.println("accept place  = " + place.getValue().getTreeItemObject());

        boolean objRemoved = true;

        if (target != null && place != null && value != null) {
            int idx = getIndex(treeView, target, place, value);

            if (!getList(target).contains(value)) {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        targetBuilder.notifyObjectRemove(treeView, cell.getTreeItem());
                        targetBuilder.notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null) {
                    //Node it = (Node) dg.getGestureSourceObject();
                    TreeItem item;
                    if (idx >= 0 && (value instanceof Node)) {
                        item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                        if (item == null) {
                            return null;
                        }
                        targetBuilder.notifyObjectRemove(treeView, item);
                        targetBuilder.notifyTreeItemRemove(treeView, item);

                    } else if (idx < 0) {
                        ChildrenNodeRemover r = (ChildrenNodeRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                        if (r != null) {
                            objRemoved = r.remove(dg.getGestureSource());
                        }

                    }
                }
                if (!objRemoved) {
                    System.err.println("0) NOTREMOVED");
                    //return null;
                }
                System.err.println("0) EMOVED");
                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                if (idx < 0) {
                    idx = getIndex(treeView, target, place);
                }
                target.getChildren().add(idx, retval);
                getList(target).add(idx, value);
            } else {
                if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                    TreeCell cell = (TreeCell) dg.getGestureSource();
                    if (cell.getTreeItem() instanceof TreeItemEx) {
                        targetBuilder.notifyObjectRemove(treeView, cell.getTreeItem());
                        targetBuilder.notifyTreeItemRemove(treeView, cell.getTreeItem());
                    }
                } else if (dg.getGestureSource() != null) {
                    TreeItem item;
                    if (idx >= 0 && (value instanceof Node)) {
                        item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                        if (item == null) {
                            return null;
                        }
                        targetBuilder.notifyObjectRemove(treeView, item);
                        targetBuilder.notifyTreeItemRemove(treeView, item);

                    } else if (idx < 0) {
                        ChildrenNodeRemover r = (ChildrenNodeRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                        if (r != null) {
                            objRemoved = r.remove(dg.getGestureSource());
                        }
                    }
                }
                if (!objRemoved) {
                    System.err.println("NOT REMOVED OBJECT");
                    //return null;
                }
                System.err.println("REMOVED OBJECT");
                idx = getIndex(treeView, target, place);
                retval = TreeItemRegistry.getInstance().getBuilder(value).build(value);
                target.getChildren().add(idx, retval);

                getList(target).add(idx, value);
            }
        }
        return retval;
    }
*/
    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        List<T> children = getList(obj);
        retval = createItem(obj);
        for (T it : children) {
            TreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(it);
            //if ( it instanceof ComboBox ) {
            retval.getChildren().add(gb.build(it));
            //}
        }
        return retval;
    }

    @Override
    public void removeObject(Object parent, Object toRemove) {
        //if (parent instanceof Pane) {
        //((Pane) parent).getChildren().remove(toRemove);
        getList(parent).remove(toRemove);
        //}
    }

}
