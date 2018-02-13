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
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeTableView;
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
public class TestTitledPane01 extends Application {

    public void start(Stage stage) {
        stage.setTitle("Palette Stage");
        VBox vbox = new VBox();
        StackPane root = new StackPane(vbox);
        //StackPane root = new StackPane();
        root.setStyle("-fx-background-color: aqua");

        //stage.setWidth(350);
        stage.setAlwaysOnTop(true);

        PaletteModel model = createDefaultPaleteModel();
        VBox titledBox = model.buildTree();
        //scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        //VBox titledBox = (VBox) scrollPane.getContent();

        vbox.getChildren().add(titledBox);
        //root.getChildren().add(titledBox);
        Button btn1 = new Button("btn1");
        vbox.getChildren().add(0, btn1);

        btn1.setOnAction(a -> {
            for (PaletteCategory pc : model.getCategories()) {
                for (PaletteItem pi : pc.getItems()) {
                    System.err.println("label = " + pi.getLabel());
                    System.err.println("   --- width = " +pi.getLabel().getWidth());
                }
            }
            /*            System.err.println("BOUNDS: " +titledBox.getChildren().get(1).getLayoutBounds());
            System.err.println("prefWidth: " + ((TitledPane)titledBox.getChildren().get(1)).getPrefWidth());
            System.err.println("minWidth: " + ((TitledPane)titledBox.getChildren().get(1)).getMinWidth());
             */
        });
        stage.setOnShown(w -> {
            Label rootLabel = new Label("");
            rootLabel.getStyleClass().add("tree-item-node-vbox");

            rootLabel.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
            titledBox.getChildren().add(rootLabel);
            rootLabel.layout();

            Platform.runLater(() -> {
                //treeView.layout();

                double rootLabelWidth = rootLabel.getLayoutBounds().getWidth();
                double maxTextWidth = 0;
                for (PaletteCategory pc : model.getCategories()) {
                    for (PaletteItem pi : pc.getItems()) {
                        System.err.println("PADDING pi.getLabel() = " + pi.getLabel().getPadding());
                        System.err.println("PADDING pi.getLabel().pref = " + pi.getLabel().getPrefWidth());
                        Text tx = new Text(pi.getLabel().getText());
                        tx.setFont(pi.getLabel().getFont());
                        System.err.println("  --- pi.getLabel() tx = " + tx.getBoundsInLocal().getWidth());
                        System.err.println("  --- pi.getLabel() gap = " + pi.getLabel().getOpaqueInsets());
                        
                        //13if (tx.getLayoutBounds().getWidth() > maxTextWidth) {
                        if (pi.getLabel().getWidth() > maxTextWidth) {
                            //13maxTextWidth = tx.getLayoutBounds().getWidth();
                            maxTextWidth = pi.getLabel().getWidth();
                            System.err.println("maxTextWidth = " + maxTextWidth);
                            System.err.println("   --- label = " + pi.getLabel());
                            System.err.println("   --- label.text = " + pi.getLabel().getText());
                        }
                    }
                }
                for (PaletteCategory pc : model.getCategories()) {
                    for (PaletteItem pi : pc.getItems()) {
                        double ins = rootLabel.getInsets().getLeft() + rootLabel.getInsets().getRight();
                        System.err.println("pi.getLabel().width=" + pi.getLabel().getWidth());
                        System.err.println("   --- rootLabel.width = " + rootLabel.getWidth());
                        //13pi.getLabel().setMinWidth(rootLabelWidth + maxTextWidth);
                        //13pi.getLabel().setMaxWidth(rootLabelWidth + maxTextWidth);
                        pi.getLabel().setMinWidth(maxTextWidth);
                        pi.getLabel().setMaxWidth(maxTextWidth);
                        
                        //System.err.println("rootLabelWidth=" + rootLabelWidth);

                        System.err.println("   --- pi.getLabel().minWidth=" + pi.getLabel().getMinWidth());
                        System.err.println("   ---  pi.getLabel() ins = " + ins);
                    }
                }
                titledBox.getChildren().remove(rootLabel);
            });
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(350);
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public PaletteModel createDefaultPaleteModel() {
        PaletteModel model = new PaletteModel();

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

        //private TreeTableView<PaletteItem> titledBox;
        private ObjectProperty<Label> label = new SimpleObjectProperty<>();

        //public PaletteItem(TreeTableView<PaletteItem> titledBox, Label lb) {
        public PaletteItem(Label lb) {
            //this.titledBox = titledBox;
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

    /*    public static class PaletteItemValue {

        private ObjectProperty<Node> graphic = new SimpleObjectProperty<>();

        //private Node graphic;
        //public PaletteItem(TreeTableView<PaletteItem> titledBox, Label lb) {
        public PaletteItemValue(Node node) {
            //this.titledBox = titledBox;
            this.graphic.set(node);
        }

        public Node getGraphic() {
            return graphic.get();
        }

    }
     */
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
            tp.setVgap(5);
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

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();
        //private final ObservableMap<String, ObservableList<PaletteItem>> categoryItems = FXCollections.observableHashMap();
        //private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();

        public PaletteModel() {
        }

        public VBox buildTree() {
            VBox titledBox = new VBox();
            //Group titledBox = new Group();
            titledBox.setStyle("-fx-background-color: green");
            //ScrollPane scrollPane = new ScrollPane();

            for (int i = 0; i < categories.size(); i++) {
                TitledPane titledPane = new TitledPane();
                titledPane.setExpanded(false);
                titledPane.setMinWidth(30);

                titledBox.getChildren().add(titledPane);
                //titledPane.setGraphic(categories.get(i).getLabel());
                titledPane.setText(categories.get(i).getLabel().getText());
                titledPane.setContent(categories.get(i).getGraphic());
            }
            //scrollPane.setContent(titledBox);
            return titledBox;
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
