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

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.FontPane;

/**
 *
 * @author Valery
 */
public class TestDialog extends Application {

    Scene scene;

    @Override
    public void start(Stage stage) {

        StackPane root = new StackPane();

        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        boolean disabled = false; // computed based on content of text fields, for example
        dialog.getDialogPane().lookupButton(loginButtonType).setDisable(disabled);
        dialog.initModality(Modality.NONE);
        //root.getChildren().add(dialog);
        dialog.show();
        //if (result.isPresent() && result.get() == ButtonType.OK) {
            System.err.println("RESULT");
        //}
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

}
