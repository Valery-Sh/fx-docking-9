package org.vns.javafx.dock.api;

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
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class DragManager implements EventHandler<MouseEvent> {

    private final Dockable dockable;

    private DragPopup popup;
    
    private DockRedirector popupDelegate;
    
    private Parent targetDockPane;

    private Stage resultStage;

    private Point2D startMousePos;

    private ObjectProperty<Node> dragNodeProperty = new SimpleObjectProperty<>();

    public DragManager(Dockable nodeHandler) {
        this.dockable = nodeHandler;
        init();
    }

    private void init() {
        dragNodeProperty.addListener(this::dragNodeChanged);
    }

    protected void dragNodeChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            addEventHandlers(newValue);
        }
    }

    public ObjectProperty<Node> dragNodeProperty() {
        return dragNodeProperty;
    }

    public Node getDragNode() {
        return dragNodeProperty.get();
    }

    public void setDragNode(Node dragNode) {
        dragNodeProperty.set(dragNode);
    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            addEventHandlers(newValue);
        }
    }

    private void removeEventHandlers(Node titleBar) {
        //titleBar.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
        //titleBar.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
        //titleBar.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        //titleBar.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    private void addEventHandlers(Node titleBar) {
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    boolean removed = false;

    /**
     * The method is called when the user presses a primary mouse button.
     * Saves the screen position of the mouse screen cursor.
     * @param ev the event that describes the mouse events
     */
    protected void mousePressed(MouseEvent ev) {
        Point2D p = dockable.node().localToScreen(0, 0);
        startMousePos = new Point2D(ev.getX(), ev.getY());
    }
    /**
     * The method is called when the user moves the mouse and the primary mouse 
     * button is pressed.
     * The method checks whether the {@literal  dockable} node is in the
     * {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock target pane} then it shows a pop up window which 
     * contains indicators to select a dock place on the target dock node or pane.
     * <p> The method checks whether the {@code control key} of the keyboard 
     * is pressed and if so then it shows a special indicator window witch
     * allows to select a dock pane or one of it's parents.
     * 
     * @param ev the event that describes the mouse events
     */
    public void mouseDragged(MouseEvent ev) {

        if (!dockable.nodeHandler().isFloating()) {
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

        if ( ev.isControlDown() && popupDelegate != null && ! popupDelegate.contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }
        if ( ev.isControlDown() && popupDelegate == null && popup != null)  {
            popup.hide();
            popupDelegate = DockRedirector.show(popup.getDockPane());
        } else if ( ! ev.isControlDown() && popupDelegate != null ) {
            popupDelegate.close();
            popupDelegate = null;
        }

        if (popup == null || !popup.isShowing()) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
        }

        if (resultStage == null) {
            return;
        }
        
        Node root = resultStage.getScene().getRoot();
        if (root == null || !(root instanceof Pane)) {
            return;
        }
        Node topPane = TopNodeHelper.getTopNode(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
            return DockRegistry.isDockPaneTarget(n);
        });

        if (topPane != null) {
            root = topPane;
        } else if (!DockRegistry.isDockPaneTarget(root)) {
            return;
        }
        if (! DockRegistry.dockPaneTarget(root).paneHandler().isUsedAsDockTarget()) {
            return;
        }

        DragPopup newPopup = DockRegistry.dockPaneTarget(root).paneHandler().getDragPopup();
        if (popup != newPopup && popup != null) {
            popup.hide();
        }
        if ( newPopup == null ) {
            return;
        }
        popup = newPopup;
        PaneHandler ph = DockRegistry.dockPaneTarget(root).paneHandler();
        if ( ev.isControlDown() ) {
            ph.getPaneIndicator().onShown(null, null);
        }
        popup.show(dockable.node());
        popup.handle(ev.getScreenX(), ev.getScreenY());
    }

    public void mouseReleased(MouseEvent ev) {
        if (popup != null && popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());

        if (dockable.nodeHandler().isFloating() && popup != null && popup.getDockPos() != null && popup.getDragTarget() != null) {
            popup.getPaneHandler().dock(pt, dockable.node(), popup.getTargetNodeSidePos(), popup.getTargetPaneSidePos(), popup.getDragTarget());
        }

        if (popup != null && popup.isShowing()) {
            popup.hide();
        }
        if ( popupDelegate != null ) {
            popupDelegate.close();
            popupDelegate = null;
        }
    }

    protected void mouseDragDetected(MouseEvent ev) {
        if (!dockable.nodeHandler().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            dockable.nodeHandler().setFloating(true);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
    }

    @Override
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

}
