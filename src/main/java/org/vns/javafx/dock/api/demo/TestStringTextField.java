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
import org.vns.javafx.dock.api.designer.bean.editor.BooleanPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.StringTextField;

/**
 *
 * @author Valery
 */
public class TestStringTextField extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

      
        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");
        TextField textField = new TextField("234");
        StringTextField tf1 = new StringTextField();
        System.err.println("@@@@@ getText = " + tf1.getText());
        tf1.setEditable(false);
        tf1.setText("txt1");
            System.err.println("1 START: getText() = " + tf1.getText());
            System.err.println("1 START: formatter.getValue() = " + tf1.getTextFormatter().getValue());
        //tf1.setText(null);
            System.err.println("2 START: getText() = " + tf1.getText());
            System.err.println("2 START: formatter.getValue() = " + tf1.getTextFormatter().getValue());
        //tf1.setText(null);
            System.err.println("3 START: getText() = " + tf1.getText());
            System.err.println("3 START: formatter.getValue() = " + tf1.getTextFormatter().getValue());        
/*        tf1.getValidators().add(item -> {
            return item != null && !item.equals("5");
        });
        //tf1.setNullSubstitution("<NULL>");

        tf1.getFilterValidators().add(item -> {
            return  item != null && ! item.isEmpty();
        });
*/            
        //tf1.setText("6");
        //tf1.setText(null);
        //tf1.setText(null);
        
        Platform.runLater(() -> {
            System.err.println("LATER: getText() = " + tf1.getText());
            System.err.println("LATER: formatter.getValue() = " + tf1.getTextFormatter().getValue());
        });
        //tf1.setText(null);
        /*         tf1.setText("1");
        tf1.setText("1");
        tf1.setText(null);
        tf1.setText("2");
         */

        btn1.setOnAction(e -> {
            System.err.println("--------------");
            System.err.println("text = " + tf1.getText());
            System.err.println("value = " + tf1.getTextFormatter().getValue());
            System.err.println("--------------");
            tf1.setText(null);
            //tf1.setText("321");
            if (tf1.getNullSubstitution() == null) {
                tf1.setNullSubstitution("<NULL>");
            }
            tf1.setText("444");
            tf1.setText(tf1.getNullSubstitution());
            tf1.setText(null);
            //tf1.setText(null);
            //tf1.setText("123");

        });

        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
//        SimpleStringPropertyEditor tf2 = new SimpleStringPropertyEditor("1234");

//        tf2.setFont(new Font(13));
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });
        Label lb3 = new Label("lable 3");
        lb3.setFont(new Font(13));

        Label elb = new Label("errors");
        HBox ehb = new HBox();
        ehb.setStyle("-fx-background-color: aqua");
        Circle shape = new Circle(2, Color.RED);
        shape.setManaged(false);
        ehb.getChildren().add(shape);

        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        grid.add(elb, 0, 1);
        grid.add(ehb, 1, 1);
        grid.add(lb2, 0, 2);
        grid.add(textField, 0, 3);
        textField.setEditable(false);
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
