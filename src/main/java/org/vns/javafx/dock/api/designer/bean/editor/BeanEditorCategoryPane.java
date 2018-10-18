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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.designer.PropertyEditorPane;
import org.vns.javafx.dock.api.designer.TreeItemEx;

/**
 *
 * @author Valery Shyshkin
 */
public class BeanEditorCategoryPane extends Control implements PropertyEditorPane.CategoryNodeCollection{
    
    private final ObservableList<Node> categoryNodes = FXCollections.observableArrayList();
            
    public BeanEditorCategoryPane() {
    }
    
     @Override
    public Skin<?> createDefaultSkin() {
        return new BeanEditorCategoryPaneSkin(this);
    }

    @Override
    public ObservableList<Node> getCategoryNodes() {
        return categoryNodes;
    }
    
    public static class BeanEditorCategoryPaneSkin extends SkinBase<BeanEditorCategoryPane> {
        private VBox layout;
        private TreeView<AnchorPane> tree;
        public BeanEditorCategoryPaneSkin(BeanEditorCategoryPane control) {
            super(control);
            layout = new VBox();
            getChildren().add(layout);
            tree = new TreeView<AnchorPane>();
            
        }
    protected void customizeCell() {
        tree.setCellFactory((TreeView<AnchorPane> tv) -> {
            TreeCell<AnchorPane> cell = new TreeCell<AnchorPane>() {
                @Override
                public void updateItem(AnchorPane value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty) {
                        value.getChildren().clear();
                    } else {
                    }
                }
            };
            return cell;
        });
    }
        
    }//Skin
    
}//Class
