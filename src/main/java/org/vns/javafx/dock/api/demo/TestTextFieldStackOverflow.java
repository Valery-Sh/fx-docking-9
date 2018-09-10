package org.vns.javafx.dock.api.demo;

import java.util.function.UnaryOperator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TestTextFieldStackOverflow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextField field = new TextField();
        
       field.setTextFormatter(new TestTextFormatter());
        
        Button btn = new Button();
        btn.setText("Test");
        btn.setOnAction(e -> {
            field.setText(null);
            field.setText("test");
        });
        BorderPane root = new BorderPane();
        root.setTop(btn);
        root.setCenter(field);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

   class TestTextFormatter extends TextFormatter<String> {
        public TestTextFormatter() {
            super(c -> {
                if ( ((TextField)c.getControl()).getText() == null ) {
                    return c;
                }
                System.out.println("newControlText=" + c.getControlNewText());
                return c;
            });
        }
    }

}
