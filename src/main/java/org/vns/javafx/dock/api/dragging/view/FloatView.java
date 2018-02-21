package org.vns.javafx.dock.api.dragging.view;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.Dockable;

/**
 * Represents a view of {@link org.vns.javafx.dock.api.Dockable } objects
 *
 * @author Valery
 */
public interface FloatView<T> {
    public static final String FLOAT_WINDOW = "UUID-11e0c7b3-2873-465a-bfce-d4edce1bed7d";
    public static final String FLOATVIEW = "UUID-d767ecfc-a868-4d95-8847-c331b1989bb1";
    public static final String FLOATVIEW_UUID = "UUID-11e0c7b3-2873-465a-bfce-d4edce1bed7d";
    //public static final String DOCKABLE_PROP_KEY = "c826e34e-6ae5-4480-b392-a1866a19f3bd";
    
    public static Cursor[] DEFAULT_CURSORS = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    T make(Dockable dockable);

    T make(Dockable dockable, boolean show);

    Object getValue();

    void setSupportedCursors(Cursor[] cursors);
    
    static boolean isFloating(Node node) {

        boolean retval = false;
        if (node.getScene() == null) {
            return false;
        }
        
        if ( ! (node.getScene().getRoot() instanceof Pane) ) {
        //if ( !(node.getScene().getRoot() instanceof StackPane) ) {
            System.err.println("NOT FLOATING " + node.getScene().getRoot());
            return false;
        }
        Pane bp = (Pane) node.getScene().getRoot();
        Node paneNode;
        if ( bp instanceof BorderPane  ) {
            paneNode = ((BorderPane)bp).getCenter();
        } else {
            paneNode = bp.getChildren().get(0);
        }
        if ( bp.getChildren().isEmpty() || paneNode != node) {
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

        StackPane borderPane = (StackPane) window.getScene().getRoot();
        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(winX - insetsDelta.getLeft());
        window.setY(winY - insetsDelta.getTop());

        if (window instanceof Stage) {
            //((Stage) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            //((Stage) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);
        } else {
            //((PopupControl) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            //((PopupControl) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);

        }
        System.err.println("nodeHeiht = " + (nodeHeight + insetsHeight) );
        System.err.println("1 prefHeiht = " + borderPane.getPrefHeight() );
        System.err.println("2 prefHeiht = " + borderPane.prefHeight(nodeWidth + insetsWidth) );
        double prefHeight = borderPane.prefHeight(nodeWidth + insetsWidth);
        double prefWidth  = borderPane.prefWidth(nodeHeight + insetsHeight);
        borderPane.setPrefWidth(nodeWidth + insetsWidth);
        borderPane.setPrefHeight(nodeHeight + insetsHeight);
        System.err.println("3 prefHeiht = " + borderPane.getPrefHeight() );
        //window.setWidth(nodeWidth);
        //window.setHeight(nodeHeight);
        return borderPane;
    }

}//interface
