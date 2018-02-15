package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockNodeSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class DockNode extends Control { //implements Dockable {

    private DockableContext context;
    
    ObjectProperty<Node> content = new SimpleObjectProperty<>();
    
    public DockNode() {
        init(null, null);
    }

    public DockNode(String title) {
        init(null, title);
    }

    public DockNode(String id, String title) {
        init(null, title);
    }

    private void init(String id, String title) {
        DockRegistry.makeDockable(this);
        this.context = Dockable.of(this).getContext();
        getStyleClass().add("dock-node");
        context.createDefaultTitleBar(title);
        
        if (id != null) {
            setId(id);
        }
    }
    public ObjectProperty<Node> contentProperty() { 
        return content;
    }
    public Node getContent() {
        return content.get();
    }
    public void setContent(Node content) {
        this.content.set(content);
    }    
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public String getTitle() {
        return Dockable.of(this).getContext().getTitle();
    }

    public void setTitle(String title) {
        context.setTitle(title);
    }

    public Region getTitleBar() {
        return context.getTitleBar();
    }

    public void setTitleBar(Region node) {
        context.setTitleBar(node);
    }


    public Node getDragNode() {
        return context.getDragNode();
    }

    public void setDragNode(Node dragSource) {
        context.setDragNode(dragSource);
    }
    
    public DockableContext getContext() {
        return context;
    }
    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }

}
