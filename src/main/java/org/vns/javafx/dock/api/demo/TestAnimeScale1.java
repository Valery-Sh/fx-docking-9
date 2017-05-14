/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
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
public class TestAnimeScale1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {

        Rectangle rect = new Rectangle(200, 50, Color.RED);
        HBox root = new HBox(rect);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scale Transition");
        stage.show();
// Set up a scale transition for the rectangle
        ScaleTransition st = new ScaleTransition(Duration.seconds(2), rect);
        st.setFromX(1.0);
        st.setToX(0.20);
        //st.setFromY(1.0);
        //st.setToY(0.20);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
        
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
