package org.vns.javafx.designer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import static org.vns.javafx.designer.SceneGraphView.FIRST;

/**
 * An Instance of the class is used as a {@code value} property of the item of
 * type {@code TreeItem}
 *
 * @author Valery
 */
public class ItemValue {

    private final TreeItemEx treeItem;
    private final ObjectProperty treeItemObject = new SimpleObjectProperty();
    private final ObjectProperty treeItemObjectChangeHandler = new SimpleObjectProperty();

    //private Object treeItemObject;
    private boolean placeholder;
    private Node cellGraphic;
    private String title;
    private int dragDropQualifier;
    private Object changeListener;
    ///////////////////////////////
    ///////////////////////////////
    private int index;

    /**
     * Creates a new instance of the class for the specified {@code TreeItem}.
     *
     * @param treeItem the tree item for which the value is created.
     */
    public ItemValue(TreeItemEx treeItem) {
        this.dragDropQualifier = FIRST;
        this.treeItem = treeItem;
        init();
    }

    public ItemValue(TreeItemEx treeItem, boolean noEvent) {
        this.dragDropQualifier = FIRST;
        this.treeItem = treeItem;
        init();
    }
    private ChangeListener treeItemObjectListener;

    private void init() {
        if (treeItemObjectListener != null) {
            treeItemObject.removeListener(treeItemObjectListener);
        }
        System.err.println("ItemValue INIT " + treeItemObject.get());

        if (treeItemObject.get() instanceof Node) {
            Node n = (Node) treeItemObject.get();
            if ("graphicLb".equals(n.getId())) {
                System.err.println("ItemValue REGICTER graphicLb");
            }
        }

        treeItemObjectListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItemBuilder b = null;
                updateContent(oldValue, newValue);
                if (newValue != null) {
                    //20.01b = TreeItemBuilderRegistry.getInstance().getBuilder(newValue);
                    if (b != null) {
                        //20.0b.registerChangeHandler(getTreeItem());
                    }
                } else if (oldValue != null && newValue == null && (oldValue instanceof Node)) {
                    //20.01b = TreeItemBuilderRegistry.getInstance().getBuilder(oldValue);
                    if (b != null) {
                        //-this is the old comment b.unregisterChangeHandler((Node) oldValue);
                    }
                }
            }
        };

        treeItemObject.addListener(treeItemObjectListener);
    }

    protected void updateContent(Object oldValue, Object newValue) {
        TreeItemBuilder builder;
        if (isPlaceholder()) {
            builder = treeItem.getPlaceholderBuilder();
        } else {
            builder = getBuilder();
        }
        //20.01 builder.updateTreeItemContent(treeItem, oldValue, newValue);
    }

    /**
     * Returns the owner of this object.
     *
     * @return the object of type {@code TreeItem} which value property is this
     * object.
     */
    public TreeItemEx getTreeItem() {
        return (TreeItemEx) treeItem;
    }

    public ObjectProperty treeItemObjectProperty() {
        return treeItemObject;
    }

    /**
     * Returns an object which was used to create an instance of
     * {@code TreeItem}.
     *
     * @return an object which was used to create an instance of
     * {@code TreeItem}.
     */
    public Object getTreeItemObject() {
        return treeItemObject.get();
    }

    /**
     * Sets the new value to the property {@code treeItemObject}.
     *
     * @param treeItemObject the new value to be set
     */
    public void setTreeItemObject(Object treeItemObject) {
        this.treeItemObject.set(treeItemObject);
    }

    public int getDragDropQualifier() {
        return dragDropQualifier;
    }

    public void setDragDropQualifier(int dragDropQualifier) {
        this.dragDropQualifier = dragDropQualifier;
    }

    public Object getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(Object changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * Return a string value which is used to display a text of the
     * {@code TreeItem}
     *
     * @return Return a string value which is used to display a text of the
     * {@code TreeItem}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets a string value which can be used to display text for
     * {@code TreeItem}.
     *
     * @param title the text to be displayed
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /*    public TreeItemBuilder getBuilder() {
        TreeItemBuilder builder;
        if (isPlaceholder() && getTreeItemObject() == null) {
            TreeItemEx p = (TreeItemEx) treeItem.getParent();
            int idx = p.getChildren().indexOf(this);
            builder = ((TreeItemBuilder.HasPlaceholders)p.getValue().getBuilder()).getPlaceholderBuilders(this)[idx];
        } else {
            builder = TreeItemBuilderRegistry.getInstance().getBuilder(getTreeItemObject());
        }
        return builder;
    }
     */
    public TreeItemBuilder getBuilder() {
        TreeItemBuilder builder = null;
        if (isPlaceholder() && getTreeItemObject() == null) {
            ////20.01 builder = treeItem.getPlaceholderBuilder();
        } else {
            ////20.01builder = TreeItemBuilderRegistry.getInstance().getBuilder(getTreeItemObject());
        }
        return builder;
    }

    public boolean isPlaceholder() {
        return placeholder;
        //20.01 return treeItem.getPlaceholderBuilder() != null;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    /*    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }
     */
    /**
     * There is no one-to-one relation between an object of type
     * {@code TreeItem} and it's {@code TreeCell}. The only place where we can
     * legitimately get access to the cell of the item is the method
     * {@code setCellFactory} defined in the class (@code TreeView}. The
     * callback function specified as a parameter of the method provides the
     * value of the {@code value} property of the {@code TreeItem}. In our case
     * it has type of this class and we can use it in order to modify the
     * corresponding {@code TreeCell}.
     * <p>
     * To display objects of type {@code TreeItem}, the {@code graphic} property
     * of a node of type {code TreeCell} is used. The method
     * {@link TreeItemBuilder#createItem(java.lang.Object) }
     * creates the mentioned node and stores it as a value of the property
     * {@code cellGraphic} of this class. Later when an object of type
     * {@code TreeCell} will be created it becomes possible to assign the saved
     * value to {@code graphic} property of the cell. It is a callback parameter
     * of the method {@code setCellFactory} where all this stuff can be done.
     * </p>
     *
     * @return the Node to be used as a value of the {@code graphic} property of
     * a {@code TreeCell} which is used to display a tree item.
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
        this.cellGraphic = cellGraphic;
    }
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    /**
     * 
     * @return 
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    
}//ItemValue
