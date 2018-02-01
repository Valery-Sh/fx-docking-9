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
        System.err.println("Start TreeItemDragManager DRAG DETECTED NOT FLOATING");
        if ( true ) {
//            return;
        }
        setStartMousePos(startMousePos);
        setDragSource((Node) ev.getSource());
        
        if (!getDockable().getDockableContext().isFloating()) {
            System.err.println("TreeItem DRAG DETECTED NOT FLOATING");
            setTargetDockPane(((Node) ev.getSource()).getScene().getRoot());
            //FloatView view = FloatViewFactory.getInstance().getFloatView(this);
            FloatViewFactory f = null;
            if ( getDockable().getDockableContext().getTargetContext() != null ) {
                f = getDockable().getDockableContext().getTargetContext().getLookup().lookup(FloatViewFactory.class);
            }
            if ( f == null ) {
                f = getDockable().getDockableContext().getLookup().lookup(FloatViewFactory.class);
            }
            FloatView view = f.getFloatView(this);
            
            setFloatingWindow((Window) view.make(getDockable()));
            System.err.println("**************** dragDetected  floatingWindow");
            //dockable.getDockableContext().setFloating(true);
            
            getTargetDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            getTargetDockPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            getDockable().getDockableContext().setFloating(true);

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
        if ( getIndicatorManager() != null && !(getIndicatorManager() instanceof Window)) {
            getIndicatorManager().hide();
        }
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!getDockable().getDockableContext().isFloating()) {
            return;
        }
        //
        // The floatingWindow where the floating dockable resides may have a root node as a Borderpane
        //

        //Window floatingWindow = (Window) dockable.node().getScene().getWindow();
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
        System.err.println("======== " + getFloatingWindow().isShowing() + " =========================");
        System.err.println("   --- startPosition x=" + getStartMousePos().getX() + "; y=" + getStartMousePos().getY());
        System.err.println("   --- mousePos      x=" + ev.getScreenX() + "; y=" + ev.getScreenY());
        System.err.println("   --- windowPos     x=" + getFloatingWindow().getX() + "; y=" + getFloatingWindow().getY());
        System.err.println("   --- leftDelta = " + leftDelta + "; topDelta=" + topDelta);
        System.err.println("=================================");
        
        //System.err.println("IndicatorManager = " + indicatorManager);
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
/*        System.err.println("ROOT = " + root);
        System.err.println("DockRegistry.dockTarget(root) = " + DockRegistry.dockTarget(root));
        System.err.println(" DockRegistry.dockTarget(root).getTargetContext() = " + DockRegistry.dockTarget(root).getTargetContext());
        
        System.err.println("dockable = " + dockable);
*/        
        //System.err.println("dockable.node() = " + dockable.node());
        if (!DockRegistry.dockTarget(root).getTargetContext().isAcceptable(getDockable().node())) {
            return;
        }
        //System.err.println("IS ACCEPTABLE");        
        if (!DockRegistry.dockTarget(root).getTargetContext().isUsedAsDockTarget()) {
            return;
        }
        //
        // Start use of IndicatorPopup
        //
        IndicatorManager newPopup =  DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorManager.class);
        if ( newPopup == null ) {
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
        //System.err.println("SimplaDragManager 4  ! before handle" );
        
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
    protected void mouseReleased(MouseEvent ev) {
        System.err.println("mouse release popup " + getIndicatorManager());
        if ( getIndicatorManager() != null ) {
            System.err.println("   --- mouse release isShowing " + getIndicatorManager().isShowing());
        }
        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            System.err.println("11111 release isShowing");
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
            //dockable.getDockableContext().setFloating(false);
            getIndicatorManager().getTargetContext().dock(pt, getDockable());
            System.err.println("22222222 doc");

        } else if (getIndicatorManager() != null && getIndicatorManager().getPositionIndicator() == null) {
            //
            // We use default indicatorPopup without position indicator
            //
            getIndicatorManager().getTargetContext().dock(pt, getDockable());
            System.err.println("33333 dock");
            
        }

        if (getIndicatorManager() != null && getIndicatorManager().isShowing()) {
            getIndicatorManager().hide();
        }
    }
    
}
