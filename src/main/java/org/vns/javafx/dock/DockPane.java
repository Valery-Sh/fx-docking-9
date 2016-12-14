package org.vns.javafx.dock;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.PaneDelegate;
import org.vns.javafx.dock.api.properties.PaneDelegateProperty;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
public class DockPane extends StackPane implements DockTarget{
    
    private final PaneDelegateProperty<PaneDelegate> delegeteProperty = new PaneDelegateProperty<>();
            
    public DockPane() {
        init();
    }

    public DockPane(Node... children) {
        super(children);
    }
    private void init() {
        delegeteProperty.set(new PaneDelegate(this));
    }
    protected PaneDelegate getDelegate() {
        return this.delegeteProperty.get();
    }
    @Override
    public void dock(Node dockable, Side dockPos) {
        delegeteProperty.get().dock(dockable, dockPos);
    }
    
}
