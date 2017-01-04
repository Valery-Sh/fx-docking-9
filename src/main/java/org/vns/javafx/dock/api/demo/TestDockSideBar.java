/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
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
        
        /*        b1.setOnAction(a -> {
            //new DragPopup(dpCenter);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        BorderPane borderPane = new BorderPane();
        
        StackPane rootPane = new StackPane();
        borderPane.setCenter(rootPane);
        
        rootPane.setId("ROOT PANE");
        
        stage.setTitle("Tests Several DockSideBar");
        DockPane dpCenter = new DockPane();
        DockSideBar sideBar01 = new DockSideBar();
        sideBar01.setOrientation(Orientation.VERTICAL);
        sideBar01.setSide(Side.RIGHT);
        sideBar01.setPrefWidth(50);
        borderPane.setRight(sideBar01);
        borderPane.setPrefHeight(200);
        Button b01 = new Button("Chanse Side Btn");
        borderPane.setCenter(b01);
        
        Scene scene = new Scene(borderPane);
        scene.getRoot().setStyle("-fx-background-color: yellow");

        DockNode dn01 = new DockNode();
        dn01.setPrefHeight(100);
        dn01.nodeHandler().setTitle("DockNode: dn01");
        b01.setOnAction(a -> {
            if (sideBar01.getSide() == Side.RIGHT) {
                sideBar01.setSide(Side.LEFT);
            } else {
                sideBar01.setSide(Side.RIGHT);
            }
        });
        sideBar01.dock(dn01, Side.TOP);
        
        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        stage.show();
        
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
