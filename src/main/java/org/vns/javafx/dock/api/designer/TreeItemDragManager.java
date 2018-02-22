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

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.ObjectReceiver;

import org.vns.javafx.dock.api.TopNodeHelper;
import org.vns.javafx.dock.api.dragging.SimpleDragManager;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.indicator.IndicatorManager;

/**
 *
 * @author Valery Shyshkin
 */
public class TreeItemDragManager extends SimpleDragManager {

    public TreeItemDragManager(Dockable dockNode) {
        super(dockNode);
    }

    @Override
    public void mouseDragDetected(MouseEvent ev, Point2D startMousePos) {
        setStartMousePos(startMousePos);
        setDragSource((Node) ev.getSource());

        if (!getDockable().getContext().isFloating()) {
            setTargetDockPane(((Node) ev.getSource()).getScene().getRoot());
            FloatViewFactory f = null;
            if (getDockable().getContext().getTargetContext() != null) {
                f = getDockable().getContext().getTargetContext().getLookup().lookup(FloatViewFactory.class);
            }
            if (f == null) {
                f = getDockable().getContext().getLookup().lookup(FloatViewFactory.class);
            }
            FloatView view = f.getFloatView(this);

            getTargetDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            getTargetDockPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        } else {
            //
            // If floating window contains snapshot and not the dockable then
            // the folowing twp operator must be skipped
            //
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            ((Node) ev.getSource()).addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }

    }

    /**
     * The method is called when the user moves the mouse and the primary mouse
     * button is pressed. The method checks whether the {@literal  dockable} node
     * is in the {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock target target} then it shows a pop up window which contains
     * indicators to select a dock place on the target dock node or target.
     * <p>
     * The method checks whether the {@code control key} of the keyboard is
     * pressed and if so then it shows a special indicator window which allows
     * to select a dock target or one of it's parents.
     *
     * @param ev the event that describes the mouse events
     */
    @Override
    protected void mouseDragged(MouseEvent ev) {
        if (getIndicatorManager() != null && !(getIndicatorManager() instanceof Window)) {
            getIndicatorManager().hide();
        }
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!getDockable().getContext().isFloating()) {
            return;
        }
        //
        // The floatingWindow where the floating dockable resides may have a root node as a Borderpane
        //

        //Window floatingWindow = (Window) dockable.node().getScene().getFloatingWindow();
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
        if (getFloatingWindow() instanceof PopupControl) {
            ((PopupControl) getFloatingWindow()).setAnchorX(ev.getScreenX() - leftDelta - getStartMousePos().getX());
            ((PopupControl) getFloatingWindow()).setAnchorY(ev.getScreenY() - topDelta - getStartMousePos().getY());

        } else {
            getFloatingWindow().setX(ev.getScreenX() - leftDelta - getStartMousePos().getX());
            getFloatingWindow().setY(ev.getScreenY() - topDelta - getStartMousePos().getY());
        }
        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            getIndicatorManager().hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if ((getIndicatorManager() == null || !getIndicatorManager().isShowing())) {
            setResultStage(DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), getFloatingWindow()));
        }

        if (getResultStage() == null) {
            return;
        }
        Node root = getResultStage().getScene().getRoot();
        if (root == null || !(root instanceof Pane) && !(DockRegistry.instanceOfDockTarget(root))) {
            return;
        }

        Node topPane = TopNodeHelper.getTopNode(getResultStage(), ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.instanceOfDockTarget(n);
        });

        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.instanceOfDockTarget(root)) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isAcceptable(getDockable())) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isUsedAsDockTarget()) {
            return;
        }
        //
        // Start use of IndicatorPopup
        //
        IndicatorManager newPopup = DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorManager.class);
        if (newPopup == null) {
            DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorManager.class);
        }
        if (newPopup == null) {
            return;
        }

        newPopup.setDraggedNode(getDockable().node());

        if (getIndicatorManager() != newPopup && getIndicatorManager() != null) {
            getIndicatorManager().hide();
        }
        setIndicatorManager(newPopup);

        if (!getIndicatorManager().isShowing()) {
            getIndicatorManager().showIndicator();
            getIndicatorManager().showIndicator(ev.getScreenX(), ev.getScreenY());
        }
        if (getIndicatorManager() == null) {
            return;
        }
        getIndicatorManager().handle(ev.getScreenX(), ev.getScreenY());
    }

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    @Override
    protected void mouseReleased(MouseEvent ev) {
        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            getIndicatorManager().handle(ev.getScreenX(), ev.getScreenY());
        }

        if (getTargetDockPane() != null) {
            getTargetDockPane().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            getTargetDockPane().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            getTargetDockPane().removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            getTargetDockPane().removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            getTargetDockPane().removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        if (getDragSource() != null) {
            getDragSource().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            getDragSource().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            getDragSource().removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            getDragSource().removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }

        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            getIndicatorManager().getTargetContext().dock(pt, getDockable());
        } else if (getIndicatorManager() != null && getIndicatorManager().getPositionIndicator() == null) {
            //
            // We use default indicatorPopup without position indicator
            //
            getIndicatorManager().getTargetContext().dock(pt, getDockable());            
        }

        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            getIndicatorManager().hide();
        }
    }

    protected void dockObject(Point2D mousePos, Dockable d) {
        Object dragObject = getDockable().getContext().getDragContainer();
        if (getDockable().node() != dragObject) {
            if (dragObject instanceof Dockable) {
                getIndicatorManager().getTargetContext().dock(mousePos, (Dockable) dragObject);
            } else if (getIndicatorManager().getTargetContext() instanceof ObjectReceiver) {
                ((ObjectReceiver) getIndicatorManager().getTargetContext()).dockObject(mousePos, (Dockable) dragObject);
            }
        } else {
            getIndicatorManager().getTargetContext().dock(mousePos, getDockable());
        }
    }
}
