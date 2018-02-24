package org.vns.javafx.dock.api.indicator;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.TargetContext;

/**
 * An instance of the class is created for each object of type
 * {@link TargetContext} when the last is created.
 *
 * The instance of the class is used by the object of type
 * {@link org.vns.javafx.dock.api.dragging.DragManager} and provides a pop up
 * window in which the user can select a position on the screen where the
 * dragged node will be placed. As a rule, the position is determined as a
 * relative position to the target object, which can be an object of type
 * {@link org.vns.javafx.dock.api.Dockable} or
 * {@link org.vns.javafx.dock.api.DockTarget}. The position of the target object
 * is set as a value of type {@code javafx.geometry.Side} the object is given
 * enum type Side and can take one of the values: Side.TOP, Side.RIGHT,
 * Side.BOTTOM or Side.LEFT.
 * <p>
 * Most of the work with the object of the class is done in the method
 * DragManager.mouseDragged(MouseEvent) . If the mouse cursor resides above the
 * {@code dockable node} then {@code DragPoup} provides two panes of position
 * indicators:
 * </P>
 * <ul>
 * <li>The pane of indicators for the {@code dockable} node</li>
 * <li>The pane of indicators for the {@code DockPaneTarget} object which is a
 * parent of the {@code dockable node} mentioned above
 * </li>
 * </ul>
 *
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
public class IndicatorPopup extends Popup implements IndicatorManager {

    /**
     * The owner of this object
     */
    private final TargetContext targetContext;

    private Node draggedNode;

    @Override
    public Node getDraggedNode() {
        return draggedNode;
    }

    @Override
    public void setDraggedNode(Node draggedNode) {
        this.draggedNode = draggedNode;
    }

    private final ObservableList<IndicatorPopup> childWindows = FXCollections.observableArrayList();

    /**
     * Creates a new instance for the specified target context.
     *
     * @param target the owner of the object to be created
     */
    public IndicatorPopup(TargetContext target) {
        this.targetContext = target;
        init();
    }

    private void init() {
        initContent();
    }

    public static IndicatorPopup getInstance(TargetContext context) {
        return context.getLookup().lookup(IndicatorPopup.class);
    }

    @Override
    public void show(Window ownerWindow) {
        if (!(ownerWindow instanceof IndicatorPopup)) {
            throw new IllegalStateException("The parameter 'ownerWindow' must be of type " + getClass().getName());
        }

        super.show(ownerWindow);
        if (getChildWindows().contains(ownerWindow)) {
            return;
        }

        check((IndicatorPopup) ownerWindow);
        getChildWindows().add((IndicatorPopup) ownerWindow);

    }

    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        if (!(ownerWindow instanceof IndicatorPopup)) {
            throw new IllegalStateException("The parameter 'ownerWindow' must be of type " + getClass().getName());
        }
        
        System.err.println("OWNER WINDOW = " + ownerWindow);
        super.show(ownerWindow, anchorX, anchorY);

        if (((IndicatorPopup) ownerWindow).getChildWindows().contains(this)) {
            return;
        }
        ((IndicatorPopup) ownerWindow).getChildWindows().add(this);
    }

    private void check(IndicatorPopup popup) {
        IndicatorPopup p = popup;
        while (p != null) {
            if (!(p.getOwnerWindow() instanceof IndicatorPopup)) {
                break;
            }
            if (p.getChildWindows().contains(popup)) {
                p.getChildWindows().remove(popup);
            }
            p = (IndicatorPopup) p.getOwnerWindow();
        }
    }

    @Override
    public void show(Node ownerNode, double anchorX, double anchorY) {
        System.err.println("SHOW ownerNode = " + ownerNode);
        super.show(ownerNode.getScene().getWindow(), anchorX, anchorY);
    }

    @Override
    public void hide() {
        if (getOwnerWindow() instanceof IndicatorPopup) {
            IndicatorPopup p = (IndicatorPopup) getOwnerWindow();
        }
        super.hide();
    }

    public ObservableList<IndicatorPopup> getChildWindows() {
        return childWindows;
    }

    /**
     * Returns an object of type {@code Region} which corresponds to the pane
     * handler which used to create this object.
     *
     * @return Returns an object of type {@code Region}
     */
    public Node getTargetNode() {
        return targetContext.getTargetNode();
    }

    protected void initContent() {
        setOnShown(e -> {
            if (targetContext.getPositionIndicator() == null || targetContext.getPositionIndicator().getIndicatorPane() == null) {
                return;
            }
            if (targetContext.getPositionIndicator().getIndicatorPane() == null) {
                return;
            }

            Pane indicatorPane = targetContext.getPositionIndicator().getIndicatorPane();

            if (getTargetNode() instanceof Region) {
                indicatorPane.prefHeightProperty().bind(((Region) getTargetNode()).heightProperty());
                indicatorPane.prefWidthProperty().bind(((Region) getTargetNode()).widthProperty());

                indicatorPane.minHeightProperty().bind(((Region) getTargetNode()).heightProperty());
                indicatorPane.minWidthProperty().bind(((Region) getTargetNode()).widthProperty());
            } else {
                getTargetNode().layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
                    indicatorPane.setPrefHeight(newValue.getHeight());
                    indicatorPane.setPrefWidth(newValue.getWidth());
                    indicatorPane.setMinHeight(newValue.getHeight());
                    indicatorPane.setMinWidth(newValue.getWidth());

                });
            }
            indicatorPane.setMouseTransparent(true);
            if (!getContent().contains(indicatorPane)) {
                getContent().add(indicatorPane);
            }
        });
    }

    public ObservableList<IndicatorPopup> getAllChildIndicatorPopup() {
        ObservableList<IndicatorPopup> list = FXCollections.observableArrayList();

        list.addAll(getChildWindows());
        getChildWindows().forEach(w -> {
            getAllChildIndicatorPopup(list, w);
        });
        return list;
    }

    private void getAllChildIndicatorPopup(ObservableList<IndicatorPopup> list, IndicatorPopup popup) {
        list.addAll(popup.getChildWindows());
    }

    /**
     * Returns an object of type {@link PositionIndicator} to display indicators
     * for an object of type {@link org.vns.javafx.dock.api.DockPaneContext }
     *
     * @return Returns an object of type {@code PositionIndicator}
     */
    @Override
    public PositionIndicator getPositionIndicator() {
        return targetContext.getPositionIndicator();
    }

    /**
     * Returns the owner of this object used when the instance created.
     *
     * @return the owner of this object used when the instance created.
     */
    @Override
    public TargetContext getTargetContext() {
        return this.targetContext;
    }

    /**
     * Shows this pop up window
     */
    @Override
    public void showIndicator() {
        if (getPositionIndicator() == null) {
            return;
        }
        setAutoFix(false);
        Point2D pos = getTargetNode().localToScreen(0, 0);
        getPositionIndicator().showIndicator(pos.getX(), pos.getY());
    }

    
    @Override
    public void showIndicator(Node targetNode) {
        if (getPositionIndicator() == null) {
            return;
        }
        setAutoFix(false);
        Point2D pos = getTargetNode().localToScreen(0, 0);
        getPositionIndicator().showIndicator(pos.getX(), pos.getY(), targetNode);
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
        if (DockUtil.contains(getPositionIndicator().getIndicatorPane(), x, y)) {
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
    @Override
    public void handle(double screenX, double screenY) {
        if (getPositionIndicator() == null) {
            return;
        }
        getPositionIndicator().showDockPlace(screenX, screenY);
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
     * Returns a shape of type {@code  Rectangle} to be displayed to
     * showIndicator a proposed dock place
     *
     * @return a shape of type {@code  Rectangle} to be displayed to
     * showIndicator a proposed dock place
     */
    public Node getDockPlace() {
        return targetContext.getPositionIndicator().getDockPlace();
    }

}
