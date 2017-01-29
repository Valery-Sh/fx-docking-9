/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode2;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockSideBar.Rotation;
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
        
        BorderPane borderPane = new BorderPane();
        
        stage.setTitle("Test DockSideBar");

        DockSideBar sideBar01 = new DockSideBar();
        sideBar01.setOrientation(Orientation.VERTICAL);
        //sideBar01.setSide(Side.RIGHT);
        sideBar01.setRotation(Rotation.UP_DOWN);
        sideBar01.setHideOnExit(true);
        
        borderPane.setRight(sideBar01);
        borderPane.setPrefHeight(300);
        borderPane.setPrefWidth(300);
        
        Button b01 = new Button("Change Rotate Angle");
        
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(borderPane);
        scene.getRoot().setStyle("-fx-background-color: yellow");

        DockNode2 dn01 = new DockNode2();
        dn01.setPrefHeight(100);
        dn01.nodeHandler().setTitle("DockNode: dn01");
        b01.setOnAction(a -> {
            if (sideBar01.getRotation()== Rotation.DEFAULT) {
                sideBar01.setRotation(Rotation.UP_DOWN);
            } else if (sideBar01.getRotation()== Rotation.UP_DOWN) {
                sideBar01.setRotation(Rotation.DOWN_UP);
            }  else if (sideBar01.getRotation()== Rotation.DOWN_UP) {
                sideBar01.setRotation(Rotation.DEFAULT );
            }
            
        });
        Button b02 = new Button("Change Orientation");
        VBox vb = new VBox();
        vb.getChildren().addAll(b01,b02);
        borderPane.setLeft(vb);
        
        b02.setOnAction(a -> {
            if (sideBar01.getOrientation()== Orientation.VERTICAL) {
                sideBar01.setOrientation(Orientation.HORIZONTAL);
            } else if (sideBar01.getOrientation()== Orientation.HORIZONTAL) {
                sideBar01.setOrientation(Orientation.VERTICAL);
            } 
            
        });        
        
        sideBar01.dock(dn01);
        
        DockNode2 dn02 = new DockNode2();
        dn02.setPrefHeight(100);
        dn02.nodeHandler().setTitle("DockNode: dn02");        
        
        sideBar01.dock(dn02);
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
