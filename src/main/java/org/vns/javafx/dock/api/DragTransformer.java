package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.demo.TestIfStageActive;
import static org.vns.javafx.dock.api.demo.TestIfStageActive.stg01;
import static org.vns.javafx.dock.api.demo.TestIfStageActive.stg02;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DragTransformer {

    private final StateProperty stateProperty;
    private Node titleBar;
    public Node dockableNode;
    private Parent targetDockPane;

    EventHandler<MouseEvent> mouseMoveHandler;

    MouseDragHandler targetDockPaneHandler = new MouseDragHandler();

    List<Dockable> dockableList = new ArrayList<>();

    private Point2D startMousePos;

    public DragTransformer(StateProperty stateProperty) {
        this.stateProperty = stateProperty;
    }

    public void initialize() {

    }

/*    public Pane getTargetDockPane() {
        return targetDockPane;
    }

    public void setTargetDockPane(Pane targetDockPane) {
        this.targetDockPane = targetDockPane;
    }
*/
    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        System.err.println("DragTransformer titlebarChanged");
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            titleBar = newValue;
            addEventHandlers(newValue);
        }

        if (oldValue != null && newValue == null) {
            // getChildren().remove(oldValue);
        } else if (oldValue != null && newValue != null) {
            //getChildren().set(0,newValue);
        } else if (oldValue == null && newValue != null) {
            //getChildren().add(0,newValue);
        }
    }

    private void removeEventHandlers(MouseEvent ev) {
        removeEventHandlers((Node) ev.getSource());
    }

    private void removeEventHandlers(Node node) {
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
        node.setOnDragDetected(null);
        node.setOnMouseReleased(null);
    }

    private void addEventHandlers(Node titleBar) {
        titleBar.setOnMousePressed(this::mousePressed);
        titleBar.setOnDragDetected(this::mouseDragDetected);
        //titleBar.setOnMouseReleased(this::mouseReleased);

    }

/*    private void addDockableEventHandlers(Region root) {
        List<Dockable> list = DockUtil.getAllDockable(root);
        list.forEach(d -> {
            Node n = (Node) d;
            //System.err.println("addDockableEventHandlers n.id= " + n.getId());
            if (!"ddt01".equals(n.getId())) {
                //((Node) d).setOnMouseDragged(this::mouseOnDockableDragged); 

            }
            if ("ddt02".equals(n.getId())) {
                System.err.println("addDockableEventHandlers n.id= " + n.getId());
                dockableNode = n;
                //n.addEventHandler(DockInputEvent.TYPE, new DockInputEventHandler());
            }

            //            ((Node) d).setOnMouseDragEntered(this::mouseOnDockableEntered);
            //((Node) d).setOnMouseDragged(this::mouseOnDockableDragged); 
            //((Node) d).setOnMouseMoved(this::mouseOnDockableDragged);            
            //((Node) d).setOnMouseDragExited(this::mouseOnDockableExited);
            //((Node) d).setOnMouseExited(this::mouseOnDockableExited);
            //((Node) d).setOnMouseEntered(this::mouseOnDockableEntered);
             
        });
    }

    private void removeDockableEventHandlers(Node titleBar) {

    }
*/
    protected void mousePressed(MouseEvent ev) {
        
        Point2D p = stateProperty.getNode().localToScreen(0,0);
        double x = p.getX() - ev.getScreenX();
        double y = p.getY() - ev.getScreenY();
        
        //this.startMousePos = new Point2D(ev.getX(), ev.getY());
        this.startMousePos = new Point2D(x,y);
        System.err.println("titleBar == (Node) ev.getSource()" + (ev.getSource() == titleBar));
        if (stateProperty.isFloating()) {
            ((Node) ev.getSource()).setMouseTransparent(true);
        } else {
            ((Node) ev.getSource()).setMouseTransparent(false);
        }

        //TestIfStageActive.frontStage.toFront();
        System.err.println("Mouse Pressed source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseReleased(MouseEvent ev) {
        ((Node) ev.getSource()).setMouseTransparent(false);
        
        System.err.println("this Mouse Released source.class=" + ev.getSource().getClass().getName());
    }


    protected void mouseDragDetected(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            targetDockPane = ((Node)ev.getSource()).getScene().getRoot();
            stateProperty.setFloating(true);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, targetDockPaneHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, targetDockPaneHandler);
            targetDockPane.startFullDrag();
            System.err.println("1) source = " + ((Node)ev.getSource()).getScene().getRoot().getClass().getName());

        //System.err.println("1) mouseDragDetected SOURCE EVENT:" + ev.getSource().getClass().getName());
            
        } else {
            ((Node)ev.getSource()).setMouseTransparent(true);
            targetDockPane = ((Node)ev.getSource()).getScene().getRoot();
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, targetDockPaneHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, targetDockPaneHandler);
            ((Node) ev.getSource()).startFullDrag();

        //System.err.println("2) mouseDragDetected SOURCE EVENT:" + ev.getSource().getClass().getName());
            
//            System.err.println("stateProperty.getPaneDelegate().getOriginalDockPane().id=" + stateProperty.getOrigionalPaneDelegate().getDockPane().getId());
            System.err.println("2) source = " + ((Node)ev.getSource()).getScene().getRoot().getClass().getName());
        }
    }
/*    protected void mouseDragDetected_OLD(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            targetDockPane = stateProperty.getPaneDelegate().getDockPane();
            stateProperty.setFloating(true);
            //System.err.println("targetDockPane.id=" + targetDockPane.getId());
            addDockableEventHandlers(targetDockPane);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, targetDockPaneHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, targetDockPaneHandler);
        } else {
            //
            // DockPane changed if we start dragging on a docked node
            //

            stateProperty.getTitleBar().setMouseTransparent(true);

            targetDockPane = stateProperty.getPaneDelegate().getDockPane();

            //targetDockPane.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, this::mouseEntered);            
            targetDockPane.setOnMouseDragEntered(this::mouseEntered);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, targetDockPaneHandler);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, targetDockPaneHandler);
            addDockableEventHandlers(stateProperty.getOrigionalPaneDelegate().getDockPane());
            ((Node) ev.getSource()).startFullDrag();

            System.err.println("stateProperty.getPaneDelegate().getOriginalDockPane().id=" + stateProperty.getOrigionalPaneDelegate().getDockPane().getId());
            System.err.println("source = " + ev.getSource().getClass().getName());
        }
    }
*/
    public class MouseDragHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent ev) {
            //System.err.println("!!!! Mouse MouseDragHandler " + ev.getEventType() );

            if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(ev);
            }
            if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                mouseDragged(ev);
            }

        }

        public void mouseDragged(MouseEvent ev) {
            //System.err.println("!!!!!!!!!!!! Mouse MouseDragHandler  w=");            
/*            Window w = null;
            if (ev.getSource() instanceof Node) {
                w = ((Node) ev.getSource()).getScene().getWindow();
            } else if (ev.getSource() instanceof Stage) {
                w = ((Stage) ev.getSource()).getOwner();
            } else if (ev.getSource() instanceof Window) {
                w = (Window) ev.getSource();
            }
*/
            Insets insetsDelta = ((BorderPane)stateProperty.getNode().getScene().getRoot()).getInsets();
            //double x = stateProperty.getNode().getScreenX();
            
            Stage stage = (Stage) stateProperty.getNode().getScene().getWindow();
//            stage.setX(ev.getScreenX() - startMousePos.getX() - insetsDelta.getLeft());
//            stage.setY(ev.getScreenY() - startMousePos.getY() - insetsDelta.getTop());
            stage.setX(ev.getScreenX() + startMousePos.getX() - insetsDelta.getLeft());
            stage.setY(ev.getScreenY() + startMousePos.getY() - insetsDelta.getTop());

            //System.err.println("Mouse MouseDragHandler  w=" + w);
            //if (stateProperty.isFloating() && w != null) {
            if (stateProperty.isFloating()) {
                Platform.runLater(() -> {
                    //StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY());                    
                });
                Stage result = StageRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), stage);
  System.err.println("DRAGTRANSFORMER: RESULT: = " + result + "; title=" + (result==null ? null: result.getTitle()) );
               
            }

        }

        public void mouseReleased(MouseEvent ev) {
            titleBar = stateProperty.getTitleBar();
            System.err.println("1) mouseReleased SOURCE EVENT:" + ev.getSource().getClass().getName());
            //((Node)ev.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            //((Node)ev.getSource()).removeEventFilter(MouseEvent.MOUSE_RELEASED, this);

/*            if (targetDockPane != null) {
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            }
*/            
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
