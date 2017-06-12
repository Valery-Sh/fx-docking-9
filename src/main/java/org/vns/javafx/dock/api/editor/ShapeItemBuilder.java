package org.vns.javafx.dock.api.editor;

import javafx.scene.shape.Shape;

/**
 *
 * @author Valery
 */
public class ShapeItemBuilder extends AbstractTreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Shape) {
            retval = createItem((Shape) obj);
        }
        return retval;
    }

    @Override
        public boolean isAcceptable(Object target, Object accepting) {
        return false;
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        
    }

    @Override
    public void updateOnMove(TreeItemEx child) {
        //
        // remove listeners fron source and all it's children
        //
        TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(child.getObject());
        b.unregisterChangeHandler(child);
        
    }
}
