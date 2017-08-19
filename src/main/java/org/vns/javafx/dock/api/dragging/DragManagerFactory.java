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

import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragManager;

/**
 *
 * @author Valery
 */
public class DragManagerFactory {
    
    public DragManager getDragManager(Dockable dockable) {
        DragManager retval = null;
        DockTargetController dtc = dockable.dockableController().getTargetController();
        if ( dtc != null  ) {
            if ( dtc.getDragType() == DragType.SIMPLE ) {
                retval = new SimpleDragManager(dockable);
            } else if ( dtc.getDragType() == DragType.DRAG_AND_DROP ) {
                retval = new DragAndDropManager(dockable);
            }
        }
        if ( retval == null ) {
            retval = new SimpleDragManager(dockable);
        }
        return retval;
    }
    public static DragManagerFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }
    private static class SingletonInstance {
        private static DragManagerFactory INSTANCE = new DragManagerFactory();
    }
}
