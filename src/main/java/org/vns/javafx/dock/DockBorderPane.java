package org.vns.javafx.dock;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.BorderPaneContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery Shyshkin
 */
public class DockBorderPane extends BorderPane implements DockTarget {

    private TargetContext targetContext;

    public DockBorderPane() {
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        if (targetContext == null) {
            targetContext = new BorderPaneContext(this);
        }
        return targetContext;
    }
}
