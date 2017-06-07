package org.vns.javafx.dock.api.editor;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.vns.javafx.dock.api.editor.DragManager.ChildrenRemover;
import static org.vns.javafx.dock.api.editor.SceneGraphView.FIRST;

/**
 *
 * @author Valery
 * @param <T> the parameter
 */
public abstract class AbstractListBasedTreeItemBuilder<T> extends AbstractTreeItemBuilder {


    protected List<T> getList(TreeItemEx target) {
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
    protected int getIndex(TreeView treeView, TreeItemEx target, TreeItemEx place, T value) {
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
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        //TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        if (target != null && place != null && value != null) {

            int idx = getIndex(treeView, target, place, value);

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    //targetBuilder.notifyObjectRemove(treeView, treeItem);
                    treeView.removeTreeItemObject(treeItem);
                    //treeView.removeTreeItem(treeItem);

                    //targetBuilder.notifyTreeItemRemove(treeView, treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                if (idx >= 0) {
                    item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    if (item == null) {
                        return null;
                    }
                    //targetBuilder.notifyObjectRemove(treeView, item);
                    treeView.removeTreeItemObject(item);
                    //treeView.removeTreeItem(item);

                    //targetBuilder.notifyTreeItemRemove(treeView, item);
                } else if (idx < 0) {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        //r.remove(dg.getGestureSource());
                        r.remove();
                    }
                }
            }

            idx = getIndex(treeView, target, place);
            //retval = TreeItemBuilderRegistry.getInstance().getBuilder(value).build(value);

            //target.getChildren().add(idx, retval);
            //System.err.println("--- idv = " + idx + "; getList.size=" + getList(target).size());
            
            getList(target).add(idx, value);
        }
        return retval;
    }

    @Override
    public TreeItem build(Object obj) {

        TreeItem retval = null;
        List<T> children = getList(obj);

        retval = createItem(obj);
        for (T it : children) {
            TreeItemBuilder gb = TreeItemBuilderRegistry.getInstance().getBuilder(it);
            retval.getChildren().add(gb.build(it));
        }
        return retval;
    }

    @Override
    public void removeChildObject(Object parent, Object toRemove) {
        getList(parent).remove(toRemove);
    }

    /*    @Override
    protected void registerChangeHandler(Object obj, Object[] others) {
        
    }    
     */
    public class BuilderListChangeListener implements ListChangeListener<T> {
        private final TreeItem<ItemValue> treeItem;
        
        public BuilderListChangeListener(TreeItem<ItemValue> treeItem ) {
            this.treeItem = treeItem;
        } 
        
        @Override
        public void onChanged(Change<? extends T> change) {
            Object ooo = treeItem.getValue().getTreeItemObject();
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends T> list = change.getRemoved();
                    if (!list.isEmpty()) {
                    }
                    for (T elem : list) {
                        TreeItem toRemove = null;
                        for ( TreeItem<ItemValue> it : treeItem.getChildren() ) {
                            if ( it.getValue().getTreeItemObject() == elem) {
                                toRemove = it;
                                break;
                            }
                        }
                        treeItem.getChildren().remove(toRemove);
                        //unregisterChangeHandler(treeItem);
                    }

                }
                if (change.wasAdded()) {
                    List<? extends T> list = change.getAddedSubList();
                    List itemList = new ArrayList();
                    if (!list.isEmpty()) {
                    }
                    for (T elem : list) {
                        TreeItem it = TreeItemBuilderRegistry.getInstance().getBuilder(elem).build(elem);
                        itemList.add(it);
                    }
                    treeItem.getChildren().addAll(change.getFrom(), itemList);
                }
            }//while
            //update();

        }
    }

}
