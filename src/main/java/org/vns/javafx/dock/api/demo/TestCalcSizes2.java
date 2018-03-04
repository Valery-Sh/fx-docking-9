package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.view.NodeResizer;

/**
 *
 * @author Valery
 */
public class TestCalcSizes2 extends Application {

    Stage stage;
    Scene scene;
    Stage primaryStage;

    @Override
    public void start(Stage stage) {
        Button resizeBtn = new Button("resize");
        Button delAddBtn = new Button("del & add");
        HBox topHBox = new HBox(resizeBtn, delAddBtn);
        topHBox.setPrefHeight(28);
        HBox bottomHBox = new HBox();
        HBox lastHBox = new HBox();
        VBox root = new VBox(topHBox, bottomHBox, lastHBox);
        root.setStyle("-fx-border-width: 1; -fx-border-color: brown");
        bottomHBox.setStyle("-fx-border-width: 1; -fx-border-color: green");
        VBox vbox = new VBox();
        primaryStage = stage;

        stage.setTitle("PRIMARY");

        Button createPopup = new Button("CreatePopup");
        createPopup.setMouseTransparent(true);
        VBox pane = new VBox(createPopup);

        VBox pane1 = new VBox();

        bottomHBox.getChildren().addAll(createPopup);

//        root.getChildren().addAll(pane, pane1);
        pane.setStyle("-fx-border-width: 1; -fx-border-color: blue");
        resizeBtn.setOnMouseClicked(e -> {
        NodeResizer nr = new NodeResizer(createPopup);
        //custom1.setTranslateX(50);
        //nr.setWindowType(NodeResizer.WindowType.STAGE);
        //nr.setApplyFtranslateXY(true);
        nr.show();

/*            double prefWidth = createPopup.prefWidth(createPopup.getPrefHeight());
            double prefHeight = createPopup.prefHeight(createPopup.getPrefWidth());
            System.err.println("width = " + createPopup.getWidth());
            System.err.println("height = " + createPopup.getHeight());
            System.err.println("prefWidth = " + prefWidth);
            System.err.println("prefHeight = " + prefHeight);
            System.err.println("   --- getPrefWidth = " + createPopup.getPrefWidth());
            System.err.println("   --- getPrefHeight = " + createPopup.getPrefHeight());
            System.err.println("---------------------------------------------------------");
            createPopup.setPrefWidth(createPopup.getWidth() + 5);
            createPopup.setPrefHeight(createPopup.getHeight() + 5);
*/            
        });
        delAddBtn.setOnMouseClicked(e -> {
            if (topHBox.getChildren().contains(createPopup)) {
                topHBox.getChildren().remove(createPopup);
                bottomHBox.getChildren().add(createPopup);
                System.err.println("DEL AND ADD");
            } else {
                bottomHBox.getChildren().remove(createPopup);
                topHBox.getChildren().add(createPopup);
                System.err.println("DEL AND ADD");
            }
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

        scene = new Scene(root, 200, 100);

        //scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");
        stage.setScene(scene);
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
