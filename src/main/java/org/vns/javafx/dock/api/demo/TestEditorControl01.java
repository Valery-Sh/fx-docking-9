/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
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
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.ItemValue;
import org.vns.javafx.dock.api.editor.NodeDragEvent;
import org.vns.javafx.dock.api.editor.NodeDragManager;
import org.vns.javafx.dock.api.editor.SceneGraphView;
import org.vns.javafx.dock.api.editor.TreeViewEx;
import org.vns.javafx.dock.api.editor.TreeViewExSkin;

/**
 *
 * @author Valery
 */
public class TestEditorControl01 extends Application {

    private Line vertLine = new Line();
    private Button dragButton;
    //Point2D mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

    @Override
    public void start(Stage stage) throws Exception {
        Label nlb1 = new Label("DRAGGED LABEL");
        HBox rootPane = new HBox();
        VBox stackPane = new VBox();
        stackPane.setId("ROOT");
        VBox vbox = new VBox();
        vbox.setId("vbox1");
        vbox.getChildren().add(new Button("bbb"));

        HBox hbox = new HBox();
        hbox.setId("hbox");
        TextField textField1 = new TextField();
        textField1.setId("textField1");
        hbox.getChildren().add(textField1);
        TextArea textArea1 = new TextArea("Text Area 1");
        textArea1.setMaxWidth(70);
        textArea1.setMaxHeight(30);
        textArea1.setId("textArea1");
        hbox.getChildren().add(textArea1);

        ChoiceBox choiceBox1 = new ChoiceBox();
        choiceBox1.setId("choiceBox1");
        hbox.getChildren().add(choiceBox1);
        ComboBox<String> comboBox1 = new ComboBox<>();
        comboBox1.setId("comboBox1");
        hbox.getChildren().add(comboBox1);
        GridPane gridPane1 = new GridPane();
        Label gb1Label1 = new Label(" Col=2, row=3");
        gridPane1.add(gb1Label1, 2, 3);
        
        gridPane1.setId("gridPane1");
        hbox.getChildren().add(gridPane1);

        stackPane.getChildren().add(vbox);

        //stackPane.getChildren().add(vbox2);        
        stackPane.getChildren().add(hbox);
        Pane pane = new Pane();
        pane.setId("pane1");
        stackPane.getChildren().add(pane);
        vbox.toFront();
        TabPane tabPane = new TabPane();
        tabPane.setId("tabpane11");
        Tab tab1 = new Tab("Tab 1");
        tabPane.getTabs().add(tab1);
        Tab tab2 = new Tab("Tab 2");

        TabPane tabPane2 = new TabPane();
        tabPane.setId("tabpane12");
        Tab tab21 = new Tab("Tab 21");
        tabPane2.getTabs().add(tab21);
        Button tabContent21 = new Button("btn of Tab21");
        tab21.setContent(tabContent21);

        Button tabContent2 = new Button("btn of Tab2");
        tab2.setContent(tabContent2);
        tabPane.getTabs().add(tab2);

        stackPane.getChildren().add(tabPane);
        stackPane.getChildren().add(tabPane2);

        VBox vbox2 = new VBox();
        vbox2.setId("vbox2");
        Button vbox2b1 = new Button("vbox2 b1");
        vbox2.getChildren().add(vbox2b1);
        Button vbox2b2 = new Button("vbox2 b2");
        vbox2.getChildren().add(vbox2b2);

        vbox2.getChildren().add(new Button("vbox2 b3"));
        vbox2.getChildren().add(new Button("vbox2 b4"));
        vbox2.getChildren().add(new Button("vbox2 b5"));
        vbox2.getChildren().add(new Button("vbox2 b6"));
        vbox2.getChildren().add(new Button("vbox2 b7"));
        vbox2.getChildren().add(new Button("vbox2 b8"));

        stackPane.getChildren().add(vbox2);

        Button vboxBtn1 = new Button("vbox btn1");
        vboxBtn1.setId("vboxBtn1");
        vbox.getChildren().add(vboxBtn1);
        Button vboxBtn2 = new Button("vbox btn2");
        vboxBtn2.setId("vboxBtn2");
        vbox.getChildren().add(vboxBtn2);

        Label vboxLb1 = new Label("vbox lb11");
        vboxLb1.setId("vboxLb1");
        vbox.getChildren().add(vboxLb1);
        BorderPane borderPane1 = new BorderPane();
        vbox.getChildren().add(borderPane1);
        Button borderPaneBtn1 = new Button("borderPaneBtn1" );
        borderPaneBtn1.setId("borderPaneBtn1");
        borderPane1.setCenter(borderPaneBtn1);
        Rectangle rect = new Rectangle(50, 50);
        vbox.getChildren().add(rect);
        rootPane.getChildren().add(stackPane);
        vboxLb1.setGraphic(rect);
        Button doAccept = new Button("Accept");
        rootPane.getChildren().add(doAccept);
        Button dragObject = new Button("Drag Object");
        dragButton = new Button("Drag Button");

        rootPane.getChildren().add(dragButton);


        //SceneGraphView graphView = new SceneGraphView(stackPane);
        SceneGraphView graphView = new SceneGraphView();
        
        StackPane editorStackPane = new StackPane();
        Scene tvScene = new Scene(graphView);
        Label statusBar = new Label("It is STAUS BAR");
        graphView.setStatusBar(statusBar);
        
        vboxBtn1.setOnAction(a -> {
            if ( graphView.getRootNode() == stackPane ) {
                graphView.setRootNode(vbox);
                graphView.setStatusBar(null);
            } else {
                graphView.setRootNode(stackPane);
            }
        });
        
        
        
        Stage tvStage = new Stage();
        tvStage.setHeight(300);
        tvStage.setWidth(300);
        
        tvStage.setScene(tvScene);
        
        rootPane.getChildren().add(editorStackPane);

        Scene scene = new Scene(rootPane);

        stage.setTitle("Test EditorTreeView");
        stage.setScene(scene);

        Label lb = new Label("label-graphic");
        Label paneLb = new Label("pane label");
        System.err.println("vbox.getChildren().size()=" + vbox.getChildren().size());
        vbox.getChildren().forEach(c -> {
            //System.err.println("VBOX c = " + c);
        });

        stage.setOnShown(ev -> {
//            rootPane.minHeightProperty().bind(rootPane.getScene().heightProperty());
        });
        //edt.prefHeightProperty().bind(tvStage.heightProperty());
        //edt.prefHeightProperty().bind(graphView.heightProperty());
        graphView.setStyle("-fx-background-color: yellow");
        stage.show();
        tvStage.show();
        //edt.windowShown(null);
/*        System.err.println("graphView maxW = " + graphView.getWidth());
        System.err.println("graphView maxH = " + graphView.getHeight());
        System.err.println("--------");
        System.err.println("tv maxW = " + graphView.getTreeView().getWidth());
        System.err.println("tv maxH = " + graphView.getTreeView().getHeight());
        System.err.println("--------");
        
        System.err.println("pane maxW = " + graphView.getTreeViewPane().getWidth());
        System.err.println("pane maxH = " + graphView.getTreeViewPane().getHeight());
*/        
        VBox nvb1 = new VBox(nlb1);
        Scene nscene = new Scene(nvb1);
        Stage nstage1 = new Stage();
        nstage1.setScene(nscene);
        nstage1.setX(10);
        nstage1.setY(10);
//        vboxBtn2.addEventHandlerr(NodeDragEvent.NODE_DRAG, 
        doAccept.setId("do-accept");
        doAccept.getStyleClass().add("do-accept-class");
        doAccept.getStyleClass().add("uuid-f53db037-2e33-4c68-8ffa-06044fc10f81");
        vboxBtn2.setOnAction(a -> {
        });
        
        
        doAccept.setOnAction(a -> {
            //NodeDragEvent de = new NodeDragEvent(null);
            //vboxBtn2.fireEvent(de);
            //tv.fireEvent(de);
        });
        NodeDragManager dm = new NodeDragManager();
        dm.enableDragAndDrop(dragButton,doAccept);
        //dm.enableDragAndDrop(doAccept);
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

