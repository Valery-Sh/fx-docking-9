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
package org.vns.javafx.designer;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class DragIndicatorManager implements IndicatorManager {

    private final LayoutContext targetContext;
    private final DragIndicator dragIndicator;
    
    public DragIndicatorManager(LayoutContext targetContext, DragIndicator indicator) {
        this.targetContext = targetContext;
        this.dragIndicator = indicator;
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
    public LayoutContext getTargetContext() {
        return targetContext;
    }

    public DragIndicator getDragIndicator() {
        return dragIndicator;
    }

    @Override
    public void handle(double screenX, double screenY) {
        showIndicator(screenX, screenY);
    }
    
    protected TreeItemEx getTargetTreeItem(Point2D screenPos , TreeItemEx item) {
        return dragIndicator.getTargetTreeItem(screenPos.getX(), screenPos.getY(), item);
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
    public void showIndicator() {
        //Point2D pos = getTargetNode().localToScreen(0, 0);
    }
    
    public void showIndicator(double mouseScreenX, double mouseScreenY) {
        TreeViewEx tv = getSceneGraphView().getTreeView(mouseScreenX, mouseScreenY);
        if ( tv == null ) {
            return;
        }
        if ( tv.getRoot() == null ) {
            dragIndicator.drawRectangle(tv); 
            return;
        }
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
    private SceneView getSceneGraphView() {
        return (SceneView)targetContext.getLayoutNode();
    }
    @Override
    public PositionIndicator getPositionIndicator() {
        return null;
    }
    /**
     * Check whether a rectangular indicator must be drawn around the specified
     * item.
     *
     * @param x x coordinate of the  mouse cursor
     * @param y y coordinate of the  mouse cursor
     * 
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
