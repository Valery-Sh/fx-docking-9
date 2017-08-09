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
package org.vns.javafx.dock.api;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class JFXDragManager implements DragManager, EventHandler<MouseEvent> {

    public static String DRAG_PANE_KEY = "JFXPANEL:drag-source-pane";
    public static String DRAG_FLOATING_STAGE = "JFXPANEL:drag-floating-stage";

    /**
     * The stage that contains the target dock target
     */
    private Window resultStage;
    
    //private Window floatPopup;
    /**
     * The object to be dragged
     */
    private final Dockable dockable;
    /**
     * Pop up window which provides indicators to choose a place of the target
     * object
     */
    private IndicatorPopup popup;

    //private Popup popupDelegate;
    /**
     * The target dock target
     */
    private Parent targetDockPane;
    /**
     * The stage that contains the target dock target
     */
    //private Window jfxWindow;
    /**
     * The stage that contains the target dock target
     */
    //private Stage resultStage;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;
    /**
     * The property that defines a node that can be used to start dragging.
     */
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     */
    public JFXDragManager(Dockable dockNode) {
        this.dockable = dockNode;
        init();
    }

    private void init() {
        //jfxWindow = dockable.node().getScene().getWindow();
        //System.err.println("!!!!!!!!!!!!!!!! " + jfxWindow);
        dragNode.addListener(this::dragNodeChanged);
    }

    /**
     * Is called when a new value of {@link #dragNode } is detected. Removes
     * mouse listeners of the old drag node and assigns listeners to the new
     * drag node.
     *
     * @param ov doesn't used
     * @param oldValue the old drag node
     * @param newValue the new drag node
     */
    public void dragNodeChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            System.err.println("=== NULL dragNodeChanged " + oldValue);
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            System.err.println("=== NOT NULL dragNodeChanged " + newValue);
            addEventHandlers(newValue);
        }
    }

    /**
     * Returns the property object that represents a drag node.
     *
     * @return the property object that represents a drag node.
     */
    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    /**
     * Returns an object of type {@code Node} which is used as a drag node.
     *
     * @return an object of type {@code Node}
     */
    public Node getDragNode() {
        return dragNode.get();
    }

    /**
     * Sets an object of type {@code Node} which can be used as a drag node.
     *
     * @param dragNode a node which becomes a drag node
     */
    public void setDragNode(Node dragNode) {
        this.dragNode.set(dragNode);
    }

    /**
     * A handler function with is called when the title bar of the
     * {@code dockable} object changes.
     *
     * @param ov doesn't used
     * @param oldValue the old value of the object which represents a title bar
     * @param newValue the new value of the object which represents a title bar
     */
    @Override
    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            System.err.println("=== NULL titleBarChanged " + oldValue);
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            System.err.println("=== NOT NULL titleBarChanged " + newValue);
            addEventHandlers(newValue);
        }
    }

    @Override
    public void removeEventHandlers(Node titleBar) {
        if ( titleBar == null ) {
            return;
        }
        
        titleBar.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    public void addEventHandlers(Node titleBar) {
        if ( titleBar == null ) {
            return;
        }
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);

    }

    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    @Override
    public void mousePressed(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            return;
        }
        Point2D p = dockable.node().localToScreen(0, 0);
        startMousePos = new Point2D(ev.getX(), ev.getY());
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
    public void mouseDragged(MouseEvent ev) {

        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isFloating()) {
            return;
        }

        double leftDelta = 0;
        double topDelta = 0;
        
        Popup floatPopup = (Popup) dockable.node().getScene().getWindow();
        
        if (floatPopup != null && dockable.dockableController().isFloating() && (floatPopup instanceof Popup)
                && (((Popup) floatPopup).getContent().get(0) instanceof BorderPane)) {
            System.err.println(" INSETS insets insets");
            Insets insets = ((BorderPane) ((Popup) floatPopup).getContent().get(0)).getInsets();
            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }


        floatPopup.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
        floatPopup.setY(ev.getScreenY() - topDelta - startMousePos.getY());

        if (popup != null && popup.isShowing()) {
            popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());            
        }
        //Window resultStage = null;
        if (popup == null || !popup.isShowing()) {
            Scene sc = dockable.node().getScene();
            
            if ( sc != null ) {
                Window w = sc.getWindow();
                System.err.println("MOUEDRAGED TEST WIN popup = " + w);
                System.err.println("MOUEDRAGED TEST WIN floatPopup = " + floatPopup);

            }
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), dockable.node().getScene().getWindow());
        }

        if (resultStage == null) {
            return;
            //resultStage = jfxWindow;
        }

        Node root = null;

        root = resultStage.getScene().getRoot();
        
        root.localToScreen(root.getBoundsInLocal());
        if (!root.localToScreen(root.getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }

        if (root == null || !(root instanceof Pane) && !(DockRegistry.isDockTarget(root))) {
            return;
        }

        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.isDockTarget(n);
        });

        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.isDockTarget(root)) {
            return;
        }

        if (!DockRegistry.dockTarget(root).targetController().isAcceptable(dockable.node())) {
            return;
        }

        if (!DockRegistry.dockTarget(root).targetController().isUsedAsDockTarget()) {
            return;
        }

        IndicatorPopup newPopup = DockRegistry.dockTarget(root).targetController().getIndicatorPopup();
        if (popup != newPopup && popup != null) {
            popup.hide();
        }
        if (newPopup == null) {
            return;
        }
        popup = newPopup;

        if (!popup.isShowing()) {
            popup.showPopup();
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
    @Override
    public void mouseReleased(MouseEvent ev) {
        
        if (popup != null && popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if (popup != null && popup.isShowing()) {
            popup.getTargetController().dock(pt, dockable);
        } else if (popup != null && popup.getPositionIndicator() == null) {
            //
            // We use default indicatorPopup without position indicator
            //
            popup.getTargetController().dock(pt, dockable);
        }

        if (popup != null && popup.isShowing()) {
            popup.hide();
        }
    }

    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     * {@code dockable} objects is in a floating state and if not invokes the
     * method {@link DockableController#setFloating(boolean,boolean...) } with
     * an argument set to {@code true}.
     *
     * @param ev the event that describes the mouse events.
     */
    public void mouseDragDetected(MouseEvent ev) {
        System.err.println("***** mouseDragDetected " + ev.getSource());        
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isDraggable()) {
            ev.consume();
            return;
        }
         System.err.println("mouseDragDetected 1 targetDockPane isFloating = " + dockable.dockableController().isFloating());            

        if (!dockable.dockableController().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            
            dockable.dockableController().setFloatingAsPopup(true);
            //System.err.println(" --- mouseDragDetected floatPopup = " + floatPopup);            

            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        //ev.consume();
    }

    @Override
    public void hideFloatingStage(Window floatStage) {
        System.err.println("HIDE FLOATING STAGE");
        floatStage.hide();
    }

    /**
     * The implementation of the interface {@code EventHandler<MouseEvent> }.
     * Depending of the event type invokes one of the methods
     * <ul>
     * <li>{@link #mousePressed(javafx.scene.input.MouseEvent)}</li>
     * <li>{@link #mouseReleased(javafx.scene.input.MouseEvent) }
     * <li>{@link #mouseDragDetected(javafx.scene.input.MouseEvent)}</li>
     * <li>{@link #mouseDragged(javafx.scene.input.MouseEvent)}</li>
     * </ul>
     *
     * @param ev the event that describes the mouse events.
     */
    /*    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            mouseDragDetected(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            mouseDragged(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased(ev);
        }
    }
     */
}
