package org.vns.javafx.dock.api;

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
import org.vns.javafx.dock.api.indicator.IndicatorManager;

public class DockPaneContext extends LayoutContext {

    private DockExecutor dockExecutor;
    private final DockSplitPane root;
    private SideIndicator.NodeSideIndicator nodeIndicator;

    //private RestoreData restoreData;
    public DockPaneContext(Node dockPane, DockSplitPane root) {
        super(dockPane);
        this.root = root;
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(PositionIndicator.class, new PaneSideIndicator(this));
        lookup.putUnique(IndicatorManager.class, new DockPaneIndicatorPopup(this));
    }

    public DockSplitPane getRoot() {
        return root;
    }

    protected DockSplitPane getParentSplitPane(Node node) {
        if (node == null) {
            return null;
        }
        DockSplitPane retval = null;
        Node parent = node.getParent();
        if (node == root) {
            return root;
        }
        while (parent != null) {
            if ((parent instanceof HPane) || (parent instanceof VPane)) {
                retval = (DockSplitPane) parent;
                break;
            }
            if (parent == null) {
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
    public boolean contains(Object obj) {
        if ((obj == null) || !(obj instanceof Node)) {
            return false;
        }
        boolean retval = false;
        if (DockRegistry.isDockable(obj)) {
            retval = getParentSplitPane((Node) obj) != null;
        }
        return retval;
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        boolean retval = super.isAcceptable(dockable);

        if (retval) {
            Object v = getValue(dockable);
            if ((v instanceof HPane) || (v instanceof VPane)) {
                retval = false;
            } else if (Dockable.of(v) != null && ((Dockable.of(v).node() instanceof HPane) || (Dockable.of(v).node() instanceof VPane))) {
                retval = false;
            }
        }

        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Object o = getValue(dockable);
        if (o == null || Dockable.of(o) == null) {
            return;
        }
        Dockable dragged = Dockable.of(o);

        IndicatorPopup popup = (IndicatorPopup) getLookup().lookup(IndicatorManager.class); //21.08

        Node node = dragged.node();
        if (!(popup instanceof DockPaneIndicatorPopup)) {
            return;
        }
        DockPaneIndicatorPopup dp = (DockPaneIndicatorPopup) popup;
        Dockable d = Dockable.of(node);
        DockPane dockPane = (DockPane) this.getLayoutNode();

        Node titleBar = dockPane.getTitleBar();

        if (dockable.getContext().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
            if (dp.getTargetPaneSidePos() != null) {
                Side pos = dp.getTargetPaneSidePos();
                if (pos == Side.TOP && titleBar != null) {
                    dock(dragged, Side.BOTTOM, Dockable.of(dockPane.getTitleBar()));
                } else if ((pos == Side.LEFT || pos == Side.RIGHT) && titleBar != null) {
                    if (Dockable.of(titleBar) != null) {
                        undock(Dockable.of(titleBar));
                    }
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

    public void dock(Dockable dockable, Side pos) {
        Object o = getValue(dockable);
        if (o == null || Dockable.of(o) == null) {
            return;
        }
        Dockable dragged = Dockable.of(o);

        if (contains(dragged.node())) {
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
    public void commitDock(Object obj) {
        if (DockRegistry.isDockable(obj)) {
            DockableContext dockableContext = Dockable.of(obj).getContext();
            if (dockableContext.getLayoutContext() == null || dockableContext.getLayoutContext() != this) {
                dockableContext.setLayoutContext(this);
            }
        }
    }

    public void dock(Dockable dockable, Side side, Dockable target) {
        Object o = getValue(dockable);
        if (o == null || Dockable.of(o) == null) {
            return;
        }
        Dockable dragged = Dockable.of(o);
        if (contains(dragged.node())) {
            return;
        }
        if (!(dragged instanceof Node) && !DockRegistry.getDockables().containsKey(dragged.node())) {
            DockRegistry.getDockables().put(dragged.node(), dragged);
        }

        Node node = dragged.node();

        DockEvent event;

        if (target == null) {
            dock(dragged, side);
            event = new DockEvent(DockEvent.NODE_DOCKED, dragged.node(), getLayoutNode(), side, null);
        } else {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            getDockExecutor().dock(node, side, target);
            event = new DockEvent(DockEvent.NODE_DOCKED, dragged.node(), getLayoutNode(), side, target);

        }
        DockableContext dockableContext = dragged.getContext();
        if (dockableContext.getLayoutContext() == null || dockableContext.getLayoutContext() != this) {
            dockableContext.setLayoutContext(this);
        }
    }

    @Override
    public void remove(Object obj) {
        if (!(obj instanceof Node)) {
            return;
        }
        Node dockNode = (Node) obj;
        DockSplitPane parent = getParentSplitPane(dockNode);
        if (parent != null) {
            LayoutContext ph = Dockable.of(dockNode).getContext().getLayoutContext();
            parent.getItems().remove(dockNode);
            Dockable.of(dockNode).getContext().setLayoutContext(ph);
        }
    }

    protected void clearEmptySplitPanes(DockSplitPane parent) {
        if (root == null || !parent.getItems().isEmpty()) {
            return;// null;
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
        }

        list.add(0, parent);
        int idx;// = -1;

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
            if (DockRegistry.isDockable(node)) {
                list.add(Dockable.of(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
    }
    
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
            } else if (newOrientation != oldOrientation) {
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

}//class DockPaneContext
