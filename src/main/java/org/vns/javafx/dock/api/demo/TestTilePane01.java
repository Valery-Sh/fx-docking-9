package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;


public class TestTilePane01 extends Application  {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("TilePane Experiment");

        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button Number 2");
        Button button3 = new Button("Button No 3");
        Button button4 = new Button("Button No 4");
        Button button5 = new Button("Button 5");
        Button button6 = new Button("Button Number 6");
        button1.minWidthProperty().bind(button2.widthProperty());
        button3.minWidthProperty().bind(button2.widthProperty());
        button4.minWidthProperty().bind(button2.widthProperty());        
        button5.minWidthProperty().bind(button2.widthProperty());
        button6.minWidthProperty().bind(button2.widthProperty());
        
        TilePane tilePane = new TilePane();
        tilePane.setStyle("-fx-border-color:red");
        //tilePane.setPrefColumns(4);
        tilePane.setHgap(8);        
        tilePane.getChildren().add(button1);
        tilePane.getChildren().add(button2);
        tilePane.getChildren().add(button3);
        tilePane.getChildren().add(button4);
        tilePane.getChildren().add(button5);
        tilePane.getChildren().add(button6);

        tilePane.setTileAlignment(Pos.TOP_LEFT);
        //tilePane.setOrientation(Orientation.VERTICAL);
        StackPane sp = new StackPane(tilePane);
        Scene scene = new Scene(sp, 800, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
