package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Cursor;
import javafx.scene.Node;
import org.vns.javafx.dock.api.Dockable;

/**
 * Represents a view of {@link org.vns.javafx.dock.api.Dockable } objects
 *
 * @author Valery
 */
public interface FloatView<T> {
    public static final String FLOATWINDOW = "UUID-11e0c7b3-2873-465a-bfce-d4edce1bed7d";
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
        if (node.getScene().getRoot().getStyleClass().contains(FloatView.FLOATWINDOW)) {
            retval = true;
        }
        return retval;
    }

}//interface
