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
public class LabeledItemBuilder extends TreeItemBuilder {

    private final PlaceholderBuilder placeholderBuilder;

    public LabeledItemBuilder() {
        this.placeholderBuilder = new PlaceholderBuilder();
    }

    private void init() {

    }

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
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
    protected Node createDefaultContent(Object obj) {
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
    @Override
    public boolean isAcceptable(TreeItem<ItemValue> target, Object obj) {
        return isAcceptable(obj) && target.getChildren().isEmpty();
    }

    @Override
    public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        if (isAcceptable(target, value)) {
            if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeCell)) {
                TreeCell cell = (TreeCell) dg.getGestureSource();
                if (cell.getTreeItem() instanceof TreeItemEx) {
                    System.err.println("Accept Labeled before Notify");
                    notifyTreeItemRemove(treeView, cell.getTreeItem());
                    cell.getTreeItem().getParent().getChildren().remove(cell.getTreeItem());
                }
            }

            ItemValue v = (ItemValue) target.getValue();

            retval = (TreeItemEx) createPlaceHolders(value)[0];
            target.getChildren().add(retval);

            ((Labeled) v.getTreeItemObject()).setGraphic((Node) value);
        }
        return retval;
    }

    @Override
    public void childrenTreeItemRemove(TreeView treeView, TreeItem<ItemValue> toRemove) {
        System.err.println("Labelled obj 1");
        Object obj = toRemove.getParent().getValue().getTreeItemObject();
        System.err.println("Labelled obj=" + obj);
        if (obj instanceof Labeled) {
            ((Labeled) obj).setGraphic(null);
        }
    }

    /*    @Override
    public boolean accept(Object target, Object place, Object value) {
        boolean retval = true;
        TreeItem item;
        if (isAcceptable(value) && (value instanceof Node)) {
            item = TreeItemRegistry.getInstance().getBuilder(value).build(value);
            //target.getChildren().add(retval);
            ItemValue v = (ItemValue)target.getValue();
            ((Labeled)v.getTreeItemObject()).setGraphic((Node) value);
        }
        
        return retval;
    }
     */

    @Override
    public TreeItem[] createPlaceHolders(Object obj) {
        return new TreeItem[]{placeholderBuilder.build(obj)};
        //placeholderBuilder.build(null);
    }

    @Override
    public boolean hasPlaceHolders() {
        return true;
    }

    @Override
    public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
        return placeholderBuilder;
    }

    public static class PlaceholderBuilder extends TreeItemBuilder {

        @Override
        public TreeItemEx build(Object obj) {
            TreeItemEx retval = createItem(obj);
            return retval;
        }

        @Override
        protected Node createDefaultContent(Object obj) {
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
        public TreeItemEx accept(TreeView treeView, TreeItem<ItemValue> target, TreeItem<ItemValue> place, Node gestureSource) {

            TreeItemEx retval = null;
            DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
            if (dg == null) {
                return retval;
            }
            Object obj = dg.getGestureSourceObject();
            if (isAcceptable(target, obj)) {
                retval = TreeItemRegistry.getInstance().getBuilder(obj).build(obj);

                ((ItemValue) retval.getValue()).setPlaceholder(true);

                target.getChildren().add(retval);
                ItemValue v = (ItemValue) target.getValue();
                ((Labeled) v.getTreeItemObject()).setGraphic((Node) obj);

            }
            return retval;
        }

        @Override
        public boolean isDragTarget() {
            return true;
        }

        @Override
        public boolean hasPlaceHolders() {
            return false;
        }

        @Override
        public TreeItemBuilder getPlaceHolderBuilder(TreeItem placeHolder) {
            return null;
        }

    }
}
