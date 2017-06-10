package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author Valery
 */
public class TabItemBuilder extends AbstractTreeItemBuilder {

    public TabItemBuilder() {
    }

    private void init() {

    }

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
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

    //@Override
/*    public TreeItem accept_OLD(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }
        Object value = dg.getGestureSourceObject();
        Tab tab = (Tab) ((ItemValue) target.getValue()).getTreeItemObject();

        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
            TreeItemEx treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
            if (treeItem instanceof TreeItemEx) {
                //notifyObjectRemove(treeView, treeItem);
                TreeViewEx.updateSourceSceneGraph(treeItem);
                treeView.removeTreeItem(treeItem);

                //notifyTreeItemRemove(treeView, treeItem);
            }
        } else if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof Node)) {
            TreeItemEx treeItem = EditorUtil.findTreeItemByObject(treeView, dg.getGestureSource());
            if (treeItem != null && treeItem.getParent() != null) {
                //
                // We must delete the item
                //
                //notifyObjectRemove(treeView, treeItem);
                treeView.updateSourceSceneGraph(treeItem);
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
*/
    @Override
    public void updateSourceSceneGraph(TreeItemEx parent, TreeItemEx child) {
        if (parent.getObject() != null && (parent.getObject() instanceof Tab)) {
            ((Tab) parent.getObject()).setContent(null);
        }
    }

/*    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }
*/
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

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        ItemValue v = target.getValue();
        ((Tab) v.getTreeItemObject()).setContent((Node) sourceObject);        
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if (!(item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof Tab))) {
            return;
        }
        Tab tab = (Tab) item.getValue().getTreeItemObject();
        unregisterChangeHandler(item);        
        TabChangeListener listener = new TabChangeListener(item);
        tab.contentProperty().addListener(listener);
        tab.getProperties().put(EditorUtil.CHANGE_LISTENER, listener);
        
    }

    @Override
    public void unregisterObjectChangeHandler(Object obj) {
        Tab tab = (Tab) obj;
        TabChangeListener listener = (TabChangeListener) tab.getProperties().get(EditorUtil.CHANGE_LISTENER);
        if ( listener == null ) {
            return;
        }
        tab.contentProperty().removeListener(listener);
        tab.getProperties().remove(EditorUtil.CHANGE_LISTENER);
        
    }

    public class TabChangeListener implements ChangeListener<Node> {

        private final TreeItemEx treeItem;

        public TabChangeListener(TreeItemEx treeItem) {
            this.treeItem = treeItem;
        }

        @Override
        public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            if (oldValue != null && newValue == null) {
                treeItem.getChildren().clear();
            } else if (oldValue == null && newValue != null) {
                TreeItemEx item = TreeItemBuilderRegistry.getInstance().getBuilder(newValue).build(newValue);
                treeItem.getChildren().add(item);
            }
        }

    }

}
