package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Cursor;
import javafx.scene.Node;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 * Represents a view of {@link org.vns.javafx.dock.api.Dockable } objects
 *
 * @author Valery
 */
public interface FloatView<T> {

    public static Cursor[] DEFAULT_CURSORS = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    T make(Dockable dockable);

    T make(Dockable dockable, boolean show);

    Object getValue();

    void setSupportedCursors(Cursor[] cursors);

    static boolean isValueDockable(Object value) {
        if (value == null) {
            return false;
        }
        boolean retval = (value instanceof Dockable) || ((value instanceof Node) && DockRegistry.isDockable((Node)value));

        return retval;
    }

}
