package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestAnchorPaneTargetContext extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane primaryRoot = new AnchorPane();
        DockRegistry.getInstance().toDockLayout(primaryRoot);
        
         // List should stretch as anchorPane is resized
        ListView list = new ListView();
        AnchorPane.setTopAnchor(list, 10.0);
        AnchorPane.setLeftAnchor(list, 10.0);
        AnchorPane.setRightAnchor(list, 65.0);
        // Button will float on right edge
        Button button = new Button("Add");
        AnchorPane.setTopAnchor(button, 10.0);
        AnchorPane.setRightAnchor(button, 10.0);
        primaryRoot.getChildren().addAll(list, button);
        primaryRoot.getChildren().addAll(new Button("Btn1"), new Label("Label1"), new Button("Btn2"));

        DockNode custom = new DockNode();
        primaryRoot.getChildren().add(custom);
        custom.setId("custom");
        Scene primaryScene = new Scene(primaryRoot);

        primaryStage.setTitle("JavaFX and Maven");
        primaryStage.setScene(primaryScene);

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
