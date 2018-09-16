/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import java.util.function.UnaryOperator;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
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
        textField.setNullable(true);
        textField.setText("9876");
        //textField.setText("start text");

        textField.mark++;
        System.err.println(" second NULL **************************");
        //textField.setText(null);
        //textField.setText("ttt");

        //textField.setText(null);
        //textField.setText("");
        //textField.setText(null);
        //btn1.setText("Test");
        btn1.setOnAction(e -> {
            textField.setText(null);
            //textField.setText("test " + (textField.mark++));
//            textField.setText(null);
        });
        btn2.setOnAction(e -> {
            System.err.println("****** text = " + textField.getText());
            System.err.println("****** formatter value = " + textField.getFormatter().getValue());
        });
        grid.add(textField, 0, 0);
        grid.add(btn1, 0, 1);
        grid.add(btn2, 0, 2);
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
        protected String lastText;
        protected boolean nullable;
        protected String nullSubstitution; 
        
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
            if (((TextField) change.getControl()).getText() == null) {
                //Platform.runLater(() -> { setText("");});
                //((TextFormatter<String>)((TextField) change.getControl()).getTextFormatter()).setValue("<NULL>");
                if ( ! isNullable() ) {
                    System.err.println("FILTER lastText = '" + lastText + "'");
                    change.setText(lastText);     
                    return change;
                } else if (nullSubstitution != null ) {
                    System.err.println("FILTER NULL SUBSTITUTION " + nullSubstitution);
                    Platform.runLater ( () -> {
                        //setText(nullSubstitution);     
                    });
                    //setText(nullSubstitution);     
                    //((TextFormatter<String>)getTextFormatter()).setValue(nullSubstitution);
                    System.err.println("FILTER NULL SUBSTITUTION getText() = " + getText());
                    
                    change.setText(nullSubstitution);
                    return change;
                }
                System.err.println("FILTER returns null");
                return null;
                //return change;
            }
            //System.err.println("   --- MyTextField !!! FILTER change controlNewText = " + change.getControlNewText());

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
            
            formatter = new TextFormatter(new MyTextField.FormatterConverter(this), "", filter.get());
            setTextFormatter(formatter);
            lastText = getText();
            nullSubstitution = "<NULL>";

        }

        public UnaryOperator<TextFormatter.Change> getFilter() {
            return filter.get();
        }

        public String getLastText() {
            return lastText;
        }

        public void setLastText(String lastText) {
            this.lastText = lastText;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public TextFormatter getFormatter() {
            return formatter;
        }

        public static class FormatterConverter extends StringConverter<String> {

            private final MyTextField textField;
            private boolean updating = false;

            public FormatterConverter(MyTextField textField) {
                this.textField = textField;
            }

            @Override
            public String toString(String txt) {
                System.err.println("1 " + textField.mark + ". MyTextField !!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");                
                if (updating) {
                    return txt;
                }
               // System.err.println("2 " +textField.mark + ". MyTextField !!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");                
                String retval = txt;
                try {
                    updating = true;
                    if (txt == null) {
                        //return "NULL";
                        //return "NULL";
                    }
                } finally {
                    textField.getFormatter().setValue(retval);
                    textField.setLastText(retval);
                    updating = false;
                }

                return retval;
                //return textField.toString(list);
            }

            @Override
            public String fromString(String txt) {
               
                //System.err.println(textField.mark + ". MyTextField  !!! fromString STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
                if ( true ) {
                    return txt;
                }                
                
                String retval = txt;
                if (txt == null) {
                    //textField.getFormatter().setValue(null);
                    return null;
                }
                if ("<NULL>".equals(txt)) {
                    //return null;
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
