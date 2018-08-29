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
package org.vns.javafx.dock.api.designer.bean.editor;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery
 */
public class BoundsPropertyEditorSkin extends ContentComboBoxSkin {

    protected BoundsPropertyEditor comboBox;
    protected GridPane grid;
    private Label displayNode;

    public BoundsPropertyEditorSkin(BoundsPropertyEditor control) {
        super(control);
        comboBox = control;
        displayNode = (Label) comboBox.getDisplayNode();
        //displayNode.setStyle("-fx-padding: 2 0 2 10");
        initContent();
        initDisplayNode();
        init();
    }

    private void initDisplayNode() {
        if (comboBox.getBounds() != null) {
            String txt = stringOf(comboBox.getBounds().getMinX())
                    + ","
                    + stringOf(comboBox.getBounds().getMinY())
                    + " "
                    + stringOf(comboBox.getBounds().getWidth())
                    + "x"
                    + stringOf(comboBox.getBounds().getHeight());
            displayNode.setText(txt);
        } else {
            displayNode.setText("<null>");
        }
    }

    private void init() {
        comboBox.boundsProperty().addListener((v, ov, nv) -> {
            initDisplayNode();
            fillGrid();

        });
    }

    private String stringOf(Double v) {

        if (v == v.longValue()) {
            return Long.toString(v.longValue());
        }
        return Double.toString(v);
    }

    protected void fillData(int row, String leftHeader, String topHeader1, String topHeader2, String topHeader3,
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

    protected void initContent() {

        grid = new GridPane() {
            @Override
            public String getUserAgentStylesheet() {
                return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
            }
        };
        grid.getStyleClass().add("bounds-property-editor-grid");
        //grid.setStyle("-fx-opacity: 1; -fx-background-color: white; -fx-border-color: lightgrey; -fx-border-width: 1; -fx-padding: 10 10 10 10");

        grid.setVgap(6);
        grid.setHgap(20);

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

        fillGrid();

        comboBox.setContent(grid);
    }

    protected void fillGrid() {
        Bounds bounds = comboBox.getBounds();
        grid.getChildren().clear();
        grid.getRowConstraints().clear();
        if (bounds == null) {
            bounds = new BoundingBox(0, 0, 0, 0);
        }
        fillData(0, "min:", "X", "Y", "Z", stringOf(bounds.getMinX()), stringOf(bounds.getMinY()), stringOf(bounds.getMinZ()));
        fillData(2, "max:", "X", "Y", "Z", stringOf(bounds.getMaxX()), stringOf(bounds.getMaxY()), stringOf(bounds.getMaxZ()));
        fillData(4, "size:", "Width", "Height", "Depth", stringOf(bounds.getWidth()), stringOf(bounds.getHeight()), Double.toString(bounds.getDepth()));
    }
}
