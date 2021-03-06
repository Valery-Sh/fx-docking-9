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

import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class JFXDragManager extends SimpleDragManager {

    /**
     * Create a new instance for the given dockable.
     *
     * @param dockable the object to be dragged
     */
    public JFXDragManager(Dockable dockable) {
        super(dockable);
    }


}
