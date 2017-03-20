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
    
    DockableController dockableController = new DockableController(this);
    
    public DockNodeBox() {
        init();
    }
    private void init() {
        //09.02Region titleBar = new DockTitleBar(this);
        Region titleBar = dockableController.createDefaultTitleBar("");
        getChildren().add(titleBar);
        dockableController.setTitleBar(titleBar);
        dockableController.titleBarProperty().addListener(this::titlebarChanged);
        getStyleClass().add("dock-node");
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public String getTitle() {
        return dockableController.getTitle();
    }

    public void setTitle(String title) {
        dockableController.setTitle(title);
    }


    public void setDragNode(Node dragSource) {
        dockableController.setDragNode(dragSource);
    }
    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableController dockableController() {
        return dockableController;
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