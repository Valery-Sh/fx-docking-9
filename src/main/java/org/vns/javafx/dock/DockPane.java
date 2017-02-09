package org.vns.javafx.dock;

import java.util.Stack;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane.ControlDockPane;
import static org.vns.javafx.dock.DockUtil.clearEmptySplitPanes;
import static org.vns.javafx.dock.DockUtil.getParentSplitPane;
import org.vns.javafx.dock.api.DockNodeController;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTargetController;

/**
 *
 * @author Valery
 */
public class DockPane extends DockSplitPane implements DockPaneTarget, EventHandler<ActionEvent> {


    private ControlDockPane delegate;
    
    public DockPane() {
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
            delegate = new ControlDockPane(new DockPaneController(this));
            //delegate = this;
            setRoot(this);

        }

        return delegate;
    }

    /*    @Override
    protected Skin<?> createDefaultSkin() {
        System.err.println("CREATE SKIN");
        //delegate = new ControlDockPane();
        return new DockPaneSplitSkin(this);
    }
     */
    @Override
    public DockSplitPane pane() {
        return this;
    }

    @Override
    public DockTargetController paneController() {
        return getDelegate().paneController();
    }

    public void dock(Dockable dockNode, Side side) {
        getDelegate().dock(dockNode, side);
    }

    public void dock(Dockable dockNode, Side side, Dockable target) {
        getDelegate().paneController().dock(dockNode, side, target);
    }

    protected void update(DockSplitPane dsp) {
        SplitPane sp = dsp;
        DockTargetController ph = getDelegate().paneController();
        for (Node node : dsp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeController().setPaneController(ph);
            } else if (node instanceof DockSplitPane) {
                //((SplitDelegate.DockSplitPaneTarget) node).setRoot(getRoot());
                update((DockSplitPane) node);
            }
        }
    }

    protected void update(DockSplitPane split, DockTargetController ph) {
        for (int i = 0; i < split.getItems().size(); i++) {
            Node node = split.getItems().get(i);
            if (DockRegistry.isDockable(node)) {
                Dockable d = DockRegistry.dockable(node);
                d.nodeController().setPaneController(ph);
/*                if (i < split.getDividers().size() && d.nodeController().getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(d.nodeController().getDividerPos());
                }
*/                
            } else if (node instanceof DockSplitPane) {
                ((DockSplitPane) node).setRoot(getRoot());
                DockSplitPane sp = (DockSplitPane) node;
/*                if (i < split.getDividers().size() && sp.getDividerPos() >= 0) {
                    split.getDividers().get(i).setPosition(sp.getDividerPos());
                }
*/                
                update(sp, ph);
            }
        }
    }

    public void update() {
        //update(((DockPaneController) getDelegate().paneController()).getRootSplitPane());
        System.err.println("getDelegate().paneController()=" + getDelegate());
        update(this);
        update(this, getDelegate().paneController());
    }

    protected void splitPaneAdded(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                DockRegistry.dockable(node).nodeController().setPaneController(dpt.paneController());
            } else if (node instanceof SplitPane) {
                splitPaneAdded(((SplitPane) node), dpt);
            }
        }
    }

    protected void splitPaneRemoved(SplitPane sp, DockPaneTarget dpt) {
        for (Node node : sp.getItems()) {
            if (DockRegistry.isDockable(node)) {
                //DockRegistry.dockable(node).nodeController().setPaneController(dpt.paneController());
            } else if (node instanceof SplitPane) {
                splitPaneRemoved(((SplitPane) node), dpt);
            }
        }

    }

    public boolean isUsedAsDockTarget() {
        return getDelegate().paneController().isUsedAsDockTarget();
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        getDelegate().paneController().setUsedAsDockTarget(usedAsDockTarget);
    }

    @Override
    public void handle(ActionEvent event) {
        update();
    }

    /*    @Override
    public Region pane() {
        return this;
    }

    @Override
    public DockTargetController paneController() {
        return delegate.paneController();
    }
     */
 /*    public static class DockPaneSplitSkin extends SkinBase<DockPane> {

        public DockPaneSplitSkin(DockPane control) {
            super(control);
            getChildren().add(control.getDelegate());
            //init(control);
        }

        private void init(DockPaneControl dp) {

            //dp.setSkin(this);
        }

    }
     */
    public class ControlDockPane implements DockPaneTarget {

        private DockTargetController paneController;

        public ControlDockPane(DockTargetController paneController) {
            this.paneController = paneController;
            init();

        }

        private void init() {
            //paneController = new DockPaneController(this);
        }

        @Override
        public DockSplitPane pane() {
            return (DockSplitPane) paneController.getDockPane();
        }

        @Override
        public DockTargetController paneController() {
            return paneController;
        }

        public Dockable dock(Dockable node, Side dockPos) {
            return paneController.dock(node, dockPos);
        }

    }

    public static class DockPaneController extends DockTargetController {

        //private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);
        private DockDelegete dockDelegate;

        public DockPaneController(Region dockPane) {
            super(dockPane);
            init();
        }

        @Override
        public DockSplitPane getDockPane() {
            return (DockSplitPane) super.getDockPane();
        }

        private void init() {
            //setRootSplitPane(new SplitDelegate.DockSplitPane());
            dockDelegate = new DockDelegete(getDockPane(), this);
        }

/*        @Override
        public void dividerPosChanged(Node node, double oldValue, double newValue) {
            if (DockRegistry.isDockable(node)) {
                //splitDelegate.update();
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
*/
        @Override
        protected boolean isDocked(Node node) {
            boolean retval = false;
            if (DockRegistry.isDockable(node)) {
                retval = DockUtil.getParentSplitPane(getDockPane(), node) != null;
            } 
            /*else {
                retval = null != notDockableItemsProperty().get(node);
            }
            */
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
            dockDelegate.dock(DockRegistry.dockable(node), dockPos);

            if (DockRegistry.isDockable(node)) {
                DockNodeController nodeController = DockRegistry.dockable(node).nodeController();
                if (nodeController.getPaneController() == null || nodeController.getPaneController() != this) {
                    nodeController.setPaneController(this);
                }
            }
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
            if (dockDelegate == null) {
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
                    DockRegistry.dockable(node).nodeController().setFloating(false);
                }
                dockDelegate.dock(node, dockPos, targetDockable);
            }
            if (DockRegistry.isDockable(node)) {
                DockNodeController state = DockRegistry.dockable(node).nodeController();
                if (state.getPaneController() == null || state.getPaneController() != this) {
                    state.setPaneController(this);
                }
                //09.02state.setDocked(true);
            }
        }

        @Override
        public void remove(Node dockNode) {
            DockSplitPane dsp = getParentSplitPane(getDockPane(), dockNode);
            if (dsp != null) {
                DockTargetController ph = DockRegistry.dockable(dockNode).nodeController().getPaneController();
                dsp.getItems().remove(dockNode);
                DockRegistry.dockable(dockNode).nodeController().setPaneController(ph);
                clearEmptySplitPanes(getDockPane(), dsp);
            }
        }

    }//class DockPaneController

    public static class DockDelegete {

        private DockSplitPane root;
        private DockPaneController paneController;

        public DockDelegete(DockSplitPane root, DockPaneController paneController) {
            this.root = root;
            this.paneController = paneController;
            //System.err.println("Constr SplitDelegate ");
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
}//class
