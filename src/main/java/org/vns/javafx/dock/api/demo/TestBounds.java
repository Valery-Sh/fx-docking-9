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
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestBounds extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("Button b2");
        
        Button spBtn1 = new Button("StackPane spBtn1");
        Button spBtn2 = new Button("--- StackPane spBtn1 ---");
        
        StackPane stackPane = new StackPane(spBtn1,spBtn2);
        //dockPane.dock(p1, Side.TOP).getContext().setTitle("Pane p1");
        Scene scene = new Scene(root);
     
        root.getChildren().addAll(b1, b2, stackPane);
        
        primaryStage.setTitle("JavaFX TestCanvas");
        primaryStage.setScene(scene);
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            System.err.println("conteins(spBtn1) = " + DockUtil.contains(spBtn1, e.getScreenX(), e.getScreenY()));
            System.err.println("conteins(spBtn2) = " + DockUtil.contains(spBtn2, e.getScreenX(), e.getScreenY()));
            
        });
        b1.setOnAction(e -> {
            System.err.println("boundsInLocal spBtn2.width = " + spBtn2.getBoundsInLocal().getWidth());
            System.err.println("boundsInLocal spBtn2.height = " + spBtn2.getBoundsInLocal().getHeight());
            Bounds bnds = spBtn2.localToScreen(spBtn2.getBoundsInLocal());
            System.err.println("screenBounds spBtn2.width = " + bnds.getWidth());
            System.err.println("boundsInLocal spBtn2.X = " + spBtn2.getBoundsInLocal().getMinX());
            System.err.println("boundsInLocal spBtn2.Y = " + spBtn2.getBoundsInLocal().getMinY());
            
            System.err.println("screenBounds spBtn2.X = " + bnds.getMinX());
            System.err.println("screenBounds spBtn2.Y = " + bnds.getMinY());
            
        });
        spBtn2.setScaleX(0.5);
        spBtn2.setScaleY(0.5);
        
        primaryStage.setOnShown(s -> {

        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
       
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
