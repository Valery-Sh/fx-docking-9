package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public class DefaultTreeItemBuilder  implements TreeItemBuilder {

    public static final String CELL_UUID = "uuid-29a4b479-0282-41f1-8ac8-21b4923235be";
    public static final String NODE_UUID = "uuid-f53db037-2e33-4c68-8ffa-06044fc10f81";

    public DefaultTreeItemBuilder() {
        init();
    }

    private void init() {
    }

    public boolean isAcceptable(Object obj) {
        return false;
    }

/*    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        if (target.getValue().getTreeItemObject() == obj) {
            return false;
        }
        return isAcceptable(obj);
    }
*/
/*    public boolean isDragTarget() {
        return true;
    }
*/
/*    public boolean isDragPlace(TreeItem<ItemValue> target, TreeItem<ItemValue> place, Object source) {
        boolean retval = true;
        if (place.getValue().isPlaceholder() && target != null) {
            DefaultTreeItemBuilder builder = place.getValue().getBuilder();
            if (!(builder instanceof PlaceHolderBuilder) && place.getParent() != null) {
                builder = place.getParent().getValue().getBuilder().getPlaceHolderBuilder(place.getParent());
                System.err.println("   ---  1 IS DRAG PLACE = " + retval);
                retval = builder.isDragPlace(target, place, source);
            }

        } else if (target == null) {
            retval = false;
        }

        return retval;
    }
*/
    /**
     *
     * @param treeView the treeView/ Cannot be null
     * @param target the item which is an actual target item to accept a dragged
     * object
     * @param place the item which is a gesture target during the drag&drop
     * operation
     * @param dragObject an object which is an actual object to be accepted by
     * the target item.
     * @return true id the builder evaluates that a specified dragObject can be
     * accepted by the given target tree item
     */
    public boolean isAdmissiblePosition(TreeView treeView, TreeItem<ItemValue> target,
            TreeItem<ItemValue> place,
            Object dragObject) {
        System.err.println("target.getObj = " + target.getValue().getTreeItemObject());
        System.err.println("dragObject = " + dragObject);
        if (target.getValue().getTreeItemObject() == dragObject) {
            System.err.println("111111111111111111111111111111");
            return false;
        }
        
        TreeItem<ItemValue> dragItem = EditorUtil.findTreeItemByObject(treeView, dragObject);
        //
        // We do not want to insert the draggedItem befor or after itself
        //
        System.err.println("22222222222222222222222222");
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

    //public abstract TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource);
    //public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {    }
    @Override
    public void removeObject(Object parent, Object toRemove) {

    }

/*
    protected void notifyObjectRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null && toRemove != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            Object remove = ((ItemValue) toRemove.getValue()).getTreeItemObject();
            TreeItemRegistry.getInstance().getBuilder(parent).removeObject(parent, remove);
        }
    }


    protected void notifyTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        if (toRemove == null) {
            return;
        }
        TreeItem<ItemValue> parentItem = toRemove.getParent();
        if (parentItem != null) {
            Object parent = ((ItemValue) parentItem.getValue()).getTreeItemObject();
            TreeItemRegistry.getInstance().getBuilder(parent).removeItem(parentItem, toRemove);
        }
    }
*/
    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Node) {
            retval = createItem(obj);
        }
        return retval;
    }
    protected HBox getItemContentPane(TreeItem<ItemValue> item) {
        return (HBox) ((AnchorPane) item.getValue().getCellGraphic()).getChildren().get(0);
    }
    public String getStyle() {
        return "-fx-backGround-color: aqua";
    }

    @Override
    public Node createItemContent(Object obj, Object... others) {
        return createDefaultContent(obj, others);
    }

    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }


    public static interface PlaceHolderBuilder {

    }
}// DefaultTreeItemBuilder
