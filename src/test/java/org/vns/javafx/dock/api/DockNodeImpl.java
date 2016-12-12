package org.vns.javafx.dock.api;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DockNodeImpl extends VBox implements Dockable {
    
    StateProperty stateProperty;
    
    public DockNodeImpl() {
        stateProperty = new StateProperty(this);
    }
    

    @Override
    public StringProperty titleProperty() {
        return new SimpleStringProperty("default");
    }

    @Override
    public StateProperty stateProperty() {
        return stateProperty;
    }

}
