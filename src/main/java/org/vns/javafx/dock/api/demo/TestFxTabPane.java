/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode2;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestFxTabPane  extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        StackPane root = new StackPane();
        root.setId("root StackPane");
        //SplitPane sp = new SplitPane();
        Button b01 = new Button("Change Rotate Angle");
        TabPane tabPane = new TabPane();
        
        root.getChildren().add(tabPane);
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
        dn01.nodeHandler().setTitle("DockNode: dn01");
        b01.setOnAction(a -> {
        });
        Button b02 = new Button("Change Orientation");
        
        b02.setOnAction(a -> {
        });        
        
        DockNode2 dn02 = new DockNode2();
        dn02.setId("DockNode: dn02");
        dn02.setPrefHeight(100);
        dn02.nodeHandler().setTitle("DockNode: dn02"); 
        Button t0Btn = new Button();
        t0Btn.getStyleClass().add("drag-button");
        Tab t0 = new Tab();
        t0.setGraphic(t0Btn);
        //Tab t1 = new Tab("dn01 tab", dn01);
        
        tabPane.setSide(Side.RIGHT);
        Tab t1 = new Tab();
        Label lbc1 = new Label("Label text");
        //Text txt1 = new Text("Text1");
        //lbc1.setPrefHeight(lbc1.getWidth());
        Group g01 = new Group(lbc1);
        
        //lbc1.setRotate(90);
        //lbc1.requestLayout();
        //lbc1.setContentDisplay(ContentDisplay.RIGHT);
        t1.setContent(dn01);
        t1.setGraphic(lbc1);
        tabPane.setRotateGraphic(true);
        Label lbc2 = new Label("VALERA");
        //Group g2 = new Group(lbc2);
        //lbc2.b
        Tab t2 = new Tab();
        t2.setGraphic(lbc2);
        //tabPane.get
        tabPane.getTabs().addAll(t0,t1,t2);
        tabPane.getSelectionModel().select(1);
        
        
        t1.setOnSelectionChanged(e -> {
            System.err.println("e.getEventType()" + e.getEventType());
            System.err.println("t1.getGraphics=" + t1.getGraphic());
            Label lbc = new Label(t1.getText() + " CC");
            //t1.setGraphic(lbc);
            
        });
        
        stage.setScene(scene);
        stage.show();
        
        lbc1.setPrefHeight(lbc1.getWidth());

        
        System.err.println("dn01.parent=" + dn01.getParent());
        System.err.println("dn01.parent=" + dn02.getParent());
        
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
    
    public static class MyTab extends Tab {

        public MyTab() {
        }

        public MyTab(String text) {
            super(text);
        }
        public MyTab(String text, Group group, String s) {
            super(((Label)group.getChildren().get(0)).getText());
            setGraphic(group);
        }

        public MyTab(String text, Node content) {
            super(text, content);
            
        }
        
        
    }
            
}
