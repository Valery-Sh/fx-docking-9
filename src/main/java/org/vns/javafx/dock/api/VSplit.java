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
public class VSplit extends DockSplitPane {
    
    
    public VSplit() {
        init();
    }

    public VSplit(Node... items) {
        super(items);
        init();
    }
    private void init() {
        setOrientation(Orientation.VERTICAL);
    }
}
