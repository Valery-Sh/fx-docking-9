package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value="items")
public class HPane extends DockSplitPane {
    
    
    public HPane() {
        init();
    }

    public HPane(Node... items) {
        super(items);
        init();
    }
    private void init() {
        setOrientation(Orientation.HORIZONTAL);
    }
}
