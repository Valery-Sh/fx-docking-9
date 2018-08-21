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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.ErrorDialog;

/**
 *
 * @author Valery
 */
public class TestErrorDialog extends Application {

    private ErrorDialog errorDialog;

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        errorDialog = new ErrorDialog();
        Button btn1 = new Button("Show Dialog");
        Button btn2 = new Button("Hide Dialog");

        Pane root = new VBox(btn1, btn2);
        //Label lb1 = new Label("Text Alignment");
        //StringListTextField tf1 = new StringListTextField();
        //tf1.setValueIfBlank("blank");
        btn1.setOnAction(e -> {
            errorDialog.setValidator(item -> {
                return item.startsWith("-fx-");
            });
            errorDialog.show(stage, "-fx-bbb", "Error item found. ","\nYou can try to fix an error and press 'Ok' button. If you press 'Cancel' then the item will be lost");
            System.err.println("erroDialog result = " + errorDialog.getResult());
        });
        btn2.setOnAction(e -> {
            errorDialog.hide();
        });        
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Test ErrorDialog");
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
