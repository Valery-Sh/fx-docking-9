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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Nastia
 */
public class HiddenTitledPane extends StackPane {

    private final BooleanProperty expanded = new SimpleBooleanProperty();

    private VBox content;

    public HiddenTitledPane() {
        this("");
    }

    public HiddenTitledPane(String title) {
        getStyleClass().add("hidden-titled-pane");
        content = new VBox();
        content.getStyleClass().clear();
        content.getStyleClass().add("content");
        //content.setSpacing(20);
        //content.setPadding(Insets.EMPTY);
        expanded.addListener((v, ov, nv) -> {
            if (nv) {
                getChildren().add(content);
                //StackPane.setAlignment(content, Pos.CENTER);
                //content.setSpacing(20);
            } else {
                getChildren().remove(content);
            }
        });
        //content.setSpacing(20);
        setId(title);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public VBox getContent() {
        return content;
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public boolean isExpanded() {
        return expanded.get();
    }

    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
    }

    /*    public boolean isExpanded() {
        return getChildren().size() >= 1;
    }
    
    public void setExpanded(boolean expanded) {
        if ( isExpanded() == expanded) {
            return;
        }
        if ( expanded ) {
            getChildren().add(content);
            StackPane.setAlignment(content, Pos.CENTER);
        } else {
            getChildren().remove(content);
        }
    }
     */
 /*    @Override
    public void layoutChildren() {
        super.layoutChildren();
    }
     */
}
