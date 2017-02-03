package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery Shyshkin
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
    public VPane(String id, double dividerPos) {
        init();
        setId(id);
        setDividerPos(dividerPos);
    }
    public VPane(double dividerPos) {
        init();
        setDividerPos(dividerPos);
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
