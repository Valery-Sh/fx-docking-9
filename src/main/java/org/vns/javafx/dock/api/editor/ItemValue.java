package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class ItemValue {
    private final TreeItem treeItem;
    private Object treeItemObject;
    //private Node treeItemObject;    
    private TreeItemBuilder builder;
    private boolean placeholder;
    private Node cellGraphic;
    
    public ItemValue(TreeItem treeItem) {
        this.treeItem = treeItem;
    }

    public TreeItem getTreeItem() {
        return treeItem;
    }

    public Object getTreeItemObject() {
        return treeItemObject;
    }
/*    public Object getTreeItemObject() {
        return treeItemObject;
    }

    public void setTreeItemObject(Node treeItemObject) {
        this.treeItemObject = treeItemObject;
    }
*/
    public void setTreeItemObject(Object treeItemObject) {
        this.treeItemObject = treeItemObject;
    }

    public TreeItemBuilder getBuilder() {
        return TreeItemRegistry.getInstance().getBuilder(treeItemObject);
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public Node getCellGraphic() {
        return cellGraphic;
    }

    public void setCellGraphic(Node cellGraphic) {
        this.cellGraphic = cellGraphic;
    }
    
    
    
}//ItemValue
