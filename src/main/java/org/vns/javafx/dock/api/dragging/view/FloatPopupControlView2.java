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

import org.vns.javafx.dock.incubator.view.FloatPopupControlView4;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class FloatPopupControlView2 extends FloatStageView {

    public FloatPopupControlView2(Dockable dockable) {
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

        final PopupControl floatPopup = new PopupControl();
        floatPopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        setFloatingWindow(floatPopup);

        BorderPane borderPane = new BorderPane();
        ///
        // Mark as a popup created with FloatView instance/
        //
        borderPane.getStyleClass().add(FLOATVIEW);

        setWindowRoot(borderPane);

        //DockPane dockPane = new DockPane();
        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };


        borderPane.getStyleClass().add("dock-node-border");
        borderPane.getStyleClass().add("float-popup-root");
        borderPane.setCenter(node);

        floatPopup.getScene().setRoot(borderPane);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        floatPopup.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        floatPopup.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);
        //
        // We must prevent the window to end up positioning off the screen
        //
        floatPopup.setAutoFix(false);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);
        floatPopup.setOnShown(e -> {
            DockRegistry.register(floatPopup);
        });
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        floatPopup.getStyleClass().clear();
        dockable.node().parentProperty().addListener(pcl);

        addResizer();
        return floatPopup;
    }//make FloatingPopupControl
    @Override
    public void addResizer() {
        removeListeners(getDockable().getContext().dockable());
        addListeners(getFloatingWindow());
        setResizer(new PopupControlResizer(this));
    }

}
