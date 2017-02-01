package org.vns.javafx.dock;

import org.vns.javafx.dock.api.DockPaneBase;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockableDockPane extends VBox implements Dockable {

    private HBox headerPane;
    private DockPaneTarget dockPane;
    DockNodeHandler nodeHandler = new DockNodeHandler(this);

    public DockableDockPane() {
        dockPane = new DockPaneBase();
        init();
    }
    public DockableDockPane(DockPaneTarget dockPane) {
        this.dockPane = dockPane;
        init();
    }

    private void init() {
/*        headerPane = new HBox();
        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);        
        headerPane.getChildren().addAll(fillPane);
        headerPane.getStyleClass().add("dockable-dock-pane");        
        headerPane.getStyleClass().add("header-pane");
*/
        
        Region titleBar = new DockTitleBar(this);
        //getChildren().add(titleBar);
        nodeHandler.setTitleBar(titleBar);
        nodeHandler.titleBarProperty().addListener(this::titlebarChanged);
        
        //dockPane = new DockPaneBase();
        //dockPane.pane().setStyle("-fx-border-width: 2; -fx-border-color: red");
        getChildren().addAll(titleBar, dockPane.pane());
        this.autosize();
        
        Platform.runLater(() -> dockPane.pane().prefHeightProperty().bind(heightProperty()));
    }

    public Dockable dock(Dockable node, Side dockPos) {
        return dockPane.paneHandler().dock(node, dockPos);
    }

    public String getTitle() {
        return nodeHandler.getTitle();
    }

    public void setTitle(String title) {
        nodeHandler.setTitle(title);
    }

    public DockNodeHandler getDockNodeHandler() {
        return nodeHandler;
    }

/*    public void dock(Dockable dockable, Side dockPos) {
        nodeHandler.getPaneHandler().dock(dockable, dockPos, this);
    }
*/
    public String getDockPos() {
        return nodeHandler.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.nodeHandler.setDockPos(dockpos);
    }
    
    public void setDragSource(Node dragSource) {
        nodeHandler.setDragNode(dragSource);
    }

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
