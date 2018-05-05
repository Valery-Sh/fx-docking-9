/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.dragging.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.StyleUtil;

/**
 *
 * @author Valery
 */
public class FloatStageView2 extends FloatStageView {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    public FloatStageView2(Dockable dockable) {
        super(dockable);
    }

    @Override
    public Window make(Dockable dockable, boolean show) {
        Node node = dockable.node();

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.getContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        final Stage window = new Stage();
        window.initStyle(stageStyle);

        setFloatingWindow(window);

        windowRoot = new StackPane() {
            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };
        Scene scene = new Scene(windowRoot);
        window.setScene(scene);
        setWindowRoot(windowRoot);

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (window != null) {
                    window.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        windowRoot.getStyleClass().add("sidebar-popup-root");
        StyleUtil.styleSideBarPopupRoot(windowRoot);

        windowRoot.getChildren().add(node);

        window.getScene().setRoot(windowRoot);
        window.sizeToScene();
        node.applyCss();
        windowRoot.applyCss();
        window.setOnShown(e -> {
            DockRegistry.register(window);
        });
        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });

        dockable.node().parentProperty().addListener(pcl);
        if (stageStyle == StageStyle.TRANSPARENT) {
            window.getScene().setFill(null);
        }
        addResizer();

        return window;
    }//make FloatingPopupControl
}
