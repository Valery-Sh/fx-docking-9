package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockSideBar.Rotation;
import org.vns.javafx.dock.DockNode;

/**
 *
 * @author Valery
 */
public class TestDockSideBar1 extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {

        StackPane root = new StackPane();

        stage.setTitle("Test DockSideBar");
        SplitPane p = new SplitPane();
        p.setId("Pane p");
        Button pBtn = new Button("Pane Btn ");
        p.getItems().add(pBtn);
        
        DockSideBar sideBar01 = new DockSideBar();
        sideBar01.setOrientation(Orientation.VERTICAL);
        sideBar01.setRotation(Rotation.UP_DOWN);
        sideBar01.setSide(Side.RIGHT);

        //sideBar01.setHideOnExit(true);
        //borderPane.getChildren().add(sideBar01);
        root.setPrefHeight(300);
        root.setPrefWidth(300);

        Button b01 = new Button("Change Rotate Angle");

        //((Region)root.getRight()).setMaxWidth(0);
        Scene scene = new Scene(root);
        //scene.getRoot().setStyle("-fx-background-color: yellow");
        root.getChildren().add(p);
        
        System.err.println("SP PARENT = " + pBtn.getParent());
        root.getChildren().remove(p);

        DockNode dn01 = new DockNode();
        dn01.setPrefHeight(100);
        dn01.getContext().setTitle("DockNode: dn01");
        b01.setOnAction(a -> {
            if (null != sideBar01.getRotation()) {
                switch (sideBar01.getRotation()) {
                    case DEFAULT:
                        sideBar01.setRotation(Rotation.UP_DOWN);
                        break;
                    case UP_DOWN:
                        sideBar01.setRotation(Rotation.DOWN_UP);
                        break;
                    case DOWN_UP:
                        sideBar01.setRotation(Rotation.DEFAULT);
                        break;
                    default:
                        break;
                }
            }

        });

        Button b02 = new Button("Change Orientation");
        Button b03 = new Button("Change Side");
        b03.setOnAction(a -> {

            if (null != sideBar01.getSide()) switch (sideBar01.getSide()) {
                case RIGHT:
                    sideBar01.setSide(Side.LEFT);
                    break;
                case LEFT:
                    sideBar01.setSide(Side.TOP);
                    break;
                case TOP:
                    sideBar01.setSide(Side.BOTTOM);
                    break;
                case BOTTOM:
                    sideBar01.setSide(Side.RIGHT);
                    break;
                default:
                    break;
            }

        });

        VBox vb = new VBox();
        vb.getChildren().addAll(b01, b02, b03);
        root.getChildren().add(vb);
        //borderPane.getChildren().add(b02);
        //StackPane.setAlignment(vb,Pos.CENTER_LEFT);

        root.getChildren().add(sideBar01);
        StackPane.setAlignment(sideBar01, Pos.CENTER_RIGHT);

        b02.setOnAction(a -> {
            if (sideBar01.getOrientation() == Orientation.VERTICAL) {
                sideBar01.setOrientation(Orientation.HORIZONTAL);
            } else if (sideBar01.getOrientation() == Orientation.HORIZONTAL) {
                sideBar01.setOrientation(Orientation.VERTICAL);
            }

        });

        //sideBar01.dock(dn01);
        DockNode dn02 = new DockNode();
        //dn02.setPrefSize(150,100);
        //dn02.setMinSize(100,50);
        
        dn02.setId("dn02");
        VBox vb2 = new VBox();
        Button dn02Btn = new Button("------------- dn02 button ---------------");
        dn02.setContent(dn02Btn);
        dn02Btn.setOnAction(a -> {
            System.err.println("SFFFFFFFFFF" + dn02.getContext().isFloating());
            System.err.println(" === " + dn02.getScene().getWindow());
           // ((SidePaneController)sideBar01.getTargetContext()).cont.changeSize();
        });
        //dn02.setContent(vb2);
        //vb2.getChildren().add(new Button("dn02 button"));
        //dn02.setPrefHeight(100);
        dn02.getContext().setTitle("DockNode: dn02");
        dn02.setId("dn02");
        //sideBar01.dock(dn02);
        //scene.getRoot().setStyle("-fx-background-color: yellow");
        //sideBar01.getDelegate().setStyle("-fx-padding: 0;");
        //sideBar01.setStyle("-fx-padding: 0; -fx-border-width: 0; -fx-border-insets: 0,0,0,0; -fx-border-color: transparent");
        //sideBar01.getDelegate().setStyle("-fx-padding: 0; -fx-border-width: 0;  -fx-border-insets: 0,0,0,0;-fx-border-color: transparent");
        DockNode dn03 = new DockNode();

        //dn03.setPrefHeight(100);
        dn03.getContext().setTitle("DockNode: dn03");
        DockNode dn04 = new DockNode();
        dn04.getContext().setTitle("DockNode: dn04");

//        sideBar01.getItems().add(dn03);
        //sideBar01.dock(dn02);
        //sideBar01.dock(dn03);     
        sideBar01.getItems().addAll(dn02, dn03, dn04);
//        sideBar01.setMaxSize(sideBar01.getDelegate().getMaxWidth(), sideBar01.getDelegate().getMaxHeight());
//        sideBar01.setMinSize(sideBar01.getDelegate().getMinWidth(), sideBar01.getDelegate().getMinHeight());        
        //stage.setTitle("Main Dockable and Toolbar");
        stage.setScene(scene);

        stage.setOnShown(e -> {
            //sideBar01.setHideOnExit(true);
//        sideBar01.setPrefSize(sideBar01.getDelegate().getWidth(), sideBar01.getDelegate().getHeight());
            //sideBar01.setMinSize(sideBar01.getDelegate().getMinWidth(), sideBar01.getDelegate().getMinHeight());        
            //System.err.println("sideBar01.getWidth()=" + sideBar01.getWidth());
//            System.err.println("sideBar01.toolBar.getWidth()=" + sideBar01.getDelegate().getWidth());
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

    private void initSceneDragAndDrop(Scene scene) {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() || db.hasUrl()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String url = null;
            if (db.hasFiles()) {
                url = db.getFiles().get(0).toURI().toString();
            } else if (db.hasUrl()) {
                url = db.getUrl();
            }
            if (url != null) {
                //songModel.setURL(url);
                //songModel.getMediaPlayer().play();
            }
            System.err.println("DROPPED");
            event.setDropCompleted(url != null);
            event.consume();
        });
    }

}
