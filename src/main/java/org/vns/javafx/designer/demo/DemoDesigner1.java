
/*
 * Copyright 2018 Your Organisation.
 *g
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
package org.vns.javafx.designer.demo;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

import org.vns.javafx.designer.PalettePane;
import org.vns.javafx.designer.DesignerLookup;
import org.vns.javafx.designer.SceneView;
import org.vns.javafx.designer.TrashTray;
import org.vns.javafx.dock.api.dragging.DragManager;

/**
 *
 * @author Valery
 */
public class DemoDesigner1 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Button tb = new Button();
        tb.toFront();
        if ( null instanceof Node) {
            System.err.println("NULL !!!!!!!");
        }
        System.err.println("");
        StageHelper.getStages().addListener((Observable c) -> {
            System.err.println("stages.size() = " + StageHelper.getStages().size());
        });
        
        Node n;

        Button b;

        stage.setAlwaysOnTop(true);
        DockPane rootDockPane = new DockPane();
        rootDockPane.setUsedAsDockLayout(false);
        StackPane root = new StackPane(rootDockPane);
        root.setId("mainStage " + root.getClass().getSimpleName());
        //DesignerLookup.putUnique(SceneGraphView.class, new SceneGraphView(root, true));
        DesignerLookup.putUnique(SceneView.class, new SceneView(true));
        SceneView sceneView = DesignerLookup.lookup(SceneView.class);
        sceneView.setPrefHeight(1000);
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
        //BorderPane root1 = new BorderPane();
        VBox root1 = new VBox();
        Button eb = new Button("Ext Button");
       

        VBox centerPane = new VBox(eb);
        //root1.setCenter(centerPane);
        //root1.setCenter(eb);
        //VBox root1 = new VBox();
        HBox hbox = new HBox(new Label("root1 Label"));
        //root1.getChildren().add(hbox);
        root1.setId("root1");
        sceneView.setRoot(root1);

        StackPane sp = new StackPane();
        //StackPane sp = new StackPane(root1);
        Scene scene1 = new Scene(root1);
        
        root1.setStyle("-fx-padding: 5 5 5 5");
        

        sceneView.rootProperty().addListener((v,ov,nv) -> {
         
        });
        
        root1.setStyle("-fx-background-color: white;-fx-padding: 5 5 5 5");
        //rightPaneRoot.setStyle("-fx-background-color: SIENNA; -fx-padding: 10 10 10 10");
        sp.setStyle("-fx-background-color: SIENNA; -fx-padding: 20 20 20 20");
        //Scene scene1 = new Scene(sp);
        
        sp.getChildren().addListener((Change<? extends Node> c) -> {
            while (c.next()) {
                if ( c.wasAdded() ) {
                    System.err.println("*** added size = " + c.getAddedSize());
                    for ( Node n1 : c.getAddedSubList()) {
                        System.err.println("   --- added node = " + n1);
                    }
                }
            }
        } );
        formButton.setOnAction(a -> {
/*            System.err.println("CLICKED CENTER ");
            Node nd = root1.getCenter();
            System.err.println("CLICKED CENTER scaleX     = " + nd.getScaleX());
            System.err.println("CLICKED CENTER translateX = " + nd.getTranslateX());
            System.err.println("eb.getInsets() = " + eb.getInsets());
            //eb.setFocusTraversable(false);
            if ( nd != null && nd.getScaleX() == 1 ) {
                nd.setScaleX(0.5);
                //if ( nd instanceof VBox) {
                if ( false) {                    
                    eb.setTranslateX(10);
                } else {
                    nd.setTranslateX(10);
                }
            } else if ( nd != null ) {
                nd.setScaleX(1);
                //if ( nd instanceof VBox) {
                if ( false) {                                    
                    eb.setTranslateX(0);
                } else {
                    nd.setTranslateX(0);
                }

            }
*/
        });

        root1.setId("root1 " + root.getClass().getSimpleName());

        Stage stage1 = new Stage();
        stage1.setAlwaysOnTop(true);
        stage1.setWidth(200);
        stage1.setHeight(200);
        stage1.setScene(scene1);
        stage1.initOwner(stage);
    

        DockSideBar sgvDockSideBar = new DockSideBar();
        sgvDockSideBar.setOrientation(Orientation.VERTICAL);
        sgvDockSideBar.setRotation(DockSideBar.Rotation.DOWN_UP);
        sgvDockSideBar.setSide(Side.RIGHT);
        sgvDockSideBar.setHideOnExit(false);

        DockNode sgvDockNode = new DockNode(" Hierarchy ");

        sgvDockNode.setContent(sceneView);
        sgvDockSideBar.getItems().add(Dockable.of(sgvDockNode));

        PalettePane palettePane = DesignerLookup.lookup(PalettePane.class);
        DockSideBar paletteDockSideBar = new DockSideBar();

        paletteDockSideBar.setOrientation(Orientation.VERTICAL);
        paletteDockSideBar.setRotation(DockSideBar.Rotation.UP_DOWN);
        paletteDockSideBar.setSide(Side.LEFT);

        DockNode palleteDockNode = new DockNode(" Palette ");
        palleteDockNode.setContent(palettePane);
        //palleteDockNode.getContext().getDragManager().getHideOption();
        palleteDockNode.getContext().getDragManager().setHideOption(DragManager.HideOption.CARRIER);
        System.err.println("dragManager.class = " + palleteDockNode.getContext().getDragManager().getClass().getSimpleName());
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

        TrashTray tray = DockRegistry.lookup(TrashTray.class);
        if (tray != null) {
            Stage trashStage = tray.show(stage);
            trashStage.toFront();
        }

        /*DockNode dn = new DockNode("Ext DockNoce");
        formPane.getChildren().add(dn);
        Button dnBtn = new Button("SHOW Layout");
        */
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        System.err.println("getUserAgent = " + Application.getUserAgentStylesheet());
        System.err.println("Show window children.size() = " + sp.getChildren().size());        
        //Dockable.initDefaultStylesheet(null);
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
