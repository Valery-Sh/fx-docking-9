package org.vns.javafx.dock.api;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DragTransformer {

    private final StateProperty stateProperty;
    private Node titleBar;
    public Node dockableNode;
    private PaneDelegate targetPaneDelegate;

    EventHandler<MouseEvent> mouseMoveHandler;

    private Point2D startMosePos;

    public DragTransformer(StateProperty stateProperty) {
        this.stateProperty = stateProperty;
        targetPaneDelegate = stateProperty.getPaneDelegate();
    }

    public void initialize() {

    }

    private Region node() {
        return stateProperty.getNode();
    }

    private Dockable dockable() {
        return stateProperty.getDockable();
    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        System.err.println("DragTransformer titlebarChanged");
        if (oldValue != null) {
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
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

    private void removeEventHandlers(Node titleBar) {
        if (titleBar == null) {
            return;
        }
        titleBar.removeEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);

    }

    private void addEventHandlers(Node titleBar) {

        //Region titleBar = stateProperty.getTitleBar();
        if (titleBar == null) {
            return;
        }
        titleBar.setOnMousePressed(this::mousePressed);

        titleBar.setOnMouseDragged(hhhh);
        titleBar.setOnDragDetected(this::mouseDragDetected);
        titleBar.setOnMouseReleased(this::mouseReleased);

// Add mouse event handlers for the target
/*        targetFld.setOnMouseDragEntered(e -> print("Target: drag entered"));
        targetFld.setOnMouseDragOver(e -> print("Target: drag over"));
        targetFld.setOnMouseDragReleased(e -> print("Target: drag released"));
        targetFld.setOnMouseDragExited(e -> print("Target: drag exited"));
         */
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

    protected void mouseMoved(MouseEvent ev) {
        System.err.println("Mouse MOUSE MOVED");
    }

    protected void mousePressed(MouseEvent ev) {
        this.startMosePos = new Point2D(ev.getX(), ev.getY());
        mouseMoveHandler = this::mouseMoved;
        ((Node) ev.getSource()).setOnMouseMoved(this::mouseMoved);

        if (stateProperty.isFloating()) {
            ((Node) ev.getSource()).setMouseTransparent(true);
        }
        //((Node)ev.getSource()).setMouseTransparent(true);

        System.err.println("Mouse Pressed source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseReleased(MouseEvent ev) {
        //((Node) ev.getSource()).removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
        //((Node) ev.getSource()).setOnMouseMoved(null);
        ((Node) ev.getSource()).setMouseTransparent(false);
        //targetPaneDelegate.getDockPane().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);
        //targetPaneDelegate.getDockPane().removeEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);            
//      targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, this::mouseOnDockPaneDragged);                        
        //targetPaneDelegate.getDockPane().removeEventFilter(MouseEvent.MOUSE_DRAGGED, hhhh);
        //targetPaneDelegate.getDockPane().removeEventHandler(MouseEvent.MOUSE_DRAGGED, hhhh);            

        System.err.println("11 Mouse Released source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseDragged(MouseEvent ev) {
        //System.err.println("Mouse Dragged source.class=" + ev.getSource().getClass().getName());
    }

    MouseDragHandler hhhh = new MouseDragHandler();

    protected void mouseDragDetected(MouseEvent ev) {
        if (!stateProperty.isFloating()) {
            targetPaneDelegate = stateProperty.getPaneDelegate();
            stateProperty.setFloating(true);
            addDockableEventHandlers(targetPaneDelegate.getDockPane());
            targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, hhhh);
            targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        } else {
            //ddDockableEventHandlers(targetPaneDelegate.getDockPane());
        }
    }

    protected void mouseOnDockPaneReleased(MouseEvent ev) {
        System.err.println("************* Mouse DOCKPANE Released source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockPaneDragged(MouseEvent ev) {
        System.err.println("Mouse DOCKPANE Dragged source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockableEntered(MouseEvent ev) {
        System.err.println("Mouse Dockable.ENTERED  source.class=" + ev.getSource().getClass().getName() + "; id=" + ((Node) ev.getSource()).getId());
    }

    protected void mouseOnDockableDragged(MouseEvent ev) {
        System.err.println("Mouse mouseOnDockableDragged source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockableExited(MouseEvent ev) {
        System.err.println("Mouse Dockable.EXITED Released source.class=" + ev.getSource().getClass().getName());
    }

    public class MouseDragHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent ev) {
            //System.err.println("Mouse MouseDragHandler  source.class=" + ev.getSource().getClass().getName());    
            //System.err.println("Mouse MouseDragHandler  source.class=" + ((Stage)ev.getSource()).getTitle());                  
//              Node dp = targetPaneDelegate.getDockPane();
            Window w = null;
            if (ev.getSource() instanceof Node) {
                w = ((Node) ev.getSource()).getScene().getWindow();
            } else if (ev.getSource() instanceof Stage) {
                w = ((Stage) ev.getSource()).getOwner();
            } else if (ev.getSource() instanceof Window) {
                w = (Window) ev.getSource();
            }
            System.err.println("Mouse MouseDragHandler  w=" + w);

//              dp.getOnMouseDragged();
//              if ( dockableNode != null)
//              System.err.println("Mouse MouseDragHandler  dockableNode.id=" + dockableNode.getId());                  
            if (stateProperty.isFloating() && w != null) {
                System.err.println("Mouse MouseDragHandler  dockableNode.id=" + dockableNode.getId());
                if (dockableNode != null) {
                    MouseEvent me = ev.copyFor(dockableNode, dockableNode);
                    DockInputEvent die = new DockInputEvent(dockableNode, dockableNode, me);
                    dockableNode.fireEvent(die);
                    System.err.println("Mouse MouseDragHandler  dockableNode.id=" + dockableNode.getId());
                }
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
