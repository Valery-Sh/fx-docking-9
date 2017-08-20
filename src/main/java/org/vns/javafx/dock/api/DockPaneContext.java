package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import org.vns.javafx.dock.api.save.AbstractDockTreeItemBuilder;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.SideIndicator;
import org.vns.javafx.dock.api.indicator.DragPopup;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.indicator.SideIndicator.PaneSideIndicator;

public class DockPaneContext extends TargetContext {

    //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    //+private DockDelegate dockDelegate;
    private DockExecutor dockExecutor;
    private final DockSplitPane root;
    private DockTreeItemBuilder dockTreeItemBuilder;
    /*    private SideIndicatorTransformer.PaneIndicatorTransformer paneTransformer;
    private SideIndicatorTransformer.NodeIndicatorTransformer nodeTransformer;
     */
    private SideIndicator.NodeSideIndicator nodeIndicator;

    public DockPaneContext(Region dockPane, DockSplitPane root) {
        super(dockPane);
        this.root = root;
        init();
    }

    private void init() {
        //dockDelegate = new DockDelegate((DockSplitPane) getTargetNode(), this);
    }

    protected DockSplitPane getRoot() {
        return root;
    }

    @Override
    public SideIndicator.NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }

    @Override
    protected IndicatorPopup createIndicatorPopup() {
        return new DragPopup(this);
    }

    protected SideIndicator.NodeSideIndicator createNodeIndicator() {
        return new SideIndicator.NodeSideIndicator(this);
    }

    @Override
    protected PositionIndicator createPositionIndicator() {
        return new PaneSideIndicator(this);
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
        if (DockRegistry.instanceOfDockable(node)) {
            //24.06retval = DockUtil.getParentSplitPane((DockSplitPane) getTargetNode(), node) != null;
            retval = DockUtil.getParentSplitPane(root, node) != null;
        }
        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        //getDockExecutor().dock(dockable);
        IndicatorPopup popup = getIndicatorPopup();
        Node node = dockable.node();
        if (!(popup instanceof DragPopup)) {
            return;
        }
        DragPopup dp = (DragPopup) popup;
        Dockable d = DockRegistry.dockable(node);
        if (d.getDockableContext().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
            //Dockable retval = null;
            if (dp.getTargetPaneSidePos() != null) {
                //13.07getDockExecutor().dock(DockRegistry.dockable(node), dp.getTargetPaneSidePos());
                dock(DockRegistry.dockable(node), dp.getTargetPaneSidePos());
            } else if (dp.getTargetNodeSidePos() != null) {
                Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                //13.07getDockExecutor().dock(DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
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
            System.err.println("DOCK 1");
            return;
        }

        if (doDock(dockable.node(), pos)) {
            System.err.println("DOCK 2");

            dockable.getDockableContext().setFloating(false);
        }
        //return dockable;

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
    protected void commitDock(Node node)  {
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

        if (target == null) {
            dock(dockable, side);
        } else {

            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
//            if (DockRegistry.instanceOfDockable(node)) {
            dockable.getDockableContext().setFloating(false);
//            }
            getDockExecutor().dock(node, side, target);
        }
//        if (DockRegistry.instanceOfDockable(node)) {
        DockableContext nodeController = dockable.getDockableContext();
        if (nodeController.getTargetContext() == null || nodeController.getTargetContext() != this) {
            nodeController.setTargetContext(this);
        }
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
        }
//        System.err.println("AFTER remove:");
//        System.err.println("===================================");
//        DockUtil.print(root);
    }

    @Override
    public DockTreeItemBuilder getDockTreeTemBuilder() {
        if (dockTreeItemBuilder == null) {
            dockTreeItemBuilder = new DockPanePreferencesBuilder(DockRegistry.dockTarget(getTargetNode()));
        }
        return dockTreeItemBuilder;
    }

    @Override
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
        System.err.println("    *** DockPaneController idx = " + idx);
        System.err.println("    *** DockPaneController (root == dsp) = " + (root == dsp));

        return new Object[]{dsp, idx};
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        Object[] obj = (Object[]) restoreposition;;
        DockSplitPane dsp = (DockSplitPane) obj[0];
        int idx = (Integer) obj[1];
//        System.err.println("BEFORE RESTORE: " + root.getDividers().size());
//        DockUtil.print(root);
//        System.err.println("================================================" );

//        DockUtil.print(root);
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
                System.err.println("!!!!! dock(Node node, Side dockPos)");
//19.08                clear();
                return;
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

    public class DockPanePreferencesBuilder extends AbstractDockTreeItemBuilder {

        public static final String DIVIDER_POSITIONS = "dividerPositions";
        public static final String ORIENTATION = "orientation";

        public DockPanePreferencesBuilder(DockTarget dockTarget) {
            super(dockTarget);
        }

        @Override
        public TreeItem<Properties> build(String fieldName) {

            //Node node = getTargetContext().getTargetNode();
            Node node = getDockTarget().target();
            TreeItem<Properties> retval = DockTreeItemBuilder.build(fieldName, node);
            setObjectProperties(retval.getValue());
            buildChildren(retval);
            return retval;

        }

        protected void buildChildren(TreeItem<Properties> root) {
            //DockPane pane = (DockPane) getTargetContext().getTargetNode();
            DockPane pane = (DockPane) getDockTarget().target();
            for (int i = 0; i < pane.getItems().size(); i++) {
                TreeItem ti = DockTreeItemBuilder.build(pane.getItems().get(i));

                root.getChildren().add(ti);
                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    setObjectProperties((Properties) ti.getValue());
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                } else if (DockRegistry.instanceOfDockTarget(pane.getItems().get(i))) {
                    TreeItem it = DockRegistry.dockTarget(pane.getItems().get(i))
                            .getTargetContext()
                            .getDockTreeTemBuilder().build();
                    ti.getChildren().addAll(it.getChildren());
                }
            }
        }

        protected void buildPane(SplitPane pane, TreeItem<Properties> root, TreeItem<Properties> parent) {
            Properties props;
            for (int i = 0; i < pane.getItems().size(); i++) {
                System.err.println("1) buildPane i = " + pane.getItems().get(i));
                TreeItem ti;
                if (DockRegistry.instanceOfDockTarget(pane.getItems().get(i))) {
                    System.err.println("2) buildPane = " + pane.getItems().get(i));
                    ti = DockRegistry.dockTarget(pane.getItems().get(i))
                            .getTargetContext()
                            .getDockTreeTemBuilder().build();
                    System.err.println("1) ti = " + ti.getChildren().size());
                } else {
                    System.err.println("3) buildPane i = " + pane.getItems().get(i));
                    ti = DockTreeItemBuilder.build(pane.getItems().get(i));
                    System.err.println("2) ti = " + ti.getChildren().size());
                }

                props = (Properties) ti.getValue();
                parent.getChildren().add(ti);

                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    setObjectProperties(props);
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                }
            }
        }

        protected void setObjectProperties(Properties props) {
            SplitPane sp;
            if (props.get(OBJECT_ATTR) instanceof DockPane) {
                sp = getRoot();
            } else {
                sp = (SplitPane) props.get(OBJECT_ATTR);
            }
            String[] strDp = new String[sp.getDividerPositions().length];

            props.put(ORIENTATION, sp.getOrientation().toString());
            for (int i = 0; i < sp.getDividerPositions().length; i++) {
                strDp[i] = String.valueOf(sp.getDividerPositions()[i]);
            }
            if (strDp.length > 0) {
                props.put(DIVIDER_POSITIONS, String.join(",", strDp));
            }
        }

        public Map<String, String> getProperties(Object obj) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (obj instanceof SplitPane) {
                SplitPane sp = (SplitPane) obj;
                props.put(ORIENTATION, sp.getOrientation().toString());
                if (sp.getDividerPositions().length != 0) {
                    String[] s = new String[sp.getDividerPositions().length];
                    Arrays.setAll(s, (idx) -> {
                        return String.valueOf(sp.getDividerPositions()[idx]);
                    });
                    String dp = String.join(",", s);
                    props.put(DIVIDER_POSITIONS, dp);
                }
            }
            return props;
        }

        private void addDividersListener(SplitPane splitPane) {

            ListChangeListener<Node> itemsListener = (ListChangeListener.Change<? extends Node> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends Node> list = change.getRemoved();
                        for (Node node : list) {
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        //System.err.println("2 addDividersListener added");
                        List<? extends Node> list = change.getAddedSubList();
                        for (Node node : list) {
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };

            ChangeListener<Number> posListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                //getDockLoader().layoutChanged(dockPane);
            };

            ListChangeListener<SplitPane.Divider> divListListener;
            divListListener = (ListChangeListener.Change<? extends SplitPane.Divider> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends SplitPane.Divider> list = change.getRemoved();
                        if (!list.isEmpty()) {
                        }
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().removeListener(posListener);
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends SplitPane.Divider> list = change.getAddedSubList();
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().addListener(posListener);
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };
            splitPane.getDividers().addListener(divListListener);
            splitPane.getItems().addListener(itemsListener);
        }

        @Override
        public Node restore(TreeItem<Properties> targetRoot) {
            Properties props = targetRoot.getValue();
            if (!(props.get(OBJECT_ATTR) instanceof DockPane)) {
                return null;
            }
            DockPane pane = (DockPane) props.get(OBJECT_ATTR);
            targetRoot.setExpanded(true);
            //addDividersListener(pane, getRoot());
            addDividersListener(getRoot());

            pane.getItems().clear();

            String dp = props.getProperty(DIVIDER_POSITIONS);
            if (dp != null && !dp.trim().isEmpty()) {
                String[] dps = dp.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                getRoot().setDividerPositions(dpd);
            }
            for (TreeItem<Properties> item : targetRoot.getChildren()) {
                Node node = (Node) item.getValue().get(OBJECT_ATTR);
                if (node == null || (node instanceof DockSplitPane) && !DockRegistry.instanceOfDockable(node)) {
                    node = buildSplitPane(item);
                    System.err.println("++++++ node=" + node);
                    pane.getItems().add(node);
                } else if (DockRegistry.instanceOfDockTarget(node)) {
                    node = DockRegistry.dockTarget(node)
                            .getTargetContext()
                            .getDockTreeTemBuilder()
                            .restore(item);
                    System.err.println("1) ++++++ node=" + node);

                    pane.getItems().add(node);

                } else if (DockRegistry.instanceOfDockable(node)) {
                    if (DockRegistry.dockable(node).getDockableContext().getTargetContext() != null) {
                        TargetContext c = DockRegistry.dockable(node).getDockableContext().getTargetContext();
                        if (c != getDockTarget().getTargetContext()) {
                            c.undock(node);
                        }
                    }
                    pane.getItems().add(node);
                } else {
                    System.err.println("4) --- node = " + node);
                    pane.getItems().add(node);
                }
            }
            return pane;
        }

        protected Node buildSplitPane(TreeItem<Properties> splitPaneItem) {
            DockSplitPane pane = (DockSplitPane) splitPaneItem.getValue().get(OBJECT_ATTR);
            if (pane == null) {
                pane = buildSplitPaneInstance(splitPaneItem);
                splitPaneItem.getValue().put(OBJECT_ATTR, pane);
            }
            pane.getItems().clear();
            for (TreeItem<Properties> item : splitPaneItem.getChildren()) {
                Node node = (Node) item.getValue().get(OBJECT_ATTR);
                if (node == null) {
                    node = buildSplitPane(item);
                    item.getValue().put(OBJECT_ATTR, node);
                    pane.getItems().add(node);
                } else if ((node instanceof DockSplitPane) && !DockRegistry.instanceOfDockable(node)) {
                    node = buildSplitPane(item);
                    pane.getItems().add(node);
                } else if (DockRegistry.instanceOfDockTarget(node)) {
                    node = DockRegistry.dockTarget(node)
                            .getTargetContext()
                            .getDockTreeTemBuilder()
                            .restore(item);
                    System.err.println("1) buildSplitPane ++++++ node=" + node);

//                    node = restore(item);
                    pane.getItems().add(node);
                } else {
                    pane.getItems().add(node);
                }
            }
            return pane;
        }

        protected DockSplitPane buildSplitPaneInstance(TreeItem<Properties> sourceItem) {
            Properties props = sourceItem.getValue();
            sourceItem.setExpanded(true);
            DockSplitPane pane = null;
            String className = sourceItem.getValue().getProperty(CLASS_NAME_ATTR);
            if (VPane.class.getName().equals(className)) {
                pane = new VPane();
            } else if (HPane.class.getName().equals(className)) {
                pane = new HPane();
            } else if (DockSplitPane.class.getName().equals(className)) {
                pane = new DockSplitPane();
            }
            System.err.println("!!!!!!!! BUIKD SplitPane " + pane);
            if (pane == null) {
                return null; // ????
            }

            pane.setId(sourceItem.getValue().getProperty("id"));

            //addDividersListener((DockPane) DockPaneContext.this.getTargetNode(), pane);
            addDividersListener(pane);

            String p = props.getProperty(ORIENTATION);
            if (p == null || "HORIZONTAL".equals(p)) {
                pane.setOrientation(Orientation.HORIZONTAL);
            } else {
                pane.setOrientation(Orientation.VERTICAL);
            }

            p = props.getProperty(DIVIDER_POSITIONS);
            if (p != null && !p.trim().isEmpty()) {
                String[] dps = p.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            return pane;
        }

    }
}//class DockPaneContext
