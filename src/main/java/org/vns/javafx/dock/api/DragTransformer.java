package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.demo.TestIfStageActive;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DragTransformer {

    private final StateProperty stateProperty;
    private Node titleBar;
    public Node dockableNode;
    private Pane targetDockPane;

    EventHandler<MouseEvent> mouseMoveHandler;
    
    MouseDragHandler targetDockPaneHandler = new MouseDragHandler();

    List<Dockable> dockableList = new ArrayList<>();
    
    private Point2D startMosePos;

    public DragTransformer(StateProperty stateProperty) {
        this.stateProperty = stateProperty;
    }

    public void initialize() {

    }

    public Pane getTargetDockPane() {
        return targetDockPane;
    }

    public void setTargetDockPane(Pane targetDockPane) {
        this.targetDockPane = targetDockPane;
    }


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
        titleBar.setOnMouseReleased(this::mouseReleased);

    }

    private void addDockableEventHandlers(Region root) {
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
                n.addEventHandler(DockInputEvent.TYPE, new DockInputEventHandler());
            }

            /*            ((Node) d).setOnMouseDragEntered(this::mouseOnDockableEntered);
            ((Node) d).setOnMouseDragged(this::mouseOnDockableDragged); 
            ((Node) d).setOnMouseMoved(this::mouseOnDockableDragged);            
            ((Node) d).setOnMouseDragExited(this::mouseOnDockableExited);
            ((Node) d).setOnMouseExited(this::mouseOnDockableExited);
            ((Node) d).setOnMouseEntered(this::mouseOnDockableEntered);
             */
        });
    }

    private void removeDockableEventHandlers(Node titleBar) {

    }

    protected void mousePressed(MouseEvent ev) {
        this.startMosePos = new Point2D(ev.getX(), ev.getY());
        System.err.println("titleBar == (Node) ev.getSource()" + (ev.getSource() == titleBar));
        if (stateProperty.isFloating()) {
            ((Node) ev.getSource()).setMouseTransparent(true);
        } else {
            ((Node)ev.getSource()).setMouseTransparent(false);
        }
        
        TestIfStageActive.frontStage.toFront();
        
        System.err.println("Mouse Pressed source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseReleased(MouseEvent ev) {
        ((Node) ev.getSource()).setMouseTransparent(false);
        System.err.println("11 Mouse Released source.class=" + ev.getSource().getClass().getName());
    }
    protected void mouseEntered(MouseEvent ev) {
        //((Node) ev.getSource()).setMouseTransparent(false);
        System.err.println("%%%%%%%%%% Mouse Entered " );
    }
    
    protected void mouseDragDetected(MouseEvent ev) {
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
            ((Node)ev.getSource()).startFullDrag();
            
            System.err.println("stateProperty.getPaneDelegate().getOriginalDockPane().id=" + stateProperty.getOrigionalPaneDelegate().getDockPane().getId());
            System.err.println("source = " + ev.getSource().getClass().getName());
        }
    }

    

    public class MouseDragHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent ev) {
            //System.err.println("!!!! Mouse MouseDragHandler " + ev.getEventType() );
            
            if ( ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(ev);
            } if ( ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                mouseDragged(ev);
            }

        }
        public void mouseDragged(MouseEvent ev) {
            //System.err.println("!!!!!!!!!!!! Mouse MouseDragHandler  w=");            
            Window w = null;
            if (ev.getSource() instanceof Node) {
                w = ((Node) ev.getSource()).getScene().getWindow();
            } else if (ev.getSource() instanceof Stage) {
                w = ((Stage) ev.getSource()).getOwner();
            } else if (ev.getSource() instanceof Window) {
                w = (Window) ev.getSource();
            }
            //System.err.println("Mouse MouseDragHandler  w=" + w);
            if (stateProperty.isFloating() && w != null) {
//                System.err.println("Mouse MouseDragHandler  dockableNode.id=" + dockableNode.getId());
                if (dockableNode != null) {
                   MouseEvent me = ev.copyFor(dockableNode, dockableNode);
                   DockInputEvent die = new DockInputEvent(dockableNode, dockableNode, me);
                   dockableNode.fireEvent(die);
                   //System.err.println("Mouse MouseDragHandler  dockableNode.id=" + dockableNode.getId());
                }
            }
            
        }
        
        public void mouseReleased(MouseEvent ev) {
            titleBar = stateProperty.getTitleBar();

            if ( targetDockPane != null ) {
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
                targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            }
        }

    }

    public static class DockInputEvent extends InputEvent {

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
            Node n = ((Node) ev.getSource());
            String sep = System.lineSeparator();
            String s = "me.getX()=" + me.getX() + sep;
            out(s);
            s = "me.getY()=" + me.getY() + sep;
            out(s);

        }

    }

    public static void out(String s) {
        System.err.println(s);
    }
}
