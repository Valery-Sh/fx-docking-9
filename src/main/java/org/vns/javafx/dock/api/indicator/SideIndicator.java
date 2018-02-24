package org.vns.javafx.dock.api.indicator;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery
 */
public abstract class SideIndicator extends PositionIndicator {

    private Pane topButtons;
    private Pane bottomButtons;
    private Pane leftButtons;
    private Pane rightButtons;
    private Pane centerButtons;

    private Button selectedButton;

    protected SideIndicator(TargetContext targetContext) {
        super(targetContext);
        init();
    }

    private void init() {
        getDockPlace().getStyleClass().add("dock-place");
    }

    @Override
    public void showDockPlace(double x, double y) {
        getDockPlace().setVisible(true);
    }

    
/*    public Window getIndicatorPopup() {
        return null;
    }
*/
    @Override
    public void showIndicator(double screenX, double screenY, Node targetNode) {
    }

    @Override
    public void showIndicator(double screenX, double screenY) {
    }

    public Button getSelectedButton() {
        return selectedButton;
    }

    public Pane getTopButtons() {
        return topButtons;
    }

    public Pane getBottomButtons() {
        return bottomButtons;
    }

    public Pane getLeftButtons() {
        return leftButtons;

    }

    public Pane getRightButtons() {
        return rightButtons;
    }

    public Pane getCenterButtons() {
        return centerButtons;
    }

    //@Override
    protected abstract String getStylePrefix();

    protected void restoreButtonsStyle(Pane pane, String style) {
        if (pane == null) {
            return;
        }
        pane.getChildren().forEach(node -> {
            node.getStyleClass().clear();
            node.getStyleClass().add("button");
            node.getStyleClass().add(style);
        });
    }

    protected String getCenterButonPaneStyle() {
        return getStylePrefix() + "-" + "center-button-pane";
    }

    protected String getButonPaneStyle(Side side) {
        String style = null;
        String prefix = getStylePrefix();
        switch (side) {
            case TOP:
                style = prefix + "-" + "top-button-pane";
                break;
            case RIGHT:
                style = prefix + "-" + "right-button-pane";
                break;
            case BOTTOM:
                style = prefix + "-" + "bottom-button-pane";
                break;
            case LEFT:
                style = prefix + "-" + "left-button-pane";
                break;
        }
        return style;
    }

    protected String getCenterButtonStyle() {
        return getStylePrefix() + "-" + "center-button";
    }

    protected String getButtonStyle(Side side) {
        String style = null;
        String prefix = getStylePrefix();
        switch (side) {
            case TOP:
                style = prefix + "-" + "top-button";
                break;
            case RIGHT:
                style = prefix + "-" + "right-button";
                break;
            case BOTTOM:
                style = prefix + "-" + "bottom-button";
                break;
            case LEFT:
                style = prefix + "-" + "left-button";
                break;
        }
        return style;
    }

    protected Pane createCenterButtons() {
        Button btn = new Button();
        centerButtons = new StackPane(btn);
        centerButtons.getStyleClass().add(getCenterButonPaneStyle());
        btn.getStyleClass().add(getCenterButtonStyle());
        return centerButtons;
    }

    protected Pane createSideButtons(Side side) {
        Button btn = new Button();
        StackPane retval = new StackPane();
        retval.getChildren().add(btn);
        retval.setAlignment(Pos.CENTER);
        retval.getStyleClass().add(getButonPaneStyle(side));
        btn.getStyleClass().add(getButtonStyle(side));
        switch (side) {
            case TOP:
                topButtons = retval;
                break;
            case RIGHT:
                rightButtons = retval;
                break;
            case BOTTOM:
                bottomButtons = retval;
                break;
            case LEFT:
                leftButtons = retval;
                break;
        }
        return retval;
    }

    @Override
    public void hideDockPlace() {
        getDockPlace().setVisible(false);
    }

    public void showDockPlace(Button selButton, Side side) {
        setSelectedButton(selButton);
        showDockPlace(side);
    }

    public void showDockPlace(Button selButton, Node targetNode, Side side) {
        setSelectedButton(selButton);
        showDockPlace(side);
    }

    public void showDockPlace(Node targetNode, double screenX, double screenY) {
    }

    public void setSelectedButton(Button selectedButton) {
        this.selectedButton = selectedButton;
    }

    public void showDockPlace(Side side) {
        Node pane = getTargetContext().getTargetNode();
        Button selected = getSelectedButton();
        if (selected != null && selected.getUserData() != null) {
            pane = ((TargetContext) selected.getUserData()).getTargetNode();
        }
        Rectangle dockPlace = (Rectangle) getDockPlace();

        switch (side) {
            case TOP:
                dockPlace.setWidth(DockUtil.widthOf(pane));
                dockPlace.setHeight(DockUtil.heightOf(pane) / 2);
                Point2D p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case BOTTOM:
                dockPlace.setWidth(DockUtil.widthOf(pane));
                dockPlace.setHeight(DockUtil.heightOf(pane) / 2);
                p = dockPlace.localToParent(0, DockUtil.heightOf(pane) - dockPlace.getHeight());
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case LEFT:
                dockPlace.setWidth(DockUtil.widthOf(pane) / 2);
                dockPlace.setHeight(DockUtil.heightOf(pane));
                p = dockPlace.localToParent(0, 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
            case RIGHT:
                dockPlace.setWidth(DockUtil.widthOf(pane) / 2);
                dockPlace.setHeight(DockUtil.heightOf(pane));
                p = dockPlace.localToParent(DockUtil.widthOf(pane) - dockPlace.getWidth(), 0);
                dockPlace.setX(p.getX());
                dockPlace.setY(p.getY());
                break;
        }
        dockPlace.setVisible(true);
        dockPlace.toFront();
    }

    public static class NodeSideIndicator extends SideIndicator {

        private DockableContext nodeContext;
        private final Scale smallbuttonsScale;

        public NodeSideIndicator(TargetContext paneContext) {
            super(paneContext);
            this.smallbuttonsScale = new Scale(0.5, 0.5);
        }

        public Scale getSmallbuttonsScale() {
            return smallbuttonsScale;
        }

        protected Node getTargetNode() {
            if (nodeContext == null) {
                return null;
            }
            return nodeContext.dockable().node();
        }

        @Override
        public IndicatorPopup getIndicatorPopup() {
            DragPopup ip = (DragPopup)getTargetContext().getLookup().lookup(IndicatorManager.class);
            return (IndicatorPopup) ip.getNodeIndicatorPopup();                                
        }

        @Override
        public void showIndicator(double screenX, double screenY, Node dockNode) {
            super.showIndicator(screenX, screenY, dockNode);
            if (dockNode != null) {
                nodeContext = Dockable.of(dockNode).getContext();
            } else {
                nodeContext = null; // 06.05.2017
            }
            Point2D newPos;

            getIndicatorPopup().setOnShown(e -> notifyPopupShown());
            getIndicatorPopup().setOnHidden(e -> notifyPopupHidden());

            if (dockNode != null) {
                newPos = getIndicatorPosition();
                //Popup popup = (Popup)getTargetContext().getLookup().lookup(IndicatorManager.class);
                //((Popup) getIndicatorPopup()).show( popup, newPos.getX(), newPos.getY());                
//                ((Popup) getIndicatorPopup()).show(getTargetContext().getIndicatorPopup(), newPos.getX(), newPos.getY());
                ((Popup) getIndicatorPopup()).show( (Popup)getTargetContext().getLookup().lookup(IndicatorManager.class), newPos.getX(), newPos.getY());                                
            } else {
                newPos = getIndicatorPosition();
                if (newPos != null) {
                    //((Popup) getIndicatorPopup()).show(getTargetContext().getIndicatorPopup(), newPos.getX(), newPos.getY());
                    ((Popup) getIndicatorPopup()).show((Popup)getTargetContext().getLookup().lookup(IndicatorManager.class), newPos.getX(), newPos.getY());
                } else {
                    getIndicatorPopup().hide();
                }
            }
        }

        public void notifyPopupShown() {
            if (getTargetContext() != null && getTargetNode() != null) {
                resizeButtonPanes();
            }
        }

        public void notifyPopupHidden() {
            if (getTargetContext() == null || getTargetNode() == null) {
                resizeButtonPanes();
            }
        }

        @Override
        protected Pane createIndicatorPane() {
            GridPane indicatorPane = new GridPane();
            indicatorPane.getStyleClass().add(getStylePrefix());
            indicatorPane.setMouseTransparent(true);

            Pane buttons = createSideButtons(Side.TOP);
            indicatorPane.add(buttons, 1, 0);
            buttons = createSideButtons(Side.BOTTOM);
            indicatorPane.add(buttons, 1, 2);
            buttons = createSideButtons(Side.LEFT);
            indicatorPane.add(buttons, 0, 1);
            buttons = createSideButtons(Side.RIGHT);
            indicatorPane.add(buttons, 2, 1);

            buttons = createCenterButtons();
            indicatorPane.add(buttons, 1, 1);

            return indicatorPane;
        }

        @Override
        protected String getStylePrefix() {
            return "drag-node-indicator";
        }

        @Override
        public void showDockPlace(Side side) {

            TargetContext paneHandler = getTargetContext();
            SideIndicator.PaneSideIndicator paneIndicator = (SideIndicator.PaneSideIndicator) paneHandler.getPositionIndicator();

            Point2D p = getTargetNode().localToScreen(0, 0).subtract(paneHandler.getTargetNode().localToScreen(0, 0));
            Rectangle dockPlace = (Rectangle) paneIndicator.getDockPlace();

            dockPlace.setX(p.getX());
            dockPlace.setY(p.getY());
            double width = DockUtil.widthOf(getTargetNode());
            double height = DockUtil.heightOf(getTargetNode());
            switch (side) {
                case TOP:
                    dockPlace.setWidth(width);
                    dockPlace.setHeight(height / 2);
                    break;
                case BOTTOM:
                    dockPlace.setWidth(width);
                    dockPlace.setHeight(height / 2);
                    dockPlace.setY(p.getY() + dockPlace.getHeight());
                    break;
                case LEFT:
                    dockPlace.setWidth(width / 2);
                    dockPlace.setHeight(height);
                    break;
                case RIGHT:
                    dockPlace.setWidth(width / 2);
                    dockPlace.setHeight(height);
                    dockPlace.setX(p.getX() + dockPlace.getWidth());
                    break;
            }
            dockPlace.setVisible(true);
            dockPlace.toFront();
        }

        protected void resizeButtonPanes() {
            if (getTargetContext() != null && getTargetNode() != null && intersects()) {
                if (!getIndicatorPane().getTransforms().contains(getSmallbuttonsScale())) {
                    getIndicatorPane().getTransforms().add(getSmallbuttonsScale());

                    double w = getIndicatorPane().getWidth() / 2;
                    double h = getIndicatorPane().getHeight() / 2;
                    Point2D p = getIndicatorPane().localToParent(w, h);
                    getSmallbuttonsScale().setPivotX(w);
                    getSmallbuttonsScale().setPivotY(h);
                }
            } else {
                getIndicatorPane().getTransforms().remove(getSmallbuttonsScale());
            }
        }

        protected boolean intersects() {
            Pane thisPane = getIndicatorPane();

            if (getTargetContext() != null) {

                Node node = ((DragPopup) getTargetContext().getLookup().lookup(IndicatorManager.class)).getPaneIndicator().getTopButtons();

                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getTargetContext().getLookup().lookup(IndicatorManager.class)).getPaneIndicator().getRightButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getTargetContext().getLookup().lookup(IndicatorManager.class)).getPaneIndicator().getBottomButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getTargetContext().getLookup().lookup(IndicatorManager.class)).getPaneIndicator().getLeftButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
            }
            return false;
        }

        public Point2D getIndicatorPosition() {
            Point2D newPos = null;
            if (getTargetNode() != null && getIndicatorPane() != null) {
                double width = DockUtil.widthOf(getTargetNode());
                double height = DockUtil.heightOf(getTargetNode());

                newPos = getTargetNode().localToScreen((width - getIndicatorPane().getWidth()) / 2, (height - getIndicatorPane().getHeight()) / 2);
            }
            return newPos;
        }
    }//class NodeSideIndicator

    public static class PaneSideIndicator extends SideIndicator {

        public PaneSideIndicator(TargetContext context) {
            super(context);
        }

        @Override
        public IndicatorPopup getIndicatorPopup() {
            //return getTargetContext().getIndicatorPopup();
            return (IndicatorPopup)getTargetContext().getLookup().lookup(IndicatorManager.class);
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            super.showIndicator(screenX, screenY);
            getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
        }

        @Override
        protected Pane createIndicatorPane() {
            BorderPane indicatorPane = new BorderPane();
            indicatorPane.getStyleClass().add(getStylePrefix());
            indicatorPane.setMouseTransparent(true);
            Pane buttons = createSideButtons(Side.TOP);
            indicatorPane.setTop(buttons);
            buttons = createSideButtons(Side.RIGHT);
            indicatorPane.setRight(buttons);
            buttons = createSideButtons(Side.BOTTOM);
            indicatorPane.setBottom(buttons);
            buttons = createSideButtons(Side.LEFT);
            indicatorPane.setLeft(buttons);

            buttons = createCenterButtons();
            indicatorPane.setCenter(buttons);

            return indicatorPane;
        }

        @Override
        protected String getStylePrefix() {
            return "drag-pane-indicator";
        }
    }//class PaneSideIndicator

}//SideIndicator
