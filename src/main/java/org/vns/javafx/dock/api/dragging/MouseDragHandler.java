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

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.DockableContext;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class MouseDragHandler implements EventHandler<MouseEvent> {

    private final DockableContext context;
    private Point2D startMousePos;

    protected MouseDragHandler(DockableContext context) {
        this.context = context;
    }
    
    public abstract void mouseDragDetected(MouseEvent ev);
    
    public void mousePressed(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            return;
        }
        startMousePos = new Point2D(ev.getX(), ev.getY());
    }

  

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            mouseDragDetected(ev);
        }
    }

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public DockableContext getContext() {
        return context;
    }
    
    public DragManager getDragManager(MouseEvent ev) {
        return getContext().getDragManager();
    }
}
