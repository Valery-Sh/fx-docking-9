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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.BooleanPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.ErrorMarkerBuilder;
import org.vns.javafx.dock.api.designer.bean.editor.IntegerListPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestIntegerListPropertyEditor extends Application {

   

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        String str = "";
        String[] a = str.split(",");
        for (int i=0; i < a.length; i++) {
            System.err.println(i + "). " + a[i]);
        }   
        
        ObservableList<Integer> plist = FXCollections.observableArrayList();
        plist.add(5);
        System.err.println("CONTAINS = "+ plist.contains(5));
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

        long start1 = System.currentTimeMillis();
        Pane p = new Pane();
        long end1 = System.currentTimeMillis();
        //System.err.println("DIF0 = " + (end1 - start1));

        Text msg = new Text("JavaFX animation is cool!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));
        //Pane root = new Pane(msg);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");

        lb1.setFont(new Font(13));
        //System.err.println("font size lb1.getFont().getSize()= " + lb1.getFont().getSize());
        //SliderEditor tf1 = new SliderEditor(0,1,1);
        //DecimalTextField tf1 = new DecimalTextField();
        
        
        IntegerListPropertyEditor tf1 = new IntegerListPropertyEditor();
        System.err.println("tf1.getErrorMarkerBuilder()= " + tf1.getErrorMarkerBuilder()); 
        tf1.setNullString("N");
        
        //tf1.setValueIfBlank("0");
        //tf1.setErrorMarkerBuilder(new ErrorMarkerBuilder(tf1));

/*        tf1.getValidators().add( item -> {
            boolean v = true;
                int n = Integer.parseInt(item.trim());
                if ( n < 0 || n > 100 ) {
                    v = false;
                }
            return v;
        });
*/        
        //CharacterTextField tf1 = new CharacterTextField();
        // System.err.println("ShortMax = " + Short.MAX_VALUE);
        /// NumberPropertyEditor tf1 = new NumberPropertyEditor();
        //tf1.setFont(new Font(13));
        //tf1.bindBidirectional(btn1.textProperty());
        //tf1.bindBidirectional(btn1.prefWidthProperty());
        //tf1.bindBidirectional(btn1.opacityProperty());
        tf1.bindContentBidirectional(plist);
        //plist.addAll(25, 26);
        
        //tf1.setEditable(false);
        //tf1.bind(btn1.prefWidthProperty());
        btn1.setOnAction(e -> {
            
            ObservableList<Integer> nol = FXCollections.observableArrayList();
            tf1.getValue().add(67);
            
            //nol.addAll(tf1.getValue());
            //plist.add(67);
//            tf1.setValue(nol);
//            tf1.formatter.setValue(nol);
            //tf1.getSlider().setValue(-1);
            //btn1.setOpacity(btn1.getOpacity() + 0.1);
            btn1.setPrefWidth(-1);
            //tf1.setText("200");
            //tf1.setEditable(true);
            tf1.getPseudoClassStates().forEach(s -> {
                //System.err.println("PSEUDO = " + s);
            });
            //System.err.println("btn1.prefWidth = " + btn1.getPrefWidth());
        });

        /*        value.bindBidirectional(tf1.valueProperty());
        value.addListener((v,ov,nv) -> {
            System.err.println("1 VALUE " + value.get() + "; TEXT = " + tf1.getText());
        });
        tf1.textProperty().addListener((v,ov,nv) -> {
            System.err.println("2 VALUE " + value.get() + "; TEXT = " + tf1.getText());

        });
         */
        //anchor.setMinWidth(10);
        //grid.setMinWidth(10);
        //tf1.setMinWidth(10);
        //lb1.setMinWidth(10);
        //tf1.prefWidthProperty().bind(grid.widthProperty().subtract(lb1.widthProperty()));
        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
        //TextField tf2 = new TextField();
        //IntegerPropertyEditor tf2 = new IntegerPropertyEditor();
        //IntegerTextField tf2 = new IntegerTextField();
        //ShortTextField tf2 = new ShortTextField();
        //LongTextField tf2 = new LongTextField();
        //DoubleTextField tf2 = new DoubleTextField(24.5);
        //ByteTextField tf2 = new ByteTextField(null);
//        SimpleStringPropertyEditor tf2 = new SimpleStringPropertyEditor("1234");
        
//        tf2.setFont(new Font(13));
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });
        //tf2.bind(btn2.prefWidthProperty());
        //tf2.bindBidirectional(btn2.prefWidthProperty());
        //tf2.bindBidirectional(btn2.textProperty());
        Label lb3 = new Label("lable 3");
        lb3.setFont(new Font(13));

        //tf1.setPrefWidth(200);
        grid.add(lb2, 0, 1);
//        grid.add(tf2, 1, 1);
        //TextField tf3 = new TextField();
        BooleanPropertyEditor tf3 = new BooleanPropertyEditor();
       
        tf3.setOnAction(e -> {
            tf3.getPseudoClassStates().forEach(s -> {
                //System.err.println("PSEUDO = " + s);
            });
            tf3.getStyleClass().forEach(s -> {
                //System.err.println("STYLE = " + s);
            });
       });
        tf3.setFont(new Font(13));
        grid.add(lb3, 0, 2);
        grid.add(tf3, 1, 2);
        //tf3.bindBidirectional(btn1.disableProperty());
        tf3.bind(btn1.disableProperty());
        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);
        //cc20.setPercentWidth(100);

        //grid.getColumnConstraints().addAll(cc0,cc1, cc20);        
        grid.getColumnConstraints().addAll(cc0, cc1);
        //GridPane.setHalignment(tf1, HPos.RIGHT);
        //GridPane.setHalignment(tf1, HPos.LEFT);
        //GridPane.setFillWidth(tf1, true);
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

        /*        TabPane tabPane = new TabPane();
        //propPane.getChildren().add(tabPane);
        Tab propTab = new Tab();
        Tab layoutTab = new Tab();
        Tab codeTab = new Tab();
        tabPane.getTabs().addAll(propTab,layoutTab,codeTab);
        
        tabPane.setTabMaxHeight(0);
        propTab.setContent(new Label("P111"));
        layoutTab.setContent(new Label("L111"));
        codeTab.setContent(new Label("C111"));
         */
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
