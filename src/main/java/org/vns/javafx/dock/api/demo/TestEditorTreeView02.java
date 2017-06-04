package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
import org.vns.javafx.dock.api.editor.DragGesture;
import org.vns.javafx.dock.api.editor.DragManager;
import org.vns.javafx.dock.api.editor.DragNodeGesture;
import org.vns.javafx.dock.api.editor.EditorUtil;
import org.vns.javafx.dock.api.editor.SceneGraphView;
import org.vns.javafx.dock.api.editor.NodeDragEvent;
import org.vns.javafx.dock.api.editor.NodeDragManager;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class TestEditorTreeView02 extends Application {

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
        tabContent2.setOnAction(v -> {

            System.err.println("tabContent2.getParent=" + tabContent2.getParent());
        });

        tab2.setContent(tabContent2);
        tabPane.getTabs().add(tab2);

        stackPane.getChildren().add(tabPane);
        stackPane.getChildren().add(tabPane2);

        VBox vbox2 = new VBox();
        vbox2.setId("vbox2");
        Button vbox2b1 = new Button("vbox2 b1");
        vbox2b1.setOnAction(a -> {
            System.err.println("vbox1 has scene  ");

        });
        vbox2.getChildren().add(vbox2b1);
        Button vbox2b2 = new Button("vbox2 b2");
        vbox2.getChildren().add(vbox2b2);
        TreeView tt = new TreeView() {
            public void v() {

            }
        };

        vbox2b2.setOnAction(a -> {
            System.err.println("vbox1 has scene  " + vbox2b1.getScene());

        });

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
/*        vboxBtn1.setOnDragDetected(ev -> {
            Dragboard dragboard = vboxBtn1.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragNodeGesture(vboxBtn1);
            ((DragNodeGesture) dg).setSourceGestureObject(vboxBtn1);
            vboxBtn1.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
//            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
//            mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });
*/
        vboxBtn1.setOnDragDetected(ev -> {
            Dragboard dragboard = dragButton.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragNodeGesture(vboxBtn1);
            dragButton.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
//            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
            
        });

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

        this.dragButton.setOnDragDetected(ev -> {
            System.err.println("isStill = " + ev.isStillSincePress());
            System.err.println("isSynth = " + ev.isSynthesized());
            Dragboard dragboard = dragButton.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            Tab tab = new Tab("New Tab 1");
            DragGesture dg = new DragNodeGesture(dragButton, tab);
            System.err.println("App dragButton ");
            dragButton.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            
            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
//            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
//            mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });

/*        rootPane.setOnDragOver(ev -> {

            if (ev.getDragboard().hasString()) {
                long x = Math.round(ev.getX());
                long y = Math.round(ev.getY());
                Point2D p = new Point2D(x, y);

                if (!p.equals(mpt)) {
                    System.err.println("1 rootPane.setOnDragOver " + ev.getX() + "; " + ev.getY());
//                    mpt = p;
                }
                ev.acceptTransferModes(TransferMode.COPY);
            }
            ev.consume();
        });
*/        
        SceneGraphView edt = new SceneGraphView(stackPane);
        //Pane editorPane = edt.initialize(stackPane);
        
        //stackPane.setStyle("-fx-background-color: yellow");   
        vboxBtn1.setOnAction(a -> {
            
        });
        vboxBtn2.setOnMouseClicked(a -> {
            System.err.println("Button vboxbtn2 clicked");
        });
        
        dragButton.setOnMouseClicked(ev -> {
        });
        
        StackPane editorStackPane = new StackPane();

        
        //editorPane.minHeightProperty().bind(editorStackPane.heightProperty());
        Scene tvScene = new Scene(edt);
        Stage tvStage = new Stage();
        tvStage.setHeight(300);
        tvStage.setWidth(300);
        
        tvStage.setScene(tvScene);
        
        rootPane.getChildren().add(editorStackPane);

        vboxBtn2.setOnAction(a -> {
        });

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
            rootPane.minHeightProperty().bind(rootPane.getScene().heightProperty());
        });
        stage.show();
        tvStage.show();
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
        vboxBtn2.addEventHandler(NodeDragEvent.NODE_DRAG, new EventHandler<NodeDragEvent>(){
            @Override
            public void handle(NodeDragEvent event) {
                System.err.println("Button vboxBtn2 NodeDragEvent handler " + vboxBtn2.getScene().lookup("#do-accept"));
                System.err.println("    --- 1 NodeDragEvent handler " + vboxBtn2.getScene().lookup(".do-accept-class"));
                System.err.println("    --- 2 NodeDragEvent handler " + vboxBtn2.getScene().getRoot().lookup(".do-accept-class"));
                System.err.println("    --- 3 NodeDragEvent handler " + vboxBtn2.getScene().getRoot().lookup(".uuid-f53db037-2e33-4c68-8ffa-06044fc10f81"));
                
            }
        });
        
        doAccept.setOnAction(a -> {
            //NodeDragEvent de = new NodeDragEvent(null);
            //vboxBtn2.fireEvent(de);
            //tv.fireEvent(de);
        });
        
        DragManager dm = NodeDragManager.getInstance();
        //dm.enableDragAndDrop(dragButton,doAccept);
        dm.enableDragAndDrop("new Text",doAccept);
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
