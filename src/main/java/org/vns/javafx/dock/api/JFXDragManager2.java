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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class JFXDragManager2 implements DragManager, EventHandler<MouseEvent> {

    public static String DRAG_PANE_KEY = "JFXPANEL:drag-source-pane";
    public static String DRAG_FLOATING_STAGE = "JFXPANEL:drag-floating-stage";
    
    private Window floatPopup; 
    
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
    private Parent targetPane;
    /**
     * The stage that contains the target dock target
     */
    private Window jfxWindow;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;
    /**
     * The property that defines a node that can be used to start dragging.
     */
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();
    private final Stage floatStage;

    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     * @param targetPane target node
     */
    public JFXDragManager2(Dockable dockNode, Parent targetPane, Stage floatStage) {
        this.dockable = dockNode;
        this.floatStage = floatStage;
        this.targetPane = targetPane;
        init();
    }

    private void init() {
        System.err.println("JFXDragManager targetPane = " + targetPane);
        System.err.println("JFXDragManager targetPane.getScene = " + targetPane.getScene());
        jfxWindow = targetPane.getScene().getWindow();
        setDragNode(targetPane);
        dragNodeChanged(dragNode, null, targetPane);
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
    @Override
    public void dragNodeChanged(ObservableValue ov, Node oldValue, Node newValue) {
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
    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    /**
     * Returns an object of type {@code Node} which is used as a drag node.
     *
     * @return an object of type {@code Node}
     */
    @Override
    public Node getDragNode() {
        return dragNode.get();
    }

    /**
     * Sets an object of type {@code Node} which can be used as a drag node.
     *
     * @param dragNode a node which becomes a drag node
     */
    @Override
    public void setDragNode(Node dragNode) {
        this.dragNode.set(dragNode);
    }

    @Override
    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
    }

    @Override
    public void removeEventHandlers(Node node) {
        if ( node == null ) {
            return;
        }
        
        node.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        node.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
        node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        node.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);

    }

    @Override
    public void addEventHandlers(Node node) {
        if ( node == null ) {
            return;
        }
        
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        node.addEventFilter(MouseEvent.DRAG_DETECTED, this);
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        node.addEventFilter(MouseEvent.MOUSE_RELEASED, this);

    }

    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    public void mousePressed(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            //ev.consume();
            return;
        }
        System.err.println("mousePressed " + ev.getSource());
        Point2D p = getDragNode().localToScreen(0, 0);
        startMousePos = new Point2D(ev.getX(), ev.getY());
        
        if (targetPane != null) {
            System.err.println("\"mousePressed \" " + targetPane);
            targetPane.addEventFilter(MouseEvent.DRAG_DETECTED, this);
            targetPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
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
    public void mouseDragged(MouseEvent ev) {
        //System.err.println("MOUSE DRAGGED");

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
        if (dockable.node().getScene() == null) {
            //dockable.dockableController().setFloating(false);
            //dockable.dockableController().setFloating(true);

        }
        System.err.println("MouseDragged popup dockable.scene=" + dockable.node().getScene());
        if ( dockable.node().getScene() != null ) {
            System.err.println("MouseDragged popup dockable.scene.window=" + dockable.node().getScene().getWindow());            
            System.err.println("MouseDragged popup PROP=" + dockable.node().getScene().getProperties().get("POPUP"));                        
        }        
        
/*        Parent p = dockable.node().getParent();
        while ( p != null ) {
            System.err.println("PARENT in Popup = " + p.getClass().getName());
            p = p.getParent();
        }
*/        
        if (dockable.dockableController().isFloating() && (floatPopup instanceof Popup)
                && ( ((Popup)floatPopup).getContent() instanceof BorderPane)) {
            Insets insets = ((BorderPane) ((Popup)floatPopup).getContent()).getInsets();
            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }

        //
        // get floating stage
        //
        //Window stage = (Stage) dockable.node().getScene().getWindow();
        floatPopup.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
        floatPopup.setY(ev.getScreenY() - topDelta - startMousePos.getY());

        if (popup != null && popup.isShowing()) {
            System.err.println("\"mouseDragged 1\" x=" + ev.getScreenX());
            //System.err.println("");
            System.err.println(" --- hidden=" + popup.hideWhenOut(ev.getScreenX(), ev.getScreenY()));
        }
        System.err.println("MOUSE DRAGGED 2");

        Window resultStage = null;
        if (popup == null || !popup.isShowing()) {
            //resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), (Stage) stage);
        }

        if (resultStage == null) {
            System.err.println("mouseDragget jfxWindow=" + jfxWindow);
            resultStage = jfxWindow;

        }

        System.err.println("MOUSE DRAGGED 3 resultStage=" + resultStage);
        Node root = null;

        root = resultStage.getScene().getRoot();
        root.localToScreen(root.getBoundsInLocal());
        //if ( ! root.getBoundsInParent().contains(ev.getScreenX(), ev.getScreenY()) ) {
        if (!root.localToScreen(root.getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }
        System.err.println("MOUSE DRAGGED 3.1 resultStage=" + resultStage);

        if (root == null || !(root instanceof Pane) && !(DockRegistry.isDockTarget(root))) {
            System.err.println("MOUSE DRAGGED 4 root=" + root);
            return;
        }
        System.err.println("MOUSE DRAGGED 5");

        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.isDockTarget(n);
        });

        if (topPane != null) {
            root = topPane;
            System.err.println("MOUSE DRAGGED 6");
        } else if (!DockRegistry.isDockTarget(root)) {
            System.err.println("MOUSE DRAGGED 7");
            return;
        }

        if (!DockRegistry.dockTarget(root).targetController().isAcceptable(dockable.node())) {
            System.err.println("MOUSE DRAGGED 8");
            return;
        }
        // System.err.println("MOUSE DRAGGED 7");

        if (!DockRegistry.dockTarget(root).targetController().isUsedAsDockTarget()) {
            return;
        }
        System.err.println("MOUSE DRAGGED 9");

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
        //System.err.println("MOUSE DRAGGED 9");

        if (popup == null) {
            return;
        }
        System.err.println("MOUSE DRAGGED 10");

        popup.handle(ev.getScreenX(), ev.getScreenY());

    }

    public void mouseDragged1(MouseEvent ev) {
        //System.err.println("MOUSE DRAGGED");

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
        if (dockable.node().getScene() == null) {
            //dockable.dockableController().setFloating(false);
            //dockable.dockableController().setFloating(true);

        }
        /*        if (dockable.dockableController().isFloating()
                && (dockable.node().getScene().getRoot() instanceof BorderPane)) {
            Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();
            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
         */
        //
        // get floating stage
        //
        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() - leftDelta - startMousePos.getX());
        stage.setY(ev.getScreenY() - topDelta - startMousePos.getY());

        if (popup != null && popup.isShowing()) {
            System.err.println("\"mouseDragged 1\" x=" + ev.getScreenX());
            //System.err.println("");
            System.err.println(" --- hidden=" + popup.hideWhenOut(ev.getScreenX(), ev.getScreenY()));
        }
        System.err.println("MOUSE DRAGGED 2");

        Window resultStage = null;
        if (popup == null || !popup.isShowing()) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), (Stage) stage);
        }

        if (resultStage == null) {
            resultStage = jfxWindow;

        }

        System.err.println("MOUSE DRAGGED 3 resultStage=" + resultStage);
        Node root = null;

        root = resultStage.getScene().getRoot();
        root.localToScreen(root.getBoundsInLocal());
        //if ( ! root.getBoundsInParent().contains(ev.getScreenX(), ev.getScreenY()) ) {
        if (!root.localToScreen(root.getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }
        System.err.println("MOUSE DRAGGED 3.1 resultStage=" + resultStage);

        if (root == null || !(root instanceof Pane) && !(DockRegistry.isDockTarget(root))) {
            System.err.println("MOUSE DRAGGED 4 root=" + root);
            return;
        }
        System.err.println("MOUSE DRAGGED 5");

        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.isDockTarget(n);
        });

        if (topPane != null) {
            root = topPane;
            System.err.println("MOUSE DRAGGED 6");
        } else if (!DockRegistry.isDockTarget(root)) {
            System.err.println("MOUSE DRAGGED 7");
            return;
        }

        if (!DockRegistry.dockTarget(root).targetController().isAcceptable(dockable.node())) {
            System.err.println("MOUSE DRAGGED 8");
            return;
        }
        // System.err.println("MOUSE DRAGGED 7");

        if (!DockRegistry.dockTarget(root).targetController().isUsedAsDockTarget()) {
            return;
        }
        System.err.println("MOUSE DRAGGED 9");

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
        //System.err.println("MOUSE DRAGGED 9");

        if (popup == null) {
            return;
        }
        System.err.println("MOUSE DRAGGED 10");

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

        if (targetPane != null) {
            targetPane.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
            targetPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
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
    @Override
    public void mouseDragDetected(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isDraggable()) {
            ev.consume();
            return;
        }

        if (!dockable.dockableController().isFloating()) {
            //System.err.println("1 targetDockPane isShowing= " + floatStage);
            //System.err.println("2 targetDockPane isShowing= " + floatStage.isShowing());

            //dockable.dockableController().setFloating(true, floatStage);
            
            floatPopup = dockable.dockableController().setFloatingAsPopup(true);
            //((Popup)floatPopup).show(((Popup)floatPopup).getOwnerWindow());
            //System.err.println("DRAD DETECTED popup.isShowing()=" + floatPopup.isShowing());            
            System.err.println("DRAD DETECTED popup.isShowing()=" + floatPopup.isShowing());
            System.err.println("DRAD DETECTED popup.X=" + floatPopup.getX() + "; Y=" + floatPopup.getY());            
            System.err.println("DRAD DETECTED popup.width=" + floatPopup.getWidth()+ "; height=" + floatPopup.getHeight());                        
            //resultStage.requestFocus();
            //dockable.dockableController().getTargetController().undock(dockable.node());
            Platform.runLater(() -> {
                //floatStage.show();
                //    Window stage = (Window) dockable.node().getScene().getWindow();
                //    stage.setX(ev.getScreenX() + 400);
                //    stage.setY(ev.getScreenY() + 500);
            });

            System.err.println("targetDockPane = " + targetPane);
            //dockable.node().setMouseTransparent(true);
//            targetPane = ((Node) ev.getSource()).getScene().getRoot();
            //targetPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            //targetPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        //ev.consume();
    }

    @Override
    public void hideFloatingStage(Window floatStage) {
        //floatStage.hide();
        System.err.println("JFX HIDE FLOATING STAGE");
            floatStage.setWidth(1);
            floatStage.setHeight(1);
            Platform.runLater(()->{
                Scale sc  = new Scale(0.01, 0.01);
                floatStage.getScene().getRoot().getTransforms().add(sc);
                floatStage.setOpacity(0);
            });
        if (floatStage instanceof Stage) {
            ((Stage) floatStage).setMaxWidth(40);
            ((Stage) floatStage).setMaxHeight(40);
        } else {
            floatStage.setWidth(-1);
            floatStage.setHeight(-1);
        }
    }

}
