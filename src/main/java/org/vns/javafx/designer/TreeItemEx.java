package org.vns.javafx.designer;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

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
            ////20.01retval = this.getPlaceholderBuilder();
        } else {
            ////20.01retval = TreeItemBuilderRegistry.getInstance().getBuilder(getValue().getTreeItemObject());
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
        //20.01if ( getBuilder() instanceof PlaceholderBuilderFactory ) {
            //20.01PlaceholderBuilder pb = ((PlaceholderBuilderFactory)getBuilder()).getPlaceholderBuilder(placeholderId);            
            //20.01ph = pb.buildPlaceholder(newValue);
            //20.01ph.setPlaceholderBuilder((TreeItemBuilder) pb);
        //20.01}
        return ph;
    }
    
}
