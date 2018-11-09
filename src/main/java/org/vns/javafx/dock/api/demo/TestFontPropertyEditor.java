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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.FontPane;
import org.vns.javafx.scene.control.editors.FontPropertyEditor;
import org.vns.javafx.scene.control.editors.Util;

/**
 *
 * @author Valery
 */
public class TestFontPropertyEditor  extends Application {

    Scene scene;
    double fontSize = 15;
    @Override
    public void start(Stage stage) {
        
        //System.err.println("VALUE OF " + Double.valueOf(10));
        Font font1 = Util.getFont("Amiri", "Bold Slanted", 10);
/*        System.err.println("FONT STYLE: " + Util.getFontStyle("Amiri Bold", "Slanted", 10));
        Font font1 = Font.font("Amiri", FontWeight.BOLD, FontPosture.ITALIC, 10);
        System.err.println("font name = " + font1.getFamily());
        System.err.println("font1 style = " + font1.getStyle());
        System.err.println("### P = " + FontPosture.findByName("Bold Slanted"));
        System.err.println("### W = " + FontWeight.findByName("Bold Slanted"));
*/        
        VBox root = new VBox();
        root.getStyleClass().add("font-editor");
        //Button btn1 = new Button("Edit Font, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10");
        Button btn1 = new Button("get styles");
        btn1.getStyleClass().add("button-shape");
        
        FontPropertyEditor tf = new FontPropertyEditor("minWidth");
        tf.bindBidirectional(btn1.fontProperty());
        
        root.setPrefSize(500, 70);
        root.getChildren().addAll(btn1, tf);
        
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.getScene().setRoot(new FontPane());
        System.err.println("btn1.getFont = " + btn1.getFont());
        btn1.setOnAction(a -> {
            System.err.println("tf.size = " + tf.size);
            System.err.println("btn1.getFont = " + btn1.getFont());
            if ( ! tf.isBound() ) {
                btn1.setFont(Font.font("Arial", 10));
                return;
            }
            tf.size.getTextField().setText(Double.toString(fontSize++));
            System.err.println("btn1 = " + btn1);
            System.err.println("   --- btn1.getFont = " + btn1.getFont());            
            if ( btn1.getFont().getSize() == 17) {
                tf.unbind();
                
            }
            System.err.println("tf.isBound = " + tf.isBound());
/*            double x = btn1.localToScreen(btn1.getBoundsInLocal()).getMinX();
            double y = btn1.localToScreen(btn1.getBoundsInLocal()).getMinY();
            popup.show(btn1,x,y + btn1.getHeight());
*/            
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
