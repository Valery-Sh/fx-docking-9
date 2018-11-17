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
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.view.FramePane;
import org.vns.javafx.dock.api.dragging.view.ResizeShape;
import org.vns.javafx.dock.api.dragging.view.StageNodeFraming;
import org.vns.javafx.dock.api.dragging.view.WindowNodeFraming;

/**
 *
 * @author Valery
 */
public class TestFrameRectangle extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: aqua");
        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("Button b2");
        Button b3 = new Button("Button b3");
        Button b4 = new Button("Set vivisible");
        
        //b1.setGraphic(b2);
        Pane p1 = new HBox(b1);
        //dockPane.dock(p1, Side.TOP).getContext().setTitle("Pane p1");
        Scene primaryScene = new Scene(root, 25, 300);
        Rectangle rect0 = new Rectangle(30, 30);
        rect0.setLayoutX(0);
        rect0.setLayoutY(0);
        //FrameRectangle rect = new FrameRectangle(Rectangle.class,true);
        FramePane rect = new FramePane();
        DockRegistry.getInstance().getLookup().putUnique(WindowNodeFraming.class, StageNodeFraming.getInstance() );        
        //FrameRectangle rect = new FrameRectangle(Rectangle.class);
        //ResizeShape rect = new ResizeShape(Circle.class);
        //rect.setPrefWidth(20);
        //rect.setCenterX(100);
        //rect.setCenterY(0);
        //rect.setStyle("-fx-pref-width: 30; -fx-pref-height: 30");
        //Rectangle rect = new Rectangle();
        //rect.setStyle("-fx-background-color: green");
/*        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.RED);
        rect.setStrokeWidth(2);
         */
        Pane pane = new Pane();
                
        pane.setPrefSize(200, 70);
        pane.setLayoutX(20);
        pane.setLayoutY(80);
        pane.setMinWidth(200);
        //pane.setMinHeight(70);
                
        pane.setMaxWidth(200);
        pane.setMaxHeight(70);
        
        pane.setLayoutY(150);
        pane.setStyle("-fx-background-color: yellow");
        HBox hbox1 = new HBox(new Label("Label1"), b1);
        VBox hbox = new VBox(hbox1);
        hbox.setStyle("-fx-background-color: grey; -fx-padding: 10 10 10 10");                
        
        
        root.getChildren().addAll(hbox, b2,  b3,b4,rect);
        rect.setOnMouseClicked(e -> {
            System.err.println("mouse clicked");
        });
        primaryStage.setTitle("JavaFX TestCanvas");
        primaryStage.setScene(primaryScene);

        primaryStage.setOnShown(s -> {

        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        b1.setOnAction(e -> {
            //rect.setX(rect.getX() + 50);
            System.err.println("App rect.bounds = " + rect.getBoundsInParent());
//            System.err.println("pane.parent = " + pane.getParent());
/*            System.err.println("1 rect.layoutX = " + rect.getLayoutX());
            System.err.println("rect.width = " + rect.getWidth());
            System.err.println("rect.bounds = " + rect.getBoundsInLocal());
            System.err.println("pane.layX = " + pane.getLayoutX());
            System.err.println("pane.insets = " + pane.getInsets());
*/            
           // rect.setCenterX(rect.getCenterX() + 50);
            //rect.toFront();
            rect.setBoundNode(b2);
            b1.setScaleX(1);
            
//            System.err.println("2 rect.layottX = " + rect.getLayoutX());
        });
        b2.setOnAction(e -> {
            System.err.println("b2 pressed");
            rect.setBoundNode(b3);
        });
        b3.setOnAction(e -> {
            System.err.println("b3 pressed");
            b1.setScaleX(0.5);
            rect.setBoundNode(b1);
        });
        b4.setOnAction(e -> {
            if ( rect.isVisible() ) {
                rect.setVisible(false);
            } else {
                rect.setVisible(true);
            }
        });        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
