/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.SceneGraphData;

/**
 *
 * @author Valery
 */
public class TestVarName extends Application {

 
    @Override
    public void start(Stage stage) {
        Button btn = new Button("Create");
        Button btn1 = new Button("nameOf");
        
        VBox root = new VBox(btn,btn1);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        SceneGraphData d = SceneGraphData.getInstance();
        
        btn.setOnAction(a -> {
           d.getVariables().put("label", new Label("label"));
           d.getVariables().put("label1", new Label("label1"));
           d.getVariables().put("label2", new Label("label2"));
           d.getVariables().put("label4", new Label("label4"));
           d.getVariables().put("label5", new Label("label5")); 
           d.getVariables().put("label8", new Label("label8"));
           
        });
        btn1.setOnAction(a -> {
            System.err.println("var = " + d.nameByObject(new Label()));
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
