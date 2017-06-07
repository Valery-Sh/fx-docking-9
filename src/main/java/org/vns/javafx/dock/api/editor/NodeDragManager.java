package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Valery
 */
public class NodeDragManager extends AbstractDragManager {

    private NodeDragManager() {

    }

    public static NodeDragManager getInstance() {
        return SingletonInstance.INSTANCE;
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
    @Override
    public void mouseDragged(MouseEvent ev) {
        if (ev.isPrimaryButtonDown()) {
            TreeViewEx tv = EditorUtil.getTargetTreeView(ev.getScreenX(), ev.getScreenY());
            if (tv == null) {
                return;
            }
            NodeDragEvent nodeEvent = tv.getNodeDragEvent(ev);
            tv.fireEvent(nodeEvent);
            notifyEventFired(ev);

            ev.consume();
        }
    }

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     * <p>
     * If the object of type {@link EventNotifier } specified then it's method {@link EventNotifier#notifyEventFired(javafx.scene.input.MouseEvent)}
     * is invoked jast after the tree view handled the event.
     * </p>
     *
     * @param ev the event that describes the mouse events.
     */
    @Override
    public void mouseReleased(MouseEvent ev) {
        TreeViewEx tv = EditorUtil.getTargetTreeView(ev.getScreenX(), ev.getScreenY());
        if (tv != null && !ev.isConsumed()) {
/*            DragEvent dragEvent = tv.getDragEvent();
            
            System.err.println("dragEvent.getTransferMode() = " + dragEvent.getAcceptedTransferMode());
            if (dragEvent.getTransferMode() != TransferMode.COPY
                    && dragEvent.getTransferMode() != TransferMode.MOVE) {
                return;
            }
*/
            if ( ! tv.isDragAccepted() ) {
                return;
            }
            tv.fireEvent(tv.getNodeDragEvent(ev));
            notifyEventFired(ev);
        }
        ev.consume();

    }

    public Object getGestureSourceObject(Node source) {
        return source.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
    }

    private static class SingletonInstance {
        private static final NodeDragManager INSTANCE = new NodeDragManager();
    }

}
