package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestBorderPane extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        BorderPane borderPane = new BorderPane();
        
        stage.setTitle("Test DockSideBar");

        borderPane.setPrefHeight(300);
        borderPane.setPrefWidth(300);
        
        Button b01 = new Button("Change Rotate Angle");
        
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(borderPane);
        //scene.getRoot().setStyle("-fx-background-color: yellow");

        
        Button b02 = new Button("Change Orientation");
        Button b03 = new Button("Change Side");
        Button b04 = new Button("center Button");
        //borderPane.getChildren().addAll(b01,b02,b03);
        borderPane.setTop(b01);
        borderPane.getChildren().add(b02);
        borderPane.getChildren().add(b03);
        borderPane.setCenter(b04);
        System.err.println("0 borderPane.getChildren().size()=" + borderPane.getChildren().size());
        
        b01.setOnAction(e->{
            borderPane.setCenter(null);
            System.err.println("1 borderPane.getChildren().size()=" + borderPane.getChildren().size());            
        });
        
        stage.setScene(scene);
        stage.setOnShown(s -> {
            borderPane.getChildren().forEach(n-> {
                System.err.println("parent=" + n.getParent());
            });
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

