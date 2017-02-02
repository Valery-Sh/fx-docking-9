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
public class HPane extends DockSplitPane {
    
    
    public HPane() {
        init();
    }
    public HPane(String id) {
        init();
        setId(id);
    }
    public HPane(String id, double dividerPos) {
        init();
        setId(id);
        setDividerPos(dividerPos);
    }
    public HPane(double dividerPos) {
        init();
        setDividerPos(dividerPos);
    }

    public HPane(Node... items) {
        super(items);
        init();
    }
    private void init() {
        setOrientation(Orientation.HORIZONTAL);
    }
    
    public HPane append(Dockable d ) {
        getItems().add(d.node());
        return this;
    }
    public HPane append(HPane hp ) {
        getItems().add(hp);
        return hp;
    }
    public VPane append(VPane vp ) {
        getItems().add(vp);
        return vp;
    }

    public HPane hor(Node... nodes ) {
        getItems().addAll(nodes);
        return this;
    }
    
}
