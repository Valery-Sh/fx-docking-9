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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.ComboButtonPropertyEditor;

//It's a filter which throws an Exception when apply a method `c.getControlNewText()`.
//You should implement the TestTextFormatter with a StringConverter.
/**
 *
 * @author Valery
 */
public class TestComboButtonPropertyEditor extends Application {

    Stage stage;
    Scene scene;
    IntegerProperty value = new SimpleIntegerProperty();

    @Override
    public void start(Stage stage) {
        ComboButtonPropertyEditor cb = new ComboButtonPropertyEditor();
        
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
      
        /*TitledPane tp = new TitledPane();
        tp.setAlignment(Pos.CENTER_RIGHT);
        tp.setGraphic(new Label("Val"));
        tp.setText("titled Pane");
        */
        VBox root = new VBox(btn1, btn2, cb);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        StackPane stackPane = new StackPane(grid);
        
        
        Button cbBtn1 = new Button("cbBtn1");
        btn1.setOnAction(e -> {
            //cb.setButton(cbBtn1);
            cb.getButton().setText("Arial 2px");
            
        });
        Button cbBtn2 = new Button("cbBtn2");
        btn2.setOnAction(e -> {
            //cb.setButton(cbBtn2);
            ComboButtonPropertyEditor.setDefaultLayout(cbBtn2);
            ComboButtonPropertyEditor.setDefaultButtonGraphic(cbBtn2);
            
        });
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
    
    public static class MyCombo extends ComboBox {

        public MyCombo() {
        }
        
    }
}
