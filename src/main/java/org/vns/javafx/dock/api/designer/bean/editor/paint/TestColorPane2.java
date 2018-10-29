/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestColorPane2 extends Application {

    private final ObjectProperty<Color> sceneColorProperty =  new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("btn1");
        Button btn2 = new Button("btn2");
        ColorPane colorPane = new ColorPane();
        btn2.textFillProperty().bind(colorPane.chosenColorProperty());
        colorPane.setPrefSize(200, 200);
        HueBar hueBar = new HueBar(colorPane);
       // hueBar.setOrientation(Orientation.VERTICAL );
        HBox hbox = new HBox(hueBar,new StackPane(colorPane));
        hbox.setSpacing(10);
        VBox root = new VBox(btn2,btn1,hbox);
        root.setPadding(new Insets(10,10,10,10));
        //VBox root = new VBox(btn2,btn1,hueBar,new StackPane(colorPane));
        
        btn1.setOnAction(e -> {
            System.err.println("1) pane.width = " + colorPane.getWidth() + "; height=" + colorPane.getHeight());
            System.err.println("2) child.width = " + colorPane.getContent().getWidth() + "; height=" + colorPane.getContent().getHeight());
            Pane p = (Pane) colorPane.getContent().getChildren().get(0);
            System.err.println("3) child.width = " + p.getWidth() + "; height=" + p.getHeight());
            System.err.println("1) hueBar.width = " + hueBar.getWidth() + "; height=" + hueBar.getHeight());
            System.err.println("1) hueBar.content.width = " + hueBar.getContent().getWidth() + "; height=" + hueBar.getContent().getHeight());
            
            System.err.println("hue.prefwidth = " + ((Region)hueBar.getContent()).getPrefWidth());
            System.err.println("hue.prefheight = " + ((Region)hueBar.getContent()).getPrefHeight());
            
            System.err.println("hue.minwidth = " + ((Region)hueBar.getContent()).getMinWidth());
            System.err.println("hue.minheight = " + ((Region)hueBar.getContent()).getMinHeight());
            
            System.err.println("hue.maxwidth = " + ((Region)hueBar.getContent()).getMaxWidth());
            System.err.println("hue.maxheight = " + ((Region)hueBar.getContent()).getMaxHeight());

            System.err.println("hue.width = " + ((Region)hueBar.getContent()).getWidth());
            System.err.println("hue.height = " + ((Region)hueBar.getContent()).getHeight());            
            System.err.println("hue.bound.width = " + ((Region)hueBar.getContent()).getBoundsInLocal().getWidth());
            System.err.println("hue.bound.height = " + ((Region)hueBar.getContent()).getBoundsInLocal().getHeight());            
            
            System.err.println("hue.parentbound.width = " + ((Region)hueBar.getContent()).getBoundsInParent().getWidth());
            System.err.println("hue.parentbound.height = " + ((Region)hueBar.getContent()).getBoundsInParent().getHeight());            
            System.err.println("hue.getContent.getParent.insets = " + ((Group)hueBar.getContent().getParent()).getBoundsInParent());
            System.err.println("hue.getContent.getParent = " + ((Region)hueBar.getContent()).getParent());
            
        });
        //colorPane.getContent().setStyle("-fx-border-width: 1; -fx-border-color: lightgray");
        //colorPane.setStyle("-fx-padding: 10");
        Scene scene = new Scene(new StackPane(root), 400, 400);
        //scene.getStylesheets().add(getClass().getResource("resources/color.css").toExternalForm());

        primaryStage.setTitle("Custom Color Selector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
