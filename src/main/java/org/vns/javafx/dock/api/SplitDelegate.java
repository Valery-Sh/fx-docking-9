package org.vns.javafx.dock.api;

import java.util.List;
import java.util.Stack;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;

/**
 *
 * @author Valery Shyshkin
 */
public class SplitDelegate {

    private DockSplitPane root;
    private PaneHandler paneHandler;
//    private int targetIndex;

    public SplitDelegate(DockSplitPane root, PaneHandler paneHandler) {
        this.root = root;
        this.paneHandler = paneHandler;
        //System.err.println("Constr SplitDelegate ");
    }

    public DockSplitPane getRoot() {
        return root;
    }

    /*    public int getIndex() {
        return targetIndex;
    }
     */
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
        //DockSplitPane parentSplitPane = root;
        //DockSplitPane oldParentSplitPane = root;
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

    public static class DockSplitPane extends SplitPane implements ListChangeListener {

        private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);

        public DockSplitPane() {
            init();
        }

        public DockSplitPane(Node... items) {
            super(items);
            init();
        }

        private void init() {
            System.err.println(" INIT 1111111111111 id=" + this.getId());
            DockPaneTarget dpt = DockUtil.getParentDockPane(this);
            if (dpt != null && getItems().size() > 0) {
                getItems().forEach(it -> {
                    if (DockRegistry.isDockable(it)) {
                        DockRegistry.dockable(it).nodeHandler().setPaneHandler(dpt.paneHandler());
                    }
                });
            }
            updateDividers(this);
            getItems().addListener(this);
            dividerPosProperty.addListener(this::dividerPosChanged);
        }

        protected void dividerPosChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            DockSplitPane dsp = getParentSplitPane(this);
            if ( dsp != null ) {
                updateDividers(dsp);
            }
        }

        @Override
        public void onChanged(Change change) {
            itemsChanged(change);
        }

        protected void itemsChanged(ListChangeListener.Change<? extends Node> change) {
            DockPaneTarget dpt = null;
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Node> list = change.getRemoved();
                    if (!list.isEmpty() && dpt == null) {
                        dpt = DockUtil.getParentDockPane(list.get(0));
                    }
                    for (Node node : list) {
                        if (dpt != null && DockRegistry.isDockable(node)) {
                        } else if (dpt != null && node instanceof DockSplitPane) {
                            splitPaneRemoved((SplitPane) node, dpt);
                        }
                    }

                }
                if (change.wasAdded()) {
                    System.err.println("=================== wasAdded:  ");
                    List<? extends Node> list = change.getAddedSubList();
                    if (!list.isEmpty() && dpt == null) {
                        dpt = DockUtil.getParentDockPane(list.get(0));
                    }
                    for (Node node : list) {
                        System.err.println("   --- added:  node=" + node.getId());
                        System.err.println("   --- added:  split=" + this.getId());

                        if (dpt != null && DockRegistry.isDockable(node)) {
                            DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
                        } else if (dpt != null && node instanceof DockSplitPane) {
                            splitPaneAdded((SplitPane) node, dpt);
                        }
                    }
                }
            }//while
            //02.02updateDividers(this);
        }

        /**
         * Does nothing. Subclasses can change behavior.
         *
         * @param split
         */
        public void updateDividers(DockSplitPane split) {
//            if ( true ) {
//                return;
//            }
            System.err.println("***** updateDividers split items.size=" + split.getItems().size() );
            for (int i = 0; i < split.getItems().size(); i++) {
                Node node = split.getItems().get(i);
                System.err.println("   --- update id=" + node.getId());
                if (DockRegistry.isDockable(node)) {
                    System.err.println("      --- is node id=" + node.getId());
                    Dockable d = DockRegistry.dockable(node);
                    if (i < split.getDividers().size() && d.nodeHandler().getDividerPos() >= 0) {
                        System.err.println("   --- do update node.id=" + node.getId());
                        split.getDividers().get(i).setPosition(d.nodeHandler().getDividerPos());
                    }
                }
                if (node instanceof DockSplitPane) {
                    System.err.println("      --- is splitPane id=" + node.getId());
                    DockSplitPane dsp = (DockSplitPane) node;
                    if (i < split.getDividers().size() && dsp.getDividerPos() >= 0) {
                        System.err.println("      --- do update split.id=" + node.getId());
                        split.getDividers().get(i).setPosition(dsp.getDividerPos());
                    }
                }
                System.err.println("-----------------------------------------");
            }
        }

        protected void splitPaneAdded(SplitPane sp, DockPaneTarget dpt) {
            for (Node node : sp.getItems()) {
                if (DockRegistry.isDockable(node)) {
                    DockRegistry.dockable(node).nodeHandler().setPaneHandler(dpt.paneHandler());
                } else if (node instanceof SplitPane) {
                    splitPaneAdded(((SplitPane) node), dpt);
                }
            }
        }

        protected void splitPaneRemoved(SplitPane sp, DockPaneTarget dpt) {
            for (Node node : sp.getItems()) {
                if (DockRegistry.isDockable(node)) {
                } else if (node instanceof SplitPane) {
                    splitPaneRemoved(((SplitPane) node), dpt);
                }
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

        public static DockSplitPane getParentSplitPane(Node dockNode) {
            return (DockSplitPane) DockUtil.getImmediateParent(dockNode, p -> {
                return (p instanceof DockSplitPane);
            });
        }

        /*        public static DockSplitPane create(Orientation o) {
            if ( o == Orientation.HORIZONTAL) {
                return new HSplit();
            } else {
                return new VSplit();
            }
        }
        public static DockSplitPane create(Orientation o, Node... items) {
            if ( o == Orientation.HORIZONTAL) {
                return new HSplit(items);
            } else {
                return new VSplit(items);
            }
        }
         */
        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }

        public void setDividerPosition(Node node, Side dockPos) {
            setDividerPosition(node, this, dockPos);
        }

        public static void setDividerPosition(Node node, DockSplitPane split, Side dockPos) {
            if (split.getItems().size() <= 1) {
                return;
            }

            int idx = split.getItems().indexOf(node);
            Dockable d = DockRegistry.dockable(node);
            //System.err.println("1. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
/*            if (d.nodeHandler().getDividerPos() >= 0) {
                System.err.println("2. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
                split.setDividerPosition(idx, d.nodeHandler().getDividerPos());
                return;
            }
             */
            //d.nodeHandler().updateDividers(idx, split);
            double sizeSum = 0;
            for (int i = 0; i < split.getItems().size(); i++) {
                if (i == idx) {
                    continue;
                }
                if (split.getOrientation() == Orientation.HORIZONTAL) {
                    sizeSum += split.getItems().get(i).prefWidth(0);
                } else {
                    sizeSum += split.getItems().get(i).prefHeight(0);
                }
            }
            if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                if (split.getOrientation() == Orientation.HORIZONTAL) {
                    split.setDividerPosition(idx,
                            node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
                } else {
                    split.setDividerPosition(idx,
                            node.prefHeight(0) / (sizeSum + node.prefHeight(0)));

                }
            } else {
                if (split.getOrientation() == Orientation.HORIZONTAL) {
                    split.setDividerPosition(idx,
                            1 - node.prefWidth(0) / (sizeSum + node.prefWidth(0)));
                } else {
                    split.setDividerPosition(idx,
                            1 - node.prefHeight(0) / (sizeSum + node.prefHeight(0)));
                }
            }
        }
    }
}
