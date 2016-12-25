package org.vns.javafx.dock.api;

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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class DragTransformer implements EventHandler<MouseEvent> {

    private final Dockable dockable;

    private final DragPopup popup;

    private Parent targetDockPane;

    private Stage resultStage;
//    MouseDragHandler dragHandler = new MouseDragHandler();

    private Point2D startMousePos;

    public DragTransformer(Dockable nodeHandler) {
        this.dockable = nodeHandler;
        this.popup = new DragPopup();
        //System.err.println("DragTransformer popup.isShowing()=" + popup.isShowing());
    }

    public void initialize() {

    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            System.err.println("titlebarChanged old=" + oldValue);            
            removeEventHandlers(oldValue);
        }
        if (newValue != null) {
            System.err.println("titlebarChanged new=" + newValue);            
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
        System.err.println("MOUSE PRESSED isFloating=" + dockable.nodeHandler().isFloating());
        Point2D p = dockable.node().localToScreen(0, 0);
        double x = p.getX() - ev.getScreenX();
        double y = p.getY() - ev.getScreenY();
        this.startMousePos = new Point2D(x, y);
    }

    public void mouseDragged(MouseEvent ev) {
        
        if (!dockable.nodeHandler().isFloating()) {
            return;
        }
        System.err.println("mouseDragges ev.source=" +ev.getSource());                
        System.err.println("mouseDragges dockable " + dockable.node().getScene().getRoot());        
        Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();

        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() + startMousePos.getX() - insets.getLeft());
        stage.setY(ev.getScreenY() + startMousePos.getY() - insets.getTop());
        if (dockable.nodeHandler().isFloating()) {
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
                if ( ! ((DockPaneTarget)root).paneHandler().isUsedAsDockTarget() ) {
                    return;
                }
                System.err.println("1 findDockPane = " + root);
/*                if ((root instanceof DockPaneTarget) && ((DockPaneTarget) root).paneHandler().zorder() == 0) {
                    //System.err.println("FIND DOCKPANE: id=" + resultStage.getScene().getRoot().getId());
                } else {
                    List<Node> ls = DockUtil.findNodes(resultStage.getScene().getRoot(), (node) -> {
                        Point2D p = node.screenToLocal(ev.getScreenX(), ev.getScreenY());
                        return node.contains(p) && (node instanceof DockPaneTarget)
                                && ((DockPaneTarget) node).paneHandler().zorder() == 0;
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
                popup.show((Pane) root, dockable.node());
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
        if (!dockable.nodeHandler().isFloating()) {
            return;
        }
        Insets insets = ((BorderPane) dockable.node().getScene().getRoot()).getInsets();

        Stage stage = (Stage) dockable.node().getScene().getWindow();
        stage.setX(ev.getScreenX() + startMousePos.getX() - insets.getLeft());
        stage.setY(ev.getScreenY() + startMousePos.getY() - insets.getTop());
        if (dockable.nodeHandler().isFloating()) {
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
                if ((root instanceof DockPaneTarget) && ((DockPaneTarget) root).paneHandler().zorder() == 0) {
                    //System.err.println("FIND DOCKPANE: id=" + resultStage.getScene().getRoot().getId());
                } else {
                    List<Node> ls = DockUtil.findNodes(resultStage.getScene().getRoot(), (node) -> {
                        Point2D p = node.screenToLocal(ev.getScreenX(), ev.getSceneY());
                        return node.contains(p) && (node instanceof DockPaneTarget)
                                && ((DockPaneTarget) node).paneHandler().zorder() == 0;
                    });
                }
                //System.err.println("DOCKPANE  111111 " + root);
                popup.show((Pane) root, dockable.node());
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
            System.err.println("2. popup.isShowing() ev.source.id=" + ((Node)ev.getSource()).getId());
            popup.handle(ev.getScreenX(), ev.getScreenY());
        }

        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        if (dockable.nodeHandler().isFloating() && popup.getDockPos() != null && popup.getDragTarget() != null) {
            System.err.println("!!!!!!!!!!! " + popup.getDragTarget());            
            if (popup.getDragTarget() instanceof DockPaneTarget) {
                
                //((Stage) dockable.node().getScene().getWindow()).close();
                //stateProperty.setFloating(false);
                DockPaneTarget dpt = (DockPaneTarget) popup.getDragTarget();
                System.err.println("!!!!!!!!!!! isFloating=" + dockable.nodeHandler().isFloating());
                dpt.paneHandler().dock(dockable, popup.getDockPos());
                //dockable.nodeHandler().setFloating(false);                
                Platform.runLater(() -> {
                    System.err.println("afterDock isFloating=" + dockable.nodeHandler().isFloating());                    
                });
            } else if (popup.getDragTarget() instanceof Dockable) {
                //((Stage) dockable.node().getScene().getWindow()).close();
                //stateProperty.setFloating(false);
                Dockable dt = (Dockable) popup.getDragTarget();
                dt.nodeHandler().getPaneHandler().dock(dockable, popup.getDockPos(), dt );
                
            }

        }
        popup.hide();
    }

    protected void mouseDragDetected(MouseEvent ev) {
        System.err.println("DRAG DETECTED AFTER iFloating="+dockable.nodeHandler().isFloating());            
        
        if (!dockable.nodeHandler().isFloating()) {
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            dockable.nodeHandler().setFloating(true);
            System.err.println("DRAG DETECTED AFTER setFloating(true)");            
            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            //targetDockPane.startFullDrag();
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
            //System.err.println("DRAG DETECTED");
            mouseDragDetected(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            //System.err.println("MOUSE DRAGGED");  
            mouseDragged(ev);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased(ev);
        }
    }

}
