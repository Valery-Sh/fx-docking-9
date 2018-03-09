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
package org.vns.javafx.dock.api;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockNode;

/**
 *
 * @author Valery Shyshkin
 */
public class DockNodeSkin extends SkinBase<DockNode> {

    private final VBox layout;
    private final StackPane titleBarPane;
    private final StackPane contentPane;
    private Node content;
    private Node titleBar;

    public DockNodeSkin(DockNode control) {
        super(control);
        titleBar = getSkinnable().getTitleBar();
        titleBarPane = new StackPane() {
            {
                if (titleBar != null) {
                    getChildren().setAll(titleBar);
                }
            }
        };
        content = getSkinnable().getContent();
        contentPane = new StackPane() {
            {
                if (content != null) {
                    getChildren().setAll(content);
                }
            }
        };
        this.layout = new VBox() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
            }
        };
        
        layout.getChildren().add(titleBarPane);
        layout.getChildren().add(contentPane);
        getSkinnable().contentProperty().addListener(this::contentChanged);
        Dockable.of(getSkinnable()).getContext().titleBarProperty().addListener(this::titlebarChanged);

        getChildren().add(layout);
    }

    protected void contentChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
            contentPane.getChildren().clear();
        }
        if (newValue != null) {
            contentPane.getChildren().add(newValue);
        }
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            titleBarPane.getChildren().clear();
        }
        if (newValue != null) {
            titleBarPane.getChildren().add(newValue);
        }
    }

}
