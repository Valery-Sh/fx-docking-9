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
package org.vns.javafx.dock.api.designer.bean.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
public class PropertyPaneModel {

    private ObservableList<Category> categories = FXCollections.observableArrayList();

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public PropertyPaneModel getCopy() {
        PropertyPaneModel retval = new PropertyPaneModel();
        for (int i = 0; i < categories.size(); i++) {
            retval.getCategories().add(categories.get(i).getCopy());

        }
        return retval;
    }

    public ObservableList<ModelProperty> getModelProperties() {
        ObservableList<ModelProperty> props = FXCollections.observableArrayList();
        getCategories().forEach(c -> {
            c.getSections().forEach(s -> {
                s.getPropertyGroups().forEach(g -> {
                    g.getModelProperties().forEach(p -> {
                        props.add(p);
                    });
                });
            });
        });
        return props;
    }

}//class
