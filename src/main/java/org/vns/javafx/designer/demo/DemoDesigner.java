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
package org.vns.javafx.designer.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
import org.vns.javafx.designer.SceneGraphView;

/**
 *
 * @author Valery
 */
public class DemoDesigner extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DockPane rootDockPane = new DockPane();
        StackPane root = new StackPane(rootDockPane);
        SceneGraphView sceneGraphView = DesignerLookup.lookup(SceneGraphView.class);
        sceneGraphView.setPrefHeight(1000);
        //sceneGraphView.setOpacity(0.2);
        DockNode formDockNode = new DockNode("Form Designer");
        StackPane formPane = new StackPane();
        formPane.setStyle("-fx-background-color: yellow");
        
        
        formDockNode.setContent(formPane);
        LayoutContextFactory ctxFactory = new LayoutContextFactory();
        LayoutContext ctx = ctxFactory.getContext(formPane);
        System.err.println("ctx=" + ctx);
        DockRegistry.makeDockLayout(formPane, ctx);
        
        //sceneGraphView.setRoot(formPane);
        
        sceneGraphView.rootProperty().addListener( (v, ov, nv) -> {
            if ( nv != null ) {
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
