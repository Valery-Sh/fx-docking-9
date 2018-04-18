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
import javafx.scene.control.PopupControl;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.TrashTray;

/**
 *
 * @author Valery
 */
public class TestTrashTray extends Application {

    Stage stage;
    Scene scene;
    Button saveBtn;

    @Override
    public void start(Stage stage) {

        Button testBtn = new Button("textBtn");
        testBtn.getStyleClass().add(DockTitleBar.StyleClasses.PIN_BUTTON.cssClass());

        Button removeBtn = new Button("remove testBtn");
        Button addTestBtn = new Button("add testBtn");
        Button createTestBtn = new Button("createBtn");
        HBox hbox = new HBox();
        VBox root = new VBox();
        //StackPane root = new StackPane(createTestBtn, removeBtn, addTestBtn);
        //StackPane root = new StackPane();
        //root.getChildren().add(treeView);
      
        removeBtn.setOnAction(a -> {
            saveBtn = testBtn;
            root.getChildren().remove(testBtn);
        });

        addTestBtn.setOnAction(a -> {
            //hbox.getChildren().add(testBtn);
        });
        createTestBtn.setOnAction(a -> {

            testBtn.graphicProperty().addListener((v, ov, nv) -> {
                System.err.println("oldValue = " + ov + "; newValue = " + nv);
            });
            root.getChildren().add(testBtn);
        });

        root.setPrefSize(500, 100);
        scene = new Scene(root);
//        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        //root.getStyleClass().add("test-css");
        //root.setStyle("-fx-background-color: blue");
        root.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");        
        
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();
        
/*        PopupControl pc = new PopupControl();
        StackPane pcRoot = new StackPane();
        TrashTray tray = new TrashTray();
        //Button b = new Button("A");
        pcRoot.getChildren().add(tray);
        pcRoot.setPrefSize(100,100);
        //pcRoot.setStyle("-fx-background-color: green");
        pcRoot.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");                
        pc.getScene().setRoot(pcRoot);
        //pc.getScene().setFill(Color.RED);
  */
        TrashTray.showStage(stage);
        
/*        pc.show(stage,50,50);
        pc.setWidth(100);
        pc.setHeight(100);
*/        
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
