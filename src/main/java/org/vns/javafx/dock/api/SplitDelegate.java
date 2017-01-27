package org.vns.javafx.dock.api;

import java.util.Stack;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class SplitDelegate {

    private DockSplitPane root;
    private PaneHandler paneHandler;
    private int targetIndex;

    public SplitDelegate(DockSplitPane root, PaneHandler paneHandler) {
        this.root = root;
        this.paneHandler = paneHandler;
    }

    public DockSplitPane getRoot() {
        return root;
    }

    public int getIndex() {
        return targetIndex;
    }

    public void dock(Dockable dockable, Side dockPos) {
        dock(dockable.node(), dockPos);
    }

    private void dock(Node node, Side dockPos) {
        targetIndex = -1;
        System.err.println("dock(Node node, Side dockPos)");
        DockSplitPane rootSplitPane = root;
        if (rootSplitPane == null) {
            rootSplitPane = new DockSplitPane();
            root = rootSplitPane;
            rootSplitPane.getItems().add(node);
            targetIndex = 0; // where inserted
            //
            // Next lines may be implemented by DockPane 
            //  getDockPane().getChildren().add(rootSplitPane);
            //  getDockableState().setDocked(true);
            return;
        }
        DockSplitPane parentSplitPane = root;
        DockSplitPane oldParentSplitPane = root;
        Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        Orientation oldOrientation = parentSplitPane.getOrientation();

        int itemCount = parentSplitPane.getItems().size();
        //
        // If itemcount == 1 then can just change orientation
        //
        if (newOrientation != oldOrientation && itemCount == 1) {
            parentSplitPane.setOrientation(newOrientation);
            oldOrientation = newOrientation;
        }

        if (newOrientation != oldOrientation) {
            DockSplitPane dp = parentSplitPane;
            if (itemCount > 1) {
                dp = new DockSplitPane(parentSplitPane);
            }
            parentSplitPane = dp;
            dp.setOrientation(newOrientation);

            targetIndex = 0;
            int idx = 0;
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                idx = parentSplitPane.getItems().size();
            }
            targetIndex = idx;
            parentSplitPane.getItems().add(idx, node);
        } else {
            int idx = 0;
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                idx = parentSplitPane.getItems().size();
            }
            targetIndex = idx;
            parentSplitPane.getItems().add(idx, node);
        }
        Dockable d = DockRegistry.dockable(node);
//        System.err.println("0. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
/*        if (d.nodeHandler().getDividerPos() >= 0) {
            System.err.println("0.1. SplitDelegate: divPos=" + d.nodeHandler().getDividerPos());
            rootSplitPane.setDividerPosition(targetIndex, d.nodeHandler().getDividerPos());
        }
*/        
        //parentSplitPane.getItems()
        //parentSplitPane.setDividerPosition(node, dockPos);
        if ( oldParentSplitPane == parentSplitPane ) {
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, parentSplitPane);
        } else {
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, oldParentSplitPane);
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, parentSplitPane);            
        }
        
        //parentSplitPane.setDividerPosition(node, dockPos);        
        root = parentSplitPane;
    }

    /*    public void dock(Node node, Side dockPos, DockTarget target) {
        if (target == null) {
            dock(node, dockPos);
        }

        Node targetNode = (Node) target;

        DockSplitPane parentSplitPane = getTargetSplitPane(targetNode);

        if (parentSplitPane == null) {
            return;
        }

        Orientation newOrientation = (dockPos == Side.LEFT || dockPos == Side.RIGHT)
                ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        Orientation oldOrientation = parentSplitPane.getOrientation();

        int itemCount = parentSplitPane.getItems().size();
        //
        // If itemcount == 1 then can just change orientation
        //
        if (newOrientation != oldOrientation && itemCount == 1) {
            parentSplitPane.setOrientation(newOrientation);
            oldOrientation = newOrientation;
        }
        if (newOrientation != oldOrientation) {

            DockSplitPane dp = new DockSplitPane();
            dp.setOrientation(newOrientation);

            int idx = parentSplitPane.getItems().indexOf((Node) targetNode);
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                //++idx;
            }
            parentSplitPane.getItems().remove((Node) targetNode);
            if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                dp.getItems().add(node);
                dp.getItems().add((Node) targetNode);
            } else {
                dp.getItems().add((Node) targetNode);
                dp.getItems().add(node);

            }

            //parentSplitPane.getItems().add(idx - 1, dp);
            parentSplitPane.getItems().add(idx, dp);
            //parentSplitPane.getItems().remove((Node) targetNode);
            //parentSplitPane = dp;
        } else {
            int idx = parentSplitPane.getItems().indexOf(targetNode);
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                ++idx;
            }
            parentSplitPane.getItems().add(idx, node);

        }
        parentSplitPane.setDividerPosition(node, dockPos);
        //if (parentSplitPane != root) {
            //root = parentSplitPane;
        //}
    }
     */
    public void dock(Node node, Side dockPos, Dockable target) {
        System.err.println("@@@@@@@@@@@@ dock " + node);
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

        int itemCount = parentSplitPane.getItems().size();
        //
        // If itemcount == 1 then can just change orientation
        //
        if (newOrientation != oldOrientation && itemCount == 1) {
            parentSplitPane.setOrientation(newOrientation);
            oldOrientation = newOrientation;
        }
        if (newOrientation != oldOrientation) {

            DockSplitPane dp = new DockSplitPane();
            dp.setOrientation(newOrientation);

            int idx = parentSplitPane.getItems().indexOf((Node) targetNode);
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                //++idx;
            }
            parentSplitPane.getItems().remove((Node) targetNode);
            if (dockPos == Side.TOP || dockPos == Side.LEFT) {
                dp.getItems().add(node);
                dp.getItems().add((Node) targetNode);
            } else {
                dp.getItems().add((Node) targetNode);
                dp.getItems().add(node);
            }

            //parentSplitPane.getItems().add(idx - 1, dp);
            parentSplitPane.getItems().add(idx, dp);
            targetSplitPane = dp;
            //parentSplitPane.getItems().remove((Node) targetNode);
            //parentSplitPane = dp;
        } else {
            int idx = parentSplitPane.getItems().indexOf(targetNode);
            if (dockPos == Side.RIGHT || dockPos == Side.BOTTOM) {
                ++idx;
            }
            parentSplitPane.getItems().add(idx, node);

        }
        if ( targetSplitPane == parentSplitPane ) {
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, targetSplitPane);            
        } else {
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, targetSplitPane);            
            paneHandler.updateDividers(d.nodeHandler().getDividerPos(), targetIndex, d, parentSplitPane);            
        }
        
    }

    /**
     * An immediate parent object of type DockSplitPane
     *
     * @param target
     * @return
     */
/*    protected DockSplitPane getTargetSplitPane_new(Node target) {
        if (target == null) {
            return null;
        }
        DockSplitPane split = root;

        DockSplitPane retval = null;
        Parent p = target.getParent();
        while (true) {
            if (p == null) {
                break;
            }
            if (p instanceof DockSplitPane) {
                retval = (DockSplitPane) p;
                break;
            }
            p = p.getParent();
        }
        System.err.println("RETVAL = " + retval);
        return retval;
    }
*/
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

    public static class DockSplitPane extends SplitPane {

        public DockSplitPane() {
        }

        public DockSplitPane(Node... items) {
            super(items);
        }
        public static DockSplitPane create(Orientation o) {
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
