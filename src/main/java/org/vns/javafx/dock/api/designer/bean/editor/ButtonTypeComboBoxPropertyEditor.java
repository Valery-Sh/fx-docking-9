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

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Valery Shyskin
 */
public class ButtonTypeComboBoxPropertyEditor extends ObservableListPropertyEditor<ButtonType>{
    

    public ButtonTypeComboBoxPropertyEditor() {
        this(null);
    }
    public ButtonTypeComboBoxPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getStyleClass().add("button-type-editor");
        setStringConverter(new ObservableListItemStringConverter(this,ButtonType.class));            
        getTextField().setSeparator(",");
        getTextField().setDefaultValue("");
        getTextField().setEmptySubstitution("");
        getTextField().setNullable(false);

    }


    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("resources/styles/default.css").toExternalForm();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new ButtonTypeComboBoxPropertyEditorSkin(this);
    }

    @Override
    protected Node createEditorNode() {
        super.createEditorNode();
        return new GridPane();
    }

    public static class ButtonTypeComboBoxPropertyEditorSkin extends BaseEditorSkin {

        private GridPane grid;
        private final Button plusButton;
        private final Button minusButton;
        private final StackPane contentPane;
        private final ComboBox<Label> comboBox;
        private final List<Label> labels;

        private final String separator;
        
        ButtonTypeComboBoxPropertyEditor control;
         
        public ButtonTypeComboBoxPropertyEditorSkin(ButtonTypeComboBoxPropertyEditor control) {
            super(control);
            this.control = control;
            separator = control.getTextField().getSeparator();

            contentPane = new StackPane();
            comboBox = new ComboBox<>();
            
            contentPane.getChildren().addAll(comboBox, control.getTextField());
            plusButton = new Button();
            plusButton.getStyleClass().add("plus-button");

            minusButton = new Button();
            minusButton.getStyleClass().add("minus-button");

            labels = createComboBoxItems();

            plusButton.setOnAction(a -> {
                show(plusButton);
            });
            minusButton.setOnAction(a -> {
                if (!control.getTextField().getText().isEmpty()) {
                    show(minusButton);
                }
            });
            HBox btnBox = new HBox(plusButton,minusButton);
            //grid = (GridPane) control.getEditorNode();
            grid = (GridPane) getEditorNode();
         
            comboBox.setCellFactory(listView -> new ListCell<Label>() {
                @Override
                public void updateItem(Label item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getText());
                    }
                }
            });

            contentPane.setAlignment(Pos.BASELINE_CENTER);

            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column0);
            grid.add(contentPane, 0, 0);
            grid.add(btnBox, 1, 0);
            comboBox.getItems().addAll(labels);
            //comboBox.setVisibleRowCount(labels.size());
            comboBox.setMaxWidth(1000);
            comboBox.setVisible(false);

            comboBox.setOnHidden(ev -> {
                if (comboBox.getValue() == null) {
                    return;
                }
                String text = control.getTextField().getText();
                if (pressed == plusButton) {
                    if (text.isEmpty()) {
                        control.getTextField().setText(comboBox.getValue().getText());
                    } else {
                        control.getTextField().setText(text + separator + comboBox.getValue().getText());
                    }
                } else {
                    if (!text.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        String[] split = text.split(separator);
                        for (String item : split) {
                            if (!item.equals(comboBox.getValue().getText())) {
                                sb.append(item);
                                sb.append(separator);
                            }
                        }
                        if (sb.lastIndexOf(separator) >= 0) {
                            sb.deleteCharAt(sb.lastIndexOf(separator));
                        }
                       control.getTextField().setText(sb.toString());
                    }
                }
            });
            //getChildren().add(grid);
        }
        private Button pressed;

        private void show(Button plusOrMinus) {
            pressed = plusOrMinus;
            String text = control.getTextField().getText();
            comboBox.getSelectionModel().clearSelection();
            if (pressed == minusButton && text.isEmpty()) {
                return;
            }
            if (pressed == plusButton && text.isEmpty() && comboBox.getItems().size() == labels.size()) {
                comboBox.show();
                return;
            }

            comboBox.getItems().clear();

            if (pressed == minusButton) {
                labels.forEach(lb -> {
                    if (text.contains(lb.getText())) {
                        comboBox.getItems().add(lb);
                    }
                });
            } else {
                comboBox.getItems().clear();
                labels.forEach(lb -> {
                    if (!text.contains(lb.getText())) {
                        comboBox.getItems().add(lb);
                    }
                });
            }
            comboBox.show();
        }

        private List<Label> createComboBoxItems() {
            List<Label> list = FXCollections.observableArrayList();
            list.add(new Label(ButtonType.APPLY.getText().toUpperCase()));
            list.add(new Label(ButtonType.CANCEL.getText().toUpperCase()));
            list.add(new Label(ButtonType.CLOSE.getText().toUpperCase()));
            list.add(new Label(ButtonType.FINISH.getText().toUpperCase()));
            list.add(new Label(ButtonType.NEXT.getText().toUpperCase()));
            list.add(new Label(ButtonType.NO.getText().toUpperCase()));
            list.add(new Label(ButtonType.OK.getText().toUpperCase()));
            list.add(new Label(ButtonType.PREVIOUS.getText().toUpperCase()));
            list.add(new Label(ButtonType.YES.getText().toUpperCase()));
            return list;
        }
        
/*        @Override
        protected Node getEditorNode() {
            if ( grid == null ) {
                grid = new GridPane();
            }
            return grid;
        }
*/
    }//Skin
}//Control
