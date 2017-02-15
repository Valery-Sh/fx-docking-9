package org.vns.javafx.dock.api;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public abstract class SideIndicator extends DockIndicator {

    private Rectangle dockPlace2;

    private Pane topButtons;
    private Pane bottomButtons;
    private Pane leftButtons;
    private Pane rightButtons;
    private Pane centerButtons;

    private Button selectedButton;

    //private final Map<Node, DockTargetController> sideButtonMap = new HashMap<>();
    //private SideIndicatorTransformer transformer;

    protected SideIndicator(DockTargetController paneController) {
        super(paneController);
        init();
    }

    private void init() {
        getDockPlace().getStyleClass().add("dock-place");

        //dockPlace2 = new Rectangle();
        //dockPlace2.getStyleClass().add("dock-place2");
        //getIndicatorPane().getChildren().add(0, dockPlace2);
        //paneController.getDragPopup().getNodeIndicatorPopup().setOnShown(this);
    }

    @Override
    public void showDockPlace(double x, double y) {
        getDockPlace().setVisible(true);
    }

    protected Window getIndicatorPopup() {
        return null;
    }

    @Override
    public void showIndicator(double screenX, double screenY, Region targetNode) {
        //transform(targetNode, screenX, screenY);
    }

    @Override
    public void showIndicator(double screenX, double screenY) {
        //transform(screenX, screenY);
    }

    /*    public void showWindow(Window window) {
        transform();

        window.setOnShown(e -> {
            getTransformer().windowOnShown(window);
        });
        window.setOnShowing(e -> {
            getTransformer().windowOnShowing(window);
        });
        window.setOnHidden(e -> {
            getTransformer().windowOnHidden(window);
        });
        window.setOnHidden(e -> {
            getTransformer().windowOnHiding(window);
        });

        getTransformer().windowBeforeShow(window);
        if (window instanceof Stage) {
            ((Stage) window).show();
        }
        getTransformer().windowAfterShow(window);

    }
     */

 /*    public Rectangle getDockPlace2() {
        return dockPlace2;
    }
     */
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

    @Override
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
        //getDockPlace2().setVisible(false);
    }

    public void showDockPlace(Button selButton, Side side) {
        setSelectedButton(selButton);
        //transform();
        showDockPlace(side);
    }

    public void showDockPlace(Button selButton, Region targetNode, Side side) {
        setSelectedButton(selButton);
        //transform(targetNode);
        showDockPlace(side);
    }

    public void showDockPlace(Region targetNode, double screenX, double screenY) {
        //transform(targetNode, screenX, screenY);
        //transformer.showDockPlace();
    }

    public void setSelectedButton(Button selectedButton) {
        this.selectedButton = selectedButton;
    }

    public void showDockPlace(Side side) {
        Region pane = getPaneController().getDockPane();
        Button selected = getSelectedButton();
        if (selected != null && selected.getUserData() != null) {
            pane = ((DockTargetController) selected.getUserData()).getDockPane();
        }
        Rectangle dockPlace = (Rectangle) getDockPlace();
        //showDockPlace2(pane, side);

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

    //protected abstract SideIndicatorTransformer getTransformer();

    /*    protected void transform() {
        transform(null, 0, 0);
    }

    protected void transform(Region targetNode) {
        transform(targetNode, 0, 0);
    }

    protected void transform(double screenX, double screenY) {
        transform(null, screenX, screenY);
    }

    protected void transform(Region targetNode, double screenX, double screenY) {
        transformer = getTransformer();
        transformer.initialize(this, new Point2D(screenX, screenY), topButtons, bottomButtons, leftButtons, rightButtons);

        transformer.setTargetNode(targetNode);
        transformer.transform();
    }
     */
    protected static class NodeSideIndicator extends SideIndicator {

        private DockNodeController nodeController;
        private final Scale smallbuttonsScale;

        public NodeSideIndicator(DockTargetController paneController) {
            super(paneController);
            this.smallbuttonsScale = new Scale(0.5, 0.5);
        }

        /*        public NodeSideIndicator(DockNodeController nodeController) {
            super(nodeController.getPaneController());
            this.nodeController = nodeController;
            this.smallbuttonsScale = new Scale(0.5, 0.5);
        }
         */
        public Scale getSmallbuttonsScale() {
            return smallbuttonsScale;
        }

        protected Region getTargetNode() {
            return nodeController.dockable().node();
        }

        @Override
        protected Window getIndicatorPopup() {
            return ((DragPopup) getPaneController().getDragPopup()).getNodeIndicatorPopup();
        }

        @Override
        public void showIndicator(double screenX, double screenY, Region dockNode) {

            super.showIndicator(screenX, screenY, dockNode);
            if (dockNode != null) {
                nodeController = DockRegistry.dockable(dockNode).nodeController();
            }
            Point2D newPos;

            //getIndicatorPopup().setOnShowing(e -> {
            //notifyPopupShowing();
            //});
            getIndicatorPopup().setOnShown(e -> notifyPopupShown());
            //getIndicatorPopup().setOnHiding(e -> notifyPopupHiding());
            getIndicatorPopup().setOnHidden(e -> notifyPopupHidden());

            if (dockNode != null) {
                newPos = getIndicatorPosition();
                //notifyBeforeShow();
                ((Popup) getIndicatorPopup()).show(getPaneController().getDragPopup(), newPos.getX(), newPos.getY());
                //notifyAfterShow();
            } else {
                newPos = getIndicatorPosition();
                if (newPos != null) {
                    //notifyBeforeShow();
                    ((Popup) getIndicatorPopup()).show(getPaneController().getDragPopup(), newPos.getX(), newPos.getY());
                    //notifyAfterShow();
                } else {
                    getIndicatorPopup().hide();
                }
            }
        }

        public void notifyPopupShown() {
            if (getPaneController() != null && getTargetNode() != null) {
                resizeButtonPanes();
            }
        }

        public void notifyPopupHidden() {
            if (getPaneController() == null || getTargetNode() == null) {
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

            DockTargetController paneHandler = getPaneController();
            SideIndicator.PaneSideIndicator paneIndicator = (SideIndicator.PaneSideIndicator) paneHandler.getDockIndicator();

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

        protected void resizeButtonPanes() {
            if (getPaneController() != null && getTargetNode() != null && intersects()) {
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

            if (getPaneController() != null) {

                Node node = ((DragPopup) getPaneController().getDragPopup()).getPaneIndicator().getTopButtons();

                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getPaneController().getDragPopup()).getPaneIndicator().getRightButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getPaneController().getDragPopup()).getPaneIndicator().getBottomButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
                node = ((DragPopup) getPaneController().getDragPopup()).getPaneIndicator().getLeftButtons();
                if (intersects(thisPane, node)) {
                    return true;
                }
            }
            return false;
        }

        public Point2D getIndicatorPosition() {
            Point2D newPos = null;
            if (getTargetNode() != null && getIndicatorPane() != null) {
                newPos = getTargetNode().localToScreen((getTargetNode().getWidth() - getIndicatorPane().getWidth()) / 2, (getTargetNode().getHeight() - getIndicatorPane().getHeight()) / 2);
            }
            return newPos;
        }

        /*        @Override
        protected NodeIndicatorTransformer getTransformer() {
            return ((DockPaneController)getPaneController()).getNodeTransformer();
        }
         */
    }//class NodeSideIndicator

    protected static class PaneSideIndicator extends SideIndicator {

        public PaneSideIndicator(DockTargetController paneController) {
            super(paneController);
        }

        @Override
        protected Popup getIndicatorPopup() {
            return getPaneController().getDragPopup();
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            super.showIndicator(screenX, screenY);

            /*            getTransformer().notifyBeforeShow();
            getIndicatorPopup().setOnShown(e -> {
                notifyPopupShown();
            });
            getIndicatorPopup().setOnShowing(e -> {
                getTransformer().notifyPopupShowing();
            });
            getIndicatorPopup().setOnHiding(e -> {
                getTransformer().notifyPopupHiding();
            });
            getIndicatorPopup().setOnHidden(e -> {
                //notifyPopupHidden();
            });
             */
            getIndicatorPopup().show(getPaneController().getDockPane(), screenX, screenY);
            //getTransformer().notifyAfterShow();
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

        /*        @Override
        protected PaneIndicatorTransformer getTransformer() {
            return ((DockPaneController) getPaneController()).getPaneTransformer();
        }
         */
    }//class SideIndicator

}//SideIndicator
