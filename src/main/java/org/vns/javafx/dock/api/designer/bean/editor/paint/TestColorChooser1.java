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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestColorChooser1 extends Application {

    private final ObjectProperty<Color> sceneColorProperty =  new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("btn1");
        btn1.setPadding(new Insets(15,15,15,15));
        Button btn2 = new Button("btn2");
        ColorChooserPane colorChooser = new ColorChooserPane(Color.TRANSPARENT);
        btn2.textFillProperty().bind(colorChooser.getColorPane().chosenColorProperty());
        //colorChooser.getColorPane().setPrefSize(150, 100);
        
        VBox root = new VBox(btn2,btn1,new StackPane(colorChooser));
        root.setPadding(new Insets(10,10,10,20));
        btn1.setOnMousePressed(e -> {
            System.err.println("MOUSE PRESSED: w = " + btn1.getWidth() + "; layout = " + btn1.getBoundsInParent().getWidth());
            ColorPane cp = colorChooser.getColorPane();
            Pane cpContent = cp.getContent();
            HueBar hb = colorChooser.getHueBar();
            Pane hbContent = hb.getContent();
            System.err.println("   hueBar.content.width = " + hbContent.getWidth() + "; bound.w = " + hbContent.getBoundsInParent().getWidth());
            System.err.println("colorPane.content.width = " + cpContent.getWidth() + "; bound.w = " + cpContent.getBoundsInParent().getWidth());
            
        });
        btn1.setOnAction(e -> {
            System.err.println("WWWW = " + colorChooser.getColorPane().getWidth() );
            double h1 = colorChooser.getColorPane().getCurrentColor().getHue();
            double h2 = ((Color)colorChooser.getColorPane().getChosenColor()).getHue();
            System.err.println("h1 = " + h1 + "; h2 = " + h2);
            System.err.println("colorPane.getHue = " + colorChooser.getColorPane().getHue() ) ;
            //colorChooser.getColorPane().getChosenColor().getHue();
            //Color c = Color.RED;
            //System.err.println("c toString = " + c.toString());
/*            System.err.println("1) pane.width = " + colorPane.getWidth() + "; height=" + colorPane.getHeight());
            System.err.println("2) child.width = " + colorPane.getContent().getWidth() + "; height=" + colorPane.getContent().getHeight());
            Pane p = (Pane) colorPane.getContent().getChildren().get(0);
            System.err.println("3) child.width = " + p.getWidth() + "; height=" + p.getHeight());
            System.err.println("1) hueBar.width = " + hueBar.getWidth() + "; height=" + hueBar.getHeight());
            System.err.println("1) hueBar.content.width = " + hueBar.getContent().getWidth() + "; height=" + hueBar.getContent().getHeight());
            
            System.err.println("hue.width = " + ((Region)hueBar.getContent().getChildren().get(0)).getPrefWidth());
            System.err.println("hue.height = " + ((Region)hueBar.getContent().getChildren().get(0)).getPrefHeight());
            System.err.println("hue.height = " + ((Region)hueBar.getContent().getChildren().get(0)).getBoundsInParent().getMinX());
            System.err.println("hue.height = " + ((Region)hueBar.getContent().getChildren().get(0)).getBoundsInParent().getMinY());            
*/            
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
