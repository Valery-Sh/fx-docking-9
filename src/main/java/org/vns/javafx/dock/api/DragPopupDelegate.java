package org.vns.javafx.dock.api;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class DragPopupDelegate {

    private DockPaneDelegate rootPane;
    private Pane delegatePane;
    private Stage stage;
    private DoubleProperty xProperty = new SimpleDoubleProperty();
    private DoubleProperty yProperty = new SimpleDoubleProperty();

    public DragPopupDelegate(PaneHandler delegateHandler) {
        rootPane = new DockPaneDelegate(delegateHandler);
        delegatePane = (Pane) delegateHandler.getDockPane();
    }
    public PaneHandler getPaneHandler() {
        return ((DockPaneTarget)delegatePane).paneHandler();
    }

    public Stage getStage() {
        return stage;
    }
    
    public void show(double x, double y) {
        //rootPane.setPrefHeight(100);
        //rootPane.setPrefWidth(100);

        HBox sp = new HBox();
        sp.getChildren().add(new Button());
        sp.getStyleClass().clear();
        sp.setStyle("-fx-border-width: 2px; -fx-border-color: red; -fx-background-color: transparent");
        sp.setPrefHeight(100);
        sp.setPrefWidth(100);

        rootPane.getStyleClass().clear();
        rootPane.setStyle("-fx-background-color: transparent; -fx-border-width: 5px; -fx-border-color: red");

        Scene scene = new Scene(rootPane);
        //Scene scene = new Scene(sp);
        stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        //Bounds dlgBounds = delegatePane.boundsInParentProperty().get();
        //delegatePane.s
        Button closeBtn = new Button("close");
        closeBtn.setOnAction(a -> {
            stage.close();
        });
        rootPane.getChildren().add(closeBtn);
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);

        stage.setOnShown(e -> {
            Bounds dlgBounds = delegatePane.boundsInLocalProperty().get();
            System.err.println("x = " + dlgBounds.getMinX() + ", y=" + dlgBounds.getMinY());
            System.err.println("w = " + dlgBounds.getWidth() + ", h=" + dlgBounds.getHeight());
        });
        delegatePane.localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
            @Override
            public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
                System.err.println("rootPane.w = " + rootPane.getWidth());
                System.err.println("rootPane.h = " + rootPane.getHeight());
            }
        });
        delegatePane.setOnKeyPressed(ev -> {System.err.println("!!!!!!!!!!!!!!!! KEY PRESSED");});
        delegatePane.getScene().getWindow().xProperty().addListener(this::delegateChanged);
        
        delegatePane.getScene().getWindow().yProperty().addListener(this::delegateChanged);
        delegatePane.widthProperty().addListener(this::delegateChanged);
        delegatePane.heightProperty().addListener(this::delegateChanged);

        stage.setX(x);
        stage.setY(y);

        stage.show();
    }
    public void close() {
        stage.close();
    }
    public boolean contains(double x, double y) {
        Bounds b = rootPane.localToScreen(rootPane.getBoundsInLocal());
        System.err.println("b.X=" + b.getMinX());
        System.err.println("b.Y=" + b.getMinY());
        System.err.println("b.W=" + b.getWidth());
        System.err.println("b.H=" + b.getHeight());
        System.err.println("x=" + x);
        System.err.println("y=" + y);
        System.err.println("=================================");
        
        
        //rootPane.localToScreen(rootPane.getBoundsInLocal()).contains(x, y);            
        return DockUtil.contains(rootPane, x, y);
    }
    protected void delegateChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        stage.setX(delegatePane.localToScreen(0, 0).getX());
        stage.setY(delegatePane.localToScreen(0, 0).getY());
        stage.setWidth(delegatePane.getWidth());
        stage.setHeight(delegatePane.getHeight());

    }

    public DockPaneDelegate getRootPane() {
        return rootPane;
    }

    public static class DockPaneDelegate extends StackPane implements DockPaneTarget {

        private PaneHandler paneHandler;
        private PaneHandler delegateHandler;

        public DockPaneDelegate(PaneHandler delegateHandler) {
            this.delegateHandler = delegateHandler;
            System.err.println("delegateHandler: " + delegateHandler.getDockPane());
            init();
        }

        public PaneHandler getDelegateHandler() {
            return delegateHandler;
        }

        private void init() {
            paneHandler = new DelegatePaneHandler(this, delegateHandler);
        }

        @Override
        public PaneHandler paneHandler() {
            return paneHandler;
        }

        @Override
        public Pane pane() {
            return this;
        }

        public static class DelegatePaneHandler extends PaneHandler {

            private PaneHandler delegate;

            public DelegatePaneHandler(Region dockPane, PaneHandler delegate) {
                super((Pane) dockPane);
                this.delegate = delegate;
            }

            @Override
            protected void doDock(Point2D mousePos, Node node, Side dockPos) {
                delegate.doDock(null, node, dockPos);
            }

            /*            @Override
            public Dockable dock(Dockable dockable, Side dockPos) {
                return delegate.dock(null, dockable, dockPos);
            }
            
            @Override
            public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
                System.err.println("2. delegateHandler: " + delegate.getDockPane());
                return delegate.dock(null, dockable, dockPos, target);
            }
             */
        }

    }//DelegatePaneHandler

}//class DelegeateDragPopup
