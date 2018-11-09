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
package org.vns.javafx.scene.control.editors.skin;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.vns.javafx.scene.control.editors.BaseEditor;
import org.vns.javafx.scene.control.editors.PropertyEditor;

/**
 *
 * @author Nastia
 */
public class BaseEditorSkin<T> extends SkinBase<BaseEditor<T>> {

        private AnchorPane anchor;
        private Button menuButton;
        
        public BaseEditorSkin(BaseEditor<T> control) {
            super(control);
            init();
        }

        private void init() {
            getSkinnable().getStyleClass().add(PropertyEditor.EDITOR_STYLE_CLASS);
            anchor = createAnchorPane();
            VBox vbox = new VBox(anchor, getSkinnable().getValuePane());
            //getChildren().add(anchor);
            getChildren().add(vbox);
        }

        protected AnchorPane createAnchorPane() {
            anchor = new AnchorPane();

            menuButton = getSkinnable().getMenuButton();
            if ( menuButton == null ) {
                menuButton = new Button();
                getSkinnable().setMenuButton(menuButton);
            }
            menuButton.getStyleClass().clear();

            Circle resetShape = new Circle(3.5);
            resetShape.setStroke(Color.DARKGRAY);
            resetShape.setStrokeWidth(2);
            menuButton.setVisible(false);

            resetShape.setFill(Color.TRANSPARENT);

            menuButton.setGraphic(resetShape);

            switch (getSkinnable().getMenuButtonAllignment()) {
                case RIGHT:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 0d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 14d);

                    AnchorPane.setRightAnchor(menuButton, 0d);
                    AnchorPane.setTopAnchor(menuButton, 0d);
                    AnchorPane.setBottomAnchor(menuButton, 0d);
                    menuButton.setId("menu-" + getSkinnable().getName());
                    anchor.getChildren().addAll(getEditorNode(), menuButton);                    
                    break;
                case LEFT:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 14d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 0d);

                    AnchorPane.setLeftAnchor(menuButton, 0d);
                    AnchorPane.setTopAnchor(menuButton, 0d);
                    AnchorPane.setBottomAnchor(menuButton, 0d);
                    menuButton.setId("menu-" + getSkinnable().getName());
                    anchor.getChildren().addAll(menuButton,getEditorNode() );
                    break;
                case NO:
                    AnchorPane.setLeftAnchor(getSkinnable().getEditorNode(), 0d);
                    AnchorPane.setRightAnchor(getSkinnable().getEditorNode(), 0d);
                    anchor.getChildren().addAll(getEditorNode() );
                    break;
            }
        
        
            anchor.setOnMouseEntered(ev -> {
                menuButton.setVisible(true);
            });
            anchor.setOnMouseExited(ev -> {
                menuButton.setVisible(false);
            });
            return anchor;
        }

        protected Node getEditorNode() {
            return getSkinnable().getEditorNode();
        }
    }//EditorSkin
