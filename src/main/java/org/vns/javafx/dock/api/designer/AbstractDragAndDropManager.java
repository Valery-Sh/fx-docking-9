/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.designer;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import static org.vns.javafx.dock.api.designer.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public abstract class AbstractDragAndDropManager implements DragAndDropManager, EventHandler<MouseEvent> {

    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     *
     * @param ev the event that describes the mouse events.
     */
    protected void mouseDragDetected(MouseEvent ev) {
        if (ev.isPrimaryButtonDown()) {
            notifyEventFired(ev);
            ev.consume();
            return;
        }
    }

    protected void registerMousePressed(Node source) {
        source.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
    }

    protected void registerMouseDragDetected(Node source) {
        registerMouseDragDetected(source, source, null);
    }

    protected void registerMouseDragDetected(Node source, Object gestureSource, ChildrenRemover remover) {
        
        DragNodeGesture dg = new DragNodeGesture(source, gestureSource);
        source.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
        source.getProperties().put(EditorUtil.DRAGBOARD_KEY, NODE_UUID);
        if (remover != null) {
            source.getProperties().put(EditorUtil.REMOVER_KEY, remover);
        }
        source.addEventHandler(MouseEvent.DRAG_DETECTED, this);
    }
    public void setChildrenRemover(Node source, ChildrenRemover remover) {
        if (remover != null) {
            source.getProperties().put(EditorUtil.REMOVER_KEY, remover);
        } else {
            source.getProperties().remove(EditorUtil.REMOVER_KEY);
        }
    }
    public void setEventNotifier(Node source, EventNotifier notifier) {
        if (notifier != null) {
            source.getProperties().put(EditorUtil.MOUSE_EVENT_NOTIFIER_KEY, notifier);
        } else {
            source.getProperties().remove(EditorUtil.MOUSE_EVENT_NOTIFIER_KEY);
        }
    }
    public EventNotifier getEventNotifier(Node source) {
       return (EventNotifier) source.getProperties().get(EditorUtil.MOUSE_EVENT_NOTIFIER_KEY);
    }
    
    protected void registerMouseReleased(Node source) {
        source.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    protected void registerMouseDragged(Node source) {
        source.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
    }

    @Override
    public DragAndDropManager enableDragAndDrop(Object gestureSourceObject, Node source, ChildrenRemover remover) {
        disableDragAndDrop(source);

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
        if (ev.isPrimaryButtonDown() ) {
            notifyEventFired(ev);
            ev.consume();
            return;
        }
    }
    protected void notifyEventFired(MouseEvent ev) {
        if ( ev.getSource() != null && getEventNotifier( (Node) ev.getSource()) != null ) {
            getEventNotifier( (Node) ev.getSource()).notifyEventFired(ev);
        }
    }
    public void disableDragAndDrop(Node source) {
        source.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
        source.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        source.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
        source.removeEventHandler(MouseEvent.DRAG_DETECTED, this);

        source.getProperties().remove(EditorUtil.GESTURE_SOURCE_KEY);
        source.getProperties().remove(EditorUtil.DRAGBOARD_KEY);
        source.getProperties().remove(EditorUtil.REMOVER_KEY);
        source.getProperties().remove(EditorUtil.MOUSE_EVENT_NOTIFIER_KEY);

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
    protected abstract void mouseDragged(MouseEvent ev);

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    protected abstract void mouseReleased(MouseEvent ev);

    @FunctionalInterface
    public interface EventNotifier {
        void notifyEventFired(MouseEvent event);
    }
}
