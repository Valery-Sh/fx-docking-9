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

import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DragContainer;
/**
 *
 * @author Valery Shyshkin
 */
public class DefaultMouseDragHandler extends MouseDragHandler {

    public DefaultMouseDragHandler(DockableContext context) {
        super(context);
    }

    @Override
    public void mouseDragDetected(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        Dockable dockable = getContext().dockable();
        if (!getContext().isDraggable()) {
            ev.consume();
            return;
        }

        DragManager dm = getDragManager(ev);
        
        if (!dockable.getContext().isFloating()) {
            dm.mouseDragDetected(ev, getStartMousePos());
        } else {
            DragContainer dc = dockable.getContext().getDragContainer();
            if ( (dc != null) && dc.getPlaceholder() != null) {
                Dockable.of(dc.getPlaceholder()).getContext().getDragManager().mouseDragDetected(ev, getStartMousePos());
            } else {
                dm.mouseDragDetected(ev, getStartMousePos());
            }
        }

    }
}
