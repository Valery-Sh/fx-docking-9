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

import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockStateLoader;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableController;

/**
 *
 * @author Valery
 */
public class TestStageManage extends Application {

    Stage stage;
    Scene scene;
    Scene scene1;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        Button btn1 = new Button("Primary Stage Btn1");
        Button btn1_1 = new Button("Primary Stage Btn1_1");
        
        StackPane stackPane = new StackPane(btn1);
        
        HBox root = new HBox(stackPane);
        
        Scene scene = new Scene(root, 200,200);
        stage.setScene(scene);
        Stage stage1 = new Stage();
        stage1.setTitle("Stage 1");
        
        Button btn2 = new Button("Stage1 Btn2");        
        StackPane stackPane1 = new StackPane(btn2);
        HBox root1 = new HBox(stackPane1);
        scene1 = new Scene(root1,200, 200);
        stage1.setScene(scene1);
        //stage.setAlwaysOnTop(true);
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            StackPane stackPane2 = new StackPane();
            HBox root2 = new HBox(stackPane2);
            stackPane2.getChildren().add(btn1);
            stage1.show();
            //stage.requestFocus();
//            StackPane stackPane2 = new StackPane();
//            stackPane2.getChildren().addAll(stackPane1.getChildren().get(0));//,stackPane1.getChildren().get(1));
//            HBox root2 = new HBox(stackPane2);
//            Scene scene2 = new Scene(root2,200, 200);
//            stage1.setScene(scene2);            
            scene1.setRoot(root2);
            //btn1_1.requestFocus();
            
        });
        stage.setX(100);
        stage1.setX(500);
        stage.show();
        stage1.show();
        //stage1.hide();
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static class CustomButton extends Button {

        public CustomButton() {
        }

        public CustomButton(String text) {
            super(text);
        }

        public CustomButton(String text, Node graphic) {
            super(text, graphic);
        }

        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
    }
}
