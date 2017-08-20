package org.vns.javafx.dock.api;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author Valery
 */
public class DockNodeBox  extends VBox implements Dockable{
    
    DockableContext dockableContext = new DockableContext(this);
    
    public DockNodeBox() {
        init();
    }
    private void init() {
        //09.02Region titleBar = new DockTitleBar(this);
        Region titleBar = dockableContext.createDefaultTitleBar("");
        getChildren().add(titleBar);
        dockableContext.setTitleBar(titleBar);
        dockableContext.titleBarProperty().addListener(this::titlebarChanged);
        getStyleClass().add("dock-node");
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public String getTitle() {
        return dockableContext.getTitle();
    }

    public void setTitle(String title) {
        dockableContext.setTitle(title);
    }


    public void setDragNode(Node dragSource) {
        dockableContext.setDragNode(dragSource);
    }
    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getDockableContext() {
        return dockableContext;
    }
    
    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null && newValue == null) {
            getChildren().remove(0);            
        } else if ( newValue != null ) {
            getChildren().remove(0);            
            getChildren().add(newValue);
        }
    }
    
}