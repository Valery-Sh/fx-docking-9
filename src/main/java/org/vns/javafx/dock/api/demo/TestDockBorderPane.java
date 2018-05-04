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
import javafx.scene.control.SplitPane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockBorderPane extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        //
        // Create stage witch contains a dockable button
        //
        stage.setTitle("Stage with a DockableButton");
        //VBox rootPane = new VBox();
        SplitPane rootPane = new SplitPane();
        //Pane rootPane = new Pane();
        scene = new Scene(rootPane, 200, 200);

        Button dockButton = new Button("To be docked");
        Dockable dockableButton
                = DockRegistry.getInstance().getDefaultDockable(dockButton);
        dockableButton.getContext().setDragNode(dockButton);
        //rootPane.getChildren().addAll(dockButton);
        rootPane.getItems().addAll(dockButton);
        dockButton.setOnAction(a -> {
            Window w = dockButton.getScene().getWindow();
            Window owner = null;
            if ( w instanceof Stage  ) {
                owner = ((Stage)w).getOwner();
            } else if ( w instanceof PopupWindow ) {
                owner = ((PopupWindow)w).getOwnerWindow();
            }
            System.err.println("OWNER: " + owner);
        });
        //
        // Create Stage with a BorderPane as DockTarget
        //
        Stage stage1 = new Stage();
        DockBorderPane borderPane = new DockBorderPane();
        borderPane.setStyle("-fx-border-width: 2px; -fx-border-color: red");
        Button topNode = new Button("Initial Top");
        borderPane.setTop(topNode);
        topNode.setOnAction(a -> {
            //NodeDescriptorRegistry.getInstance().getDescriptor(rootPane);
            System.err.println("CENTER  = " + borderPane.getBottom().getParent());
            System.err.println("CENTER0  = " + borderPane.getChildren().size());
            //((BorderPane)borderPane.getBottom().getParent()).getChildren().remove(borderPane.getBottom());
            borderPane.setBottom(null);
            System.err.println("CENTER1  = " + borderPane.getBottom());            
            System.err.println("CENTER2  = " + borderPane.getChildren().size());
        });
        //
        // Create & Register an Object witch declares a Given BorderPane 
        // as a DockTarget
        //
        //DockBorderPane borderPaneTarget = new DockBorderPane();
        //DockRegistry.getInstance().register(borderPaneTarget);

        stage1.setTitle("Stage with a BorderPane as DockTarget");

        stage1.setX(100);
        stage1.setY(100);

        //StackPane rootPane1 = new StackPane(borderPane);
        //Scene scene1 = new Scene(rootPane1, 200, 200);
        Scene scene1 = new Scene(borderPane, 200, 200);
        stage1.setScene(scene1);

        stage.setScene(scene);
        stage.show();
        stage1.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        //Dockable.initDefaultStylesheet(null);

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

}
