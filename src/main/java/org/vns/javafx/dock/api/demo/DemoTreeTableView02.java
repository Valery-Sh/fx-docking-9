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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class DemoTreeTableView02 extends Application {

    List<Employee> employees = Arrays.<Employee>asList(
            new Employee("Ethan Williams", "ethan.williams@example.com"),
            new Employee("Emma Jones", "emma.jones@example.com"),
            new Employee("Michael Brown", "michael.brown@example.com"),
            new Employee("Anna Black", "anna.black@example.com"),
            new Employee("Rodger York", "roger.york@example.com"),
            new Employee("Susan Collins", "susan.collins@example.com"));

    private final ImageView depIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/org/vns/javafx/dock/api/resources/combo-box-16x16.png"))
    );

    final TreeItem<Employee> root
            = new TreeItem<>(new Employee("Sales Department", ""), depIcon);

    public static void main(String[] args) {
        Application.launch(DemoTreeTableView02.class, args);
    }

    @Override
    public void start(Stage stage) {
        root.setExpanded(true);
        employees.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });
        stage.setTitle("Tree Table View Sample");
        Label lb = new Label("Tom");
        //Label lb = new Label("");
        lb.setStyle("-fx-background-color: yellow; -fx-border-color: red");
        VBox vbox = new VBox(lb);
        
        final Scene scene = new Scene(vbox, 400, 400);
        scene.setFill(Color.LIGHTGRAY);
        VBox sceneRoot = (VBox) scene.getRoot();

        TreeTableColumn<Employee, String> empColumn
                = new TreeTableColumn<>("Employee");
        empColumn.setPrefWidth(150);
        empColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getName())
        );

        TreeTableColumn<Employee, String> emailColumn
                = new TreeTableColumn<>("Email");
        emailColumn.setPrefWidth(190);
        emailColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getEmail())
        );

        TreeTableView<Employee> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().setAll(empColumn, emailColumn);
        sceneRoot.getChildren().add(treeTableView);
        treeTableView.setTableMenuButtonVisible(true);
        stage.setScene(scene);
        
        stage.show();
        Set<Node> set = treeTableView.lookupAll(".column-header-background" );
        set.forEach(node -> {
            node.setStyle("visibility: hidden; -fx-padding: -1em");        
        });
//        System.err.println("Label width = " + lb.getWidth()); 
        
        
        Label lb1 = new Label("Tom");
        
        Pane p = new Pane();
        Scene sc = new Scene(p);
        //p.getChildren().add(lb1);
        lb.setText("");
        System.err.println("1 lb width= " + lb.getLayoutBounds().getWidth());
        lb.getParent().layout();
        System.err.println("2 lb width = " + lb.getLayoutBounds().getWidth());
        Platform.runLater(() -> {
            System.err.println("3 lb width = " + lb.getLayoutBounds().getWidth());
        });
        Text tx = new Text(lb1.getText());
        tx.setFont(lb1.getFont());
        double width = tx.getBoundsInLocal().getWidth();
        System.err.println("Text width = " + width); 
        System.err.println("root graphic = " + root.getGraphic());
        
    }

    public class Employee {

        private SimpleStringProperty name;
        private SimpleStringProperty email;

        public SimpleStringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }

        public SimpleStringProperty emailProperty() {
            if (email == null) {
                email = new SimpleStringProperty(this, "email");
            }
            return email;
        }

        private Employee(String name, String email) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String fName) {
            name.set(fName);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String fName) {
            email.set(fName);
        }
    }
}
