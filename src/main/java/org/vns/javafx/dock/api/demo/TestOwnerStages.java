package org.vns.javafx.dock.api.demo;

import java.util.UUID;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.dragging.view.NodeResizer;

/**
 *
 * @author Valery
 */
public class TestOwnerStages extends Application {

    Stage stage;
    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox();
        primaryStage = stage;
        Stage popup = new Stage();
        popup.initOwner(stage);
        
        Scene popupScene = new Scene(vbox);

        popup.setScene(popupScene);
        Button popupBtn = new Button("replace VBox ");
        vbox.getChildren().add(popupBtn);
        Button popupBtn1 = new Button("add button");
        vbox.getChildren().add(popupBtn1);

        stage.setTitle("PRIMARY");
        System.err.println("UUID = " + UUID.randomUUID());

        Button createPopup = new Button("CreatePopup");
        createPopup.setOnAction(a -> {
            popup.show();
        });
        VBox pane = new VBox(createPopup);

        VBox pane1 = new VBox();
        VBox inPane = new VBox();

        inPane.setId("inPane");
//        inPane.setStyle("-fx-border-width: 2; -fx-border-color: red");
        pane1.getChildren().add(inPane);

        HBox root = new HBox();

        root.getChildren().addAll(pane, pane1);

        pane.setStyle("-fx-border-width: 1; -fx-border-color: blue");
          pane.setOnMouseClicked(e -> {
              System.err.println("prefWidth = " + createPopup.prefWidth(createPopup.getPrefHeight()));
              System.err.println("width = " + createPopup.getWidth());
              System.err.println("prefHeight = " + createPopup.getPrefHeight());
              createPopup.setPrefWidth(100);
          });
/*        pane.setOnMouseClicked(e -> {
            System.err.println("MOUSE CLICKED");
            NodeResizer nr = new NodeResizer(pane);
            //pane.setTranslateX(50);
            nr.setWindowType(NodeResizer.WindowType.STAGE);
            nr.setApplyFtranslateXY(true);
            nr.show();            
        });
*/
        //pane1.setStyle("-fx-border-width: 4; -fx-border-color: green");

        popupBtn.setOnAction(a -> {
            Pane p = (Pane) pane1.getChildren().get(0);
            if ((p instanceof VBox) && p.getId().equals("inPane")) {
                //pane1.getChildren().add(new Button("New Button"));
                VBox inPaneNew = new VBox();
                inPaneNew.setId("inPaneNew");
                //inPaneNew.applyCss();
                //inPaneNew.setStyle("-fx-border-width: 2; -fx-border-color: aqua");
                //inPaneNew.getStyleClass().add("fff");
                pane1.getChildren().set(0, inPaneNew);
                stage.requestFocus();
            }
        });
        popupBtn1.setOnAction(a -> {
            Pane p = (Pane) pane1.getChildren().get(0);
            if ((p instanceof VBox) && p.getId().equals("inPaneNew")) {
                //pane1.getChildren().add(new Button("New Button"));
                Button btn = new Button("New Button");
                p.getChildren().add(btn);
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
