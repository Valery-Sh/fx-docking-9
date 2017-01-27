package org.vns.javafx.dock.api;

import javafx.beans.DefaultProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value="items")
public class HSplit extends DockSplitPane {
    
    
    public HSplit() {
        init();
    }

    public HSplit(Node... items) {
        super(items);
        init();
    }
    private void init() {
        setOrientation(Orientation.HORIZONTAL);
    }
}
