package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockSplitDelegate.DockSplitPane;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockUtil {

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

    public static Parent getImmediateParent(Parent root, Node child, Consumer<Parent> consumer) {
        if (child == null || child.getScene() == null || child.getScene().getRoot() == null) {
            return null;
        }
        Parent retval = null;
        List<Node> dockables = new ArrayList<>();
        addAllDockable(root, dockables);

        for (Node dockable : dockables) {
            Node node = findNode((Parent) dockable, child);
            if (node != null) {
                retval = (Parent) dockable;
                break;
            }
        }

        return retval;
    }

    public static Node getDockableImmediateParent(Node child) {
        Node retval = null;
        Node p = getDockableParent(child);
        if (p == null) {
            return null;
        }
        retval = getDockableParent((Parent) p, child);
        if (retval == null) {
            retval = p;
        }
        return retval;
    }

    public static Node getDockableParent(Parent root, Node child) {
        if (child == null || child.getScene() == null || child.getScene().getRoot() == null) {
            return null;
        }
        Node retval = null;
        List<Node> dockables = new ArrayList<>();
        addAllDockable(root, dockables);

        for (Node dockable : dockables) {
            Node node = findNode((Parent) dockable, child);
            if (node != null) {
                retval = dockable;
                break;
            }
        }

        return retval;
    }

    public static Node getDockableParent(Node child) {
        if (child == null || child.getScene() == null || child.getScene().getRoot() == null) {
            return null;
        }
        Parent root = child.getScene().getRoot();
        return getDockableParent(root, child);
    }

    public static Node findNode(Parent dockable, Node toSearch) {
        Node retval = null;
        for (Node node : dockable.getChildrenUnmodifiable()) {
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

    public static Node getFocusedDockable(Node focusedNode) {
        if (focusedNode == null || focusedNode.getScene() == null || focusedNode.getScene().getRoot() == null) {
            return null;
        }
        Node retval = null;

        Parent root = focusedNode.getScene().getRoot();
        List<Node> dockables = new ArrayList<>();
        addAllDockable(root, dockables);

        for (Node dockable : dockables) {
            //List<Node> list = getAllNodes((Parent)d);
            Node node = getFocused((Parent) dockable);
            if (node != null) {
                retval = dockable;
                break;
            }

        }

        return retval;
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
    }

    public static void addAllDockable(Parent parent, List<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Dockable) {
                nodes.add(node);
            }
            if (node instanceof Parent) {
                addAllDockable((Parent) node, nodes);
            }
        }
    }

    public static Parent get(Parent root, Node child) {
        Parent r = null;
        Stack<Parent> s = new Stack<>();
        Parent retval = null;
        s.push(root);
        while (!s.isEmpty()) {
            Parent p = s.pop();

            List<Node> children = p.getChildrenUnmodifiable();
            
            if (p instanceof Region) {
                Region splitPane = (Region) p;
                children = splitPane.getChildrenUnmodifiable();
            }

            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == child) {
                    r =  p;
                } else if (children.get(i) instanceof Parent) {
                    s.push((Parent) children.get(i));
                }
            }
        }
        return r;
    }

    public static Node getFocused(Parent parent) {
        Node retval = null;
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                retval = node;
            } else if (node instanceof Parent) {
                retval = getFocused((Parent) node);
            }
            if (retval != null) {
                break;
            }
        }
        return retval;

    }

}
