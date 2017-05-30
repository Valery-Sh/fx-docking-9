package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;

/**
 * 
 * When a DRAG_DETECTED event is raised on a {@code TreeCell}, an instance of 
 * this class is created.
 * The reference to the object is put as a value to the 
 * {@code properties} collection of the  {@code cell}.
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
 * @see DragGesture
 * @see DragNodeGesture
 * 
 * @author Valery Shyshkin
 */
public class DragTreeCellGesture extends DragNodeGesture{
    /**
     * Creates a new instance of the class for the specified parameter.
     * the following code sets the value of the property {@code gestureSourceObject}
     * <pre>
     *  TreeItem it = ((TreeCell)getGestureSource()).getTreeItem();
     *   setSourceGestureObject(((ItemValue)it.getValue()).getTreeItemObject());
     * </pre>
     * 
     * @param gestureSource the object of type {@code TreeCell} on which the 
     *    gesture is initiated.
     */
    public DragTreeCellGesture(Node gestureSource) {
        super(gestureSource);
        DragEvent ev;
        init();
    }
    private void init() {
        TreeItem it = ((TreeCell)getGestureSource()).getTreeItem();
        setSourceGestureObject(((ItemValue)it.getValue()).getTreeItemObject());
    } 
}
