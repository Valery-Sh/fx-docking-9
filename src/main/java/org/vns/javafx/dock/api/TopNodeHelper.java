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
public class TopNodeHelper {

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
/*    public static Node pickTop(Window win, double screenX, double screenY, Predicate<Node> predicate) {
        if (win == null || win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        Parent root = win.getScene().getRoot();
        String skip = FramePane.CSS_CLASS;
        Point2D p = root.screenToLocal(screenX, screenY);

        if (!root.contains(p) || !root.isVisible() || root.isMouseTransparent() || root.getStyleClass().contains(skip) || !predicate.test(root)) {
            return null;
        }
        return TopNodeHelper.pickTopExclusive(root, screenX, screenY, predicate);
    }
*/
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
  /*  public static Node pickTopExclusive(Node node, double screenX, double screenY, Predicate<Node> predicate) {
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
                return TopNodeHelper.pickTopExclusive(top, screenX, screenY, predicate);
            }
        }
        return node;
    }
    public static Node getTopExclusive(Node node, double screenX, double screenY, Predicate<Node> predicate) {
        String skip = FramePane.CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!(node.contains(p) && !node.getStyleClass().contains(skip))) {
            return null;
        }
        if (! (node instanceof Parent) ) {
            return null;
        }
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
                return TopNodeHelper.pickTopExclusive(top, screenX, screenY, predicate);
            }
        return node;
    }
    
    public static Node pickTopExclusive(Node node, double screenX, double screenY) {
        return TopNodeHelper.pickTopExclusive(node, screenX, screenY, c -> {
            return true;
        });
    }
*/    
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
    public static Node getTop(Window win, double screenX, double screenY, Predicate<Node> predicate) {
        if (win == null || win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        return getTop(win.getScene().getRoot(), screenX, screenY, predicate);
    }

    /**
     * Returns the top node in the scene graph of the specified node. The method
     * doesn't take into account the {@code visible} property and
     * {@code mouseTransparent} property. If no node found the method return
     * {@code null}. If the specified node doesn't contain the given point the
     * method returns null.
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
     * Returns the top node in the scene graph of the specified node. The method
     * doesn't take into account the {@code visible} property and
     * {@code mouseTransparent} property. If no node found the method return
     * {@code null}. If the specified node doesn't contain the given point or
     * does not pass the test specified by the predicate the the method returns
     * null.
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

        if (!node.contains(p) || node.getStyleClass().contains(skip)) {
            return null;
        }
        if (!(node instanceof Parent) && predicate.test(node)) {
            return node;
        } else if (!(node instanceof Parent) ) {
            return null;
        }
        
        Node top = testTop(node, screenX, screenY, predicate);
        System.err.println("************* 1) top = " + top);
        if ( top == null ) {
            return null;
        }
        while ( top != null && ! predicate.test(top)   ) {
            top = top.getParent();
        }
        System.err.println("************* 2) return top = " + top);
        return top;
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
                if (c.isVisible() && !node.isMouseTransparent() && c.contains(p) && !c.getStyleClass().contains(skip)) {
                    top = c;
                    break;
                }
            }

            if (top != null) {
                return testTop(top, screenX, screenY, predicate);
            }
        }
        return node;
    }

}
