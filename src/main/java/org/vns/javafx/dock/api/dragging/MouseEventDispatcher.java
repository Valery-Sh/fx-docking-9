/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api.dragging;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Valery
 */
public class MouseEventDispatcher implements EventDispatcher {
    
    private Node node; 
    private final EventDispatcher nativeDispatcher;
            
    public MouseEventDispatcher(Node node) {
        this.node = node;
        nativeDispatcher = node.getEventDispatcher();
    }
    
    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        
        final EventDispatcher initial = node.getEventDispatcher();

        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                return initial.dispatchEvent(event, tail);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

                return null;
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                return null;
            } else if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED || mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                return initial.dispatchEvent(event, tail);
            }
        }
        return null;
    }

    public EventDispatcher getNativeDispatcher() {
        return nativeDispatcher;
    }


}
