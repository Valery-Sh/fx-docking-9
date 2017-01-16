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

    public DragPopup(SideIndicator paneIndicator, SideIndicator nodeIndicator) {
        this.paneIndicator = paneIndicator;
        this.nodeIndicator = nodeIndicator;

        init();

    }

    private void init() {

    }

    public void initContent() {
        Pane paneIndicatorPane = paneIndicator.getIndicatorPane();
        paneIndicatorPane.setMouseTransparent(true);
        paneIndicatorPane.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");

        Pane nodeIndicatorPane = nodeIndicator.getIndicatorPane();
        nodeIndicatorPane.setStyle("-fx-border-width: 2.0; -fx-border-color: red;");
        nodeIndicatorPane.setMouseTransparent(true);

        //nodeIndicator.getIndicatorPane().setScaleX(0.5);
        //nodeIndicator.getIndicatorPane().setScaleY(0.5);
        getContent().add(paneIndicatorPane);

        dockPlace = new Rectangle();
        dockPlace.setManaged(false);
        dockPlace.getStyleClass().add("dock-place");
        paneIndicatorPane.getChildren().add(dockPlace);

        nodeIndicatorPopup = new Popup();

        nodeIndicatorPopup.getContent().add(nodeIndicatorPane);

    }

    /*    public ObjectProperty<PositionPointer> nodePointerObjectProperty() {
        return nodePointerProperty;
    }
    public SideIndicator getNodePointer() {
        return nodePointerProperty.get();
    }
    public void setNodePointer(SideIndicator positionPointer) {
        this.nodePointerProperty.set(positionPointer);
    }    
    public ObjectProperty<PositionPointer> nodePointerAddOnObjectProperty() {
        return nodePointerAddOnProperty;
    }
    public SideIndicator getNodePointerAddOn() {
        return nodePointerAddOnProperty.get();
    }
    public void setNodePointerAddOn(SideIndicator positionPointer) {
        this.nodePointerAddOnProperty.set(positionPointer);
    }    
    
    
    public ObjectProperty<PositionPointer> panePointerObjectProperty() {
        return panePointerProperty;
    }
    public SideIndicator getPanePointer() {
        return panePointerProperty.get();
    }
    public void setPanePointer(SideIndicator positionPointer) {
        this.panePointerProperty.set(positionPointer);
    }    
     */
 /*    public Button getPaneSideButton(Side side) {
        Pane p = getPaneSideButtons(side);
        if (p == null) {
            return null;
        }
        return (Button) p.getChildren().get(0);
    }
     */
    public SideIndicator getPaneIndicator() {
        return paneIndicator;
    }

    public SideIndicator getNodeIndicator() {
        return nodeIndicator;
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


    /*    public Pane removeNodeSideButtons(Side side) {

        Pane retval = getNodeSideButtons(side);
        if (retval != null) {
            nodeIndicatorPane.getChildren().remove(retval);
        }
        return retval;
    }

    public Pane removePaneSideButtons(Side side) {

        Pane retval = getPaneSideButtons(side);
        if (retval != null) {
            popupPane.getChildren().remove(retval);
        }
        return retval;
    }
     */
 /*    public void addPaneSideButton(Button button, Side side) {
        popupPane.getChildren().add(button);
        switch (side) {
            case TOP:
                StackPane.setAlignment(button, Pos.TOP_CENTER);
                break;
            case BOTTOM:
                StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
                break;
            case LEFT:
                StackPane.setAlignment(button, Pos.CENTER_LEFT);
                break;
            case RIGHT:
                StackPane.setAlignment(button, Pos.CENTER_RIGHT);
                break;

        }
    }
     */
 /*    public void addNodeSideButtons(Pane buttonPane, Side side) {

        switch (side) {
            case TOP:
                nodeIndicatorPane.add(buttonPane, 1, 0);
                break;
            case BOTTOM:
                nodeIndicatorPane.add(buttonPane, 1, 2);
                break;
            case LEFT:
                nodeIndicatorPane.add(buttonPane, 0, 1);
                break;
            case RIGHT:
                nodeIndicatorPane.add(buttonPane, 2, 1);
                break;

        }
    }
     */
    public void show(PaneHandler paneHandler, Node dockable) {
        if (paneHandler != this.paneHandler) {
            paneIndicator = new SideIndicator(SideIndicator.PANE_POINTER);
            nodeIndicator = new SideIndicator(SideIndicator.NODE_POINTER);
            initContent();
        }

        setAutoFix(false);
        this.dockPane = paneHandler.getDockPane();
        this.paneHandler = paneHandler;

        paneIndicator.getIndicatorPane().prefHeightProperty().bind(dockPane.heightProperty());
        paneIndicator.getIndicatorPane().prefWidthProperty().bind(dockPane.widthProperty());

        paneIndicator.getIndicatorPane().minHeightProperty().bind(dockPane.heightProperty());
        paneIndicator.getIndicatorPane().minWidthProperty().bind(dockPane.widthProperty());

        Point2D pos = dockPane.localToScreen(0, 0);
        dragTarget = null;
        this.show(dockPane, pos.getX(), pos.getY());
        paneIndicator.sideIndicatorShowing(paneHandler, null);
    }

    @Override
    public void hide() {
        super.hide();
        paneIndicator.sideIndicatorHiding(paneHandler, null);
    }

    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
//        if (paneIndicatorContains(x, y)) {
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

        for (Node node : buttonPane.getChildren()) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button b = (Button) node;
            Bounds bnd = b.getBoundsInParent();

            Point2D pl = b.screenToLocal(x, y);
            Point2D lp = b.localToParent(pl);
            if (bnd.contains(point)) {
                retval = true;
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
        Region dockNode = (Region) DockUtil.findDockable(dockPane, screenX, screenY);
        paneIndicator.targetNodeChanged(getTargetPaneHandler(),dockNode,screenX, screenY);
        nodeIndicator.targetNodeChanged(getTargetPaneHandler(),dockNode,screenX, screenY);

        Point2D newPos;// = null;// = dockNode.localToScreen((dockNode.getWidth() - nodeIndicatorPane.getWidth()) / 2, (dockNode.getHeight() - nodeIndicatorPane.getHeight()) / 2);

        if (dockNode != null) {
            //nodeIndicator.transform(getTargetPaneHandler(), dockNode, screenX, screenY);
            //newPos = nodeIndicator.getMousePos();
            newPos = nodeIndicator.mousePosBy(getTargetPaneHandler(), dockNode, screenX, screenY);
            nodeIndicatorPopup.show(this, newPos.getX(), newPos.getY());
            nodeIndicator.sideIndicatorShowing(getTargetPaneHandler(), dockNode);
        } else {
            nodeIndicatorPopup.hide();
            nodeIndicator.sideIndicatorHiding(getTargetPaneHandler(), null);
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
            }

            if (dragTarget != null) {
                targetNodeSidePos = dockPos;
                return;
            }
        }
        
        if (contains(paneIndicator.getTopButtons(), screenX, screenY)) {
            showDockPlace(Side.TOP);
        } else if (contains(paneIndicator.getLeftButtons(), screenX, screenY)) {
            showDockPlace(Side.LEFT);
        } else if (contains(paneIndicator.getRightButtons(), screenX, screenY)) {
            showDockPlace(Side.RIGHT);
        } else if (contains(paneIndicator.getBottomButtons(), screenX, screenY)) {
            showDockPlace(Side.BOTTOM);
        } else {
            dockPlace.setVisible(false);
        }
        targetPaneSidePos = dockPos;
    }

    public void showDockPlace(Side side) {
        dockPos = side;

        dragTarget = dockPane;
        switch (side) {
            case TOP:
                dockPlace.setWidth(dockPane.getWidth());
                dockPlace.setHeight(dockPane.getHeight() / 2);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
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

}
