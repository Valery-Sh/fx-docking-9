package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane2;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRedirector;

/**
 *
 * @author Valery
 */
public class TestDockTabPane2 extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;
    public boolean scrollBarShowing = true;
    public boolean menuButtonShowing = true;
    
    @Override
    public void start(Stage stage) throws Exception {
        Button b1 = new Button("b01 - DOCK");
        /*        b1.setOnAction(a -> {
            //new DragPopup(dpCenter);
//            System.err.println("STAGE COUNT=" + StageHelper.getStages().size());
        });
         */
        //BorderPane rootPane = new BorderPane();
        StackPane rootPane = new StackPane();
        rootPane.setId("ROOT PANE");
        
        stage.setTitle("Tests Several DockPanes");
        DockPane dpCenter = new DockPane();
        dpCenter.setPrefHeight(200);
        dpCenter.setId("dpCenter");
        
        DockNode dn01 = new DockNode();
        dn01.setId("dn01");
        Button btn = new Button("BOTTON FROM STG");
        dn01.getChildren().add(btn);
        dpCenter.dock(dn01, Side.TOP);
        dn01.setTitle("DockNode: dn01");
        Button dn01Btn = new Button("Print");
        dn01Btn.setOnAction((event) -> {
            DockUtil.print(dn01Btn.getScene().getRoot());
        });
        dpCenter.getChildren().add(dn01Btn);
        
        DockPane dpRight = new DockPane();
        dpRight.setPrefHeight(200);
        dpRight.setId("dpRight");
        DockNode dn02 = new DockNode();
        dn02.setId("dn02");
        dpRight.dock(dn02, Side.TOP);
        Button dn02Btn = new Button("Print");
        dn02Btn.setOnAction((event) -> {
            DockUtil.print(dn02Btn.getScene().getRoot());
        });
        
        dpRight.getChildren().add(dn02Btn);
        
        SplitPane sp = new SplitPane(dpCenter,dpRight);
        rootPane.getChildren().add(sp);
        //rootPane.setCenter(dpCenter);
        //rootPane.setRight(dpRight);
        
        Scene scene = new Scene(rootPane);

        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);
        
        Stage stage01 = new Stage();
        //StackPane rootAsDockPane = new StackPane();
        //rootPane01.setId("ROOT PANE 01");
        
        stage01.setTitle("STAGE01: Tests Several DockPanes ");
        
        DockPane rootAsDockPane = new DockPane();
        //stg01dp01.paneHandler().setUsedAsDockTarget(false);
        rootAsDockPane.setPrefHeight(200);
        rootAsDockPane.setPrefWidth(200);
        rootAsDockPane.setId("stg01dp01");
        DockNode stg01dn01 = new DockNode();
        stg01dn01.setId("stg01dn01");
        
        Button btn01 = new Button("Button of Tab 01");
        Pane pane01= new Pane(btn01);
        stg01dn01.getChildren().add(pane01);
        pane01.setStyle("-fx-background-color: aqua");
        
        //stg01dn01.getChildren().add(btn01);
        
        rootAsDockPane.dock(stg01dn01, Side.TOP);
        
        DockNode stg01dn02 = new DockNode();
        stg01dn02.setTitle("stg01dn02" );
        stg01dn02.setId("stg01dn02");
        Button btn02 = new Button("Button of Tab 02");
        stg01dn02.getChildren().add(btn02);
                
        DockNode stg01dn03 = new DockNode();
        stg01dn03.setTitle("stg01dn03" );
        stg01dn03.setId("stg01dn03");
        Pane stack02 = new Pane(btn02);
        stg01dn02.getChildren().add(stack02);
        stack02.setStyle("-fx-background-color: aqua");
        Button btn03 = new Button("Button 03 of Tab 03");
        Button btn04 = new Button("Button 04 of Tab 03");
        
        VBox stack03 = new VBox(btn03, btn04);
        stg01dn03.getChildren().add(stack03);
        stack03.setStyle("-fx-background-color: gray");
        
        //stg01dp01.dock(stg01dn02, Side.TOP);
        
        /*DockTab stg01tab01 = new DockTab(stg01dn02);
        stg01tab01.setTitle("DockTab 01");
        rootAsDockPane.dock(stg01tab01, Side.RIGHT);
        */
        DockTabPane2 dockTabPane = new DockTabPane2();
        btn03.setOnAction(a->{
           System.err.println("SHOW " + scrollBarShowing); 
           dockTabPane.showScrollBar(! scrollBarShowing);
           scrollBarShowing = ! scrollBarShowing;
        });
           //dockTabPane.showScrollBar(false);
           //scrollBarShowing = false;
        
        btn04.setOnAction(a->{
           dockTabPane.showMenuButton(! menuButtonShowing);
           menuButtonShowing = ! menuButtonShowing;
        });
           //dockTabPane.showMenuButton(false);
           //menuButtonShowing = false;
        rootAsDockPane.dock(dockTabPane, Side.LEFT);
        
        stage.show();
        
        Scene scene01 = new Scene(rootAsDockPane);
        stage01.setScene(scene01);
        
        
        //DockRedirector dpd = new DockRedirector(rootAsDockPane);
        //DragPopupDelegate dpd = new DockRedirector(dockTabPane.paneHandler());        
        stage01.setOnShown(e -> { 
            Point2D p = rootAsDockPane.localToScreen(0, 0);
            double w = rootAsDockPane.getWidth();
            double h = rootAsDockPane.getHeight();
            //dpd.getRootPane().setPrefSize(w, h);
            //dpd.show(p.getX(),p.getY()); 
            //dpd.show(50,50);
        });
        stage01.setX(150);
        stage01.show();
        
        dockTabPane.paneHandler().dock(stg01dn03, Side.TOP);        
        //Platform.runLater(()->{
        
            
        //});
        
        
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
