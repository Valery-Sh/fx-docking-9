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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.BeanEditorCategoryPane;
import org.vns.javafx.dock.api.designer.bean.editor.BooleanPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.StringTextField;

/**
 *
 * @author Valery
 */
public class TestBeanEditorCategoryPane extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        
        BeanEditorCategoryPane beanPane = new BeanEditorCategoryPane();
      
        VBox vbox = new VBox(btn1,btn2,beanPane);
        
        StackPane root = new StackPane(vbox);
        btn1.setOnAction(e -> {
            //System.err.println("beanPane.getChildren().size() = " + beanPane.getChildren().size());
            //System.err.println("class = " + beanPane.getChildren().get(0).getClass().getSimpleName());
        });
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });
        
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        System.err.println("R = " + getClass().getResource("resources/demo-styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

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
