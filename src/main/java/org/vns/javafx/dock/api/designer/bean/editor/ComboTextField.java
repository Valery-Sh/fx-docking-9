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

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 *
 * @author Valery
 */
public class ComboTextField extends Control {

    private final ObservableList<ButtonBase> buttons = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<StringTextField> textFieldWrapper = new ReadOnlyObjectWrapper<>();

    public ComboTextField() {
        init();
    }

    private void init() {
        getStyleClass().add("combo-text-field");
        textFieldWrapper.setValue(new StringTextField());
    }

    public ObservableList<ButtonBase> getButtons() {
        return buttons;
    }

    public ReadOnlyObjectProperty<StringTextField> textFieldProperty() {
        return textFieldWrapper.getReadOnlyProperty();
    }
    
    public StringTextField getTextField() {
        return textFieldWrapper.get();
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("resources/styles/default.css").toExternalForm();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new ComboTextFieldSkin(this);
    }


    public static class ComboTextFieldSkin extends SkinBase<ComboTextField> {

        private final GridPane grid;
        //private final StackPane contentPane;

        public ComboTextFieldSkin(ComboTextField control) {
            super(control);
            grid = new GridPane();
            //contentPane = new StackPane();
            HBox btnBox = new HBox();
            btnBox.setSpacing(1);
            btnBox.getStyleClass().add("button-box");
            grid.getStyleClass().add("control-pane");
            
            btnBox.getChildren().addAll(control.getButtons());
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column0);
            grid.add(control.getTextField(), 0, 0);
            grid.add(btnBox, 1, 0);
            getChildren().add(grid);
        }

    }
}
