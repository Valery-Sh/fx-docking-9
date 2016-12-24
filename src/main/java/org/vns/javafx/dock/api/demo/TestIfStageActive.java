/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import com.sun.javafx.stage.StageHelper;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockToolBar;
import org.vns.javafx.dock.TitledToolBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.StageRegistry;
//import org.vns.javafx.dock.api.StageRegistry;

/**
 *
 * @author Valery
 */
public class TestIfStageActive extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
//        StageRegistry.register(stage);
        //com.sun.javafx.scene.NodeHelper.
        Node dd;

        Node nn;
        //nn.
        stage.setTitle("Tests If Stage Active");
        DockPane dockPane = new DockPane();
        dockPane.setPrefHeight(200);
        dockPane.setId("MAIN DOCK PANE");
        Button b1 = new Button("b01 - DOCK");
        /*        b1.setOnAction(a -> {
            //new DragPopup(dockPane);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        TitledToolBar dtt01 = new TitledToolBar();
        dtt01.setId("ddt01");
        Label lb = new Label("id = dtt01");
        dtt01.getChildren().add(lb);

        dockPane.dock(dtt01, Side.BOTTOM);

        TitledToolBar dtt02 = new TitledToolBar();
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

        TitledToolBar dtt03 = new TitledToolBar();
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

        Stage stage01 = new Stage();
        stg01 = stage01;
        //StageRegistry.register(stage01);        
        stage01.setTitle("Stage01");
        Pane pane01 = new Pane();
        pane01.setPrefSize(100, 200);
        Scene scene01 = new Scene(pane01);
        initSceneDragAndDrop(scene01);
        pane01.setOnMouseDragOver(v -> {
            //System.err.println("Stage01. hover ");
        });

        stage01.setScene(scene01);
        stage01.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(true);

        stage.show();
        //stage01.show();
        List<Dockable> ls = DockUtil.getAllDockable(dockPane);

        ls.forEach(s -> {
            //System.err.println("++++ DOCKABLE LIST: " + ((Node) s).getId());
        });


        dtt03.dock(dtt01, Side.TOP);

        dtt01.stateProperty().setFloating(false);

        dtt01.stateProperty().setFloating(true);
        ((Stage) ((Region) dtt01).getScene().getWindow()).close();
        dtt01.stateProperty().setFloating(false);

        dockPane.dock(dtt01, Side.LEFT);
        
        DockNode dockNode01 = new DockNode();
        dockNode01.setId("dockNode01");
        dockNode01.titleProperty().set("Dock Node01");
        dockPane.dock(dockNode01, Side.BOTTOM);

        DockToolBar dockToolBar01 = new DockToolBar();
        dockToolBar01.setId("dockToolBar01");
        dockToolBar01.titleProperty().set("Dock ToolBar 01");
        Label lbTB = new Label("Tool Bar");
        lbTB.autosize();
        
        //lbTB.setPrefHeight(100);
        //lbTB.setPrefWidth(28);
        lbTB.setRotate(-90);
        lbTB.autosize();
        
        dockToolBar01.getItems().add(new Group(lbTB));
        dockToolBar01.setOrientation(Orientation.VERTICAL);
        dockToolBar01.useAaTitleBar(lbTB);
        dockPane.dock(dockToolBar01, Side.LEFT);
        
        Stage stage02 = new Stage();
        stg02 = stage02;

        stage02.setTitle("Stage02");
        Pane pane02 = new Pane();
        pane02.setPrefSize(200, 200);
        Scene scene02 = new Scene(pane02);
        stage02.setScene(scene02);

        Stage stage03 = new Stage();

        stage03.setTitle("Stage03");
        Pane pane03 = new Pane();
        pane03.setPrefSize(200, 150);
        Scene scene03 = new Scene(pane03);
        stage03.setScene(scene03);
        stage03.initOwner(stage);

        Stage stage04 = new Stage();
        stage04.setTitle("Stage04");
        Pane pane04 = new Pane();
        pane04.setPrefSize(200, 150);

        Scene scene04 = new Scene(pane04);
        stage04.setScene(scene04);
        stage04.initOwner(stage03);

        Stage stage05 = new Stage();

        stage05.setTitle("Stage05");
        Pane pane05 = new Pane();
        pane05.setPrefSize(200, 150);
        Scene scene05 = new Scene(pane05);
        stage05.setScene(scene05);
        stage05.initOwner(stage03);
        //stage02.initOwner(stage);

        //stage02.setX(100);
        //stage02.show();
        //stage05.show();
        //stage03.show();
        //stage04.show();        
        Platform.runLater(() -> {

            //StageHelper.getStages().remove(stage05);
            //stage05.initOwner(stage03);
            //stage05.close();
            //stage05.show();
            //stage05.toFront();
            //StageHelper.getStages().add(stage05);
            StageHelper.getStages().forEach(s -> {
                //System.err.println("RUN LATER: stage.title=" + s.getTitle() + "; owner=" + StageRegistry.getOwner(s));

            });

        });

        b1.setOnAction(ev -> {
            //System.out.println("===============================");
            //System.out.println("1) On b1 Action Stages.size=" + StageHelper.getStages().size());

            for (int i = 0; i < StageHelper.getStages().size(); i++) {
                //System.out.println("2) On b1 Action: Stage title=" + StageHelper.getStages().get(i).getTitle());
            }
            //System.out.println("----------------------------------");
            //System.out.println("3) On b1 Action Stages.size=" + StageRegistry.getStages().size());

            for (int i = 0; i < StageRegistry.getStages().size(); i++) {
                Stage stg = StageRegistry.getStages().get(i);
                String tStg = stg.getTitle();
                String tOwner = null;
                Window w = StageRegistry.getOwner(stg);
                if (w != null && (w instanceof Stage)) {
                    tOwner = ((Stage) w).getTitle();
                }
                //System.out.println("4) On b1 Action: Stage title=" + tStg + "; owner.title=" + tOwner);
            }

            //System.out.println("===============================");

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