package org.vns.javafx.dock.api.demo;

import org.vns.javafx.dock.api.DockPaneBox;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockableDockPane extends VBox implements Dockable {

    private DockPaneTarget dockPane;
    DockNodeHandler nodeHandler = new DockNodeHandler(this);

    public DockableDockPane() {
        dockPane = new DockPaneBox();
        init();
    }
    public DockableDockPane(DockPaneTarget dockPane) {
        this.dockPane = dockPane;
        init();
    }

    private void init() {
        Region titleBar = new DockTitleBar(this);
        nodeHandler.setTitleBar(titleBar);
        nodeHandler.titleBarProperty().addListener(this::titlebarChanged);
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
