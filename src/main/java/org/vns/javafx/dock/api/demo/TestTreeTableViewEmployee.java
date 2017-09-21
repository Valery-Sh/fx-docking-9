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

//import org.o7planning.javafx.model.Employee;
 
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
 
public class TestTreeTableViewEmployee extends Application {
 
    @Override
    public void start(Stage stage) {
 
        TreeTableView<Employee> treeTableView = new TreeTableView<Employee>();
 
        // Create column EmpNo (Data type of String).
        TreeTableColumn<Employee, String> empNoCol //
                = new TreeTableColumn<Employee, String>("Emp No");
 
        // Create column FullName (Data type of String).
        TreeTableColumn<Employee, String> fullNameCol//
                = new TreeTableColumn<Employee, String>("Full Name");
 
        // Create 2 sub column for FullName.
        TreeTableColumn<Employee, String> firstNameCol //
                = new TreeTableColumn<Employee, String>("First Name");
 
        TreeTableColumn<Employee, String> lastNameCol //
                = new TreeTableColumn<Employee, String>("Last Name");
 
        // Add sub columns to the FullName
        fullNameCol.getColumns().addAll(firstNameCol, lastNameCol);
 
        // Gender Column
        TreeTableColumn<Employee, String> genderCol //
                = new TreeTableColumn<Employee, String>("Gender");
 
        // Position Column
        TreeTableColumn<Employee, String> positionCol //
                = new TreeTableColumn<Employee, String>("Position");
 
        // Single? Column
        TreeTableColumn<Employee, Boolean> singleCol//
                = new TreeTableColumn<Employee, Boolean>("Single?");
 
        // Defines how to fill data for each cell.
        // Get value from property of Employee.
        empNoCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, String>("empNo"));
        firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, String>("firstName"));
        lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, String>("lastName"));
        positionCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, String>("position"));
        genderCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, String>("gender"));
        singleCol.setCellValueFactory(new TreeItemPropertyValueFactory<Employee, Boolean>("single"));
 
        // Add columns to TreeTable.
        treeTableView.getColumns().addAll(empNoCol, fullNameCol,positionCol, genderCol, singleCol);
 
        // Data
        Employee empBoss = new Employee("E00", "Abc@gmail.com", //
                "Boss", "Boss", "Manager", "M", false);
 
        Employee empSmith = new Employee("E01", "Smith@gmail.com", //
                "Susan", "Smith", "Salesman", "F", true);
 
        Employee empMcNeil = new Employee("E02", "McNeil@gmail.com", //
                "Anne", "McNeil", "Cleck", "M", false);
 
        // Root Item
        TreeItem<Employee> itemRoot = new TreeItem<Employee>(empBoss);
        TreeItem<Employee> itemSmith = new TreeItem<Employee>(empSmith);
        TreeItem<Employee> itemMcNeil = new TreeItem<Employee>(empMcNeil);
 
        itemRoot.getChildren().addAll(itemSmith, itemMcNeil);
        treeTableView.setRoot(itemRoot);
        //
        StackPane root = new StackPane();
        root.setPadding(new Insets(5));
        root.getChildren().add(treeTableView);
 
        stage.setTitle("TreeTableView (o7planning.org)");
 
        Scene scene = new Scene(root, 450, 300);
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}