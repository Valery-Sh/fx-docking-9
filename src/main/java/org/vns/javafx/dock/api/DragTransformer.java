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
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class DragTransformer implements EventHandler<MouseEvent> {

    private final Dockable dockable;

    private DragPopup popup;

    private Parent targetDockPane;

    private Stage resultStage;
//    MouseDragHandler dragHandler = new MouseDragHandler();

    //private Point2D startMousePos;
    private Point2D eventSourceOffset;

    private ObjectProperty<Node> dragSourceProperty = new SimpleObjectProperty<>();

    public DragTransformer(Dockable nodeHandler) {
        this.dockable = nodeHandler;
        init();
    }

    private void init() {
        dragSourceProperty.addListener(this::dragSourceChanged);
    }

    protected void dragSourceChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            addEventHandlers(newValue);
        }
    }

    public ObjectProperty<Node> dragSourceProperty() {
        return dragSourceProperty;
    }

    public Node getDragSource() {
        return dragSourceProperty.get();
    }

    public void setDragSource(Node dragSource) {
        dragSourceProperty.set(dragSource);
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
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    private void addEventHandlers(Node titleBar) {
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    protected void mousePressed(MouseEvent ev) {
        Point2D p = dockable.node().localToScreen(0, 0);
        eventSourceOffset = new Point2D(ev.getX(), ev.getY());
    }

    public void mouseDragged(MouseEvent ev) {
        if (!dockable.nodeHandler().isFloating()) {
            return;
        }
        Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();

        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() - insets.getLeft() - eventSourceOffset.getX());
        stage.setY(ev.getScreenY() - insets.getTop() - eventSourceOffset.getY());

        if (dockable.nodeHandler().isFloating()) {
            if (popup != null && popup.isShowing()) {
                popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
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
            Node topPane = (Pane) DockUtil.findTopDockPane((Pane) root, ev.getScreenX(), ev.getScreenY());
            if (topPane != null) {
                root = topPane;
            } else if (!(root instanceof DockPaneTarget)) {
                return;
            }
            if (!((DockPaneTarget) root).paneHandler().isUsedAsDockTarget()) {
                return;
            }
            DragPopup newPopup = ((DockPaneTarget) root).paneHandler().getDragPopup();
            if (popup != newPopup && popup != null) {
                popup.hide();
                
            }
            popup = newPopup;
            popup.show((Pane) root, dockable.node());
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }
    }

    public void mouseReleased(MouseEvent ev) {
        if (popup != null && popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
        if ( popup != null ) {
            System.err.println("poput gragTarget="+popup.getDragTarget());
        } else {
            System.err.println("poput = NULL"  );
        }
        if (dockable.nodeHandler().isFloating() && popup != null && popup.getDockPos() != null && popup.getDragTarget() != null) {
            if (popup != null && (popup.getDragTarget() instanceof DockPaneTarget)) {
                DockPaneTarget dpt = (DockPaneTarget) popup.getDragTarget();
                dpt.paneHandler().dock(pt, dockable.node(), popup.getDockPos());
//31.12            } else if (popup != null && (popup.getDragTarget() instanceof Dockable)) {
            } else if (popup != null && DockRegistry.isDockable(popup.getDragTarget()) ) {
                //31.12Dockable dt = (Dockable) popup.getDragTarget();
                Dockable dt = DockRegistry.dockable(popup.getDragTarget());
                dt.nodeHandler().getPaneHandler().dock(pt, dockable.node(), popup.getDockPos(), dt);
            }
        }
        
        if (popup != null) {
            popup.hide();
        }
    }

    protected void mouseDragDetected(MouseEvent ev) {

        if (!dockable.nodeHandler().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            dockable.nodeHandler().setFloating(true);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        } else {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
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

    /**
     *
     */
    @FunctionalInterface
    public interface SidePointerModifier {

        /**
         *
         * @param mouseX
         * @param mouseY
         * @param target
         * @return null than a default position of node indicator is used or a
         * new position of node indicator
         */
        Point2D modify(DragPopup popup, Dockable target, double mouseX, double mouseY);
    }

}
