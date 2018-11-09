/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.StringTextField;

/**
 *
 * @author Valery
 */
public class DemoStyleStringField extends Application {

 
  

    @Override
    public void start(Stage stage) {
        Scene scene;
        //System.err.println("UUID:" + java.util.UUID.randomUUID().toString());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        

        StringTextField styleTextField = new StringTextField();
        styleTextField.setSeparator(";");
        styleTextField.getValidators().add( it -> {
            if ( it.isEmpty() ) {
                //return true;
            }
            if ( ! it.startsWith("-fx-") ) {
                return false;
            }
            return true;
        });
      
        styleTextField.setEmptySubstitution("EM");
        //styleTextField.setNullSubstitution("NULL");
        //styleTextField.setNullSubstitution("NULL");
        
        //styleTextField.setText("-fx-a: b");
        //styleTextField.setNullable(true);     
        
        //styleTextField.setText(null);
        styleTextField.setText("");
        
//        styleTextField.setText("-fx");
     
/*        styleTextField.setText("-fx-a: b");
        styleTextField.setText(null);
        styleTextField.setText(null);
        styleTextField.setText("-fx-a: b");
        styleTextField.setText(null);
        styleTextField.setText("-fx-c: d");
*/        
        grid.add(styleTextField, 0, 0);
        scene = new Scene(grid);
        stage.setScene(scene);
        stage.setTitle("StringTextField Demo");
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
