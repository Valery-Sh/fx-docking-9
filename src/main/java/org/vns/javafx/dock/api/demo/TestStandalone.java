/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class TestStandalone extends Application {
 
    Stage stage;
    Scene scene;

    @Override 
    public void start(Stage stage) {
        stage.show();
        scene = new Scene(new VBox(), 840, 680);
        VBox vb = (VBox) scene.getRoot();
        Button execBtn = new Button("start");
        vb.getChildren().add(execBtn);
        stage.setScene(scene);
        execBtn.setOnAction(a -> {
            TestStandaloneNext.main(new String[0]);
        });
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}