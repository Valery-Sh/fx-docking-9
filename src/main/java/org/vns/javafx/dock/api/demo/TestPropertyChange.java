/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.EnumChoiceBox;
import org.vns.javafx.dock.api.designer.bean.InsetsPropertyEditor;
import org.vns.javafx.dock.api.designer.bean.SliderEditor;

/**
 *
 * @author Valery
 */
public class TestPropertyChange extends Application {

    Stage stage;
    Scene scene;
    Button saveBtn;

       @Override
    public void start(Stage stage) {
   /*     TextField dp = new TextField();
        System.err.println("dp1 = " + dp.textProperty().get());
        
        dp.textProperty().set((String)null);
        System.err.println("dp2 = " + dp.textProperty().get());
    */    
        TreeItem rootItem = new TreeItem("Root Item");
        rootItem.setExpanded(true);
        TreeView treeView = new TreeView(rootItem);
        TreeItem item1 = new TreeItem("item1");
        //item1.setExpanded(false);
        rootItem.getChildren().add(item1);
        TreeItem item1_1 = new TreeItem("item1_1");
        item1.getChildren().add(item1_1);
        TreeItem item1_2 = new TreeItem("item1_2");
        item1.getChildren().add(item1_2);

        TreeItem item2 = new TreeItem("item2");
        rootItem.getChildren().add(item2);

        Button testBtn = new Button("textBtn");
        testBtn.getStyleClass().add(DockTitleBar.StyleClasses.PIN_BUTTON.cssClass());

        Button removeBtn = new Button("remove testBtn");
        
        Button addTestBtn = new Button("add testBtn");
        Button createTestBtn = new Button("createBtn");
        HBox hbox = new HBox();
        VBox root = new VBox(hbox, createTestBtn, removeBtn, addTestBtn);
        //StackPane root = new StackPane(createTestBtn, removeBtn, addTestBtn);
        //StackPane root = new StackPane();
        //root.getChildren().add(treeView);
      
        removeBtn.setOnAction(a -> {
            saveBtn = testBtn;
            root.getChildren().remove(testBtn);
        });

        addTestBtn.setOnAction(a -> {
            //hbox.getChildren().add(testBtn);
            TreeItem item2_1 = new TreeItem("item2_1");
            item2.getChildren().add(item2_1);
        });
        createTestBtn.setOnAction(a -> {

            testBtn.graphicProperty().addListener((v, ov, nv) -> {
                System.err.println("oldValue = " + ov + "; newValue = " + nv);
            });
            root.getChildren().add(testBtn);
        });

        root.setPrefSize(500, 100);
        Scene scene = new Scene(root);
        //stage.initStyle(StageStyle.TRANSPARENT);
        //scene.setFill(Color.TRANSPARENT);
        //root.getStyleClass().add("test-css");
        //root.setStyle("-fx-background-color: blue");
        root.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");        
        hbox.setStyle("-fx-background-color:green");
        
        //hbox.setStyle("-fx-fill:red");
        
       
        NodeOrientation no = NodeOrientation.INHERIT;
        EnumChoiceBox<NodeOrientation> cbox = new EnumChoiceBox<>(NodeOrientation.class); 
        CheckBox ckBox = new CheckBox("Valery");
        //cbox.bindBidirectional(ckBox.nodeOrientationProperty());
        cbox.bind(ckBox.nodeOrientationProperty());

        ckBox.setOnAction(e -> {
             cbox.getPseudoClassStates().forEach(s -> {
                System.err.println("PSEUDO = " + s);
            });
            cbox.getStyleClass().forEach(s -> {
                System.err.println("STYLE = " + s);
            });
           cbox.bindBidirectional(ckBox.nodeOrientationProperty());
           if ( ckBox.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT)  {
               ckBox.setNodeOrientation(NodeOrientation.INHERIT);
           } else {
               ckBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
           }
        });
       
        SliderEditor slider = new SliderEditor(0,1,1);
        InsetsPropertyEditor insetsEditor = new InsetsPropertyEditor(1,2,3,4);
        insetsEditor.setGraphics(null,null,null,null);
        insetsEditor.bindBidirectional(cbox.paddingProperty());
        root.getChildren().addAll(insetsEditor,cbox,ckBox, slider);        
        cbox.setPadding(new Insets(1.9,2,3,4));
//        insetsEditor.bind(cbox.paddingProperty());
/*        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.25f);
        slider.setBlockIncrement(0.1f);
*/
        
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();
        
        PopupControl pc = new PopupControl();
        StackPane pcRoot = new StackPane();
        Button b = new Button("A");
        pcRoot.getChildren().add(b);
        pcRoot.setPrefSize(50,50);
        //pcRoot.setStyle("-fx-background-color: green");
        pcRoot.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");                
        pc.getScene().setRoot(pcRoot);
        //pc.getScene().setFill(Color.RED);
        
        
        pc.show(stage,50,50);
        pc.setWidth(100);
        pc.setHeight(100);
 
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

}
