package org.vns.javafx.dock.api;

import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Valery
 */
public class DockPaneBox extends StackPane implements DockPaneTarget{
    
    private PaneHandler paneHandler;
    
    public DockPaneBox() {
        init();
    }

    private void init() {
        paneHandler = paneHandler = new DockPaneBoxHandler(this);
    }
    

    @Override
    public PaneHandler paneHandler() {
        return paneHandler;
    }

    public Dockable dock(Dockable node, Side dockPos) {
        return paneHandler.dock(node, dockPos);
    }

    @Override
    public Pane pane() {
        return this;
    }
    
}
