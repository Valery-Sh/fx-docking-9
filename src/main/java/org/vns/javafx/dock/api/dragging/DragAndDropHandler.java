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
package org.vns.javafx.dock.api.dragging;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.ItemValue;
import org.vns.javafx.dock.api.editor.TreeItemEx;

/**
 *
 * @author Valery
 */
public class DragAndDropHandler implements EventHandler<DragEvent> {

    private DragManager dragManager;

    public DragAndDropHandler(DragManager dragManager) {
        this.dragManager = dragManager;
    }

    @Override
    public void handle(DragEvent ev) {
        if (ev.getEventType() == DragEvent.DRAG_OVER) {
            //if (isAdmissiblePosition(ev)) {
            ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            System.err.println("DRAG OVER");
            //    drawIndicator(ev);
            //}
            ev.consume();
        } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
            System.err.println("DRAG DROPPED");
            //
            // Transfer the data to the place
            //
//            if (isAdmissiblePosition(ev)) {
            ev.setDropCompleted(true);

//            } else {
//                ev.setDropCompleted(false);
//            }
        } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
            System.err.println("DRAG DONE");
            //getEditor().getDragIndicator().hideDrawShapes();
        }
        ev.consume();
    }

}
