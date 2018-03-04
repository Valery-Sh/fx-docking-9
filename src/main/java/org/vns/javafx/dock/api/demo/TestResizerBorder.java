package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.view.NodeResizer;

/**
 *
 * @author Valery
 */
public class TestResizerBorder extends Application {

    Stage stage;
    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        stage.initStyle(StageStyle.TRANSPARENT);
        Rectangle rect = new Rectangle(50,20);
        
        Button btn = new Button("Btn");
        StackPane root = new StackPane(btn);
        rect.widthProperty().bind(root.widthProperty());
        rect.heightProperty().bind(root.heightProperty());
        
        //rect.
        rect.setFill(null);
        rect.setStroke(Color.AQUA);
        //StackPane sp = new StackPane();
        //VBox root = new VBox(rect);
       
        
        
        
        //root.setStyle("-fx-background-color: transparent;" );
        
        //SubScene sc = new SubScene(pane,50,50);
        //sc.setFill(null);
        Rectangle rect1 = new Rectangle(20,20);
        rect1.setFill(Color.YELLOW);
        
        //VBox root = new VBox(rect1,sc);
        
        root.setStyle("-fx-border-width: 1; -fx-border-color: green; -fx-background-color: transparent;");
        
        scene = new Scene(root, 200, 100);
        scene.setFill(null);
        stage.setScene(scene);
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
