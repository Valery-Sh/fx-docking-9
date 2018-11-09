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

import java.util.regex.Pattern;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
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
import org.vns.javafx.scene.control.editors.BooleanPropertyEditor;
import org.vns.javafx.scene.control.editors.ErrorMarkerBuilder;
import org.vns.javafx.scene.control.editors.PrimitivePropertyEditor.IntegerPropertyEditor;
import org.vns.javafx.scene.control.editors.StringTextField;

/**
 *
 * @author Valery
 */
public class TestIntegerStringPropertyEditor extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

        long start1 = System.currentTimeMillis();
        Pane p = new Pane();
        long end1 = System.currentTimeMillis();
        //System.err.println("DIF0 = " + (end1 - start1));


        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");
        //lb1.getStyleClass().add("str");
        //lb1.setFont(new Font(13));
        //System.err.println("font size lb1.getFont().getSize()= " + lb1.getFont().getSize());
        //SliderEditor tf1 = new SliderEditor(0,1,1);
        //DecimalTextField tf1 = new DecimalTextField();
        btn2.setOpacity(12.375);
        
        IntegerProperty ip = new SimpleIntegerProperty(27);
        IntegerPropertyEditor tf1 = new IntegerPropertyEditor();
        tf1.bindBidirectional(ip);
//        System.err.println("IntegerPropertyEditor value=" + tf1.getLastValidText());
        btn1.setOnAction(e -> {
            //tf1.setLastValidValue("21");
            System.err.println("btn2.getOpacity = " + btn2.getOpacity());
            //System.err.println("IntegerPropertyEditor ip=" + ip.get());
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


        BooleanPropertyEditor tf3 = new BooleanPropertyEditor();


        grid.add(lb3, 0, 3);
        grid.add(tf3, 1, 3);
        tf3.bind(btn1.disableProperty());
        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);

        grid.getColumnConstraints().addAll(cc0, cc1);
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

        stage1.show();

        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
        pc.show(stage, 20, 2);

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
