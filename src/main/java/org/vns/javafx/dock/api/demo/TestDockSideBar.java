/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockSideBar extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        Button b1 = new Button("b01 - DOCK");
        /*        b1.setOnAction(a -> {
            //new DragPopup(dpCenter);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        BorderPane borderPane = new BorderPane();
        
        StackPane rootPane = new StackPane();
        borderPane.setCenter(rootPane);
        
        rootPane.setId("ROOT PANE");
        
        stage.setTitle("Tests Several DockPanes");
        DockPane dpCenter = new DockPane();
        dpCenter.setPrefHeight(200);
        dpCenter.setId("dpCenter");
        DockNode dn01 = new DockNode();
        dn01.setId("dn01");
        dpCenter.dock(dn01, Side.TOP);
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
        
        SplitPane sp = new SplitPane(dpCenter,dpRight);
        rootPane.getChildren().add(sp);
        //rootPane.setCenter(dpCenter);
        //rootPane.setRight(dpRight);
        DockSideBar sideBar01 = new DockSideBar();
        
        
        //sideBar01.setPrefWidth(24);
        Scene scene = new Scene(borderPane);

        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        
        Stage stage01 = new Stage();
        StackPane rootPane01 = new StackPane();
        rootPane01.setId("ROOT PANE 01");
        
        stage01.setTitle("STAGE01: Tests Several DockPanes ");
        DockPane stg01dp01 = new DockPane();
        //stg01dp01.paneHandler().setUsedAsDockTarget(false);
        stg01dp01.setPrefHeight(200);
        stg01dp01.setPrefWidth(200);
        stg01dp01.setId("stg01dp01");
        DockNode stg01dn01 = new DockNode();
        stg01dn01.setId("stg01dn01");
        stg01dp01.dock(stg01dn01, Side.TOP);
        
        
        stage.show();
        
        Scene scene01 = new Scene(stg01dp01);
        stage01.setScene(scene01);
        stage01.show();
        borderPane.setRight(sideBar01);
        sideBar01.dock(dn02);
        
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
