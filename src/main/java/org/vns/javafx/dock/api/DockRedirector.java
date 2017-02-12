package org.vns.javafx.dock.api;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.SideIndicatorTransformer.PaneIndicatorTransformer;

/**
 * This class is designed to solve the problem that arises during docking 
 * operation when one dock pane overlaps  the other one.
 * Indeed, when in the process of dragging of a {@code dockable} object 
 * the mouse cursor is over the target panels that overlap each other then the 
 * indicators of dock positions correspond only to the target which is in front.
 * An example is when we docked an object of type {@link org.vns.javafx.dock.DockTabPane ) to the
 * target pane of type {@link org.vns.javafx.dock.DockPane ). We are able to do
 * this way because of the fact that the {@code DockTabPane} implements both 
 * {@code Dockable} and {@DockPaneTarget} interfaces. 
 * 
 * This class solves the problem allowing to show the hierarchy of targets and 
 * select the needed one.
 * 
 * @author Valery Shyshkin
 */
public class DockRedirector {

    private RedirectorDockPane rootPane;
    private Region targetDockPane;
    private final Region topDockPane;
    private Stage stage;
    /**
     * Creates a new instance of the class for the specified dock target.
     * The constructor is called by the current {@link DragManager} object and the 
     * mouse is positioned on the {@code topDockPane).
     * 
     * @param topDockPane the dock target . 
     */
    public DockRedirector(Region topDockPane) {
        this.topDockPane = topDockPane;
        init();
    }

    private void init() {
        targetDockPane = findTargetDockPane();
        rootPane = new RedirectorDockPane(DockRegistry.dockPaneTarget(topDockPane).paneController(), DockRegistry.dockPaneTarget(targetDockPane).paneController());
    }
    /**
     * Returns the last parent of the {@code topDockPane}.
     * We are going to show hierarchy of all dock targets starting from
     * the top target and ending with the last parent.
     * 
     * @return the last parent of the {@code topDockPane}.
     */
    protected Region findTargetDockPane() {
        List<Node> nodes = TopNodeHelper.getParentChain(topDockPane, node -> {
            return DockRegistry.isDockPaneTarget(node);
        });
        return (Region) nodes.get(nodes.size() - 1);
    }
    /**
     * Returns the pane controller of the last parent target node.
     * @return the pane controller of the last parent target node.
     */
    protected DockTargetController getPaneController() {
        return DockRegistry.dockPaneTarget(targetDockPane).paneController();
    }
    /**
     * Returns the stage which is used to display indicator pane.
     * The bounds of the stage are equal to the bounds of the last parent dock target.
     * @return the stage which is used to display indicator pane.
     */
    protected Stage getStage() {
        return stage;
    }
    /**
     * Creates and shows a stage which is used to display an indicator pane.
     * 
     * @param topDockPane the dock target which is in front
     * @return the object of type {@link DockRedirector}
     */
    public static DockRedirector show(Region topDockPane) {
        DockRedirector redir = new DockRedirector(topDockPane);
        redir.show();
        return redir;
    }
    
    /**
     * Calculates the position and bounds of the stage and then shows it. 
     */
    protected void show() {
        Point2D p = topDockPane.localToScreen(0, 0);
        double w = topDockPane.getWidth();
        double h = topDockPane.getHeight();
        getRootPane().setPrefSize(w, h);
        show(p.getX(), p.getY());
    }
    /**
     * Shows the indicator stage at the specified position on the screen.
     * @param x the x coordinate of the upper left conner
     * @param y the y coordinate of the upper left conner
     */
    protected void show(double x, double y) {

        rootPane.getStyleClass().clear();
        rootPane.getStyleClass().add("drag-redir-indicator");

        Scene scene = new Scene(rootPane);
        scene.setOnKeyReleased(ke -> {
            if (!ke.isControlDown()) {
                stage.close();
            }
        });

        stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        Button closeBtn = new Button("close");
        closeBtn.getStyleClass().add("redir-close-button");

        closeBtn.setOnAction(a -> {
            stage.close();
        });
        //rootPane.getChildren().add(closeBtn);
        //StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);

        targetDockPane.getScene().getWindow().xProperty().addListener(this::delegateChanged);

        targetDockPane.getScene().getWindow().yProperty().addListener(this::delegateChanged);
        targetDockPane.widthProperty().addListener(this::delegateChanged);
        targetDockPane.heightProperty().addListener(this::delegateChanged);

        stage.setX(x);
        stage.setY(y);
        stage.setWidth(targetDockPane.getWidth());
        stage.setHeight(targetDockPane.getHeight());
        DockTargetController ph = DockRegistry.dockPaneTarget(rootPane).paneController();
        
        //ph.getPaneIndicator().windowBeforeShow(null);
        ph.getPaneIndicator().showWindow(stage);
        stage.show();
    }
    /**
     * Closes the indicator stage
     */
    protected void close() {
        stage.close();
    }

    protected boolean contains(double x, double y) {
        return DockUtil.contains(rootPane, x, y);
    }
    protected void delegateChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        stage.setX(targetDockPane.localToScreen(0, 0).getX());
        stage.setY(targetDockPane.localToScreen(0, 0).getY());
        //System.err.println("DockRedirector topDockPane w = " + topDockPane.getWidth());
        //System.err.println("DockRedirector targetDockPane w = " + targetDockPane.getWidth());

        stage.setWidth(targetDockPane.getWidth());
        stage.setHeight(targetDockPane.getHeight());

    }

    public RedirectorDockPane getRootPane() {
        return rootPane;
    }
    /**
     * The pane which implements the interface {@link DockPaneTarget} and is used
     * to display indicator buttons.
     */
    public static class RedirectorDockPane extends StackPane implements DockPaneTarget {

        private DockTargetController paneController;
        private final DockTargetController targetPaneController;
        private final DockTargetController topPaneController;

        public RedirectorDockPane(DockTargetController topPaneController, DockTargetController targetPaneController) {
            this.targetPaneController = targetPaneController;
            this.topPaneController = topPaneController;
            init();
        }

        public DockTargetController getTargetController() {
            return targetPaneController;
        }

        public DockTargetController getTopController() {
            return topPaneController;
        }

        private void init() {
            paneController = new RedirectorPaneController(this, targetPaneController);
        }

        @Override
        public DockTargetController paneController() {
            return paneController;
        }

        @Override
        public Pane pane() {
            return this;
        }
    }//DelegatePaneController
    /**
     * The pane controller of the target pane of type 
     * {@link RedirectorDockPane}. 
     * 
     */
    public static class RedirectorPaneController extends DockTargetController {

        private final DockTargetController targetPaneController;

        public RedirectorPaneController(Region dockPane, DockTargetController targetPaneController) {
            super((Pane) dockPane);
            this.targetPaneController = targetPaneController;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {
            DockTargetController ph = targetPaneController;
            Button btn = getPaneIndicator().getSelectedButton();
            if (btn != null) {
                ph = (DockTargetController) btn.getUserData();
            }
            ph.doDock(null, node, dockPos);
        }

        @Override
        protected PaneIndicatorTransformer createPaneIndicatorTransformer() {
            return new RedirectorTransformer();
        }
    }

    /**
     * Builds and layouts indicator buttons. 
     */
    public static class RedirectorTransformer extends PaneIndicatorTransformer {

        public RedirectorTransformer() {
        }

        /**
         * Modifies SideButtons
         * @param win a window to be shown
         */
        @Override
        public void windowBeforeShow(Window win) {
            DockTargetController topHandler = ((RedirectorDockPane) getTargetPaneController().getDockPane()).getTopController();
            Region topDockPane = topHandler.getDockPane();
            List<Node> chain = TopNodeHelper.getParentChain(topDockPane, node -> {
                return DockRegistry.isDockPaneTarget(node);
            });
            getTopButtons().getChildren().clear();
            getTopButtons().setId("topButtonsPane");
            getRightButtons().getChildren().clear();
            getBottomButtons().getChildren().clear();
            getLeftButtons().getChildren().clear();

            for (int i = chain.size() - 1; i >= 0; i--) {
                DockTargetController ph = DockRegistry.dockPaneTarget(chain.get(i)).paneController();
                Button top = createSideButton(Side.TOP);
                Button right = createSideButton(Side.RIGHT);
                Button bottom = createSideButton(Side.BOTTOM);
                Button left = createSideButton(Side.LEFT);

                getTopButtons().getChildren().add(top);
                top.setUserData(ph);
                top.setId("top" + i);

                getRightButtons().getChildren().add(right);
                right.setUserData(ph);
                right.setId("right" + i);

                getBottomButtons().getChildren().add(bottom);
                bottom.setUserData(ph);
                bottom.setId("bottom" + i);

                getLeftButtons().getChildren().add(left);
                left.setUserData(ph);
                left.setId("left" + i);
            }
        }

        @Override
        public void notifyPopupShown() {
            adjustWidths();

            topRight(getTopButtons());
            topRight(getRightButtons());
            bottom(getBottomButtons());
            left(getLeftButtons());

            Node dp = getIndicator().getDockPlace2();
            getIndicator().getIndicatorPane().getChildren().remove(dp);
            getIndicator().getIndicatorPane().getChildren().add(0,dp);            
            
            dp = getIndicator().getDockPlace2();
            getIndicator().getIndicatorPane().getChildren().remove(dp);
            getIndicator().getIndicatorPane().getChildren().add(0,dp);
            
            
        }

        protected void topRight(Pane buttons) {
            for (int i = 0; i < buttons.getChildren().size(); i++) {
                Button btn = (Button) buttons.getChildren().get(i);

                double h = btn.getHeight();
                double w = btn.getWidth();

                int xOffset = i * 10;
                int yOffset = (int) (i * (h - 10));
                if (i != 0) {
                    btn.setTranslateX(-xOffset);
                    btn.setTranslateY(yOffset);
                }
            }
        }

        protected void bottom(Pane buttons) {
            for (int i = 0; i < buttons.getChildren().size(); i++) {

                Button btn = (Button) buttons.getChildren().get(i);

                double h = btn.getHeight();
                double w = btn.getWidth();

                int xOffset = i * 10;
                int yOffset = (int) (i * (h - 10));
                if (i != 0) {
                    btn.setTranslateX(xOffset);
                    btn.setTranslateY(-yOffset);
                }
            }
        }

        protected void left(Pane buttons) {
            for (int i = 0; i < buttons.getChildren().size(); i++) {

                Button btn = (Button) buttons.getChildren().get(i);

                double h = btn.getHeight();
                double w = btn.getWidth();

                int xOffset = i * 10;
                int yOffset = (int) (i * (h - 10));

                if (i != 0) {
                    btn.setTranslateX(xOffset);
                    btn.setTranslateY(yOffset);
                }
            }
        }

        protected Button createSideButton(Side side) {
            Button btn = new Button();
            btn.getStyleClass().add(getButtonStyle(side));
            return btn;
        }

        protected String getStylePrefix() {
            return "drag-redir-indicator";
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

        public void adjustWidths() {
            adjustWidths(getTopButtons());
            adjustWidths(getRightButtons());
            adjustWidths(getBottomButtons());
            adjustWidths(getLeftButtons());

        }

        public void adjustWidths(Pane buttons) {
            double maxWidth = -1;
            double maxHeight = -1;
            int idxW = -1;
            int idxH = -1;

            for (int i = 0; i < buttons.getChildren().size(); i++) {

                Region r = (Region) buttons.getChildren().get(i);
                if (r.getWidth() > maxWidth) {
                    maxWidth = r.getWidth();
                    idxW = i;
                }
                if (r.getHeight() > maxHeight) {
                    maxHeight = r.getHeight();
                    idxH = i;
                }

            }
            if (idxW >= 0) {
                for (int i = 0; i < buttons.getChildren().size(); i++) {
                    if (i != idxW) {
                        ((Region) buttons.getChildren().get(i)).minWidthProperty().bind(((Region) buttons.getChildren().get(idxW)).widthProperty());
                    }
                }
            }
        }
    }
}//class DelegeateDragPopup
