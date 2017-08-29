/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestTitledPane extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        Button btn01 = new Button("sp btn01");
        StackPane root = new StackPane();
        scene = new Scene(root);
        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");
        HBox content = new HBox();
        TitledPane tp = new TitledPane();
        tp.setCollapsible(false);
        tp.setExpanded(false);
        //tp.setClip(new Button("Button"));
        //tp.setContentDisplay(ContentDisplay.LEFT);
        tp.setGraphic(new DockTitleBar("Button"));
        root.getChildren().add(tp);
        stage.setScene(scene);
        stage.show();
        tp.setContent(content);
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);
        
        
    }

    public static void main(String[] args) {
        Application.launch(args);

    }
    
    
}
