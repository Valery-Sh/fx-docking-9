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
        init(-1);
    }
    public HPane(String id) {
        init(-1);
        setId(id);
    }
    public HPane(String id, double dividerPos) {
        init(dividerPos);
        setId(id);
    }
    public HPane(double dividerPos) {
        init(dividerPos);
    }

    public HPane(Node... items) {
        super(items);
        init(-1);
    }
    private void init(double dividerPos) {
        setOrientation(Orientation.HORIZONTAL);
        setDividerPos(dividerPos);
        
    }
    
    public HPane hor(Node... nodes ) {
        getItems().addAll(nodes);
        return this;
    }
    
}
