package org.vns.javafx.dock.api;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public class DragTransformer1 {

    private final StateProperty stateProperty;
    private Node titleBar;

    private PaneDelegate targetPaneDelegate;

    private Point2D startMosePos;

    public DragTransformer1(StateProperty stateProperty) {
        this.stateProperty = stateProperty;
        targetPaneDelegate = stateProperty.getPaneDelegate();
    }

    public void initialize() {

    }

    /*    public PaneDelegate getTargetPaneDelegate() {
        return targetPaneDelegate;
    }
     */
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

        titleBar.setOnMouseDragged(this::mouseDragged);
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
            ((Node) d).setOnMouseDragEntered(this::mouseOnDockableEntered);
            ((Node) d).setOnMouseDragged(this::mouseOnDockableDragged); 
            ((Node) d).setOnMouseMoved(this::mouseOnDockableDragged);            
            ((Node) d).setOnMouseDragExited(this::mouseOnDockableExited);
            ((Node) d).setOnMouseExited(this::mouseOnDockableExited);
            ((Node) d).setOnMouseEntered(this::mouseOnDockableEntered);
            
        });
        //Stage s;
        //s.getOwner().
    }

    private void addDockableEventHandlers01(Region root) {
        List<Dockable> list = DockUtil.getAllDockable(root);
        list.forEach(d -> {
            ((Node) d).setOnDragOver((DragEvent e) -> {
// If drag board has a string, let the event know that the target accepts
// copy and move transfer modes
                //Dragboard dragboard = e.getDragboard();
                
                System.err.println("Mouse DRAGOVER" + e.getSource().getClass().getName());                
                e.acceptTransferModes(TransferMode.ANY);
                e.consume();
            });
        });
    }

    private void removeDockableEventHandlers(Node titleBar) {

    }

    protected void mousePressed(MouseEvent ev) {
        this.startMosePos = new Point2D(ev.getX(), ev.getY());
        if (stateProperty.isFloating()) {
            ((Node) ev.getSource()).setMouseTransparent(true);
        }
        //((Node)ev.getSource()).setMouseTransparent(true);

        System.err.println("Mouse Pressed source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseReleased(MouseEvent ev) {
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
             //stateProperty.getPaneDelegate().getDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);
            //stateProperty.getPaneDelegate().getDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, hhhh);             
            Node s = (Node) ev.getSource();
            
            //s.getScene().getWindow().
            s.getScene().getWindow().addEventFilter(MouseEvent.MOUSE_DRAGGED, hhhh);             
             targetPaneDelegate = stateProperty.getPaneDelegate();
             stateProperty.setFloating(true);
             
         } else {
            ((Node)ev.getSource()).setMouseTransparent(true);
            ((Node)ev.getSource()).startFullDrag();
            Node s = (Node) ev.getSource();
            s.getScene().getWindow().addEventFilter(MouseEvent.MOUSE_DRAGGED, hhhh);             
            targetPaneDelegate.getDockPane().setOnMouseDragged(this::mouseOnDockPaneDragged);
            
            addDockableEventHandlers(targetPaneDelegate.getDockPane());
            
         }
        if (true) {
            return;
        }
        System.err.println("Mouse Dragged Detected source.class=" + ev.getSource().getClass().getName());

        ((Node) ev.getSource()).startFullDrag();

        if (!stateProperty.isFloating()) {
            addDockableEventHandlers(stateProperty.getPaneDelegate().getDockPane());
            targetPaneDelegate = stateProperty.getPaneDelegate();

            System.err.println("1. PRION ID " + stateProperty.getPaneDelegate().getDockPane().getId());
            System.err.println("2. PRION ID " + targetPaneDelegate.getDockPane().getId());
            targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);
            targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this::mouseOnDockPaneReleased);
            //stateProperty.setFloating(true);
            System.err.println("3. PRION ID " + stateProperty.getPaneDelegate().getDockPane().getId());
            System.err.println("4. PRION ID " + targetPaneDelegate.getDockPane().getId());
            stateProperty.setFloating(true);
        } else {
            //System.err.println("4. PRION ID " + targetPaneDelegate.getDockPane().getId());                        
            //addDockableEventHandlers(targetPaneDelegate.getDockPane());
            //targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);
            //targetPaneDelegate.getDockPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this::mouseOnDockPaneReleased);            
            //stateProperty.setFloating(true);
            targetPaneDelegate = stateProperty.getOrigionalPaneDelegate();

            targetPaneDelegate.getDockPane().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this::mouseOnDockPaneDragged);
            targetPaneDelegate.getDockPane().removeEventFilter(MouseEvent.MOUSE_RELEASED, this::mouseOnDockPaneReleased);

        }

    }

    protected void mouseOnDockPaneReleased(MouseEvent ev) {
        System.err.println("************* Mouse DOCKPANE Released source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockPaneDragged(MouseEvent ev) {
        System.err.println("Mouse DOCKPANE Dragged source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockableEntered(MouseEvent ev) {
        System.err.println("Mouse Dockable.ENTERED  source.class=" + ev.getSource().getClass().getName() + "; id=" + ((Node)ev.getSource()).getId());
    }
    protected void mouseOnDockableDragged(MouseEvent ev) {
        System.err.println("Mouse mouseOnDockableDragged source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseOnDockableExited(MouseEvent ev) {
        System.err.println("Mouse Dockable.EXITED Released source.class=" + ev.getSource().getClass().getName());
    }

    protected void mouseDragDetected01(MouseEvent ev) {
        if ( ! stateProperty.isFloating() ) {
            addDockableEventHandlers01(stateProperty.getPaneDelegate().getDockPane());        
            
        }
        System.err.println("Mouse mouseDragDetected01 Released source.class=" + ev.getSource().getClass().getName());
        Node sourceNode = (Node) ev.getSource();
        //sourceNode.startDragAndDrop(TransferMode.NONE);
        Dragboard dragboard = sourceNode.startDragAndDrop(TransferMode.MOVE);
// Add the source text to the Dragboard
        ClipboardContent content = new ClipboardContent();
        //content.putString("VALERA");
        content.putUrl("u://bb");
        dragboard.setContent(content);
        if ( ! stateProperty.isFloating() ) {
            stateProperty.setFloating(true);
        }        
        ev.consume();
    }

    public class MouseDragHandler implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent ev) {
              //System.err.println("Mouse MouseDragHandler  source.class=" + ev.getSource().getClass().getName());    
              System.err.println("Mouse MouseDragHandler  source.class=" + ((Stage)ev.getSource()).getTitle());                  
              Node dp = targetPaneDelegate.getDockPane();
              Stage s = ((Stage)ev.getSource());
              dp.getOnMouseDragged();
              MouseEvent me = ev.copyFor(dp, dp);
              if ( stateProperty.isFloating() && s.getOwner() != null ) {
                s.getOwner().fireEvent(me);
              }
        }
        
    }
}
