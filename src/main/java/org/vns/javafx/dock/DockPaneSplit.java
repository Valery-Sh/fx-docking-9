package org.vns.javafx.dock;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockPaneSplitBoxHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PaneHandler;
import org.vns.javafx.dock.api.SplitDelegate;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneSplit extends DockSplitPane {

    public DockPaneSplit() {
    }

    static {
        //StyleManager.getInstance()
        //        .addUserAgentStylesheet(Dockable.class.getResource("resources/default.css").toExternalForm());
        Dockable.initDefaultStylesheet(null);
    }
    private final ControlDockPane delegate = new ControlDockPane();

    protected ControlDockPane getDelegate() {
        return delegate;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        System.err.println("CREATE SKIN");
        return new DockPaneSplitSkin(this);
    }

/*    @Override
    public ObservableList<Node> getItems() {
        return getRoot().getItems();
    }
*/

    public void dock(Dockable dockNode, Side side) {
        getDelegate().dock(dockNode, side);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDelegate().paneHandler().dock(dockNode, side, target);
    }
    
    protected void update(SplitDelegate.DockSplitPane dsp) {
        SplitPane sp = dsp;
        PaneHandler ph = getDelegate().paneHandler();
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeHandler().setPaneHandler(ph);
            } else if (node instanceof SplitDelegate.DockSplitPane) {
                //((SplitDelegate.DockSplitPane) node).setRoot(getRoot());
                update((SplitDelegate.DockSplitPane) node);
            }
        }
    }

    public void update() {
        //update(((DockPaneHandler) getDelegate().paneHandler()).getRootSplitPane());
System.err.println("getDelegate().paneHandler()=" + getDelegate());        
        update(((DockPaneSplitBoxHandler) getDelegate().paneHandler()).getDockPane());
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
    public static class DockPaneSplitSkin extends SkinBase<DockPaneSplit> {

        public DockPaneSplitSkin(DockPaneSplit control) {
            super(control);
            getChildren().add(control.getDelegate());
            //init(control);
        }

        private void init(DockPane dp) {

            //dp.setSkin(this);
        }

    }

    public class ControlDockPane extends DockSplitPane implements DockPaneTarget {

        private PaneHandler paneHandler;

        public ControlDockPane() {
            init();
        }

        private void init() {
            paneHandler = new DockPaneSplitBoxHandler(this);
        }

        @Override
        public PaneHandler paneHandler() {
            return paneHandler;
        }

        public Dockable dock(Dockable node, Side dockPos) {
            return paneHandler.dock(node, dockPos);
        }

        @Override
        public DockSplitPane pane() {
            return this;
        }
    }

    public static class DockPaneHandler extends DockPaneSplitBoxHandler {

        public DockPaneHandler(Region dockPane) {
            super(dockPane);
        }

        @Override
        public DockSplitPane getDockPane() {
            return super.getDockPane();
        }

    }
}
