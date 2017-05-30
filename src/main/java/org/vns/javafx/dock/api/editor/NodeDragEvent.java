package org.vns.javafx.dock.api.editor;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.shape.Shape;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class NodeDragEvent extends Event{
    
    public static EventType<NodeDragEvent>  NODE_DRAG = createtEventType();
    private MouseEvent mouseEvent;
    private Node gestureSource;
    
/*    public NodeDragEvent() {
        super(NODE_DRAG);
    }
*/
    public NodeDragEvent(MouseEvent mouseEvent) {
        super(mouseEvent == null ? null : mouseEvent.getSource(), null, NODE_DRAG);
        this.mouseEvent = mouseEvent;
        this.gestureSource = (Node) mouseEvent.getSource();
    }    
    
/*    public NodeDragEvent(Node source) {
        super(source, null, NODE_DRAG);
    }    
    public NodeDragEvent(Node source, EventTarget target ) {
        super(source, target, NODE_DRAG);
    }
*/
    private static EventType<NodeDragEvent> createtEventType() {
        return new EventType("NODE_DRAG");
    }
    public void setSource(Node source) {
        this.source = source;
    }

    public Node getGestureSource() {
        return gestureSource;
    }
    
    public DragGesture getDragGesture() {
        if ( gestureSource == null ) {
            return null;
        }
        Node node = (Node) gestureSource;
        return (DragGesture) node.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public void setMouseEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
    }
    
    

}
