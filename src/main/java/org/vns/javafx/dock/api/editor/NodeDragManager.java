package org.vns.javafx.dock.api.editor;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class NodeDragManager implements EventHandler<MouseEvent> {

    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     *
     * @param ev the event that describes the mouse events.
     */
    protected void mouseDragDetected(MouseEvent ev) {
        if (ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
    }

    public void registerMousePressed(Node source) {
        source.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
    }

    public void registerMouseDragDetected(Node source) {
        registerMouseDragDetected(source, source, null);
    }

    protected void registerMouseDragDetected(Node source, Object gestureSource, ChildrenNodeRemover remover) {
        DragNodeGesture dg = new DragNodeGesture(source,gestureSource);
        source.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
        source.getProperties().put(EditorUtil.DRAGBOARD_KEY, NODE_UUID);
        if ( remover != null ) {
            source.getProperties().put(EditorUtil.REMOVER_KEY, remover);
        }
        source.addEventHandler(MouseEvent.ANY, this);
    }

    protected void registerMouseReleased(Node source) {
        source.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
        source.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    protected void registerMouseDragged(Node source) {
        source.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
    }

    public NodeDragManager enableDragAndDrop(Object gestureSourceObject, Node source ) {
        enableDragAndDrop(gestureSourceObject, source, null);
        return this;
    }
    public NodeDragManager enableDragAndDrop(Object gestureSourceObject, Node source, ChildrenNodeRemover remover) {
        registerMousePressed(source);
        
        registerMouseReleased(source);
        if (gestureSourceObject != null) {
            registerMouseDragDetected(source, gestureSourceObject, remover);
        } else {
            registerMouseDragDetected(source, source, remover);
        }
        registerMouseDragged(source);
        return this;
    }

    public NodeDragManager enableDragAndDrop(Node source) {
        return this.enableDragAndDrop(null, source, null);
    }
    public NodeDragManager enableDragAndDrop(Node source, ChildrenNodeRemover remover) {
        return this.enableDragAndDrop(null, source, remover);
    }

    /**
     * The implementation of the interface {@code EventHandler<MouseEvent> }.
     * Depending of the event type invokes one of the methods
     * <ul>
     * <li>{@link #mousePressed(javafx.scene.input.MouseEvent)}<li>
     * <li>{@link #mouseReleased(javafx.scene.input.MouseEvent) }
     * <li>{@link #mouseDragDetected(javafx.scene.input.MouseEvent)}<li>
     * <li>{@link #mouseDragged(javafx.scene.input.MouseEvent) }<li>
     * </ul>
     *
     * @param ev the event that describes the mouse events.
     */
    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            mouseDragDetected(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            mouseDragged(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased(ev);
        }
    }

    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    protected void mousePressed(MouseEvent ev) {
        if (ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
    }

    /**
     * The method is called when the user moves the mouse and the primary mouse
     * button is pressed. The method checks whether the {@literal  dockable} node
     * is in the {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock target target} then it shows a pop up window which contains
     * indicators to select a dock place on the target dock node or target.
     * <p>
     * The method checks whether the {@code control key} of the keyboard is
     * pressed and if so then it shows a special indicator window which allows
     * to select a dock target or one of it's parents.
     *
     * @param ev the event that describes the mouse events
     */
    public void mouseDragged(MouseEvent ev) {
        if (ev.isPrimaryButtonDown()) {

            TreeViewEx tv = EditorUtil.getTargetTreeView(ev.getScreenX(), ev.getScreenY());
            if (tv == null) {
                return;
            }
            tv.fireEvent(new NodeDragEvent(ev));

            ev.consume();
        }
    }
static int cc = 0;
    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    public void mouseReleased(MouseEvent ev) {
        TreeViewEx tv = EditorUtil.getTargetTreeView(ev.getScreenX(), ev.getScreenY());
        if (tv != null && ! ev.isConsumed()) {
            tv.fireEvent(new NodeDragEvent(ev));
        }

        Node source = (Node) ev.getSource();
        source.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
        source.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        source.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        source.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
        ev.consume();

        //Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
    }

}
