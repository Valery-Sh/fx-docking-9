package org.vns.javafx.dock.api.demo;

/**
 *
 * @author Valery
 */


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;


import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

public class TestSubscene extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Popup Example");
        final PopupControl popup = new PopupControl();
        popup.setX(300);
        popup.setY(200);
        Pane root = new Pane();
        root.getChildren().addAll(new Circle(25, 25, 50, Color.AQUAMARINE));
        popup.getScene().setRoot(root);
        Button show = new Button("Show");
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.show(primaryStage);
            }
        });

        Button hide = new Button("Hide");
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.hide();
            }
        });

        HBox layout = new HBox(10);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
        layout.getChildren().addAll(show, hide);
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        
    }
}
