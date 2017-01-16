package org.vns.javafx.dock.api.demo;

import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;
import org.vns.javafx.dock.api.TopNodeHelper;

/**
 *
 * @author Valery
 */
public class TestCommon extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        DockSplitPane sp = new DockSplitPane();
        Button btn01 = new Button("sp btn01");
        sp.getItems().addAll(btn01);
        
        System.err.println("1 btn01.parent=" + btn01.getParent());
        Platform.runLater(() -> {
            System.err.println("2 btn01.parent=" + btn01.getParent());
        });
        
        StackPane root = new StackPane();
        scene = new Scene(root, 250, 250);
        HBox pn01 = new HBox();
        pn01.setStyle("-fx-background-color: yellow");
        pn01.setId("id: pn01");
        HBox pn02 = new HBox();
        pn02.setId("id: pn02");
        pn02.setStyle("-fx-background-color: aqua");
        root.getChildren().addAll(pn01, pn02);
        Button pn01Btn = new Button("To pn02");
        Button pn02Btn = new Button("To pn01");
        pn01.getChildren().add(pn01Btn);
        pn01.getChildren().add(sp);
        

        System.err.println("5 [pn01Btn.parent=" + pn01Btn.getParent()); // not null
        System.err.println("6 btn01.parent=" + btn01.getParent()); // null
        Platform.runLater(() -> {
            System.err.println("6 btn01.parent=" + btn01.getParent()); //not null SplitPaneSkin$Content
            System.err.println("6 btn01.parent.parent=" + btn01.getParent().getParent()); //not null SplitPaneSkin$Content    
            // the nets will print size=1
            System.err.println("btn01.getParent().getParent().getChilds=" + ((DockSplitPane) btn01.getParent().getParent()).getChildren().size());
        });

        pn02.getChildren().add(pn02Btn);
        Label lb01 = new Label("VVVV");
        lb01.getStyleClass().add("tttt");
        pn02.getChildren().add(lb01);
        pn01Btn.setOnAction(a -> {
            System.err.println("=== SIZE=" + lb01.getCssMetaData().size());
            lb01.getCssMetaData().forEach(s -> {
                System.err.println("==== s=" + s.getProperty());
            });
            
/*            System.err.println("--- in pn01");
            Platform.runLater(() -> {
                System.err.println("(0) id = " + root.getChildren().get(0).getId());
                System.err.println("(1) id = " + root.getChildren().get(1).getId());
                List<Stage> stgList = StageHelper.getStages();
                System.err.println("stage-0 = " + stgList.get(0).getTitle());
                System.err.println("stage-1 = " + stgList.get(1).getTitle());
            });
*/            
            pn02.toFront();
            
        });
        pn02Btn.setOnAction(a -> {
/*            System.err.println("--- in pn02");
            Platform.runLater(() -> {
                System.err.println("(0) id = " + root.getChildren().get(0).getId());
                System.err.println("(1) id = " + root.getChildren().get(1).getId());
                List<Stage> stgList = StageHelper.getStages();
                System.err.println("stage-0 = " + stgList.get(0).getTitle());
                System.err.println("stage-1 = " + stgList.get(1).getTitle());
            });
*/
            pn01.toFront();
            
        });

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();

        // Next Stage
        
        HBox sp2 = new HBox();
        Scene sc02 = new Scene(sp2);
        Button sp2Btn01 = new Button("sp2Btn01");
        Button sp2Btn02 = new Button("sp2Btn02");
        
        sp2Btn02.setOnAction(a -> {
            if ( pn02.isVisible()) {
                pn02.setVisible(false);
            } else {
                pn02.setVisible(true);
            }
        });
        
        sp2Btn01.setOnAction(a -> {
            //System.err.println("TOP NODE: " + TopNodeHelper.getHigherNode(pn01, pn02));
            Platform.runLater(() -> {
/*                System.err.println(" --- MY STAGE CLICKED");
                List<Stage> stgList = StageHelper.getStages();
                System.err.println("stage-0 = " + stgList.get(0).getTitle());
                System.err.println("stage-1 = " + stgList.get(1).getTitle());
                for ( Node n : root.getChildrenUnmodifiable()) {
                    System.err.println("NODE of root: " + n);
                }
*/              
                root.getChildren().forEach(n -> {
                    System.err.println("+++ root c=" + n);
                });
                
                System.err.println("");
                Point2D pt1 = btn01.localToScreen(3,5);
                Point2D pt2 = pn01Btn.localToScreen(2,4);
                Point2D pt3 = pn02Btn.localToScreen(1,3);
                List<Node> ls = TopNodeHelper.getNodes(stage,pt1);
                
                System.err.println("==========================================");
                
                System.err.println("Button of HBox.id=id: pn01 in SplitPane sp");
                System.err.println("List of all Nodes ");
                ls.forEach(n -> {
                    System.err.println(" --- node: " + n);
                });
                System.err.println("Top Node:  " + TopNodeHelper.getTopNode(stage, pt1));
                System.err.println("==========================================");
                
                System.err.println("******* only SplitPanes (predicate) *********");
                
                System.err.println("Button of HBox.id=id: pn01 in SplitPane sp");
                System.err.println("List of all Nodes ");
                ls.forEach(n -> {
                    System.err.println(" --- node: " + n);
                });
                System.err.println("Top Node:  " + TopNodeHelper.getTopNode(stage, pt1, n -> {return (n instanceof SplitPane);}));
                System.err.println("==========================================");
                        
                
            });
        });
        sp2.getChildren().addAll(sp2Btn01,sp2Btn02);
        Stage stage2 = new Stage();
        stage2.setTitle("MY ATAGE");
        stage2.setScene(sc02);
        stage2.setY(500);
        stage2.show();
        
        Stage stage3 = new Stage();
        
        Button b3 = new Button("B111111111111 3");
        Button b4 = new Button("B111111111111 4");
        HBox hb3 = new HBox(b3,b4);
        HBox hb4 = new HBox();
        VBox vb3 = new VBox(hb3,hb4);
        stage3.setTitle("STAGE 3");
        StackPane sp3 = new StackPane();
        sp3.getChildren().add(vb3);
        Scene sc3 = new Scene(sp3);
        stage3.setScene(sc3);
        stage3.setY(300);
        stage3.setX(300);
        stage3.show();
        
        

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
