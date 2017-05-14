/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestAnimePath1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {

// Create the node
        Rectangle rect = new Rectangle(20, 10, Color.RED);
// Create the path
        Circle path = new Circle(100, 100, 100);
        path.setFill(null);
        path.setStroke(Color.BLACK);
        Group root = new Group(rect, path);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Path Transition");
        stage.show();
// Set up a path transition for the rectangle
        PathTransition pt = new PathTransition(Duration.seconds(2), path, rect);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pt.setCycleCount(PathTransition.INDEFINITE);
        pt.setAutoReverse(true);
        pt.play();
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
