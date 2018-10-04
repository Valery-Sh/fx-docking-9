package org.vns.javafx.dock.api.demo;

import javafx.application.Application;  
import javafx.event.ActionEvent;  
import javafx.event.EventHandler;  
import javafx.scene.Scene;  
import javafx.scene.control.Button;  
import javafx.scene.control.TextField;  
import javafx.scene.layout.HBox;  
import javafx.stage.Stage;  
  
public class OpenDefaultBrowser extends Application {  
  
    @Override  
    public void start(Stage primaryStage) {  
        final HBox root = new HBox(5);  
        final TextField textField = new TextField("https://community.oracle.com/thread/3514307");  
        final Button goButton = new Button("Go");  
  
        EventHandler<ActionEvent> goHandler = new EventHandler<ActionEvent>() {  
  
            @Override  
            public void handle(ActionEvent event) {  
                getHostServices().showDocument(textField.getText());  
            }  
  
        };  
  
        textField.setOnAction(goHandler);  
        goButton.setOnAction(goHandler);  
  
        root.getChildren().addAll(textField, goButton);  
        final Scene scene = new Scene(root, 250, 150);  
        primaryStage.setScene(scene);  
        primaryStage.show();  
    }  
  
    public static void main(String[] args) {  
        launch(args);  
    }  
}  