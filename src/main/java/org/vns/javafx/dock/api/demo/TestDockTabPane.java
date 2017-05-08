package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public class TestDockTabPane extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        Button dockButton = new Button("To be docked 1");
        dockButton.setMaxHeight(dockButton.getPrefHeight());
        dockButton.setMaxWidth(dockButton.getPrefWidth());
        
        Dockable dockableButton = DockRegistry.getInstance().getDefaultDockable(dockButton);
        dockableButton.dockableController().setDragNode(dockButton);
        dockableButton.dockableController().setTitle("Dockable Button");

        Button b1 = new Button("b01 - DOCK");
        /*        b1.setOnAction(a -> {
            //new DragPopup(dpCenter);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        //BorderPane rootPane = new BorderPane();
        StackPane rootPane = new StackPane();
        rootPane.setId("ROOT PANE");

        stage.setTitle("Primary Tests Several DockPanes");
        
        DockPane dpCenter = new DockPane();
        
        dpCenter.setPrefHeight(200);
        dpCenter.setId("dpCenter");

        DockNode dn01 = new DockNode();
        dn01.setId("dn01");
        dpCenter.dock(dn01, Side.TOP);
        dpCenter.dock(dockableButton, Side.TOP);
        dn01.setTitle("DockNode: dn01");
        Button dn01Btn = new Button("Print");
        dn01Btn.setOnAction((event) -> {
            DockUtil.print(dn01Btn.getScene().getRoot());
        });
        dpCenter.getChildren().add(dn01Btn);

        DockPane dpRight = new DockPane();
        dpRight.setPrefHeight(200);
        dpRight.setId("dpRight");
        DockNode dn02 = new DockNode();
        dn02.setId("dn02");
        dpRight.dock(dn02, Side.TOP);
        Button dn02Btn = new Button("Print");
        dn02Btn.setOnAction((event) -> {
            DockUtil.print(dn02Btn.getScene().getRoot());
        });

        dpRight.getChildren().add(dn02Btn);

        SplitPane sp = new SplitPane(dpCenter, dpRight);
        rootPane.getChildren().add(sp);
        //rootPane.setCenter(dpCenter);
        //rootPane.setRight(dpRight);

        Scene scene = new Scene(rootPane);

        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);

        Stage stage01 = new Stage();
        StackPane rootPane01 = new StackPane();
        rootPane01.setId("ROOT PANE 01");

        stage01.setTitle("STAGE01: Tests Several DockPanes ");
        DockPane stg01dp01 = new DockPane();
        //stg01dp01.targetController().setUsedAsDockTarget(false);
        stg01dp01.setPrefHeight(200);
        stg01dp01.setPrefWidth(200);
        stg01dp01.setId("stg01dp01");
        DockNode stg01dn01 = new DockNode();
        stg01dn01.setId("stg01dn01");

        Button btn01 = new Button("Button of Tab 01");
        Pane pane01 = new Pane(btn01);
        stg01dn01.setContent(pane01);
        pane01.setStyle("-fx-background-color: aqua");

        //stg01dn01.getChildren().add(btn01);
        stg01dp01.dock(stg01dn01, Side.TOP);

        DockNode stg01dn02 = new DockNode();
        stg01dn02.setTitle("stg01dn02");
        stg01dn02.setId("stg01dn02");
        Button btn02 = new Button("Button of Tab 02");
        VBox vb = new VBox();
        stg01dn02.setContent(vb);
        vb.getChildren().add(btn02);

        DockNode stg01dn03 = new DockNode();
        stg01dn03.setTitle("stg01dn03");
        stg01dn03.setId("stg01dn03");
        Button btn03 = new Button("Button of Tab 03");

        Pane stack02 = new Pane(btn02);
        vb.getChildren().add(stack02);
        stack02.setStyle("-fx-background-color: aqua");

        StackPane stack03 = new StackPane(btn03);
        stg01dn03.setContent(stack03);
        stack03.setStyle("-fx-background-color: gray");

        //stg01dp01.dock(stg01dn02, Side.TOP);
        /*DockTab stg01tab01 = new DockTab(stg01dn02);
        stg01tab01.setTitle("DockTab 01");
        stg01dp01.dock(stg01tab01, Side.RIGHT);
         */
        DockTabPane tabPane01 = new DockTabPane();
        tabPane01.setSide(Side.TOP);
        //DockableDockPane dockPane = new DockableDockPane(tabPane01);
        //tabPane01.openDragTag();
        Button tbButton = new Button("VALERA");
        //Rectangle tbIv = new Rectangle(20, 20);
        //ImageView tbIv = new ImageView();
        //tbRect.setFill(new ImageView());
        //tbIv.setOpacity(50);

        //tbRect.getStyleClass().add("drag-button");
        //tbIv.getStyleClass().add("drag-image-view");
        //tabPane01.dockableController().setDragNode(tbIv);
        //tabPane01.getChildren().add(tbIv);
        //tbIv.toFront();
        //tbIv.setTranslateX(-5);
        //tbIv.setTranslateY(-5);
        //tabPane01.targetController().dock(stg01dn02, Side.TOP);
        //tabPane01.targetController().dock(stg01dn03, Side.TOP);        
        stg01dp01.dock(tabPane01, Side.TOP);
        //stg01dp01.dock(dockPane, Side.T);

        //tabPane01.setSide(Side.RIGHT);
        btn03.setOnAction(a -> {
            System.err.println("tabPane01.getChildren().size()=" + tabPane01.getChildren().size());
            tabPane01.getChildren().forEach(n -> {
                System.err.println("class=" + n.getClass().getName() + "; vis=" + n.isVisible());

                Bounds bnd = n.getBoundsInParent();
                if (n instanceof Button) {
                    System.err.println("isRes=" + n.isResizable());
                    System.err.println("minx=" + bnd.getMinX());
                    System.err.println("maxx=" + bnd.getMaxX());
                    System.err.println("miny=" + bnd.getMinY());
                    System.err.println("maxy=" + bnd.getMaxY());
                    System.err.println("w=" + bnd.getWidth());
                    System.err.println("h=" + bnd.getHeight());
                }

                System.err.println("class=" + n.getClass().getName() + "; vis=" + n.isVisible());

            });
        });

        stage.show();

        Scene scene01 = new Scene(stg01dp01);
        stage01.setScene(scene01);
        stage01.show();
        
        dockButton.setMaxHeight(dockButton.getHeight());
        dockButton.setMaxWidth(dockButton.getWidth());

        tabPane01.dock(stg01dn03);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void initSceneDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() || db.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String url = null;
            if (db.hasFiles()) {
                url = db.getFiles().get(0).toURI().toString();
            } else if (db.hasUrl()) {
                url = db.getUrl();
            }
            if (url != null) {
                //songModel.setURL(url);
                //songModel.getMediaPlayer().play();
            }
            System.err.println("DROPPED");
            event.setDropCompleted(url != null);
            event.consume();
        });
    }
}
