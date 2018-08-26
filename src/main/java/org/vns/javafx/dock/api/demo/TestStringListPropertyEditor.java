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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.BooleanPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.IntegerListPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.ErrorMarkerBuilder;
import org.vns.javafx.dock.api.designer.bean.editor.StringListPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.editor.SimpleStringPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestStringListPropertyEditor extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        ObservableList<Integer> plist = FXCollections.observableArrayList();

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
        //lb1.textProperty().bindBidirectional(other, converter);
        //lb1.getStyleClass().add("str");
        //lb1.setFont(new Font(13));
        //System.err.println("font size lb1.getFont().getSize()= " + lb1.getFont().getSize());
        //SliderEditor tf1 = new SliderEditor(0,1,1);
        //DecimalTextField tf1 = new DecimalTextField();

        StringListPropertyEditor tf1 = new StringListPropertyEditor();
        tf1.setSeparator(",");
        //tf1.setSeparator(",", "\\s*,\\s*");
        //tf1.setValueIfBlank("blank");
        //tf1.setSeparator(" ", "\\s* \\s*");'
        
        //"a,,b, c,".
        String src = "a,,b,    , c,   ";
        String[] s1 = (src).split(tf1.getSeparator(),src.length());
        for ( String s : s1) {
            System.err.println("s = '" + s + "'");
        }
        String q = java.util.regex.Matcher.quoteReplacement("\\s*,\\s*");
        System.err.println("Q = '" + q + "'");
        //tf1.getStyleClass().remove("label");
        //tf1.setValueIfBlank("");
        tf1.setErrorMarkerBuilder(new ErrorMarkerBuilder(tf1));
        //tf1.setKeepItemTrimmed(false);
        //tf1.setSeparator(",");
        String[] sss = "a b c".split("\\s+,");
        //System.err.println("0 " + sss[0] + "; 1 " + sss[1] + "; 2 " + sss[2]);
        //Pattern pattern = Pattern.compile("\\w+");
        //System.err.println("quote = " + Pattern.quote("\\s+,"));        
        String tx = ",a1,b ,c";
        Pattern ptn = Pattern.compile(",");
        //Matcher m = ptn.matcher(tx);        
        //System.err.println("m0 = " + m.find() + "; start=" + m.start() + "; end=" + m.end() );
        //System.err.println("m0.1 = " + "; start=" + m.start() );
        //System.err.println("m1 = " + m.find() + "; start=" + m.start() + "; end=" + m.end() );
/*        String[] its = ptn.split(tx);
        System.err.println("itx.length=" + its.length);
        Integer[][] itemPos = new Integer[its.length][2];
        Matcher m = ptn.matcher(tx);        
        int start = 0;
        for ( int i=0; i < its.length; i++) {
            
            boolean found = m.find();
            if ( ! found && i == its.length -1 ) {
                itemPos[i][0] = start + 1;
                itemPos[i][1] = start + 1 + its[its.length -1].length();
                break;
            }
            start = m.start();
            itemPos[i][0] = start - its[i].length();
            itemPos[i][1] = start;
            if ( itemPos[i][0] == -1 ) {
                //itemPos[i][0] = 0;
                //itemPos[i][1] = 0;
            }
        }
        for ( int i= 0; i < its.length; i ++ ) {
            System.err.println("item[" + i + "] = " + its[i] );
            System.err.println("[" + i + ",0] = " + itemPos[i][0]);
            System.err.println("[" + i + ",1] = " + itemPos[i][1]);
            System.err.println("----------------------------------");
            
        }
        System.err.println("last = " + tx.substring(7, 8));
*/        
        // in case you would like to ignore case sensitivity,
        // you could use this statement:
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        //Matcher matcher = pattern.matcher("");
        tf1.setFromStringTransformer(srcStr -> {
            return srcStr.trim();
        });
        
        tf1.getValidators().add(item -> {
            //  if ( true) return true;
            boolean v = true;
            //System.err.println("V ITEM = " + item);
            //if (!item.trim().isEmpty()) {
                if (!(item.trim().startsWith("tx1") || item.trim().startsWith("-fx") || item.trim().startsWith("label"))) {
                    System.err.println("VALIDATOR false item = " + item);
                    v = false;
                }
            //}

            return v;
        });

        //CharacterTextField tf1 = new CharacterTextField();
        // System.err.println("ShortMax = " + Short.MAX_VALUE);
        /// NumberPropertyEditor tf1 = new NumberPropertyEditor();
        //tf1.setFont(new Font(13));
        //tf1.bindBidirectional(btn1.textProperty());
        //tf1.bindBidirectional(btn1.prefWidthProperty());
        //tf1.bindBidirectional(btn1.opacityProperty());
        //lb1.getStyleClass().remove("label");
        lb1.getStyleClass().add("str-0    ");
        tf1.bindContentBidirectional(lb1.getStyleClass());
        //plist.addAll(25, 26);

        //tf1.setEditable(false);
        //tf1.bind(btn1.prefWidthProperty());
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
        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
        //TextField tf2 = new TextField();
        //IntegerPropertyEditor tf2 = new IntegerPropertyEditor();
        //IntegerTextField tf2 = new IntegerTextField();
        //ShortTextField tf2 = new ShortTextField();
        //LongTextField tf2 = new LongTextField();
        //DoubleTextField tf2 = new DoubleTextField(24.5);
        //ByteTextField tf2 = new ByteTextField(null);
        SimpleStringPropertyEditor tf2 = new SimpleStringPropertyEditor("1234");

        tf2.setFont(new Font(13));
        btn2.setOnAction(e -> {
            btn2.setPrefWidth(200.56);
        });
        //tf2.bind(btn2.prefWidthProperty());
        //tf2.bindBidirectional(btn2.prefWidthProperty());
        //tf2.bindBidirectional(btn2.textProperty());
        //tf2.bind(btn2.textProperty());
        //tf2.unbind();
        Label lb3 = new Label("lable 3");
        lb3.setFont(new Font(13));

        //tf1.setPrefWidth(200);
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
        grid.add(tf2, 1, 2);
        //TextField tf3 = new TextField();

        btn1.setOnAction(e -> {
            //tf1.getValue().addAll("STR11","STR12","STR13");
            lb1.getStyleClass().forEach(s -> {
                System.err.println("Label class = " + s);
            });
            tf1.getValue().addAll("STR10", "STR11");
            //tf1.getValue().add("");
            System.err.println("INSETS = " + tf1.getInsets());

            //ObservableList<Integer> nol = FXCollections.observableArrayList();
            //lb1.getStyleClass().add("str");
            //tf1.getValue().add("a67");
            System.err.println("STYLE CLASSES: " + lb1.getStyleClass());
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

        grid.add(lb3, 0, 3);
        grid.add(tf3, 1, 3);
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
