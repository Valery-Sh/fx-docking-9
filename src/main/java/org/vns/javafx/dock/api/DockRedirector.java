package org.vns.javafx.dock.api;

import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.PaneHandler.PaneSideIndicator;

/**
 *
 * @author Valery Shyshkin
 */
public class DockRedirector {

    private DockPaneRedirector rootPane;
    private Region targetDockPane;
    private final Region topDockPane;
    private Stage stage;
    private final DoubleProperty xProperty = new SimpleDoubleProperty();
    private final DoubleProperty yProperty = new SimpleDoubleProperty();

    /*    public DockRedirector(PaneHandler targetPaneHandler) {
        rootPane = new DockPaneRedirector(targetPaneHandler,targetPaneHandler);
        targetDockPane = (Pane) targetPaneHandler.getDockPane();
        topDockPane = targetDockPane;
    }
     */
    public DockRedirector(Region topDockPane) {
        this.topDockPane = topDockPane;
        init();
    }

    private void init() {
        targetDockPane = findTargetDockPane();
        //System.err.println("DockRedirector targetDockPane = " + targetDockPane);
        //System.err.println("DockRedirector topDockPane = " + topDockPane);

        rootPane = new DockPaneRedirector(((DockPaneTarget) topDockPane).paneHandler(), ((DockPaneTarget) targetDockPane).paneHandler());
    }

    public Region findTargetDockPane() {

        List<Node> nodes = TopNodeHelper.getParentChain(topDockPane, node -> {
            return (node instanceof DockPaneTarget);
        });

        return (Region) nodes.get(nodes.size() - 1);
    }

    public PaneHandler getPaneHandler() {
        return ((DockPaneTarget) targetDockPane).paneHandler();
    }

    public Stage getStage() {
        return stage;
    }

    public void show(double x, double y) {

        rootPane.getStyleClass().clear();
        rootPane.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-border-color: red");

        Scene scene = new Scene(rootPane);
        //Scene scene = new Scene(sp);
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
        PaneHandler ph = ((DockPaneTarget) rootPane).paneHandler();
        ph.getPaneIndicator().windowBeforeShow(null);
        //stage.setOnShown(ev -> ph.getPaneIndicator().windowOnShown(ev, null));

        stage.show();
    }

    public void close() {
        stage.close();
    }

    public boolean contains(double x, double y) {
        /*        Bounds b = rootPane.localToScreen(rootPane.getBoundsInLocal());
        System.err.println("b.X=" + b.getMinX());
        System.err.println("b.Y=" + b.getMinY());
        System.err.println("b.W=" + b.getWidth());
        System.err.println("b.H=" + b.getHeight());
        System.err.println("x=" + x);
        System.err.println("y=" + y);
        System.err.println("=================================");
         */

        //rootPane.localToScreen(rootPane.getBoundsInLocal()).contains(x, y);            
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

    public DockPaneRedirector getRootPane() {
        return rootPane;
    }

    public static class DockPaneRedirector extends StackPane implements DockPaneTarget {

        private PaneHandler paneHandler;
        private final PaneHandler targetPaneHandler;
        private final PaneHandler topPaneHandler;

        public DockPaneRedirector(PaneHandler topPaneHandler, PaneHandler targetPaneHandler) {
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
            return new ParentChainTransformer();
        }
    }

    /**
     *
     */
    public static class ParentChainTransformer extends PaneHandler.PaneIndicatorTransformer {

        public ParentChainTransformer() {
        }
        /**
         * Modifies SideButtons
         *
         * @param r doesn't used here
         */
        @Override
        public void windowBeforeShow(Region r) {
            PaneHandler topHandler = ((DockPaneRedirector) getTargetPaneHandler().getDockPane()).getTopHandler();
            Region topDockPane = topHandler.getDockPane();
//            System.err.println("ParentChainTransformer sideIndicatorShowing targetDockPane=" + ((DockPaneRedirector)getTargetPaneHandler().getDockPane()));            
//            System.err.println("ParentChainTransformer sideIndicatorShowing topDockPane=" + topDockPane);                        

            List<Node> chain = TopNodeHelper.getParentChain(topDockPane, node -> {
                return (node instanceof DockPaneTarget);
            });
//            System.err.println("ParentChainTransformer sideIndicatorShowing chain.size()=" + chain.size());
            getTopButtons().getChildren().clear();
            getTopButtons().setId("topButtonsPane");
            getRightButtons().getChildren().clear();
            getBottomButtons().getChildren().clear();
            getLeftButtons().getChildren().clear();

            for (int i = chain.size() - 1; i >= 0; i--) {
                PaneHandler ph = ((DockPaneTarget) chain.get(i)).paneHandler();
                Button top = createSideButton(Side.TOP);
                Button right = createSideButton(Side.RIGHT);
                Button bottom = createSideButton(Side.BOTTOM);
                Button left = createSideButton(Side.LEFT);

                getTopButtons().getChildren().add(top);
                top.setUserData(ph);
                top.setId("top" + i);
                //System.err.println("before show top.getHeight=" + top.getHeight());
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
//            System.err.println("ParentChainTransformer sideIndicatorShowing topButtons.sz=" + getTopButtons().getChildren().size());                        
        }

        @Override
        public void windowOnShown(WindowEvent ev, Region r) {
            //PaneHandler topHandler = ((DockPaneRedirector) getTargetPaneHandler().getDockPane()).getTopHandler();
            //Region topDockPane = topHandler.getDockPane();
            
            adjustWidths();

            topRight(getTopButtons());
            topRight(getRightButtons());
            bottom(getBottomButtons());
            left(getLeftButtons());
        }
        
        protected void topRight(Pane buttons ) {
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
        protected void bottom(Pane buttons ) {
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
        protected void left(Pane buttons ) {
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
            Button b = (Button) buttons.getChildren().get(0);
            Insets ins = b.getInsets();
            System.err.println("ins.left=" + ins.getLeft());
            System.err.println("ins.right=" + ins.getRight());
            if (idxW >= 0) {
                for (int i = 0; i < buttons.getChildren().size(); i++) {
                    if (i != idxW) {
                        ((Region) buttons.getChildren().get(i)).minWidthProperty().bind(((Region) buttons.getChildren().get(idxW)).widthProperty());
                    }
                }
            }
            System.err.println("btn width=" + ((Region) buttons.getChildren().get(0)).getWidth());
            if (idxW >= 0) {

                System.err.println("maxWidth=" + maxWidth + "; sum=" + (buttons.getChildren().size() - 1) * 5);
                System.err.println("vb w =" + buttons.getWidth());
                //vb.setPrefWidth(maxWidth + (vb.getChildren().size()-1) * 5 );
                //popup.setWidth(maxWidth + 120);
                //buttons.setPrefSize(100, 100);
                Platform.runLater(() -> {
                    System.err.println("btn  w =" + ((Button) buttons.getChildren().get(0)).getWidth());
                    System.err.println("vb   w =" + buttons.getWidth());
                });

                //popup.setWidth(100);
            }

        }
    }
}//class DelegeateDragPopup
