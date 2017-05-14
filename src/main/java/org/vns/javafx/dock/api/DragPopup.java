package org.vns.javafx.dock.api;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.vns.javafx.dock.DockUtil;

/**
 * An instance of the class is created for each object of type
 * {@link DockTargetController} when the last is created.
 *
 * The instance of the class is used by the object of type {@link DragManager}
 * and provides a pop up window in which the user can select a position on the
 * screen where the dragged node will be placed. As a rule, the position is
 * determined as a relative position to the target object, which can be an
 * object of type {@link Dockable} or {@link DockPaneTarget}. The position of
 * the target object is set as a value of type {@code javafx.geometry.Side} the
 * object is given enum type Side and can take one of the values: Side.TOP,
 * Side.RIGHT, Side.BOTTOM or Side.LEFT.
 * <p>
 * Most of the work with the object of the class is done in the method 
 * {@link DragManager#mouseDragged(javafx.scene.input.MouseEvent) }. If the
 * mouse cursor resides above the {@code dockable node} then {@code DragPoup}
 * provides two panes of position indicators:
 * <ul>
 * <li>The pane of indicators for the {@code dockable} node</li>
 * <li>The pane of indicators for the {@code DockPaneTarget} object which is a
 * parent of the {@code dockable node} mentioned above
 * </li>
 * </ul>
 * </p>
 * If the mouse cursor resides above the {@code DockPaneTarget} then
 * {@code DragPoup} provides a single pane of position indicators.
 * <p>
 * Each pane generally comprises four indicators which are objects of type
 * Button. Every button is located on the top, right, bottom side of the
 * indicator pane. As noted above, the position of the button is determined in
 * terms of {@code enum Side} type.
 * </p>
 * When the user moves the mouse cursor and the cursor intersects one of the
 * buttons of the indicator pane, the rectangular area is displayed. This area
 * points to the position of the dragged object in case the user releases the
 * mouse button.
 * <p>
 * While dragging the object {@code Dockable} the drag manager object defines
 * the cursor position and calculates the object type {@code DockPaneTarget},
 * over which the mouse cursor is. For each recovered target it's own drag pop
 * up is used to display an indicator pane.Such approach allows for different
 * implementations of {@code DockPaneTarget} to use different types of indicator
 * pane. For instance, the class {@code org.vns.javafx.dock.DockTabPane} uses an
 * indicator pane containing a single button which moves with mouse cursor when
 * the later is above the {@code tab area} of the {@code TabPane} control. This
 * lets a user to insert a dragged object in the desired position or even
 * rearrange {@code Tab} objects.
 * </p>
 *
 * @author Valery Shyshkin
 */
public class DragPopup extends IndicatorPopup {

    /**
     * The owner of this object
     */
    //private final DockTargetController paneController;
    /**
     * The pop up window for dock nodes
     */
    private Popup nodeIndicatorPopup;
    /**
     * The current target to which the mouse cursor points
     */
    private Node dragTarget;

    /**
     * Current side position of the selected button in the node indicator pane
     * or null
     */
    private Side targetNodeSidePos;
    /**
     * Current side position of the selected button in the pane indicator pane
     * or null
     */
    private Side targetPaneSidePos;

    /**
     * Creates a new instance for the specified pane handler.
     *
     * @param paneController the owner of the object to be created
     */
    public DragPopup(DockTargetController paneController) {
        super(paneController);
    }

    /**
     * Returns an object of type {@code Region} which corresponds to the pane
     * handler which used to create this object.
     *
     * @return Returns an object of type {@code Region}
     */
    @Override
    public Region getTargetNode() {
        return getTargetController().getTargetNode();
    }

    @Override
    protected void initContent() {
        Pane paneIndicatorPane = ((DockPaneController)getTargetController()).getPositionIndicator().getIndicatorPane();
        paneIndicatorPane.setMouseTransparent(true);
        Pane nodeIndicatorPane = ((DockPaneController)getTargetController()).getNodeIndicator().getIndicatorPane();
        nodeIndicatorPane.setMouseTransparent(true);

        nodeIndicatorPopup = new Popup();

        getPaneIndicator().getIndicatorPane().prefHeightProperty().bind(getTargetNode().heightProperty());
        getPaneIndicator().getIndicatorPane().prefWidthProperty().bind(getTargetNode().widthProperty());

        getPaneIndicator().getIndicatorPane().minHeightProperty().bind(getTargetNode().heightProperty());
        getPaneIndicator().getIndicatorPane().minWidthProperty().bind(getTargetNode().widthProperty());
        nodeIndicatorPopup.getContent().add(nodeIndicatorPane);
        getContent().add(paneIndicatorPane);
    }

    /**
     * Returns an object of type {@link SideIndicator} to display indicators for
     * an object of type {@link DockPaneTarget}.
     *
     * @return Returns an object of type {@code SideIndicator}
     */
    public SideIndicator getPaneIndicator() {
        return (SideIndicator) getTargetController().getPositionIndicator();
    }

    /**
     * Returns an object of type {@link SideIndicator} to display indicators for
     * an object of type {@link Dockable}.
     *
     * @return Returns an object of type {@code SideIndicator}
     */
    public SideIndicator getNodeIndicator() {
        return ((DockPaneController)getTargetController()).getNodeIndicator();
    }

    /**
     * Returns a pop up window which is used to display a doc node side
     * indicators.
     *
     * @return a pop up window which is used to display a doc node side
     * indicators.
     */
    public Popup getNodeIndicatorPopup() {
        return nodeIndicatorPopup;
    }

    /**
     * Returns an object of type {@link Dockable} or {@link DockPaneTarget}
     * depending on the user selection or null if no object has been selected.
     *
     * @return a target object to dock to or null.
     */
    public Node getDragTarget() {
        return dragTarget;
    }


    /**
     * Returns a side position of the selected dock node if any.
     *
     * @return a side position of the selected dock node or null if no selection
     * has been done.
     */
    public Side getTargetNodeSidePos() {
        return targetNodeSidePos;
    }

    /**
     * Returns a side position of the selected dock pane target if any.
     *
     * @return a side position of the selected dock pane target or null if no
     * selection has been done.
     */
    public Side getTargetPaneSidePos() {
        return targetPaneSidePos;
    }

    /**
     * Shows this pop up window
     */
    @Override
    public void showPopup() {
        setAutoFix(false);
        Point2D pos = getTargetNode().localToScreen(0, 0);
        dragTarget = null;
        getPaneIndicator().showIndicator(pos.getX(), pos.getY());
    }

    /**
     * Hides the pop up window when some condition are satisfied. If this pop up
     * is hidden returns true. If the mouse cursor is still inside the pane
     * indicator then return true. Otherwise hides the pop up and returns false
     *
     * @param x a screen x coordinate of the mouse cursor
     * @param y a screen y coordinate of the mouse cursor
     *
     * @return If this pop up is hidden returns true. If the mouse cursor is
     * still inside the pane indicator then return true. Otherwise hides the pop
     * up and returns false
     */
    @Override
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

    /**
     * Checks whether the specified pane object contains the given screen
     * coordinates.
     *
     * @param buttonPane the pain object to be chacked
     * @param x a screen x coordinate
     * @param y a screen y coordinate
     * @return true if the specified pane contains the given screen coordinates
     */
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

    /**
     * Returns the button from the specified pane which contains the given
     * screen coordinates
     *
     * @param buttonPane the pane which resides on the indicator pane
     * @param x a screen x coordinate
     * @param y a screen y coordinate
     * @return the button which contains the specified screen coordinate or null
     * if no such button found
     */
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

    /**
     * The method is called when the the mouse moved during drag operation.
     *
     * @param screenX a screen mouse position
     * @param screenY a screen mouse position
     */
    @Override
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
        //
        // if the node exists but its property usedAsDockTarge==false then 
        // findDockable returns null
        //
        Region targetNode = (Region) DockUtil.findDockable(getTargetNode(), screenX, screenY);
        System.err.println("Dragpopup targetNode = " + targetNode);
        getNodeIndicator().showIndicator(screenX, screenY, targetNode);

        getPaneIndicator().hideDockPlace();

        dragTarget = null;
        targetNodeSidePos = null;
        targetPaneSidePos = null;

        Button btn;
        if (nodeIndicatorPopup.isShowing()) {
            if ((btn = getSelectedButton(getNodeIndicator().getTopButtons(), screenX, screenY)) != null) {
                showDockPlace(btn, targetNode, Side.TOP);
            } else if ((btn = getSelectedButton(getNodeIndicator().getLeftButtons(), screenX, screenY)) != null) {
                showDockPlace(btn, targetNode, Side.LEFT);
            } else if ((btn = getSelectedButton(getNodeIndicator().getRightButtons(), screenX, screenY)) != null) {
                showDockPlace(btn, targetNode, Side.RIGHT);
            } else if ((btn = getSelectedButton(getNodeIndicator().getBottomButtons(), screenX, screenY)) != null) {
                showDockPlace(btn, targetNode, Side.BOTTOM);
            } else if ((btn = getSelectedButton(getNodeIndicator().getCenterButtons(), screenX, screenY)) != null) {
                getNodeIndicator().showDockPlace(targetNode, screenX, screenY);
            } else {
                getDockPlace().setVisible(false);
            }

            if (dragTarget != null) {
                return;
            }
        }

        if ((btn = getSelectedButton(getPaneIndicator().getTopButtons(), screenX, screenY)) != null) {
            showDockPlace(btn, Side.TOP);
        } else if ((btn = getSelectedButton(getPaneIndicator().getLeftButtons(), screenX, screenY)) != null) {
            showDockPlace(btn, Side.LEFT);
        } else if ((btn = getSelectedButton(getPaneIndicator().getRightButtons(), screenX, screenY)) != null) {
            showDockPlace(btn, Side.RIGHT);
        } else if ((btn = getSelectedButton(getPaneIndicator().getBottomButtons(), screenX, screenY)) != null) {
            showDockPlace(btn, Side.BOTTOM);
        } else if ((btn = getSelectedButton(getPaneIndicator().getCenterButtons(), screenX, screenY)) != null) {
            getPaneIndicator().showDockPlace(targetNode, screenX, screenY);
        } else {
            getDockPlace().setVisible(false);
        }
        ((Rectangle) getDockPlace()).strokeDashOffsetProperty().set(0);
        if (getDockPlace().isVisible()) {
            Timeline placeTimeline = new Timeline();
            placeTimeline.setCycleCount(Timeline.INDEFINITE);
            KeyValue kv = new KeyValue(((Rectangle) getDockPlace()).strokeDashOffsetProperty(), 12);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            placeTimeline.getKeyFrames().add(kf);
            placeTimeline.play();
        }
    }

    /**
     * Displays a rectangle that shows a proposed place to dock a dragged node.
     *
     * @param selected the selected button which resides on the pane indicate
     * pane.
     *
     * @param side the position of the button on the pane indicator pane
     */
    public void showDockPlace(Button selected, Side side) {
        //dockPos = side;           
        targetPaneSidePos = side;
        getPaneIndicator().showDockPlace(selected, side);
        Region pane = getTargetNode();
        if (selected != null && selected.getUserData() != null) {
            pane = ((DockTargetController) selected.getUserData()).getTargetNode();
        }
        dragTarget = pane;
    }

    /**
     * Displays a rectangle that shows a proposed place to dock a dragged node.
     *
     * @param selected the selected button which resides on the node indicate
     * pane.
     *
     * @param target a dock node which is used as a target to dock
     * @param side the position of the button on the node indicator pane
     */
    public void showDockPlace(Button selected, Region target, Side side) {
        targetNodeSidePos = null;
        if (target == null) {
            return;
        }
        //dockPos = side;
        targetNodeSidePos = side;
        getNodeIndicator().showDockPlace(selected, target, side);
        dragTarget = target;

    }

    /**
     * Returns a shape of type {@code  Rectangle} to be displayed to showPopup a
     * proposed dock place
     *
     * @return a shape of type {@code  Rectangle} to be displayed to showPopup a
     * proposed dock place
     */
    @Override
    public Rectangle getDockPlace() {
        return (Rectangle) getTargetController().getPositionIndicator().getDockPlace();
    }

}
