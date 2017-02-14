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
import org.vns.javafx.dock.api.SideIndicator.PaneSideIndicator;

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

    public DockTargetController getTargetPaneController() {
        return getIndicator().getPaneController();
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
        Region pane = getTargetPaneController().getDockPane();
        Button selected = getIndicator().getSelectedButton();
        if (selected != null && selected.getUserData() != null) {
            pane = ((DockTargetController) selected.getUserData()).getDockPane();
        }
        Rectangle dockPlace = (Rectangle) getIndicator().getDockPlace();
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

/*    public void setMousePos(Point2D mousePos) {
        this.mousePos = mousePos;
    }
*/
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
    public static class NodeIndicatorTransformer extends SideIndicatorTransformer {

        public NodeIndicatorTransformer() {
        }

        @Override
        public void showDockPlace(Side side) {
            if (getTargetNode() == null) {
                return;
            }
            DockTargetController paneHandler = getIndicator().getPaneController();
            PaneSideIndicator paneIndicator = (PaneSideIndicator) paneHandler.getDockIndicator();

            Point2D p = getTargetNode().localToScreen(0, 0).subtract(paneHandler.getDockPane().localToScreen(0, 0));
            Rectangle dockPlace = (Rectangle) paneIndicator.getDockPlace();
            //showDockPlace2(pane, side);

            dockPlace.setX(p.getX());
            dockPlace.setY(p.getY());

            switch (side) {
                case TOP:
                    dockPlace.setWidth(getTargetNode().getWidth());
                    dockPlace.setHeight(getTargetNode().getHeight() / 2);
                    break;
                case BOTTOM:
                    dockPlace.setWidth(getTargetNode().getWidth());
                    dockPlace.setHeight(getTargetNode().getHeight() / 2);
                    dockPlace.setY(p.getY() + dockPlace.getHeight());
                    break;
                case LEFT:
                    dockPlace.setWidth(getTargetNode().getWidth() / 2);
                    dockPlace.setHeight(getTargetNode().getHeight());
                    break;
                case RIGHT:
                    dockPlace.setWidth(getTargetNode().getWidth() / 2);
                    dockPlace.setHeight(getTargetNode().getHeight());
                    dockPlace.setX(p.getX() + dockPlace.getWidth());
                    break;
            }
            dockPlace.setVisible(true);
            dockPlace.toFront();
        }


        @Override
        public void notifyPopupShown() {
            if (getTargetPaneController() != null && getTargetNode() != null) {
                resizeButtonPanes();
            }
        }

        @Override
        public void notifyPopupHidden() {
            if (getTargetPaneController() == null || getTargetNode() == null) {
                resizeButtonPanes();
            }

        }

        @Override
        protected void resizeButtonPanes() {
            if (getTargetPaneController() != null && getTargetNode() != null && intersects()) {
                if (!getIndicator().getIndicatorPane().getTransforms().contains(getSmallbuttonsScale())) {
                    getIndicator().getIndicatorPane().getTransforms().add(getSmallbuttonsScale());

                    double w = getIndicator().getIndicatorPane().getWidth() / 2;
                    double h = getIndicator().getIndicatorPane().getHeight() / 2;
                    Point2D p = getIndicator().getIndicatorPane().localToParent(w, h);
                    getSmallbuttonsScale().setPivotX(w);
                    getSmallbuttonsScale().setPivotY(h);
                }
            } else {
                getIndicator().getIndicatorPane().getTransforms().remove(getSmallbuttonsScale());
            }

        }

        protected boolean intersects() {
            boolean retval = false;
            Pane thisPane = getIndicator().getIndicatorPane();

            if (getTargetPaneController() != null) {

                Node node = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator().getTopButtons();

                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator().getRightButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator().getBottomButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup)getTargetPaneController().getDragPopup()).getPaneIndicator().getLeftButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Point2D getIndicatorPosition() {
            Point2D newPos = null;
            if (getTargetNode() != null && getIndicator().getIndicatorPane() != null) {
                newPos = getTargetNode().localToScreen((getTargetNode().getWidth() - getIndicator().getIndicatorPane().getWidth()) / 2, (getTargetNode().getHeight() - getIndicator().getIndicatorPane().getHeight()) / 2);
            }
            return newPos;
        }

    }//NodeTransformer
    public static class PaneIndicatorTransformer extends SideIndicatorTransformer {

        public PaneIndicatorTransformer() {
            super();
        }


        @Override
        protected void showDockPlace2(Region pane, Side side) {
            Rectangle dockPlace = getIndicator().getDockPlace2();
            dockPlace.setWidth(pane.getWidth() - 1);
            dockPlace.setHeight(pane.getHeight() - 1);
            Point2D p = dockPlace.localToParent(0, 0);
            dockPlace.setX(p.getX() + 1);
            dockPlace.setY(p.getY() + 1);
            dockPlace.setVisible(true);
        }

    }//Pane Transformer

}//Transformer
