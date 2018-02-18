package org.vns.javafx.dock.api.dragging.view;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
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
        
        //18.02if ( !(node.getScene().getRoot() instanceof BorderPane) ) {
        if ( !(node.getScene().getRoot() instanceof StackPane) ) {
            return false;
        }
        StackPane bp = (StackPane) node.getScene().getRoot();
        if ( bp.getChildren().isEmpty() || bp.getChildren().get(0) != node) {
            return false;
        }
        if (node.getScene().getRoot().getStyleClass().contains(FloatView.FLOAT_WINDOW)) {
            
            retval = true;
        }
        return retval;
    }
    static BorderPane layout(Window window, Bounds bounds) {
        double winX = bounds.getMinX();
        double winY = bounds.getMinY();
        double nodeWidth = bounds.getWidth();
        double nodeHeight = bounds.getHeight();

        BorderPane borderPane = (BorderPane) window.getScene().getRoot();
        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(winX - insetsDelta.getLeft());
        window.setY(winY - insetsDelta.getTop());

        if (window instanceof Stage) {
            ((Stage) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            ((Stage) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);
        } else {
            ((PopupControl) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            ((PopupControl) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);

        }

        borderPane.setPrefWidth(nodeWidth + insetsWidth);
        borderPane.setPrefHeight(nodeHeight + insetsHeight);
        return borderPane;
    }
    static void layout2(Window window, Bounds bounds) {
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
            ((Stage) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            ((Stage) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);
        } else {
            ((PopupControl) window).setMinWidth(borderPane.minWidth(nodeHeight) + insetsWidth);
            ((PopupControl) window).setMinHeight(borderPane.minHeight(nodeWidth) + insetsHeight);

        }

        //borderPane.setPrefWidth(nodeWidth + insetsWidth);
        //borderPane.setPrefHeight(nodeHeight + insetsHeight);
    }

}//interface
