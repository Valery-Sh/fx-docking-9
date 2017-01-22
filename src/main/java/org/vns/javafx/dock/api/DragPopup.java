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
import org.vns.javafx.dock.api.PaneHandler.SideIndicator;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup {

    private final PaneHandler paneHandler;

    private Popup nodeIndicatorPopup;

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
        initContent();
    }

    public Region getDockPane() {
        return paneHandler.getDockPane();
    }

    public void initContent() {
        Pane paneIndicatorPane = paneHandler.getPaneIndicator().getIndicatorPane();
        paneIndicatorPane.setMouseTransparent(true);
        Pane nodeIndicatorPane = paneHandler.getNodeIndicator().getIndicatorPane();
        nodeIndicatorPane.setMouseTransparent(true);

        nodeIndicatorPopup = new Popup();

        getPaneIndicator().getIndicatorPane().prefHeightProperty().bind(getDockPane().heightProperty());
        getPaneIndicator().getIndicatorPane().prefWidthProperty().bind(getDockPane().widthProperty());

        getPaneIndicator().getIndicatorPane().minHeightProperty().bind(getDockPane().heightProperty());
        getPaneIndicator().getIndicatorPane().minWidthProperty().bind(getDockPane().widthProperty());
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

    public Node getDragTarget() {
        return dragTarget;
    }

    public PaneHandler getPaneHandler() {
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
        setOnShown(e -> {getPaneIndicator().onShown(e, (Region) dockable);});
        setOnShowing(e -> {getPaneIndicator().onShown(e, (Region) dockable);});
        this.show(getDockPane(), pos.getX(), pos.getY());
        getPaneIndicator().afterShow((Region) dockable);
    }


    @Override
    public void hide() {
        super.hide();
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
        for (Node node : buttonPane.getChildren()) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button b = (Button) node;
            Bounds bnd = b.getBoundsInParent();
            if (bnd.contains(point)) {
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
        setOnHiding(v -> {
            dragTarget = null;
        });
        //
        // Try to find a Dockable object which paneIndicatorContains the specifired 
        // (x,y) coordinates
        // The result may be null
        //
        dragTarget = null;
        Region dockNode = (Region) DockUtil.findDockable(getDockPane(), screenX, screenY);
        Point2D newPos;

        nodeIndicatorPopup.setOnShowing(e -> {getNodeIndicator().onShowing(e, dockNode);});
        nodeIndicatorPopup.setOnShown(e -> getNodeIndicator().onShown(e, dockNode));
        nodeIndicatorPopup.setOnHiding(e -> getNodeIndicator().onHiding(e, null));        
        nodeIndicatorPopup.setOnHidden(e -> getNodeIndicator().onHidden(e, null));        

        if (dockNode != null) {
            newPos = getNodeIndicator().mousePosBy(dockNode, screenX, screenY);
            getNodeIndicator().beforeShow(dockNode);
            nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());
            getNodeIndicator().afterShow(dockNode);
        } else {
            newPos = getNodeIndicator().mousePosBy(screenX, screenY);
            if (newPos != null) {
                getNodeIndicator().beforeShow(dockNode);
                nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());
                getNodeIndicator().afterShow(dockNode);
            } else {
                nodeIndicatorPopup.hide();
            }
        }
        
        getPaneIndicator().hideDockPlace();
        
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

        Button btn;
        
        if ( (btn = getSelectedButton(getPaneIndicator().getTopButtons(), screenX, screenY)) != null) {
            showDockPlace(btn,Side.TOP);
        } else if ((btn = getSelectedButton(getPaneIndicator().getLeftButtons(), screenX, screenY)) != null) {
            showDockPlace(btn,Side.LEFT);
        } else if ((btn = getSelectedButton(getPaneIndicator().getRightButtons(), screenX, screenY)) != null) {
            showDockPlace(btn,Side.RIGHT);
        } else if ((btn = getSelectedButton(getPaneIndicator().getBottomButtons(), screenX, screenY)) != null) {
            showDockPlace(btn,Side.BOTTOM);
        } else if ((btn = getSelectedButton(getPaneIndicator().getCenterButtons(), screenX, screenY)) != null) {
            getPaneIndicator().showDockPlace(dockNode, screenX, screenY);
        } else {
            //dockPlace.setVisible(false);
            getDockPlace().setVisible(false);
        }
        targetPaneSidePos = dockPos;
    }
    public void showDockPlace(Button selected,Side side) {
        dockPos = side;

        getPaneIndicator().showDockPlace(selected, side);
        Region pane = getDockPane();
        if ( selected != null && selected.getUserData() != null ) {
             pane = ((PaneHandler) selected.getUserData()).getDockPane();
        }             
        dragTarget = pane;
    }

    public void showDockPlace(Region target, Side side) {
        if (target == null) {
            return;
        }
        dockPos = side;
        dragTarget = target;

        Point2D p = target.localToScreen(0, 0).subtract(getDockPane().localToScreen(0, 0));
        getDockPlace().setX(p.getX());
        getDockPlace().setY(p.getY());

        switch (side) {
            case TOP:
                getDockPlace().setWidth(target.getWidth());
                getDockPlace().setHeight(target.getHeight() / 2);
                break;
            case BOTTOM:
                getDockPlace().setWidth(target.getWidth());
                getDockPlace().setHeight(target.getHeight() / 2);
                getDockPlace().setY(p.getY() + getDockPlace().getHeight());
                break;
            case LEFT:
                getDockPlace().setWidth(target.getWidth() / 2);
                getDockPlace().setHeight(target.getHeight());
                break;
            case RIGHT:
                getDockPlace().setWidth(target.getWidth() / 2);
                getDockPlace().setHeight(target.getHeight());
                getDockPlace().setX(p.getX() + getDockPlace().getWidth());
                break;
            default:
                dragTarget = null;
                dockPos = null;
        }
        getDockPlace().setVisible(true);
    }

    public Rectangle getDockPlace() {
        return paneHandler.getPaneIndicator().getDockPlace();
    }

}
