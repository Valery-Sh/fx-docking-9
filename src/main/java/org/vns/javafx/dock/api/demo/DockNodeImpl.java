package org.vns.javafx.dock.api.demo;

import java.util.function.Function;
import org.vns.javafx.dock.api.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    public void print() {

//        System.out.println(((Function<Integer,String>)stateProperty.converter).apply(3).length());
//        System.out.println(((Function<Integer,String>)stateProperty.converter).apply(3).length());
        
        //stateProperty.setConverter( (Integer i) -> Integer.toString(i) + " !!!" );
        //System.out.println(((Function<Integer,String>)stateProperty.converter).apply(3).length());
        //System.out.println(stateProperty.converter.apply(30).length());
    }    
    
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
            //stateProperty.titleBarProperty().set(newtb);
            
        });
    }
    
    /*public void addNode(Node node) {
        int idx = 1;
        if ( getChildren().size() == 0 ) {
            idx = 0;
        }
        getChildren().add(getC)
    }
*/
    @Override
    public StringProperty titleProperty() {
        return new SimpleStringProperty("NODE default");
    }

    @Override
    public StateProperty stateProperty() {
        return stateProperty;
    }

}
