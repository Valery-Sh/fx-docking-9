package org.vns.javafx.dock;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.vns.javafx.dock.api.DockBorderPaneContext;
import org.vns.javafx.dock.api.DockRegistry;

/**
 * The objects of the class can be used as {@code DoclLayout) or/and {@code Dockable)
 * objects.
 * 
 * @author Valery Shyshkin
 */
public class DockBorderPane extends BorderPane {

    /**
     * Creates a new instance of the class.
     */
    public DockBorderPane() {
        init();
    }

    private void init() {
        DockRegistry.makeDockLayout(this, new DockBorderPaneContext(this));
        DockRegistry.makeDockable(this).getContext().setDragNode(null);
    }

}
