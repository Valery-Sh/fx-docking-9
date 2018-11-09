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
package org.vns.javafx.scene;

import org.vns.javafx.scene.control.paint.LinearGradientPane;
import org.vns.javafx.scene.control.paint.RadialGradientPane;
import org.vns.javafx.scene.control.paint.ColorChooserPane;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestLinearGradientPane1 extends Application {

    private final ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("btn1");
        Label l1 = new Label();
        Slider sl = new Slider();
        TextField tf = new TextField();
        ChoiceBox cb = new ChoiceBox();
        ComboBox cob = new ComboBox();
        GridPane gp = new GridPane();
        System.err.println("label " + l1.getStyleClass());
        System.err.println("Slider " + sl.getStyleClass());
        System.err.println("TextField " + tf.getStyleClass());
        System.err.println("ChoiceBox " + cb.getStyleClass());
        System.err.println("ComboBox " + cob.getStyleClass());
        System.err.println("GridPane " + gp.getStyleClass());
        
        
        btn1.setPadding(new Insets(15, 15, 15, 15));
        Button btn2 = new Button("btn2");
        ColorChooserPane colorChooserPane = new ColorChooserPane(Color.BLUE);
        ColorChooserPane colorChooserPane1 = new ColorChooserPane(Color.BLUE);
        ColorChooserPane colorChooserPane2 = new ColorChooserPane(Color.BLUE);
        LinearGradientPane lgPane = new LinearGradientPane();
        RadialGradientPane rgPane = new RadialGradientPane();
        VBox root = new VBox(btn1, btn2, colorChooserPane);
        
        root.setPadding(new Insets(10, 10, 10, 20));
        btn1.setOnAction(e -> {
            root.getChildren().remove(colorChooserPane);
            root.getChildren().add(lgPane);
        });
        //colorPane.getContent().setStyle("-fx-border-width: 1; -fx-border-color: lightgray");
        //colorPane.setStyle("-fx-padding: 10");
        Scene scene = new Scene(new StackPane(root), 400, 600);
        //scene.getStylesheets().add(getClass().getResource("resources/color.css").toExternalForm());

        primaryStage.setTitle("Custom Color Selector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
