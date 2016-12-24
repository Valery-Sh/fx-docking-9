package org.vns.javafx.dock.api;

import java.util.List;
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
import org.vns.javafx.dock.api.properties.DockableState;

/**
 *
 * @author Valery
 */
public class DragTransformer implements EventHandler<MouseEvent> {

    private final DockableState stateProperty;

    private final DragPopup popup;

    private Parent targetDockPane;

    private Stage resultStage;
//    MouseDragHandler dragHandler = new MouseDragHandler();

    private Point2D startMousePos;

    public DragTransformer(DockableState stateProperty) {
        this.stateProperty = stateProperty;
        this.popup = new DragPopup();
        //System.err.println("DragTransformer popup.isShowing()=" + popup.isShowing());
    }

    public void initialize() {

    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            addEventHandlers(newValue);
        }
    }

    private void removeEventHandlers_old(Node node) {
        node.setOnMousePressed(null);
        node.setOnDragDetected(null);
    }

    private void removeEventHandlers(Node titleBar) {
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    private void addEventHandlers_OLD(Node titleBar) {
        titleBar.setOnMousePressed(this::mousePressed);
        titleBar.setOnDragDetected(this::mouseDragDetected);
    }

    private void addEventHandlers(Node titleBar) {
        titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        titleBar.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
    }

    protected void mousePressed(MouseEvent ev) {
        Point2D p = stateProperty.getNode().localToScreen(0, 0);
        double x = p.getX() - ev.getScreenX();
        double y = p.getY() - ev.getScreenY();
        this.startMousePos = new Point2D(x, y);
    }

    protected void mouseReleased_old(MouseEvent ev) {
        ((Node) ev.getSource()).setMouseTransparent(false);
        //System.err.println("************** this Mouse Released source.class=" + ev.getSource().getClass().getName());
    }

    public void mouseDragged(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            return;
        }
        Insets insets = ((BorderPane) stateProperty.getNode().getScene().getRoot()).getInsets();

        Stage stage = (Stage) stateProperty.getNode().getScene().getWindow();
        stage.setX(ev.getScreenX() + startMousePos.getX() - insets.getLeft());
        stage.setY(ev.getScreenY() + startMousePos.getY() - insets.getTop());
        if (stateProperty.isFloating()) {
            //System.err.println("1 MOUSE DRAGGED resultStage = " + resultStage);
            if (popup.isShowing()) {
                popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
            }
            if (!popup.isShowing()) {
                resultStage = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
            }
            //System.err.println("2 MOUSE DRAGGED resuktStage = " + resultStage);
            //resultStage = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);

            if (resultStage == null) {
                return;
            }
            //System.err.println("MOUSE DRAGGED resultStage = " + resultStage);
            if (!popup.isShowing()) {
                Node root = resultStage.getScene().getRoot();
                if (root == null || !(root instanceof Pane)) {
                    return;
                }
                if ( ! (root instanceof DockPaneTarget)  ) {
                    root = (Pane) DockUtil.findDockPane((Pane) root, ev.getScreenX(), ev.getScreenY());
                    if ( root == null ) {
                        return;
                    }
                    System.err.println("0 findDockPane = " + root);                    
                }
                if ( ! ((DockPaneTarget)root).getDelegate().isUsedAsDockTarget() ) {
                    return;
                }
                System.err.println("1 findDockPane = " + root);
/*                if ((root instanceof DockPaneTarget) && ((DockPaneTarget) root).getDelegate().zorder() == 0) {
                    //System.err.println("FIND DOCKPANE: id=" + resultStage.getScene().getRoot().getId());
                } else {
                    List<Node> ls = DockUtil.findNodes(resultStage.getScene().getRoot(), (node) -> {
                        Point2D p = node.screenToLocal(ev.getScreenX(), ev.getScreenY());
                        return node.contains(p) && (node instanceof DockPaneTarget)
                                && ((DockPaneTarget) node).getDelegate().zorder() == 0;
                    });
                    if (!ls.isEmpty()) {
                        //System.err.println("FIND DOCKPANE: size=" + ls.size());
                        //System.err.println("FIND DOCKPANE: id=" + ls.get(0).getId());
                    } else {
                        //System.err.println("NOT FOUND DOCKPANE");
                    }
                }
                //System.err.println("DOCKPANE  111111 " + root);
*/                
                popup.show((Pane) root, stateProperty.getNode());
                //MouseEvent me = ev.copyFor(popup.getRoot(), popup.btnTop);
                // MouseEvent me = ev.copyFor(ev.getSource(), popup.btnTop);
                //popup.getRoot().fireEvent(me);
            }
            if (popup.isShowing()) {
                popup.handle(ev.getScreenX(), ev.getScreenY());
            }
        }
    }
    
    
    public void mouseDragged_new (MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            return;
        }
        Insets insets = ((BorderPane) stateProperty.getNode().getScene().getRoot()).getInsets();

        Stage stage = (Stage) stateProperty.getNode().getScene().getWindow();
        stage.setX(ev.getScreenX() + startMousePos.getX() - insets.getLeft());
        stage.setY(ev.getScreenY() + startMousePos.getY() - insets.getTop());
        if (stateProperty.isFloating()) {
            //System.err.println("1 MOUSE DRAGGED resultStage = " + resultStage);
            if (popup.isShowing()) {
                popup.hideWhenOut(ev.getScreenX(), ev.getScreenY());
            }
            if (!popup.isShowing()) {
                resultStage = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
            }
            //System.err.println("2 MOUSE DRAGGED resuktStage = " + resultStage);
            //resultStage = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);

            if (resultStage == null) {
                return;
            }
            //System.err.println("MOUSE DRAGGED resultStage = " + resultStage);
            if (!popup.isShowing()) {
                Node root = resultStage.getScene().getRoot();
                //DockUtil.
                if (root == null || !(root instanceof Pane)) {
                    return;
                }
                if ((root instanceof DockPaneTarget) && ((DockPaneTarget) root).getDelegate().zorder() == 0) {
                    //System.err.println("FIND DOCKPANE: id=" + resultStage.getScene().getRoot().getId());
                } else {
                    List<Node> ls = DockUtil.findNodes(resultStage.getScene().getRoot(), (node) -> {
                        Point2D p = node.screenToLocal(ev.getScreenX(), ev.getSceneY());
                        return node.contains(p) && (node instanceof DockPaneTarget)
                                && ((DockPaneTarget) node).getDelegate().zorder() == 0;
                    });
                }
                //System.err.println("DOCKPANE  111111 " + root);
                popup.show((Pane) root, stateProperty.getNode());
                //MouseEvent me = ev.copyFor(popup.getRoot(), popup.btnTop);
                // MouseEvent me = ev.copyFor(ev.getSource(), popup.btnTop);
                //popup.getRoot().fireEvent(me);
            }
            if (popup.isShowing()) {
                popup.handle(ev.getScreenX(), ev.getScreenY());
            }
        }
    }
    
    public void mouseReleased(MouseEvent ev) {
        System.err.println("1) ***************** mouseReleased SOURCE EVENT:" + ev.getSource().getClass().getName() + "; id = " + ((Node)ev.getSource()).getId());
        if (popup.isShowing()) {
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        if (stateProperty.isFloating() && popup.getDockPos() != null && popup.getDragTarget() != null) {
            System.err.println("!!!!!!!!!!! " + popup.getDragTarget());            
            if (popup.getDragTarget() instanceof DockPaneTarget) {
                System.err.println("!!!!!!!!!!!");
                //((Stage) stateProperty.getNode().getScene().getWindow()).close();
                //stateProperty.setFloating(false);
                DockPaneTarget dpt = (DockPaneTarget) popup.getDragTarget();
                dpt.dock(stateProperty.getNode(), popup.getDockPos());
            } else if (popup.getDragTarget() instanceof DockTarget) {
                //((Stage) stateProperty.getNode().getScene().getWindow()).close();
                //stateProperty.setFloating(false);
                DockTarget dt = (DockTarget) popup.getDragTarget();
                dt.dock(stateProperty.getNode(), popup.getDockPos());
            }

        }
        popup.hide();
    }

    protected void mouseDragDetected(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            stateProperty.setFloating(true);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            //targetDockPane.startFullDrag();
        } else {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            //((Node) ev.getSource()).startFullDrag();
        }
    }

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePressed(ev);
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            //System.err.println("DRAG DETECTED");
            mouseDragDetected(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            //System.err.println("MOUSE DRAGGED");  
            mouseDragged(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased(ev);
        }
    }

    /*    public class MouseDragHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(ev);
            }
            if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                mouseDragged(ev);
            }

        }

        public void mouseDragged(MouseEvent ev) {
            Insets insets = ((BorderPane) stateProperty.getNode().getScene().getRoot()).getInsets();

            Stage stage = (Stage) stateProperty.getNode().getScene().getWindow();
            stage.setX(ev.getScreenX() + startMousePos.getX() - insets.getLeft());
            stage.setY(ev.getScreenY() + startMousePos.getY() - insets.getTop());
            if (stateProperty.isFloating()) {
                Stage result = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
            }
        }

        public void mouseReleased(MouseEvent ev) {
            System.err.println("1) ***************** mouseReleased SOURCE EVENT:" + ev.getSource().getClass().getName());
            //((Node)ev.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            //((Node)ev.getSource()).removeEventFilter(MouseEvent.MOUSE_RELEASED, this);

            if (targetDockPane != null) {
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            }

        }

    }
     */
 /*    public static class DockInputEvent extends InputEvent {

        private MouseEvent mouseEvent;

        public static final EventType TYPE = new EventType("DOCK");

        public DockInputEvent(EventType<? extends InputEvent> eventType) {
            super(eventType);
        }

        public DockInputEvent() {
            super(TYPE);
        }

        public DockInputEvent(MouseEvent ev) {
            super(TYPE);
            this.mouseEvent = ev;
        }

        public DockInputEvent(Object source, EventTarget target, EventType<? extends InputEvent> eventType) {
            super(source, target, eventType);
        }

        public DockInputEvent(Object source, EventTarget target, MouseEvent ev) {
            super(source, target, TYPE);
            this.mouseEvent = ev;
        }

        public MouseEvent getMouseEvent() {
            return mouseEvent;
        }

    }

    public class DockInputEventHandler implements EventHandler<DockInputEvent> {

        @Override
        public void handle(DockInputEvent ev) {
            //out("DockInputHandler source Id=" + ((Node)ev.getSource()).getId() );
            out("===========================================");

            MouseEvent me = ev.getMouseEvent();
            StageRegistry.getInstance().getTarget(me.getScreenX(), me.getScreenY());

            Node n = ((Node) ev.getSource());
            String sep = System.lineSeparator();
            //String s = "me.getX()=" + me.getX() + sep;
            String s = "me.getScreenX()=" + me.getScreenX()
                    + "; stg02.getX()=" + stg02.getX()
                    + "; width=" + stg01.getWidth()
                    + sep;
            out(s);
            //s = "me.getY()=" + me.getY() + sep;
            s = "me.getScreenY()=" + me.getScreenY()
                    + "; stg02.getY()=" + stg02.getY()
                    + "; height=" + stg01.getHeight()
                    + sep;

            out(s);

        }

    }

    public static void out(String s) {
//        System.err.println(s);
    }
     */
}
