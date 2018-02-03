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
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.event.DockEvent;
import org.vns.javafx.dock.api.indicator.DragPopup;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockPane extends Control implements DockTarget, EventHandler<ActionEvent> {

    protected StackPane stackPane = new StackPane();
    private DockSplitPane root;
    private DockPaneContext targetContext;
    
    public DockPane() {
        super();
        init();
    }

    private void init() {
        //getDelegate();
        root = new DockSplitPane();
        root.setRoot(this);
        root.setId("rootSplitPane");
        targetContext = new DockPaneContext(this, root);
    }

    public DockSplitPane getRoot() {
        return root;
    }
    public ObservableList<Node> getItems() {
        return root.getItems();
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

/*    protected ControlDockPane getDelegate() {
        if (delegate == null) {
            delegate = new ControlDockPane(new DockPaneContext(this));
            //delegate = this;
            //setRoot(this);

        }

        return delegate;
    }
*/
    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockPaneSkin(this);
    }

    public static class DockPaneSkin extends SkinBase<DockPane> {

        public DockPaneSkin(DockPane control) {
            super(control);
            control.stackPane.getChildren().add(control.getRoot());
            getChildren().add(control.stackPane);
            //getChildren().add(control.getRoot());
        }
    }
    
    @Override
    public DockPane target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        return targetContext;
    }

/*    private void dock(int idx, Dockable dockable, DockSplitPane splitPane) {
        if (!getDelegate().getTargetContext().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        getDelegate().getTargetContext().dock(idx, dockable, splitPane);
    }
*/
    public void dock(Dockable dockable, Side side) {
        if (!getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getDockableContext().getTargetContext() != null) {
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
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
        if (!getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getDockableContext().getTargetContext() != null) {
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
        }

        targetContext.dock(dockable, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        TargetContext ph = targetContext;
        for (Node node : dsp.getItems()) {
            if (DockRegistry.instanceOfDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.getDockableContext().setTargetContext(ph);
            } else if (node instanceof DockSplitPane) {
                update((DockSplitPane) node);
            }
        }
    }

    protected void update(DockSplitPane split, TargetContext ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.instanceOfDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.getDockableContext().setTargetContext(ph);
                /*                if (i < split.getDividers().size() && d.getDockableContext().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.getDockableContext().getDividerPos());
                }
                 */
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(this);
                DockSplitPane sp = (DockSplitPane) node;
                update(sp, ph);
            }
        }
    }

    public void update() {
        update(getRoot());
        update(this.getRoot(), targetContext);
    }

    protected void splitPaneAdded(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.instanceOfDockable(node)) {
                DockRegistry.dockable(node).getDockableContext().setTargetContext(dpt.getTargetContext());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    protected void splitPaneRemoved(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.instanceOfDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }
    }

    public boolean isUsedAsDockTarget() {
        return targetContext.isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        targetContext.setUsedAsDockTarget(usedAsDockTarget);
    }

    @Override
    public void handle(ActionEvent event) {
        update();
    }


}//class
