package org.vns.javafx.dock.api.demo;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
public class DockPaneOld extends DockSplitPane implements DockTarget , EventHandler<ActionEvent> {

    private ControlDockPane delegate;

    public DockPaneOld() {
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
            delegate = new ControlDockPane(new DockPaneControllerOld(this));
            //delegate = this;
            setRoot(this);

        }

        return delegate;
    }

    @Override
    public DockSplitPane target() {
        return this;
    }

    @Override
    public DockTargetController targetController() {
        return getDelegate().targetController();
    }

    public void dock(Dockable dockNode, Side side) {
        if ( ! targetController().isAcceptable(dockNode.node())) {
            throw new UnsupportedOperationException("The node '" + dockNode + "' to be docked is not registered by the DockLoader");
        }
        getDelegate().dock(dockNode, side);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        if ( ! targetController().isAcceptable(dockNode.node())) {
            throw new UnsupportedOperationException("The node '" + dockNode + "' to be docked is not registered by the DockLoader");
        }

        getDelegate().targetController().dock(dockNode, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        DockTargetController ph = getDelegate().targetController();
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.dockableController().setTargetController(ph);
            } else if (node instanceof DockSplitPane) {
                update((DockSplitPane) node);
            }
        }
    }

    @Override
    protected void update(DockSplitPane split, DockTargetController ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.dockableController().setTargetController(ph);
//                                if (i < split.getDividers().size() && d.dockableController().getDividerPos() >= 0) {
//                    split.getDividers().get(i).setPosition(d.dockableController().getDividerPos());
                //}
                for ( int di = 0; di < split.getDividerPositions().length; di++) {
                    split.setDividerPosition(di,split.getDividerPositions()[di] + 0.01 );
                }
                
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(getRoot());
                DockSplitPane sp = (DockSplitPane) node;
                /*                if (i < split.getDividers().size() && sp.getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(sp.getDividerPos());
                }
                 */
                for ( int di = 0; di < split.getDividerPositions().length; di++) {
                    split.setDividerPosition(di,split.getDividerPositions()[di] + 0.01);
                }
                
                update(sp, ph);
            }
        }
    }

    @Override
    public void update() {
        update(this);
        update(this, getDelegate().targetController());
    }

    @Override
    protected void splitPaneAdded(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).dockableController().setTargetController(dpt.targetController());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    @Override
    protected void splitPaneRemoved(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }
    }

    public boolean isUsedAsDockTarget() {
        return getDelegate().targetController().isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        getDelegate().targetController().setUsedAsDockTarget(usedAsDockTarget);
    }

    @Override
    public void handle(ActionEvent event) {
        update();
    }

    public class ControlDockPane implements DockTarget {

        private final DockPaneControllerOld paneController;

        public ControlDockPane(DockPaneControllerOld paneController) {
            this.paneController = paneController;
            init();

        }

        private void init() {
            //paneController = new DockPaneControllerOld(this);
        }

        @Override
        public DockSplitPane target() {
            return (DockSplitPane) paneController.getTargetNode();
        }

        @Override
        public DockPaneControllerOld targetController() {
            return paneController;
        }

        public void dock(Dockable node, Side dockPos) {
            paneController.dock(node, dockPos);
        }

    }

}//class