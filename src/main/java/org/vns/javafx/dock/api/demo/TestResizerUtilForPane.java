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
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.view.NodeResizer;

/**
 *
 * @author Valery
 */
public class TestResizerUtilForPane extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DockPane rootPane = new DockPane();
        Button b1 = new Button("Add or Remove TitleBar");
        Button b2 = new Button("b2r");
        //b1.setGraphic(b2);
        Pane p1 = new HBox(b1);
        DockNode custom = new DockNode();
        rootPane.dockNode(custom, Side.TOP);

        DockNode custom1 = new DockNode();
        custom1.setTitle("CUSTOM 1");
        custom1.setId("custom1");
        custom.setContent(p1);

        StackPane sp = new StackPane();
        sp.setStyle("-fx-border-width:5; -fx-border-color:red;-fx-background-color: transparent");
        //sp.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        Scene scene = new Scene(sp);
        scene.setFill(null);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);

        stage.setOnShown(s -> {
            DockUtil.print(b1);
        });
        stage.setAlwaysOnTop(true);
        stage.setWidth(200);
        stage.setHeight(100);
        stage.show();

        Pane rootPane2 = new Pane();
        rootPane2.setPadding(new Insets(10d, 10d, 10d, 10d));
        rootPane2.setStyle("-fx-background-color: green");
        custom1.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: yellow");
        rootPane2.setMinWidth(200);
        rootPane2.setMinHeight(100);
        rootPane2.getChildren().add(custom1);
        custom1.setMinHeight(50);
        Stage stage2 = new Stage();
        //stage2.initStyle(StageStyle.TRANSPARENT);
        Scene scene2 = new Scene(rootPane2);
        stage2.setScene(scene2);
        stage2.setY(stage.getY() + stage.getHeight() + 20);
        //stage2.setWidth(200);
        //stage2.setHeight(100);
        //stage2.sizeToScene();
        //custom1.setTitleBar(null);
        stage2.setAlwaysOnTop(true);
        stage2.show();

        NodeResizer nr = new NodeResizer(custom1);
        //custom1.setTranslateX(50);
        nr.show();
        
        //ResizeUtil.start(custom1, false);

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
