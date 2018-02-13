package org.vns.javafx.dock.incubator.designer;

import javafx.scene.Node;

/**
 * When a DRAG_DETECTED event is raised, an instance of the object which implements
 * {@code DragGesture} is created. The reference to the object is put as a value
 * to the {@code properties} collection of the node which is a source of the
 * event with event type {@code MouseEvent.DRAG_DETECTED}.. The key to this
 * {@code properties} collection is the value of the constant {@link EditorUtil#GESTURE_SOURCE_KEY
 * }. Thus, when a gesture target handles the {@code DragEvent} it gets access
 * tho the instance of this class, for example
 * <pre>
 *   public void handle(DragEvent event) {
 *       Node node = (Node) ev.getGestureSource();
 *       DragGesture dg = (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
 *   }
 * </pre>
 *
 * @see DragGesture
 * @see DragTreeCellGesture * @author Valery Shyshkin
 */
public class DragNodeGesture implements DragGesture {

    private final Node sourceGesture;
    private Object sourceGestureObject;

    /**
     * Creates a new instance of the class for the specified {@code node }
     * which is used as a (@code source gesture}. The constructor with a single
     * parameter is used when both the {@code gestureSource} and {@code gestureSourceObject
     * } point to the same object.
     *
     * @param sourceGesture the object of type Node used as gesture source in a
     * drag process.
     */
    public DragNodeGesture(Node sourceGesture) {
        this.sourceGesture = sourceGesture;
        this.sourceGestureObject = sourceGesture;
    }

    /**
     * Creates a new instance of the class for the specified parameters. The
     * {@code gestureSource} and {@code gestureSourceObject } may point to the
     * same or different object.
     *
     * @param sourceGesture the object of type Node used as gesture source in a
     * drag process.
     * @param sourceGestureObject the object used to create an item of the
     * {@code TreeView}.
     */
    public DragNodeGesture(Node sourceGesture, Object sourceGestureObject) {
        this.sourceGesture = sourceGesture;
        this.sourceGestureObject = sourceGestureObject;
    }

    /**
     * Designed for use in sub classes.
     *
     * @param sourceGestureObject a new value of the property
     * gestureSourceObjecy
     */
    protected void setSourceGestureObject(Object sourceGestureObject) {
        this.sourceGestureObject = sourceGestureObject;
    }

    /**
     * Returns a value of the property {@code gestureSource}.
     *
     * @return the object of type Node which is the node that process an event
     * of type a {@code MouseEvent.DRAG_DETECTED}
     */
    @Override
    public Node getGestureSource() {
        return sourceGesture;
    }

    /**
     * Returns a value of the property {@code gestureSourceObject}.
     *
     * @return the object of type Object which will be used to create an object
     * of type TreeItem.
     */
    @Override
    public Object getGestureSourceObject() {
        return sourceGestureObject;
    }

}
