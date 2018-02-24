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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PalettePane;

/**
 *
 * @author Valery
 */
public class TestTargetContextFactory extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        Button btn1 = new Button("btn1");
        root.setId("vbox1");
        root.getChildren().add(btn1);

        Tab tab1 = new Tab("Tab1");
        Button tab1Btn1 = new Button("tab1Btn1");
        tab1.setContent(tab1Btn1);
        tab1.getProperties().put("key1", "MyKey");
        tab1.setId("tb1");
        tab1.getStyleClass().add("tab-1");        
        
        Tab tab2 = new Tab("Tab2_1_1");
        tab2.setId("tb2");
        Label tab2Lb1 = new Label("tab2Lb1");
        tab2.setContent(tab2Lb1);

        Tab tab3 = new Tab("Tab3");
        tab3.setId("tb3");
        Label tab3Lb1 = new Label("tab3Lb1");
        tab3.setContent(tab3Lb1);        
        VBox pane = new VBox(tab3Lb1);
        root.getChildren().add(pane);
//        DockRegistry.getInstance().registerAsDockTarget(pane, new DockTabPane2Context(pane) );
        DockRegistry.getInstance().registerAsDockTarget(pane);
        //TargetContextFactory f = new TargetContextFactory();
        
        
        PalettePane palettePane = new PalettePane(true);
        
        PalettePane.DragValueCustomizer customizer = new PalettePane.DragValueCustomizer() {
            @Override
            public void customize(Object value) {
                if ( value instanceof Node ) {
                    DockRegistry.makeDockable((Node) value);
                }
/*                if ( value instanceof Rectangle) {
                    Rectangle shape = (Rectangle) value;
                    shape.setWidth(75);
                    shape.setHeight(24);
                    
                }
*/                
            }
        };
        
        palettePane.getModel().setDragValueCustomizer(customizer);
        VBox pvbox = new VBox(palettePane);
        Scene pscene = new Scene(pvbox);
        pvbox.setId("pvbox");
        Button dBtn = new Button("To Drag");
        DockRegistry.makeDockable(dBtn);
        pvbox.getChildren().add(dBtn);
        Stage pstage = new Stage();
        pstage.setScene(pscene);
        
        Scene scene = new Scene(root);    
        stage.setScene(scene);
        
        stage.setAlwaysOnTop(true); //!!!!!
        
        stage.show();
        stage.setX(10);
        stage.setY(100);
        pstage.show();
        pstage.setX(400);
        pstage.setY(100);
        pstage.setHeight(300);
        pstage.setAlwaysOnTop(true);
        pstage.show();
        
        VBox root1 = new VBox();
        
        Stage stage1 = new Stage();

        stage1.setAlwaysOnTop(true);
        btn1.setOnAction(a -> {
            if ( stage1.isShowing() ) {
                System.err.println("root1.getScene = " + root1.getScene());
                System.err.println("root1.getScene.getWindow = " + root1.getScene().getWindow());
                stage1.close();
            } else {
                System.err.println("1 root1.getScene = " + root1.getScene());
                System.err.println("1 root1.getScene.getWindow = " + root1.getScene().getWindow());
            }
        });
        
        Label lb03 = new Label("Valery");
       
        //root1.getChildren().add(lb03);
        Scene scene1 = new Scene(lb03);        
        WritableImage wi = lb03.snapshot(null,null);
        ImageView iv = new ImageView(wi);
        
        root1.getChildren().add(iv);   
        
        
        stage1.setScene(scene1);
        
        //stage1.show();
        
/*        pane.lookup(".tab-header-background").setOnMousePressed(ev -> {
            System.err.println("tab-header-background Mouse Pressed");
        });
*/        
        System.err.println("tabContent.size = " + pane.lookupAll(".tab-content-area").size());
        
        System.err.println("Tabs size()= " + pane.lookupAll(".tab").size());
         pane.lookupAll(".tab").forEach(t -> {
             System.err.println("tab.bounds = " + t.getLayoutBounds());
             System.err.println("tab.id = " + t.getId());
             
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

}
