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
    
    DockNodeController nodeController = new DockNodeController(this);
    
    public DockNodeBox() {
        init();
    }
    private void init() {
        //09.02Region titleBar = new DockTitleBar(this);
        Region titleBar = nodeController.createDefaultTitleBar("");
        getChildren().add(titleBar);
        nodeController.setTitleBar(titleBar);
        nodeController.titleBarProperty().addListener(this::titlebarChanged);
        getStyleClass().add("dock-node");
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public String getTitle() {
        return nodeController.getTitle();
    }

    public void setTitle(String title) {
        nodeController.setTitle(title);
    }


    public void setDragNode(Node dragSource) {
        nodeController.setDragNode(dragSource);
    }
/*    public double getDividerPos() {
        return nodeController.getDividerPos();
    }

    public void setDividerPos(double divpos) {
        this.nodeController.setDividerPos(divpos);
    }
*/
    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeController nodeController() {
        return nodeController;
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