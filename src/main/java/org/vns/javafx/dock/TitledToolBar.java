package org.vns.javafx.dock;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery Shyshkin
 */
public class TitledToolBar extends VBox implements Dockable{
    
    StringProperty titleProperty = new SimpleStringProperty("Tool Bar Enabled");
    DockNodeHandler dockState = new DockNodeHandler(this);
    
    private ToolBar toolBar = new ToolBar();
    
    private Dockable dockTarget;
    
    public TitledToolBar() {
        init();
    }
    private void init() {
        getChildren().add(toolBar);
        Region titleBar = new DockTitleBar(this);
        
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
    
    public StringProperty titleProperty() {
        return dockState.titleProperty();
    }

    public String getDockPos() {
        return dockState.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.dockState.setDockPos(dockpos);
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
