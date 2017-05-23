package org.vns.javafx.dock.api.editor;

import javafx.geometry.Bounds;
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
    private String title;
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

    public void setTreeItemObject(Object treeItemObject) {
        this.treeItemObject = treeItemObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public TreeItemBuilder getBuilder() {
        //Object obj = getDragTargetObject(ev);
        TreeItemBuilder builder;
        if (isPlaceholder() && treeItemObject == null) {
            TreeItem<ItemValue> p = treeItem.getParent();
            System.err.println("  Handle itemValue.getBuilder p=" + p.getValue().getTreeItemObject());
            builder = p.getValue().getBuilder().getPlaceHolderBuilder(p);
        } else {
            builder = TreeItemRegistry.getInstance().getBuilder(treeItemObject);
        }
        return builder;
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
