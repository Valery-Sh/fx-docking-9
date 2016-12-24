package org.vns.javafx.dock.api.demo;

import org.vns.javafx.dock.api.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.DockableState;

/**
 *
 * @author Valery
 */
public class DockNodeImpl extends VBox implements Dockable {
    
    DockableState stateProperty;

    public void print() {

//        System.out.println(((Function<Integer,String>)getDockState.converter).apply(3).length());
//        System.out.println(((Function<Integer,String>)getDockState.converter).apply(3).length());
        
        //stateProperty.setConverter( (Integer i) -> Integer.toString(i) + " !!!" );
        //System.out.println(((Function<Integer,String>)getDockState.converter).apply(3).length());
        //System.out.println(getDockState.converter.apply(30).length());
    }    
    
    public DockNodeImpl() {
        stateProperty = new DockableState(this);
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
            //stateProperty.titleBarProperty().set(newtb);
            
        });
        
        stateProperty.titleBarProperty().addListener(this::titlebarChanged);
        
    }
    
    @Override
    public StringProperty titleProperty() {
        return new SimpleStringProperty("NODE default");
    }

    @Override
    public DockableState getDockState() {
        return stateProperty;
    }
    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if ( oldValue != null && newValue == null ) {
            getChildren().remove(oldValue);
        } else if ( oldValue != null && newValue != null ) {
            getChildren().set(0,newValue);
        } else if ( oldValue == null && newValue != null ) {
            getChildren().add(0,newValue);
        }
    }

}
