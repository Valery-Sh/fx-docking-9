package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class DefaultTreeItemBuilder  extends AbstractTreeItemBuilder {

    public DefaultTreeItemBuilder() {
        init();
    }

    private void init() {
    }
    @Override
    public boolean isAcceptable(Object obj) {
        return false;
    }

    @Override
    public TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        return null;
    }

    @Override
    public void removeChildObject(Object parent, Object toRemove) {
        
    }

    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {
    }
    
    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
    }
 
}// DefaultTreeItemBuilder
