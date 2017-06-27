/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DemoDockablePane extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        VBox root = new VBox();
        scene = new Scene(root, 200, 200);
        Button dockButton = new Button("To be docked 1");
        StackPane dockablePane = new StackPane(dockButton);
                
        Dockable dockableButton = DockRegistry.getInstance().getDefaultDockable(dockablePane);
        dockableButton.dockableController().setDragNode(dockButton);
        
        Button dockButton1 = new Button("To be docked 2");        
        StackPane dockablePane1 = new StackPane(dockButton1);
        
        Dockable dockableButton1 = DockRegistry.getInstance().getDefaultDockable(dockablePane1);
        dockableButton1.dockableController().setDragNode(dockButton1);
        
        root.getChildren().addAll(dockablePane, dockablePane1);

        Stage stage1 = new Stage();
        DockPane dockPane = new DockPane();
        dockPane.setStyle("-fx-border-width: 2px; -fx-border-color: red");
        stage1.setX(100);
        stage1.setY(100);
        
        StackPane rootPane = new StackPane(dockPane);
        Scene scene1 = new Scene(rootPane, 200, 200);
        stage1.setScene(scene1);

        stage.setScene(scene);
        stage.show();
        stage1.show();

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

}
