/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
public class TestAnimeTnter1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {

        Text msg = new Text("Hopping text!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));
        Pane root = new Pane(msg);
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Hopping Text");
        stage.show();
// Setup a Timeline animation
        double start = scene.getWidth();
        double end = -1.0 * msg.getLayoutBounds().getWidth();
        KeyFrame[] frame = new KeyFrame[11];
        for (int i = 0; i <= 10; i++) {
            double pos = start - (start - end) * i / 10.0;
// Set 2.0 seconds as the cycle duration
            double duration = i / 5.0;
// Use a discrete interpolator
            KeyValue keyValue = new KeyValue(msg.translateXProperty(),
                    pos,
                    Interpolator.DISCRETE);
            frame[i] = new KeyFrame(Duration.seconds(duration), keyValue);
        }
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
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
