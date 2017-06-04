package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.DragManager;
import org.vns.javafx.dock.api.editor.EditorUtil;
import org.vns.javafx.dock.api.editor.ItemValue;
import org.vns.javafx.dock.api.editor.NodeDragManager;
import org.vns.javafx.dock.api.editor.SceneGraphView;

public class TestNodeDragManager  extends Application {

    private Line vertLine = new Line();
    private Button dragButton;
    //Point2D mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

    @Override
    public void start(Stage stage) throws Exception {
        
        
        VBox vbox = new VBox();
        vbox.setId("vbox1");
        vbox.getChildren().add(new Button("bbb"));

        HBox hbox = new HBox();
        hbox.setId("hbox");
        
        TabPane tabPane1 = new TabPane();
        tabPane1.setId("tabpane1");
        Tab tab1 = new Tab("Tab 1");
        tabPane1.getTabs().add(tab1);
        Button tabContent1 = new Button("btn of Tab 1");
        tab1.setContent(tabContent1);
        
        VBox treeViewRoot = new VBox(vbox,hbox,tabPane1);
        treeViewRoot.setId("ROOT");
        
        SceneGraphView graphView = new SceneGraphView(treeViewRoot);
        
        Scene tvScene = new Scene(graphView);
        Label statusBar = new Label("It is STAUS BAR");
        graphView.setStatusBar(statusBar);
        Stage graphViewStage = new Stage();
        graphViewStage.setScene(tvScene);
        graphViewStage.setHeight(300);
        graphViewStage.setWidth(300);
        
        graphViewStage.setScene(tvScene);
        graphViewStage.setX(350);
        graphViewStage.setY(40);
        
        ////////////////////////////////////
        
        TabPane tabPane2 = new TabPane();
        tabPane2.setId("tabpane12");
        Tab tab21 = new Tab("Tab 21");
        tabPane2.getTabs().add(tab21);
        Tab tab22 = new Tab("Tab 22");
        tabPane2.getTabs().add(tab22);
        
        
        Button tabContent21 = new Button("btn of Tab21");
        tab21.setContent(tabContent21);
        Button tabContent22 = new Button("btn of Tab22");
        tab22.setContent(tabContent22);
        tabContent22.setOnAction(a -> {
            System.err.println("DISABLE DRAG AND DROP");
            NodeDragManager.getInstance().disableDragAndDrop(tabContent21);
        });
        VBox rootPane = new VBox(tabPane2);
        Scene scene = new Scene(rootPane);

        stage.setTitle("Test EditorTreeView");
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(300);        
        stage.setX(10);
        stage.setY(20);
        //
        // Move tab21 from tabPane2 to tabPane1 using contentButton
        //
        NodeDragManager.getInstance().enableDragAndDrop(tab21, tabContent21, () -> {
            tab21.getTabPane().getTabs().remove(tab21);
            return true;
        });
        
    
//        tabContent21.addEventHandler(MouseEvent.MOUSE_PRESSED, NodeDragManager.getInstance());
//        NodeDragManager.getInstance().disableDragAndDrop(tabContent21);
        
/*        NodeDragManager.getInstance().setEventNotifier(tabContent21, e -> {
            System.err.println("NOTIFY: " + e.getEventType());
        });
*/                
        graphView.setStyle("-fx-background-color: yellow");
        stage.show();
        graphViewStage.show();

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
    public static boolean test(Object o) {
        return false;
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


