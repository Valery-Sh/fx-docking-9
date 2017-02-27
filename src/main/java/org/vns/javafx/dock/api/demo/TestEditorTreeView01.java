package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
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
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.EditorTreeView;
import org.vns.javafx.dock.api.editor.ItemValue;

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
        StackPane stackPane = new StackPane();
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

        this.dragButton.setOnDragDetected(ev -> {
            Dragboard dragboard = nlb1.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
            mpt = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        });

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
            TreeItem ii;

            ev.consume();
        });
        EditorTreeView edt = new EditorTreeView();
        Pane editorPane = edt.createEditorPane(stackPane);

        TreeView<ItemValue> tv = edt.getTreeView();
        vboxBtn1.setOnAction(a -> {
            System.err.println("edt.findTreeItem=" + tv.getRow(edt.findTreeItem(vboxBtn1)));
        });

//        Pane editorPane = edt.getEditorPane();
        rootPane.getChildren().add(editorPane);

//        TreeItem<ItemValue> tib1 = edt.createItem(stackPane);
//        tv.setRoot(tib1);
        //tv.setStyle("-fx-background-color: yellow");
        tv.getStyleClass().add("myTree");
        //rootTreeViewPane.getChildren().add(tv);
        tv.relocate(5, 0);
        tv.getRoot().setExpanded(true);
 /*        tv.setOnMouseClicked(ev -> {
            TreeItem it = edt.getTreeItem(ev.getScreenX(), ev.getScreenY());
            //edt.drawRectangle(tv.getRoot());
            if ( it != null ) {
                edt.drawRectangle(it);
                System.err.println("it != root it.idx=" + tv.getRow(it));
            } else {
                edt.drawRectangle(tv.getRoot());
            }
        });
         */
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
            TreeItem item = tv.getTreeItem(2);
            vbox.getChildren().forEach(c -> {
                //System.err.println("VBOX c = " + c);
            });

            //System.err.println("tv.getChildren().size=" + tv.getRoot().getParent());            
            //System.err.println("BOUNDS[] = " + edt.getLevelBounds(item)[0]);
            //System.err.println("BOUNDS[] = " + edt.getLevelBounds(item)[1]);            
            //System.err.println("BOUNDS[] = " + edt.getLevelBounds(item)[2]);            
        });
        stage.setOnShown(ev -> {
            //DockUtil.print(editorPane);
/*            TreeItem<ItemValue> tib1 = edt.createItem(stackPane);
            tv.setRoot(tib1);
            //tv.setStyle("-fx-background-color: yellow");
            tv.getStyleClass().add("myTree");
            editorPane.getChildren().add(tv);
            tv.relocate(5, 0);
            tv.getRoot().setExpanded(true);
*/
        });
        stage.show();
        VBox nvb1 = new VBox(nlb1);
        Scene nscene = new Scene(nvb1);
        Stage nstage1 = new Stage();
        nstage1.setScene(nscene);
        nstage1.setX(10);
        nstage1.setY(10);

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
