package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;

/**
 *
 * @author Valery Shyshkin
 */
public interface DragGesture {

    Node getGestureSource();
    Object getGestureSourceObject();
    
}
