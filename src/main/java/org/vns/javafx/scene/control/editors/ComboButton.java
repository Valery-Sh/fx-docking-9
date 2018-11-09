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
package org.vns.javafx.scene.control.editors;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 *
 * @author Valery Shyshkin
 */
public class ComboButton<T> extends Button {

    private ComboBox<T> comboBox;
    
    private ItemsUpdater itemsUpdater;
    
    public ComboButton() {
        init();
    }

    private void init() {
        getStyleClass().add("combo-button");
        comboBox = new ComboBox();
        Shape graphic = createTriangle();
        setGraphic(graphic);
        setOnAction(a -> {
            if ( itemsUpdater != null ) {
                itemsUpdater.update(comboBox.getItems());
            }
            System.err.println("comboBox.parent = " + comboBox.getParent().getClass().getSimpleName());
            comboBox.show();
        });

        comboBox.setCellFactory(listView -> new ListCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    String text = "";
                    if ( item instanceof String ) {
                        text = (String) item;
                    } else if ( item instanceof Labeled ) {
                        text = (((Labeled)item).getText());
                    } else {
                        text = item.toString();
                    }
                    setText(text);
                }
            }
        });

    }

    public ComboBox<T> getComboBox() {
        return comboBox;
    }

    public ItemsUpdater getItemsUpdater() {
        return itemsUpdater;
    }

    public void setItemsUpdater(ItemsUpdater itemsUpdater) {
        this.itemsUpdater = itemsUpdater;
    }

    public String getSelectedText() {
        String retval = (comboBox.getValue() instanceof String) ? (String) comboBox.getValue() : null;
        if (retval == null) {
            retval = (comboBox.getValue() instanceof Labeled) ? ((Labeled) comboBox.getValue()).getText() : null;
        }
        if (retval == null) {
            retval = comboBox.getValue().toString();
        }
        return retval;
    }

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    public static void setDefaultButtonGraphic(Button btn) {
        Polygon polygon = (Polygon) createTriangle();
        btn.setGraphic(polygon);
    }

    public static void setDefaultLayout(Button btn) {
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setAlignment(Pos.CENTER_LEFT);

    }

    public static Polygon createTriangle() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            0.0, 0.0,
            7.0, 0.0,
            3.5, 4.0});
        return polygon;
    }

/*    public static class ItemPane<T> extends Control {

        public ObservableList<T> items = FXCollections.observableArrayList();

        public ObservableList<T> getItems() {
            return items;
        }

        @Override
        public Skin<?> createDefaultSkin() {
            return new ItemPaneSkin(this);
        }

    }

    public static class ItemPaneSkin<T> extends SkinBase<ItemPane<T>> {

        ScrollPane root;
        VBox content;

        public ItemPaneSkin(ItemPane control) {
            super(control);
            content = new VBox();
            root = new ScrollPane(content);
            root.setFitToWidth(true);
            root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            if (!control.getItems().isEmpty()) {
                if (control.getItems().get(0) instanceof String) {

                }
                for (Object o : control.getItems()) {
                    if (o instanceof Node) {
                        content.getChildren().add((Node) o);
                    } else {
                        Label lb = new Label(o.toString());
                        content.getChildren().add(lb);
                    }
                }
            }

            getChildren().add(root);

        }

    }
*/
    @FunctionalInterface
    public interface ItemsUpdater<T> {
        void update(ObservableList<T> items);
    }
}
