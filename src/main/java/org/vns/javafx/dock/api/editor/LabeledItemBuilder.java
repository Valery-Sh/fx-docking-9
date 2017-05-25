package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

/**
 *
 * @author Valery
 */
public class LabeledItemBuilder extends DefaultTreeItemBuilder {

    private final LabelPlaceholderBuilder placeholderBuilder;

    public LabeledItemBuilder() {
        this.placeholderBuilder = new LabelPlaceholderBuilder();
    }

    private void init() {

    }

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Labeled) {
            Labeled node = (Labeled) obj;
            retval = createItem((Node) obj);

            if (node.getGraphic() != null) {
                TreeItem placeholder = createPlaceHolders(node.getGraphic())[0];
                retval.getChildren().add(placeholder);
            }
        }
        return retval;
    }

    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = ((Labeled) obj).getText();
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     *
     * @param obj an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object obj) {
        return obj != null && (obj instanceof Node);
    }

    @Override
    public boolean isAdmissiblePosition(TreeView treeView, TreeItem<ItemValue> target,
            TreeItem<ItemValue> place,
            Object dragObject) {
//        System.err.println("Labeled isAdmissiblePosition");
        boolean retval = super.isAdmissiblePosition(treeView, target, place, dragObject);
        if (!retval) {
            return false;
        }
//       System.err.println("=== LabelIBuilderplace.getParent() obj = " + place.getParent().getValue().getTreeItemObject());
//       System.err.println("=== LabelIBuilder target. obj = " + target.getValue().getTreeItemObject());
        
        if (place.getParent() == target) {
            return false;
        }
        Labeled lb = (Labeled) place.getValue().getTreeItemObject();
        if (place == target && lb.getGraphic() != null) {
            return false;
        }
        return true;
    }

    /**
     * /**
     * Checks whether the specified object is not null and is an instance of
     * Node and the specified target doesn't have children. The method returns {@literal false
     * } if one of the following conditions is not satisfied:
     * <ul>
     * <li>The method
     * {@link #isAcceptable(java.lang.Object) returns {@literal false}</li>
     * <li>The specified {@literal target} has children. This means that the
     *        {@literal Labeled} node has already it's {@literal  graphic}
     *         value set to not null value   </li>
     * </ul>
     *
     * @param target the TreeItem object witch corresponds to the 
     *           {@literal  Labeled node}.
     * @param obj an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node and the specified target doesn't have children
     */
    /*    @Override
    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        if ( target.getValue().getTreeItemObject() == obj ) {
            return false;
        }
        return isAcceptable(obj) && target.getChildren().isEmpty();
    }
     */
    @Override
    public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        //if (isAcceptable(target, value)) {
        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
            TreeCell cell = (TreeCell) dg.getGestureSource();
            if (cell.getTreeItem() instanceof TreeItemEx) {
                notifyObjectRemove(treeView, cell.getTreeItem());
                notifyTreeItemRemove(treeView, cell.getTreeItem());

                //cell.getTreeItem().getParent().getChildren().removeObject(cell.getTreeItem());
            }
        }

        ItemValue v = (ItemValue) target.getValue();

        retval = (TreeItemEx) createPlaceHolders(value)[0];
        target.getChildren().add(retval);

        ((Labeled) v.getTreeItemObject()).setGraphic((Node) value);
        //}
        return retval;
    }

    /*    @Override
    public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        Object obj = toRemove.getParent().getValue().getTreeItemObject();
        if (obj instanceof Labeled) {
            ((Labeled) obj).setGraphic(null);
        }
    }
     */
    @Override
    public void removeObject(Object parent, Object toRemove) {
        if (parent instanceof Labeled) {
            ((Labeled) parent).setGraphic(null);
        }
    }

    @Override
    public void removeItem(TreeItem<ItemValue> parent, TreeItem<ItemValue> toRemove) {
        parent.getChildren().remove(toRemove);
    }

    protected TreeItem[] createPlaceHolders(Object obj) {
        return new TreeItem[]{placeholderBuilder.build(obj)};
    }

    @Override
    public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return placeholderBuilder;
    }

    public static class LabelPlaceholderBuilder extends DefaultTreeItemBuilder implements PlaceholderBuilder {

        @Override
        public TreeItem build(Object obj) {
            TreeItem retval = createItem(obj);
            return retval;
        }

        @Override
        protected Node createDefaultContent(Object obj, Object... others) {
            //HBox hb = new HBox();
            Label label = new Label();
            HBox hb = new HBox(label);
            label.getStyleClass().add("labeled-insert-graphic");
            if (obj != null) {

                Label glb = new Label(obj.getClass().getSimpleName());

                glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
                hb.getChildren().add(glb);
                if (obj instanceof Labeled) {
                    glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
                }
            }
            return hb;
        }

        @Override
        public boolean isAcceptable(Object obj) {
            return obj != null && (obj instanceof Node);
        }

        @Override
        public TreeItem accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {

            TreeItem retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object obj = dg.getGestureSourceObject();
            //if (isAcceptable(target, obj)) {
            retval = TreeItemRegistry.getInstance().getBuilder(obj).build(obj);
//            System.err.println("!!! TreeItemRegistry.getBuilder = " + TreeItemRegistry.getInstance().getBuilder(obj));
            ((ItemValue) retval.getValue()).setPlaceholder(true);

            target.getChildren().add(retval);
            ItemValue v = (ItemValue) target.getValue();
            ((Labeled) v.getTreeItemObject()).setGraphic((Node) obj);

            //}
            return retval;
        }
    }
}
