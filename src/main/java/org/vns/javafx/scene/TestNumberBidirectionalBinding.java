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

import org.vns.javafx.scene.control.paint.binding.NumberBidirectionalBinding;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestNumberBidirectionalBinding extends Application {

    private final ObjectProperty<Color> sceneColorProperty =  new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button("btn1");
        //btn1.setPadding(new Insets(15,15,15,15));
        Button btn2 = new Button("btn2");
        DoubleProperty btn1Width = new SimpleDoubleProperty(0);
        NumberBidirectionalBinding dbb = new NumberBidirectionalBinding(btn1Width,btn1.prefWidthProperty());
        dbb.bindBidirectional( (v1,v2) -> {
            Double retval = v2;
            if ( v1 == 1 ) {
                retval = v2 * 2;
            } else {
                retval = v2 / 2;
            }
           return retval;
        });
        VBox root = new VBox(btn2,btn1);
        root.setPadding(new Insets(10,10,10,20));
/*        btn1.setOnMousePressed(e -> {
            
            System.err.println("MOUSE PRESSED: w = " + btn1.getWidth() + "; layout = " + btn1.getBoundsInParent().getWidth());
            //ColorPane cp = colorChooser.getColorPane();
            //Pane cpContent = cp.getContent();
            //HueBar hb = colorChooser.getHueBar();
            //Pane hbContent = hb.getContent();
            //System.err.println("   hueBar.content.width = " + hbContent.getWidth() + "; bound.w = " + hbContent.getBoundsInParent().getWidth());
            //System.err.println("colorPane.content.width = " + cpContent.getWidth() + "; bound.w = " + cpContent.getBoundsInParent().getWidth());
            
        });
*/
        btn1.setOnAction(e -> {
            System.err.println("1. btn1.prefwidth = " + btn1.getPrefWidth());
            btn1Width.set(70);
/*            if ( btn1.getPrefWidth() == 100) {
                btn1.setPrefWidth(75);
            } else {
                btn1.setPrefWidth(btn1.getPrefWidth() + 10);
            }
*/
            System.err.println("btn1Width = " + btn1Width.get());
            System.err.println("2. btn1.prefwidth = " + btn1.getPrefWidth());
        });
        btn2.setOnAction(e -> {     
            System.err.println("1. btn1Width = " + btn1Width.get());
            System.err.println("2. btn1.prefwidth = " + btn1.getPrefWidth());
            //btn1Width.set(btn1Width.get() + 10);
            btn1.setPrefWidth(49);
            System.err.println("3. btn1Width = " + btn1Width.get());
            System.err.println("4. btn1.prefwidth = " + btn1.getPrefWidth());
            
        });
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
