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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MyCustomColorContextMenu extends Application {

    private final ObjectProperty<Color> sceneColorProperty =  new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {

        Rectangle rect = new Rectangle(400,400);
        rect.fillProperty().bind(sceneColorProperty);

        Scene scene = new Scene(new StackPane(rect), 400, 400);
        scene.getStylesheets().add(getClass().getResource("resources/color.css").toExternalForm());
        rect.setOnMouseClicked(e->{
            if(e.getButton().equals(MouseButton.SECONDARY)){
                MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
                myCustomColorPicker.setCurrentColor(sceneColorProperty.get());

                CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
                itemColor.setHideOnClick(false);
                sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
                ContextMenu contextMenu = new ContextMenu(itemColor);
                contextMenu.setOnHiding(t->sceneColorProperty.unbind());
                contextMenu.show(scene.getWindow(),e.getScreenX(),e.getScreenY());
            }
        });

        primaryStage.setTitle("Custom Color Selector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
