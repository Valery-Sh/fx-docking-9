package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 * @param <T> specifies value type
 */
public class TreeItemEx extends TreeItem<ItemValue> {

    public TreeItemEx() {

    }

    public TreeItemEx(ItemValue value) {
        super(value);
    }

    public TreeItemEx(ItemValue value, Node graphic) {
        super(value, graphic);
    }

    public TreeItemBuilder getBuilder() {
        TreeItemBuilder retval = null;
        if (getValue().isPlaceholder()) {
            retval = getParent().getValue().getBuilder().getPlaceHolderBuilder((TreeItemEx) getParent());
        } else {
            retval = TreeItemBuilderRegistry.getInstance().getBuilder(getValue().getTreeItemObject());
        }
        return retval;
    }

    public TreeItemBuilder getPlaceHolderBuilder() {
        TreeItemBuilder builder = null;
        if (getValue().isPlaceholder()) {
            TreeItemEx p = (TreeItemEx) getParent();
            builder = p.getValue().getBuilder().getPlaceHolderBuilder(p);
        } else {
            builder = null;
        }
        return builder;
        //return ((TreeItemEx) getParent()).getBuilder().getPlaceHolderBuilder(this);
    }

    public Object getObject() {
        return getValue().getTreeItemObject();
    }

    public void removeObjectFromParent() {
        TreeViewEx.removeTreeItemObject(this);
    }

    public TreeItemEx treeItemOf(Object obj) {
        TreeItemEx retval = null;
        TreeItemEx t = (TreeItemEx) EditorUtil.findRootTreeItem(this);
        if (t == null) {
            return null;
        }
        t = (TreeItemEx) EditorUtil.findChildTreeItem(t, obj);
        if (t != null) {
            retval = t;
        }
        return retval;
    }
}
