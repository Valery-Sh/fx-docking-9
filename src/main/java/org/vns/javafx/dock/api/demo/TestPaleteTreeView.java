/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.editor.ItemValue;
import org.vns.javafx.dock.api.editor.SceneGraphView;

/**
 *
 * @author Valery
 */
public class TestPaleteTreeView extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        //VBox rootPane = new VBox();
        StackPane rootPane = new StackPane();
        TreeView<PaletteModel> tvt = new TreeView<>();
        
        customizeCell(tvt);
        Label lb1 = new Label("Label1");
        Label lb2 = new Label("Label2");

        Label lb3 = new Label("Label3 ");
        lb3.setOnMouseClicked(e -> {
            System.err.println("MOUSE CLICKED");
        });
        Label lb4 = new Label("Label4");

        Label lb5 = new Label("Label5");
        Label lb6 = new Label("Label6");

        Label lb7 = new Label("Label7");

        PaletteModels models = new PaletteModels(new PaletteModel(lb1, lb2), new PaletteModel(lb3, lb4,"Controls"), new PaletteModel(lb5, lb6), new PaletteModel(lb7, null));
        //TreeItem<PaletteModel> rootItem = new TreeItem<>();

        TreeItem<PaletteModel> root = new TreeItem<>();
        root.setValue(models.getItems().get(0));

        TreeItem<PaletteModel> item1 = new TreeItem<>();
        item1.setValue(models.getItems().get(1));

        TreeItem<PaletteModel> item2 = new TreeItem<>();
        item2.setValue(models.getItems().get(2));

        TreeItem<PaletteModel> item3 = new TreeItem<>();
        item3.setValue(models.getItems().get(3));
        root.getChildren().addAll(item1, item2, item3);
        tvt.setRoot(root);
        
        root.setExpanded(
                true);

        rootPane.setPrefHeight(
                200);
        rootPane.getChildren()
                .add(tvt);
        //rootPane.applyCss();
        Scene scene = new Scene(rootPane, Color.GREEN);

        stage.setScene(scene);

        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(
                null);

    }
    protected void customizeCell(TreeView<PaletteModel> treeView) {
        //TreeView<ItemValue> t = treeView;
        treeView.setCellFactory((TreeView<PaletteModel> tv) -> {
            TreeCell<PaletteModel> cell = new TreeCell<PaletteModel>() {
                @Override
                public void updateItem(PaletteModel value, boolean empty) {
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
                        this.setGraphic(value.getPane());
                    }
                }
            };
            return cell;
        });
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

    public static class PaletteModel {

        private boolean category;
        private String categoryName;

        private ObjectProperty<Label> left = new SimpleObjectProperty<>();
        private ObjectProperty<Label> right = new SimpleObjectProperty<>();
        private Pane pane = new Pane();
        
        public PaletteModel() {
        }

        public PaletteModel(Label left, Label right) {
            this.left.set(left);
            this.right.set(right);
            if ( left != null ) {
                pane.getChildren().add(left);
            }
            if ( right != null ) {
                pane.getChildren().add(right);
            }
            pane.setStyle("-fx-background-color: yellow");
        }
        public PaletteModel(Label left, Label right, String categoryName) {
            this(left, right);
            this.left.set(left);
            this.right.set(right);
            this.categoryName = categoryName;
            if ( categoryName != null ) {
                setCategory(true);
            }
        }

        public Pane getPane() {
            return pane;
        }

        public boolean isCategory() {
            return category;
        }

        public void setCategory(boolean category) {
            this.category = category;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public ObjectProperty<Label> getLeft() {
            return left;
        }

        public ObjectProperty<Label> getRight() {
            return right;
        }

    }

    public static class PaletteModels {

        private ObservableList<PaletteModel> items = FXCollections.observableArrayList();

        public PaletteModels() {
        }

        public PaletteModels(PaletteModel... models) {

            for (PaletteModel pm : models) {
                //System.err.println("model: left = " + pm.getLeft());
                //System.err.println("model: right = " + pm.getRight());
                items.add(pm);
            }
        }

        public ObservableList<PaletteModel> getItems() {
            return items;
        }

    }

    public static class TreeTableRowEx extends TreeTableRow<PaletteModel> {

        @Override
        protected void updateItem(PaletteModel model, boolean empty) {
            super.updateItem(model, empty);
            if (empty) {
                getChildren().clear();
            } else {
                System.err.println("size() = " + getChildren().size());
                System.err.println("   --- getSkin()=" + getSkin());
                getChildren().forEach(n -> {
                    System.err.println("n = " + n.getClass().getName());
                });
                
                    if (getTreeItem() != null) {
                        System.err.println("TreeItem = " + getTreeTableView().getRow(getTreeItem()));
                        System.err.println("   ---  = " + getTreeItem().getValue().getLeft());
                    } else {
                        System.err.println("TreeItem = NULL");
                    }
  //              });
                if (model.isCategory()) {
                    //getChildren().clear();
                    TreeItem<PaletteModel> it = new TreeItem(model.getCategoryName());
                    //getChildren().add(it);
                }
            }
        }

        @Override
        protected ObservableList<Node> getChildren() {
            return super.getChildren();
        }

    }
}
