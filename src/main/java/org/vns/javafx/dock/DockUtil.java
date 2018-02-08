package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.TopNodeHelper;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockUtil {
    public static Window getOwnerWindow(Node node) {
        Window retval = null;
        if ( node != null && node.getScene() != null && node.getScene().getWindow() != null ) {
            retval = node.getScene().getWindow();
        }
        return retval;
    }
    public static double widthOf(Node node) {
        double w = 0;
        if ((node instanceof Region)) {
            w = ((Region) node).getWidth();
        } else {
            w = node.getLayoutBounds().getWidth();
        }
        return w;
    }
    public static double heightOf(Node node) {
        double h = 0;
        if ((node instanceof Region)) {
            h = ((Region) node).getHeight();
        } else {
            h = node.getLayoutBounds().getHeight();
        }
        return h;
    }

    public static DockSplitPane getParentSplitPane(DockSplitPane root, Node childNode) {
        DockSplitPane retval = null;
        DockSplitPane split = root;
        Stack<DockSplitPane> stack = new Stack<>();
        stack.push(split);

        while (!stack.empty()) {
            split = stack.pop();
            if (split.getItems().contains(childNode)) {
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

    public static void clearEmptySplitPanes(DockSplitPane root, DockSplitPane empty) {
        if (root == null || !empty.getItems().isEmpty()) {
            return;
        }
        List<DockSplitPane> list = new ArrayList<>();

        DockSplitPane dsp = empty;
        while (true) {
            dsp = getParentSplitPane(root, dsp);
            if (dsp == null) {
                break;
            }
            list.add(dsp);
        }
        list.add(0, empty);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getItems().isEmpty()) {
                break;
            }
            if (i < list.size() - 1) {
                list.get(i + 1).getItems().remove(list.get(i));
            }
        }
    }

    public static Side sideValue(String dockPos) {
        Side retval = null;
        if (dockPos == null) {
            retval = Side.BOTTOM;
        } else {
            switch (dockPos) {
                case "TOP":
                    retval = Side.TOP;
                    break;
                case "BOTTOM":
                    retval = Side.BOTTOM;
                    break;
                case "LEFT":
                    retval = Side.LEFT;
                    break;
                case "RIGHT":
                    retval = Side.RIGHT;
                    break;
            }
        }
        return retval;
    }

    /*    public static ObservableList<Dockable> getAllDockable(Region root) {
        ObservableList<Dockable> retval = FXCollections.observableArrayList();

        List<Dockable> list = findNodes(root, p -> {
            return (DockRegistry.instanceOfDockable(p));
        });
        retval.addAll(list.toArray(new Dockable[0]));
        list.forEach(d -> {
            //((DockTarget)root).dock(root, d.getDockState().getDockPos());
        });
        return retval;
    }
     */

 /*11.01    public static List findNodes(Parent root, Predicate<Node> predicate) {
        List retval = new ArrayList();
        for (Node node : root.getChildrenUnmodifiable()) {
            if (predicate.test(node)) {
                retval.add(node);
            }
            if (node instanceof Parent) {
                retval.addAll(findNodes((Parent) node, predicate));
            }
        }
        return retval;
    }
     */
 /*11.01    public static Node findNode(Parent root, Predicate<Node> predicate) {
        List<Node> ls = findNodes(root, predicate);
        if (ls.isEmpty()) {
            return null;
        }
        return ls.get(0);
    }
     */
    public static Node findDockable(Node root, double screenX, double screenY) {

        Predicate<Node> predicate = (node) -> {
            Point2D p = node.localToScreen(0, 0);
            boolean b = false;
            //!!!08
            if (DockRegistry.isDockable(node)) {
                b = true;
                TargetContext pd = Dockable.of(node).getDockableContext().getTargetContext();
                DockableContext st = Dockable.of(node).getDockableContext();
                if (pd == null) {
                    b = false;
                } else {
                    b = pd.isUsedAsDockTarget() && st.isUsedAsDockTarget();
                }

            }
            return b;
        };
        return TopNodeHelper.getTopNode(root.getScene().getWindow(), screenX, screenY, predicate);
    }


    public static Node findNode(Parent root, Node toSearch) {
        if (toSearch == null) {
            return null;
        }
        Node retval = null;
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node == toSearch) {
                retval = node;
            } else if (node instanceof Parent) {
                retval = findNode((Parent) node, toSearch);
            }
            if (retval != null) {
                break;
            }
        }
        return retval;

    }

    public static void print(Parent root) {
        print(root, 1, " ", p -> {
            return ((p instanceof Control) || (p instanceof Pane))
                    && !(p.getClass().getName().startsWith("com.sun.javafx"));
        });
    }

    public static boolean contains(Node node, double x, double y) {
        return node.localToScreen(node.getBoundsInLocal()).contains(x, y);
    }    
    public static Node findNode(Pane pane, double x, double y) {
        Node retval = null;
        for ( Node node : pane.getChildren() ) {
            if ( contains(node, x, y) ) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    public static boolean contains(Window w, double x, double y) {
        return ((x >= w.getX() && x <= w.getX() + w.getWidth() 
                && y >= w.getY() && y <= w.getY() + w.getHeight()));
    }

    public static Node findNode(List<Node> list, double x, double y) {
        Node retval = null;
        for (Node node : list) {
            if (!(node instanceof Region)) {
                continue;
            }
            if (contains((Region) node, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    public static void print(Parent root, int level, String indent, Predicate<Node> predicate) {
        StringBuilder sb = new StringBuilder();
        print(sb, root, level, indent, predicate);
        System.out.println("=======================================");
        System.out.println(sb);
        System.out.println("=======================================");

    }

    public static void print(StringBuilder sb, Node node, int level, String indent, Predicate<Node> predicate) {
        String id = node.getId() == null ? " " : node.getId() + " ";
        String ln = level + "." + id;
        String ind = new String(new char[level]).replace("\0", indent);
        if (predicate.test(node)) {
            sb.append(ind)
                    .append(ln)
                    .append(" : ")
                    .append(node.getClass().getName())
                    .append(System.lineSeparator());
        }
        if (node instanceof Parent) {
            List<Node> list = ((Parent) node).getChildrenUnmodifiable();
            for (Node n : list) {
                int newLevel = level;
                if (predicate.test(n)) {
                    newLevel++;
                }
                print(sb, n, newLevel, indent, predicate);
            }
        }
    }

    public static List<Parent> getParentChain(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> retval = new ArrayList<>();
        Node p = child;
        while (true) {
            Parent p1 = getImmediateParent(root, p);
            if (p1 != null) {
                p = p1;
                if (predicate.test(p1)) {
                    retval.add(0, p1);
                }
            } else {
                break;
            }
        }
        return retval;
    }

    public static Parent getImmediateParent(Parent root, Node child) {
        Parent retval = null;
        List<Node> list = root.getChildrenUnmodifiable();
        for (int i = 0; i < list.size(); i++) {
            Node r = list.get(i);
            if (r == child) {
                retval = root;
                break;
            }
            if (r instanceof Parent) {
                retval = getImmediateParent((Parent) r, child);
                if (retval != null) {
                    break;
                }
            }
        }
        return retval;
    }

    public static Parent getImmediateParent(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> chain = getParentChain(root, child, predicate);
        Parent retval = null;
        if (!chain.isEmpty() && root != chain.get(chain.size() - 1)) {
            retval = chain.get(chain.size() - 1);
        }
        return retval;
    }

    public static Parent getImmediateParent(Node child, Predicate<Parent> predicate) {
        //if (child == null || child.getScene() == null || child.getScene().getRoot() == null) {
        if (child == null) {
            return null;
        }
        Parent retval = null;
        Parent p = child.getParent();
        while (true) {
            if (p == null) {
                break;
            }
            if (predicate.test(p)) {
                retval = p;
                break;
            }
            p = p.getParent();
        }

        return retval;
    }

    public static DockTarget getParentDockPane(Node dockNode) {
        Node node = DockUtil.getImmediateParent(dockNode, p -> {
            return (DockRegistry.instanceOfDockTarget(p));
        });
        return DockRegistry.dockTarget(node);
    }
/*07.05    public static DockTarget getParentDockPane(Node dockNode) {
        return (DockTarget) DockUtil.getImmediateParent(dockNode, p -> {
            return (p instanceof DockTarget);
        });
    }
*/
}
