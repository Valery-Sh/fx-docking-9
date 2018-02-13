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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestTreeView01 extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        //VBox rootPane = new VBox();
        StackPane rootPane = new StackPane();
        TreeTableView<PaletteModel> tvt = new TreeTableView<>();
        tvt.setRowFactory(treeTable -> {
            TreeTableRow<PaletteModel> row = new TreeTableRowEx();
            row.treeItemProperty().addListener((ov, oldTreeItem, newTreeItem) -> {
                System.err.println("oldTreeItem = " + oldTreeItem);
                if (newTreeItem != null) {

                    //System.err.println("   --- left  newTreeItem = " + newTreeItem.getValue().getLeft().get().getText());
                    //System.err.println("   --- ryght newTreeItem = " + newTreeItem.getValue().getRight());
                }
                //System.err.println("newTreeItem = " + newTreeItem);

            });
            return row;
        });

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
        TreeItem<PaletteModel> rootItem = new TreeItem<>(models.getItems().get(0));

        //TreeTableColumn<Label[],String> ttc1 = new TreeTableColumn<>("Column1");
        TreeTableColumn<PaletteModel, Label> col1 = new TreeTableColumn<>("Column1          ");
        TreeTableColumn<PaletteModel, Label> col2 = new TreeTableColumn<>("Column2          ");
        tvt.getColumns().addAll(col1, col2);
        //col1.setVisible(false);

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
        col1.setCellValueFactory(
                new Callback<CellDataFeatures<PaletteModel, Label>, ObservableValue<Label>>() {
            public ObservableValue<Label> call(CellDataFeatures<PaletteModel, Label> p) {
                //p.getValue() returns the TreeItem<Person> instance for a particular TreeTableView row,
                // p.getValue().getValue() returns the Person instance inside the TreeItem<Person>
                //return new ReadOnlyObjectWrapper(p.getValue().getValue().getFirstName());
                //return p.getValue().getValue();
                PaletteModel m = p.getValue().getValue();
                int idx = models.getItems().indexOf(m);
                //System.err.println("col1: idx = " + idx);
                //System.err.println("col1  left = " + m.getLeft());
                return m.getLeft();
                //return models.getItems().get(idx).getLeft();
                //return new SimpleObjectProperty<Label>(p.getValue().getValue());
            }
        });
        col2.setCellValueFactory(
                new Callback<CellDataFeatures<PaletteModel, Label>, ObservableValue<Label>>() {
            public ObservableValue<Label> call(CellDataFeatures<PaletteModel, Label> p) {
                //p.getValue() returns the TreeItem<Person> instance for a particular TreeTableView row,
                // p.getValue().getValue() returns the Person instance inside the TreeItem<Person>
                //return new ReadOnlyObjectWrapper(p.getValue().getValue().getFirstName());
                //return p.getValue().getValue();
                PaletteModel m = p.getValue().getValue();
                int idx = models.getItems().indexOf(m);
                //System.err.println("col2: idx = " + idx);
                //System.err.println("col2  right = " + m.getRight());

                return m.getRight();
                //return models.getItems().get(idx).getRight();
                //return new SimpleObjectProperty<Label>(p.getValue().getValue());
            }
        });

/*        col2.setCellFactory(col -> {
            TreeTableCell<PaletteModel, Label> cell = new TreeTableCell<PaletteModel, Label>() {
                @Override
                public void updateItem(Label label, boolean empty) {
                    super.updateItem(label, empty);
// Cleanup the cell before populating it
                    if (!empty) {
// Format the birth date in mm/dd/yyyy format

                        //this.setScaleX(0.01);
                    }
                }
            };
            return cell;
        });
*/
        //rootPane.getChildren().add(new Button("Button1"));
        //rootPane.setPrefSize(20, 20);
        //rootPane.setStyle("-fx-background-color: yellow");
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

        public PaletteModel() {
        }

        public PaletteModel(Label left, Label right) {
            this.left.set(left);
            this.right.set(right);
        }
        public PaletteModel(Label left, Label right, String categoryName) {
            this.left.set(left);
            this.right.set(right);
            this.categoryName = categoryName;
            if ( categoryName != null ) {
                setCategory(true);
            }
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
