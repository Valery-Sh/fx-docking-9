package org.vns.javafx.dock.api;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneHandler extends PaneHandler {

    private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);

    private SplitDelegate splitDelegate;
    private SplitDelegate.DockSplitPane rootSplitPane;

    public DockSplitPane getRoot() {
        return rootSplitPane;
    }

    public DockPaneHandler(Pane dockPane) {
        super(dockPane);
        init();
    }

    public DockPaneHandler(Pane dockPane, DockSplitPane rootSplitPane) {
        super(dockPane);
        this.rootSplitPane = rootSplitPane;
        init();
    }

    @Override
    public Pane getDockPane() {
        return (Pane) super.getDockPane();
    }

    private void init() {
        if (rootSplitPane == null) {
            rootSplitPane = new SplitDelegate.DockSplitPane();
        }
        //rootSplitPane = new HSplit();
        getDockPane().getChildren().add(rootSplitPane);
        splitDelegate = new SplitDelegate(rootSplitPane, this);
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
            //System.err.println("isDocked roorSplitPane=" + rootSplitPane + "; retval=" + retval);
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
        if (dsp != null) {
            PaneHandler ph = DockRegistry.dockable(dockNode).nodeHandler().getPaneHandler();
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).nodeHandler().setPaneHandler(ph);
            clearEmptySplitPanes(rootSplitPane, dsp);
        }
    }

    /**
     * Does nothing. Subclasses can change behavior.
     *
     * @param dockable a dock node witch divider pos is to be set
     * @param parent a parent the given dock node of th
     */
    @Override
    public void updateDividers(Node dockable, Parent parent) {
//        System.err.println("111. DockPaneHandler: dockable=" + dockable);
        if (parent instanceof DockSplitPane) {
            DockSplitPane split = (DockSplitPane) parent;
//            System.err.println("split.getDividers().sz=" + split.getDividers().size());
            for (int i = 0; i < split.getItems().size(); i++) {
                Node node = split.getItems().get(i);
                if (DockRegistry.isDockable(node)) {
                    Dockable d = DockRegistry.dockable(node);
                    if (i < split.getDividers().size() && d.nodeHandler().getDividerPos() >= 0) {
                        split.getDividers().get(i).setPosition(d.nodeHandler().getDividerPos());
                    }
                }
                if (node instanceof DockSplitPane) {
                    DockSplitPane dsp = (DockSplitPane) node;
                    if (i < split.getDividers().size() && dsp.getDividerPos() >= 0) {
                        split.getDividers().get(i).setPosition(dsp.getDividerPos());
                    }
                }

            }
        }

    }

    //public void updateDividers(double dividerPos, int divIndex, Dockable dockable, Parent parent) {
    public void updateDividers___(double dividerPos, int divIndex, Dockable dockable, Parent parent) {
//        System.err.println("111. DockPaneHandler: dockable=" + dockable);
        if (parent instanceof DockSplitPane) {
            DockSplitPane split = (DockSplitPane) parent;
//            System.err.println("split.getDividers().sz=" + split.getDividers().size());
            for (int i = 0; i < split.getItems().size(); i++) {
                Node node = split.getItems().get(i);
                if (DockRegistry.isDockable(node)) {
                    Dockable d = DockRegistry.dockable(node);
                    if (i > 0 && i < split.getItems().size() && d.nodeHandler().getDividerPos() >= 0) {
//                        System.err.println("DockPaneHandler: setDividerPos() i=" + i + "; d=" + d);
                        split.setDividerPosition(i - 1, d.nodeHandler().getDividerPos());
//                        System.err.println("DockPaneHandler: setDividerPos()  ; split.getItems().get(i-1).pos=" + split.getDividers().get(i-1).getPosition());
                    }
                }
            }
        }

    }

}//class
