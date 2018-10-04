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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.FontPane;
import org.vns.javafx.dock.api.designer.bean.editor.FontPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.FontStringConverter;

/**
 *
 * @author Valery
 */
public class TestFontEditor1  extends Application {

    Scene scene;

    @Override
    public void start(Stage stage) {

        VBox root = new VBox();
        root.getStyleClass().add("font-editor");
        Button btn1 = new Button("Edit Font, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10");
        FontStringConverter conv = new FontStringConverter();
        String str = "Arial 10px (Regular)";
        
        Font font = conv.fromString(str);
        btn1.getStyleClass().add("button-shape");
        Rectangle sh = new Rectangle(0,0,8, 8);
        FontPropertyEditor tf = new FontPropertyEditor();
        
        Font f = Font.font("Verdana", FontWeight.LIGHT, FontPosture.ITALIC, 12);
        
        System.err.println("FONT = " + f);
        FontStringConverter  fontconv = new FontStringConverter();
        tf.getEditorButton().textProperty().bindBidirectional(btn1.fontProperty(), fontconv );
        
        System.err.println("FONT = " + btn1.getFont());
        
        root.setPrefSize(500, 70);
        root.getChildren().addAll(btn1, tf);
        
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.getScene().setRoot(new FontPane());
        btn1.setOnAction(a -> {
            double x = btn1.localToScreen(btn1.getBoundsInLocal()).getMinX();
            double y = btn1.localToScreen(btn1.getBoundsInLocal()).getMinY();
            popup.show(btn1,x,y + btn1.getHeight());
        });
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
