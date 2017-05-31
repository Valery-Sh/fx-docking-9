package org.vns.javafx.dock.api.demo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.DragGesture;
import org.vns.javafx.dock.api.editor.DragNodeGesture;
import org.vns.javafx.dock.api.editor.EditorUtil;
import org.vns.javafx.dock.api.editor.SceneGraphView;
import org.vns.javafx.dock.api.editor.ItemValue;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class TestEditorTreeView01 extends Application {

    private Line vertLine = new Line();
    private Button dragButton;
    Point2D mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

    @Override
    public void start(Stage stage) throws Exception {
        Label nlb1 = new Label("DRAGGED LABEL");

        String s = java.util.UUID.randomUUID().toString();
        System.err.println("uidd-" + s);

        HBox rootPane = new HBox();
        VBox stackPane = new VBox();
        stackPane.setId("ROOT");
        VBox vbox = new VBox();
        vbox.setId("vbox1");
        vbox.getChildren().add(new Button("bbb"));

        HBox hbox = new HBox();
        hbox.setId("hbox");

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

        /*        GridPane gridPane = new GridPane();
        gridPane.setId("gridpane1");        
        stackPane.getChildren().add(gridPane);
        BorderPane borderPane = new BorderPane();
        gridPane.setId("borderPane1");        
        stackPane.getChildren().add(borderPane);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setId("anchorPane1");        
        stackPane.getChildren().add(anchorPane);
        Accordion accordion = new Accordion();
        accordion.setId("accordion1");        
        stackPane.getChildren().add(accordion);
        FlowPane flowPane = new FlowPane();
        flowPane.setId("flowPane1");        
        stackPane.getChildren().add(flowPane);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("scrollPane1");        
        stackPane.getChildren().add(scrollPane);
        SplitPane splitPane = new SplitPane();
        splitPane.setId("splitPane1");        
        stackPane.getChildren().add(splitPane);
        TitledPane titledPane = new TitledPane();
        titledPane.setId("titledPane1");        
        stackPane.getChildren().add(titledPane);
        
        TilePane tilePane = new TilePane();
        tilePane.setId("tiledPane1");        
        stackPane.getChildren().add(tilePane);       
        TabPane tabPane = new TabPane();
        tabPane.setId("tabPane1");        
        stackPane.getChildren().add(tabPane);       
         */
        Button vboxBtn1 = new Button("vbox btn1");
        vboxBtn1.setId("vboxBtn1");
        vbox.getChildren().add(vboxBtn1);
        vboxBtn1.setOnDragDetected(ev -> {
            Dragboard dragboard = dragButton.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragNodeGesture(vboxBtn1);
            dragButton.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
//            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
            mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });

        Button vboxBtn2 = new Button("vbox btn2");
        vboxBtn2.setId("vboxBtn2");
        vbox.getChildren().add(vboxBtn2);

        Label vboxLb1 = new Label("vbox lb11");
        vboxLb1.setId("vboxLb1");
        vbox.getChildren().add(vboxLb1);

        Rectangle rect = new Rectangle(50, 50);
        vbox.getChildren().add(rect);
        rootPane.getChildren().add(stackPane);
        vboxLb1.setGraphic(rect);
        Button doAccept = new Button("Accept");
        rootPane.getChildren().add(doAccept);
        Button dragObject = new Button("Drag Object");
        dragButton = new Button("Drag Button");

        rootPane.getChildren().add(dragButton);

        /*        this.dragButton.setOnDragDetected(ev -> {
            Dragboard dragboard = dragButton.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragNodeGesture(dragButton);
            Tab tab = new Tab("New Tab 1");
            ((DragNodeGesture) dg).setSourceGestureObject(tab);
            dragButton.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
//            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
            mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });
         */
        rootPane.setOnDragOver(ev -> {

            if (ev.getDragboard().hasString()) {
                long x = Math.round(ev.getX());
                long y = Math.round(ev.getY());
                Point2D p = new Point2D(x, y);

                if (!p.equals(mpt)) {
                    System.err.println("1 rootPane.setOnDragOver " + ev.getX() + "; " + ev.getY());
                    mpt = p;
                }
                ev.acceptTransferModes(TransferMode.COPY);
            }
            ev.consume();
        });
        Button bb1 = new Button("bb1");
        Button bb2 = new Button("bb2");
        VBox vb2 = new VBox(bb1, bb2);
        Scene scene2 = new Scene(vb2);
        Stage stage2 = new Stage();
        stage2.setScene(scene2);
        stage2.setX(10);
        stage2.setY(400);

        stage2.show();

        SceneGraphView edt = new SceneGraphView(stackPane);
        //Pane editorPane = edt.initialize(stackPane);
        PauseTransition pt2 = new PauseTransition(Duration.seconds(3));
        vboxBtn1.setOnAction(a -> {
        });
        StackPane editorStackPane = new StackPane();

        //rootPane.getChildren().add(editorPane);
        editorStackPane.getChildren().add(edt);
        editorStackPane.setStyle("-fx-background-color: red)");
        editorStackPane.minHeightProperty().bind(rootPane.heightProperty());

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
            System.err.println("VBOX c = " + c);
        });

        doAccept.setOnAction(a -> {
        });
        stage.setOnShown(ev -> {
        });
        stage.show();
        VBox nvb1 = new VBox(nlb1);
        Scene nscene = new Scene(nvb1);
        Stage nstage1 = new Stage();
        nstage1.setScene(nscene);
        nstage1.setX(10);
        nstage1.setY(10);

        bb1.setOnMousePressed(e -> {

            bb1.setMouseTransparent(true);
            System.err.println("Source: Mouse pressed");
        });
        bb1.setOnDragDetected(ev -> {
            System.err.println("dragButton = " + bb1);
            Dragboard dragboard = bb2.startDragAndDrop(TransferMode.COPY_OR_MOVE);

            DragGesture dg = new DragNodeGesture(bb1);
            bb1.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);

            ClipboardContent content = new ClipboardContent();
            content.putUrl(NODE_UUID);
            dragboard.setContent(content);
            ev.consume();
            //mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });
/*        bb1.setOnDragDetected(ev -> {
            System.err.println("dragButton = " + bb1);
            dragButton.startFullDrag();
//            Dragboard dragboard = bb1.startDragAndDrop(TransferMode.COPY_OR_MOVE);

            DragGesture dg = new DragNodeGesture(bb1);
            dragButton.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
            ev.consume();
        });
*/        
        bb2.setOnMouseDragOver(e -> System.err.println("Target: drag over " + e.getGestureSource() + "; e.getSource=" + e.getSource()));
        vboxBtn1.setOnMouseDragOver(e -> System.err.println("Target: drag over " + e));
        dragButton.setOnMouseClicked(ev -> {
        });
        dragButton.setOnMouseDragOver(e -> System.err.println("DragButton Target: drag over"));
        bb2.setOnDragOver(ev -> {
            System.err.println("drag over = " + bb2);
            System.err.println("   --- drag over gestureSource = " + ev.getGestureSource());
            System.err.println("   --- drag over        source = " + ev.getSource());

            ev.consume();
            //mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });

        //nstage1.show();
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
