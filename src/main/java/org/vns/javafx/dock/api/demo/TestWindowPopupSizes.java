/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.demo;

import java.awt.Paint;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestWindowPopupSizes extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setWidth(50);
        stage.setHeight(50);
        stage.setX(50);
        stage.setY(50);
        
    //    StackPane root = new StackPane();
        BorderPane root = new BorderPane();
        root.setId("ROOT");
        PopupControl popup = new PopupControl();
        //popup.initStyle(StageStyle.TRANSPARENT);
        popup.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            System.err.println("==============================================");
            System.err.println("Insets " + root.getInsets());
            System.err.println("----------------------------------------------");
            Bounds bnd = root.localToScreen(root.getBoundsInLocal());
            System.err.println("ev.getX()=" + e.getX() + "; ev.getY=" + e.getY());
            System.err.println("ev.getScreenX()=" + e.getScreenX() + "; ev.getScreenY=" + e.getScreenY());            
            System.err.println("stage.X=" + popup.getX() + "; stageY=" + popup.getY());            
            System.err.println("root.X=" + bnd.getMinX() + "; rootY=" + bnd.getMinY());            
            
            System.err.println("Stage width = " + popup.getWidth() + "; height=" + popup.getHeight());
            System.err.println("root  width = " + root.getWidth() + "; height=" + root.getHeight());
            System.err.println("root bounds width = " + bnd.getWidth() + "; height=" + bnd.getHeight());
            //System.err.println("Scene width = " + scene.getWidth() + "; height=" + scene.getHeight());
            System.err.println("layoutBounds = " + root.getLayoutBounds());
            
        });
        //stage.sizeToScene();
        //root.setStyle("-fx-padding: 10; -fx-background-color: transparent");
        
        //root.setStyle("-fx-background-color: green");
        //root.setStyle("-fx-background-color: green");
        //root.getStyleClass().clear();
        //root.setStyle("-fx-padding: 20; -fx-background-color: green");
        //root.setStyle("-fx-background-color: blue");
        popup.getStyleClass().clear();
        //root.setStyle("-fx-background-color: green");
     //1234567890abcde;pbrkvyprstefx
        root.getStyleClass().add("my-test-popup-css");
        Button b1 = new Button("Show Dimentions");
        Button b2 = new Button("b2r");
        //root.getChildren().add(b1);
        //Shape sh = new Rectangle(59,20,Color.BLUE);
        //root.setTop(sh);
        root.setCenter(b1);
        b1.setGraphic(b2);
        
        StackPane primStageRoot = new StackPane();
        Scene scene = new Scene(primStageRoot);        
        b1.setOnAction(a->{
            Bounds b  = root.getBoundsInParent();
            System.err.println("root.getBoundsInParent()=" + b);  
            b = root.getBoundsInLocal();
            System.err.println("root.getBoundsInLocal()=" + b);  
            root.getStyleClass().forEach(s -> {
                System.err.println("styleClass = " + s);
                
            });
            System.err.println("EFFECT " + root.getEffect());
            if ( root.getEffect() != null ) {
                //root.setEffect(null);
            }
            //System.err.println("EFFECT " + root.getEffect().);
            System.err.println("Padding " + root.getPadding());
            System.err.println("Insets " + root.getInsets());
            //System.err.println("Background " + root.getBackground().getOutsets());
            Bounds bnd = root.localToScreen(root.getBoundsInLocal());
            System.err.println("Stage width = " + popup.getWidth() + "; height=" + popup.getHeight());
            System.err.println("root  width = " + root.getWidth() + "; height=" + root.getHeight());
            System.err.println("root bounds width = " + bnd.getWidth() + "; height=" + bnd.getHeight());
            System.err.println("Scene width = " + scene.getWidth() + "; height=" + scene.getHeight());
            System.err.println("layoutBounds = " + root.getLayoutBounds());
            
            
        });
        // 1. setStyle("-fx-padding: 10")
        //dockPane.dock(p1, Side.TOP).dockableController().setTitle("Pane p1");
        
        stage.setScene(scene);
        //stage.getScene().setFill(Color.BEIGE);    
        //stage.getScene().setFill(null);
        
        popup.getScene().setRoot(root);
        root.applyCss();
        //popup.setTitle("Stage Dimentions");
        
        popup.getScene().setFill(Color.AQUA);

        popup.setOnShown(s -> {
            //((Pane)custom.getContent()).getChildren().forEach(n -> {System.err.println("custom node=" + n);});
            //System.err.println("tp.lookup(arrowRegion)" + tp.);
            //DockUtil.print(b1);
        });
        popup.setMinHeight(100);
        root.setMinHeight(100);
        //popup.setWidth(200);
        //root.setPrefWidth(200);
        stage.show();
        popup.show(stage);
        
        

        
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
