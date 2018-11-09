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

import org.vns.javafx.scene.control.paint.StopChangeEvent;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.scene.control.paint.StopChangeEvent.CurrentStopChangeEvent;
import org.vns.javafx.scene.control.paint.StopChangeEvent.StopChangeEventHandler;

public class TestStopEvent1 extends Application {

    private final ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {

        Button btn1 = new Button("btn1");
        btn1.setPadding(new Insets(15, 15, 15, 15));
        Button btn2 = new Button("btn2");

        Button btn = new Button("Say 'Hello World'");
        btn.setOnAction((ActionEvent event) -> {
            btn.fireEvent(new CurrentStopChangeEvent(42));
            //btn.fireEvent(new CustomEvent2("Hello World"));
        });

        btn.addEventHandler(StopChangeEvent.STOP_CHANGE, new StopChangeEventHandler() {

            @Override
            public void onEvent(int param0) {
                System.out.println("integer parameter: " + param0);
            }

        });
        VBox root = new VBox(btn, btn2, btn1);//, stopPane);

        root.setPadding(new Insets(10, 10, 10, 20));

        //     System.err.println("BTN1 EVENT HANDLER");
        // });
//        btn1.addEventHandler(StopChangeEvent.STOP_EVENT, new StopChangeEventHandler() {});
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
