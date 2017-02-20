package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.tmp.EditorTreeView;
import org.vns.javafx.dock.api.editor.tmp.TreeItemEx;
import org.vns.javafx.dock.api.editor.tmp.TreeItemRegistry;

/**
 *
 * @author Valery
 */
public class TestEditorTreeView extends Application {

    private Line vertLine = new Line();
    private Button dragButton;
    Point2D mpt = new Point2D(Double.MAX_VALUE,Double.MAX_VALUE);
    @Override
    public void start(Stage stage) throws Exception {
        
        String s = java.util.UUID.randomUUID().toString();
        System.err.println("uidd-" + s );
        HBox rootPane = new HBox();
        StackPane stackPane = new StackPane();
        VBox vbox = new VBox();
        vbox.setId("vbox");
        stackPane.getChildren().add(vbox);
        Button vboxBtn1 = new Button("vbox btn1");
        vbox.getChildren().add(vboxBtn1);
        Rectangle rect = new Rectangle(50, 50);
        vbox.getChildren().add(rect);        
        rootPane.getChildren().add(stackPane);

        Button doAccept = new Button("Accept");
        rootPane.getChildren().add(doAccept);
        Button dragObject = new Button("Drag Object");
        dragButton = new Button("Drag Button");

        rootPane.getChildren().add(dragButton);
        
        this.dragButton.setOnDragDetected(ev -> {
            Dragboard dragboard = dragButton.startDragAndDrop(TransferMode.COPY_OR_MOVE);
// Add the source text to the Dragboard
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, "dragButton");
            dragboard.setContent(content);
            ev.consume();
            mpt = new Point2D(Double.MAX_VALUE,Double.MAX_VALUE);
            //System.err.println("DRAG DETECTED");            
        });
        
        rootPane.setOnDragOver(ev -> {
            
            if ( ev.getDragboard().hasString() ) {
                long x = Math.round(ev.getX());
                long y = Math.round(ev.getY());
                Point2D p = new Point2D(x, y);
                
                if ( ! p.equals(mpt) ) {
                    System.err.println("1 rootPane.setOnDragOver " + ev.getX() + "; " + ev.getY());
                    mpt = p;
                }    
                ev.acceptTransferModes(TransferMode.COPY);
                //System.err.println("2 rootPane.setOnDragOver " + ev.getX() + "; " + ev.getY());
                
            }
            ev.consume();
        });
        //rootTreeViewPane.getChildren().add(vertLine);
        //vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //rootTreeViewPane.setId("DOCK PANE");
        EditorTreeView edt = new EditorTreeView();
//        edt.setDragObject(dragObject);

        TreeView<AnchorPane> tv = edt.getTreeView();
        Pane rootTreeViewPane = edt.getRootPane();
        rootPane.getChildren().add(rootTreeViewPane);

        TreeItem<AnchorPane> tib1 = edt.createItem(stackPane);

        tv.setRoot(tib1);
        tv.setStyle("-fx-background-color: yellow");
        rootTreeViewPane.getChildren().add(tv);
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
        doAccept.setOnAction(a -> {
            TreeCell cc = new TreeCell();
            TreeItemRegistry.getInstance().getBuilder(cc);
/*            TreeItemEx it = (TreeItemEx) tv.getTreeItem(2);
            it.accept(lb);
            System.err.println("vboxBtn=" + vboxBtn1.getGraphic());
            TreeItemEx itPane = (TreeItemEx) tv.getTreeItem(0);
            itPane.accept(paneLb);
*/            
        });
        stage.setOnShown(ev -> {
            DockUtil.print(rootTreeViewPane);
        });
        stage.show();
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
