/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api.demo.designer;

import com.sun.glass.ui.Cursor;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.api.DefaultContextLookup;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.IndicatorDelegate;

/**
 *
 * @author Valery
 */
public class TestDockable01  extends Application {

    private ObservableList<Shape> nodeList = FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox();
//        Pane p;
//        nodeList.add(root);
        Label btn1Graphic = new Label("btn1Graphic");
        Label lbGraphic = new Label("lbGraphic");
        btn1Graphic.setGraphic(lbGraphic);

        root.setId("ROOT");
        DockBorderPane dockBorderPane1 = new DockBorderPane();
        dockBorderPane1.setStyle("-fx-cursor: hand ; -fx-border-width: 2px; -fx-border-color: red");
        
        root.getChildren().add(dockBorderPane1);

        Button btn1 = new Button("btn1");
        DockRegistry.getInstance().registerDefault(btn1);
        DockRegistry.dockable(btn1).getDockableContext().setDragNode(btn1);
        dockBorderPane1.setRight(btn1);
        Button btn2 = new Button("btn2");
        DockRegistry.getInstance().registerDefault(btn2);
        DockRegistry.dockable(btn2).getDockableContext().setDragNode(btn2);
        dockBorderPane1.setTop(btn2);
        
        Button btn3 = new Button("btn3");
        DockRegistry.getInstance().registerDefault(btn3);
        DockRegistry.dockable(btn3).getDockableContext().setDragNode(btn3);
        dockBorderPane1.setBottom(btn3);

        Button btn4 = new Button("btn4");
        DockRegistry.getInstance().registerDefault(btn4);
        DockRegistry.dockable(btn4).getDockableContext().setDragNode(btn4);
        dockBorderPane1.setLeft(btn4);

        Button btn5 = new Button("btn5");
        DockRegistry.getInstance().registerDefault(btn5);
        DockRegistry.dockable(btn5).getDockableContext().setDragNode(btn5);
        dockBorderPane1.setLeft(btn5);
        
        btn1.setGraphic(btn1Graphic);
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        //root.getChildren().add(btn1);
        stage.sizeToScene();
        
        //scene.setFill(null);
        stage.setTitle("Stage TestDockable01");
        stage.setMinHeight(100);
        root.setMinHeight(100);
        stage.setMinWidth(400);
        root.setMinWidth(400);

        btn1.setOnAction(a -> {
       
            System.err.println("bestCursor=" + Cursor.getBestSize(64,64));
            Map<Class, List<Object>> m = ((DefaultContextLookup)dockBorderPane1.getTargetContext().getLookup()).lookup;
            m.forEach((k,v) -> {
                System.err.println("key=" + k.getSimpleName());
                v.forEach(o -> {
                    System.err.println("   --- " + o.getClass().getName());
                });
            });
            Object obj = dockBorderPane1.getTargetContext().getLookup().lookup(IndicatorDelegate.class);
            System.err.println("*** obj = " + obj);
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

}
