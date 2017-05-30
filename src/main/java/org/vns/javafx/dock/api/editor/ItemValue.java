package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import static org.vns.javafx.dock.api.editor.SceneGraphEditor.FIRST;

/**
 * An Instance of the class is used as a {@code value} property of the
 * item of type {@code TreeItem}
 * @author Valery
 */
public class ItemValue {

    private final TreeItem treeItem;
    private Object treeItemObject;

    private boolean placeholder;
    private Node cellGraphic;
    private String title;
    private int dragDropQualifier;
    
    /**
     * Creates a new instance of the class for the specified {@code TreeItem}.
     * 
     * @param treeItem  the tree item for which the value is created.
     */
    public ItemValue(TreeItem treeItem) {
        this.dragDropQualifier = FIRST;
        this.treeItem = treeItem;
    }
    /**
     * Returns the owner of this object.
     * 
     * @return the object of type {@code TreeItem} which value property is 
     * this object.
     */
    public TreeItem getTreeItem() {
        return treeItem;
    }
    /**
     * Returns an object which was used to create an instance of
     * {@code TreeItem}.
     * @return an object which was used to create an instance of
     * {@code TreeItem}.
     */
    public Object getTreeItemObject() {
        return treeItemObject;
    }

    public int getDragDropQualifier() {
        return dragDropQualifier;
    }

    public void setDragDropQualifier(int dragDropQualifier) {
        this.dragDropQualifier = dragDropQualifier;
    }
    /**
     * Sets  the new value to the property {@code treeItemObject}.
     * @param treeItemObject the new value to be set
     */
    public void setTreeItemObject(Object treeItemObject) {
        this.treeItemObject = treeItemObject;
    }
    /**
     * Return a string value which is used to display a text of the {@code TreeItem}
     * @return Return a string value which is used to display a text of the {@code TreeItem}
     */
    public String getTitle() {
        return title;
    }
    /**
     * Sets a string value which can be used to display text for {@code TreeItem}.
     * 
     * @param title the text to be displayed
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return 
     */
    public TreeItemBuilder getBuilder() {
        TreeItemBuilder builder;
        if (isPlaceholder() && treeItemObject == null) {
            TreeItem<ItemValue> p = treeItem.getParent();
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
    /**
     * There is no one-to-one relation between an object of 
     * type {@code TreeItem} and it's {@code TreeCell}. The only place where we 
     * can legitimately get access to the cell of the item is the method 
     * {@code setCellFactory} defined in the class (@code TreeView}. The callback
     * function specified as a parameter of the method provides the value of the 
     * {@code value} property of the {@code TreeItem}. In our case it has type
     * of this class and we can use it in order to modify the corresponding
     * {@code TreeCell}. 
     * <p>
     * To display objects of type {@code TreeItem}, the {@code graphic} property 
     * of a node of type {code TreeCell} is used. The method 
     * {@link TreeItemBuilder#createItem(java.lang.Object, java.lang.Object...)
     * creates the mentioned node and stores it as a value of the property 
     * {@code cellGraphic) of this class. Later when an object of type {@TreeCell} 
     * will be created it becomes possible to assign the saved value to 
     * {@code graphic} property of the cell. It is a callback parameter of the 
     * method {@code setCellFactory} where all this stuff can be done.
     * </p>
     * 
     * @return the Node to be used as a value of the {@code graphic} property 
     *   of a {@code TreeCell} which is used to display a tree item.
     */
    public Node getCellGraphic() {
        return cellGraphic;
    }
    /**
     * Sets the specified node as a value of the property {@code cellGraphic} 
     * 
     * @param cellGraphic the Node to be set
     */
    public void setCellGraphic(Node cellGraphic) {
        cellGraphic.setMouseTransparent(true);
        this.cellGraphic = cellGraphic;
    }

}//ItemValue
