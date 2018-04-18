package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.bean.ReflectHelper;

/**
 *
 * @author Valery
 */
public class TestReflection extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane primaryRoot = new StackPane();
        
        Button b1 = new Button("Add or Remove TitleBar");
        Button b2 = new Button("b2r");
        b1.setOnAction(a->{
        });
        Scene primaryScene = new Scene(primaryRoot);
        
        primaryStage.setTitle("Test Reflection");
        primaryStage.setScene(primaryScene);
        
        primaryStage.setOnShown(s -> {
            Class clazz = ReflectHelper.getListGenericType(Accordion.class, "panes");
            System.err.println("GenericType = " + clazz);
        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        
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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
    
}
