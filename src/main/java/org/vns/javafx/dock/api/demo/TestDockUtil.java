/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockUtil extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        Button b1 = new Button("Btn 1");
        Button b2 = new Button("Btn 2");
        
        //VBox rootPane = new VBox(b1);
        BorderPane rootPane = new BorderPane(b1);
        rootPane.setBottom(b2);
        rootPane.setMouseTransparent(false);
        rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            System.err.println("Node x = " + e.getScreenX() + "; y = " + e.getScreenY());
            System.err.println("   --- Node = " + DockUtil.findNode(rootPane, e.getScreenX(), e.getScreenY()));
        });
/*        rootPane.setOnMouseClicked(e -> {
            System.err.println("Node x = " + e.getScreenX() + "; y = " + e.getScreenY());
        });
*/        
        stage.setTitle("Test DockUtil");
        Scene scene = new Scene(rootPane);
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

