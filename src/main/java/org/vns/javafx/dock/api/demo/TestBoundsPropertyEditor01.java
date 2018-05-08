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

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.ComboBoxBaseSkin;
import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.ContentComboBox;

/**
 *
 * @author Valery
 */
public class TestBoundsPropertyEditor01 extends Application {

    public enum BoundsValue {
        minX, minY, minZ,
        maxX, maxY, maxZ,
        width, height, depth
    }

    private void fillMinData(GridPane grid, Bounds bounds) {
        Text tx0 = new Text("min:");
        Text tx1 = new Text("X");
        Text tx2 = new Text("Y");
        Text tx3 = new Text("Z");
        tx0.setStyle("-fx-font-size: 10");
        tx1.setStyle("-fx-font-size: 9");
        tx2.setStyle("-fx-font-size: 9");
        tx3.setStyle("-fx-font-size: 9");

        grid.add(tx0, 0, 0, 1, 2);
        grid.add(tx1, 1, 0, 1, 1);
        grid.add(tx2, 2, 0, 1, 1);
        grid.add(tx3, 3, 0, 1, 1);

        RowConstraints crow = new RowConstraints();
        crow.setValignment(VPos.CENTER);
        grid.getRowConstraints().addAll(crow);

        tx0 = new Text("");
        tx1 = new Text(Double.toString(bounds.getMinX()));
        tx2 = new Text(Double.toString(bounds.getMinY()));
        tx3 = new Text(Double.toString(bounds.getMinZ()));
        tx0.setStyle("-fx-font-size: 10");
        tx1.setStyle("-fx-font-size: 10");
        tx2.setStyle("-fx-font-size: 10");
        tx3.setStyle("-fx-font-size: 10");

        grid.add(tx1, 1, 1);
        grid.add(tx2, 2, 1);
        grid.add(tx3, 3, 1);

    }

    private void fillMaxData(GridPane grid, Bounds bounds) {
        int row = 2;
        Text tx0 = new Text("max:");
        Text tx1 = new Text("X");
        tx1.setTextAlignment(TextAlignment.CENTER);
        Text tx2 = new Text("Y");
        tx2.setTextAlignment(TextAlignment.CENTER);
        Text tx3 = new Text("Z");
        tx3.setTextAlignment(TextAlignment.CENTER);
        tx0.setStyle("-fx-font-size: 10");
        tx1.setStyle("-fx-font-size: 9");
        tx2.setStyle("-fx-font-size: 9");
        tx3.setStyle("-fx-font-size: 9");

        grid.add(tx0, 0, row, 1, 2);
        grid.add(tx1, 1, row, 1, 1);
        grid.add(tx2, 2, row, 1, 1);
        grid.add(tx3, 3, row, 1, 1);

        RowConstraints crow = new RowConstraints();

        crow.setValignment(VPos.CENTER);
        grid.getRowConstraints().addAll(crow);

        tx1 = new Text(Double.toString(bounds.getMaxX()));
        tx1.setTextAlignment(TextAlignment.CENTER);
        tx2 = new Text(Double.toString(bounds.getMaxY()));
        tx2.setTextAlignment(TextAlignment.CENTER);
        tx3 = new Text(Double.toString(bounds.getMaxZ()));
        tx3.setTextAlignment(TextAlignment.CENTER);
        tx1.setStyle("-fx-font-size: 10");
        tx2.setStyle("-fx-font-size: 10");
        tx3.setStyle("-fx-font-size: 10");

        //grid.add(tx0, 0, 2);
        grid.add(tx1, 1, row + 1);
        grid.add(tx2, 2, row + 1);
        grid.add(tx3, 3, row + 1);

    }

    private void fillSizeData(GridPane grid, Bounds bounds) {
        int row = 4;
        Text tx0 = new Text("size:");
        Text tx1 = new Text("Width");
        Text tx2 = new Text("Height");
        Text tx3 = new Text("Depth");
        tx0.setStyle("-fx-font-size: 10");
        tx1.setStyle("-fx-font-size: 9");
        tx2.setStyle("-fx-font-size: 9");
        tx3.setStyle("-fx-font-size: 9");

        grid.add(tx0, 0, row, 1, 2);
        grid.add(tx1, 1, row, 1, 1);
        grid.add(tx2, 2, row, 1, 1);
        grid.add(tx3, 3, row, 1, 1);

        RowConstraints crow = new RowConstraints();

        crow.setValignment(VPos.CENTER);
        grid.getRowConstraints().addAll(crow);

        tx1 = new Text(Double.toString(bounds.getWidth()));
        tx2 = new Text(Double.toString(bounds.getHeight()));
        tx3 = new Text(Double.toString(bounds.getDepth()));
        tx1.setStyle("-fx-font-size: 10");
        tx2.setStyle("-fx-font-size: 10");
        tx3.setStyle("-fx-font-size: 10");

        //grid.add(tx0, 0, 2);
        grid.add(tx1, 1, row + 1);
        grid.add(tx2, 2, row + 1);
        grid.add(tx3, 3, row + 1);

    }
    private GridPane grid;

    private void fillData(int row, String leftHeader, String topHeader1, String topHeader2, String topHeader3,
            String data1, String data2, String data3) {

        Text tx0 = new Text(leftHeader);
        Text tx1 = new Text(topHeader1);
        Text tx2 = new Text(topHeader2);
        Text tx3 = new Text(topHeader3);
        tx0.setStyle("-fx-font-size: 10");
        tx1.setStyle("-fx-font-size: 9");
        tx2.setStyle("-fx-font-size: 9");
        tx3.setStyle("-fx-font-size: 9");

        grid.add(tx0, 0, row, 1, 2);
        grid.add(tx1, 1, row, 1, 1);
        grid.add(tx2, 2, row, 1, 1);
        grid.add(tx3, 3, row, 1, 1);

        RowConstraints crow = new RowConstraints();

        crow.setValignment(VPos.CENTER);
        grid.getRowConstraints().addAll(crow);

        tx1 = new Text(data1);
        tx2 = new Text(data2);
        tx3 = new Text(data3);
        tx1.setStyle("-fx-font-size: 10");
        tx2.setStyle("-fx-font-size: 10");
        tx3.setStyle("-fx-font-size: 10");

        //grid.add(tx0, 0, 2);
        grid.add(tx1, 1, row + 1);
        grid.add(tx2, 2, row + 1);
        grid.add(tx3, 3, row + 1);

    }

    @Override
    public void start(Stage stage) {
        Bounds bounds = new BoundingBox(10, 20, 100, 50);

        grid = new GridPane();
        grid.setVgap(6);
        grid.setGridLinesVisible(true);
        fillData(0, "min:", "X", "Y", "Z", Double.toString(bounds.getMinX()), Double.toString(bounds.getMinY()), Double.toString(bounds.getMinZ()));
        fillData(2, "max:", "X", "Y", "Z", Double.toString(bounds.getMaxX()), Double.toString(bounds.getMaxY()), Double.toString(bounds.getMaxZ()));
        fillData(4, "size:", "X", "Y", "Z", Double.toString(bounds.getWidth()), Double.toString(bounds.getHeight()), Double.toString(bounds.getDepth()));
        //fillMinData(grid, bounds);
        //fillMaxData(grid, bounds);
        //fillSizeData(grid, bounds);

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(25);
        column0.setHalignment(HPos.CENTER);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        column1.setHalignment(HPos.CENTER);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25);
        column2.setHalignment(HPos.CENTER);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25);
        column3.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().addAll(column0, column1, column2, column3);

        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add(grid);
        //comboBox.setPlaceholder(grid);
        VBox root = new VBox(comboBox);

        Button btn2 = new Button("Btn1");
        //grid.add(btn2,0,0);
        Button btn1 = new Button("Btn1");
        root.getChildren().add(btn1);
        ContentComboBox ccb = new ContentComboBox();
        Rectangle ccbRect = new Rectangle(100,50);
        Button ccbBtn1 = new Button("ccbBtn1");
        ccbRect.setFocusTraversable(false);
        ccb.setContent(ccbRect);
        root.getChildren().add(ccb);
        root.setPrefSize(500, 100);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

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

    public static class BoundsComboBox extends ComboBoxBase {

        public BoundsComboBox() {
            this.setOnAction(e -> {
                System.err.println("ACTION");
            });
        }

        @Override
        public Skin<?> createDefaultSkin() {
            return new BoundsComboBoxSkin(this);
        }
    }

    public static class BoundsComboBoxSkin extends ComboBoxBaseSkin {

        public BoundsComboBoxSkin(BoundsComboBox control) {
            super(control, new ComboBoxBaseBehavior(control,new ArrayList<KeyBinding>()));
        }

        @Override
        public Node getDisplayNode() {
            return null;
        }

        @Override
        public void show() {

        }

        @Override
        public void hide() {
        }

    }

}
