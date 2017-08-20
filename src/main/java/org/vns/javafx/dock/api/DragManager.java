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
package org.vns.javafx.dock.api;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.dragging.DragType;

/**
 *
 * @author Valery
 */
public interface DragManager { //extends EventHandler<MouseEvent> {

    DragType getDragType();
    
    Dockable getDockable();
    
    void dragDetected(MouseEvent ev, Point2D startMousePos);
    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    //void mousePressed(MouseEvent ev);

    //void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue);

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
    //void mouseDragged(MouseEvent ev);/* {
    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    //void mouseReleased(MouseEvent ev);
    /* {
    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     * {@code dockable} objects is in a floating state and if not invokes the
     * method {@link DockableContext#setFloating(boolean) } with an argument
     * set to {@code true}.
     *
     * @param ev the event that describes the mouse events.
     */
    //void mouseDragDetected(MouseEvent ev);/* {
    /**
     * The implementation of the interface {@code EventHandler<MouseEvent> }.
     * Depending of the event type invokes one of the methods
     * <ul>
     * <li>{@link #mousePressed(javafx.scene.input.MouseEvent)}</li>
     * <li>{@link #mouseReleased(javafx.scene.input.MouseEvent) }
     * <li>{@link #mouseDragDetected(javafx.scene.input.MouseEvent)}</li>
     * <li>{@link #mouseDragged(javafx.scene.input.MouseEvent)}</li>
     * </ul>
     *
     * @param ev the event that describes the mouse events.
     */
/*    @Override
    default void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
           // mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            //mouseDragDetected(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            mouseDragged(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased(ev);
        }
    }
*/    
//    public void setStartMousePos(Point2D startMousePos);
}
