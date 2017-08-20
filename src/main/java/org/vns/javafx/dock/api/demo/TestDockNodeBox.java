/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.DockNodeBox;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockNodeBox extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DockPane dockPane = new DockPane();
        dockPane.setId("DOCK PANE");
        Button b1 = new Button("b01");
        Pane p1 = new HBox(b1);
        
        p1.setId("pane p1");
        //dockPane.dock(p1, Side.TOP).getDockableContext().setTitle("Pane p1");
        
        Button b2 = new Button("b02");
        Pane p2 = new HBox(b2);
        //dockPane.dock(p2, Side.RIGHT).getDockableContext().setTitle("Pane p2");;
        p2.setId("pane p2");
        
        Button b3 = new Button("b03");
        Pane p3 = new HBox(b3);
        //dockPane.dock(p3, Side.BOTTOM).getDockableContext().setTitle("Pane p3");;
        p3.setId("pane p3");        
        
        DockNodeBox dn01 = new DockNodeBox();
        dn01.setTitle("DockNode dn01");
        dn01.setId("dockNode dn01");     
        //dn01.setStyle("-fx-background-color:red");
        dn01.getStyleClass().add("delegate");
        dn01.getChildren().add(new Label("DOCK NODE IMPL"));
        //dockPane.getItems().add(dn01);
        dockPane.dock(dn01, Side.TOP);
        
        Scene scene = new Scene(dockPane);
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        

        stage.show();
        

        
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
