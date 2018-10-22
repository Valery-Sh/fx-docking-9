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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.EffectChildPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.CompositePropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.EffectTreePaneItem;
import org.vns.javafx.dock.api.designer.bean.editor.TitledPaneItem;

/**
 *
 * @author Valery
 */
public class TestCompositePropertyEditor extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        Button btn3 = new Button("ExpandTitledPane");

        EffectChildPropertyEditor rootEditor = new EffectChildPropertyEditor("effect");
        EffectTreePaneItem effectTreeItem = new EffectTreePaneItem("effect");        
        rootEditor.bindBidirectional(btn1.effectProperty());
        
        VBox vbox = new VBox(btn1, btn2, rootEditor,  btn3, effectTreeItem );

        StackPane root = new StackPane(vbox);
        btn1.setOnAction(e -> {
            rootEditor.getTextButton().setText("Bloom");
        });
        btn2.setOnAction(e -> {
   
        });
        btn3.setOnAction(e -> {
        });
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
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

    public static class TitledPaneEx extends TitledPane {
//        StackPane p = (StackPane) titledPane.lookup(".title");
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
