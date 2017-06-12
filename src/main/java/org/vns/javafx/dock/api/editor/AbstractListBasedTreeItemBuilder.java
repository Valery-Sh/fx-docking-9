package org.vns.javafx.dock.api.editor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import static org.vns.javafx.dock.api.editor.SceneGraphView.FIRST;

/**
 *
 * @author Valery
 * @param <T> the parameter
 */
public abstract class AbstractListBasedTreeItemBuilder<T> extends AbstractTreeItemBuilder {

    /*    protected List<T> getList(TreeItemEx target) {
        //AbstractListBasedTreeItemBuilder.this.
        return getList(target.getValue().getTreeItemObject());
    }
     */
    protected abstract ObservableList<T> getList(Object obj);

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
        TreeItemEx sourceItem = EditorUtil.findTreeItemByObject(treeView, value);
        if (sourceItem == null) {
            return -1;
        }
        int idx = -1;

        int valueIdx = getList(target.getObject()).indexOf(value);

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST && valueIdx == 0) {
                idx = -1;
            } else if (q == FIRST) {
                idx = 0;
            } else if (valueIdx == getList(target.getObject()).size()) {
                idx = -1;
            } else {
                idx = getList(target.getObject()).size();
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
    protected int getIndex(TreeView treeView, TreeItemEx target, TreeItemEx place) {
        int idx = -1;

        if (target == place) {
            int q = place.getValue().getDragDropQualifier();

            if (q == FIRST) {
                idx = 0;
            } else {
                idx = getList(target.getObject()).size();
            }
        } else {
            int targetLevel = treeView.getTreeItemLevel(target);
            int placeLevel = treeView.getTreeItemLevel(place);
            TreeItem<ItemValue> parent = place;
            if (targetLevel - placeLevel != 1) {
                //
                // Occurs when place is the last TreeItem of it's parent
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
    protected Object createAndAddListener(TreeItemEx item) {
        BuilderListChangeListener listener = new BuilderListChangeListener(item);
        
        getList(item.getObject()).addListener(listener);
        return listener;
    }

    @Override
    protected void removelistener(TreeItemEx item, Object listener) {
        getList(item.getObject()).removeListener((ListChangeListener<? super T>) listener);
    }
    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        int idx = getIndex(treeView, target, place);
        getList(target.getObject()).add(idx, (T)sourceObject);
    }
    @Override
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        //((Pane) parent.getObject()).getChildren().remove(child.getObject());
        getList(parent.getObject()).remove(child.getObject());
        //
        // remove listeners fron source and all it's children
        //
        TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(child.getObject());
        b.unregisterChangeHandler(child);
                
    }
    @SuppressWarnings("unchecked")
    public Class<T> getTypeParameterClass() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    /*    public TreeItem update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        //TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        if (target != null && place != null && value != null) {

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    updateOnMove((TreeItemEx) treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                if ( ! updateOnMove(treeView, value) ) {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }                    
            }
        }
    
        update(treeView, target.getObject(), place.getObject(), value);
        return retval;
    }
     */
 /*    protected void updateOnMove(TreeItemEx sourceTreeItem) {
        TreeItem<ItemValue> parentItem = sourceTreeItem.getParent();
        if (parentItem != null && sourceTreeItem != null) {
            Object parent = parentItem.getValue().getTreeItemObject();
            Object remove = sourceTreeItem.getValue().getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parent).updateOnMove(parent, remove);
        }
    }
     */
 /*    public boolean updateOnMove(TreeViewEx treeView, Object sourceObject) {
       boolean retval = false;
        TreeItemEx sourceTreeItem = EditorUtil.findTreeItemByObject(treeView, sourceObject);
        if (sourceTreeItem != null) {
            updateOnMove(sourceTreeItem);
            retval = true;
        }
        return retval;
    }
     */
 /*    public TreeItem accept_OLD(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;
        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);

        T value = (T) dg.getGestureSourceObject();
        //TreeItemBuilder targetBuilder = target.getValue().getBuilder();

        if (target != null && place != null && value != null) {

            int idx = -1; //getIndex(treeView, target, place, value);

            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
                TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
                if (treeItem instanceof TreeItemEx) {
                    treeView.updateOnMove((TreeItemEx) treeItem);
                }
            } else if (dg.getGestureSource() != null) {
                TreeItem item;
                idx = getIndex(treeView, target, place, value);
                if (idx >= 0) {
                    item = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSourceObject());
                    if (item == null) {
                        return null;
                    }
                    //targetBuilder.notifyObjectRemove(treeView, item);
                    treeView.updateOnMove((TreeItemEx) item);
                    //treeView.removeTreeItem(item);

                    //targetBuilder.notifyTreeItemRemove(treeView, item);
                } else if (idx < 0) {
                    ChildrenRemover r = (ChildrenRemover) dg.getGestureSource().getProperties().get(EditorUtil.REMOVER_KEY);
                    if (r != null) {
                        r.remove();
                    }
                }
            }

            idx = getIndex(treeView, target, place);

            getList(target).add(idx, value);
        }
        return retval;
    }
     */
    @Override
    public TreeItemEx build(Object obj) {

        TreeItemEx retval = null;
        //List<T> children = getList(obj);        
        List<T> children = getList(obj);
        //List<T> children = AbstractListBasedTreeItemBuilder.this.getList(obj);
        if ( obj instanceof ListView ) {
            System.err.println("1 listView " + obj);
        }
        retval = createItem(obj);
        for (T it : children) {
            System.err.println("2 listView " + it);
            TreeItemBuilder gb = TreeItemBuilderRegistry.getInstance().getBuilder(it);
            System.err.println("3 listView " + it);

            retval.getChildren().add(gb.build(it));
        }
        return retval;
    }

    /*    @Override
    public void updateOnMove(Object parent, Object toRemove) {
        getList(parent).remove(toRemove);

    }
     */

 /*    @Override
    protected void registerChangeHandler(Object obj, Object[] others) {
        
    }    
     */
    public class BuilderListChangeListener implements ListChangeListener<T> {

        private final TreeItem<ItemValue> treeItem;

        public BuilderListChangeListener(TreeItem<ItemValue> treeItem) {
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
                        for (TreeItem<ItemValue> it : treeItem.getChildren()) {
                            if (it.getValue().getTreeItemObject() == elem) {
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
        }
    }

}
