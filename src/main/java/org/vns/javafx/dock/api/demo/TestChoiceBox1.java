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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestChoiceBox1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {

        Text msg = new Text("JavaFX animation is cool!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));
        //Pane root = new Pane(msg);
        
        ComboBox<Integer> cb = new ComboBox<>();
        cb.getItems().addAll(1111111,2222222,3333333,4444444,
                1111111,2222222,3333333,4444444,
                1111111,2222222,3333333,4444444,
                1111111,2222222,3333333,4444444);
        
        Pane root = new Pane(cb);
        
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();
        /* Set up a Timeline animation */
// Get the scene width and the text width
/*        double sceneWidth = scene.getWidth();
        double msgWidth = msg.getLayoutBounds().getWidth();
// Create the initial and final key frames
        KeyValue initKeyValue
                = new KeyValue(msg.translateXProperty(), sceneWidth);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);
        KeyValue endKeyValue
                = new KeyValue(msg.translateXProperty(), -1.0 * msgWidth);
        //= new KeyValue(msg.translateXProperty(), 0);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(3), endKeyValue);
// Create a Timeline object
        Timeline timeline = new Timeline(initFrame, endFrame);
        timeline.setRate(0.5);
// Let the animation run forever
        timeline.setCycleCount(Timeline.INDEFINITE);
// Start the animation
        timeline.play();
*/
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
