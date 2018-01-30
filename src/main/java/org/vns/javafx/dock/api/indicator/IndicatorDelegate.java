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
package org.vns.javafx.dock.api.indicator;

import javafx.scene.Node;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery Shyshkin
 */
public interface IndicatorDelegate {

    TargetContext getTargetContext();


    Node getDraggedNode();

    void setDraggedNode(Node draggedNode);
    /**
     * The method is called when the the mouse moved during drag operation.
     *
     * @param screenX a screen mouse position
     * @param screenY a screen mouse position
     */
    void handle(double screenX, double screenY);
    void hide();
    boolean isShowing();
    boolean hideWhenOut(double x, double y);
    void showIndicator();
    PositionIndicator getPositionIndicator();
}
