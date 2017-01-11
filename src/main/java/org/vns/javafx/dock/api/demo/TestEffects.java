/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.plaf.synth.SynthLookAndFeel;

/**
 *
 * @author Valery
 */
public class TestEffects extends Application {
 
    Stage stage;
    Scene scene;

    @Override 
    public void start(Stage stage) {
        stage.show();
        scene = new Scene(new VBox(), 840, 680);
        
        ObservableList<Node> content = ((VBox)scene.getRoot()).getChildren();
        
        content.add(dropShadow());        
        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");
        stage.setScene(scene);
    }

    static Node dropShadow() {
        VBox g = new VBox();
        DropShadow ds = new DropShadow();
        ds.setOffsetY(1.0);
        ds.setOffsetX(1.0);
        ds.setColor(Color.GRAY);        
       
 
        Text t = new Text();
        t.setEffect(ds);
        t.setCache(true);
        t.setX(20.0f);
        t.setY(70.0f);
        t.setFill(Color.RED);
        t.setText("JavaFX drop shadow effect");
        t.setFont(Font.font("null", FontWeight.BOLD, 32));
 
        DropShadow ds1 = new DropShadow();
        ds1.setOffsetY(4.0f);
        ds1.setOffsetX(4.0f);
        ds1.setColor(Color.CORAL);
 
        Circle c = new Circle();
        c.setEffect(ds);
        c.setCenterX(50.0f);
        c.setCenterY(325.0f);
        //c.setRadius(30.0f);
        c.setRadius(3.0f);
        c.setFill(Color.WHITE);
        c.setCache(true);
 
        g.getChildren().add(t);
        g.getChildren().add(c);
        return g;
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}