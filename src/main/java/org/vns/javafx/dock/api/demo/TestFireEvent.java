package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestFireEvent  extends Application {

    private Button dragButton;

    @Override
    public void start(Stage stage) throws Exception {
        VBox vboxPane = new VBox();
        HBox rootPane = new HBox(vboxPane);
        rootPane.setId("ROOT");
        vboxPane.setId("vboxPane");
        Button btn01 = new Button("click btn01");
        vboxPane.getChildren().add(btn01);
        Button btn02 = new Button("target btn02");
        vboxPane.getChildren().add(btn02);
        Button btn03 = new Button("target btn03");
        vboxPane.getChildren().add(btn03);
        

        Scene scene = new Scene(rootPane);
        
        stage.setHeight(300);
        stage.setWidth(300);
        stage.setScene(scene);
        stage.show();
        Stage stage1 = new Stage();
        Button stag1Button = new Button("stag1Button");
        VBox stage1VBox = new VBox(stag1Button);
        Scene stage1Scene = new Scene(stage1VBox);
        stage1.setScene(stage1Scene);
        stage1.setHeight(200);
        stage1.setWidth(200);
        stage1.show();
        //Stage stage2 = new Stage();
        //stage2.setHeight(100);
        //stage2.setWidth(100);
        //stage2.show();
        
        btn01.setOnAction(a -> {
            //ChildrenNodeRemover r = n -> vboxPane.getChildren().remove(n);
            //r.remove(btn03);
        });
        
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
        if (e.getEventType() == MouseEvent.DRAG_DETECTED) {
            /*            String sourceText = sourceFld.getText();
            if (sourceText == null || sourceText.trim().equals("")) {
                e.consume();
                return;
// Initiate a drag-and-drop gesture
                Dragboard dragboard = sourceFld.startDragAndDrop(TransferMode.COPY_OR_MOVE);
// Add the source text to the Dragboard
                ClipboardContent content = new ClipboardContent();
                content.putString(sourceText);
                dragboard.setContent(content);
                e.consume();
            }
             */
        }
    }

}
