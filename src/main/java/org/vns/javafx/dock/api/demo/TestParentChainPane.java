/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.util.ParentChainPopup;

/**
 *
 * @author Valery
 */
public class TestParentChainPane extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        StackPane sp1 = new StackPane();
        sp1.setId("sp1");
        StackPane root = new StackPane(sp1);
        root.setId("root");
        StackPane sp2 = new StackPane();
        sp2.setId("sp2");
        sp1.getChildren().add(sp2);
        ParentChainPopup pcp = new ParentChainPopup(sp2);
        scene = new Scene(root, 250, 250);
        
        stage.setScene(scene);
        
        
        stage.show();
        pcp.show(150,250);
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
