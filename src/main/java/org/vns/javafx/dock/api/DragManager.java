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

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public interface DragManager extends EventHandler<MouseEvent> {



    /**
     * Is called when a new value of {@code dragNode } is detected. Removes
     * mouse listeners of the old drag node and assigns listeners to the new
     * drag node.
     *
     * @param ov doesn't used
     * @param oldValue the old drag node
     * @param newValue the new drag node
     */
    default void dragNodeChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            addEventHandlers(newValue);
        }
    }

    /**
     * Returns the property object that represents a drag node.
     *
     * @return the property object that represents a drag node.
     */
     //ObjectProperty<Node> dragNodeProperty();

    /**
     * Returns an object of type {@code Node} which is used as a drag node.
     *
     * @return an object of type {@code Node}
     */
     Node getDragNode();

    /**
     * Sets an object of type {@code Node} which can be used as a drag node.
     *
     * @param dragNode a node which becomes a drag node
     */
    void setDragNode(Node dragNode);
    
    void hideFloatingStage(Window floatStage);

    void removeEventHandlers(Node titleBar);


    void addEventHandlers(Node titleBar);
    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    void mousePressed(MouseEvent ev);
    void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue);
    
/*{
        if (!ev.isPrimaryButtonDown()) {
            //ev.consume();
            return;
        }
        System.err.println("mousePressed " + ev.getSource());
        Point2D p = dockable.node().localToScreen(0, 0);
        startMousePos = new Point2D(ev.getX(), ev.getY());
    }
*/
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

    void mouseDragged(MouseEvent ev);/* {
        System.err.println("MOUSE DRAGGED");

        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isFloating()) {
            return;
        }

        double leftDelta = 0;
        double topDelta = 0;
        //
        // The stage where the floating dockable resides may have a root node as a Borderpane
        //
        if (dockable.node().getScene().getRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();
            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }

        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
        stage.setY(ev.getScreenY() - topDelta - startMousePos.getY());
        System.err.println("MOUSE DRAGGED 1");

        if (popup != null && popup.isShowing()) {
            popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }
        System.err.println("MOUSE DRAGGED 2");

        if (popup == null || !popup.isShowing()) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
        }
        System.err.println("MOUSE DRAGGED 3");

        if (resultStage == null) {
            return;
        }
        System.err.println("MOUSE DRAGGED 4");

        Node root = resultStage.getScene().getRoot();
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
        //14.05 DockTargetController ph = DockRegistry.dockTarget(root).targetController();

        if (!popup.isShowing()) {
            popup.showPopup();
        }
        if (popup == null) {
            return;
        }
        popup.handle(ev.getScreenX(), ev.getScreenY());
    }
*/
    /*    public void mouseDragged1(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        
        if (!dockable.dockableController().isFloating()) {
            return;
        }

        double leftDelta = 0;
        double topDelta = 0;

        if (dockable.node().getScene().getRoot() instanceof BorderPane) {
            Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();
            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }

        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
        stage.setY(ev.getScreenY() - topDelta - startMousePos.getY());
        
        if (popup != null && popup.isShowing()) {
            popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
        }

        if (ev.isControlDown() && popupDelegate == null && popup != null) {
            popup.hide();
            //popupDelegate = DockRedirector.show(popup.getTargetNode());
        } else if (!ev.isControlDown() && popupDelegate != null) {
            popupDelegate = null;
        }

        if (popup == null || !popup.isShowing()) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
        }

        if (resultStage == null) {
            return;
        }

        Node root = resultStage.getScene().getRoot();
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
        //14.05 DockTargetController ph = DockRegistry.dockTarget(root).targetController();

        if (!popup.isShowing()) {
            popup.showPopup();
        }
        if ( popup == null ) {
            return;
        }
        popup.handle(ev.getScreenX(), ev.getScreenY());
    }
     */
    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the target object is detected during dragging
     * the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
     void mouseReleased(MouseEvent ev);/* {
        if (popup != null && popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
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
     */

    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     * {@code dockable} objects is in a floating state and if not invokes the
     * method {@link DockableController#setFloating(boolean,boolean...) } with an argument
     * set to {@code true}.
     *
     * @param ev the event that describes the mouse events.
     */
     void mouseDragDetected(MouseEvent ev);/* {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isDraggable()) {
            ev.consume();
            return;
        }

        if (!dockable.dockableController().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            System.err.println("1 targetDockPane = " + ev.getSource());            
            dockable.dockableController().setFloating(true);
            Platform.runLater(() -> {

                
            //    Window stage = (Window) dockable.node().getScene().getWindow();
            //    stage.setX(ev.getScreenX() + 400);
            //    stage.setY(ev.getScreenY() + 500);
            });
            
            System.err.println("targetDockPane = " + targetDockPane);
            //dockable.node().setMouseTransparent(true);
//            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        //ev.consume();
    }
*/
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
    @Override
    default void handle(MouseEvent ev) {
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

}
