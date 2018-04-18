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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.event.DockEvent;

/**
 *
 * @author Valery
 */
public class TestCanvas extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("b2r");
        //b1.setGraphic(b2);
        Pane p1 = new HBox(b1);
        //dockPane.dock(p1, Side.TOP).getContext().setTitle("Pane p1");
        Scene primaryScene = new Scene(root);
        double cw = 100;
        double ch = 100;
        double sw = 10; //stroke width
        double swDelta = 10; //stroke width
        
        Canvas canvas = new Canvas(cw, ch);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.fillRect(0, 0, cw, ch);
        
        gc.clearRect(sw, sw, cw-2*sw, ch-2*sw);
        gc.setFill(Color.BLACK);
        // Top line
//        gc.fillRect(sw, sw, cw - 2*sw, sw + swDelta);
        // Right line
        //gc.fillRect(cw-2*sw - swDelta, sw, sw + swDelta,cw - 2*sw );
        // Bottom line
        //gc.fillRect(sw, ch-2*sw-swDelta, cw - 2*sw, sw + swDelta);
        // Left line
        gc.fillRect(sw,sw, sw + swDelta,ch - 2*sw );
        //gc.strokeRect(sw, sw, cw-2*sw, ch-2*sw);
        //gc.strokeLine(sw+1, sw+1, cw - 2*sw, sw+1);
        //gc.setStroke(Color.BLACK);
        //gc.strokeLine(sw+1, sw+2, cw-2*sw, sw+2);
        root.getChildren().addAll(b1,canvas);
        primaryStage.setTitle("JavaFX TestCanvas");
        primaryStage.setScene(primaryScene);

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
