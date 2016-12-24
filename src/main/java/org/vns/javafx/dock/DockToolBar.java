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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableState;

/**
 *
 * @author Valery
 */
public class DockToolBar extends ToolBar implements Dockable{

    StringProperty titleProperty = new SimpleStringProperty("Tool Bar Enabled");
    DockableState dockState = new DockableState(this);
    
    private Dockable dockTarget;
    
    public DockToolBar() {
        init();
    }
    private void init() {
        Separator titleBar = new Separator();
        titleBar.setPrefWidth(USE_PREF_SIZE);
        dockState.setTitleBar(titleBar);
        //stateProperty.titleBarProperty().setActiveChoosedPseudoClass(false);
        Button b1 = new Button("",new Circle(0, 0, 4));
/*        b1.setOnAction(value -> {
            getDockState.titleBarProperty().setActiveChoosedPseudoClass(true); 
        });
*/
        Button b2 = new Button("", new Rectangle(0,0,8,8));
/*        b2.setOnAction(value -> {
            getDockState.titleBarProperty().setActiveChoosedPseudoClass(false); 
        });        
*/        
        getItems().addAll(b1,b2, new Separator(), titleBar);
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
    public DockableState getDockState() {
        return dockState;
    }

    @Override
    public void dock(Node dockable, Side dockPos) {
        dockState.getPaneDelegate().dock(dockable, dockPos, this);
    }

    @Override
    public String getDockPos() {
        return dockState.getDockPos();
    }

    @Override
    public void setDockPos(String dockpos) {
        this.dockState.setDockPos(dockpos);
    }
    public void useAaTitleBar(Region titleBar) {
        dockState.setTitleBar(titleBar);
    }
}
