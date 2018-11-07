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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.designer.DesignerLookup;

public class TestStopPane1 extends Application {

    private final ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Rectangle rect = new Rectangle(75,30);
        
        Label lb1 = new Label(" DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD ");
        AnchorPane anchor = new AnchorPane();
        Label txt = new Label("Click to add stop");
        //txt.getStyleClass().clear();
        System.err.println("txt styles = " + txt.getStyleClass());
        txt.getStylesheets().add(DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm());
        //txt.setStyle("-fx-background-color: yellow");
        txt.getStyleClass().add("title");
        
        anchor.getChildren().add(txt);
        AnchorPane.setLeftAnchor(txt, 0d);
        AnchorPane.setRightAnchor(txt, 0d);
        AnchorPane.setTopAnchor(txt, 0d);
        AnchorPane.setBottomAnchor(txt, 0d);
        txt.setAlignment(Pos.CENTER);
        //txt.setMaxWidth(Double.MAX_VALUE);
        
        Button btn1 = new Button("btn1");
        btn1.setPadding(new Insets(15, 15, 15, 15));
        Button btn2 = new Button("btn2");
        ColorChooserPane colorChooserPane = new ColorChooserPane(Color.BLUE);
        StopPane stopPane = new StopPane(colorChooserPane);
        GridPane grid = colorChooserPane.getContent();
        grid.add(stopPane, 0, 5, 2, 1);
        stopPane.stopsProperty().addListener((v,ov,nv) -> {
            Paint p = new LinearGradient(0,0,1,1, true, CycleMethod.REFLECT, nv);
            rect.setFill(p);
        });
        Polygon pol = StopPane.createUpTriangle();
        VBox root = new VBox(rect,lb1, anchor,btn2, btn1, colorChooserPane);//, stopPane);
        
        root.setPadding(new Insets(10, 10, 10, 20));
        btn1.setOnAction(e -> {
            System.err.println("pol. = " + pol.getBoundsInLocal().getWidth() + "; h = " + pol.getBoundsInLocal().getHeight());
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
