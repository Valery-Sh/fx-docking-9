package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.DockNodeSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 * The objects of the class can be used as {@code DoclLayout} or(and) {@code Dockable}
 * objects. By default it has a title bar which is defined as a dragNode.
 * 
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "content")
public class DockNode extends Control { //implements Dockable {

    private DockableContext context;
    
    
    ObjectProperty<Node> content = new SimpleObjectProperty<>();
    /**
     * Creates a new instance of the class.
     */
    public DockNode() {
        init(null, null);
    }
    /**
     * Creates a new instance of the class with the specified title.
     * @param title the value used as a title of the new object
     */
    public DockNode(String title) {
        init(null, title);
    }
    /**
     * Creates a new instance of the class with the specified identifier and title.
     * @param id the value used as an id property of the new node
     * @param title the value used as a title of the new object
     */
    public DockNode(String id, String title) {
        init(null, title);
    }

    private void init(String id, String title) {
        Dockable d = DockRegistry.makeDockable(this);
        context = Dockable.of(this).getContext();
        context.setDragNode(null);
        
        getStyleClass().add("dock-node");

        //StyleUtil.styleDockNode(this); // modena -fx-background
        
        context.createDefaultTitleBar(title);
        
        if (id != null) {
            setId(id);
        }
    }
    /**
     * The content of the DockPane.
     * @return the content property of the DockPane. 
     */
    public ObjectProperty<Node> contentProperty() { 
        return content;
    }
    /**
     * Returns the content of the DockPane.
     * @return the content  of the DockPane. May be null
     */
    public Node getContent() {
        return content.get();
    }
    /**
     * Sets the content of the DockNode which can be any Node or null.
     * @param content the node to be set. May be null
     */
    public void setContent(Node content) {
        this.content.set(content);
    }    
    /**
     * Return the title bar property of the DockNode.
     * @return the title bar property of the DockNode.
     */
    public ObjectProperty<Node> titleBarProperty() {
        return getContext().titleBarProperty();
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }
    /**
     * Returns a string value used as a title of the DockNode.
     * @return a string value used as a title of the DockNode.
     */
    public String getTitle() {
        return Dockable.of(this).getContext().getTitle();
    }
    /**
     * Sets the given value used as a title of the DockNode.
     * @param title the new value to be set
     */
    public void setTitle(String title) {
        context.setTitle(title);
    }
    /**
     * Returns an object of type Node which is used a title bar of the DockNode.
     * @return an object of type Node which is used a title bar of the DockNode.
     */
    public Node getTitleBar() {
        return context.getTitleBar();
    }
    /**
     * Sets an object of type Node which is used a title bar of the DockNode.
     * @param node the new value to be set
     */
    public void setTitleBar(Node node) {
        context.setTitleBar(node);
    }

    /**
     * Returns an object of type Node which is used start dragging of the DockNode.
     * @return an object of type Node which is used start dragging of the DockNode.
     */
    public Node getDragNode() {
        return context.getDragNode();
    }
    /**
     * Sets an object of type Node which is used start dragging of the DockNode.
     * @param dragSource the new value to be set
     */
    public void setDragNode(Node dragSource) {
        context.setDragNode(dragSource);
    }
    /**
     * Returns an object of type {@code DockableContex} bound with the DockNode
     * @return an object of type {@code DockableContex} bound with the DockNode
     */
    public DockableContext getContext() {
        return context;
    }
    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }
}
