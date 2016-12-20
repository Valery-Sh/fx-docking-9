/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.stage.WindowHelper;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockToolBarTitled;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragPopup;

/**
 *
 * @author Valery
 */
public class TestIfStageActive extends Application {

    public static Stage frontStage;

    @Override
    public void start(Stage stage) throws Exception {

        //com.sun.javafx.scene.NodeHelper.
        Node dd;

        Node nn;
        //nn.
        stage.setTitle("Tests If Stage Active");
        DockPane dockPane = new DockPane();
        dockPane.setId("MAIN DOCK PANE");
        Button b1 = new Button("b01 - DOCK");
/*        b1.setOnAction(a -> {
            //new DragPopup(dockPane);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
*/        
        DockToolBarTitled dtt01 = new DockToolBarTitled();
        dtt01.setId("ddt01");
        Label lb = new Label("id = dtt01");
        dtt01.getChildren().add(lb);

        dockPane.dock(dtt01, Side.BOTTOM);

        DockToolBarTitled dtt02 = new DockToolBarTitled();
        dtt02.setId("ddt02");
        dtt02.getChildren().add(b1);
        dockPane.dock(dtt02, Side.RIGHT);
        lb = new Label("id = dtt02");
        dtt02.getChildren().add(lb);

        dtt02.setOnMouseDragEntered(ev -> {
            System.err.println("%%%%%%%%%%%% ENTERED");
        });
        dockPane.setOnMouseDragEntered(ev -> {
            System.err.println("%%%%%%%%%%%% ENTERED");
        });

        dtt02.setOnMouseDragReleased(ev -> {
            System.err.println("%%%%%%%%%%%% RELEASED");
        });

        DockToolBarTitled dtt03 = new DockToolBarTitled();
        dtt03.setId("ddt03");
        lb = new Label("id = dtt03");
        dtt03.getChildren().add(lb);
        dtt01.dock(dtt03, Side.RIGHT);
/*        b1.setOnAction(ev ->{
            DockUtil.print(dockPane, 1, " ", p -> {
                return ((p instanceof Control) || (p instanceof Pane)) 
                && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });
           
            //List<Dockable> list = findNodes(dockPane, p -> {return (p instanceof Dockable);});
            List<Dockable> list = DockUtil.getAllDockable(dockPane);            
            System.err.println("findNodes size=" + list.size());            
            DockUtil.print(dockPane, 1, " ", p -> {
                return ((p instanceof DockSplitPane) || (p instanceof Dockable)) 
                && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });
            
        });
         */
        Scene scene = new Scene(dockPane);

        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);

        stage.show();

        Stage stage01 = new Stage();
        stage01.setTitle("Stage01");
        Pane pane01 = new Pane();
        pane01.setPrefSize(100, 200);
        Scene scene01 = new Scene(pane01);
        initSceneDragAndDrop(scene01);
        pane01.setOnMouseDragOver(v -> {
            System.err.println("Stage01. hover ");
        });

        pane01.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.err.println("Stage01. hover = " + newValue);
            }
        });
        stage01.setScene(scene01);
        /*        Platform.runLater(() -> {
            stage.requestFocus();
        });
         */
        //stage01.initOwner(stage);
        stage01.show();

        Stage stage02 = new Stage();
        frontStage = stage02;
        stage02.setTitle("Stage02");
        Pane pane02 = new Pane();
        pane02.setPrefSize(100, 200);
        Scene scene02 = new Scene(pane02);
        stage02.setScene(scene02);
        /*Platform.runLater(() -> {
            stage02.requestFocus();
        });
        */
        //stage02.initOwner(stage);
        stage02.setX(100);
        stage02.show();
        b1.setOnAction(ev ->{
            ObservableList<Stage> ls = StageHelper.getStages();
            ls.remove(stage02);
            ls.add(0, stage02);
            System.out.println("Stages.size=" + ls.size());
            for ( int i=0; i < ls.size(); i++) {
                System.out.println("Stage title=" + ls.get(i).getTitle());
            }
            stage01.toFront();
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
