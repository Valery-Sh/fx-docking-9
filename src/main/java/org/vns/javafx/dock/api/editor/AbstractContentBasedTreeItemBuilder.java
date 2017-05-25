package org.vns.javafx.dock.api.editor;

import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public abstract class AbstractContentBasedTreeItemBuilder<T> extends DefaultTreeItemBuilder {

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        retval = createItem(obj);
        if ( getContent(obj) != null ) {
            TreeItem it = TreeItemRegistry.getInstance().getBuilder(obj).createItem(getContent(obj));
            retval.getChildren().add(it);
        }
        return retval;
    }
    
    protected abstract T getContent(Object obj);
    protected abstract void setContent(Object obj, T content);

}
