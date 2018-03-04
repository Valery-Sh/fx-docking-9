package org.vns.javafx.dock.api.demo;

import java.util.UUID;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class TestOwnerStages1 extends Application {

    Stage stage;
    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage stage) {
        HBox root = new HBox();
        VBox vbox = new VBox();
        primaryStage = stage;
        DockPane dockPane = new DockPane();
        Stage popup = new Stage();
        popup.initOwner(stage);
        
        Scene popupScene = new Scene(vbox);
        
        popup.setScene(popupScene);
        Button popupBtn = new Button("Popup Button");
        vbox.getChildren().add(popupBtn);
        
        stage.setTitle("PRIMARY");
        System.err.println("UUID = " + UUID.randomUUID());
        
        Button createPopup = new Button("CreatePopup");
        createPopup.setOnAction(a -> {
            popup.show();
        });
        VBox pane = new VBox(createPopup);
        root.getChildren().add(pane);
        
        //VBox pane1 = new VBox();
        StackPane sp = new StackPane(); 
        sp.setId("sp");
        DockNode dockNode = new DockNode("dockNode");
        DockNode dockNode1 = new DockNode("dockNode1");
        dockNode.setContent(sp);
        
        dockPane.dock(dockNode, Side.TOP);
        dockPane.dock(dockNode1, Side.RIGHT);
        
        root.getChildren().addAll(dockPane);


        popupBtn.setOnAction(a -> {
            if ( (dockNode.getContent() instanceof StackPane) && dockNode.getContent().getId().equals("sp")) {
                //pane1.getChildren().add(new Button("New Button"));
                StackPane sp1 = new StackPane();
                sp1.setId("sp1");
                dockNode.setContent(sp1);
            } else {
                ((StackPane)dockNode.getContent()).getChildren().add(new Button("New Button"));
            }
        });
        
        scene = new Scene(root, 550, 550);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        //popup.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
