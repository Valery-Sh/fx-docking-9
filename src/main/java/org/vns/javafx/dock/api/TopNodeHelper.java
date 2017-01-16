/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class TopNodeHelper {

    public static Node getTopNode(List<Node> nodes) {
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
        
        Node retval = null;
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

    public static Node getTopNode(Stage stage, double screenX, double screenY) {
        return getTopNode(stage, screenX, screenY, (n -> {
            return true;
        }));
    }

    public static Node getTopNode(Stage stage, Point2D screenPos) {
        return getTopNode(stage, screenPos, (n -> {
            return true;
        }));
    }

    public static Node getTopNode(Stage stage, Point2D screenPos, Predicate<Node> predicate) {
        return getTopNode(stage, screenPos.getX(), screenPos.getY(), predicate);
    }

    public static Node getTopNode(Stage stage, double screenX, double screenY, Predicate<Node> predicate) {
        Node retval = null;
        Node node = getTopNode(getNodes(stage, screenX, screenY));
        while (node != null) {
            if (predicate.test(node)) {
                retval = node;
                break;
            }
            node = node.getParent();
        }
        return retval;
    }

    /**
     * Returns a list of nodes for a given stage, screen position.
     *
     * @param stage the stage where nodes are searched
     * @param pos a position of a point on the screen
     * @return a list of nodes
     */
    public static List<Node> getNodes(Stage stage, Point2D pos) {
        return getNodes(stage, pos.getX(), pos.getY(), (node) -> {
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
    public static List<Node> getNodes(Stage stage, double screenX, double screenY) {
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
    public static List<Node> getNodes(Stage stage, double screenX, double screenY, Predicate<Node> predicate) {
        if (stage == null || stage.getScene() == null || stage.getScene().getRoot() == null) {
            return new ArrayList<>();
        }
        List<Node> retval = getNodes(stage.getScene().getRoot(), screenX, screenY, predicate);
        Parent root = stage.getScene().getRoot();
        boolean inside = root.localToScreen(root.getBoundsInLocal()).contains(screenX, screenY);
        if (inside && predicate.test(root)) {
            retval.add(0,root);
        }
        return retval;
    }

    public static List<Node> getNodes(Parent root, double screenX, double screenY, Predicate<Node> predicate) {
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
