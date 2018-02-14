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
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PalettePane;

/**
 *
 * @author Valery
 */
public class TestPalettePane01 extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Palette Stage");
        
        PalettePane palettePane = new PalettePane(true);
        Button btn1 = new Button("btn1");
        Button btn2 = new Button("btn2");
        btn1.setOnAction(a -> {
            if ( palettePane.getScrollPaneVbarPolicy() == ScrollPane.ScrollBarPolicy.NEVER) {
                palettePane.setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            } else {
                palettePane.setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
            btn1.setText("" + palettePane.getScrollPaneVbarPolicy());
        });
        btn2.setOnAction(a -> {
            if ( palettePane.dragNodeProperty().get() == null) {
                palettePane.setDragNode(new Button("Drag Button"));
                //palettePane.dragNodeProperty().set( new Button("Drag Button"));
            } else {
                palettePane.setDragNode(null);
                //palettePane.dragNodeProperty().set(null);
            }
            btn1.setText("" + palettePane.getScrollPaneVbarPolicy());
        });
        
        VBox root = new VBox(btn1,btn2,palettePane);
        palettePane.setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //stage.setWidth(350);
        stage.setAlwaysOnTop(true);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(350);
        stage.setOnShown(value -> {
            System.err.println("**************** SHOWN");
        });
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
