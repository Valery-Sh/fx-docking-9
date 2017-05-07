package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.FIRST;

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
    
    private int dragDropQualifier;
    
    public ItemValue(TreeItem treeItem) {
        this.dragDropQualifier = FIRST;
        this.treeItem = treeItem;
    }

    public TreeItem getTreeItem() {
        return treeItem;
    }

    public Object getTreeItemObject() {
        return treeItemObject;
    }

    public int getDragDropQualifier() {
        return dragDropQualifier;
    }

    public void setDragDropQualifier(int dragDropQualifier) {
        this.dragDropQualifier = dragDropQualifier;
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
