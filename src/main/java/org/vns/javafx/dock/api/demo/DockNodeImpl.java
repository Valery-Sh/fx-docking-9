package org.vns.javafx.dock.api.demo;

import org.vns.javafx.dock.api.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DockNodeImpl extends VBox implements Dockable {
    
    StateProperty stateProperty;
    
    public DockNodeImpl() {
        stateProperty = new StateProperty(this);
        
        Region bar = stateProperty.createDefaultTitleBar("Default Title Bar");
        //bar.setId("FIRST");
        bar.getStyleClass().add("dock-title-bar");
        getChildren().add(0,bar);
        Button b1 = new Button("Change Title Bar");
        getChildren().add(b1);
        b1.setOnAction(value -> {
            this.titleProperty().set("New TTTT");
            DockTitleBar newtb = new DockTitleBar(this);
            newtb.getStyleClass().add("dock-title-bar");
            bar.setId("SECOND");  
            System.out.println("SECOND !!!!");
            stateProperty.titleBarProperty().set(newtb);
            
        });
    }
    

    @Override
    public StringProperty titleProperty() {
        return new SimpleStringProperty("NODE default");
    }

    @Override
    public StateProperty stateProperty() {
        return stateProperty;
    }

}
