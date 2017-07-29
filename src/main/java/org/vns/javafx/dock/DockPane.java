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
import org.vns.javafx.dock.api.DockPaneController;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockPane extends Control implements DockTarget, EventHandler<ActionEvent> {

    protected StackPane stackPane = new StackPane();
    private DockSplitPane root;
    private DockPaneController targetController;
    
    public DockPane() {
        super();
        init();
    }

    private void init() {
        //getDelegate();
        root = new DockSplitPane();
        root.setRoot(this);
        root.setId("rootSplitPane");
        targetController = new DockPaneController(this, root);
    }

    protected DockSplitPane getRoot() {
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
            delegate = new ControlDockPane(new DockPaneController(this));
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
    public DockTargetController targetController() {
        return targetController;
    }

/*    private void dock(int idx, Dockable dockable, DockSplitPane splitPane) {
        if (!getDelegate().targetController().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        getDelegate().targetController().dock(idx, dockable, splitPane);
    }
*/
    public void dock(Dockable dockable, Side side) {
        if (!targetController().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.dockableController().getTargetController() != null) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }
        targetController.dock(dockable, side);
    }

    public void dock(Dockable dockable, Side side, Dockable target) {
        if (!targetController().isAcceptable(dockable.node())) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.dockableController().getTargetController() != null) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }

        targetController.dock(dockable, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        DockTargetController ph = targetController;
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.dockableController().setTargetController(ph);
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
                d.dockableController().setTargetController(ph);
                /*                if (i < split.getDividers().size() && d.dockableController().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.dockableController().getDividerPos());
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
        update(this.getRoot(), targetController);
    }

    protected void splitPaneAdded(SplitPane sp, DockTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).dockableController().setTargetController(dpt.targetController());
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

    public boolean isUsedAsDockTarget() {
        return targetController.isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        targetController.setUsedAsDockTarget(usedAsDockTarget);
    }

    @Override
    public void handle(ActionEvent event) {
        update();
    }


}//class
