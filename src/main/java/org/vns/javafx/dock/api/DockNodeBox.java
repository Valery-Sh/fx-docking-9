package org.vns.javafx.dock.api;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;

/**
 *
 * @author Valery
 */
public class DockNodeBox  extends VBox implements Dockable{
    
    DockNodeHandler nodeHandler = new DockNodeHandler(this);
    
    public DockNodeBox() {
        init();
    }
    private void init() {
        Region titleBar = new DockTitleBar(this);
        getChildren().add(titleBar);
        nodeHandler.setTitleBar(titleBar);
        nodeHandler.titleBarProperty().addListener(this::titlebarChanged);
        getStyleClass().add("dock-node");
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public String getTitle() {
        return nodeHandler.getTitle();
    }

    public void setTitle(String title) {
        nodeHandler.setTitle(title);
    }


    public void dock(Dockable dockable, Side dockPos) {
        nodeHandler.getPaneHandler().dock(dockable, dockPos, this);
    }

    public void setDragNode(Node dragSource) {
        nodeHandler.setDragNode(dragSource);
    }
/*    public double getDividerPos() {
        return nodeHandler.getDividerPos();
    }

    public void setDividerPos(double divpos) {
        this.nodeHandler.setDividerPos(divpos);
    }
*/
    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeHandler nodeHandler() {
        return nodeHandler;
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