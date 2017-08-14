package org.vns.javafx.dock.api;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

/**
 * The class manages the process of dragging of the object of type
 * {@link Dockable}} from the moment you press the mouse button and ending by
 * initiation docking operations.
 *
 * The objects of typo {@code Dockable} can have a title bar. It is an object of
 * type {@code Region}, which is assigned by calling the method 
 * {@link DockableController#setTitleBar(javafx.scene.layout.Region) } or by
 * applying the method {@link DockableController#createDefaultTitleBar(java.lang.String)
 * }. The title bar object automatically becomes a listener of mouse events by
 * executing the code below:
 * <pre>
 *   titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED,  this);
 *   titleBar.addEventHandler(MouseEvent.DRAG_DETECTED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
 * </pre> Thus, if the object of type {@code Dockable} has a title bar and it is
 * visible on screen, then it can be used to perform mouse dragging.
 * <p>
 * The object of type {@code Dockable} has a method {@link DockableController#setDragNode(javafx.scene.Node)
 * }. The {@code Node } which has been set by the method may be used to drag the
 * {@literal dockable} in the same manner as the title bar is used. Thus, both
 * objects, such as a title bar and a drag node can be used to perform dragging.
 *
 * </p>
 *
 * @author Valery Shyshkin
 */
public class FxDragManager implements DragManager, EventHandler<MouseEvent> {

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
     * The floatWindow that contains the target dock target
     */
    private Window resultStage;
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
    public FxDragManager(Dockable dockNode) {
        this.dockable = dockNode;
        init();
    }

    private void init() {
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

    @Override
    public void removeEventHandlers(Node node) {
        if (node == null) {
            return;
        }

        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
        node.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
        node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        node.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    @Override
    public void addEventHandlers(Node node) {
        if (node == null) {
            return;
        }

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        node.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, this);

    }

    /**
     * The method is called when the user presses a primary mouse button. Saves
     * the screen position of the mouse screen cursor.
     *
     * @param ev the event that describes the mouse events
     */
    @Override
    public void mousePressed(MouseEvent ev) {
        System.err.println("MOUSE PRESSED");
        if (!ev.isPrimaryButtonDown()) {
            return;
        }

        startMousePos = new Point2D(ev.getX(), ev.getY());
        startScreenMousePos = new Point2D(ev.getScreenX(), ev.getScreenY());
    }
    public Point2D startScreenMousePos;

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
System.err.println("MOUSE DRAGGED");        
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!dockable.dockableController().isFloating()) {
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
            targetDockPane.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if (popup != null && popup.isShowing()) {
            popup.getTargetController().dock(pt, dockable);
        } else if (popup != null && popup.getPositionIndicator() == null) {
            //
            // We use default indicatorPopup without position indicator
            //
            popup.getTargetController().dock(pt, dockable);
        } else {
            /*            Window w = dockable.node().getScene().getWindow();
            if (w instanceof Popup) {
                Popup p = (Popup) w;
                FloatStageBuilder b = new FloatStageBuilder(dockable.dockableController());
                b.makeFloating(dockable, false);
                b.getFloatingWindow().setX(p.getX());
                b.getFloatingWindow().setY(p.getY());
                p.hide();
                ((Stage) b.getFloatingWindow()).show();
          }
             */
        }

        if (popup != null && popup.isShowing()) {
            popup.hide();
        }
    }

    /**
     * The method is called when the the drag-detected event is generated once
     * after the mouse is dragged. The method checks whether the
     * {@code dockable} objects is in a floating state and if not invokes the
     * method {@link DockableController#setFloating(boolean) } with an argument
     * set to {@code true}.
     *
     * @param ev the event that describes the mouse events.
     */
    @Override
    public void mouseDragDetected(MouseEvent ev) {
System.err.println("MOUSE DRAG DETECTED " + dockable.dockableController().isFloating());
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
            setFloating(true);
            System.err.println("1 MOUSE DRAG DETECTED " + dockable.dockableController().isFloating());
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
    }

    protected Dockable getDockable() {
        return dockable;
    }

    protected void setFloating(boolean floating) {
        dockable.dockableController().setFloating(true);
    }

    protected Node getFloatingWindowRoot() {
        if (dockable.node().getScene() == null || dockable.node().getScene().getWindow() == null) {
            return null;
        }
        Node r = dockable.node().getScene().getRoot();
        return r;
    }

    @Override
    public void hideFloatingStage(Window floatStage) {
        floatStage.hide();
    }

}
