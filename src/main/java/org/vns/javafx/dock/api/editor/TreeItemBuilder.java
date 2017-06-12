package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author Valery
 */
public interface TreeItemBuilder {
    public static final String ACCEPT_TYPES_KEY = "tree-item-builder-accept-types";
    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

/*    default TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        return null;
    }
*/
    TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource);    
    //TreeItem buildRoot(Object obj);
    TreeItemEx build(Object obj);
    TreeItemEx createItem(Object obj, Object... others);
    

    Node createItemContent(Object obj, Object... others);
    /**
     * Removes the specified {@code toRemove} object from the given 
     * {@code parent} one.
     * The {@code parent object} can be of any type. For example it can
     * represent a node of type {@code VBox}. The {@code toRemove} also can be of 
     * any type for example a node of type {@code Button}. Then the method 
     * will try to remove the button from the children collection of the VBox pane.
     * 
     * @param item  the object to be removed.
     */
    void updateOnMove(TreeItemEx item);
    //void updateOnMove(TreeItemEx parent, TreeItemEx child);
    //void accept(Object targetParent, Object targetPlace, Object treeItemObject);

    /**
     * Removes the specified {@code toRemove} tree item from the given 
     * {@code parent} one.
     * The {@code parent} item can represent the object of type {@code Node} or
     * any other type, for example {@code Tab} element of {@code TabPane}.
     * Usually it will be enough to remove the item from it's parent 
     * applying the code:
     * <pre>
     *    toRemove.getParent().getChildren().remove(toRemove)
     * </pre>
     * However, in more complex cases, this will not be enough.
     * @param parent the parent object to remove from
     * @param toRemove  the object to be removed.
     */
    //void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove);
    void registerChangeHandler(TreeItemEx item);
    void unregisterChangeHandler(TreeItemEx source);
    void unregisterObjectChangeHandler(TreeItemEx item);
    //void removeChangeHandler(Node node);
    //Object getChangeHandler(Node node);

    default TreeItemBuilder getPlaceHolderBuilder(TreeItemEx placeHolder) {
        return null;
    }
    /**
     * Removes the object specified by the parameter {@code toRemove}.
     * When a tree item @code toRemove} is dragged and dropped on another
     * item {@code item2} 
     * @param treeView
     * @param toRemove 
     */
/*    default void notifyObjectRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null && toRemove != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            Object remove = ((ItemValue) toRemove.getValue()).getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parent).updateOnMove(parent, remove);
        }
    }
*/
/*    default void notifyTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        if (toRemove == null) {
            return;
        }
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parent).removeChildTreeItem(parentItem, toRemove);
        }
    }
*/
    /**
     *
     * @param treeView the treeView/ Cannot be null
     * @param target the item which is an actual target item to accept a dragged
     * object
     * @param place the item which is a gesture target during the drag-and-drop
     * operation
     * @param dragObject an object which is an actual object to be accepted by
     * the target item.
     * @return true id the builder evaluates that a specified dragObject can be
     * accepted by the given target tree item
     */
    default boolean isAdmissiblePosition(TreeView treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        if (target.getValue().getTreeItemObject() == dragObject) {
            return false;
        }
        System.err.println("TreeItemBuilder isAdmissiblePosition 1 " );
        TreeItem<ItemValue> dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        //
        // We do not want to insert the draggedItem before or after itself
        //
        if (target == place.getParent() && dragItem != null) {
            if (dragItem == place || dragItem.previousSibling() == place) {
        System.err.println("TreeItemBuilder isAdmissiblePosition 2 " + ((TreeItemEx)dragItem).getObject() );

                return false;
            }
        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            int level = treeView.getTreeItemLevel(target) + 1;
            TreeItem<ItemValue> actualPlace = EditorUtil.parentOfLevel(treeView, place, level);
            if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
//                System.err.println("builder 2");
        System.err.println("TreeItemBuilder isAdmissiblePosition 3 " );

                return false;
            }
        }
        System.err.println("TreeItemBuilder isAdmissiblePosition 4 " );
        
        return isAcceptable(target.getObject(),dragObject);
    }

    //boolean isAcceptable(Object obj);
    boolean isAcceptable(Object target,Object accepting);
    
    
}
