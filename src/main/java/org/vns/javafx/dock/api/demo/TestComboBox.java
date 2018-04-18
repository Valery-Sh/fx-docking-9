/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 *
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestComboBox extends Application {

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

        Stage stage1 = new Stage();
        stage1.initOwner(stage);
        
        Button btn1 = new Button("Stage1 Button");
        VBox vbox = new VBox(btn1);
        Scene scene1 = new Scene(vbox);
        stage1.setScene(scene1);
        
        stage1.show();
        
        Button btn2 = new Button("Stage1 Button2");
        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
        pc.show(stage, 20, 2);
        
        
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
