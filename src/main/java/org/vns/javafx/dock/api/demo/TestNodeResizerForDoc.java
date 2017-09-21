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
import javafx.scene.layout.VBox;
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
public class TestNodeResizerForDoc extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox();
        
        //rootPane2.setStyle("-fx-background-color: green");
        root.setMinWidth(200);
        root.setMinHeight(100);
        //rootPane2.getChildren().add(custom1);
        Button btn = new Button("Button 1");
        root.getChildren().addAll(btn);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        NodeResizer nr = new NodeResizer(btn);
        nr.setWindowType(NodeResizer.WindowType.STAGE);
        btn.setTranslateX(50);
        nr.setApplyFtranslateXY(true);
        nr.setHideOnMouseRelease(true);
        nr.show();
        //PopupBasedNodeResizer pnr = new PopupBasedNodeResizer(btn);
        //pnr.show();
        //nr.start();
        
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
