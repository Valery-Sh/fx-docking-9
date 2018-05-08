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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestCommon extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button b1 = new Button("Button b1");
        Button b2 = new Button("Botton b2");        
        Label lb1 = new Label("Label lb1");
        lb1.setFocusTraversable(true);
        
        HBox hbox = new HBox(b2,lb1);
        hbox.getStyleClass().add("test-hbox");
        hbox.setFocusTraversable(true);
        hbox.setStyle("-fx-background-color: -fx-selection-bar; -fx-border-width: 20; -fx-border-color: -fx-outer-border");     
        
        VBox root = new VBox(hbox, b1);
        //root.setStyle("-fx-background-color: YELLOW");

        hbox.setOnMouseClicked(e -> {
            System.err.println("is hbox focused = " + hbox.isFocused());
            System.err.println("is b1 focused = " + b1.isFocused());
            Background bg = hbox.getBackground();
            bg.getFills().forEach(f -> {
                System.err.println("fx-background fill = " + f.getFill());
                System.err.println("   --- Radii = " + f.getRadii());
                System.err.println("   --- insets = " + f.getInsets());
            });
            hbox.getCssMetaData().forEach(s -> {
                System.err.println("Stylable = " + s);
            });
            //hbox.setStyle("-fx-min-height: 600");
            
        });
        Scene primaryScene = new Scene(root);
 
        
        primaryStage.setTitle("JavaFX TestCommon");
        primaryStage.setScene(primaryScene);

        primaryStage.setOnShown(s -> {
            
        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
       
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        primaryScene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
