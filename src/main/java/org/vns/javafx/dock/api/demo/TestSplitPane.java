/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode2;
import org.vns.javafx.dock.DockableDockPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class TestSplitPane  extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        StackPane root = new StackPane();
        root.setId("root StackPane");
        //SplitPane sp = new SplitPane();
        Button b01 = new Button("Change Rotate Angle");
        
        SplitPane sp01 = new SplitPane();
        root.getChildren().add(sp01);
        SplitPane sp01_1 = new SplitPane();
        sp01.getItems().add(sp01_1);
        Button spBtn01 = new Button("spBtn01");
        Button spBtn02 = new Button("spBtn02");
        sp01_1.getItems().addAll(spBtn01,spBtn02);
        spBtn01.setOnAction(a -> {
            System.err.println("spBtn02.parent " + spBtn02.getParent());
            System.err.println("spBtn02.parent.parent " + spBtn02.getParent().getParent());
            System.err.println("spBtn02.parent.parent.parent " + spBtn02.getParent().getParent().getParent());
            ((Pane)spBtn02.getParent()).getChildren().remove(spBtn02);
        });
        
        
        //root.getChildren().add(sp);
        //sp.getItems().add(ddp);
        //DockPane ddp = new DockPane();
        Label lb = new Label("VALERA");
        //vb.getChildren().addAll(lb,ddp);        
        
    
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-background-color: yellow");

        DockNode2 dn01 = new DockNode2();
        dn01.setId("DockNode: dn01");
        dn01.setPrefHeight(100);
        sp01.getItems().add(dn01);
        dn01.nodeHandler().setTitle("DockNode: dn01");
        Button b02 = new Button("Change Orientation");
        
        b02.setOnAction(a -> {
        });        
        
        DockNode2 dn02 = new DockNode2();
        dn02.setId("DockNode: dn02");
        dn02.setPrefHeight(100);
        dn02.nodeHandler().setTitle("DockNode: dn02");   
        
        stage.setScene(scene);
        stage.show();
        
        Stage stage1 = new Stage();
        StackPane sp1 = new StackPane();
        SplitPane dsp1 = new SplitPane();
        sp1.getChildren().add(dsp1);
        Scene sc1 = new Scene(sp1);
        stage1.setScene(sc1);
        stage1.setWidth(100);
        stage1.setHeight(100);
        stage1.show();
        spBtn01.setOnAction(a -> {
            System.err.println("b01 clicked");
            System.err.println("b01 sp01.size()=" + sp01.getItems().size());
            dsp1.getItems().add(dn01);
        });
        spBtn02.setOnAction(a -> {
            System.err.println("b02 clicked");
            System.err.println("b02 sp01.size()=" + sp01.getItems().size());
            sp01.getItems().forEach(n -> {
                System.err.println("sp01.item = " + n);
            });
            System.err.println("b02 sp01.size()=" + sp01.getItems().size());
            
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

    private void initSceneDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() || db.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String url = null;
            if (db.hasFiles()) {
                url = db.getFiles().get(0).toURI().toString();
            } else if (db.hasUrl()) {
                url = db.getUrl();
            }
            if (url != null) {
                //songModel.setURL(url);
                //songModel.getMediaPlayer().play();
            }
            System.err.println("DROPPED");
            event.setDropCompleted(url != null);
            event.consume();
        });
    }
}
