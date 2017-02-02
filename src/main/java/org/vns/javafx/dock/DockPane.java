package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneBox;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "root")
public class DockPane extends Control {

    private final ControlDockPane delegate = new ControlDockPane();
    //private Node root;

    public DockPane() {
    }
    public DockPane(VPane root) {
        init(root);
    }
    public DockPane(HPane root) {
        init(root);
    }

    private void init(DockSplitPane root) {
        setRoot(root);
    }

    protected ControlDockPane getDelegate() {
        return delegate;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockPaneSkin(this);
    }

    public DockSplitPane getRoot() {
        return ((DockPaneHandler) getDelegate().paneHandler()).getRootSplitPane();
    }

    public void setRoot(DockSplitPane splitPane) {
        System.err.println("1. SET ROOT %%%%%%%%%%%%%%%% " + splitPane);
        ///delegate = new DockPaneBox(splitPane);
        getDelegate().getChildren().add(splitPane);
        ((DockPaneHandler)delegate.paneHandler()).setRootSplitPane(splitPane);
        update();
        System.err.println("2. SET ROOT %%%%%%%%%%%%%%%% " + ((DockPaneHandler)delegate.paneHandler()).getRootSplitPane() );
        splitPane.updateDividers(splitPane);
    }

    public void dock(Dockable dockNode, Side side) {
        getDelegate().dock(dockNode, side);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDelegate().paneHandler().dock(dockNode, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        PaneHandler ph = getDelegate().paneHandler();
        for (Node node : dsp.getItems()) {
            if ( DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeHandler().setPaneHandler(ph);
            } else if ( node instanceof DockSplitPane ) {
                update((DockSplitPane) node);
            }
        }
    }

    protected void update() {
        update(((DockPaneHandler) getDelegate().paneHandler()).getRootSplitPane());
    }

    protected void splitPaneAdded(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    protected void splitPaneRemoved(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                //DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }

    }
    public boolean isUsedAsDockTarget() {
        return getDelegate().paneHandler().isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        getDelegate().paneHandler().setUsedAsDockTarget(usedAsDockTarget);
    }

/*    @Override
    public Region pane() {
        return this;
    }

    @Override
    public PaneHandler paneHandler() {
        return delegate.paneHandler();
    }
*/
    public static class DockPaneSkin extends SkinBase<DockPane> {

        public DockPaneSkin(DockPane control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

    public class ControlDockPane extends StackPane implements DockPaneTarget {

        private PaneHandler paneHandler;

        public ControlDockPane() {
            init();
        }

        private void init() {
            paneHandler = paneHandler = new DockPaneHandler(this);
        }

        @Override
        public PaneHandler paneHandler() {
            return paneHandler;
        }

        public Dockable dock(Dockable node, Side dockPos) {
            return paneHandler.dock(node, dockPos);
        }

        @Override
        public Pane pane() {
            return this;
        }

    }
    
}
