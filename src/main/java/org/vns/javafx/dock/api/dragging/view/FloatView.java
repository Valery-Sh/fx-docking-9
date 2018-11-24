package org.vns.javafx.dock.api.dragging.view;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.vns.javafx.dock.api.Dockable;

/**
 * Represents a view of {@link org.vns.javafx.dock.api.Dockable } objects
 *
 * @author Valery
 */
public interface FloatView {

    public static final String FLOAT_WINDOW = "UUID-11e0c7b3-2873-465a-bfce-d4edce1bed7d";
    public static final String FLOATVIEW = "UUID-d767ecfc-a868-4d95-8847-c331b1989bb1";
    public static final String FLOATVIEW_UUID = "UUID-11e0c7b3-2873-465a-bfce-d4edce1bed7d";
    //public static final String DOCKABLE_PROP_KEY = "c826e34e-6ae5-4480-b392-a1866a19f3bd";

    public static Cursor[] DEFAULT_CURSORS = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    Window make(Dockable dockable);

    Window make(Dockable dockable, boolean show);

    Object getValue();

    void setSupportedCursors(Cursor[] cursors);

    static boolean isFloating(Node node) {

        boolean retval = false;
        if (node.getScene() == null) {
            return false;
        }

        if (!(node.getScene().getRoot() instanceof Pane)) {
            return false;
        }
        Pane bp = (Pane) node.getScene().getRoot();
        Node paneNode;
        if (bp instanceof BorderPane) {
            paneNode = ((BorderPane) bp).getCenter();
        } else {
            paneNode = bp.getChildren().get(0);
        }
        if (bp.getChildren().isEmpty() || paneNode != node) {
            return false;
        }
        if (node.getScene().getRoot().getStyleClass().contains(FloatView.FLOAT_WINDOW)) {

            retval = true;
        }
        return retval;
    }

    static StackPane layout(Window window, Bounds bounds) {
        double winX = bounds.getMinX();
        double winY = bounds.getMinY();
        double nodeWidth = bounds.getWidth();
        double nodeHeight = bounds.getHeight();

        StackPane pane = (StackPane) window.getScene().getRoot();
        Insets insetsDelta = pane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(winX - insetsDelta.getLeft());
        window.setY(winY - insetsDelta.getTop());

        /*        if (window instanceof Stage) {
            //((Stage) window).setMinWidth(pane.minWidth(nodeHeight) + insetsWidth);
            //((Stage) window).setMinHeight(pane.minHeight(nodeWidth) + insetsHeight);
        } else {
            //((PopupControl) window).setMinWidth(pane.minWidth(nodeHeight) + insetsWidth);
            //((PopupControl) window).setMinHeight(pane.minHeight(nodeWidth) + insetsHeight);
        }
         */
        pane.setPrefWidth(nodeWidth + insetsWidth);
        pane.setPrefHeight(nodeHeight + insetsHeight);

        return pane;
    }
    
    default Pane createRoot(Node dockNode) {
        return new RootPane(dockNode);
    }
            
    public static class RootPane extends StackPane {

        public final Node dockNode;

        public RootPane(Node dockNode) {
            super(dockNode);
            this.dockNode = dockNode;
        }

        public Node getDockNode() {
            return dockNode;
        }


        @Override
        public String getUserAgentStylesheet() {
            return Dockable.class.getResource("resources/default.css").toExternalForm();
        }
    }

}//interface
