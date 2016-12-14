
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockToolBarTitled;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestForFXML01  extends Application {
    

    @Override
    public void start(Stage stage) throws Exception {
        
        DockPane dockPane = new DockPane();
        Button b1 = new Button("b01");
        DockToolBarTitled dtt = new DockToolBarTitled();
        dockPane.getChildren().add(dtt);
        b1.setLayoutX(126);
        b1.setLayoutY(90);
        //dockPane.dock(dtt, Side.TOP);
        dockPane.getChildren().add(b1);


        Scene scene = new Scene(dockPane);

        stage.setTitle("Dockable and Toolbar");
        stage.setScene(scene);


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

