/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import java.util.function.UnaryOperator;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.NewTextField;
import org.vns.javafx.dock.api.designer.bean.editor.StringTextField;

//It's a filter which throws an Exception when apply a method `c.getControlNewText()`.

//You should implement the TestTextFormatter with a StringConverter.
/**
 *
 * @author Valery
 */
public class TestTextField extends Application {

    Stage stage;
    Scene scene;
    IntegerProperty value = new SimpleIntegerProperty();

    @Override
    public void start(Stage stage) {
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        StackPane root = new StackPane(grid);
        btn1.setOnAction(e -> {
        });
        MyTextField textField = new MyTextField();
        textField.mark++;
        System.err.println(" first NULL **************************");
        textField.setText(null);

        textField.mark++;
        System.err.println(" second NULL **************************");
        textField.setText(null);
        textField.setText("ttt");
        
        textField.setText(null);
        //textField.setText("");
        //textField.setText(null);
        btn1.setText("Test");
        btn1.setOnAction(e -> {
            textField.setText(null);
            textField.setText("test");
            textField.setText(null);
        });
        grid.add(textField, 0, 0);
        grid.add(btn1, 0, 1);
        //System.err.println("textField.getText= " + textField.getText());
        /*        StringTextField stextField = new StringTextField();
        stextField.setNullString("<NULL>");
        stextField.setText(null);
        stextField.setText(null);
        
        grid.add(stextField, 0, 1);
         */
        //System.err.println("StextField.getText= " + stextField.getText());        

        NewTextField newTextField = new NewTextField();
        newTextField.setText("21");
        newTextField.setText("22");
        //grid.add(newTextField, 0, 2);

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

        VBox vbox = new VBox(btn2);
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

        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
        //pc.show(stage, 20, 2);

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

    public static class MyTextField extends TextField {

        public int mark = -1;

        private final ObjectProperty<UnaryOperator<TextFormatter.Change>> filter = new SimpleObjectProperty<>(change -> {
            mark++;
            System.err.println("====================================");
            System.err.println(mark + ". MyTextField !!! FILTER change = " + change.getClass().getName());
            System.err.println("   --- MyTextField !!! FILTER change start = " + change.getRangeStart() + "; end = " + change.getRangeEnd());
            //String newText = change.getControlNewText();
            //String newText = change.getControlText();
            System.err.println("   --- MyTextField !!! FILTER change controlText = " + change.getControlText());
            System.err.println("   --- MyTextField !!! FILTER change change.getText = " + change.getText());
            System.err.println("   --- MyTextField !!! FILTER change textField.getText = " + getText());
            if ( ((TextField)change.getControl()).getText() == null ) {
                return null;
            }
            System.err.println("   --- MyTextField !!! FILTER change controlNewText = " + change.getControlNewText());
            
            //if (isAcceptable(change.getControlNewText())) {
            return change;

        });
        private TextFormatter formatter;

        public MyTextField() {
            init();
        }

        public MyTextField(String text) {
            super(text);
        }

        private void init() {
            formatter = new TextFormatter(new MyTextField.FormatterConverter(this), null, filter.get());
            setTextFormatter(formatter);

        }

        public UnaryOperator<TextFormatter.Change> getFilter() {
            return filter.get();
        }

        public TextFormatter getFormatter() {
            return formatter;
        }

        public static class FormatterConverter extends StringConverter<String> {

            private final MyTextField textField;

            public FormatterConverter(MyTextField textField) {
                this.textField = textField;
            }

            @Override
            public String toString(String txt) {

                System.err.println(textField.mark + ". MyTextField !!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
                if (txt == null) {
                    return "<NULL>";
                }
                return txt;
                //return textField.toString(list);
            }

            @Override
            public String fromString(String txt) {
                System.err.println(textField.mark + ". MyTextField  !!! fromString STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
                String retval = txt;
                if (txt == null) {
                    //textField.getFormatter().setValue(null);
                    return null;
                }
                if ("<NULL>".equals(txt)) {
                    return null;
                }

                /*                if ( "<NULL>".equals(txt) ) {
                    return null;
                }
                 */
                return retval;
            }
        }//class FormatterConverter
    }
}
