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

import org.vns.javafx.scene.control.paint.PaintPane;
import org.vns.javafx.scene.control.PaintPicker;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestPaintPicker2 extends Application {

    private final ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.WHITE);

    @Override
    public void start(Stage primaryStage) {
        Color c = Color.TRANSPARENT;
        String s = c.toString();
        System.err.println("SSS = " + s);

        Button btn1 = new Button("btn1");
        btn1.setPadding(new Insets(15, 15, 15, 15));
        Button btn2 = new Button("btn2");

        //PaintPicker paintPicker = new PaintPicker();
        //paintPicker.setCurrentPaint(Color.GREEN);
        LinearGradient linear = createLinearGradient();
        LinearGradient linear1 = createLinearGradient1();
        RadialGradient radial = createRadialGradient();
        
//        PaintPicker paintPicker = new PaintPicker(PaintPane.Options.COLOR,PaintPane.Options.LINEAR_GRADIENT,PaintPane.Options.RADIAL_GRADIENT);
        //PaintPicker paintPicker = new PaintPicker(PaintPane.Options.COLOR);
        //PaintPicker paintPicker = new PaintPicker(PaintPane.Options.LINEAR_GRADIENT,PaintPane.Options.RADIAL_GRADIENT);
        PaintPicker paintPicker = new PaintPicker(PaintPane.Options.RADIAL_GRADIENT);
        
        Rectangle btn1Rect = new Rectangle(16,12);
        btn1.setGraphic(btn1Rect);
        paintPicker.valueProperty().addListener((v,ov,nv) -> {
            
            btn1Rect.setFill(nv);
            System.err.println("PaintPicker.valueProperty newValue = " + nv);
        });
        //Control paintPicker = new Control(radial);
    
        //StackPane sp = new StackPane(btn2);
        
        //sp.setStyle("-fx-background-color: yellow");
        //StackPane.setAlignment(paintPicker, Pos.CENTER_RIGHT);
//        ButtonEx btnEx = new ButtonEx();
        //paintPicker.setCurrentPaint(linear);
        //paintPicker.setCurrentPaint(createRadialGradient());
        VBox root = new VBox(btn1, new ColorPicker(), paintPicker);//paintPane);

        root.setPadding(new Insets(10, 10, 10, 20));
        btn1.setOnAction(e -> {
            System.err.println("btn1.action **** paint = " + paintPicker.getPaint());
            
            if ( paintPicker.getPaint() == Color.RED) {
                //paintPicker.setPaint(linear);
                paintPicker.setPaint(linear);
            } else if ( paintPicker.getPaint() == linear ) {
                System.err.println("========== btn1.action set =  linear1");
                paintPicker.setPaint(linear1);
            } else  if ( paintPicker.getPaint() == linear1 ) {
                System.err.println("btn1.action set = radial");
                paintPicker.setPaint(radial);
            } else  if ( paintPicker.getPaint() == radial ) {
                System.err.println("btn1.action set =  linear");
                //paintPicker.replaceCurrentPaint(Color.TRANSPARENT);
                paintPicker.setPaint(Color.RED);
            } else {
                paintPicker.setPaint(linear);
            }
/*
            if ( paintPicker.getValue() instanceof Color) {
                System.err.println("11");
                //paintPicker.setPaint(linear);
                paintPicker.setValue(linear);
            } else if ( paintPicker.getValue() instanceof LinearGradient ) {
                System.err.println("33");
                System.err.println("btn1.action set = radial");
                paintPicker.setValue(radial);
            } else  if ( paintPicker.getValue() instanceof RadialGradient ) {
                System.err.println("btn1.action set =  linear");
                System.err.println("44");
                //paintPicker.replaceCurrentPaint(Color.TRANSPARENT);
                paintPicker.setValue(Color.RED);
            } else {
                System.err.println("55");
                paintPicker.setValue(linear);
            }
*/
        });

        //colorPane.getContent().setStyle("-fx-border-width: 1; -fx-border-color: lightgray");
        //colorPane.setStyle("-fx-padding: 10");
        Scene scene = new Scene(new StackPane(root), 400, 650);
        //Scene scene = new Scene(new StackPane(root));
        //scene.getStylesheets().add(getClass().getResource("resources/color.css").toExternalForm());

        primaryStage.setTitle("Custom Color Selector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static LinearGradient createLinearGradient() {
            return new LinearGradient(0,0,
                    1,1,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0d,Color.YELLOWGREEN),
                    new Stop(0.2d,Color.AQUA),
                    new Stop(0.5d,Color.RED),
                    new Stop(1d,Color.WHITE));
                            
    }
    public static LinearGradient createLinearGradient1() {
            return new LinearGradient(0.3,0.3,
                    1,1,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0d,Color.BISQUE),
                    new Stop(0.35d,Color.GREEN),
                    new Stop(0.6d,Color.YELLOW),
                    new Stop(1d,Color.BLUE));
    }
    public static RadialGradient createRadialGradient() {
            return new RadialGradient(0,0,
                    0.5,0.5,
                    0.5,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0d,Color.YELLOWGREEN),
                    new Stop(0.2d,Color.AQUA),
                    new Stop(0.5d,Color.RED),
                    new Stop(1d,Color.WHITE));
                            
    }
    
}
