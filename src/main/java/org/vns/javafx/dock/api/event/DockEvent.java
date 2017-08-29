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
package org.vns.javafx.dock.api.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.stage.WindowEvent;

/**
 *
 * @author Valery
 */
public class DockEvent extends Event {
   
    public static final EventType<DockEvent> NODE_DOCKED = new EventType<>("node-docked");    
    public static final EventType<DockEvent> NODE_UNDOCKED  = new EventType<>("node-undocked");    ;    
    
    private Node dockedNode;
    private Node targetNode;
    
    private Object[] dockPosition;
            
    public DockEvent(EventType<? extends Event> eventType, Node dockedNode, Node targetNode, Object... dockPosition) {
        super(eventType);
        this.dockedNode = dockedNode;
        this.targetNode = targetNode;
        this.dockPosition = dockPosition;
    }

    public Node getTargetNode() {
        return targetNode;
    }
    public Node getDockedNode() {
       return dockedNode;
    }

    public Object[] getDockPosition() {
        return dockPosition;
    }
    
}
