package org.vns.javafx.dock;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.BorderPaneContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery Shyshkin
 */
public class DockBorderPane extends BorderPane {

    public DockBorderPane() {
        init();
    }

    private void init() {
        DockRegistry.makeDockTarget(this, new BorderPaneContext(this));
        DockRegistry.makeDockable(this).getContext().setDragNode(null);
        
    }

}
