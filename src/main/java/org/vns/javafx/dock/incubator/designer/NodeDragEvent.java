package org.vns.javafx.dock.incubator.designer;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The class serves as an intermediate link between {@code simple press-drag-release gesture} 
 * and {@code drag-and-drop gesture}.

 * Suppose that our application consists of two windows. 
 * The first window contains an object of type {@link TreeViewEx }
 * and the second object of type {@code Button }. We want to drag the button
 * to the tree view. It is not possible to implement {@code drag-and-drop gesture }
 * because of the fact that the source gesture and the target one reside 
 * on different windows. But we can implement {@code simple press-drag-release gesture}.
 * When the {@code MouseEvent.MOUSE_DRAGGED } is triggered then we can execute
 * the following code:
 * 
 * <pre>
 *    DragEvent dragEvent = new DragEvent(....)
 *    treViewEx.fireEvent(dragEvent);
 * </pre>
 * But this approach has several drawbacks. The first is that the {@code dragEvent}
 * changes the value of the {@code gestureSource}, which becomes {@code treeViewEx}.
 * The second is that it's better to provide the object {@code treeViewEx} to decide
 * how to fire the {@code DragEvent}.
 * <p>
 * This class solves the problems mentioned above. The class {@code TreeViewEx}} 
 * implements the interface {@code EventHandler<NodeDragEvent>}. And now we can execute the code:
 * </p>
 * <pre>
 *    NodeDragEvent nodeDragEvent = new NodeDragEvent(mouseEvent);
 *    treeViewEx.fireEvent(nodeDragEvent);
 * </pre>
 * 
 * 
 * @see NodeDragManager
 * 
 * @author Valery
 */
public class NodeDragEvent extends Event{
    
    public   static EventType<NodeDragEvent>  NODE_DRAG = createtEventType();
    private  MouseEvent mouseEvent;
    private  Node gestureSource;
    
    /**
     * Create a new instance of the class for the specified {@code mouseEvent}.
     * @param mouseEvent the event  for which this object is creating
     */
    public NodeDragEvent(MouseEvent mouseEvent) {
        super(mouseEvent == null ? null : mouseEvent.getSource(), null, NODE_DRAG);
        this.mouseEvent = mouseEvent;
        if ( mouseEvent == null ) {
            this.gestureSource = null; 
        } else {
            this.gestureSource = (Node) mouseEvent.getSource();
        }
    }    
    protected void setMouseEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
        this.gestureSource = (Node) mouseEvent.getSource();
    }

    private static EventType<NodeDragEvent> createtEventType() {
        return new EventType("NODE_DRAG");
    }
    /**
     * Returns an object of type {@code Node} which serves as a gesture source.
     * Actually the the method returns the same value as 
     * {@code (Node}getMouseEvent().getSource()}
     * @return an object of type {@code Node} which serves as a gesture source.
     * 
     * @see #getMouseEvent() 
     */
    public Node getGestureSource() {
        return gestureSource;
    }
    
    /**
     * Returns the object which was used to create this one.
     * @return the object which was used to create this one.
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }


}
