/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.demo.controls.CustomControl;

/**
 *
 * @author Valery
 */
public class TestTranslate extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        StackPane rootPane = new StackPane();
        rootPane.setId("DOCK PANE");
        Button b1 = new Button("b01");
        Pane p1 = new HBox(b1);
        p1.setStyle("-fx-border-color: red; -fx-border-width: 1");
        p1.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                System.err.println("CLICKED");
            }
        });
        rootPane.getChildren().add(p1);
        p1.setId("pane p1");
        Button b2 = new Button("b02");
        Pane p2 = new VBox(b2);
        p2.setStyle("-fx-border-color: blue; -fx-border-width: 1");        
        rootPane.getChildren().add(p2);
        b2.setOnAction(a->{
            if ( p2.getTranslateX() != 0 ) {
                p2.setTranslateX(0);
                p2.setTranslateY(0);
            } else {
                p2.setTranslateX(20);
                p2.setTranslateY(20);
            }
        });
        Scene scene = new Scene(rootPane);
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        
        stage.setOnShown(s -> {
            //((Pane)custom.getContent()).getChildren().forEach(n -> {System.err.println("custom node=" + n);});
            //System.err.println("tp.lookup(arrowRegion)" + tp.lookup("#arrowRegion"));
            DockUtil.print(rootPane);
        });
        stage.show();
        //javafx.data.pull.PullParser
        
        
        StageHelper.getStages().addListener(new ListChangeListener<Stage>(){
            @Override
            public void onChanged(ListChangeListener.Change<? extends Stage> c) {
                while (c.next() ) {
                    System.err.println("StageAdded c=" + c.getAddedSubList().get(0).getTitle());
                }
                //+ c.getAddedSubList());
                //.get(0).getTitle()
            }
        });
        
        VBox vbRoot = new VBox();
        Scene scene1 = new Scene(vbRoot);
        Stage stage1 = new Stage();
        stage1.setTitle("Stage01");
        stage1.setScene(scene1);
        
        stage1.show();

        
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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
