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
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Transform;
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
        //startMousePos = new Point2D(ev.getX(), ev.getY());
        startMousePos = new Point2D(ev.getX(), ev.getY());
/*        Bounds b = context.getDragNode().getBoundsInParent();        
        
        System.err.println("MouseDragHandler: transf = " + context.getDragNode().getTransforms() );
        Transform tr = context.getDragNode().getLocalToParentTransform();
        System.err.println("MouseDragHandler: localTo Parentt ransf class= " + context.getDragNode().getLocalToParentTransform().getClass().getName() );
        System.err.println("MouseDragHandler: localTo Parentt ransf = " + context.getDragNode().getLocalToParentTransform() );
        System.err.println("MouseDragHandler: localTo Scene transf = " + context.getDragNode().getLocalToSceneTransform() );

        System.err.println("MouseDragHandler: layouBounds = " + context.getDragNode().getLayoutBounds() );
        System.err.println("MouseDragHandler: boundsInParent = " + context.getDragNode().getBoundsInParent() );        
        
        System.err.println("MouseDragHandler: startMousePos = " + startMousePos);
        //System.err.println("MouseDragHandler: MOUSE PRESSED dockable=" + context.dockable().node());
        //System.err.println("MouseDragHandler: MOUSE PRESSED dockable=" + context.getTargetContext().getTargetNode());        
*/
        ev.consume();
        
    }

  

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            mouseDragDetected(ev);
        }
        ev.consume();

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
