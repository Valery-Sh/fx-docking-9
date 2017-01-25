package org.vns.javafx.dock.api;

import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
 *
 * @author Valery Shyshkin
 */
public class DockRedirector {

    private RedirectorDockPane rootPane;
    private Region targetDockPane;
    private final Region topDockPane;
    private Stage stage;
    private final DoubleProperty xProperty = new SimpleDoubleProperty();
    private final DoubleProperty yProperty = new SimpleDoubleProperty();

    public DockRedirector(Region topDockPane) {
        this.topDockPane = topDockPane;
        init();
    }

    private void init() {
        targetDockPane = findTargetDockPane();
        rootPane = new RedirectorDockPane(DockRegistry.dockPaneTarget(topDockPane).paneHandler(), DockRegistry.dockPaneTarget(targetDockPane).paneHandler());
    }

    public Region findTargetDockPane() {
        List<Node> nodes = TopNodeHelper.getParentChain(topDockPane, node -> {
            return DockRegistry.isDockPaneTarget(node);
        });
        return (Region) nodes.get(nodes.size() - 1);
    }

    public PaneHandler getPaneHandler() {
        return DockRegistry.dockPaneTarget(targetDockPane).paneHandler();
    }

    public Stage getStage() {
        return stage;
    }

    public static DockRedirector show(Region topDockPane) {
        DockRedirector redir = new DockRedirector(topDockPane);
        redir.show();
        return redir;
    }

    public void show() {
        Point2D p = topDockPane.localToScreen(0, 0);
        double w = topDockPane.getWidth();
        double h = topDockPane.getHeight();
        getRootPane().setPrefSize(w, h);
        show(p.getX(), p.getY());
    }

    public void show(double x, double y) {

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
        PaneHandler ph = DockRegistry.dockPaneTarget(rootPane).paneHandler();
        
        //ph.getPaneIndicator().windowBeforeShow(null);
        ph.getPaneIndicator().showWindow(stage);
        stage.show();
    }

    public void close() {
        stage.close();
    }

    public boolean contains(double x, double y) {
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

    public static class RedirectorDockPane extends StackPane implements DockPaneTarget {

        private PaneHandler paneHandler;
        private final PaneHandler targetPaneHandler;
        private final PaneHandler topPaneHandler;

        public RedirectorDockPane(PaneHandler topPaneHandler, PaneHandler targetPaneHandler) {
            this.targetPaneHandler = targetPaneHandler;
            this.topPaneHandler = topPaneHandler;
            init();
        }

        public PaneHandler getTargetHandler() {
            return targetPaneHandler;
        }

        public PaneHandler getTopHandler() {
            return topPaneHandler;
        }

        private void init() {
            paneHandler = new RedirectorPaneHandler(this, targetPaneHandler);
        }

        @Override
        public PaneHandler paneHandler() {
            return paneHandler;
        }

        @Override
        public Pane pane() {
            return this;
        }
    }//DelegatePaneHandler

    public static class RedirectorPaneHandler extends PaneHandler {

        private final PaneHandler targetPaneHandler;

        public RedirectorPaneHandler(Region dockPane, PaneHandler targetPaneHandler) {
            super((Pane) dockPane);
            this.targetPaneHandler = targetPaneHandler;
        }

        @Override
        protected void doDock(Point2D mousePos, Node node, Side dockPos) {
            PaneHandler ph = targetPaneHandler;
            Button btn = getPaneIndicator().getSelectedButton();
            if (btn != null) {
                ph = (PaneHandler) btn.getUserData();
            }
            ph.doDock(null, node, dockPos);
        }

        @Override
        protected PaneIndicatorTransformer createPaneIndicatorTransformer() {
            return new RedirectorTransformer();
        }
    }

    /**
     *
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
            PaneHandler topHandler = ((RedirectorDockPane) getTargetPaneHandler().getDockPane()).getTopHandler();
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
                PaneHandler ph = DockRegistry.dockPaneTarget(chain.get(i)).paneHandler();
                Button top = createSideButton(Side.TOP);
/*                if ( i != 0 ) {
                    top.setTooltip(new Tooltip("Parent-" + i));
                } else {
                    top.setTooltip(new Tooltip("In Front Node"));
                }
*/

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
