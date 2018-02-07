package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Window;
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
    public static final String DOCKABLE_PROP_KEY = "c826e34e-6ae5-4480-b392-a1866a19f3bd";
    
    public static Cursor[] DEFAULT_CURSORS = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    T make(Dockable dockable);

    T make(Dockable dockable, boolean show);

    Object getValue();

    void setSupportedCursors(Cursor[] cursors);

    static Dockable getDockable(Window window) {
        Dockable retval = null;
        if (window != null && window.getScene() != null) {
            Node root = window.getScene().getRoot();
            System.err.println("FloatView: root = " + root);
            if (root != null ) {
                retval = (Dockable) root.getProperties().get(FLOATVIEW_UUID);
            }
        }
        return retval;
    }
    static Window getWindow(Dockable dockable) {
        Window retval = null;
        if ( dockable != null && dockable.node().getScene() != null ) {
            retval = dockable.node().getScene().getWindow();
        }
        System.err.println("FloatView: getWindow = " + retval);
        
        return retval;
    }
    static Dockable getDraggedDockable(Dockable carried) {
        Dockable retval = null;
        Window w = getWindow(carried);
        if ( w != null ) {
            retval = getDockable(w);
        }
        return retval;
    }
}//interface
