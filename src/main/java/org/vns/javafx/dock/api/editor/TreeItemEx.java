package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.editor.TreeItemBuilder.PlaceholderBuilder;
import org.vns.javafx.dock.api.editor.TreeItemBuilder.PlaceholderBuilderFactory;

/**
 *
 * @author Valery
 *
 */
public class TreeItemEx extends TreeItem<ItemValue> {

    private TreeItemBuilder placeholderBuilder; 
    
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
        if (getValue().isPlaceholder() && getValue().getTreeItemObject() == null ) {
            retval = this.getPlaceholderBuilder();
        } else {
            retval = TreeItemBuilderRegistry.getInstance().getBuilder(getValue().getTreeItemObject());
        }
        return retval;
    }    
    
    public Object getObject() {
        return getValue().getTreeItemObject();
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
    
    public TreeItemBuilder getPlaceholderBuilder() {
        return placeholderBuilder;
    }
    public void setPlaceholderBuilder(TreeItemBuilder placeholderBuilder) {
        this.placeholderBuilder = placeholderBuilder;
    }
    public TreeItemEx createPlaceholder(int placeholderId, Object newValue)  {
        TreeItemEx ph = null; 
        if ( getBuilder() instanceof PlaceholderBuilderFactory ) {
            PlaceholderBuilder pb = ((PlaceholderBuilderFactory)getBuilder()).getPlaceholderBuilder(placeholderId);            
            ph = pb.buildPlaceholder(newValue);
            ph.setPlaceholderBuilder((TreeItemBuilder) pb);
        }
        return ph;
    }
    
}
