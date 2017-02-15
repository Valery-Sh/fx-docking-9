package org.vns.javafx.dock;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.api.DockPaneController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;

/**
 *
 * @author Valery
 */
public class DockPane extends DockSplitPane implements DockPaneTarget, EventHandler<ActionEvent> {

    private ControlDockPane delegate;

    public DockPane() {
        super();
        init();
    }

    private void init() {
        getDelegate();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected ControlDockPane getDelegate() {
        if (delegate == null) {
            delegate = new ControlDockPane(new DockPaneController(this));
            //delegate = this;
            setRoot(this);

        }

        return delegate;
    }

    @Override
    public DockSplitPane pane() {
        return this;
    }

    @Override
    public DockTargetController paneController() {
        return getDelegate().paneController();
    }

    public void dock(Dockable dockNode, Side side) {
        getDelegate().dock(dockNode, side);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        //((DockPaneExecutor) getDelegate().paneController().getDockExecutor()).dock(dockNode, side, target);
        getDelegate().paneController().dock(dockNode, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        DockTargetController ph = getDelegate().paneController();
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeController().setPaneController(ph);
            } else if (node instanceof DockSplitPane) {
                update((DockSplitPane) node);
            }
        }
    }

    protected void update(DockSplitPane split, DockTargetController ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeController().setPaneController(ph);
                /*                if (i < split.getDividers().size() && d.nodeController().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.nodeController().getDividerPos());
                }
                 */
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(getRoot());
                DockSplitPane sp = (DockSplitPane) node;
                /*                if (i < split.getDividers().size() && sp.getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(sp.getDividerPos());
                }
                 */
                update(sp, ph);
            }
        }
    }

    @Override
    public void update() {
        update(this);
        update(this, getDelegate().paneController());
    }

    @Override
    protected void splitPaneAdded(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).nodeController().setPaneController(dpt.paneController());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    @Override
    protected void splitPaneRemoved(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }

    }

    public boolean isUsedAsDockTarget() {
        return getDelegate().paneController().isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        getDelegate().paneController().setUsedAsDockTarget(usedAsDockTarget);
    }

    @Override
    public void handle(ActionEvent event) {
        update();
    }

    public class ControlDockPane implements DockPaneTarget {

        private DockPaneController paneController;

        public ControlDockPane(DockPaneController paneController) {
            this.paneController = paneController;
            init();

        }

        private void init() {
            //paneController = new DockPaneController(this);
        }

        @Override
        public DockSplitPane pane() {
            return (DockSplitPane) paneController.getDockPane();
        }

        @Override
        public DockPaneController paneController() {
            return paneController;
        }

        public void dock(Dockable node, Side dockPos) {
            paneController.dock(node, dockPos);
        }

    }

}//class
