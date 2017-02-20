package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.vns.javafx.dock.api.DockSplitPane;

/**
 *
 * @author Valery
 */
@DefaultProperty(value="items")
public class VPane extends DockSplitPane {
    public VPane() {
        init();
    }
    public VPane(String id) {
        init();
        setId(id);
    }
    public VPane(Node... items) {
        super(items);
        init();
    }
    private void init() {
        setOrientation(Orientation.VERTICAL);
    }
    public VPane vert(Node... nodes ) {
        getItems().addAll(nodes);
        return this;
    }
}
