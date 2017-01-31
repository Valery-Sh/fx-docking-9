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
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.controls.CustomSideBar;
import org.vns.javafx.dock.api.controls.DockNode;

/**
 *
 * @author Valery
 */
public class TestToolbar extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {

        StackPane root = new StackPane();

        stage.setTitle("Test DockSideBar");
        
        //ToolBar toolBar = new ToolBar(
        Button btn1 = new Button("Print");
//        ControlSB toolBar = new ControlSB(
ToolBar toolBar = new ToolBar(                
//DockSideBar1 toolBar = new DockSideBar(
//        CustomSideBar toolBar = new CustomSideBar(                
                btn1,
                new Button("New"),
                new Button("Open"),
                new Button("Save"),
                new Separator(),
                new Button("Clean"),
                new Button("Compile"),
                new Button("Run"),
                new Separator(),
                new Button("Debug"),
                new Button("Profile")
        );
        //root.getChildren().add(toolBar);
        VBox vb = new VBox();
        vb.setStyle("-fx-background-color: green;");
        vb.getChildren().addAll(new Button("bbb"));
        root.getChildren().add(vb);     
         root.getChildren().add(toolBar);
        //toolBar.toFront();
        //toolBar.getStyleClass().addAll(toolBar.getDelegate().getStyleClass());
        //toolBar.getDelegate().setOrientation(Orientation.VERTICAL);
        //toolBar.setOrientation(Orientation.VERTICAL);
        Scene scene = new Scene(root);
        scene.getRoot().setStyle("-fx-background-color: yellow");
        toolBar.setStyle("-fx-background-color: aqua; -fx-border-width: 0");
        //toolBar.setStyle("-fx-border-width: 0");

        DockNode dn03 = new DockNode();

        //dn03.setPrefHeight(100);
        dn03.nodeHandler().setTitle("DockNode: dn03");


        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        btn1.setOnAction(a -> {
            System.err.println("sideBar01.getWidth()=" + toolBar.getWidth());
            System.err.println("sideBar01.getHeight()=" + toolBar.getHeight());
   //         System.err.println("sideBar01.getHeight()=" + toolBar.getHeight());
            //System.err.println("sideBar01.Height()=" + toolBar.getDelegate().getHeight());
            
            System.err.println("sideBar01.isManaged=" + toolBar.isManaged());            
            //Insets ins = toolBar.getDelegate().getPadding();
            //System.err.println("sideBar01.insets=" + ins);            
            
        });
        
        stage.setOnShown(e -> {
         //   System.err.println("sideBar01.getWidth()=" + toolBar.getWidth());
            //System.err.println("sideBar01.getWidth()=" + toolBar.getDelegate().getWidth());
         //   System.err.println("sideBar01.getHeight()=" + toolBar.getHeight());
            //System.err.println("sideBar01.Height()=" + toolBar.getDelegate().getHeight());
            
            System.err.println("sideBar01.isManaged=" + toolBar.isManaged());            
            //Insets ins = toolBar.getDelegate().getPadding();
            //System.err.println("sideBar01.insets=" + ins);            
        });
        
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
