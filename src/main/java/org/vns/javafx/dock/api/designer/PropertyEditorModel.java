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
package org.vns.javafx.dock.api.designer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

/**
 *
 * @author Valery
 */
public class PropertyEditorModel {

    private final PropertyEditorPane editorPane;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    public PropertyEditorModel(PropertyEditorPane editorPane) {
        this.editorPane = editorPane;
    }

    /**
     * Creates a new instance of type {@link Category} and adds it to the end of
     * the categories list.
     *
     * @param id the identifier for the new category/ Must be unique otherwise
     * the exception of type {@code IllegalArgumentException} will be thrown
     * @param title the title used to represent the new category in the property
     * editor's pane.
     * @return the newly created category
     */
    public Category addCategory(String id, String title) {
        return addCategory(this, id, title);
    }

    protected Category addCategory(PropertyEditorModel model, String id, String title) {
        if (getCategory(id) != null) {
            throw new IllegalArgumentException("A PaletteCategory with the same id already exists (id=" + id + ")");
        }

        Category c = new Category(model, id, title);
        categories.add(c);
        return c;
    }

    /**
     * Returns the category with the given identifier.
     *
     * @param id the identifier to search for
     * @return the category with the given identifier.
     */
    public Category getCategory(String id) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getId().equals(id)) {
                retval = c;
                break;
            }
        }
        return retval;
    }

    public static class Category {

        private final StringProperty id = new SimpleStringProperty();
        private final PropertyEditorModel model;
        private final Button button;

        public Category(PropertyEditorModel model, String id,  String title) {
            this.model = model;
            this.button = new Button(title);
            this.id.set(id);
        }

        /**
         * Returns the identifier of the category
         *
         * @return the identifier of the category
         */
        public String getId() {
            return id.get();
        }

        /**
         * Sets the identifier of the category. The value must be unique for all
         * categories
         *
         * @param id the identifier to be set
         */
        public void setId(String id) {
            this.id.set(id);
        }

        public PropertyEditorModel getModel() {
            return model;
        }

    }

    public static class Section {

    }

}
