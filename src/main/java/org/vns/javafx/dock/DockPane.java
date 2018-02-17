package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockPaneSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockPane extends Control {

    private HPane root;
    
    public DockPane() {
        super();
        init();
    }

    private void init() {
        root = new HPane();
        TargetContext c = new DockPaneContext(this, root);
        DockRegistry.makeDockTarget(this, c);
    }

    
    public ObservableList<Node> getItems() {
        return root.getItems();
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        DockPaneSkin skin = new DockPaneSkin(this, root);
        return skin;
    }

    public void dock(Node node, Side side) {
        Dockable dockable =  Dockable.of(node);
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        if (!targetContext.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        targetContext.dock(dockable, side);
    }
 /*   public void dockNode(Node dockableNode, Side side) {
        dock( dockableNode, side);
    }

    public void dockNode(Node dockableNode, Side side, Dockable target) {
        dock( dockableNode, side, target);
    }
*/    
    public void dock(Node dockableNode, Side side, Dockable dockableTarget) {
        Dockable dockable = Dockable.of(dockableNode);
        Dockable target = Dockable.of(dockableTarget);
        
                
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        if (!targetContext.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        targetContext.dock(dockable, side, target);
    }

    public boolean isUsedAsDockTarget() {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        return targetContext.isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();        
        targetContext.setUsedAsDockTarget(usedAsDockTarget);
    }
}//class
