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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PalettePane;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.SceneGraphView;
import org.vns.javafx.dock.api.designer.TreeItemBuilder;
import org.vns.javafx.dock.api.designer.TreeItemEx;
import org.vns.javafx.dock.api.designer.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.bean.ReflectHelper;

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
        
        DockNode formDockNode = new DockNode("Form Designer");
        StackPane formPane = new StackPane();
        formDockNode.setContent(formPane);
        
        sceneGraphView.setRoot(formPane);
        DockSideBar sgvDockSideBar = new DockSideBar();
        sgvDockSideBar.setOrientation(Orientation.VERTICAL);
        sgvDockSideBar.setRotation(DockSideBar.Rotation.DOWN_UP);
        sgvDockSideBar.setSide(Side.LEFT);
        sgvDockSideBar.setHideOnExit(false);
        
        DockNode sgvDockNode = new DockNode(" Hierarchy ");
        
        sgvDockNode.setContent(sceneGraphView);
        sgvDockSideBar.getItems().add(Dockable.of(sgvDockNode));
                
        PalettePane palettePane = DesignerLookup.lookup(PalettePane.class);
        DockSideBar paletteDockSideBar = new DockSideBar();
        paletteDockSideBar.setOrientation(Orientation.VERTICAL);
        paletteDockSideBar.setRotation(DockSideBar.Rotation.UP_DOWN);
        paletteDockSideBar.setSide(Side.RIGHT);
        
        DockNode palleteDockNode = new DockNode(" Palette ");
        palleteDockNode.setContent(palettePane);
        paletteDockSideBar.getItems().add(Dockable.of(palleteDockNode));
        
       
        rootDockPane.dock(formDockNode, Side.TOP);
        rootDockPane.dock(sgvDockSideBar, Side.LEFT);
        rootDockPane.dock(paletteDockSideBar, Side.RIGHT);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        stage.setHeight(600);
        stage.setWidth(800);
        

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
