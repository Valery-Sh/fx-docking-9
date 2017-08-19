
package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Cursor;
import org.vns.javafx.dock.api.Dockable;

/**
 * Represents a view of {@link org.vns.javafx.dock.api.Dockable } objects
 * @author Valery
 */
public interface FloatView<T> {
    public static Cursor[] DEFAULT_CURSORS = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };
    
    T make(Dockable dockable);
    T make(Dockable dockable, boolean show);
    void setSupportedCursors(Cursor[] cursors);
}
