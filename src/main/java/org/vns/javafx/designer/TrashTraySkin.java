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
package org.vns.javafx.designer;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class TrashTraySkin extends SkinBase<TrashTray> {

    private final Pane contentPane;
    private final ReadOnlyObjectWrapper<ImageView> imageViewWrapper = new ReadOnlyObjectWrapper<>();

    public TrashTraySkin(TrashTray control) {
        super(control);

        ImageView iv = new ImageView(getSkinnable().getImage());
        iv.getStyleClass().add("image-view");
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> {
            showTableView();
        });
        MenuItem clearItem = new MenuItem("Clear");
        clearItem.getStyleClass().add("trash-clear-menu-item");
        clearItem.setOnAction(e -> {
            clearTableView();
        });        
        ContextMenu ctxMenu = new ContextMenu(openItem, clearItem);
        
        ctxMenu.setOnShowing( e -> {
            String url = DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
            ctxMenu.getScene().getStylesheets().remove(url);
            ctxMenu.getScene().getStylesheets().add(url);
        });
        
        getSkinnable().setContextMenu(ctxMenu);
        ctxMenu.setAutoHide(true);
        if ( getSkinnable().getTableView().getItems().isEmpty() ) {
            clearItem.setDisable(true);
        }
        getSkinnable().getTableView().getItems().addListener( (Observable c) -> {
            clearItem.setDisable(getSkinnable().getTableView().getItems().isEmpty());
        });
        contentPane = new StackPane(iv) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
            }
        };

        getChildren().add(contentPane);
    }

    public ReadOnlyObjectProperty<ImageView> imageViewProperty() {
        return imageViewWrapper.getReadOnlyProperty();
    }

    public ImageView getImageView() {
        return imageViewWrapper.getReadOnlyProperty().getValue();
    }

    protected void setImageView(ImageView imageView) {
        imageViewWrapper.setValue(imageView);
    }

    protected void showTableView() {
        if (getSkinnable().getTableView() == null) {
            return;
        }
        Stage stage = new Stage();
        stage.setHeight(200);
        stage.setWidth(400);
        StackPane root = new StackPane(getSkinnable().getTableView());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initOwner(getSkinnable().getScene().getWindow());
        stage.show();

    }

    protected void clearTableView() {
        if (getSkinnable().getTableView() == null) {
            return;
        }
        TableView tv = getSkinnable().getTableView();
        tv.getItems().clear();

    }    


}
