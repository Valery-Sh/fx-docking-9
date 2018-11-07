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
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 *
 * @author Nastia
 */
public abstract class StopChangeEvent extends Event {

    public static final EventType<StopChangeEvent> STOP_CHANGE = new EventType(ANY);

    public StopChangeEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(StopChangeEventHandler handler);

    public static class CurrentStopChangeEvent extends StopChangeEvent {

        public static final EventType<StopChangeEvent> CURRENT_STOP_CHANGE = new EventType(STOP_CHANGE, "currentStopChange");

        private final int param;

        public CurrentStopChangeEvent(int param) {
            super(CURRENT_STOP_CHANGE);
            this.param = param;
        }

        @Override
        public void invokeHandler(StopChangeEventHandler handler) {
            handler.onEvent(param);
        }

    }


    public static abstract class StopChangeEventHandler implements EventHandler<StopChangeEvent> {

        public abstract void onEvent(int param0);
        
        @Override
        public void handle(StopChangeEvent event) {
            event.invokeHandler(this);
        }
    }

}
