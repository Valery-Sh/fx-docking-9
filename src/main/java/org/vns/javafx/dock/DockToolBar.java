/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockToolBar extends ToolBar implements Dockable{

    StringProperty titleProperty = new SimpleStringProperty("Tool Bar Enabled");
    DockNodeHandler dockState = new DockNodeHandler(this);
    
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
    public StringProperty titleProperty() {
        return titleProperty;
    }
    public String getTitle() {
        return titleProperty.get();
    }
    public void setTitle(String title) {
        titleProperty.set(title);
    }
    
    public Dockable getDockTarget() {
        return dockTarget;
    }

    public void setDockTarget(Dockable dockTarget) {
        this.dockTarget = dockTarget;
    }
    
    public String getDockPos() {
        return dockState.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.dockState.setDockPos(dockpos);
    }
    public void useAaTitleBar(Region titleBar) {
        dockState.setTitleBar(titleBar);
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeHandler nodeHandler() {
        return dockState;
    }
}
