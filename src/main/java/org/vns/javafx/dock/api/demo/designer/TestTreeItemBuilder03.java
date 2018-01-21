/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.demo.designer;

import java.util.Set;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;
import org.vns.javafx.designer.ContentProperty;
import org.vns.javafx.designer.ItemValue;
import org.vns.javafx.designer.NodeDescriptor;
import org.vns.javafx.designer.NodeDescriptorRegistry;
import org.vns.javafx.designer.SceneGraphView;
import org.vns.javafx.dock.api.Dockable;



public class TestTreeItemBuilder03 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        VBox root = new VBox();
        Label btn1Graphic = new Label("btn1Graphic");
        
        root.setId("ROOT");
        Button btn1 = new Button("btn1");
        btn1.setGraphic(btn1Graphic);
        
        root.getChildren().add(btn1);
        BorderPane borderPane1 = new BorderPane();
        HBox hbox1 = new HBox();
        borderPane1.setRight(hbox1);
        root.getChildren().add(borderPane1);
        
        TabPane tabPane1 = new TabPane();
        root.getChildren().add(tabPane1);
        Tab tab1 = new Tab("Tab01");
        tabPane1.getTabs().add(tab1);
        Arc arc1 = new Arc();
        root.getChildren().add(arc1);
        
        //TreeItemBuilder builder = new TreeItemBuilder();
        //TreeItem rootItem = builder.build(root);
        //TreeView treeView = new TreeView(rootItem);
        SceneGraphView sceneGraphView = new SceneGraphView(root);
        Scene tvScene = new Scene(sceneGraphView);
        Stage tvStage = new Stage();
        tvStage.setHeight(300);
        tvStage.setWidth(300);
        
        tvStage.setScene(tvScene);        
        //rootItem.setExpanded(true);
        //rootItem.setValue(itemLabel);        
        //root.getChildren().add(treeView);
        //customizeCell(treeView);
        Label lbl1 = new Label("lbl1");
        //btn1.setGraphic(lbl1);
        stage.sizeToScene();
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setTitle("Stage TestTreeItemBuilder01");
        //NodeDescriptor labeledNd = new NodeDescriptor(btn1);
        stage.setScene(scene);
        ObservableList<Node> ol = FXCollections.observableArrayList();
        stage.setOnShown(s -> {
/*            System.err.println("titleProperty = " + labeledNd.getTitleProperty()); 
            System.err.println("name = " + child1.getName()); 
            System.err.println("isPlaceholder  = " + child1.isPlaceholder()); 
            System.err.println("placeholder isHideIfNull = " + child1.isHideIfNull()); 
            System.err.println("placeholder title = " + child1.getTitle()); 
            
            System.err.println("placeholderStyleClass  = " + child1.getStyleClass()); 
*/
            System.err.println("isAssignable " + Set.class.isAssignableFrom(ol.getClass()));
            
        });
        stage.setMinHeight(100);
        root.setMinHeight(100);
        stage.setMinWidth(400);
        root.setMinWidth(400);

        stage.show();
        tvStage.show();
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
    protected void customizeCell(TreeView treeView) {
        TreeView<ItemValue> t = treeView;
        t.setCellFactory((TreeView<ItemValue> tv) -> {
            TreeCell<ItemValue> cell = new TreeCell<ItemValue>() {
                @Override
                public void updateItem(ItemValue value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty || value == null) {
                        setText(null);
                        setGraphic(null);
                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.DRAG_OVER, (SceneGraphView.TreeItemCellDragEventHandler) o[0]);
                            }
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                    } else {
                        this.setGraphic(value.getCellGraphic());
                        if (value.getTreeItemObject() instanceof Node) {
                            setId(((Node) value.getTreeItemObject()).getId());
                        }
                    }
                }
            };
            return cell;
        });
    }

}
