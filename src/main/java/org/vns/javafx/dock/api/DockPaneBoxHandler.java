package org.vns.javafx.dock.api;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneBoxHandler extends PaneHandler {

    private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);

    private SplitDelegate splitDelegate;
    private SplitDelegate.DockSplitPane rootSplitPane;

    public SplitDelegate.DockSplitPane getRootSplitPane() {
        return rootSplitPane;
    }

    public DockPaneBoxHandler(Pane dockPane) {
        super(dockPane);
        init();
    }

    @Override
    public Pane getDockPane() {
        return (Pane) super.getDockPane();
    }

    private void init() {
        setRootSplitPane(new SplitDelegate.DockSplitPane());
    }


    public void setRootSplitPane(SplitDelegate.DockSplitPane rootSplitPane) {
        this.rootSplitPane = rootSplitPane;
        addRootSplitPane();
        splitDelegate = new SplitDelegate(rootSplitPane, this);
    }
    protected void addRootSplitPane() {
        getDockPane().getChildren().add(rootSplitPane);
    }
    
    @Override
    public void dividerPosChanged(Node node, double oldValue, double newValue) {
        if ( DockRegistry.isDockable(node) && getRootSplitPane() != null ) {
            getRootSplitPane().update();
        }
    }

    public DoubleProperty dividerPosProperty() {
        return dividerPosProperty;
    }

    public double getDividerPos() {
        return dividerPosProperty.get();
    }

    public void setDividerPos(double dividerPos) {
        this.dividerPosProperty.set(dividerPos);
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval;
        if (DockRegistry.isDockable(node)) {
            retval = DockUtil.getParentSplitPane(rootSplitPane, node) != null;
        } else {
            retval = null != notDockableItemsProperty().get(node);
        }
        return retval;
    }

    @Override
    public Dockable dock(Dockable dockable, Side dockPos) {
        return super.dock(dockable, dockPos);
    }

    @Override
    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        return super.dock(dockable, dockPos, target);
    }

    @Override
    protected void doDock(Point2D mousePos, Node node, Side dockPos) {
        if ( splitDelegate == null ) {
            return;
        }
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            ((Stage) node.getScene().getWindow()).close();
        }
        splitDelegate.dock(DockRegistry.dockable(node), dockPos);

        SplitDelegate.DockSplitPane save = rootSplitPane;
        if (rootSplitPane != splitDelegate.getRoot()) {
            rootSplitPane = splitDelegate.getRoot();
        }

        int idx = getDockPane().getChildren().indexOf(save);

        getDockPane().getChildren().set(idx, rootSplitPane);

        if (DockRegistry.isDockable(node)) {
            DockNodeHandler state = DockRegistry.dockable(node).nodeHandler();
            if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                state.setPaneHandler(this);
            }
        }
    }

    @Override
    protected void doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
        if ( splitDelegate == null ) {
            return;
        }
        if (isDocked(node)) {
            return;
        }
        if (targetDockable == null) {
            dock(DockRegistry.dockable(node), dockPos);
        } else {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).nodeHandler().setFloating(false);
            }
            splitDelegate.dock(node, dockPos, targetDockable);
        }
        if (DockRegistry.isDockable(node)) {
            DockNodeHandler state = DockRegistry.dockable(node).nodeHandler();
            if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                state.setPaneHandler(this);
            }
            state.setDocked(true);
        }
    }

    @Override
    public void remove(Node dockNode) {
        SplitDelegate.DockSplitPane dsp = getParentSplitPane(rootSplitPane, dockNode);
        System.err.println("1. DockPaneHandler remove(dockNode " + dockNode.getId());
        System.err.println("1. DockPaneHandler remove from dockPane " + dsp.getId());        
        if (dsp != null) {
            PaneHandler ph = DockRegistry.dockable(dockNode).nodeHandler().getPaneHandler();
            System.err.println("1. DockPaneHandler remove(dockNode) dockPane id= " + ph.getDockPane().getId());
            
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).nodeHandler().setPaneHandler(ph);
            clearEmptySplitPanes(rootSplitPane, dsp);
        }
    }



}//class
