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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestRegistryDock extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        VBox root = new VBox();
        scene = new Scene(root, 200, 200);
        Button btn1 = new Button("set float");
        Button btn2 = new Button("dn1 set float");
        root.getChildren().addAll(btn1,btn2);
        Stage stage1 = new Stage();
        HBox pn01 = new HBox();
        Button btn2_1 = new Button("Button: btn2_1");
        pn01.getChildren().add(btn2_1);
        
        DockNode dn1 = new DockNode();
        dn1.setTitle("New DockNode dn1");
        dn1.getChildren().add( new Label("Valery"));
        pn01.getChildren().add(dn1);
        
        
        Scene scene1 = new Scene(pn01, 100,100);
        stage1.setScene(scene1);

        stage.setScene(scene);
        stage.show();
        stage1.show();
        
        // Next Stage
        
        HBox sp2 = new HBox();
        Scene sc02 = new Scene(sp2);
        
        /////////////////////////////////////
        /////////////////////////////////////
        
        btn1.setOnAction(a->{
            Dockable d = DockRegistry.getInstance().getDefaultDockable(btn2_1);
            d.nodeHandler().setDragNode(d.node());
            d.nodeHandler().setFloating(true);
        });
        btn2.setOnAction(a->{
            
            //dn1.nodeHandler().dockedProperty().set(true);
            dn1.nodeHandler().setFloating(true);
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

}
