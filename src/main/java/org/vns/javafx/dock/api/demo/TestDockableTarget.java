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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockPaneBase;
import org.vns.javafx.dock.TitledToolBar;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockableTarget extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DockPaneBase dockPane = new DockPaneBase();
        dockPane.setId("MAIN DOCK PANE");
        Button b1 = new Button("b01 - DOCK");
        b1.setOnAction(a -> { 
            //new DragPopup(dockPane);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
        TitledToolBar dtt01 = new TitledToolBar();
        dtt01.setId("ddt01");
        Label lb = new Label("id = dtt01");
        dtt01.getChildren().add(lb);
        
        dockPane.dock(dtt01, Side.BOTTOM);

        TitledToolBar dtt02 = new TitledToolBar();
        dtt02.setId("ddt02");
        dtt02.getChildren().add(b1);
        dockPane.dock(dtt02, Side.RIGHT);
        lb = new Label("id = dtt02");
        dtt02.getChildren().add(lb);      
        
        dtt02.setOnMouseDragEntered(ev -> {
            System.err.println("%%%%%%%%%%%% ENTERED");
        });
        dockPane.setOnMouseDragEntered(ev -> {
            System.err.println("%%%%%%%%%%%% ENTERED");
        });
        
        dtt02.setOnMouseDragReleased(ev -> {
            System.err.println("%%%%%%%%%%%% RELEASED");
        });
        
        TitledToolBar dtt03 = new TitledToolBar();
        dtt03.setId("ddt03");
        lb = new Label("id = dtt03");
        dtt03.getChildren().add(lb);        
        dtt01.nodeHandler().getPaneHandler().dock(dtt03, Side.RIGHT, dtt01);
        
/*        b1.setOnAction(ev ->{
//            DockUtil.print(dockPane, 1, " ", p -> {
//                return ((p instanceof Control) || (p instanceof Pane)) 
//                && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
//            });
           
            //List<Dockable> list = findNodes(dockPane, p -> {return (p instanceof Dockable);});
            List<Dockable> list = DockUtil.getAllDockable(dockPane);            
            System.err.println("findNodes size=" + list.size());            
            DockUtil.print(dockPane, 1, " ", p -> {
                return ((p instanceof DockSplitPane) || (p instanceof Dockable)) 
                && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });
            
        });
*/        
        Scene scene = new Scene(dockPane);
        
        stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        Rectangle r1;
        
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

}
