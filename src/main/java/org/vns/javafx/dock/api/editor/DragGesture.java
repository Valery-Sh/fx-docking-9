package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;

/**
 * When a DRAG_DETECTED event is raised, an instance of the object
 * implementing {@code DragGesture} is created. 
 * The reference to the object is put as a value to the 
 * {@code properties} property of the node which is a source of the event.
 * The key to this {@code properties} collection is 
 * the value of the constant {@link EditorUtil#GESTURE_SOURCE_KEY }. 
 * Thus, when a gesture target handles the {@code DragEvent} it gets access
 * tho the instance of this class, for example
 * <pre>
 *   public void handle(DragEvent event) {
 *       Node node = (Node) ev.getGestureSource();
 *       DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
 *   }
 * </pre>
 *
 * 
 * @see DragNodeGesture
 * @see DragTreeCellGesture
 */
public interface DragGesture {
    /**
     * Returns a value of the property {@code gestureSource}.
     * @return the object of type Node which is the node that process 
     *  an event of type a {@code MouseEvent.DRAG_DETECTED}
     * 
     */
    Node getGestureSource();
    /**
     * Returns a value of the property {@code gestureSourceObject}.
     * @return the object of type Object which will be used to create 
     *   an object of type TreeItem.
     * 
     */
    Object getGestureSourceObject();
    
    
}
