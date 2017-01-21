package org.vns.javafx.dock.api;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.PaneHandler.NodeSideIndicator;
import org.vns.javafx.dock.api.PaneHandler.PaneSideIndicator;
import org.vns.javafx.dock.api.PaneHandler.SideIndicator;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup {

    private SideIndicator paneIndicator;
    private SideIndicator nodeIndicator;

    private Region dockPane;
    private PaneHandler paneHandler;

    private Popup nodeIndicatorPopup;

    private Rectangle dockPlace;

    private Node dragTarget;
    private Side targetNodeSidePos;
    private Side targetPaneSidePos;

    private Side dockPos;

/*    public DragPopup(SideIndicator paneIndicator, SideIndicator nodeIndicator) {
        this.paneIndicator = paneIndicator;
        this.nodeIndicator = nodeIndicator;

        init();

    }
*/
    public DragPopup() {
        //this.paneIndicator = paneIndicator;
        //this.nodeIndicator = nodeIndicator;

        init();

    }

    private void init() {
    }

    public void initContent() {
        Pane paneIndicatorPane = paneIndicator.getIndicatorPane();
        paneIndicatorPane.setMouseTransparent(true);
        paneIndicatorPane.setStyle("-fx-border-width: 2.0; -fx-border-color: blue;");

        Pane nodeIndicatorPane = nodeIndicator.getIndicatorPane();
        //nodeIndicatorPane.setStyle("-fx-border-width: 2.0; -fx-border-color: red;");
        nodeIndicatorPane.setMouseTransparent(true);

        //nodeIndicator.getIndicatorPane().setScaleX(0.5);
        //nodeIndicator.getIndicatorPane().setScaleY(0.5);
        getContent().add(paneIndicatorPane);

        dockPlace = new Rectangle();
        //dockPlace.setManaged(false);
        dockPlace.getStyleClass().add("dock-place");
        paneIndicatorPane.getChildren().add(dockPlace);

        nodeIndicatorPopup = new Popup();
        nodeIndicatorPopup.getContent().add(nodeIndicatorPane);
    }

    public SideIndicator getPaneIndicator() {
        return paneIndicator;
    }

    public SideIndicator getNodeIndicator() {
        return nodeIndicator;
    }

    public Popup getNodeIndicatorPopup() {
        return nodeIndicatorPopup;
    }

    public Pane getRoot() {
        return getPaneIndicator().getIndicatorPane();
    }

    public Node getDragTarget() {
        return dragTarget;
    }

    public PaneHandler getTargetPaneHandler() {
        return this.paneHandler;
    }

    public Side getTargetNodeSidePos() {
        return targetNodeSidePos;
    }

    public Side getTargetPaneSidePos() {
        return targetPaneSidePos;
    }

    public Side getDockPos() {
        return dockPos;
    }
    public void show(PaneHandler paneHandler, Node dockable) {
        if (paneHandler != this.paneHandler) {
            paneIndicator = new PaneSideIndicator();
            nodeIndicator = new NodeSideIndicator();
            initContent();
        }
        this.paneHandler = paneHandler;
        doShow();
    }
    protected void doShow() {
        setAutoFix(false);
        this.dockPane = paneHandler.getDockPane();

        paneIndicator.getIndicatorPane().prefHeightProperty().bind(dockPane.heightProperty());
        paneIndicator.getIndicatorPane().prefWidthProperty().bind(dockPane.widthProperty());

        paneIndicator.getIndicatorPane().minHeightProperty().bind(dockPane.heightProperty());
        paneIndicator.getIndicatorPane().minWidthProperty().bind(dockPane.widthProperty());

        Point2D pos = dockPane.localToScreen(0, 0);
        dragTarget = null;
        
        //Platform.runLater(()->{paneIndicator.sideIndicatorShowing(paneHandler, null);});
        //paneIndicator.sideIndicatorShowing(paneHandler, null);
        //paneIndicator.endUpdateIndicator(paneHandler, null);
        this.show(dockPane, pos.getX(), pos.getY());
        
    }
    public void showRedirect(PaneHandler paneHandler, PaneSideIndicator paneIndicator,Node dockable) {
        this.paneIndicator = paneIndicator;
        if (paneHandler != this.paneHandler) {
            nodeIndicator = new NodeSideIndicator();
            initContent();
            setOnShown(e -> paneIndicator.endUpdateIndicator(paneHandler, null));
        }
        this.paneHandler = paneHandler;
        doShow();
    }
    
    @Override
    public void hide() {
        super.hide();
        paneIndicator.sideIndicatorHidden(paneHandler, null);
    }

    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (DockUtil.contains(paneIndicator.getIndicatorPane(), x, y)) {
            retval = true;
        } else {
            hide();
        }
        return retval;
    }

    public boolean paneIndicatorContains(double x, double y) {
        if (!isShowing()) {
            return false;
        }
        Point2D p = getPaneIndicator().getIndicatorPane().localToScreen(0, 0);
        return !((x < p.getX() || x > p.getX() + getPaneIndicator().getIndicatorPane().getWidth()
                || y < p.getY() || y > p.getY() + getPaneIndicator().getIndicatorPane().getHeight()));
    }

    public boolean contains(Button b, double x, double y) {
        if (b == null) {
            return false;
        }
        Point2D p = b.localToScreen(0, 0);
        if (p == null) {
            return false;
        }

        return !((x < p.getX() || x > p.getX() + b.getWidth()
                || y < p.getY() || y > p.getY() + b.getHeight()));

    }

    public Boolean intersects(Node node1, Node node2) {

        if (node1 == null || node2 == null) {
            return false;
        }
        Bounds pb1 = node1.getBoundsInParent();
        Bounds pb2 = node1.getBoundsInParent();

        Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
        Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
        return b1.intersects(b2);

    }

    public boolean contains(Pane buttonPane, double x, double y) {
        if (buttonPane == null) {
            return false;
        }

        Point2D p = buttonPane.localToScreen(0, 0);
        if (p == null) {
            return false;
        }
        boolean retval = false;

        Point2D point = buttonPane.screenToLocal(x, y);
        //System.err.println("buttonPane size=" + buttonPane.getChildren().size());
        for (Node node : buttonPane.getChildren()) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button b = (Button) node;
            Bounds bnd = b.getBoundsInParent();
//            System.err.println("--- b.W=" + b.getWidth());
//            System.err.println("--- b.H=" + b.getHeight());
//            System.err.println("--- point.X=" + point.getX());
//            System.err.println("--- point.Y=" + point.getY());            
//            System.err.println("--- bnd.X=" + bnd.getMinX());
//            System.err.println("--- bnd.Y=" + bnd.getMinY());            
//            System.err.println("--- bnd.W=" + (bnd.getMinX() + bnd.getWidth()) );
//            System.err.println("--- bnd.H=" + (bnd.getMinY() + bnd.getHeight()) );            
            
            //Point2D pl = b.screenToLocal(x, y);
//System.err.println("1 buttonPane size=" + buttonPane.getChildren().size());            
            //Point2D lp = b.localToParent(pl);
            if (bnd.contains(point)) {
//System.err.println("2 buttonPane CONTAINES=" + buttonPane.getChildren().size());                            
//System.err.println("3 buttonPane CONTAINES=" + buttonPane.getId());                            
                retval = true;
                break;
            }
        }
        return retval;

    }

    public void handle(double screenX, double screenY) {
        //System.err.println("paneIndicator.getIndicatorPane().getChildren().indexOf(dockPlace)" + paneIndicator.getIndicatorPane().getChildren().indexOf(dockPlace));
        setOnHiding(v -> {
            dragTarget = null;
        });
//        System.err.println("x=" + screenX +",y=" + screenY);
        //
        // Try to find a Dockable object which paneIndicatorContains the specifired 
        // (x,y) coordinates
        // The result may be null
        //
        dragTarget = null;
        Region dockNode = (Region) DockUtil.findDockable(dockPane, screenX, screenY);
        paneIndicator.targetNodeChanged(getTargetPaneHandler(),dockNode,screenX, screenY);
        nodeIndicator.targetNodeChanged(getTargetPaneHandler(),dockNode,screenX, screenY);

        Point2D newPos;
        
        nodeIndicatorPopup.setOnShown( e -> nodeIndicator.sideIndicatorShown(getTargetPaneHandler(), dockNode));
        nodeIndicatorPopup.setOnHidden( e -> nodeIndicator.sideIndicatorHidden(getTargetPaneHandler(), null));            
        
        if (dockNode != null) {
            newPos = nodeIndicator.mousePosBy(getTargetPaneHandler(), dockNode, screenX, screenY);
            nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());
        } else {
            newPos = nodeIndicator.mousePosBy(getTargetPaneHandler(), screenX, screenY);
            if ( newPos != null ) {
                nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());                
            } else {
                nodeIndicatorPopup.hide();
            }
        }

        dockPlace.setVisible(false);
        dragTarget = null;
        targetNodeSidePos = null;
        targetPaneSidePos = null;

        if (nodeIndicatorPopup.isShowing()) {
            if (contains(nodeIndicator.getTopButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.TOP);
            } else if (contains(nodeIndicator.getLeftButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.LEFT);
            } else if (contains(nodeIndicator.getRightButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.RIGHT);
            } else if (contains(nodeIndicator.getBottomButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.BOTTOM);
            } else if (contains(nodeIndicator.getCenterButtons(), screenX, screenY)) {
                nodeIndicator.showDockPlace(getTargetPaneHandler(),dockNode, screenX, screenY);
            }

            if (dragTarget != null) {
                targetNodeSidePos = dockPos;
                return;
            }
        }
//        System.err.println("PAIN INDICATOR getTopButtons().id=" + paneIndicator.getTopButtons().getId());
        if (contains(paneIndicator.getTopButtons(), screenX, screenY)) {
//            System.err.println("CONTAINS !!!!!!!!!!!!!!! TOP id=" + paneIndicator.getTopButtons().getId());
            showDockPlace(Side.TOP);
        } else if (contains(paneIndicator.getLeftButtons(), screenX, screenY)) {
            showDockPlace(Side.LEFT);
        } else if (contains(paneIndicator.getRightButtons(), screenX, screenY)) {
            showDockPlace(Side.RIGHT);
        } else if (contains(paneIndicator.getBottomButtons(), screenX, screenY)) {
            showDockPlace(Side.BOTTOM);
        } else if (contains(nodeIndicator.getCenterButtons(), screenX, screenY)) {
                paneIndicator.showDockPlace(getTargetPaneHandler(),dockNode, screenX, screenY);
        } else {
            dockPlace.setVisible(false);
        }
        targetPaneSidePos = dockPos;
    }

    public void showDockPlace(Side side) {
        dockPos = side;
//        System.err.println("SHOW DOCK PLACE = " + dockPane.getWidth());                

        dragTarget = dockPane;
        switch (side) {
            case TOP:
//                System.err.println("showDockPlace: dockPane.getWidth() = " + dockPane.getWidth());                
//                System.err.println("showDockPlace: dockPane.getHeight() = " + dockPane.getHeight());                                
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(dockPane.getHeight() / 2);
                //Point2D p = dockPlace.localToParent(0, 0);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
//                System.err.println("showDockPlace: dockPlace.getWidth() = " + dockPlace.getWidth());                
//                System.err.println("showDockPlace: dockPlace.getHeight() = " + dockPlace.getHeight());                                
                
                break;
            case BOTTOM:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(dockPane.getHeight() / 2);
                p = dockPlace.localToParent(0, dockPane.getHeight() - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case LEFT:
                dockPlace.setWidth(dockPane.getWidth() / 2);
                dockPlace.setHeight(dockPane.getHeight());
                p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case RIGHT:
                dockPlace.setWidth(dockPane.getWidth() / 2);
                dockPlace.setHeight(dockPane.getHeight());
                p = dockPlace.localToParent(dockPane.getWidth() - dockPlace.getWidth(), 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            default:
                dockPos = null;
                dragTarget = null;
        }
        dockPlace.setVisible(true);
        dockPlace.toFront();
    }

    public void showDockPlace(Region target, Side side) {
        if (target == null) {
            return;
        }
        dockPos = side;
        dragTarget = target;

        Point2D p = target.localToScreen(0, 0).subtract(dockPane.localToScreen(0, 0));
        dockPlace.setX(p.getX());
        dockPlace.setY(p.getY());

        switch (side) {
            case TOP:
                dockPlace.setWidth(target.getWidth());
                dockPlace.setHeight(target.getHeight() / 2);
                break;
            case BOTTOM:
                dockPlace.setWidth(target.getWidth());
                dockPlace.setHeight(target.getHeight() / 2);
                dockPlace.setY(p.getY() + dockPlace.getHeight());
                break;
            case LEFT:
                dockPlace.setWidth(target.getWidth() / 2);
                dockPlace.setHeight(target.getHeight());
                break;
            case RIGHT:
                dockPlace.setWidth(target.getWidth() / 2);
                dockPlace.setHeight(target.getHeight());
                dockPlace.setX(p.getX() + dockPlace.getWidth());
                break;
            default:
                dragTarget = null;
                dockPos = null;
        }
        dockPlace.setVisible(true);
    }

    public Region getDockPane() {
        return dockPane;
    }

    public Rectangle getDockPlace() {
        return dockPlace;
    }

}
