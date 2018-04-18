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
package org.vns.javafx.dock.api;

import java.util.function.Predicate;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Olga
 */
public interface ConditionalEventDispatcher  extends EventDispatcher {
    void start(Node node);
    Predicate<Node> getPreventCondition();
    void setPreventCondition(Predicate<Node> preventCondition);
    void finish(Node node);
    //boolean isNative();
    
    public static class RejectMouseReleasedDispatcher implements ConditionalEventDispatcher {

        private EventDispatcher initial;
        private Node node;
        private Predicate<Node> preventCondition;

        public RejectMouseReleasedDispatcher() {
            this(null);
        }

        public RejectMouseReleasedDispatcher(Predicate<Node> cond) {
            preventCondition = cond;
            init();
        }

        private void init() {
        }

        @Override
        public void start(Node node) {
            this.node = node;
            initial = node.getEventDispatcher();
            node.setEventDispatcher(this);
        }

        @Override
        public Predicate<Node> getPreventCondition() {
            return preventCondition;
        }

        @Override
        public void setPreventCondition(Predicate<Node> preventCondition) {
            this.preventCondition = preventCondition;
        }

        @Override
        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {

                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    return pressed(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    return released(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    return clicked(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
                    return dragDetected(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    return dragged(event, tail);
                }
            }

            return initial.dispatchEvent(event, tail);
        }

        protected Event pressed(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event released(Event event, EventDispatchChain tail) {
            if (preventCondition == null || preventCondition.test(node)) {
                return null;
            }
            return initial.dispatchEvent(event, tail);
        }

        protected Event clicked(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event dragDetected(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event dragged(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        @Override
        public void finish(Node node) {
            if ( initial != null ) {
                node.setEventDispatcher(initial);
            }
        }


    }
    
}
