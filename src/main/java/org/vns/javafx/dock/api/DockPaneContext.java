package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.SideIndicator;
import org.vns.javafx.dock.api.indicator.DockPaneIndicatorPopup;
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
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.event.DockEvent;
import org.vns.javafx.dock.api.indicator.SideIndicator.PaneSideIndicator;
import org.vns.javafx.dock.api.save.DockTreeItemBuilderFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

public class DockPaneContext extends LayoutContext {


    private DockExecutor dockExecutor;
    private final DockSplitPane root;
    private DockTreeItemBuilder dockTreeItemBuilder;
    private SideIndicator.NodeSideIndicator nodeIndicator;

    private RestoreData restoreData;
    
    
    public DockPaneContext(Node dockPane, DockSplitPane root) {
        super(dockPane);
        this.root = root;
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(PositionIndicator.class, new PaneSideIndicator(this));

        lookup.putUnique(IndicatorManager.class,new DockPaneIndicatorPopup(this));
        lookup.add(new DockTreeItemBuilderFactory());        
    }

    public DockSplitPane getRoot() {
        return root;
    }
    protected DockSplitPane getParentSplitPane(Node node) {
        if ( node == null ) {
            return null;
        }
        DockSplitPane retval = null;
        Node parent = node.getParent();
     
        if ( node == root) {
            return root;
        }
        while( parent != root ) {
            if ( (parent instanceof HPane) || (parent instanceof VPane ) ) {
                retval = (DockSplitPane)parent;
                break;
            }
            if ( parent == null ) {
                break;
            }            
            parent = parent.getParent();
        
        }
        return retval;
    }
            
    protected DockExecutor getDockExecutor() {
        if (dockExecutor == null) {
            dockExecutor = new DockExecutor(this, root);
        }
        return dockExecutor;
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            //retval = DockUtil.getParentSplitPane(root, node) != null;
            retval = getParentSplitPane(node) != null;
        }
        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Object o = getValue(dockable);
        if ( o == null || Dockable.of(o) == null ) {
            return;
        }
        Dockable dragged = Dockable.of(o);
        
        IndicatorPopup popup = (IndicatorPopup)getLookup().lookup(IndicatorManager.class); //21.08

        Node node = dragged.node();
        if (!(popup instanceof DockPaneIndicatorPopup)) {
            return;
        }
        DockPaneIndicatorPopup dp = (DockPaneIndicatorPopup) popup;
        Dockable d = Dockable.of(node);
        DockPane dockPane = (DockPane) this.getLayoutNode();
        
        Node titleBar = dockPane.getTitleBar();
        
        if (d.getContext().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
            if (dp.getTargetPaneSidePos() != null) {
                Side pos = dp.getTargetPaneSidePos();
                if ( pos == Side.TOP && titleBar != null) {
                    dock(dragged, Side.BOTTOM, Dockable.of(dockPane.getTitleBar()));
                } else if ( (pos == Side.LEFT || pos == Side.RIGHT) && titleBar != null) {
                    undock(titleBar);
                    dock(dragged, pos);
                    dockPane.dock(titleBar, Side.TOP);
                } else {
                    dock(dragged, dp.getTargetPaneSidePos());
                }
            } else if (dp.getTargetNodeSidePos() != null) {
                Dockable t = dp.getDragTarget() == null ? null : Dockable.of(dp.getDragTarget());
                dock(dragged, dp.getTargetNodeSidePos(), t);
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
        Object o = getValue(dockable);
        if ( o == null || Dockable.of(o) == null ) {
            return;
        }
        Dockable dragged = Dockable.of(o);
        if (isDocked(dragged.node())) {
            return;
        }
        doDock(dragged.node(), pos);
        
    }
    
    private boolean doDock(Node node, Side dockPos) {
        if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
            ((Stage) node.getScene().getWindow()).close();
        }
        getDockExecutor().dock(node, dockPos);
        commitDock(node);
        return true;
    }

    @Override
    protected void commitDock(Node node) {
        if (DockRegistry.isDockable(node)) {
            DockableContext dockableContext = Dockable.of(node).getContext();
            if (dockableContext.getLayoutContext() == null || dockableContext.getLayoutContext() != this) {
                dockableContext.setLayoutContext(this);
            }
        }
    }


    public void dock(Dockable dockable, Side side, Dockable target) {
        Object o = getValue(dockable);
        if ( o == null || Dockable.of(o) == null ) {
            return;
        }
        Dockable dragged = Dockable.of(o);
        if (isDocked(dragged.node())) {
            return;
        }
        if (!(dragged instanceof Node) && !DockRegistry.getDockables().containsKey(dragged.node())) {
            DockRegistry.getDockables().put(dragged.node(), dragged);
        }

        Node node = dragged.node();
        
        DockEvent event;
                
        if (target == null) {
            dock(dragged, side);
            event = new DockEvent(DockEvent.NODE_DOCKED,dragged.node(), getLayoutNode(), side, null);
        } else {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            getDockExecutor().dock(node, side, target);
            event = new DockEvent(DockEvent.NODE_DOCKED, dragged.node(), getLayoutNode(),side, target);
            
        }
        DockableContext dockableContext = dragged.getContext();
        if (dockableContext.getLayoutContext() == null || dockableContext.getLayoutContext() != this) {
            dockableContext.setLayoutContext(this);
        }
    }
    
    
    @Override
    public boolean restore(Dockable dockable) {
        boolean retval = true;
        if ( restoreData != null && dockable.getContext().isFloating() ) {
            //if ( restoreData.parent )
            System.err.println("1 dockable.getTargetContext() = " + dockable.getContext().getLayoutContext());
            restoreData.parent.getItems().add(dockable.node());
            commitDock(dockable.node());
            System.err.println("2 dockable.getTargetContext() = " + dockable.getContext().getLayoutContext());
        }
        restoreData = null;
        
        return retval;
    }
    
    @Override
    public void remove(Node dockNode) {

        //DockSplitPane parent = DockUtil.getParentSplitPane(root, dockNode);
        
        DockSplitPane parent = getParentSplitPane(dockNode);
        int idx = parent.getItems().indexOf(dockNode);
        
        if (parent != null) {
            LayoutContext ph = Dockable.of(dockNode).getContext().getLayoutContext();
            parent.getItems().remove(dockNode);
            Dockable.of(dockNode).getContext().setLayoutContext(ph);
            restoreData = clearEmptySplitPanes(parent);
            if ( restoreData == null ) {
                restoreData = new RestoreData(parent, idx);
            }
        }
    }
    protected RestoreData clearEmptySplitPanes(DockSplitPane parent) {
        if (root == null || !parent.getItems().isEmpty()) {
            return null;
        }
        DockSplitPane topNotEmpty = parent;
        List<DockSplitPane> list = new ArrayList<>();

        DockSplitPane dsp = parent;
        while (true) {
            dsp = getParentSplitPane(dsp);
            if (dsp == null) {
                break;
            }
            list.add(dsp);
//            topNotEmpty = dsp;
        }
        
        list.add(0, parent);
        int idx = -1;
        
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getItems().isEmpty()) {
                topNotEmpty = list.get(i);
                break;
            }
            if (i < list.size() - 1) {
                idx = list.get(i + 1).getItems().indexOf(list.get(i));
                list.get(i + 1).getItems().remove(list.get(i));
            }
        }
        if ( topNotEmpty != null && idx >= 0 ) {
            return new RestoreData(topNotEmpty, idx);
        }
        return null;
    }

    /**
     * For test purpose
     *
     * @return the list of {@code dockables}
     */
    public ObservableList<Dockable> getDockables() {
        ObservableList<Dockable> list = FXCollections.observableArrayList();
        ((DockPane) getLayoutNode()).getItems().forEach(node -> {
            if (DockRegistry.isDockable(node)) {
                list.add(Dockable.of(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
        return list;
    }

    private void getDockables(DockSplitPane pane, List<Dockable> list) {
        pane.getItems().forEach(node -> {
            //!!!08
            if (DockRegistry.isDockable(node)) {
                list.add(Dockable.of(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
    }

/*    @Override
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
            } else 
            if (newOrientation != oldOrientation) {
                DockSplitPane dp;
                DockSplitPane dpOrig;
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

            Dockable d = Dockable.of(node);

            Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            Orientation oldOrientation = parentSplitPane.getOrientation();

            if (newOrientation != oldOrientation) {
                DockSplitPane dp;
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
    public class RestoreData{
        
        DockSplitPane parent;
        int index;

        public RestoreData(DockSplitPane parent, int index) {
            this.parent = parent;
            this.index = index;
        }
        
    }


}//class DockPaneContext
