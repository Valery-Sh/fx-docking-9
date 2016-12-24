package org.vns.javafx.dock.api;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class AbstractDockNode  extends VBox implements Dockable, DockTarget{
    
    StringProperty titleProperty = new SimpleStringProperty("Dock Node");
    
    StateProperty stateProperty = new StateProperty(this);
    
    private Dockable dockTarget;
    
    public AbstractDockNode() {
        init();
    }
    private void init() {
        Region titleBar = new DockTitleBar(this);
        getChildren().add(titleBar);
        stateProperty.setTitleBar(titleBar);
    }

    public Dockable getDockTarget() {
        return dockTarget;
    }

    public void setDockTarget(Dockable dockTarget) {
        this.dockTarget = dockTarget;
    }
    
    @Override
    public StringProperty titleProperty() {
        return titleProperty;
    }

    @Override
    public StateProperty stateProperty() {
        return stateProperty;
    }

    @Override
    public void dock(Node dockable, Side dockPos) {
        stateProperty.getPaneDelegate().dock(dockable, dockPos, this);
    }

    @Override
    public String getDockPos() {
        return stateProperty.getDockPos();
    }

    @Override
    public void setDockPos(String dockpos) {
        this.stateProperty.setDockPos(dockpos);
    }
    
    public void assignDragRegion(Region dragRegion) {
        stateProperty.setTitleBar(dragRegion);
    }
}