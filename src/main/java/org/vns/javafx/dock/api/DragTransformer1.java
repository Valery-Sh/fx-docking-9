package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DragTransformer1 {

    private final StateProperty stateProperty;
    
    private Parent targetDockPane;

    MouseDragHandler dragHandler = new MouseDragHandler();

    private Point2D startMousePos;

    public DragTransformer1(StateProperty stateProperty) {
        this.stateProperty = stateProperty;
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


    private void removeEventHandlers(Node node) {
        node.setOnMousePressed(null);
        node.setOnDragDetected(null);
    }

    private void addEventHandlers(Node titleBar) {
        titleBar.setOnMousePressed(this::mousePressed);
        titleBar.setOnDragDetected(this::mouseDragDetected);
    }

    protected void mousePressed(MouseEvent ev) {
        
        Point2D p = stateProperty.getNode().localToScreen(0,0);
        double x = p.getX() - ev.getScreenX();
        double y = p.getY() - ev.getScreenY();
        
        //this.startMousePos = new Point2D(ev.getX(), ev.getY());
        this.startMousePos = new Point2D(x,y);
        if (stateProperty.isFloating()) {
            ((Node) ev.getSource()).setMouseTransparent(true);
        } else {
            ((Node) ev.getSource()).setMouseTransparent(false);
        }
        System.err.println("*************** Mouse Pressed source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseReleased(MouseEvent ev) {
        ((Node) ev.getSource()).setMouseTransparent(false);
        
        System.err.println("************** this Mouse Released source.class=" + ev.getSource().getClass().getName());
    }


    protected void mouseDragDetected(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            targetDockPane = ((Node)ev.getSource()).getScene().getRoot();
            stateProperty.setFloating(true);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, dragHandler);
            //targetDockPane.startFullDrag();
            System.err.println("1) source = " + ((Node)ev.getSource()).getScene().getRoot().getClass().getName());
        } else {
            ((Node)ev.getSource()).setMouseTransparent(true);
            targetDockPane = ((Node)ev.getSource()).getScene().getRoot();
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, dragHandler);
            //((Node) ev.getSource()).startFullDrag();
            
            System.err.println("2) source = " + ((Node)ev.getSource()).getScene().getRoot().getClass().getName());
        }
    }

    public class MouseDragHandler implements EventHandler<MouseEvent> {

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
            Insets insets = ((BorderPane)stateProperty.getNode().getScene().getRoot()).getInsets();
            
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
