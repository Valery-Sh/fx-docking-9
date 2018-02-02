package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.SideIndicator;
import org.vns.javafx.dock.api.indicator.DragPopup;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.event.DockEvent;
import org.vns.javafx.dock.api.indicator.SideIndicator.PaneSideIndicator;
import org.vns.javafx.dock.api.save.DockTreeItemBuilderFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

public class DockPaneContext extends TargetContext {

    //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    //+private DockDelegate dockDelegate;
    private DockExecutor dockExecutor;
    private final DockSplitPane root;
    private DockTreeItemBuilder dockTreeItemBuilder;
    private SideIndicator.NodeSideIndicator nodeIndicator;

    public DockPaneContext(Node dockPane, DockSplitPane root) {
        super(dockPane);
        this.root = root;
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(PositionIndicator.class, new PaneSideIndicator(this));

        lookup.putUnique(IndicatorManager.class,new DragPopup(this));
        lookup.add(new DockTreeItemBuilderFactory());        
    }

    public DockSplitPane getRoot() {
        return root;
    }

    /*    @Override
    public SideIndicator.NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }
     */
 /*    @Override
    protected IndicatorPopup createIndicatorPopup() {
        return new DragPopup(this);
    }
     */
 /*    protected SideIndicator.NodeSideIndicator createNodeIndicator() {
        return new SideIndicator.NodeSideIndicator(this);
    }
     */
 /*    @Override
    protected PositionIndicator createPositionIndicator() {
        return new PaneSideIndicator(this);
    }
     */
    protected DockExecutor getDockExecutor() {
        if (dockExecutor == null) {
            dockExecutor = new DockExecutor(this, root);
        }
        return dockExecutor;
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.instanceOfDockable(node)) {
            //24.06retval = DockUtil.getParentSplitPane((DockSplitPane) getTargetNode(), node) != null;
            retval = DockUtil.getParentSplitPane(root, node) != null;
        }
        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Dockable dragged = dockable;
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        Object v = dc.getValue();

        if ( v != null && ! (dc.isValueDockable()) ) {
            return;
        } else if (dc.isValueDockable()) {
            dragged = DockRegistry.dockable(v);
        }
        if ( dockable instanceof DragContainer ) {
            if ( ! ((DragContainer)dockable).isValueDockable() ) {
                return;
            }
            dragged = (Dockable) ((DragContainer)dockable).getValue();
        }
        //21.08IndicatorPopup popup = getIndicatorPopup();
        //IndicatorPopup popup = getLookup().lookup(IndicatorPopup.class); //21.08
        IndicatorPopup popup = (IndicatorPopup)getLookup().lookup(IndicatorManager.class); //21.08
        ///
        Node node = dragged.node();
        if (!(popup instanceof DragPopup)) {
            return;
        }
        DragPopup dp = (DragPopup) popup;
        Dockable d = DockRegistry.dockable(node);
        if (d.getDockableContext().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
            if (dp.getTargetPaneSidePos() != null) {
                dock(DockRegistry.dockable(node), dp.getTargetPaneSidePos());
            } else if (dp.getTargetNodeSidePos() != null) {
                Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                dock(DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
            }
        }

    }

    /**
     * The method does nothing.
     *
     * @param mousePos the mouse pos
     * @param node the node to be docked
     * @return true if the method execution was successful
     */
    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    public void dock(Dockable dockable, Side pos) {
        if (isDocked(dockable.node())) {
            return;
        }
        if (doDock(dockable.node(), pos)) {
            dockable.getDockableContext().setFloating(false);
            getTargetNode().fireEvent(new DockEvent(DockEvent.NODE_DOCKED,dockable.node(), getTargetNode()));
        }
    }
/////

/////    
    private boolean doDock(Node node, Side dockPos) {
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            ((Stage) node.getScene().getWindow()).close();
        }
        getDockExecutor().dock(node, dockPos);
        commitDock(node);
        return true;
    }

    protected void commitDock(Node node) {
        if (DockRegistry.instanceOfDockable(node)) {
            DockableContext dockableContext = DockRegistry.dockable(node).getDockableContext();
            if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                dockableContext.setTargetContext(this);
            }
            dockableContext.setFloating(false);
        }
    }

    /*13.07    private boolean doDock(Node node, Side dockPos, Dockable targetDockable) {
        if (isDocked(node)) {
            return false;
        }
        if (targetDockable == null) {
            dock(DockRegistry.dockable(node), dockPos);
        } else {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            if (DockRegistry.instanceOfDockable(node)) {
                DockRegistry.dockable(node).getDockableContext().setFloating(false);
            }
            getDockExecutor().dock(node, dockPos, targetDockable);
        }
        if (DockRegistry.instanceOfDockable(node)) {
            DockableContext dockableContext = DockRegistry.dockable(node).getDockableContext();
            if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                dockableContext.setTargetContext(this);
            }
        }
        return true;
    }
     */
    public void dock(Dockable dockable, Side side, Dockable target) {
        //13.07 getDockExecutor().dock(dockable, side, target);
        if (isDocked(dockable.node())) {
            return;
        }
        if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
            DockRegistry.getDockables().put(dockable.node(), dockable);
        }
        dockable.getDockableContext().setFloating(false);

        //doDock(dockable.node(), side, target);
        Node node = dockable.node();
        
        DockEvent event = null;
                
        if (target == null) {
            dock(dockable, side);
            event = new DockEvent(DockEvent.NODE_DOCKED,dockable.node(), getTargetNode(), side, null);
        } else {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
//            if (DockRegistry.instanceOfDockable(node)) {
            dockable.getDockableContext().setFloating(false);
//            }
            getDockExecutor().dock(node, side, target);
            event = new DockEvent(DockEvent.NODE_DOCKED, dockable.node(), getTargetNode(),side, target);
            
        }
//        if (DockRegistry.instanceOfDockable(node)) {
        DockableContext nodeController = dockable.getDockableContext();
        if (nodeController.getTargetContext() == null || nodeController.getTargetContext() != this) {
            nodeController.setTargetContext(this);
        }
        
        getTargetNode().fireEvent(event);
//        }

    }

    /*    public void dock(int idx,Dockable dockable,DockSplitPane splitpane) {
        if ( dockable.getDockableContext().getTargetContext() != null ) {
            dockable.getDockableContext().getTargetContext().undock(dockable.node());
        }
        getDockExecutor().dock(idx, dockable, splitpane);
    }
     */
    @Override
    public void remove(Node dockNode) {
        //DockSplitPane dsp = getParentSplitPane((DockSplitPane) getTargetNode(), dockNode);
        DockSplitPane dsp = getParentSplitPane(root, dockNode);
        if (dsp != null) {
            TargetContext ph = DockRegistry.dockable(dockNode).getDockableContext().getTargetContext();
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).getDockableContext().setTargetContext(ph);
            clearEmptySplitPanes(root, dsp);
            getTargetNode().fireEvent(new DockEvent(DockEvent.NODE_UNDOCKED, dockNode, getTargetNode()));
        }
    }

    /**
     * For test purpose
     *
     * @return the list of dockables
     */
    public ObservableList<Dockable> getDockables() {
        ObservableList<Dockable> list = FXCollections.observableArrayList();
        ((DockPane) getTargetNode()).getItems().forEach(node -> {
            if (DockRegistry.instanceOfDockable(node)) {
                list.add(DockRegistry.dockable(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
        //getDockables(((DockPane) getTargetNode()).getRoot(), list);
        return list;
    }

    private void getDockables(DockSplitPane pane, List<Dockable> list) {
        pane.getItems().forEach(node -> {
            if (DockRegistry.instanceOfDockable(node)) {
                list.add(DockRegistry.dockable(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
    }

    @Override
    public Object getRestorePosition(Dockable dockable) {
        DockSplitPane dsp = null;
        Parent p = dockable.node().getParent();
        while (p != null) {
            if (p instanceof DockSplitPane) {
                dsp = (DockSplitPane) p;
                break;
            }
            p = p.getParent();
        }
        Integer idx = 0;
        if (dsp != null) {
            idx = dsp.getItems().indexOf(dockable.node());
        }
        return new Object[]{dsp, idx};
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        Object[] obj = (Object[]) restoreposition;;
        DockSplitPane dsp = (DockSplitPane) obj[0];
        int idx = (Integer) obj[1];
        if (!dsp.getItems().contains(dockable.node())) {
            dsp.getItems().add(idx, dockable.node());
            commitDock(dockable.node());
        }
    }

    /*    public static class DockDelegate {

        private DockSplitPane root;
        private final DockPaneContext paneController;

        public DockDelegate(DockSplitPane root, DockPaneContext paneController) {
            this.root = root;
            this.paneController = paneController;
        }

        public DockSplitPane getRoot() {
            return root;
        }

        public void dock(Dockable dockable, Side dockPos) {
            dock(dockable.node(), dockPos);
        }

        private void dock(Node node, Side dockPos) {

            DockSplitPane rootSplitPane = root;

            if (rootSplitPane == null) {
                rootSplitPane = new DockSplitPane();
                root = rootSplitPane;
                rootSplitPane.getItems().add(node);
                return;
            }
            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = root.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                } else {
                    dp = new VPane();
                }
                dp.getItems().addAll(root.getItems());

                root.getItems().clear();
                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = dp.getItems().size();
                }
                dp.getItems().add(idx, node);
                root.getItems().add(dp);
            } else {
                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = root.getItems().size();
                }
                root.getItems().add(idx, node);
            }
        }

        public void dock(Node node, Side dockPos, Dockable target) {
            if (target == null) {
                dock(node, dockPos);
                return;  //added 26.01
            }

            Node targetNode = target.node();

            DockSplitPane parentSplitPane = getTargetSplitPane(targetNode);
            //DockSplitPane targetSplitPane = parentSplitPane;

            if (parentSplitPane == null) {
                return;
            }

            Dockable d = DockRegistry.dockable(node);

            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = parentSplitPane.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                } else {
                    dp = new VPane();
                }

                int idx = parentSplitPane.getItems().indexOf((Node) targetNode);

                parentSplitPane.getItems().remove((Node) targetNode);
                if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                    dp.getItems().add(node);
                    dp.getItems().add((Node) targetNode);
                } else {
                    dp.getItems().add((Node) targetNode);
                    dp.getItems().add(node);
                }
                parentSplitPane.getItems().add(idx, dp);
                //07.05targetSplitPane = p;
            } else {
                int idx = parentSplitPane.getItems().indexOf(targetNode);
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    ++idx;
                }
                parentSplitPane.getItems().add(idx, node);
            }
        }

        protected DockSplitPane getTargetSplitPane(Node target) {
            DockSplitPane retval = null;
            DockSplitPane split = root;
            Stack<DockSplitPane> stack = new Stack<>();
            stack.push(split);

            while (!stack.empty()) {
                split = stack.pop();
                if (split.getItems().contains(target)) {
                    retval = split;
                    break;
                }
                for (Node n : split.getItems()) {
                    if (n instanceof DockSplitPane) {
                        stack.push((DockSplitPane) n);
                    }
                }
            }
            return retval;

        }

    }//class DockDelegate
     */
    public static class DockExecutor {

        private final DockPaneContext paneController;
        private DockSplitPane root;

        public DockExecutor(DockPaneContext paneController, DockSplitPane root) {
            this.paneController = paneController;
            this.root = root;
        }

        private void dock(Node node, Side dockPos) {

            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = root.getOrientation();

            if (root.getItems().isEmpty()) {
                root.getItems().add(node);
            }
            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                DockSplitPane dpOrig = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                    dpOrig = new VPane();
                } else {
                    dp = new VPane();
                    dpOrig = new HPane();
                }

                dpOrig.getItems().addAll(root.getItems());
                dp.getItems().add(dpOrig);
                root.getItems().clear();

                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = dp.getItems().size();
                }
                dp.getItems().add(idx, node);
                root.getItems().add(dp);
                for (int i = 0; i < dp.getDividerPositions().length; i++) {
                    dp.setDividerPosition(i, dp.getDividerPositions()[i] + 0.01);
                    dp.setDividerPosition(i, dp.getDividerPositions()[i] - 0.01);
                }

            } else {
                int idx = 0;
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    idx = root.getItems().size();
                }
                root.getItems().add(idx, node);
            }
            for (int i = 0; i < root.getDividerPositions().length; i++) {
                root.setDividerPosition(i, root.getDividerPositions()[i] + 0.01);
                root.setDividerPosition(i, root.getDividerPositions()[i] - 0.01);
            }
        }

        /*        protected static void clearRoot(DockSplitPane root) {

            List<SplitPane> list = new ArrayList<>();
            for (Node node : root.getItems()) {
                if (node instanceof SplitPane) {
                    if (((SplitPane) node).getItems().isEmpty()) {
                        list.add((SplitPane) node);
                    } else {
                        clearRoot(root, (SplitPane) node);
                    }
                }
            }
            list.forEach(sp -> {
                root.getItems().remove(sp);
            });            
        }
        private static void clearRoot(DockSplitPane root, SplitPane splitPane) {

            List<SplitPane> list = new ArrayList<>();
            for (Node node : splitPane.getItems()) {
                if (node instanceof SplitPane) {
                    if (((SplitPane) node).getItems().isEmpty()) {
                        list.add((SplitPane) node);
                    } else {
                        clearRoot(root, (SplitPane) node);
                    }
                }
            }
            list.forEach(sp -> {
                root.getItems().remove(sp);
            });

        }
         */
        protected void clear() {

            List<SplitPane> list = new ArrayList<>();
            for (Node node : root.getItems()) {
                if (node instanceof SplitPane) {
                    if (((SplitPane) node).getItems().isEmpty()) {
                        list.add((SplitPane) node);
                    } else {
                        clear((SplitPane) node);
                    }
                }
            }
            list.forEach(sp -> {
                root.getItems().remove(sp);
            });
        }

        private void clear(SplitPane splitPane) {

            List<SplitPane> list = new ArrayList<>();
            for (Node node : splitPane.getItems()) {
                if (node instanceof SplitPane) {
                    if (((SplitPane) node).getItems().isEmpty()) {
                        list.add((SplitPane) node);
                    } else {
                        clear((SplitPane) node);
                    }
                }
            }
            list.forEach(sp -> {
                root.getItems().remove(sp);
            });

        }

        private void dock(Node node, Side dockPos, Dockable target) {
            if (target == null) {
                dock(node, dockPos);
                return;
            }

            Node targetNode = target.node();

            DockSplitPane parentSplitPane = getTargetSplitPane(targetNode);

            if (parentSplitPane == null) {
                return;
            }

            Dockable d = DockRegistry.dockable(node);

            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = parentSplitPane.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp = null;
                if (newOrientation == Orientation.HORIZONTAL) {
                    dp = new HPane();
                } else {
                    dp = new VPane();
                }

                int idx = parentSplitPane.getItems().indexOf((Node) targetNode);

                parentSplitPane.getItems().remove((Node) targetNode);
                if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                    dp.getItems().add(node);
                    dp.getItems().add((Node) targetNode);
                } else {
                    dp.getItems().add((Node) targetNode);
                    dp.getItems().add(node);
                }
                parentSplitPane.getItems().add(idx, dp);
            } else {
                int idx = parentSplitPane.getItems().indexOf(targetNode);
                if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                    ++idx;
                }
                parentSplitPane.getItems().add(idx, node);
            }
            for (int di = 0; di < parentSplitPane.getDividerPositions().length; di++) {
                parentSplitPane.setDividerPosition(di, parentSplitPane.getDividerPositions()[di] + 0.01);
                parentSplitPane.setDividerPosition(di, parentSplitPane.getDividerPositions()[di] - 0.01);
            }
            clear();
        }

        protected DockSplitPane getTargetSplitPane(Node target) {
            DockSplitPane retval = null;
            DockSplitPane split = root;
            Stack<DockSplitPane> stack = new Stack<>();
            stack.push(split);

            while (!stack.empty()) {
                split = stack.pop();
                if (split.getItems().contains(target)) {
                    retval = split;
                    break;
                }
                for (Node n : split.getItems()) {
                    if (n instanceof DockSplitPane) {
                        stack.push((DockSplitPane) n);
                    }
                }
            }
            return retval;

        }

    }//DockExcecutor


}//class DockPaneContext
