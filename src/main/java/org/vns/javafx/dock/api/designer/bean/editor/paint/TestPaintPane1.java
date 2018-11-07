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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestPaintPane1 extends Application {

    private final ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    double anchorValue = 0;

    @Override
    public void start(Stage primaryStage) {

        AnchorPane anchor = new AnchorPane();
        anchor.setStyle("-fx-background-color: yellow");
        anchor.setOnMouseClicked(e -> {
            ToggleButton b1 = new ToggleButton("B");
            anchor.getChildren().add(b1);
            anchorValue += 25;
            AnchorPane.setLeftAnchor(b1, e.getX() );
            
        });
        HBox hb = new HBox();
        GridPane.setHgrow(hb, Priority.ALWAYS);
        hb.getChildren().add(new Rectangle(100, 50));
        Rectangle r = new Rectangle(50, 50);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.add(hb, 0, 0, 1, 2);
        grid.add(r, 1, 0);
        grid.add(anchor, 0, 2, 2, 1);
        GridPane.setHgrow(anchor, Priority.ALWAYS);

        StackPane sp = new StackPane(grid);
        Button btn1 = new Button("btn1");
        btn1.setPadding(new Insets(15, 15, 15, 15));
        Button btn2 = new Button("btn2");
        //ColorChooserPane colorChooserPane = new ColorChooserPane(Color.BLUE);
        PaintPane paintPane = new PaintPane();
//        ColorPane paintPane = new ColorPane();
//        paintPane.chosenPaintProperty().addListener((v,ov,nv) -> {
//            System.err.println("chosenPaint = " + nv);
//        });
        VBox root = new VBox(sp,btn1, new ColorPicker(),paintPane);
        root.setStyle("-fx-background-color: aqua");
        root.setPadding(new Insets(10, 10, 10, 20));
        btn1.setOnAction(e -> {
        });
        //colorPane.getContent().setStyle("-fx-border-width: 1; -fx-border-color: lightgray");
        //colorPane.setStyle("-fx-padding: 10");
        Scene scene = new Scene(new StackPane(root), 400, 650);
        //Scene scene = new Scene(new StackPane(root));
        //scene.getStylesheets().add(getClass().getResource("resources/color.css").toExternalForm());

        ToggleButton b = new ToggleButton("B");
        anchor.getChildren().add(b);
        AnchorPane.setLeftAnchor(b, 0d);
        
        b.setOnAction(e -> {
            System.err.println("ACTION ");
            ToggleButton b1 = new ToggleButton("B");
            anchor.getChildren().add(b1);
            anchorValue += 25;
            AnchorPane.setLeftAnchor(b1, anchorValue );
            
        });
        ToggleButton b2 = new ToggleButton("B");
        anchor.getChildren().add(b2);
        AnchorPane.setRightAnchor(b2, 0d);

        primaryStage.setTitle("Custom Color Selector");
        primaryStage.setScene(scene);
        primaryStage.show();

        //AnchorPane.setLeftAnchor(b,anchor.getWidth() - b.getWidth());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
