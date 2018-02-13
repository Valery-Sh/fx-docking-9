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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestPalette02 extends Application {

    public void start(Stage stage) {
        stage.setTitle("Palette Stage");
        VBox vbox = new VBox();
        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color: aqua");

     
        //stage.setWidth(350);
        stage.setAlwaysOnTop(true);

        TreeView<PaletteItemValue> treeView = new TreeView<>();
        treeView.getStyleClass().add("palette");
        treeView.setFixedCellSize(-1);
        
        PaletteModel model = createDefaultPaleteModel(treeView);

        TreeItem<PaletteItemValue> rootItem = model.buildTree();
        rootItem.setExpanded(true);
        customizeCell(treeView);
        Button btn1 = new Button("btn1");
        vbox.getChildren().add(btn1);
        btn1.setOnAction(a -> {
            Label rootLabel = (Label) treeView.getRoot().getValue().getGraphic();
            System.err.println("rootLabel = " + rootLabel);
            System.err.println("rootLabel.bounds = " + rootLabel.getLayoutBounds());

            Label rootLb = (Label) treeView.getRoot().getValue().getGraphic();
            double w = rootLb.getLayoutBounds().getWidth();
            System.err.println("root label width = " + w);
            Text tx = new Text(rootLb.getText());
            tx.setFont(rootLb.getFont());
            System.err.println("root text width = " + tx.getLayoutBounds().getWidth());

            for (int i = 0; i < model.getCategories().size(); i++) {
                TreeItem<PaletteItem> cit = new TreeItem(model.getCategories().get(i));

                //System.err.println("Category = " + cit.getValue().getG        ().getParent());
                for (int j = 0; j < model.getCategories().get(i).getItems().size(); j++) {
                    TreeItem<PaletteItem> it = new TreeItem(model.getCategories().get(i).getItems().get(j));
                    /*                    Label iLb = model.getCategories().get(i).getItems().get(j).getLabel();
                    //System.err.println("item = " + it.getValue().getLabel().getParent());
                    System.err.println("--------------------------------------");
                    double iw = rootLb.getLayoutBounds().getWidth();
                    System.err.println("  --- label width = " + iw);
                    System.err.println("  --- label graphic = " + iLb.getGraphic());
                    Text itx = new Text(iLb.getText());
                    itx.setFont(iLb.getFont());

                    System.err.println("   --- text width = " + itx.getLayoutBounds().getWidth());
                     */
                }
            }

            //System.err.println("width = " + treeView.getRoot().getValue().getLabel().getLayoutBounds().getWidth());
        });

        vbox.getChildren().add(treeView);
        Label testLabel = new Label("HBox11123456789012345678");
        testLabel.setMinWidth(178.3125);
        testLabel.setMaxWidth(178.3125);
        testLabel.setMinWidth(180);
        testLabel.setMaxWidth(180);
        testLabel.setStyle("-fx-border-width: 2; -fx-border-color: green; -fx-background-color: yellow ");
        testLabel.getStyleClass().add("tree-item-node-vbox");
        //vbox.getChildren().add(testLabel);
        TilePane tp = model.getCategories().get(0).getGraphic();
        tp.setMinWidth(20);
        treeView.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
/*                System.err.println("TilePame treeView width = " + treeView.getWidth());
                System.err.println("TilePame tilePane width = " + tp.getWidth());
                System.err.println("TilePame tilePane prefWidth = " + tp.getPrefWidth());
                System.err.println("TilePame tilePane tilePrefWidth = " + tp.getPrefTileWidth());
                System.err.println("-----------------------------------------------");
*/                
                //tp.setPrefTileWidth(78.20703125);
                //tp.setMaxWidth(tp.getPrefTileWidth()-2);
                tp.layout();
                if ( treeView.localToScreen(treeView.getBoundsInLocal()).contains(tp.localToScreen(tp.getBoundsInLocal())) ) {
                    System.err.println("treeView.width = " + treeView.getWidth());
                }
                
            }
            
        });
        //testLabel.applyCss();
        //testLabel.layout();
        stage.setOnShown(w -> {
            Label rootLabel1 = (Label) treeView.getRoot().getValue().getGraphic();
            System.err.println("RRRR Window=" + rootLabel1.getScene().getWindow());
            
            Platform.runLater(() -> {
                //treeView.layout();
                Label rootLabel = (Label) treeView.getRoot().getValue().getGraphic();
                double rootLabelWidth = rootLabel.getLayoutBounds().getWidth();
                double maxTextWidth = 0;
                for (PaletteCategory pc : model.getCategories()) {
                    for (PaletteItem pi : pc.getItems()) {
                        Text tx = new Text(pi.getLabel().getText());
                        tx.setFont(pi.getLabel().getFont());
                        if (tx.getLayoutBounds().getWidth() > maxTextWidth) {
                            maxTextWidth = tx.getLayoutBounds().getWidth();
                        }
                    }
                }
                for (PaletteCategory pc : model.getCategories()) {
                    for (PaletteItem pi : pc.getItems()) {
                        double ins = rootLabel.getInsets().getLeft() + rootLabel.getInsets().getRight();
                        pi.getLabel().setMinWidth(ins + rootLabelWidth + maxTextWidth);
                        //pi.getLabel().setMaxWidth(ins + rootLabelWidth + maxTextWidth);
                        System.err.println("pi.getLabel().minWidth=" + pi.getLabel().getMinWidth());
                    }
                }
                //vbox.getChildren().remove(treeView);
                //vbox.getChildren().add(1,tp);
            });
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(350);
        stage.show();
        //System.err.println("!!! " + rootItem.getValue().getLabel().getParent());
/*        Label rootLabel = (Label) treeView.getRoot().getValue().getGraphic();
        System.err.println("rootLabel = " + rootLabel);
        System.err.println("rootLabel.bounds = " + rootLabel.getLayoutBounds());
        double rootLabelWidth = rootLabel.getLayoutBounds().getWidth();
        double maxTextWidth = 0;
        for (PaletteCategory pc : model.getCategories()) {
            for (PaletteItem pi : pc.getItems()) {
                Text tx = new Text(pi.getLabel().getText());
                tx.setFont(pi.getLabel().getFont());
                if (tx.getLayoutBounds().getWidth() > maxTextWidth) {
                    maxTextWidth = tx.getLayoutBounds().getWidth();
                    System.err.println("Label = " + pi.getLabel());
                    System.err.println("Label min width = " + pi.getLabel().getWidth());
                    System.err.println("   --- rootLabelWidth = " + rootLabelWidth);
                    System.err.println("   --- maxTextWidth = " + maxTextWidth);
                    System.err.println("------------------------------------------");
                }
            }
        }
        for (PaletteCategory pc : model.getCategories()) {
            for (PaletteItem pi : pc.getItems()) {
                pi.getLabel().setMinWidth(rootLabelWidth + maxTextWidth);
                pi.getLabel().setMaxWidth(rootLabelWidth + maxTextWidth);
            }
        }
         */
         
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    protected void customizeCell(TreeView<PaletteItemValue> treeView) {
        TreeView<PaletteItemValue> t = treeView;
        t.setCellFactory((TreeView<PaletteItemValue> tv) -> {
            TreeCell cell = new TreeCell() {
                @Override
                public void updateItem(Object value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);

                    } else {
                        /*                        Node n = ((PaletteItemValue)this.getTreeItem().getValue()).getGraphic();
                        System.err.println("label layout = " + n.getLayoutBounds());
                        System.err.println("CELL : " + ((PaletteItemValue)this.getTreeItem().getValue()).getGraphic());
                        
                        System.err.println("!! value =" + value);
                         */
                        //setMinWidth(50);
                        System.err.println("!! value =" + value);
                        
                        this.setGraphic(((PaletteItemValue) value).getGraphic());
                        if ( getGraphic() instanceof TilePane) {
                            setPrefHeight(((TilePane)this.getGraphic()).getWidth());
                            this.layout();
                            this.prefHeightProperty().bind(((TilePane)this.getGraphic()).heightProperty());
                        }
                        this.setStyle("-fx-border-color: blue");
                    }
                }
            };
            return cell;
        });
    }

    public PaletteModel createDefaultPaleteModel(TreeView<PaletteItemValue> table) {
        PaletteModel model = new PaletteModel(table);

        Label lb = new Label("Containers");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        PaletteCategory pc = model.addCategory("containers", lb);
        lb.getStyleClass().add("tree-item-font-bold");

        lb = new Label("VBox");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-node-vbox");
        lb.applyCss();
        pc.addItem(lb);

        lb = new Label("HBox123");
        //lb = new Label("Rectangle");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb);
        lb.getStyleClass().add("tree-item-node-hbox");
        lb.applyCss();

        lb = new Label("Shapes");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-font-bold");

        pc = model.addCategory("shapes", lb);
        lb.applyCss();

        lb = new Label("Rectangle");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb);
        lb.getStyleClass().add("tree-item-node-rectangle");
        lb.applyCss();

        lb = new Label("Arc");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb);
        lb.getStyleClass().add("tree-item-node-rectangle");
        return model;
    }

    public static class PaletteItem {

        //private TreeTableView<PaletteItem> treeView;
        private ObjectProperty<Label> label = new SimpleObjectProperty<>();

        //public PaletteItem(TreeTableView<PaletteItem> treeView, Label lb) {
        public PaletteItem(Label lb) {
            //this.treeView = treeView;
            label.set(lb);
        }

        public Label getLabel() {
            return label.get();
        }

        public void setLabel(Label graphic) {
            this.label.set(graphic);
        }

        public ObservableValue<Label> labelProperty() {
            return label;
        }
    }

    public static class PaletteItemValue {

        private ObjectProperty<Node> graphic = new SimpleObjectProperty<>();

        //private Node graphic;
        //public PaletteItem(TreeTableView<PaletteItem> treeView, Label lb) {
        public PaletteItemValue(Node node) {
            //this.treeView = treeView;
            this.graphic.set(node);
        }

        public Node getGraphic() {
            return graphic.get();
        }

        /*        public Label getLabel() {
            return graphic.get();
        }

        public void setLabel(Label graphic) {
            this.graphic.set(graphic);
        }

        public ObservableValue<Label> labelProperty() {
            return graphic;
        }
         */
    }

    public static class PaletteCategory extends PaletteItem {

        private final StringProperty id = new SimpleStringProperty();
        private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();
        private final ObjectProperty<TilePane> graphic = new SimpleObjectProperty<>();

        public PaletteCategory(String id, Label lb) {
            super(lb);
            this.id.set(id);
            init();
        }

        private void init() {
            //addItem(getLabel());
            TilePane tp = new TilePane();
            tp.setStyle("-fx-border-color: red");
            tp.setHgap(10);
            //tp.autosize();
            //tp.setOrientation(Orientation.VERTICAL);
            
            tp.setPrefColumns(1);
            tp.setTileAlignment(Pos.TOP_LEFT);
            setGraphic(tp);
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

        public TilePane getGraphic() {
            return graphic.get();
        }

        public void setGraphic(TilePane pane) {
            this.graphic.set(pane);
        }

        public ObjectProperty<TilePane> graphicProperty() {
            return graphic;
        }

        public PaletteItem addItem(Label label) {
            PaletteItem item = new PaletteItem(label);
            items.add(item);
            getGraphic().getChildren().add(item.getLabel());

            return item;
        }

    }

    public static class PaletteModel {

        private final TreeView<PaletteItemValue> treeView;

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();
        //private final ObservableMap<String, ObservableList<PaletteItem>> categoryItems = FXCollections.observableHashMap();
        //private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();

        public PaletteModel(TreeView<PaletteItemValue> treeView) {
            this.treeView = treeView;
            //init();
        }

        public PaletteItemValue getRoot() {
            Label lb = new Label();
            lb.setId("root");
            lb.getStyleClass().add("tree-item-node-vbox");
            lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");

            return new PaletteItemValue(lb);
        }

        public TreeItem<PaletteItemValue> buildTree() {
            TreeItem root = new TreeItem(getRoot());
            treeView.setRoot(root);
            for (int i = 0; i < categories.size(); i++) {
                TreeItem cit = new TreeItem(new PaletteItemValue(categories.get(i).getLabel()));
                root.getChildren().add(cit);
                //PaletteItemValue piv = new PaletteItemValue(treeView)
                TreeItem it = new TreeItem(new PaletteItemValue(categories.get(i).getGraphic()));
                cit.getChildren().add(it);
                /*                for (int j = 0; j < categories.get(i).getItems().size(); j++) {
                    TreeItem it = new TreeItem(categories.get(i).getItems().get(j));
                    cit.getChildren().add(it);
                }
                 */
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
