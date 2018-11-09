/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestStringPropertyEditor extends Application {

    Stage stage;
    Scene scene;
    IntegerProperty value = new SimpleIntegerProperty();

    @Override
    public void start(Stage stage) {
        Button btn1 = new Button("setText(null)");
        Button btn2 = new Button("setText(Valery)");
        Button btn3 = new Button("setText(\"\")");
        Button btn4 = new Button("setLastValidText(Olga)");
        
        

        GridPane grid = new GridPane();
        grid.setHgap(10);

        StackPane root = new StackPane(grid);
        StringProperty boundStr = new SimpleStringProperty(null);
        
        StringPropertyEditor tf1 = new StringPropertyEditor("tf1");
        tf1.bindBidirectional(btn1.textProperty());
        btn1.setOnAction(e -> {
//            tf1.setText(null);
        });

        grid.add(tf1, 0, 0);
        grid.add(btn1, 0, 1);
        grid.add(btn2, 0, 2);
        grid.add(btn3, 0, 3);
        
        btn2.setOnAction(e -> {
        });
        btn3.setOnAction(e -> {
        });
        btn4.setOnAction(e -> {
        });

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
