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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestScrollPane extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        Button b = new Button("b01 - DOCK");
        /*        b1.setOnAction(a -> {
            //new DragPopup(dpCenter);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        //BorderPane rootPane = new BorderPane();
        VBox rootPane = new VBox();
        rootPane.setPrefHeight(200);

        HBox hBox = new HBox();
        
        ScrollPane scrollPane = new ScrollPane();
        HBox content = new HBox();
        //content.setStyle("fx-background-color: blue;");
        //content.setBackground();

        Button b1 = new Button("Button 01");
        Button b2 = new Button("Button 02");
//        content.getChildren().addAll(b1,b2);
        content.getChildren().addAll(b1);
      
        scrollPane.setContent(content);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        //scrollPane.mi

        
        hBox.getChildren().add(scrollPane);
        hBox.getChildren().add(new Button("do  scroll"));
        rootPane.getChildren().add(hBox);
        StackPane lowPane = new StackPane();
        rootPane.getChildren().add(lowPane);
        
        
        Scene scene = new Scene(rootPane);
        rootPane.setId("ROOT PANE");
        scrollPane.getStyleClass().add("edge-to-edge");
        
        scrollPane.setMinSize(0, 0);
        //scrollPane.minViewportHeightProperty().bind(content.heightProperty());

        HBox box01 = new HBox();
        Button addBtn = new Button("add");
        Button removeBtn = new Button("remove");
        
        removeBtn.setOnAction(a -> {
            if ( ! content.getChildren().isEmpty()) {
                content.getChildren().remove(0);
            }
        });
        addBtn.setOnAction(a -> {
            content.getChildren().add(new Button("" + content.getChildren().size() + 1));
        });
        
        
        box01.getChildren().addAll(addBtn, removeBtn);
        lowPane.getChildren().add(box01);
        
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

}
