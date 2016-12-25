package org.vns.javafx.dock;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.properties.DockPaneHandlerProperty;

/**
 *
 * @author Valery
 */
public class DockPane extends StackPane implements DockPaneTarget{
    
    //private final DockPaneHandlerProperty<DockPaneHandler> delegeteProperty = new DockPaneHandlerProperty<>();
    private DockPaneHandler paneHandler;
            
    public DockPane() {
        init();
    }

    public DockPane(Node... children) {
        super(children);
    }
    private void init() {
        paneHandler = new DockPaneHandler(this);
        //delegeteProperty.set(new DockPaneHandler(this));
    }
    @Override
    public DockPaneHandler paneHandler() {
        return paneHandler;
    }
    @Override
    public Dockable dock(Node node, Side dockPos) {
        return paneHandler.dock(node, dockPos);
    }

    @Override
    public Pane pane() {
        return this;
    }
    
}
