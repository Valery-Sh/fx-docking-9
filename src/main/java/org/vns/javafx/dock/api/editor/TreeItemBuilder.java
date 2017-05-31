package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphView.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public interface TreeItemBuilder {

    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";
    
    default TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        return null;
    }
    
    TreeItem build(Object obj);

    default TreeItem createItem(Object obj, Object... others) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);

        itv.setTreeItemObject(obj);
        itv.setCellGraphic(anchorPane);

        item.setValue(itv);
        box.getChildren().add(createItemContent(obj, others));

        return item;
    }
    
    Node createItemContent(Object obj, Object... others);    
    
    default void removeObject(Object parent, Object toRemove) {
        
    }

    default void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {
        parent.getChildren().remove(toRemove);
    }
    
    default TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return null;
    }
    

    default void notifyObjectRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null && toRemove != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            Object remove = ((ItemValue) toRemove.getValue()).getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parent).removeObject(parent, remove);
        }
    }


    default void notifyTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        if (toRemove == null) {
            return;
        }
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            TreeItemBuilderRegistry.getInstance().getBuilder(parent).removeItem(parentItem, toRemove);
        }
    }
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
    default boolean isAdmissiblePosition(TreeView treeView, TreeItem<ItemValue> target,
            TreeItem<ItemValue> place,
            Object dragObject) {
        if (target.getValue().getTreeItemObject() == dragObject) {
            return false;
        }
        
        TreeItem<ItemValue> dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        //
        // We do not want to insert the draggedItem before or after itself
        //
        if (target == place.getParent() && dragItem != null) {
            if ( dragItem == place || dragItem.previousSibling() == place) {
                return false;
            }
        } else if (treeView.getTreeItemLevel(place) - treeView.getTreeItemLevel(target) > 1 && dragItem != null) {
            int level = treeView.getTreeItemLevel(target) + 1;
            TreeItem<ItemValue> actualPlace = EditorUtil.parentOfLevel(treeView, place, level);
            if ( dragItem == actualPlace || dragItem.previousSibling() == actualPlace) {
                return false;
            }
        }
        
        return isAcceptable(dragObject);
    }
    boolean isAcceptable(Object obj);
}
