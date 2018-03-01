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

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.PalettePane;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.DockTabPane2Context;
import org.vns.javafx.dock.api.DockTabPane2MouseDragHandler;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery
 */
public class TestTabPane extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        Button btn1 = new Button("btn1");
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
        TabPane tabPane = new TabPane(tab1, tab2, tab3);
        DockRegistry.getInstance().registerAsDockLayout(tabPane, new DockTabPane2Context(tabPane) );
        DockRegistry.getInstance().getDefaultDockable(tabPane);
        DockableContext dc = Dockable.of(tabPane).getContext();
        dc.setLayoutContext(DockLayout.of(tabPane).getLayoutContext());
        
        System.err.println("++++ dc.getTargetContext = " + dc.getLayoutContext());
        dc.setDragNode(tabPane);
        DockTabPane2MouseDragHandler dragHandler = new DockTabPane2MouseDragHandler(dc);
        dc.getLookup().putUnique(MouseDragHandler.class, dragHandler);
                
        //tabPane.
        root.getChildren().add(tabPane);
        
        tabPane.setOnMousePressed(ev -> {
            System.err.println("***** " + tabPane.lookup(".tab-1"));    
            tabPane.lookupAll(".tab").forEach(t -> {
                System.err.println("PROP " + t.getProperties().get("key1"));
                System.err.println("StylableParent" + tab1.getStyleableParent());
                
            });
            if ( true ) return;
            System.err.println("o - " + tabPane.getSelectionModel().isSelected(0));
            System.err.println("1 - " + tabPane.getSelectionModel().isSelected(1));
            System.err.println("=================================================");
            tabPane.setStyle("-fx-background-color: aqua");
            System.err.println("TabPane: stayleClass = " + tabPane.getStyleClass().get(0));
            System.err.println("   --- layoutBounds = " + tabPane.getLayoutBounds());            
            System.err.println("=================================================");
            
            ((TabPaneSkin) tabPane.getSkin()).getChildren().forEach(n -> {
                String h = "";
                String s = "";
                if (n.getClass().getName().endsWith("TabContentRegion")) {
                    h = "TabContentRegion";
                    s = n.getStyleClass().get(0);
                } else if (n.getClass().getName().endsWith("TabHeaderArea")) {
                    h = "TabHeaderArea";
                    s = n.getStyleClass().get(0);
                    
                }
                System.err.println("ClassName: " + h + " : styleClass = " + s);
                System.err.println("   --- layoutBounds = " + n.getLayoutBounds());
                
                if (n instanceof Pane) {
                    
                    ((Pane) n).getChildren().forEach(nc -> {
                        String h1 = "";
                        String s1 = "";
                        s1 = nc.getStyleClass().get(0);
                        if (nc.getClass().getName().endsWith("TabHeaderArea$1")) {
                            h1 = "          ClassName: TabHeaderArea$1.";
                        } else if (getClass().getName().endsWith("TabControlButtons")) {
                            h1 = "          ClassName TabControlButtons:";
                        } else {
                            h1 = "          ClassName " + nc.getClass().getSimpleName();
                        }
                        
                        System.err.println(h1 + " stylClass = " + s1);
                        System.err.println("            --- layoutBounds = " + nc.getLayoutBounds());
                        System.err.println("            --- boundsInParent = " + nc.getBoundsInParent());
                        if (nc instanceof Pane) {
                            ((Pane) nc).getChildren().forEach(tabHeaderSkin -> {
                                String h2 = "";
                                  String s2 = "";
                                  s2 = tabHeaderSkin.getStyleClass().get(0);
                                if (tabHeaderSkin.getClass().getName().endsWith("TabHeaderSkin")) {
                                    h2 = "                ClassName: TabHeaderSkin:";
                                } else {
                                    h2 = "                ClassName: " + nc.getClass().getSimpleName();
                                }

                                System.err.println(h2 + " : styleClass = " + s2 );

                                System.err.println("            --- tabHeaderSkin = " + tabHeaderSkin);
                                System.err.println("            --- tabHeaderSkin.layoutBounds = " + tabHeaderSkin.getLayoutBounds());
                                System.err.println("            --- tabHeaderSkin.localToParent = " + tabHeaderSkin.localToParent(tabHeaderSkin.getLayoutBounds()));
                                System.err.println("            --- tabHeaderSkin.getBoundsParent = " + tabHeaderSkin.getBoundsInParent());
                                System.err.println("            ---------------------------------------------------");                                
                            });
                        }
                    });
                }
            });

        });
        PalettePane palettePane = new PalettePane(true);
/*        palettePane.setDragValueCustomizer(o -> {
            if (o instanceof Tab) {
                ((Tab) o).setText("tab01");
                System.err.println("TAB = " + ((Tab)o).getText());
            }
        });        
*/        
        Scene pscene = new Scene(palettePane);
        Stage pstage = new Stage();
        pstage.setScene(pscene);
        
        Scene scene = new Scene(root);    
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
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
        
/*        tabPane.lookup(".tab-header-background").setOnMousePressed(ev -> {
            System.err.println("tab-header-background Mouse Pressed");
        });
*/        
        System.err.println("tabContent.size = " + tabPane.lookupAll(".tab-content-area").size());
        
        System.err.println("Tabs size()= " + tabPane.lookupAll(".tab").size());
         tabPane.lookupAll(".tab").forEach(t -> {
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
