package org.vns.javafx.dock.api;

import java.util.Stack;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
//import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;

public class DockPaneController extends DockTargetController {

    //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
    private DockDelegate dockDelegate;
    private DockExecutor dockExecutor;
    
/*    private SideIndicatorTransformer.PaneIndicatorTransformer paneTransformer;
    private SideIndicatorTransformer.NodeIndicatorTransformer nodeTransformer;
*/    
    private SideIndicator.NodeSideIndicator nodeIndicator;

    public DockPaneController(Region dockPane) {
        super(dockPane);
        init();
    }

    private void init() {
        dockDelegate = new DockDelegate((DockSplitPane) getDockPane(), this);
    }
///////////////////////////
    /*protected void setPaneTransformer(SideIndicatorTransformer.PaneIndicatorTransformer paneTransformer) {
        this.paneTransformer = paneTransformer;
    }

    protected void setNodeTransformer(SideIndicatorTransformer.NodeIndicatorTransformer nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    public SideIndicatorTransformer.NodeIndicatorTransformer getNodeTransformer() {
        if (nodeTransformer == null) {
            nodeTransformer = createNodeIndicatorTransformer();
        }
        return nodeTransformer;
    }

    protected SideIndicatorTransformer.NodeIndicatorTransformer createNodeIndicatorTransformer() {
        return new SideIndicatorTransformer.NodeIndicatorTransformer();
    }

    public SideIndicatorTransformer.PaneIndicatorTransformer getPaneTransformer() {
        if (paneTransformer == null) {
            paneTransformer = createPaneIndicatorTransformer();
        }
        return paneTransformer;
    }

    protected SideIndicatorTransformer.PaneIndicatorTransformer createPaneIndicatorTransformer() {
        return new SideIndicatorTransformer.PaneIndicatorTransformer();
    }
*/
    ///
    public SideIndicator.NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }

    protected SideIndicator.NodeSideIndicator createNodeIndicator() {
        return new SideIndicator.NodeSideIndicator(this);
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
            retval = DockUtil.getParentSplitPane((DockSplitPane) getDockPane(), node) != null;
        }
        return retval;
    }

    @Override
    protected void dock(Point2D mousePos, Dockable dockable) {
        getDockExecutor().dock(mousePos, dockable);
    }

    @Override
    public void dock(Dockable dockable, Object pos) {
        getDockExecutor().dock(dockable, pos);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDockExecutor().dock(dockNode, side, target);
    }

    @Override
    public void remove(Node dockNode) {
        DockSplitPane dsp = getParentSplitPane((DockSplitPane) getDockPane(), dockNode);
        if (dsp != null) {
            DockTargetController ph = DockRegistry.dockable(dockNode).nodeController().getPaneController();
            dsp.getItems().remove(dockNode);
            DockRegistry.dockable(dockNode).nodeController().setPaneController(ph);
            clearEmptySplitPanes((DockSplitPane) getDockPane(), dsp);
        }
    }

    public static class DockDelegate {

        private DockSplitPane root;
        private DockPaneController paneController;

        public DockDelegate(DockSplitPane root, DockPaneController paneController) {
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
            DockSplitPane targetSplitPane = parentSplitPane;

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
                targetSplitPane = dp;
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

    public static class DockExecutor {
        
        private DockPaneController paneController;
        
        public DockExecutor(DockPaneController paneController) {
            this.paneController = paneController;
        }


        protected Dockable dock(Point2D mousePos, Node node, Side nodeDockPos, Side paneDockPos, Node target) {
            Dockable retval = null;
            if (paneDockPos != null) {
                dock(mousePos, DockRegistry.dockable(node), paneDockPos);
            } else if (nodeDockPos != null) {
                Dockable t = target == null ? null : DockRegistry.dockable(target);
                dock(mousePos, DockRegistry.dockable(node), nodeDockPos, t);
            }
            return retval;
        }

        protected void dock(Point2D mousePos, Dockable dockable) {
            IndicatorPopup popup = paneController.getDragPopup();
            Node node = dockable.node();
            if (!(popup instanceof DragPopup)) {
                return;
            }
            DragPopup dp = (DragPopup) popup;
            Dockable d = DockRegistry.dockable(node);
            if (d.nodeController().isFloating() && dp != null && (dp.getTargetNodeSidePos() != null || dp.getTargetPaneSidePos() != null) && dp.getDragTarget() != null) {
                Dockable retval = null;
                if (dp.getTargetPaneSidePos() != null) {
                    dock(mousePos, DockRegistry.dockable(node), dp.getTargetPaneSidePos());
                } else if (dp.getTargetNodeSidePos() != null) {
                    Dockable t = dp.getDragTarget() == null ? null : DockRegistry.dockable(dp.getDragTarget());
                    dock(mousePos, DockRegistry.dockable(node), dp.getTargetNodeSidePos(), t);
                }

                //dock(mousePos, node, dp.getTargetNodeSidePos(), dp.getTargetPaneSidePos(), dp.getDragTarget());
            }

        }
        public void dock(Dockable dockable, Object pos) {
            if (pos instanceof Side) {
                dock(null, dockable, (Side) pos);
            }
        }

        protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }

            if (doDock(mousePos, dockable.node(), dockPos)) {
                dockable.nodeController().setFloating(false);
            }
            return dockable;
        }

        public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
            return dock(null, dockable, dockPos, target);
        }

        protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos, Dockable target) {
            if (paneController.isDocked(dockable.node())) {
                return dockable;
            }
            if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
                DockRegistry.getDockables().put(dockable.node(), dockable);
            }
            dockable.nodeController().setFloating(false);

            doDock(mousePos, dockable.node(), dockPos, target);
            //09.02changeDockedState(dockable, true);
            return dockable;
        }

        protected boolean doDock(Point2D mousePos, Node node, Side dockPos) {
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
            paneController.getDockDelegate().dock(DockRegistry.dockable(node), dockPos);

            if (DockRegistry.isDockable(node)) {
                DockNodeController nodeController = DockRegistry.dockable(node).nodeController();
                if (nodeController.getPaneController() == null || nodeController.getPaneController() != paneController) {
                    nodeController.setPaneController(paneController);
                }
            }
            return true;
        }

        protected boolean doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
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
                    DockRegistry.dockable(node).nodeController().setFloating(false);
                }
                paneController.getDockDelegate().dock(node, dockPos, targetDockable);
            }
            if (DockRegistry.isDockable(node)) {
                DockNodeController nodeController = DockRegistry.dockable(node).nodeController();
                if (nodeController.getPaneController() == null || nodeController.getPaneController() != paneController) {
                    nodeController.setPaneController(paneController);
                }
            }
            return true;
        }

    }//DockExcecutor

}//class DockPaneController
