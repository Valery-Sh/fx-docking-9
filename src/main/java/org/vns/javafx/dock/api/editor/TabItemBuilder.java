package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author Valery
 */
public class TabItemBuilder extends DefaultTreeItemBuilder {

    public TabItemBuilder() {
    }

    private void init() {

    }

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        if (obj instanceof Tab) {
            Tab tab = (Tab) obj;
            retval = createItem((Tab) obj);
            if (tab.getContent() != null) {
                TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(tab.getContent());
                retval.getChildren().add(b.build(tab.getContent()));
            }
        }
        return retval;
    }

    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = ((Tab) obj).getText();
        text = text == null ? "" : text;
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
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItem retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        Tab tab = (Tab) ((ItemValue) target.getValue()).getTreeItemObject();

        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
            TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
            if (treeItem instanceof TreeItemEx) {
                //notifyObjectRemove(treeView, treeItem);
                treeView.removeTreeItemObject(treeItem);
                treeView.removeTreeItem(treeItem);
                
                //notifyTreeItemRemove(treeView, treeItem);
            }
        } else if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof Node)) {
            TreeItem<ItemValue> treeItem = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSource());
            if (treeItem != null && treeItem.getParent() != null) {
                //
                // We must delete the item
                //
                //notifyObjectRemove(treeView, treeItem);
                treeView.removeTreeItemObject(treeItem);
                treeView.removeTreeItem(treeItem);
                
                //notifyTreeItemRemove(treeView, treeItem);
            }
        }

        retval = TreeItemBuilderRegistry.getInstance().getBuilder(value).build(value);
        if (!target.getChildren().isEmpty()) {
            target.getChildren().clear();
        }
        target.getChildren().add(retval);
        tab.setContent((Node) value);
        Node n = (Node) value;
        return retval;
    }

    @Override
    public void removeChildObject(Object parent, Object toRemove) {
        if (parent != null && (parent instanceof Tab)) {
            ((Tab) parent).setContent(null);
        }
    }

    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }

    @Override
    public boolean isAdmissiblePosition(TreeView treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        boolean retval = super.isAdmissiblePosition(treeView, target, place, dragObject);
        if (!retval) {
            return false;
        }
        if (place != target) {
            return false;
        }
        Tab tab = (Tab) place.getValue().getTreeItemObject();
        if (tab.getContent() != null) {
            return false;
        }
        return true;
    }

}
