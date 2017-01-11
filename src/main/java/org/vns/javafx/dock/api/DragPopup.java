package org.vns.javafx.dock.api;

import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.PaneHandler.SidePointerModifier;

/**
 *
 * @author Valery
 */
public class DragPopup extends Popup {

    
    Pane topButtons;
    Pane bottomButtons;
    Pane leftButtons;
    Pane rightButtons;

    Pane nodeTopButtons;
    Pane nodeBottomButtons;
    Pane nodeLeftButtons;
    Pane nodeRightButtons;

    private Region dockPane;
    private PaneHandler paneHandler;
    
    private BorderPane popupPane;

    private Popup nodeSidePointerPopup;
    private GridPane sidePointerGrid;

    private Node dockable;
    private Rectangle dockPlace;

    private Node dragTarget;
    private Side targetNodeSidePos;
    private Side targetPaneSidePos;
    
    private PaneHandler dragTargetPaneHandler;    
    
    private Side dockPos;

    //private SidePointerModifier onModifySideIndicator;
    public DragPopup() {
        init();
    }

    private void init() {

        popupPane = new BorderPane();
        getContent().add(popupPane);
        popupPane.setMouseTransparent(true);

        popupPane.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");

        dockPlace = new Rectangle();
        dockPlace.setManaged(false);
        dockPlace.getStyleClass().add("dock-place");
        popupPane.getChildren().add(dockPlace);

        topButtons = createSideButtons(Side.TOP);
        popupPane.setTop(topButtons);
        rightButtons = createSideButtons(Side.RIGHT);
        popupPane.setRight(rightButtons);
        bottomButtons = createSideButtons(Side.BOTTOM);
        popupPane.setBottom(bottomButtons);
        leftButtons = createSideButtons(Side.LEFT);
        popupPane.setLeft(leftButtons);

        nodeSidePointerPopup = new Popup();

        sidePointerGrid = new GridPane();
        sidePointerGrid.getStyleClass().add("dock-target-pos");

        nodeTopButtons = createSideNodeButtons(Side.TOP);
        sidePointerGrid.add(nodeTopButtons, 1, 0);

        nodeBottomButtons = createSideNodeButtons(Side.BOTTOM);
        sidePointerGrid.add(nodeBottomButtons, 1, 2);

        nodeLeftButtons = createSideNodeButtons(Side.LEFT);
        sidePointerGrid.add(nodeLeftButtons, 0, 1);

        nodeRightButtons = createSideNodeButtons(Side.RIGHT);
        sidePointerGrid.add(nodeRightButtons, 2, 1);

        nodeSidePointerPopup.getContent().add(sidePointerGrid);
    }

    public Button getPaneSideButton(Side side) {
        Pane p = getPaneSideButtons(side);
        if (p == null) {
            return null;
        }
        return (Button) p.getChildren().get(0);
    }

    protected Pane createSideButtons(Side side) {
        Button b = new Button();
        StackPane p = new StackPane(b);
        switch (side) {
            case TOP:
                b.getStyleClass().add("dock-pos-top");
                break;
            case BOTTOM:
                b.getStyleClass().add("dock-pos-bottom");
                break;
            case LEFT:
                b.getStyleClass().add("dock-pos-left");
                break;
            case RIGHT:
                b.getStyleClass().add("dock-pos-right");
                break;
        }
        return p;
    }

    protected Pane createSideNodeButtons(Side side) {
        Button b = new Button();
        StackPane p = new StackPane(b);
        switch (side) {
            case TOP:
                b.getStyleClass().add("dock-node-pos-top");
                break;
            case BOTTOM:
                b.getStyleClass().add("dock-node-pos-bottom");
                break;
            case LEFT:
                b.getStyleClass().add("dock-node-pos-left");
                break;
            case RIGHT:
                b.getStyleClass().add("dock-node-pos-right");
                break;
        }
        return p;
    }

    public Pane getRoot() {
        return popupPane;
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

    public GridPane getSidePointerGrid() {
        return sidePointerGrid;
    }

    public Pane getNodeSideButtons(Side side) {
        Pane retval = null;
        if (null != side) {
            switch (side) {
                case TOP:
                    retval = nodeTopButtons;
                    break;
                case BOTTOM:
                    retval = nodeBottomButtons;
                    break;
                case LEFT:
                    retval = nodeLeftButtons;
                    break;
                case RIGHT:
                    retval = nodeRightButtons;
                    break;
            }
        }
        return retval;
    }

    public Pane getPaneSideButtons(Side side) {
        Pane retval = null;
        if (null != side) {
            switch (side) {
                case TOP:
                    retval = topButtons;
                    break;
                case BOTTOM:
                    retval = bottomButtons;
                    break;
                case LEFT:
                    retval = leftButtons;
                    break;
                case RIGHT:
                    retval = rightButtons;
                    break;
            }
        }
        return retval;
    }

    public Pane removeNodeSideButtons(Side side) {

        Pane retval = getNodeSideButtons(side);
        if (retval != null) {
            sidePointerGrid.getChildren().remove(retval);
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
    public void addNodeSideButtons(Pane buttonPane, Side side) {

        switch (side) {
            case TOP:
                sidePointerGrid.add(buttonPane, 1, 0);
                break;
            case BOTTOM:
                sidePointerGrid.add(buttonPane, 1, 2);
                break;
            case LEFT:
                sidePointerGrid.add(buttonPane, 0, 1);
                break;
            case RIGHT:
                sidePointerGrid.add(buttonPane, 2, 1);
                break;

        }
    }

    public void show(PaneHandler paneHandler, Node dockable) {
        setAutoFix(false);
        this.dockPane = paneHandler.getDockPane();
        this.paneHandler = paneHandler;
        //this.dockable = dockable;

        popupPane.prefHeightProperty().bind(dockPane.heightProperty());
        popupPane.prefWidthProperty().bind(dockPane.widthProperty());

        popupPane.minHeightProperty().bind(dockPane.heightProperty());
        popupPane.minWidthProperty().bind(dockPane.widthProperty());

        Point2D pos = dockPane.localToScreen(0, 0);
        dragTarget = null;
        this.show(dockPane, pos.getX(), pos.getY());

    }

    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (contains(x, y)) {
            retval = true;
        } else {
            hide();
        }
        return retval;
    }

    public boolean contains(double x, double y) {
        if (!isShowing()) {
            return false;
        }
        Point2D p = popupPane.localToScreen(0, 0);
        //System.err.println("CONTAINS p=" + p);
        return !((x < p.getX() || x > p.getX() + popupPane.getWidth()
                || y < p.getY() || y > p.getY() + popupPane.getHeight()));
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

    public boolean contains(Pane buttonPane, double x, double y) {
        if (buttonPane == null) {
            return false;
        }

        Point2D p = buttonPane.localToScreen(0, 0);
        if (p == null) {
            return false;
        }
        boolean retval = false;
        for (Node node : buttonPane.getChildren()) {
            if (!(node instanceof Button)) {
                continue;
            }
            Button b = (Button) node;
            p = node.localToScreen(0, 0);
            if (!((x < p.getX() || x > p.getX() + b.getWidth()
                    || y < p.getY() || y > p.getY() + b.getHeight()))) {
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
        // Try to find a Dockable object which contains the specifired 
        // (x,y) coordinates
        // The result may be null
        //
        dragTarget = null;
        Region d = (Region) DockUtil.findDockable(dockPane, screenX, screenY);
        //System.err.println("DragPopup region findDockable = " + d);
        Point2D newPos = null;// = d.localToScreen((d.getWidth() - sidePointerGrid.getWidth()) / 2, (d.getHeight() - sidePointerGrid.getHeight()) / 2);

        SidePointerModifier pm = paneHandler.getSidePointerModifier();
        if (pm != null) {
            newPos = pm.modify(this, DockRegistry.dockable(d), screenX, screenY);
            System.err.println("DragPopup newPos=" + newPos);                        
            System.err.println("Dockable=" + d);                        
        }

        if (newPos != null) {
            nodeSidePointerPopup.show(this, newPos.getX(), newPos.getY());
        } else if (d != null) {
            newPos = d.localToScreen((d.getWidth() - sidePointerGrid.getWidth()) / 2, (d.getHeight() - sidePointerGrid.getHeight()) / 2);
            nodeSidePointerPopup.show(this, newPos.getX(), newPos.getY());
        } 
            
        dockPlace.setVisible(false);
        dragTarget = null;
        targetNodeSidePos = null;
        targetPaneSidePos = null;
        
        if (contains(nodeTopButtons, screenX, screenY)) {
            showDockPlace(d, Side.TOP);
        } else if (contains(nodeLeftButtons, screenX, screenY)) {
            showDockPlace(d, Side.LEFT);
        } else if (contains(nodeRightButtons, screenX, screenY)) {
            showDockPlace(d, Side.RIGHT);
        } else if (contains(nodeBottomButtons, screenX, screenY)) {
            showDockPlace(d, Side.BOTTOM);
        }

        if (dragTarget != null) {
            targetNodeSidePos = dockPos;
            return;
        }
        //System.err.println("DragPopup dragTarget = " + dragTarget);
        if (contains(topButtons, screenX, screenY)) {
            showDockPlace(Side.TOP);
        } else if (contains(leftButtons, screenX, screenY)) {
            showDockPlace(Side.LEFT);
        } else if (contains(rightButtons, screenX, screenY)) {
            showDockPlace(Side.RIGHT);
        } else if (contains(bottomButtons, screenX, screenY)) {
            showDockPlace(Side.BOTTOM);
        } else {
            dockPlace.setVisible(false);
            //dragTarget = null;
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
            default :    
                dragTarget = null;
                dockPos = null;
        }
        dockPlace.setVisible(true);
    }

    public Region getDockPane() {
        return dockPane;
    }

}
