/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.ComboButton;
import org.vns.javafx.dock.api.designer.bean.editor.ComboButton.ItemsUpdater;
import org.vns.javafx.dock.api.designer.bean.editor.TextFieldPropertyEditor;

//It's a filter which throws an Exception when apply a method `c.getControlNewText()`.
//You should implement the TestTextFormatter with a StringConverter.
/**
 *
 * @author Valery
 */
public class TestTextFieldPropertyEditor extends Application {

    Stage stage;
    Scene scene;
    IntegerProperty value = new SimpleIntegerProperty();

    @Override
    public void start(Stage stage) {
        //ComboPropertyEditor comboText = new ComboPropertyEditor();
        TextFieldPropertyEditor comboText = new TextFieldPropertyEditor() {
            @Override
            public Object valueOf(String txt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            protected StringBinding asString(ReadOnlyProperty property) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        ComboButton cbtn = new ComboButton();
        cbtn.getComboBox().getItems().add("Text1");
        cbtn.getComboBox().getItems().add("Text2");
        cbtn.getComboBox().getItems().add("Text3");        
        comboText.getButtons().add(cbtn);
        ItemsUpdater<String> updater = list -> {
            cbtn.getComboBox().getItems().add("text4");
        };
        cbtn.setItemsUpdater(updater);
        
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");
        Button btn3 = new Button("Set editable");
    
        VBox root = new VBox(btn1, btn2, btn3, comboText);
        btn3.setOnAction(a -> {
           comboText.setEditable(! comboText.isEditable());
        });
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        StackPane stackPane = new StackPane(grid);
        
        
        Button cbBtn1 = new Button("cbBtn1");
        btn1.setOnAction(e -> {
            comboText.getButtons().add(cbBtn1);
            cbBtn1.setText("Arial 2px");
            
        });
        Button cbBtn2 = new Button("cbBtn2");
        btn2.setOnAction(e -> {
//            comboText.setButton(cbBtn2);
            comboText.getButtons().add(cbBtn2);
            //ComboButton.setDefaultLayout(cbBtn2);
            //ComboButton.setDefaultButtonGraphic(cbBtn2);
            
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
