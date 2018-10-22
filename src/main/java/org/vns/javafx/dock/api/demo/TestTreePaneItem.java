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
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.bean.editor.EffectTreePane;

import org.vns.javafx.dock.api.designer.bean.editor.HiddenTitledPane;
import org.vns.javafx.dock.api.designer.bean.editor.TreePane;
import org.vns.javafx.dock.api.designer.bean.editor.TreePaneItem;

/**
 *
 * @author Valery
 */
public class TestTreePaneItem extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        Button btn3 = new Button("ExpandTitledPane");
        Button btn4 = new Button("add child");
        
        HiddenTitledPane tp = new HiddenTitledPane("tp1");
        tp.setExpanded(true);
        VBox tpc = tp.getContent();
        Button b1 = new Button("b1");
        Button b2 = new Button("b2");
        Button b3 = new Button("b3");
        Button b4 = new Button("b4");
        
        Button b5 = new Button("b5");
        
        tpc.getChildren().addAll(b1,b2,b3,b4);
        HiddenTitledPane tp2 = new HiddenTitledPane("tp2");
        //tpc.getChildren().add(tp2);
        
        HiddenTitledPane tp3 = new HiddenTitledPane("tp3");
        
        EffectTreePane treePane = new EffectTreePane();
        treePane.getValuePane().getChildren().add(new Label("TreePane. Test Value pane"));
        TreePaneItem treePaneItem = new TreePaneItem("Titled Pane");
        //treePane.setRoot(treePaneItem);
//        treePaneItem.getTitledPane().setId("root");
        TreePaneItem child0 = new TreePaneItem("Child0");
        
//        child0.getTitledPane().setId("child0");
        treePaneItem.getChildItems().add(child0);
        treePane.getChildItems().add(treePaneItem);
        
        //System.err.println("1 TreePane = " + child0.getTreePane());
        child0.getParentItem();
        int lev = child0.getLevel();

        
        VBox vbox = new VBox(btn4,btn1, btn2, treePane, btn3 );
        //VBox vbox = new VBox(btn4,btn1, btn2, treePaneItem, btn3 );
        //VBox vbox = new VBox(btn4,btn1, btn2, tp, btn3 );
        System.err.println("vbox.getStyleClass() = " + vbox.getStyleClass());
        vbox.getStyleClass().add("vbox");
        StackPane root = new StackPane(vbox);
        btn1.setOnAction(e -> {
        });
        btn2.setOnAction(e -> {
        });
        btn3.setOnAction(e -> {
            System.err.println("treePaneItem.getChildItems().size= " + treePaneItem.getChildItems().size());
        });
        btn4.setOnAction(e -> {
/*            if ( tp.isExpanded() ) {
                tp.setExpanded(false);
            } else {
                tp.setExpanded(true);
            }
*/            
        //    ((VBox)treePaneItem.getTitledPane().getContent()).getChildren().add(new TreePaneItem("child0.0"));
/*            if ( tpc.getChildren().contains(b5) ) {
                tpc.getChildren().remove(b5);
            } else {
                tpc.getChildren().add(b5);
            }
*/            
            //double spacing = treePaneItem.getTitledPane().getContent().getSpacing();
            
            TreePaneItem child = new TreePaneItem("child0.0");
            child.getValuePane().getChildren().add(new Label("Test Value pane"));
            treePaneItem.getChildItems().add(child);
            System.err.println("1 TreePane = " + child.getTreePane());
            
            //System.err.println("SPACING = " + spacing);
/*            child0.getChildItems().add(new TreePaneItem("child0.0"));
            ((Pane)child0.getTitledPane().getChildren().get(0)).setPrefHeight(-1);
            ((Pane)child0.getTitledPane().getChildren().get(0)).setMinHeight(-1);
            ((Pane)child0.getTitledPane().getChildren().get(0)).setMaxHeight(-1);
            Platform.runLater(() -> {
                System.err.println("SIZE = " + child0.getChildItems().size());
                System.err.println("SIZE1 = " + ((VBox)child0.getTitledPane().getContent()).getChildren().size());
            });
  */          
        });
        
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        scene.getStylesheets().add(DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm());
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

    public static class TitledPaneEx extends TitledPane {
//        StackPane p = (StackPane) treePaneItem.lookup(".title");
        //p.setManaged(false);
        //p.setMaxHeight(0);
        //p.setMinHeight(0);
//        p.setPrefHeight(0);
//        p.getChildren().clear();

        public TitledPaneEx() {
        }

        public TitledPaneEx(String title, Node content) {
            super(title, content);
            this.setBorder(Border.EMPTY);
            this.setLineSpacing(0);
            //this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }

        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
        private boolean layoutDone = false;

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            StackPane p = (StackPane) lookup(".title");
            if (p != null && !layoutDone) {
                System.err.println("LAYOUT");
                p.setPrefHeight(0);
                p.getChildren().clear();
                layoutDone = true;
            }
        }
    }
}
