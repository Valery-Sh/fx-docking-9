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
package org.vns.javafx.dock.api;

import java.util.Set;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.dragging.DragManager;

/**
 *
 * @author Valery Shyshkin
 */
public class TabPaneMouseDragHandler extends DefaultMouseDragHandler {

    public TabPaneMouseDragHandler(DockableContext context) {
        super(context);
    }

    @Override
    public void mousePressed(MouseEvent ev) {
        setStartMousePos(null);
        Point2D pos = new Point2D(ev.getX(), ev.getY());
        
        if (!ev.isPrimaryButtonDown() || getHeaderArea(ev) == null) {
            return;
        }
        if (getHeadersRegion(ev) != null) {
            Tab tab = getTab(ev);
            Node tabNode = tab.getTabPane().lookup("." + getUUIDStyle(tab));
            getContext().setDragContainer(new DragContainer(getContext().dockable(), tab));
            WritableImage wi = null;

            if (tabNode != null) {
                wi = tabNode.snapshot(null, null);
                if (wi != null) {
                    Node node = new ImageView(wi);
                    node.setOpacity(0.75);
                    getContext().getDragContainer().setGraphic(node);
                }

            }
            pos = tabNode.screenToLocal(ev.getScreenX(), ev.getScreenY());
            
        }
        setStartMousePos(pos);
    }

    private String getUUIDStyle(Tab tab) {
        String retval = null;
        for (String s : tab.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                retval = s;
                break;
            }
        }
        return retval;
    }

    protected Node getHeaderArea(MouseEvent ev) {
        //System.err.println("getHeaderArea");
        Node retval = getContext().getDragNode().lookup(".tab-header-area");
        //System.err.println("getHeaderArea node=" + retval);
        if (retval == null || !DockUtil.contains(retval, ev.getScreenX(), ev.getScreenY())) {
            retval = null;
        }
        //System.err.println("getHeaderArea retvale=" + retval);
        return retval;
    }

    protected Node getHeadersRegion(MouseEvent ev) {

        Node retval = getContext().getDragNode().lookup(".headers-region");
        //System.err.println("getHeaderArea node=" + retval);

        if (retval == null || !DockUtil.contains(retval, ev.getScreenX(), ev.getScreenY())) {
            retval = null;
        }
        //System.err.println("getHeaderArea retval=" + retval);

        return retval;
    }

    protected Tab getTab(MouseEvent ev) {
        Tab retval = null;
        Set<Node> set = getContext().getDragNode().lookupAll(".tab");
        Node tabNode = null;
        for (Node node : set) {
            if (DockUtil.contains(node, ev.getScreenX(), ev.getScreenY())) {
                tabNode = node;
                break;
            }
        }
        String style = null;
        if (tabNode != null) {
            for (String s : tabNode.getStyleClass()) {
                if (s.startsWith("tab-uuid-")) {
                    style = s;
                    break;
                }
            }
            TabPane pane = (TabPane) getContext().getDragNode();
            for (Tab tab : pane.getTabs()) {
                //System.err.println("tab.id = " + tab.getId());
                for (String s : tab.getStyleClass()) {
                    //System.err.println("   --- style = " + s);
                }
            }

            if (style != null) {
                for (Tab tab : pane.getTabs()) {
                    for (String s : tab.getStyleClass()) {
                        if (s.equals(style)) {
                            retval = tab;
                            break;
                        }
                    }
                }
            }
        }
        return retval;
    }

/*    @Override
    public void mouseDragDetected(MouseEvent ev) {
        System.err.println("TabPaneMouseDragHandler: mouseDragDetected startMousePos = " + getStartMousePos());

        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        Dockable dockable = getContext().dockable();
        if (!getContext().isDraggable()) {
            ev.consume();
            return;
        }
        //
        // NEW
        //
        DragManager dm = getDragManager(ev);
        // END NEW
        if (!dockable.getContext().isFloating()) {
            System.err.println("TabPaneMouseDragHandler: not isFloating");
            dm.mouseDragDetected(ev, getStartMousePos());
        } else {
            System.err.println("TabPaneMouseDragHandler: isFloating");
            DragContainer dc = dockable.getContext().getDragContainer();
            if ((dc != null)) {
                System.err.println("TabPaneMouseDragHandler: isFloating dc != null");
                Dockable.of(dc.getGraphic()).getContext().getDragManager().mouseDragDetected(ev, getStartMousePos());
            } else {
                System.err.println("TabPaneMouseDragHandler: isFloating dc == null");
                dm.mouseDragDetected(ev, getStartMousePos());
            }

        }

    }
*/
    public DragManager getDragManager(MouseEvent ev) {
        DragManager dm;
        if (getHeaderArea(ev) != null && getHeadersRegion(ev) == null) {
            dm = getContext().getDragManager(true);
        } else {
            dm = getContext().getDragManager();
        }
        return dm;
    }
}
