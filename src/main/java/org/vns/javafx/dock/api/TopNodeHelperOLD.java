package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.dragging.view.FramePane;

/**
 *
 * @author Valery Shyshkin
 */
public class TopNodeHelperOLD {

    public static Node getTopNode(Collection<Node> nodes) {
        List<Node> visNodes = new ArrayList<>();
        nodes.stream().filter((n) -> (n.isVisible())).forEachOrdered((n) -> {
            visNodes.add(n);
        });
        if (visNodes.isEmpty()) {
            return null;
        }
        if (visNodes.size() == 1) {
            return visNodes.get(0);
        }

        Stack<Node> stack = new Stack<>();
        stack.addAll(visNodes);
        Node retval = null;
        Node node1;
        while (!stack.isEmpty()) {
            node1 = stack.pop();
            if (stack.isEmpty()) {
                retval = node1;
                break;
            }
            Node node2 = stack.peek();
            Node top = getHigherNode(node1, node2);
            if (top == node1) {
                stack.pop();
                stack.push(node1);
            }
        }
        return retval;
    }

    public static Node getHigherNode(Node node1, Node node2) {
        if (node1 == node2) {
            return node1;
        }

        Node retval;// = null;
        List<Node> chain1 = getParentChain(node1);
        List<Node> chain2 = getParentChain(node2);
        if (chain1.isEmpty() && chain2.isEmpty()) {
            return null;
        } else if (chain1.isEmpty()) {
            return node2;
        } else if (chain2.isEmpty()) {
            return node1;
        }
        //
        // Find  a common root. It cannot be null because of scene.getRoot()
        //
        int idx1 = -1;
        for (int i = 0; i < chain1.size(); i++) {
            if (chain2.contains(chain1.get(i))) {
                idx1 = i;
                break;
            }
        }

        int idx2 = chain2.indexOf(chain1.get(idx1));

        if (idx1 == 0) {
            retval = node2;
        } else if (idx2 == 0) {
            retval = node1;
        } else {
            Node commonRoot = chain1.get(idx1);
            Node p1 = chain1.get(idx1 - 1);
            Node p2 = chain2.get(idx2 - 1);
            //
            // Now we have a subtree:
            // commonRoot 
            //    p1
            //    p2
            //
            List<Node> c = ((Parent) commonRoot).getChildrenUnmodifiable();
            if (c.indexOf(p1) > c.indexOf(p2)) {
                retval = node1;
            } else {
                retval = node2;
            }
        }
        return retval;
    }

    /**
     * Returns a list of parent nodes for the given node. The first element of
     * the list is the specified node, the second is the parent of the first,
     * the third is the parent of the second etc.
     *
     * @param node a top most node from which the parent chain starts.
     * @return a list of parent nodes for the given node.
     */
    public static List<Node> getParentChain(Node node) {

        List<Node> chain = new ArrayList<>();
        if (node == null || node.getScene() == null || node.getScene().getWindow() == null) {
            return chain;
        }
        chain.add(node);
        Node p = node.getParent();
        while (p != null) {
            chain.add(p);
            p = p.getParent();
        }
        return chain;
    }

    public static List<Node> getParentChain(Node node, Predicate<Node> predicate) {
        List<Node> retval = new ArrayList<>();
        getParentChain(node).forEach(p -> {
            if (predicate.test(p)) {
                retval.add(p);
            }
        });
        return retval;
    }

    public static Node getTopNode(Stage stage, double screenX, double screenY) {
        return getTopNode(stage, screenX, screenY, (n -> {
            return true;
        }));
    }

    public static Node getTopNode(Window stage, Point2D screenPos) {
        return getTopNode(stage, screenPos, (n -> {
            return true;
        }));
    }

    public static Node getTopNode(Window stage, Point2D screenPos, Predicate<Node> predicate) {
        return getTopNode(stage, screenPos.getX(), screenPos.getY(), predicate);
    }

    public static Node getTopNode(Window stage, double screenX, double screenY, Predicate<Node> predicate) {
        String skipWithSyleClass = FramePane.CSS_CLASS;

        Node retval = null;
        Node node = getTopNode(getNodes(stage, screenX, screenY, n -> {
            return !n.getStyleClass().contains(skipWithSyleClass);
        }
        ));
        while (node != null) {
            if (!node.getStyleClass().contains(skipWithSyleClass) && node.isVisible() && node.contains(node.screenToLocal(screenX, screenY)) && predicate.test(node)) {
                retval = node;
                break;
            }
            node = node.getParent();
        }
        System.err.println("TopNodeHelperOLD getTopNode(Window stage...) = " + node);
        return retval;
    }

    /**
     * Returns the top node in the scene graph of the scene of the specified
     * window. May be used with the code dealing with the mouse events and takes
     * into account the visible property and mouseTransparent property of the
     * tested nodes..
     *
     * @param win the window to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * @param predicate the predicate used to filter nodes
     *
     * @return the top node in the scene graph or null if not found
     */
    public static Node pickTop(Window win, double screenX, double screenY, Predicate<Node> predicate) {
        if (win == null || win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        Parent root = win.getScene().getRoot();
        String skip = FramePane.CSS_CLASS;
        Point2D p = root.screenToLocal(screenX, screenY);

        if (!root.contains(p) || !root.isVisible() || root.isMouseTransparent() || root.getStyleClass().contains(skip) || !predicate.test(root)) {
            return null;
        }
        return TopNodeHelperOLD.pickTopExclusive(root, screenX, screenY, predicate);
    }

    /**
     * Returns the top node in the scene graph of the specified node. May be
     * used with the code dealing with the mouse events and takes into account
     * the visible property and mouseTransparent property. If no node found the
     * node given as parameter returns even if it is not visible or transparent
     * or doesn't tested by the predicate. The code which uses this method must
     * test the returned node whether it is the same as the node specified as a
     * parameter.
     *
     * @param node the node to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * @param predicate the predicate used to filter nodes
     * @return the top node which is not the same as the node given by the
     * parameter or the node specified as a parameter.
     */
    public static Node pickTopExclusive(Node node, double screenX, double screenY, Predicate<Node> predicate) {
        String skip = FramePane.CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!(node.isVisible() && !node.isMouseTransparent() && node.contains(p) && !node.getStyleClass().contains(skip))) {
            return node;
        }
        if (node instanceof Parent) {
            Node top = null;
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node c = children.get(i);
                p = c.screenToLocal(screenX, screenY);
                if (c.isVisible() && !node.isMouseTransparent() && c.contains(p) && predicate.test(c) && !c.getStyleClass().contains(skip)) {
                    top = c;
                    break;
                }
            }

            if (top != null) {
                return TopNodeHelperOLD.pickTopExclusive(top, screenX, screenY, predicate);
            }
        }
        return node;
    }

    public static Node pickTopExclusive(Node node, double screenX, double screenY) {
        return TopNodeHelperOLD.pickTopExclusive(node, screenX, screenY, c -> {
            return true;
        });
    }
 /**
     * Returns the top node in the scene graph of the specified node. 
     * The method doesn't take into account the {@code visible} property and 
     * {@code mouseTransparent} property. If no node found the method return {@code null}.
     * If the specified node doesn't contain the given point the method returns null.
     * 
     * @param node the node to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * 
     * @return the top node in the scene graph of the node given as parameter
     */
    public static Node getTop(Node node, double screenX, double screenY) {
        return getTop(node, screenX, screenY, n -> {
            return true;
        });
    }

    /**
     * Returns the top node in the scene graph of the specified node. 
     * The method doesn't take into account the {@code visible} property and 
     * {@code mouseTransparent} property. If no node found the method return {@code null}.
     * If the specified node doesn't contain the given point or does not pass 
     * the test specified by the predicate the the method returns null.
     * 
     * @param node the node to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * @param predicate the predicate used to filter nodes
     * @return the top node in the scene graph of the node given as parameter
     */
    public static Node getTop(Node node, double screenX, double screenY, Predicate<Node> predicate) {

        String skip = FramePane.CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!(node.contains(p) && !node.getStyleClass().contains(skip))) {
            return null;
        }
        if (!(node instanceof Parent)) {
            return node;
        }
        return testTop(node, screenX, screenY, predicate);
    }

    private static Node testTop(Node node, double screenX, double screenY, Predicate<Node> predicate) {
        String skip = FramePane.CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!(node.contains(p) && !node.getStyleClass().contains(skip))) {
            return node;
        }
        if (node instanceof Parent) {
            Node top = null;
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node c = children.get(i);
                p = c.screenToLocal(screenX, screenY);
                if (c.isVisible() && !node.isMouseTransparent() && c.contains(p) && predicate.test(c) && !c.getStyleClass().contains(skip)) {
                    top = c;
                    break;
                }
            }

            if (top != null) {
                return TopNodeHelperOLD.pickTopExclusive(top, screenX, screenY, predicate);
            }
        }
        return node;
    }

    /**
     * Returns a list of nodes for a given stage, screen position.
     *
     * @param stage the stage where nodes are searched
     * @param screenPos a position of a point on the screen
     * @return a list of nodes
     */
    public static List<Node> getNodes(Window stage, Point2D screenPos) {
        return getNodes(stage, screenPos.getX(), screenPos.getY(), (node) -> {
            return true;
        });
    }

    /**
     * Returns a list of nodes for a given stage, screen position.
     *
     * @param stage the stage where nodes are searched
     * @param screenX a horizontal position of a point on the screen
     * @param screenY a vertical position of a point on the screen
     * @return a list of nodes
     */
    public static List<Node> getNodes(Window stage, double screenX, double screenY) {
        return getNodes(stage, screenX, screenY, (node) -> {
            return true;
        });
    }

    /**
     * Returns a list of nodes for a given stage, screen position and the
     * specified predicate.
     *
     * @param stage the stage where nodes are searched
     * @param screenX a horizontal position of a point on the screen
     * @param screenY a vertical position of a point on the screen
     * @param predicate a function to filter nodes
     * @return a list of nodes
     */
    public static List<Node> getNodes(Window stage, double screenX, double screenY, Predicate<Node> predicate) {
        if (stage == null || stage.getScene() == null || stage.getScene().getRoot() == null) {
            return new ArrayList<>();
        }
        List<Node> retval = getNodes(stage.getScene().getRoot(), screenX, screenY, predicate);
        Parent root = stage.getScene().getRoot();
        boolean inside = root.localToScreen(root.getBoundsInLocal()).contains(screenX, screenY);
        if (inside && predicate.test(root)) {
            retval.add(0, root);
        }
        return retval;
    }

    private static List<Node> getNodes(Parent root, double screenX, double screenY, Predicate<Node> predicate) {
        List retval = new ArrayList();

        for (Node node : root.getChildrenUnmodifiable()) {
            boolean inside = node.localToScreen(node.getBoundsInLocal()).contains(screenX, screenY);
            if (inside && predicate.test(node)) {
                retval.add(node);
            }
            if (node instanceof Parent) {
                retval.addAll(getNodes((Parent) node, screenX, screenY, predicate));
            }
        }
        return retval;
    }

}
