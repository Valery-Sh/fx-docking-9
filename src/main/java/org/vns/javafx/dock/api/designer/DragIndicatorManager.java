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
package org.vns.javafx.dock.api.designer;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class DragIndicatorManager implements IndicatorManager {

    private final TargetContext targetContext;
    private final DragIndicator dragIndicator;
    
    public DragIndicatorManager(TargetContext targetContext, DragIndicator dragIndicator) {
        this.targetContext = targetContext;
        this.dragIndicator = dragIndicator;
    }

    private Node draggedNode;

    @Override
    public Node getDraggedNode() {
        return draggedNode;
    }

    @Override
    public void setDraggedNode(Node draggedNode) {
        this.draggedNode = draggedNode;
    }

    @Override
    public TargetContext getTargetContext() {
        return targetContext;
    }

    public DragIndicator getDragIndicator() {
        return dragIndicator;
    }


    @Override
    public void handle(double screenX, double screenY) {
    }

    @Override
    public void hide() {
        dragIndicator.hideDrawShapes();
    }

    @Override
    public boolean isShowing() {
        return dragIndicator.isShowing();
    }

    @Override
    public void showIndicator(double mouseScreenX, double mouseScreenY) {
        TreeItemEx toItem =  getSceneGraphView().getTreeItem(mouseScreenX, mouseScreenY);
        if ( toItem == null ) {
            return;
        }
        TreeItemEx fromItem = dragIndicator.getTargetTreeItem(mouseScreenX, mouseScreenY, toItem);
        if ( fromItem == null ) {
            return;
        }
        
        if ( toItem == fromItem && isRectangleIndicator(mouseScreenX, mouseScreenY, toItem)) {
            dragIndicator.drawRectangle(toItem);
        } else {
            dragIndicator.drawLines(fromItem, toItem);
        }
    }
    private SceneGraphView getSceneGraphView() {
        return (SceneGraphView)targetContext.getTargetNode();
    }
    @Override
    public PositionIndicator getPositionIndicator() {
        return null;
    }
    /**
     * Check whether a rectangular indicator must be drawn around the specified
     * item.
     *
     * @param ev the processed event
     * @param place the item the rectangle indicator may be drawn around.
     * @return true if a rectangular indicator must be drawn. false otherwise
     */
    protected boolean isRectangleIndicator(double x, double y, TreeItemEx place) {
        boolean retval = false;
        Bounds[] bounds = dragIndicator.levelBoundsOf(place);
        int n = -1;
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].contains(x,y)) {
                n = i;
                break;
            }
        }

        if (n < 0) {
            retval = true;
        } else if (!place.isExpanded()) {
            int level = getSceneGraphView().getTreeView().getTreeItemLevel(place);
            if (n == level || n == level + 1 || n == level + 2) {
                retval = true;
            }
        }
        return retval;
    }

}
