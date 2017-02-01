package org.vns.javafx.dock.api;

import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneBase extends StackPane implements DockPaneTarget{
    
    private PaneHandler paneHandler;
    
    public DockPaneBase() {
        init();
    }

    public DockPaneBase(DockSplitPane rootSplitPane) {
        init(rootSplitPane);
    }
    private void init() {
        paneHandler = paneHandler = new DockPaneHandler(this);
    }
    

    private void init(DockSplitPane rootSplitPane) {
        paneHandler = new DockPaneHandler(this,rootSplitPane);
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
