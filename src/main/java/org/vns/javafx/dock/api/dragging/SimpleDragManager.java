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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.TopNodeHelper;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.indicator.IndicatorDelegate;

/**
 * The class manages the process of dragging of the object of type
 * {@link Dockable}} from the moment you press the mouse button and ending by
 * initiation docking operations.
 *
 * The objects of typo {@code Dockable} can have a title bar. It is an object of
 * type {@code Region}, which is assigned by calling the method 
 DockableContext.setTitleBar(javafx.scene.layout.Region)  or by
 applying the method DockableContext.createDefaultTitleBar(java.lang.String). 
 The title bar object automatically becomes a listener of mouse events by
 executing the code below:
 <pre>
 *   titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED,  this);
 *   titleBar.addEventHandler(MouseEvent.DRAG_DETECTED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
 * </pre> Thus, if the object of type {@code Dockable} has a title bar and it is
 * visible on screen, then it can be used to perform mouse dragging.
 * <p>
 * The object of type {@code Dockable} has a method DockableContext#setDragNode(javafx.scene.Node)
 . The {@code Node } which has been set by the method may be used to drag the
 * {@literal dockable} in the same manner as the title bar is used. Thus, both
 * objects, such as a title bar and a drag node can be used to perform dragging.
 *
 * </p>
 *
 * @author Valery Shyshkin
 */
public class SimpleDragManager implements DragManager, EventHandler<MouseEvent> {

    /**
     * The object to be dragged
     */
    private final Dockable dockable;
    
    private Node dragSource;
    /**
     * Pop up window which provides indicators to choose a place of the target
     * object
     */
    private IndicatorDelegate popup;

    //private IndicatorDelegate indicatorDelegate;

    /**
     * The target dock target
     */
    private Parent targetDockPane;
    /**
     * The floatWindow that contains the target dock target
     */
    private Window resultStage;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;
    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     */
    public SimpleDragManager(Dockable dockNode) {
        this.dockable = dockNode;
    }

    @Override
    public DragType getDragType() {
        return DragType.SIMPLE;
    }

    @Override
    public void mouseDragDetected(MouseEvent ev, Point2D startMousePos) {
        
        this.startMousePos = startMousePos;
        this.dragSource = (Node) ev.getSource();
        
        if (!dockable.getDockableContext().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            FloatView view = FloatViewFactory.getInstance().getFloatView(this);
            view.make(dockable);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            dockable.getDockableContext().setFloating(true);

        } else {
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
    protected void mouseDragged(MouseEvent ev) {

        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.getDockableContext().isFloating()) {
            return;
        }
        //
        // The floatWindow where the floating dockable resides may have a root node as a Borderpane
        //

        Window floatWindow = (Window) dockable.node().getScene().getWindow();
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
        if (floatWindow instanceof PopupControl) {
            ((PopupControl) floatWindow).setAnchorX(ev.getScreenX() - leftDelta - startMousePos.getX());
            ((PopupControl) floatWindow).setAnchorY(ev.getScreenY() - topDelta - startMousePos.getY());

        } else {
            floatWindow.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
            floatWindow.setY(ev.getScreenY() - topDelta - startMousePos.getY());
        }
        /*System.err.println("=================================");
        System.err.println("   --- startPosition x=" + startMousePos.getX() + "; y=" + startMousePos.getY());
        System.err.println("   --- mousePos      x=" + ev.getScreenX() + "; y=" + ev.getScreenY());
        System.err.println("   --- windowPos     x=" + floatWindow.getX() + "; y=" + floatWindow.getY());
        System.err.println("   --- leftDelta = " + leftDelta + "; topDelta=" + topDelta);
        System.err.println("=================================");
         */

        if (popup != null && popup.isShowing()) {
            popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if ((popup == null || !popup.isShowing())) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), floatWindow);
        }

        if (resultStage == null) {
            return;
        }
        Node root = resultStage.getScene().getRoot();
        if (root == null || !(root instanceof Pane) && !(DockRegistry.instanceOfDockTarget(root))) {
            return;
        }

        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.instanceOfDockTarget(n);
        });

        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.instanceOfDockTarget(root)) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isAcceptable(dockable.node())) {
            return;
        }
        if (!DockRegistry.dockTarget(root).getTargetContext().isUsedAsDockTarget()) {
            return;
        }
        //
        // Start use of IndicatorPopup
        //
        
        IndicatorDelegate newPopup =  DockRegistry.dockTarget(root).getTargetContext().getLookup().lookup(IndicatorDelegate.class);
        newPopup.setDraggedNode(getDockable().node());

        if (popup != newPopup && popup != null) {
            popup.hide();
        }
        if (newPopup == null) {
            return;
        }
        popup = newPopup;

        if (!popup.isShowing()) {
            popup.showIndicator();
        }
        if (popup == null) {
            return;
        }
        popup.handle(ev.getScreenX(), ev.getScreenY());
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
        if (popup != null && popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        if (dragSource != null) {
            dragSource.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if (popup != null && popup.isShowing()) {
            popup.getTargetContext().dock(pt, dockable);
        } else if (popup != null && popup.getPositionIndicator() == null) {
            //
           // We use default indicatorPopup without position indicator
            //
            popup.getTargetContext().dock(pt, dockable);
        }

        if (popup != null && popup.isShowing()) {
            popup.hide();
        }
    }

    @Override
    public Dockable getDockable() {
        return dockable;
    }
  
    protected Node getFloatingWindowRoot() {
        if (dockable.node().getScene() == null || dockable.node().getScene().getWindow() == null) {
            return null;
        }
        Node r = dockable.node().getScene().getRoot();
        return r;
    }

    @Override
    public void handle(MouseEvent ev) {
        if ( ev.getEventType().equals(MouseEvent.MOUSE_DRAGGED) ) {
            mouseDragged(ev);
        } else if ( ev.getEventType().equals(MouseEvent.MOUSE_RELEASED) ) {
            mouseReleased(ev);
        }
    }
}
