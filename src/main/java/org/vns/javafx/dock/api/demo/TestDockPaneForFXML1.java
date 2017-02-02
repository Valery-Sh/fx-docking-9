/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockPaneForFXML1 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        
        //SplitPane sp1 = new SplitPane();
        stage.setTitle("PRIMARY");
        DockNode dn01 = new DockNode("dn01","Dn01", 0.3);    
        DockNode dn02 = new DockNode("dn02","Dn02");                
        DockNode dn03 = new DockNode("dn03","Dn03");                
        DockNode dn04 = new DockNode("dn04","Dn04");                
        DockNode dn05 = new DockNode("dn05","Dn05");                
        
        VPane vp01 = new VPane("vpane1");
        VPane vp02 = new VPane("vpane2");
        
        HPane hp01 = new HPane("hpid",0.66);        
        
        StackPane root = new StackPane();
        DockPane dockPane = new DockPane();
        root.getChildren().add(dockPane);
        HPane hpRoot = new HPane("hpaneRoot");
        dockPane.setRoot(hpRoot);                                
        hpRoot.append(vp01)
                .append(hp01)
                .append(dn01)
                .append(vp02)
                .append(dn02)
                .append(dn03);
        vp01.append(dn04);
        
        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
       // SplitDelegate.DockSplitPane dsp = SplitDelegate.DockSplitPane.getParentSplitPane(dn3);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
    
}
