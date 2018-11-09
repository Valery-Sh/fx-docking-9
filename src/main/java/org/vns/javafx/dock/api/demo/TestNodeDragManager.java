package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.designer.NodeDragManager;
import org.vns.javafx.designer.SceneGraphView;

public class TestNodeDragManager  extends Application {

    private Line vertLine = new Line();
    private Button dragButton;
    //Point2D mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

    @Override
    public void start(Stage stage) throws Exception {
        
        
        VBox vbox = new VBox();
        vbox.setId("vbox1");
        Button btn1 = new Button("btn1");
        vbox.getChildren().add(btn1);

        HBox hbox = new HBox();
        hbox.setId("hbox");
        
        TabPane tabPane1 = new TabPane();
        tabPane1.setId("tabpane1");
        Tab tab1 = new Tab("Tab 1");
        tabPane1.getTabs().add(tab1);
        
        TabPane tabPane2 = new TabPane();
        tabPane2.setId("tabpane12");
        Tab tab21 = new Tab("Tab 21");
        tabPane2.getTabs().add(tab21);
        Tab tab22 = new Tab("Tab 22");
        tabPane2.getTabs().add(tab22);
        
        
        VBox treeViewRoot = new VBox(vbox,hbox,tabPane1);
        treeViewRoot.setId("ROOT");
        
        //tab22.setContent(treeViewRoot);
        
        ////////////////////////////////////
        
        
        
        Button dragBtn1 = new Button("drag Btn");
        Button btn2 = new Button("Add new Label");
        Button btn3 = new Button("Remove btn1");
        Button btn4 = new Button("Set Graphic");
        Button btn5 = new Button("Remove Graphic");
        
        Label lb1 = new Label("lb1");
        Label lb2 = new Label("lb2");
        Label lb3 = new Label("lb3");
        Label lb4 = new Label("lb4");
        VBox labelBox = new VBox();
        tab22.setContent(labelBox);
/*        btn2.setOnAction(a -> {
            System.err.println("vbox.sxene = " + vbox.getScene());
            vbox.getChildren().add(0,lb1);
            vbox.getChildren().addAll(lb2,lb3);
            
        });
*/        
        btn2.setOnAction(a -> {
            System.err.println("vbox.sxene = " + labelBox.getScene());
            labelBox.getChildren().add(0,lb1);
            labelBox.getChildren().addAll(lb2,lb3,lb4);
            lb3.setGraphic(lb4);
            
        });        
        btn4.setOnAction(a -> {
            System.err.println("before SIZE = " + vbox.getChildren().size());
            try {
                lb2.setGraphic(lb1);
                lb2.setGraphic(null);
                System.err.println("lb1 parent = " + lb1.getParent());
                lb3.setGraphic(lb1);
                
                
            } catch(IllegalArgumentException  ex) {
                System.err.println("EXCEPTION !!!");
            }
            System.err.println("after SIZE = " + vbox.getChildren().size());
            System.err.println("lb2 graphic = " + lb2.getGraphic());
        });
        btn5.setOnAction(a -> {
            System.err.println("VBox SIZE = " + labelBox.getChildren().size());
            System.err.println("lb2 graphic =  = " + lb2.getGraphic());
            System.err.println("vbox.getLb1 =  = " + labelBox.getChildren().indexOf(lb1));
            
            //lb2.setGraphic(null);
        });
        
        btn3.setOnAction(a -> {
            vbox.getChildren().remove(btn1);
        });
        VBox vbox1 = new VBox(dragBtn1, btn2,btn3, btn4,btn5);
        
        tab21.setContent(vbox1);
        
        Button tabContent22 = new Button("btn of Tab22");
        //tab22.setContent(tabContent22);

        VBox rootPane = new VBox(tabPane2);
        Scene scene = new Scene(rootPane);

        stage.setTitle("Test EditorTreeView");
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(300);        
        stage.setX(10);
        stage.setY(20);
        
//        SceneGraphView graphView = new SceneGraphView(treeViewRoot);
        SceneGraphView graphView = new SceneGraphView(rootPane);
        Label statusBar = new Label("It is STAUS BAR");
        graphView.setStatusBar(statusBar);
        
        Scene tvScene = new Scene(graphView);
        Stage graphViewStage = new Stage();
        graphViewStage.setScene(tvScene);
        graphViewStage.setHeight(300);
        graphViewStage.setWidth(300);
        
        graphViewStage.setScene(tvScene);
        graphViewStage.setX(350);
        graphViewStage.setY(40);
      
        //
        // Move tab21 from tabPane2 to tabPane1 using contentButton
        //
//        NodeDragManager.getInstance().enableDragAndDrop(tab21, dragBtn1, () -> {
//        NodeDragManager.getInstance().enableDragAndDrop(tab21, dragBtn1);
/*        NodeDragManager.getInstance().enableDragAndDrop(tab21, dragBtn1, () -> {        
            tab21.getTabPane().getTabs().remove(tab21);
            return true;
        });
*/
        NodeDragManager.getInstance().enableDragAndDrop(tab21, dragBtn1);
        NodeDragManager.getInstance().setEventNotifier(dragBtn1, e -> {
            System.err.println("NOTIFY: " + e.getEventType());
        });
        //graphView.setStyle("-fx-background-color: yellow");
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


