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

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.LayoutContextFactory;

import org.vns.javafx.dock.api.PalettePane;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.SceneGraphView;

/**
 *
 * @author Valery
 */
public class DemoDesigner1 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setAlwaysOnTop(true);
        DockPane rootDockPane = new DockPane();
        rootDockPane.setUsedAsDockLayout(false);
        StackPane root = new StackPane(rootDockPane);
        root.setId("mainStage " + root.getClass().getSimpleName());

        SceneGraphView sceneGraphView = DesignerLookup.lookup(SceneGraphView.class);
        sceneGraphView.setPrefHeight(1000);
        //sceneGraphView.setOpacity(0.2);
        DockNode formDockNode = new DockNode("Form Designer");
        Button formButton = new Button("CLICK");

        StackPane formPane = new StackPane();
        formPane.setStyle("-fx-background-color: yellow");
        formPane.getChildren().add(formButton);
        formDockNode.setContent(formPane);
        LayoutContextFactory ctxFactory = new LayoutContextFactory();
        LayoutContext ctx = ctxFactory.getContext(formPane);
        System.err.println("ctx=" + ctx);
        DockRegistry.makeDockLayout(formPane, ctx);
        VBox root1 = new VBox();
        
        sceneGraphView.setRoot(root1);
        
        StackPane sp = new StackPane(root1);
        root1.setStyle("-fx-background-color: white;");
        //rightPaneRoot.setStyle("-fx-background-color: SIENNA; -fx-padding: 10 10 10 10");
        sp.setStyle("-fx-background-color: SIENNA; -fx-padding: 20 20 20 20");        
        Scene scene1 = new Scene(sp);
        //sceneGraphView.getRootLayout().getChildren().add(root1);
        //Scene scene1 = new Scene(sceneGraphView.getRootLayout());
        formButton.setOnAction(a -> {
            ((Region)sceneGraphView.getRoot()).requestLayout();
            DockRegistry.getWindows().forEach(w -> {
/*                System.err.println("******************** w = " + w.getScene().getRoot().getId());
                System.err.println("******************** w = " + w.getScene().getRoot().isVisible());
                System.err.println("******************** w.isShowing() = " + w.isShowing());
                System.err.println("DockRegistry FOCUSED WIN.bounds = " + w.getScene().getRoot().localToScreen(w.getScene().getRoot().getBoundsInLocal()));

                System.err.println("=============================================");
*/
                
            });

        });

        root1.setId("root1 " + root.getClass().getSimpleName());

        Stage stage1 = new Stage();
        stage1.setAlwaysOnTop(true);
        stage1.setWidth(200);
        stage1.setHeight(200);
        stage1.setScene(scene1);
        stage1.initOwner(stage);
        scene1.setOnZoom(value -> {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!! ZOOOO MED");
        });
     
        //Rectangle rect = new Rectangle(50,25);

        sceneGraphView.rootProperty().addListener((v, ov, nv) -> {
            if (nv != null) {
                System.err.println("DemoDesigner: rootChanged");
                formDockNode.setContent(nv);
            }
        });

        DockSideBar sgvDockSideBar = new DockSideBar();
        sgvDockSideBar.setOrientation(Orientation.VERTICAL);
        sgvDockSideBar.setRotation(DockSideBar.Rotation.DOWN_UP);
        sgvDockSideBar.setSide(Side.RIGHT);
        sgvDockSideBar.setHideOnExit(false);

        DockNode sgvDockNode = new DockNode(" Hierarchy ");

        sgvDockNode.setContent(sceneGraphView);
        sgvDockSideBar.getItems().add(Dockable.of(sgvDockNode));

        PalettePane palettePane = DesignerLookup.lookup(PalettePane.class);
        DockSideBar paletteDockSideBar = new DockSideBar();
        /*        sgvDockSideBar.getLookup().putUnique(FloatViewFactory.class, new FloatViewFactory() {
            public FloatView getFloatView(Dockable d) {
                return new FloatPopupControlView2(d);
            }
        });
         */
        paletteDockSideBar.setOrientation(Orientation.VERTICAL);
        paletteDockSideBar.setRotation(DockSideBar.Rotation.UP_DOWN);
        paletteDockSideBar.setSide(Side.LEFT);

        DockNode palleteDockNode = new DockNode(" Palette ");
        palleteDockNode.setContent(palettePane);
        paletteDockSideBar.getItems().add(Dockable.of(palleteDockNode));

        rootDockPane.dock(formDockNode, Side.TOP);
        rootDockPane.dock(sgvDockSideBar, Side.LEFT);
        rootDockPane.dock(paletteDockSideBar, Side.RIGHT);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setHeight(400);
        stage.setWidth(500);

        stage.show();
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
    
}
