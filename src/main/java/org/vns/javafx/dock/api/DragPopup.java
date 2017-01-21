package org.vns.javafx.dock.api;

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
import org.vns.javafx.dock.api.PaneHandler.PaneSideIndicator;
import org.vns.javafx.dock.api.PaneHandler.SideIndicator;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup {

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
    public DragPopup(PaneHandler paneHandler) {
        this.paneHandler = paneHandler;
        init();

    }

    private void init() {
        System.err.println("init getPaneIndicator()=" + getPaneIndicator());
        System.err.println("init getPaneIndicator().getIndicatorPane()=" + getPaneIndicator().getIndicatorPane());
        //System.err.println("init dockPane=" + dockPane);
        //getPaneIndicator().getIndicatorPane().setPrefHeight(dockPane.heightProperty().get());
        //getPaneIndicator().getIndicatorPane().setPrefWidth(dockPane.widthProperty().get());
/*        getPaneIndicator().getIndicatorPane();
        setOnShown(e -> {
            getPaneIndicator().getIndicatorPane().prefHeightProperty().bind(dockPane.heightProperty());
            getPaneIndicator().getIndicatorPane().prefWidthProperty().bind(dockPane.widthProperty());

            getPaneIndicator().getIndicatorPane().minHeightProperty().bind(dockPane.heightProperty());
            getPaneIndicator().getIndicatorPane().minWidthProperty().bind(dockPane.widthProperty());
        System.err.println("onShown = bind(dockPane.heightProperty()=" + dockPane.heightProperty().get());
        System.err.println("onShown = getIndicatorPane().W=" + getPaneIndicator().getIndicatorPane().getWidth());        
        System.err.println("onShown = getIndicatorPane().prefW=" + getPaneIndicator().getIndicatorPane().getPrefWidth());        
            
        });
         */
        initContent();
    }

    public Region getDockPane() {
        return paneHandler.getDockPane();
    }

    public void initContent() {
        Pane paneIndicatorPane = paneHandler.getPaneIndicator().getIndicatorPane();
        paneIndicatorPane.setMouseTransparent(true);
        paneIndicatorPane.setStyle("-fx-border-width: 2.0; -fx-border-color: blue;");

        Pane nodeIndicatorPane = paneHandler.getNodeIndicator().getIndicatorPane();
        //nodeIndicatorPane.setStyle("-fx-border-width: 2.0; -fx-border-color: red;");
        nodeIndicatorPane.setMouseTransparent(true);

        dockPlace = new Rectangle();
        dockPlace.getStyleClass().add("dock-place");
        paneIndicatorPane.getChildren().add(dockPlace);

        nodeIndicatorPopup = new Popup();

        getPaneIndicator().getIndicatorPane().prefHeightProperty().bind(getDockPane().heightProperty());
        getPaneIndicator().getIndicatorPane().prefWidthProperty().bind(getDockPane().widthProperty());

        getPaneIndicator().getIndicatorPane().minHeightProperty().bind(getDockPane().heightProperty());
        getPaneIndicator().getIndicatorPane().minWidthProperty().bind(getDockPane().widthProperty());
//        System.err.println("onShown = bind(dockPane.heightProperty()=" + getDockPane().heightProperty().get());
//        System.err.println("onShown = getIndicatorPane().W=" + getPaneIndicator().getIndicatorPane().getWidth());
//        System.err.println("onShown = getIndicatorPane().prefW=" + getPaneIndicator().getIndicatorPane().getPrefWidth());
        nodeIndicatorPopup.getContent().add(nodeIndicatorPane);
        getContent().add(paneIndicatorPane);
    }

    public SideIndicator getPaneIndicator() {
        return paneHandler.getPaneIndicator();
    }

    public SideIndicator getNodeIndicator() {
        return paneHandler.getNodeIndicator();
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

    public void show(Node dockable) {
        setAutoFix(false);
        Point2D pos = getDockPane().localToScreen(0, 0);
        dragTarget = null;
        this.show(getDockPane(), pos.getX(), pos.getY());
    }


    @Override
    public void hide() {
        super.hide();
        //paneHandler.getPaneIndicator().sideIndicatorHidden(paneHandler, null);
    }

    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (DockUtil.contains(getPaneIndicator().getIndicatorPane(), x, y)) {
            retval = true;
        } else if (isShowing()) {
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
    public Button getSelectedButton(Pane buttonPane, double x, double y) {
        if (buttonPane == null) {
            return null;
        }

        Point2D p = buttonPane.localToScreen(0, 0);
        if (p == null) {
            return null;
        }
        Button retval = null;

        Point2D point = buttonPane.screenToLocal(x, y);
        //System.err.println("buttonPane size=" + buttonPane.getChildren().size());
        for (Node node : buttonPane.getChildren()) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button b = (Button) node;
            Bounds bnd = b.getBoundsInParent();
            if (bnd.contains(point)) {
                retval = b;
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
        Region dockNode = (Region) DockUtil.findDockable(getDockPane(), screenX, screenY);
//        getPaneIndicator().targetNodeChanged(getTargetPaneHandler(), dockNode, screenX, screenY);
//        getPaneIndicator().targetNodeChanged(getTargetPaneHandler(), dockNode, screenX, screenY);

        Point2D newPos;

        nodeIndicatorPopup.setOnShown(e -> {
            getNodeIndicator().sideIndicatorShown(getTargetPaneHandler(), dockNode);
        });
        nodeIndicatorPopup.setOnHidden(e -> getNodeIndicator().sideIndicatorHidden(getTargetPaneHandler(), null));

        if (dockNode != null) {
            newPos = getNodeIndicator().mousePosBy(dockNode, screenX, screenY);
            nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());
        } else {
            newPos = getNodeIndicator().mousePosBy(screenX, screenY);
            if (newPos != null) {
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
            if (contains(getNodeIndicator().getTopButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.TOP);
            } else if (contains(getNodeIndicator().getLeftButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.LEFT);
            } else if (contains(getNodeIndicator().getRightButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.RIGHT);
            } else if (contains(getNodeIndicator().getBottomButtons(), screenX, screenY)) {
                showDockPlace(dockNode, Side.BOTTOM);
            } else if (contains(getNodeIndicator().getCenterButtons(), screenX, screenY)) {
                getNodeIndicator().showDockPlace(dockNode, screenX, screenY);
            }

            if (dragTarget != null) {
                targetNodeSidePos = dockPos;
                return;
            }
        }
//        System.err.println("PAIN INDICATOR getTopButtons().id=" + paneIndicator.getTopButtons().getId());
/*        if (contains(getPaneIndicator().getTopButtons(), screenX, screenY)) {
            showDockPlace(Side.TOP);
        } else if (contains(getPaneIndicator().getLeftButtons(), screenX, screenY)) {
            showDockPlace(Side.LEFT);
        } else if (contains(getPaneIndicator().getRightButtons(), screenX, screenY)) {
            showDockPlace(Side.RIGHT);
        } else if (contains(getPaneIndicator().getBottomButtons(), screenX, screenY)) {
            showDockPlace(Side.BOTTOM);
        } else if (contains(getPaneIndicator().getCenterButtons(), screenX, screenY)) {
            getPaneIndicator().showDockPlace(getTargetPaneHandler(), dockNode, screenX, screenY);
        } else {
            dockPlace.setVisible(false);
        }
*/
        Button btn = null;
        if ( (btn = getSelectedButton(getPaneIndicator().getTopButtons(), screenX, screenY)) != null) {
            showDockPlace(btn,Side.TOP);
        } else if (getSelectedButton(getPaneIndicator().getLeftButtons(), screenX, screenY) != null) {
            showDockPlace(Side.LEFT);
        } else if (getSelectedButton(getPaneIndicator().getRightButtons(), screenX, screenY) != null) {
            showDockPlace(Side.RIGHT);
        } else if (getSelectedButton(getPaneIndicator().getBottomButtons(), screenX, screenY) != null) {
            showDockPlace(Side.BOTTOM);
        } else if (getSelectedButton(getPaneIndicator().getCenterButtons(), screenX, screenY) != null) {
            getPaneIndicator().showDockPlace(dockNode, screenX, screenY);
        } else {
            dockPlace.setVisible(false);
        }
        targetPaneSidePos = dockPos;
    }
    public void showDockPlace(Button selected,Side side) {
        dockPos = side;
        //dragTarget = getDockPane();
        
        Region pane = getDockPane();
        if ( selected != null && selected.getUserData() != null ) {
             pane = ((PaneHandler) selected.getUserData()).getDockPane();
             System.err.println("Dragpopup.targetPaneHandler.id" + getTargetPaneHandler().getDockPane().getId());
        }             
        dragTarget = pane;
        getPaneIndicator().setSelectedButton(selected);
        switch (side) {
            case TOP:
                dockPlace.setWidth(pane.getWidth());
                dockPlace.setHeight(pane.getHeight() / 2);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case BOTTOM:
                dockPlace.setWidth(pane.getWidth());
                dockPlace.setHeight(pane.getHeight() / 2);
                p = dockPlace.localToParent(0, pane.getHeight() - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case LEFT:
                dockPlace.setWidth(pane.getWidth() / 2);
                dockPlace.setHeight(pane.getHeight());
                p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case RIGHT:
                dockPlace.setWidth(pane.getWidth() / 2);
                dockPlace.setHeight(pane.getHeight());
                p = dockPlace.localToParent(pane.getWidth() - dockPlace.getWidth(), 0);
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

    public void showDockPlace(Side side) {
        dockPos = side;
//        System.err.println("SHOW DOCK PLACE = " + dockPane.getWidth());                

        dragTarget = getDockPane();
        switch (side) {
            case TOP:
//                System.err.println("showDockPlace: dockPane.getWidth() = " + dockPane.getWidth());                
//                System.err.println("showDockPlace: dockPane.getHeight() = " + dockPane.getHeight());                                
                dockPlace.setWidth(getDockPane().getWidth());
                dockPlace.setHeight(getDockPane().getHeight() / 2);
                //Point2D p = dockPlace.localToParent(0, 0);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
//                System.err.println("showDockPlace: dockPlace.getWidth() = " + dockPlace.getWidth());                
//                System.err.println("showDockPlace: dockPlace.getHeight() = " + dockPlace.getHeight());                                

                break;
            case BOTTOM:
                dockPlace.setWidth(getDockPane().getWidth());
                dockPlace.setHeight(getDockPane().getHeight() / 2);
                p = dockPlace.localToParent(0, getDockPane().getHeight() - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case LEFT:
                dockPlace.setWidth(getDockPane().getWidth() / 2);
                dockPlace.setHeight(getDockPane().getHeight());
                p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case RIGHT:
                dockPlace.setWidth(getDockPane().getWidth() / 2);
                dockPlace.setHeight(getDockPane().getHeight());
                p = dockPlace.localToParent(getDockPane().getWidth() - dockPlace.getWidth(), 0);
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

        Point2D p = target.localToScreen(0, 0).subtract(getDockPane().localToScreen(0, 0));
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

    public Rectangle getDockPlace() {
        return dockPlace;
    }

}
