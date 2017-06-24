package org.vns.javafx.dock.api.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableController;
import org.vns.javafx.dock.api.DragPopup;
import org.vns.javafx.dock.api.IndicatorPopup;
import org.vns.javafx.dock.api.PositionIndicator;
import org.vns.javafx.dock.api.PreferencesBuilder;
import org.vns.javafx.dock.api.PreferencesItem;
import org.vns.javafx.dock.api.SideIndicator;

public class DockPaneControllerOld extends DockTargetController {

    //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    private DockDelegate dockDelegate;
    private DockExecutor dockExecutor;

    /*    private SideIndicatorTransformer.PaneIndicatorTransformer paneTransformer;
    private SideIndicatorTransformer.NodeIndicatorTransformer nodeTransformer;
     */
    private SideIndicator.NodeSideIndicator nodeIndicator;

    public DockPaneControllerOld(Region dockPane) {
        super(dockPane);
        init();
    }

    private void init() {
        dockDelegate = new DockDelegate((DockSplitPane) getTargetNode(), this);
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
        return new SideIndicator.PaneSideIndicator(this);
    }

    //////////////////////
    protected DockExecutor getDockExecutor() {
        if (dockExecutor == null) {
            dockExecutor = new DockExecutor(this);
        }
        return dockExecutor;
    }

    protected DockDelegate getDockDelegate() {
        return dockDelegate;
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            retval = DockUtil.getParentSplitPane((DockSplitPane) getTargetNode(), node) != null;
        }
        return retval;
    }

    @Override
    protected void dock(Point2D mousePos, Dockable dockable) {
        //07.05getDockExecutor().dock(mousePos, dockable);
        getDockExecutor().dock(dockable);
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
    public void dock(Dockable dockable, Object pos) {
        getDockExecutor().dock(dockable, pos);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDockExecutor().dock(dockNode, side, target);
    }

    /*    public void dock(int idx,Dockable dockable,DockSplitPane splitpane) {
        if ( dockable.dockableController().getTargetController() != null ) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }
        getDockExecutor().dock(idx, dockable, splitpane);
    }
     */
    @Override
    public void remove(Node dockNode) {
        DockSplitPane dsp = getParentSplitPane((DockSplitPane) getTargetNode(), dockNode);
        if (dsp != null) {
            DockTargetController ph = DockRegistry.dockable(dockNode).dockableController().getTargetController();
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).dockableController().setTargetController(ph);
            clearEmptySplitPanes((DockSplitPane) getTargetNode(), dsp);
        }
    }

    @Override
    public PreferencesBuilder getPreferencesBuilder() {
        return new DockPanePreferencesBuilder();
    }

    @Override
    public ObservableList<Dockable> getDockables() {
        ObservableList<Dockable> list = FXCollections.observableArrayList();
        getDockables((DockPaneOld) getTargetNode(), list);
        return list;
    }

    private void getDockables(DockSplitPane pane, List<Dockable> list) {
        pane.getItems().forEach(node -> {
            if (DockRegistry.isDockable(node)) {
                list.add(DockRegistry.dockable(node));
            } else if (node instanceof DockSplitPane) {
                getDockables((DockSplitPane) node, list);
            }
        });
    }

    public static class DockDelegate {

        private DockSplitPane root;
        private final DockPaneControllerOld paneController;

        public DockDelegate(DockSplitPane root, DockPaneControllerOld paneController) {
            this.root = root;
            this.paneController = paneController;
        }

        public DockSplitPane getRoot() {
            return root;
        }

        public void dock(Dockable dockable, Side dockPos) {
            dock(dockable.node(), dockPos);
        }

        public void dock(int idx, Dockable dockable, DockSplitPane splitPane) {
            splitPane.getItems().add(idx, dockable.node());
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
            for (int di = 0; di < parentSplitPane.getDividerPositions().length; di++) {
                parentSplitPane.setDividerPosition(di, parentSplitPane.getDividerPositions()[di] + 0.01);
                parentSplitPane.setDividerPosition(di, parentSplitPane.getDividerPositions()[di] - 0.01);
            }
//            for (int di = 0; di < parentSplitPane.getDividerPositions().length; di++) {
//                parentSplitPane.setDividerPosition(di, parentSplitPane.getDividerPositions()[di] - 0.01);
//            }

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

    public static class DockExecutor {

        private DockPaneControllerOld paneController;

        public DockExecutor(DockPaneControllerOld paneController) {
            this.paneController = paneController;
        }

        protected Dockable dock(Node node, Side nodeDockPos, Side paneDockPos, Node target) {
            Dockable retval = null;
            if (paneDockPos != null) {
                dock(DockRegistry.dockable(node), paneDockPos);
            } else if (nodeDockPos != null) {
                Dockable t = target == null ? null : DockRegistry.dockable(target);
                dock(DockRegistry.dockable(node), nodeDockPos, t);
            }
            return retval;
        }

        protected void dock(Dockable dockable) {
            IndicatorPopup popup = paneController.getIndicatorPopup();
            Node node = dockable.node();
            if (!(popup instanceof DragPopup)) {
                return;
            }
            DragPopup dp = (DragPopup) popup;
            Dockable d = DockRegistry.dockable(node);
            if (d.dockableController().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
                //Dockable retval = null;
                if (dp.getTargetPaneSidePos() != null) {
                    dock(DockRegistry.dockable(node), dp.getTargetPaneSidePos());
                } else if (dp.getTargetNodeSidePos() != null) {
                    Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                    dock(DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
                }
            }

        }

        public void dock(Dockable dockable, Object pos) {
            if (pos instanceof Side) {
                dock(dockable, (Side) pos);
            }
        }

        protected Dockable dock(Dockable dockable, Side dockPos) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }

            if (doDock(dockable.node(), dockPos)) {
                dockable.dockableController().setFloating(false);
            }
            return dockable;
        }

        protected Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }
            if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
                DockRegistry.getDockables().put(dockable.node(), dockable);
            }
            dockable.dockableController().setFloating(false);

            doDock(dockable.node(), dockPos, target);

            return dockable;
        }

        /*        protected Dockable dock(int idx, Dockable dockable, DockSplitPane splitPane) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }
            if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
                DockRegistry.getDockables().put(dockable.node(), dockable);
            }
            dockable.dockableController().setFloating(false);

            doDock(idx,dockable.node(), splitPane);

            return dockable;
        }
        
        protected boolean doDock(int idx, Node node, DockSplitPane splitPane) {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            paneController.getDockDelegate().dock(idx,DockRegistry.dockable(node), splitPane);

            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }

            return true;
        }
         */
        protected boolean doDock(Node node, Side dockPos) {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            paneController.getDockDelegate().dock(DockRegistry.dockable(node), dockPos);

            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }

            return true;
        }

        protected boolean doDock(Node node, Side dockPos, Dockable targetDockable) {
            if (paneController.getDockDelegate() == null) {
                return false;
            }
            if (paneController.isDocked(node)) {
                return false;
            }
            if (targetDockable == null) {
                dock(DockRegistry.dockable(node), dockPos);
            } else {
                if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                    ((Stage) node.getScene().getWindow()).close();
                }
                if (DockRegistry.isDockable(node)) {
                    DockRegistry.dockable(node).dockableController().setFloating(false);
                }
                paneController.getDockDelegate().dock(node, dockPos, targetDockable);
            }
            if (DockRegistry.isDockable(node)) {
                DockableController nodeController = DockRegistry.dockable(node).dockableController();
                if (nodeController.getTargetController() == null || nodeController.getTargetController() != paneController) {
                    nodeController.setTargetController(paneController);
                }
            }
            return true;
        }

    }//DockExcecutor

    public class DockPanePreferencesBuilder implements PreferencesBuilder {

        public static final String DIVIDER_POSITIONS = "dividerPositions";
        public static final String ORIENTATION = "orientation";

        @Override
        public TreeItem<PreferencesItem> build(DockTarget dockTarget) {
            TreeItem<PreferencesItem> retval = new TreeItem<>();
            DockPaneOld pane = (DockPaneOld) dockTarget;
            PreferencesItem it = new PreferencesItem(retval, pane);
            retval.setExpanded(true);
            retval.setValue(it);
            setProperties(it);
            for (int i = 0; i < pane.getItems().size(); i++) {
                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    TreeItem ti = new TreeItem();
                    it = new PreferencesItem(ti, pane.getItems().get(i));
                    ti.setValue(it);
                    retval.getChildren().add(ti);
                    //ti.setExpanded(true);
                    setProperties(it);
                    buildPane((SplitPane) pane.getItems().get(i), retval, ti);
                } else if (DockRegistry.isDockable(pane.getItems().get(i))) {
                    TreeItem ti = new TreeItem();
                    it = new PreferencesItem(ti, pane.getItems().get(i));
                    ti.setValue(it);
                    retval.getChildren().add(ti);
                    //ti.setExpanded(true);
                }
            }
            return retval;
        }

        protected void buildPane(SplitPane pane, TreeItem<PreferencesItem> root, TreeItem<PreferencesItem> parent) {
            for (int i = 0; i < pane.getItems().size(); i++) {
                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    TreeItem ti = new TreeItem();
                    PreferencesItem it = new PreferencesItem(ti, pane.getItems().get(i));
                    ti.setValue(it);
                    parent.getChildren().add(ti);
                    //ti.setExpanded(true);
                    setProperties(it);
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                } else if (DockRegistry.isDockable(pane.getItems().get(i))) {
                    TreeItem ti = new TreeItem();
                    PreferencesItem it = new PreferencesItem(ti, pane.getItems().get(i));
                    ti.setValue(it);
                    parent.getChildren().add(ti);
                    //ti.setExpanded(true);
                }
            }
        }

        private void setProperties(PreferencesItem it) {
            SplitPane sp = (SplitPane) it.getItemObject();
            String[] strDp = new String[sp.getDividerPositions().length];

            it.getProperties().put(ORIENTATION, sp.getOrientation().toString());
            for (int i = 0; i < sp.getDividerPositions().length; i++) {
                strDp[i] = String.valueOf(sp.getDividerPositions()[i]);
            }
            if (strDp.length > 0) {
                it.getProperties().put(DIVIDER_POSITIONS, String.join(",", strDp));
            }
        }

        @Override
        public Map<String, String> getProperties(Object node) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (node instanceof SplitPane) {
                SplitPane sp = (SplitPane) node;
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

        /*        @Override
        public void setProperties(Object node, Map<String, String> prefProps) {
            if (node instanceof SplitPane) {
                SplitPane sp = (SplitPane) node;
                if (prefProps.get(ORIENTATION) != null) {
                    if (prefProps.get(ORIENTATION).equals("VERTICAL")) {
                        sp.setOrientation(Orientation.VERTICAL);
                    } else {
                        sp.setOrientation(Orientation.HORIZONTAL);
                    }
                }
                if (prefProps.get(DIVIDER_POSITIONS) != null) {
                    String[] s = prefProps.get(DIVIDER_POSITIONS).split(",");
                    if (s.length > 0) {
                        double[] d = new double[s.length];
                        Arrays.setAll(d, (idx) -> {
                            return Double.parseDouble(s[idx]);
                        });
                        sp.setDividerPositions(d);
                    }
                }
            }

        }
         */
        @Override
        public void restoreFrom(TreeItem<PreferencesItem> targetRoot) {
            PreferencesItem pit = targetRoot.getValue();
            if (!(pit.getItemObject() instanceof DockPaneOld)) {
                return;
            }
            DockPaneOld pane = (DockPaneOld) pit.getItemObject();
            addDividersListener(pane, pane);
            pane.getItems().clear();

            String dp = pit.getProperties().get(DIVIDER_POSITIONS);
            if (dp != null && !dp.trim().isEmpty()) {
                String[] dps = dp.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            for (TreeItem<PreferencesItem> item : targetRoot.getChildren()) {
                //pane.getItems().add(buildFrom(item));
                Node node = buildFrom(pane, item);
                if ((node instanceof DockSplitPane) && !DockRegistry.isDockable(node)) {
                    pane.getItems().add(node);
                } else {
                    int idx = targetRoot.getChildren().indexOf(item);
                    System.err.println("1 BUILD FROM node = " + node);
                    if (DockRegistry.dockable(node).dockableController().getTargetController() != null) {
                        DockRegistry.dockable(node).dockableController().getTargetController().undock(node);
                    }
                    pane.getItems().add(idx, node);
                    //pane.dock(idx, DockRegistry.dockable(node), pane);
                }

//                int idx = targetRoot.getChildren().indexOf(item);
//                pane.dock(idx, DockRegistry.dockable(buildFrom(pane,item)), pane);
            }
        }

        protected Node buildFrom(DockPaneOld dockPane, TreeItem<PreferencesItem> targetRoot) {
            PreferencesItem pit = targetRoot.getValue();
            if (pit.getItemObject() instanceof Node) {
                return (Node) pit.getItemObject();
            }
            //
            // We consider that itemObject ia a string
            //
            String clazz = (String) pit.getItemObject();
            DockSplitPane pane = new DockSplitPane();
            addDividersListener((DockPaneOld) DockPaneControllerOld.this.getTargetNode(), pane);

            String p = pit.getProperties().get(ORIENTATION);
            if (p == null || "HORIZONTAL".equals(p)) {
                pane.setOrientation(Orientation.HORIZONTAL);
            } else {
                pane.setOrientation(Orientation.VERTICAL);
            }

            p = pit.getProperties().get(DIVIDER_POSITIONS);
            if (p != null && !p.trim().isEmpty()) {
                String[] dps = p.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            for (TreeItem<PreferencesItem> item : targetRoot.getChildren()) {
                Node node = buildFrom(dockPane, item);
                if ((node instanceof DockSplitPane) && !DockRegistry.isDockable(node)) {
                    pane.getItems().add(node);
                } else {
                    //
                    int idx = targetRoot.getChildren().indexOf(item);
                    System.err.println("2 BUILD FROM node = " + node);
                    if (DockRegistry.dockable(node).dockableController().getTargetController() != null) {
                        DockRegistry.dockable(node).dockableController().getTargetController().undock(node);
                    }
                    pane.getItems().add(idx, node);

//                    dockPane.dock(idx, DockRegistry.dockable(node), pane);
                }
            }
            return pane;
        }

        private void addDividersListener(DockPaneOld dockPane, SplitPane splitPane) {

            ListChangeListener<Node> itemsListener = (ListChangeListener.Change<? extends Node> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends Node> list = change.getRemoved();
                        for (Node node : list) {
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends Node> list = change.getAddedSubList();
                        for (Node node : list) {
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };

            ChangeListener<Number> posListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                getDockLoader().layoutChanged(dockPane);
            };

            ListChangeListener<SplitPane.Divider> divListListener = (ListChangeListener.Change<? extends SplitPane.Divider> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends SplitPane.Divider> list = change.getRemoved();
                        if (!list.isEmpty()) {
                        }
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().removeListener(posListener);
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends SplitPane.Divider> list = change.getAddedSubList();
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().addListener(posListener);
                            getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };
            splitPane.getDividers().addListener(divListListener);
            splitPane.getItems().addListener(itemsListener);
        }
    }
}//class DockPaneControllerOld
