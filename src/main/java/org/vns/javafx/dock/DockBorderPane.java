package org.vns.javafx.dock;

import javafx.scene.layout.BorderPane;
import org.vns.javafx.dock.api.DockBorderPaneContext;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery Shyshkin
 */
public class DockBorderPane extends BorderPane {

    public DockBorderPane() {
        init();
    }

    private void init() {
        DockRegistry.makeDockLayout(this, new DockBorderPaneContext(this));
        DockRegistry.makeDockable(this).getContext().setDragNode(null);
        
    }

}
