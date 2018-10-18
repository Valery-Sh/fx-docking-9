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

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.ButtonTypeComboBoxPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.ButtonTypeListPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestButtonTypeComboBoxPropertyEditor extends Application {

    boolean updating;

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Print ObservableList");
        Button btn2 = new Button("Button btn2");
        Button btn3 = new Button("Empty List");
        Button btn4 = new Button("add Empty String");

        ObservableList ol = FXCollections.observableArrayList();
        System.err.println("isInstance = " + (ObservableList.class.isAssignableFrom(btn1.getStyleClass().getClass())));
        if (true) {
            //return;
        }
        //System.err.println("lp." + lp.getName());

        GridPane grid = new GridPane();
        grid.setHgap(10);

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");
        ObservableList<ButtonType> list = FXCollections.observableArrayList();

        //list.add(null);
        //System.err.println("list.size=" + list.size() + "; item = " + list.get(0));
        ButtonTypeComboBoxPropertyEditor tf1 = new ButtonTypeComboBoxPropertyEditor("buttonType");

        //tf1.bindBidirectional(list);

        btn1.setOnAction(e -> {
            list.forEach(s -> {
                System.err.println("list item = " + s);
            });
        });

        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });

        btn3.setOnAction(e -> {
            list.clear();
        });
        btn4.setOnAction(e -> {
        });

        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        grid.add(lb2, 0, 2);
        grid.add(btn3, 0, 3);
        grid.add(btn4, 0, 4);
        TextField textField = new TextField("AAA");
        grid.add(textField, 0, 5);

//        grid.add(tf2, 1, 2);
        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);

//        grid.getColumnConstraints().addAll(cc0, cc1);
        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Stage stage1 = new Stage();
        stage1.initOwner(stage);

        VBox vbox = new VBox(btn1, btn2);
        VBox propPane = new VBox();
        TilePane tilePane = new TilePane();
        propPane.setStyle("-fx-border-width: 2; -fx-border-color: green");
        vbox.getChildren().add(propPane);
        propPane.getChildren().add(tilePane);

        StackPane contentPane = new StackPane();
        propPane.getChildren().add(contentPane);
        contentPane.setStyle("-fx-border-width: 2; -fx-border-color: blue");
        Button propBtn = new Button("Properties");
        Button layoutBtn = new Button("Layout");
        Button codeBtn = new Button("Code");
        tilePane.getChildren().addAll(propBtn, layoutBtn, codeBtn);
        //
        // Properties Category
        //
        TitledPane propTitledPane1 = new TitledPane();
        propTitledPane1.setText("Node");

        TitledPane propTitledPane2 = new TitledPane();
        propTitledPane2.setText("JavaFx CSS");
        TitledPane propTitledPane3 = new TitledPane();
        propTitledPane3.setText("Extras");
        VBox propSecBox = new VBox(propTitledPane1, propTitledPane2, propTitledPane3);
        contentPane.getChildren().add(propSecBox);

        TitledPane layoutTitledPane1 = new TitledPane();
        layoutTitledPane1.setText("Content");
        TitledPane layoutTitledPane2 = new TitledPane();
        layoutTitledPane2.setText("Internals");
        VBox layoutSecBox = new VBox(layoutTitledPane1, layoutTitledPane2);
        contentPane.getChildren().add(layoutSecBox);
        layoutSecBox.setVisible(false);

        TitledPane codeTitledPane1 = new TitledPane();
        codeTitledPane1.setText("onAction");
        VBox codeSecBox = new VBox(codeTitledPane1);
        contentPane.getChildren().add(codeSecBox);
        codeSecBox.setVisible(false);

        propBtn.setDisable(true);

        propBtn.setOnAction(e -> {
            propBtn.setDisable(true);
            propSecBox.setVisible(true);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        layoutBtn.setOnAction(e -> {
            layoutBtn.setDisable(true);
            layoutSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        codeBtn.setOnAction(e -> {
            codeBtn.setDisable(true);
            codeSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
        });

        Scene scene1 = new Scene(vbox);
        stage1.setScene(scene1);

//        stage1.show();
        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
//        pc.show(stage, 20, 2);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        //Dockable.initDefaultStylesheet(null);
        //URL u = ButtonTypeComboBoxPropertyEditor.class.getResource("resources/empty-style.css");
        //StyleManager.getInstance().addUserAgentStylesheet(u.toExternalForm());
        //scene.getStylesheets().add(ButtonTypeComboBoxPropertyEditor.class.getResource("resources/empty-style.css").toExternalForm());
        //System.err.println("R = " + getClass().getResource("resources/emptystyle.css").toExternalForm());
        //scene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

        //scene.getStylesheets().add(ButtonTypeComboBoxPropertyEditor.class.getResource("resources/styles/default.css").toExternalForm());

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
