/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class Test01 extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        BorderPane borderPane = new BorderPane();
        
        stage.setTitle("Test DockSideBar");

        borderPane.setPrefHeight(300);
        borderPane.setPrefWidth(300);
        
        Button b01 = new Button("b01");
        Button gb = new Button("gb of bo1");
        b01.setGraphic(gb);
        
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(borderPane);
        //scene.getRoot().setStyle("-fx-background-color: yellow");

        Pane p = new Pane();
        Button b02 = new Button("add pane");
        b02.setOnAction(a -> {
            System.err.println("1. " + b01.getGraphic());
           // b01.setGraphic(null);
            p.getChildren().add(gb);
            System.err.println("2. " + b01.getGraphic());
        });
        
        borderPane.setTop(b01);
        borderPane.setCenter(p);
        borderPane.setBottom(b02);
        stage.setScene(scene);
        stage.setOnShown(s -> {
            borderPane.getChildren().forEach(n-> {
                System.err.println("parent=" + n.getParent());
            });
        });
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

}

