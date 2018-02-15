package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockPaneSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockPane extends Control {

    private DockSplitPane root;
    
    public DockPane() {
        super();
        init();
    }

    private void init() {
        root = new DockSplitPane();
        root.setId("rootSplitPane");
        TargetContext c = new DockPaneContext(this, root);
        DockRegistry.makeDockTarget(this, c);
    }

    protected DockSplitPane getRoot() {
        return (DockSplitPane)((StackPane)((SkinBase)getSkin()).getChildren().get(0)).getChildren().get(0);
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


/*    private void dock(int idx, Dockable dockable, DockSplitPane splitPane) {
        if (!getDelegate().getTargetContext().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        getDelegate().getTargetContext().dock(idx, dockable, splitPane);
    }
*/
    public void dock(Dockable dockable, Side side) {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        if (!targetContext.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        targetContext.dock(dockable, side);
    }
    public void dockNode(Node dockableNode, Side side) {
        dock( Dockable.of(dockableNode), side);
    }

    public void dockNode(Node dockableNode, Side side, Dockable target) {
        dock( DockRegistry.dockable(dockableNode), side, target);
    }
    
    public void dock(Dockable dockable, Side side, Dockable target) {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        if (!targetContext.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }

        targetContext.dock(dockable, side, target);
    }

/*    protected void update(DockSplitPane dsp) {
        //SplitPane sp = dsp;
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.getContext().setTargetContext(targetContext);
            } else if (node instanceof DockSplitPane) {
                update((DockSplitPane) node);
            }
        }
    }

    protected void update(DockSplitPane split, TargetContext ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.getContext().setTargetContext(ph);
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(this);
                DockSplitPane sp = (DockSplitPane) node;
                update(sp, ph);
            }
        }
    }

    public void update() {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        update(getRoot());
        update(this.getRoot(), targetContext);
    }

    protected void splitPaneAdded(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).getContext().setTargetContext(dpt.getTargetContext());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    protected void splitPaneRemoved(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }
    }
*/
    public boolean isUsedAsDockTarget() {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();
        return targetContext.isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        DockPaneContext targetContext = (DockPaneContext) DockTarget.of(this).getTargetContext();        
        targetContext.setUsedAsDockTarget(usedAsDockTarget);
    }

/*    @Override
    public void handle(ActionEvent event) {
        update();
    }
*/

}//class
