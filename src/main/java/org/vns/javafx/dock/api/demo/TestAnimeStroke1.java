/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestAnimeStroke1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(200, 50, Color.WHITE);

        Pane root = new Pane(rect);
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text using a Translate Transition");
        stage.show();

        StrokeTransition strokeTransition = new StrokeTransition(Duration.seconds(2), rect);
        strokeTransition.setFromValue(Color.RED);
        strokeTransition.setToValue(Color.BLUE);
        strokeTransition.setCycleCount(StrokeTransition.INDEFINITE);
        strokeTransition.setAutoReverse(true);
        strokeTransition.play();
        
        
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
