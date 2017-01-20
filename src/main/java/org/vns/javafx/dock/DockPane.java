package org.vns.javafx.dock;

import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PaneHandler;

/**
 *
 * @author Valery
 */
public class DockPane extends StackPane implements DockPaneTarget{
    
    private PaneHandler paneHandler;
            
    public DockPane() {
        init();
    }


    private void init() {
        paneHandler = new DockPaneHandler(this);
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
