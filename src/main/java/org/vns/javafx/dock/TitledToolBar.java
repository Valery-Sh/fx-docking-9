/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery Shyshkin
 */
public class TitledToolBar extends VBox implements Dockable, DockTarget{
    
    StringProperty titleProperty = new SimpleStringProperty("Tool Bar Enabled");
    StateProperty stateProperty = new StateProperty(this);
    
    private ToolBar toolBar = new ToolBar();
    
    private Dockable dockTarget;
    
    public TitledToolBar() {
        init();
    }
    private void init() {
        getChildren().add(toolBar);
        Region titleBar = new DockTitleBar(this);
        
        stateProperty.setTitleBar(titleBar);
        //stateProperty.titleBarProperty().setActiveChoosedPseudoClass(false);
        Button b1 = new Button("",new Circle(0, 0, 4));
/*        b1.setOnAction(value -> {
            stateProperty.titleBarProperty().setActiveChoosedPseudoClass(true); 
        });
*/
        Button b2 = new Button("", new Rectangle(0,0,8,8));
/*        b2.setOnAction(value -> {
            stateProperty.titleBarProperty().setActiveChoosedPseudoClass(false); 
        });        
*/        
        toolBar.getItems().addAll(b1,b2, new Separator(), titleBar);
    }
    public ToolBar getToolBar() {
        return toolBar;
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
    //private String dockpos = "BOTTOM";

    @Override
    public String getDockPos() {
        return stateProperty.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.stateProperty.setDockPos(dockpos);
    }
    
}