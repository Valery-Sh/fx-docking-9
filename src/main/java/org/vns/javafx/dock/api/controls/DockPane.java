package org.vns.javafx.dock.api.controls;

import javafx.beans.DefaultProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.DockPaneBase;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
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

    private DockPaneBase delegate;// = new DockPaneBase();
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

    protected DockPaneBase getDelegate() {
        if (delegate == null) {
            delegate = new DockPaneBase();
        }
        return delegate;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockPaneSkin(this);
    }

    public DockSplitPane getRoot() {
        return ((DockPaneHandler) getDelegate().paneHandler()).getRoot();
    }

    public void setRoot(DockSplitPane splitPane) {
        delegate = new DockPaneBase(splitPane);
        update();
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
        update(((DockPaneHandler) getDelegate().paneHandler()).getRoot());
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

    public static class DockPaneSkin extends SkinBase<DockPane> {

        public DockPaneSkin(DockPane control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

}
