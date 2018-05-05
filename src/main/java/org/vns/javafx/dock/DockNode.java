package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.StyleUtil;
import org.vns.javafx.dock.api.DockNodeSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery Shyshkin
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
        Dockable d = DockRegistry.makeDockable(this);
        context = Dockable.of(this).getContext();
        context.setDragNode(null);
        
        getStyleClass().add("dock-node");

        StyleUtil.styleDockNode(this); // modena -fx-background
        
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
    
    public Node getTitleBar() {
        return context.getTitleBar();
    }

    public void setTitleBar(Node node) {
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
