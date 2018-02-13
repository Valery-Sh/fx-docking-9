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
package org.vns.javafx.dock.api.demo;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestPalette01 extends Application {

    public void start(Stage stage) {
        stage.setTitle("Palette Stage");
        VBox vbox = new VBox();
        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color: aqua");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(350);
        stage.setWidth(350);
        stage.setAlwaysOnTop(true);

        TreeTableView<PaletteItem> treeTableView = new TreeTableView<>();
        TreeTableColumn<PaletteItem, Label> col01 = new TreeTableColumn<>("Palette Items");
        col01.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<PaletteItem, Label> p) -> {
                    //p.getValue().setGraphic(p.getValue().getValue());
                    ImageView icon = new ImageView(
                            new Image(getClass().getResourceAsStream("/org/vns/javafx/dock/api/resources/combo-box-16x16.png"))
                    );
                    //p.getValue().setGraphic(icon);
                    return p.getValue().getValue().labelProperty();
                    //return ll.textProperty();
                });

        col01.prefWidthProperty().bind(treeTableView.widthProperty().subtract(10));
        treeTableView.widthProperty().addListener(new WidthHandler(treeTableView));

        treeTableView.getColumns().add(col01);

        PaletteModel model = createDefaultPaleteModel(treeTableView);

        TreeItem<PaletteItem> rootItem = model.buildTree();
        rootItem.setExpanded(true);
        Button btn1 = new Button("btn1");
        vbox.getChildren().add(btn1);
        btn1.setOnAction(a -> {
            Label rootLb = treeTableView.getRoot().getValue().getLabel();
            double w = rootLb.getLayoutBounds().getWidth();
            System.err.println("root label width = " + w);
            Text tx = new Text(rootLb.getText());
            tx.setFont(rootLb.getFont());
            System.err.println("root text width = " + tx.getLayoutBounds().getWidth());

            for (int i = 0; i < model.getCategories().size(); i++) {
                TreeItem<PaletteItem> cit = new TreeItem(model.getCategories().get(i));

                System.err.println("Category = " + cit.getValue().getLabel().getParent());

                for (int j = 0; j < model.getCategories().get(i).getItems().size(); j++) {
                    TreeItem<PaletteItem> it = new TreeItem(model.getCategories().get(i).getItems().get(j));
                    Label iLb = model.getCategories().get(i).getItems().get(j).getLabel();
                    //System.err.println("item = " + it.getValue().getLabel().getParent());
                    System.err.println("--------------------------------------"); 
                    double iw = rootLb.getLayoutBounds().getWidth();
                    System.err.println("  --- label width = " + iw);
                    System.err.println("  --- label graphic = " + iLb.getGraphic());
                    Text itx = new Text(iLb.getText());
                    itx.setFont(iLb.getFont());
                    
                    System.err.println("   --- text width = " + itx.getLayoutBounds().getWidth());
                    
                }
            }

            System.err.println("width = " + treeTableView.getRoot().getValue().getLabel().getLayoutBounds().getWidth());
        });
        vbox.getChildren().add(treeTableView);
        stage.show();
        System.err.println("!!! " + rootItem.getValue().getLabel().getParent());

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public PaletteModel createDefaultPaleteModel(TreeTableView<PaletteItem> table) {
        PaletteModel model = new PaletteModel(table);
        Label lb = new Label("Containers");
        PaletteCategory pc = model.addCategory("containers", lb);
        lb.getStyleClass().add("tree-item-font-bold");
        
        lb = new Label("VBox");
        lb.getStyleClass().add("tree-item-node-vbox");
        lb.applyCss();
        pc.addItem(lb);

        lb = new Label("HBox");
        pc.addItem(lb);
        lb.getStyleClass().add("tree-item-node-hbox");
        lb.applyCss();
        
        lb = new Label("Shapes");
        lb.getStyleClass().add("tree-item-font-bold");
        pc = model.addCategory("shapes", lb);
        lb.applyCss();        
        
        lb = new Label("Rectangle");
        pc.addItem(lb);
        lb.getStyleClass().add("tree-item-node-rectangle");
        lb.applyCss();
        
        lb = new Label("Arc111111111111111111111111111111111111111");
        pc.addItem(lb);
        //lb.getStyleClass().add("tree-item-node-rectangle");
        ImageView icon = new ImageView(
                            new Image(getClass().getResourceAsStream("/org/vns/javafx/dock/api/resources/combo-box-16x16.png"))
                    );
        lb.setGraphic(icon);
        lb.applyCss();
        System.err.println("ARC GRAPHIC = " + lb.getGraphic());
        return model;
    }

    public static class PaletteItem {

        //private TreeTableView<PaletteItem> treeTableView;
        private ObjectProperty<Label> label = new SimpleObjectProperty<>();

        //public PaletteItem(TreeTableView<PaletteItem> treeTableView, Label lb) {
        public PaletteItem(Label lb) {
            //this.treeTableView = treeTableView;
            this.label.set(lb);
        }

        public Label getLabel() {
            return label.get();
        }

        public void setLabel(Label label) {
            this.label.set(label);
        }

        public ObservableValue<Label> labelProperty() {
            return label;
        }

    }

    public static class PaletteCategory extends PaletteItem {

        private StringProperty id = new SimpleStringProperty();
        private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();

        public PaletteCategory(String id, Label lb) {
            super(lb);
            this.id.set(id);
        }

        public ObservableList<PaletteItem> getItems() {
            return items;
        }

        public String getId() {
            return id.get();
        }

        public void setId1(String id) {
            this.id.set(id);
        }

        public PaletteItem addItem(Label label) {
            PaletteItem item = new PaletteItem(label);
            items.add(item);
            return item;
        }

    }

    public static class PaletteModel {

        private final TreeTableView<PaletteItem> treeTableView;

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();
        //private final ObservableMap<String, ObservableList<PaletteItem>> categoryItems = FXCollections.observableHashMap();
        //private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();

        public PaletteModel(TreeTableView<PaletteItem> treeTableView) {
            this.treeTableView = treeTableView;
            //init();
        }

        public PaletteItem getRoot() {
            return new PaletteItem(new Label("Palette Root"));
        }

        public TreeItem<PaletteItem> buildTree() {
            TreeItem root = new TreeItem(getRoot());
            treeTableView.setRoot(root);
            for (int i = 0; i < categories.size(); i++) {
                TreeItem cit = new TreeItem(categories.get(i));
                root.getChildren().add(cit);
                for (int j = 0; j < categories.get(i).getItems().size(); j++) {
                    TreeItem it = new TreeItem(categories.get(i).getItems().get(j));
                    cit.getChildren().add(it);
                }
            }
            return root;
        }

        public ObservableList<PaletteCategory> getCategories() {
            return categories;
        }

        public PaletteCategory addCategory(String id, Label label) {
            PaletteCategory c = new PaletteCategory(id, label);
            categories.add(c);
            return c;
        }

        public PaletteCategory getCategory(String id) {
            PaletteCategory retval = null;
            for (PaletteCategory c : categories) {
                if (c.getId().equals(id)) {
                    retval = c;
                    break;
                }
            }

            return retval;
        }

    }

    public static class WidthHandler implements ChangeListener<Number> {

        private final TreeTableView treeTableView;

        public WidthHandler(TreeTableView treeTableView) {
            this.treeTableView = treeTableView;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            System.err.println("WIDTH CHANGED");
        }

    }
}
