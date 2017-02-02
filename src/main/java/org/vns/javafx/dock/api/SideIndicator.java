package org.vns.javafx.dock.api;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.SideIndicatorTransformer.NodeIndicatorTransformer;
import org.vns.javafx.dock.api.SideIndicatorTransformer.PaneIndicatorTransformer;

/**
 *
 * @author Valery
 */
public abstract class SideIndicator {

    private final PaneHandler paneHandler;

    private Rectangle dockPlace;
    private Rectangle dockPlace2;

    private Pane topButtons;
    private Pane bottomButtons;
    private Pane leftButtons;
    private Pane rightButtons;
    private Pane centerButtons;

    private Button selectedButton;

    private Pane indicatorPane;

    //private final Map<Node, PaneHandler> sideButtonMap = new HashMap<>();
    private SideIndicatorTransformer transformer;

    protected SideIndicator(PaneHandler paneHandler) {
        this.paneHandler = paneHandler;
        init();
    }

    private void init() {
        indicatorPane = createIndicatorPane();
        dockPlace = new Rectangle();
        dockPlace.getStyleClass().add("dock-place");
        indicatorPane.getChildren().add(dockPlace);
        dockPlace2 = new Rectangle();
        dockPlace2.getStyleClass().add("dock-place2");
        indicatorPane.getChildren().add(0, dockPlace2);

        //paneHandler.getDragPopup().getNodeIndicatorPopup().setOnShown(this);
    }

    protected Window getIndicatorPopup() {
        return null;
    }

    public void showIndicator(double screenX, double screenY, Region targetNode) {
        transform(targetNode, screenX, screenY);
    }

    public void showIndicator(double screenX, double screenY) {
        transform(screenX, screenY);
    }

    public void showWindow(Window window) {
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

    public Rectangle getDockPlace() {
        return dockPlace;
    }

    public Rectangle getDockPlace2() {
        return dockPlace2;
    }

    public PaneHandler getPaneHandler() {
        return paneHandler;
    }

    protected abstract Pane createIndicatorPane();

    public Pane getIndicatorPane() {
        return indicatorPane;
    }

    public Button getSelectedButton() {
        return selectedButton;
    }

    public Pane getTopButtons() {
        Pane retval;
        if (transformer == null) {
            retval = topButtons;
        } else {
            retval = transformer.getTopButtons();
        }
        return retval;
    }

    public Pane getBottomButtons() {
        Pane retval;
        if (transformer == null) {
            retval = bottomButtons;
        } else {
            retval = transformer.getBottomButtons();
        }
        return retval;

    }

    public Pane getLeftButtons() {
        Pane retval;
        if (transformer == null) {
            retval = leftButtons;
        } else {
            retval = transformer.getLeftButtons();
        }
        return retval;

    }

    public Pane getRightButtons() {
        Pane retval;
        if (transformer == null) {
            retval = rightButtons;
        } else {
            retval = transformer.getRightButtons();
        }
        return retval;
    }

    public Pane getCenterButtons() {
        Pane retval;
        if (transformer == null) {
            retval = centerButtons;
        } else {
            retval = transformer.getCenterButtons();
        }
        return retval;
    }

    protected abstract String getStylePrefix();

    /*        protected void restoreButtonsStyle() {
            restoreButtonsStyle(topButtons, getStylePrefix() + "-top-button");
            restoreButtonsStyle(rightButtons, getStylePrefix() + "-right-button");
            restoreButtonsStyle(bottomButtons, getStylePrefix() + "-bottom-button");
            restoreButtonsStyle(leftButtons, getStylePrefix() + "-left-button");
            restoreButtonsStyle(centerButtons, getStylePrefix() + "-center-button");
        }
     */
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
                style = prefix + "-" + "laft-button-pane";
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

    public void hideDockPlace() {
        getDockPlace().setVisible(false);
        getDockPlace2().setVisible(false);
    }

    public void showDockPlace(Button selButton, Side side) {
        setSelectedButton(selButton);
        transform();
        transformer.showDockPlace(side);
    }

    public void showDockPlace(Button selButton, Region targetNode, Side side) {
        setSelectedButton(selButton);
        transform(targetNode);
        transformer.showDockPlace(side);
    }

    public void showDockPlace(Region targetNode, double screenX, double screenY) {
        transform(targetNode, screenX, screenY);
        transformer.showDockPlace();
    }

    /*        public Point2D mousePos(Region targetNode, double screenX, double screenY) {
            transform(targetNode, screenX, screenY);
            return transformer.mousePos();
        }
     */
    public void setSelectedButton(Button selectedButton) {
        this.selectedButton = selectedButton;
    }

    protected abstract SideIndicatorTransformer getTransformer();

    protected void transform() {
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

    protected static class NodeSideIndicator extends SideIndicator {

        public NodeSideIndicator(PaneHandler paneHandler) {
            super(paneHandler);
        }

        @Override
        protected Window getIndicatorPopup() {
            return getPaneHandler().getDragPopup().getNodeIndicatorPopup();
        }

        @Override
        public void showIndicator(double screenX, double screenY, Region dockNode) {

            super.showIndicator(screenX, screenY, dockNode);

            Point2D newPos;

            getIndicatorPopup().setOnShowing(e -> {
                getTransformer().notifyPopupShowing();
            });
            getIndicatorPopup().setOnShown(e -> getTransformer().notifyPopupShown());
            getIndicatorPopup().setOnHiding(e -> getTransformer().notifyPopupHiding());
            getIndicatorPopup().setOnHidden(e -> getTransformer().notifyPopupHidden());

            if (dockNode != null) {
                newPos = getTransformer().getIndicatorPosition();
                getTransformer().notifyBeforeShow();
                ((Popup) getIndicatorPopup()).show(getPaneHandler().getDragPopup(), newPos.getX(), newPos.getY());
                getTransformer().notifyAfterShow();
            } else {
                newPos = getTransformer().getIndicatorPosition();
                if (newPos != null) {
                    getTransformer().notifyBeforeShow();
                    ((Popup) getIndicatorPopup()).show(getPaneHandler().getDragPopup(), newPos.getX(), newPos.getY());
                    getTransformer().notifyAfterShow();
                } else {
                    getIndicatorPopup().hide();
                }
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
        protected NodeIndicatorTransformer getTransformer() {
            return getPaneHandler().getNodeTransformer();
        }
    }//class NodeSideIndicator

    protected static class PaneSideIndicator extends SideIndicator {

        public PaneSideIndicator(PaneHandler paneHandler) {
            super(paneHandler);
        }

        @Override
        protected Popup getIndicatorPopup() {
            return getPaneHandler().getDragPopup();
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            super.showIndicator(screenX, screenY);

            getTransformer().notifyBeforeShow();
            getIndicatorPopup().setOnShown(e -> {
                getTransformer().notifyPopupShown();
            });
            getIndicatorPopup().setOnShowing(e -> {
                getTransformer().notifyPopupShowing();
            });
            getIndicatorPopup().setOnHiding(e -> {
                getTransformer().notifyPopupHiding();
            });
            getIndicatorPopup().setOnHidden(e -> {
                getTransformer().notifyPopupHidden();
            });

            getIndicatorPopup().show(getPaneHandler().getDockPane(), screenX, screenY);
            getTransformer().notifyAfterShow();
        }

        /*        @Override
        public void showIndicator(double screenX, double screenY, Region dockNode) {
            super.showIndicator(screenX, screenY, dockNode);
            getIndicatorPopup().setOnShown(e -> {
                getTransformer().notifyPopupShown();
            });
            getIndicatorPopup().setOnShowing(e -> {
                getTransformer().notifyPopupShowing();
            });
            getIndicatorPopup().showPopup(getPaneHandler().getDockPane(), screenX, screenY);
            getTransformer().notifyAfterShow();
        }
         */
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

        @Override
        protected PaneIndicatorTransformer getTransformer() {
            return getPaneHandler().getPaneTransformer();
        }
    }//class SideIndicator

}//SideIndicator
