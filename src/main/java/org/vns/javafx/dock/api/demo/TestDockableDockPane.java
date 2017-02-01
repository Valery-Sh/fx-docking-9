package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode2;
import org.vns.javafx.dock.api.DockPaneBase;
import org.vns.javafx.dock.DockableDockPane;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestDockableDockPane  extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        StackPane root = new StackPane();
        root.setId("root StackPane");
        //SplitPane
        
        //p = new SplitPane();
        Button b01 = new Button("Change Rotate Angle");
        
        DockableDockPane ddp = new DockableDockPane(new DockPaneBase());
        ddp.setId("DockableDockPane: dpp");
        root.getChildren().add(ddp);
        //root.getChildren().add(sp);
        //sp.getItems().add(ddp);
        //DockPane ddp = new DockPaneBase();
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
        ddp.dock(dn01, Side.TOP);
        ddp.dock(dn02, Side.BOTTOM);
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
