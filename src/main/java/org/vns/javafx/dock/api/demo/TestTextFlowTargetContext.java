package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
public class TestTextFlowTargetContext extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextFlow primaryRoot = new TextFlow();
        primaryRoot.getChildren().addAll(new Button("Btn1"), new Label("Label1"), new Button("Btn2"));
        DockRegistry.getInstance().registerAsDockTarget(primaryRoot);

        DockNode custom = new DockNode();
        primaryRoot.getChildren().add(custom);
        custom.setId("custom");
        Text text1 = new Text("Big italic red text");
        text1.setFill(Color.RED);
        text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
        Text text2 = new Text(" little bold blue text");
        text2.setFill(Color.BLUE);
        text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
        //TextFlow textFlow = new TextFlow(text1, text2);
        primaryRoot.getChildren().addAll(text1, text2);
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
