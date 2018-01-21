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

    TreeItemEx createItem(Object obj);
    
    void updateTreeItemContent(TreeItemEx item, Object oldValue, Object newValue); 
    
    Node createItemContent(Object obj);

    void updateOnMove(TreeItemEx item);

    void registerChangeHandler(TreeItemEx item);

    void unregisterChangeHandler(TreeItemEx source);

    void unregisterObjectChangeHandler(TreeItemEx item);

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
        //System.err.println("TreeItemBuilder isAdmissiblePosition 1 " );
        TreeItemEx dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        //
        // We do not want to insert the draggedItem before or after itself
        //
        if (target == place.getParent() && dragItem != null) {
            if (dragItem == place || dragItem.previousSibling() == place) {
                //System.err.println("TreeItemBuilder isAdmissiblePosition 2 " + ((TreeItemEx)dragItem).getObject() );

                return false;
            }
        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            int level = treeView.getTreeItemLevel(target) + 1;
            TreeItem<ItemValue> actualPlace = EditorUtil.parentOfLevel(treeView, place, level);
            if (dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
//                System.err.println("builder 2");
                //System.err.println("TreeItemBuilder isAdmissiblePosition 3 " );

                return false;
            }
        }
        //System.err.println("TreeItemBuilder isAdmissiblePosition 4 " );

        return isAcceptable(target.getObject(), dragObject);
    }

    //boolean isAcceptable(Object obj);
    boolean isAcceptable(Object target, Object accepting);

    @FunctionalInterface
    public interface PlaceholderBuilder {

        //TreeItemEx createPlaceholder(Object obj);
        TreeItemEx buildPlaceholder(Object obj);
        
        public static TreeItemEx createPlaceholder(int placeholderId, Object placeholderParent, Object obj) {
            TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(placeholderParent);
            if (!(b instanceof PlaceholderBuilderFactory)) {
                return null;
            }
            PlaceholderBuilder pb = ((PlaceholderBuilderFactory) b).getPlaceholderBuilder(placeholderId);
            TreeItemEx retval = pb.buildPlaceholder(obj);            
            retval.setPlaceholderBuilder((TreeItemBuilder) pb);
            
            return pb.buildPlaceholder(obj);
        }
    }

    @FunctionalInterface
    public interface PlaceholderBuilderFactory {

        PlaceholderBuilder getPlaceholderBuilder(int placeholderId);

        public static PlaceholderBuilder getPlaceholderBuilder(int placeholderId, Object placeholderParent) {
            TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(placeholderParent);
            if (!(b instanceof PlaceholderBuilderFactory)) {
                return null;
            }
            return ((PlaceholderBuilderFactory) b).getPlaceholderBuilder(placeholderId);
        }

    }

}
