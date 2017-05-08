package org.vns.javafx.dock.api;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
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
public class IndicatorPopup extends Popup {

    /**
     * The owner of this object
     */
    private final DockTargetController targetController;

    /**
     * Creates a new instance for the specified pane handler.
     *
     * @param targetController the owner of the object to be created
     */
    public IndicatorPopup(DockTargetController targetController) {
        this.targetController = targetController;
        init();

    }

    private void init() {
        initContent();
    }

    /**
     * Returns an object of type {@code Region} which corresponds to the pane
     * handler which used to create this object.
     *
     * @return Returns an object of type {@code Region}
     */
    public Region getTargetNode() {
        return targetController.getTargetNode();
    }

    protected void initContent() {
        Pane indicatorPane = targetController.getDockIndicator().getIndicatorPane();
        indicatorPane.setMouseTransparent(true);

        indicatorPane.prefHeightProperty().bind(getTargetNode().heightProperty());
        indicatorPane.prefWidthProperty().bind(getTargetNode().widthProperty());

        indicatorPane.minHeightProperty().bind(getTargetNode().heightProperty());
        indicatorPane.minWidthProperty().bind(getTargetNode().widthProperty());

        getContent().add(indicatorPane);
    }

    /**
     * Returns an object of type {@link DockIndicator} to display indicators for
     * an object of type {@link DockPaneTarget}.
     *
     * @return Returns an object of type {@code SideIndicator}
     */
    public DockIndicator getDockIndicator() {
        return targetController.getDockIndicator();
    }


    /**
     * Returns the owner of this object used when the instance created.
     *
     * @return the owner of this object used when the instance created.
     */
    public DockTargetController getTargetController() {
        return this.targetController;
    }

    /**
     * Shows this pop up window
     */
    //public void showPopup(Node dockNode) {
    public void showPopup() {
        setAutoFix(false);
        Point2D pos = getTargetNode().localToScreen(0, 0);
        getDockIndicator().showIndicator(pos.getX(), pos.getY());
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
    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (DockUtil.contains(getDockIndicator().getIndicatorPane(), x, y)) {
            retval = true;
        } else if (isShowing()) {
            hide();
        }
        return retval;
    }

    /**
     * The method is called when the the mouse moved during drag operation.
     *
     * @param screenX a screen mouse position
     * @param screenY a screen mouse position
     */
    public void handle(double screenX, double screenY) {
        setOnHiding(v -> {
        });

        //getDockIndicator().hideDockPlace();
        getDockIndicator().showDockPlace(screenX,screenY);
    }
    

    /**
     * Returns a shape of type {@code  Rectangle} to be displayed to showPopup a
     * proposed dock place
     *
     * @return a shape of type {@code  Rectangle} to be displayed to showPopup a
     * proposed dock place
     */
    public Node getDockPlace() {
        return targetController.getDockIndicator().getDockPlace();
    }

}
