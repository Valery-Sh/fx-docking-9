package org.vns.javafx.dock.api;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public abstract class SideIndicatorTransformer {

    private Region targetNode;
    private Point2D mousePos;
    private Pane topButtons;
    private Pane rightButtons;
    private Pane bottomButtons;
    private Pane leftButtons;
    private Pane centerButtons;
    private SideIndicator indicator;

    private final Scale smallbuttonsScale;

    public SideIndicatorTransformer() {
        //this.targetPaneHandler = targetPaneHandler;
        this.smallbuttonsScale = new Scale(0.5, 0.5);
    }

    /**
     *
     * @param indicator
     * @param mousePos may be null for instance when default transformer
     * @param topButtons
     * @param bottomButtons
     * @param leftButtons
     * @param rightButtons
     */
    public void initialize(SideIndicator indicator, Point2D mousePos, Pane topButtons, Pane bottomButtons, Pane leftButtons, Pane rightButtons) {
        this.indicator = indicator;

        this.mousePos = mousePos;
        this.topButtons = topButtons;

        this.bottomButtons = bottomButtons;
        this.leftButtons = leftButtons;
        this.rightButtons = rightButtons;
    }

    public PaneHandler getTargetPaneHandler() {
        return getIndicator().getPaneHandler();
    }

    public Region getTargetNode() {
        return targetNode;
    }

    public SideIndicator getIndicator() {
        return indicator;
    }

    public Scale getSmallbuttonsScale() {
        return smallbuttonsScale;
    }

    public void transform() {
    }

    protected void notifyPopupShown() {
    }

    protected void notifyPopupShowing() {
    }

    protected void notifyPopupHiding() {
    }

    protected void notifyPopupHidden() {
    }

    protected void notifyBeforeShow() {
    }

    protected void notifyAfterShow() {
    }

    protected Point2D getIndicatorPosition() {
        Point2D newPos = null;
        if (targetNode != null && indicator.getIndicatorPane() != null) {
            newPos = targetNode.localToScreen((targetNode.getWidth() - indicator.getIndicatorPane().getWidth()) / 2, (targetNode.getHeight() - indicator.getIndicatorPane().getHeight()) / 2);
        }
        return newPos;

    }

    /*        public void windowBeforeShow(Region node) {}

        public void windowAfterShow(Region node) {}

        public void windowOnShowing(WindowEvent ev, Region node) {}

        public void windowOnShown(WindowEvent ev, Region node) {}

        public void windowOnHiding(WindowEvent ev, Region node) {}

        public void windowOnHidden(WindowEvent ev, Region node) {}
     */
    public void windowBeforeShow(Window win) {
    }

    public void windowAfterShow(Window win) {
    }

    public void windowOnShowing(Window win) {
    }

    public void windowOnShown(Window win) {
    }

    public void windowOnHiding(Window win) {
    }

    public void windowOnHidden(Window win) {
    }

    protected void resizeButtonPanes() {
    }

    protected Boolean intersects(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
        Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
        return b1.intersects(b2);

    }

    protected Point2D getMousePos() {
        return mousePos;
    }

    public void showDockPlace() {
    }

    protected void showDockPlace2(Region forNode, Side side) {
    }

    public void showDockPlace(Side side) {
        Region pane = getTargetPaneHandler().getDockPane();
        Button selected = getIndicator().getSelectedButton();
        if (selected != null && selected.getUserData() != null) {
            pane = ((PaneHandler) selected.getUserData()).getDockPane();
        }
        Rectangle dockPlace = getIndicator().getDockPlace();
        showDockPlace2(pane, side);

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
        }
        dockPlace.setVisible(true);
        dockPlace.toFront();
    }

    public void sideButtonSelected() {
    }

    public void setMousePos(Point2D mousePos) {
        this.mousePos = mousePos;
    }

    protected void setTargetNode(Region targetNode) {
        this.targetNode = targetNode;
    }

    public Pane getTopButtons() {
        return topButtons;
    }

    public void setTopButtons(Pane topButtons) {
        this.topButtons = topButtons;
    }

    public Pane getBottomButtons() {
        return bottomButtons;
    }

    public void setBottomButtons(Pane bottomButtons) {
        this.bottomButtons = bottomButtons;
    }

    public Pane getLeftButtons() {
        return leftButtons;
    }

    public void setLeftButtons(Pane leftButtons) {
        this.leftButtons = leftButtons;
    }

    public Pane getRightButtons() {
        return rightButtons;
    }

    public Pane getCenterButtons() {
        return centerButtons;
    }

    public void setRightButtons(Pane rightButtons) {
        this.rightButtons = rightButtons;
    }

}//Transformer
