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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class FloatPopupControlView extends FloatStageView {

    public FloatPopupControlView(Dockable dockable) {
        super(dockable);
    }

    @Override
    public Window make(Dockable dockable, boolean show) {
        setSupportedCursors(DEFAULT_CURSORS);
        Node node = dockable.node();
        Window owner = null;
        if ((node.getScene() == null || node.getScene().getWindow() == null)) {
            return null;
        } else {
            owner = node.getScene().getWindow();
        }

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.getContext().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.getContext().isDocked() && dockable.getContext().getTargetContext().getTargetNode() != null) {
            Window w = dockable.getContext().getTargetContext().getTargetNode().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                setSupportedCursors(DEFAULT_CURSORS);
                setRootPane((Pane) dockable.node().getScene().getRoot());
                markFloating(dockable.node().getScene().getWindow());
                dockable.getContext().getTargetContext().undock(dockable.node());
                return getFloatingWindow();
            }
        }

        if (dockable.getContext().isDocked()) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }

        final PopupControl floatPopup = new PopupControl();
        floatPopup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        System.err.println("IT IS FloatPopupControlView");
        markFloating(floatPopup);

        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FLOATWINDOW);
        borderPane.setId(FLOATWINDOW);
        //borderPane.setMouseTransparent(true);
        setRootPane(borderPane);
        //
        // We must prevent the window to end up positioning off the screen
        //

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        //
        // Prohibit to use as a dock target
        //
        //dockPane.setUsedAsDockTarget(false);
        //dockPane.getItems().add(dockable.node());
        borderPane.getStyleClass().add("dock-node-border");
        borderPane.getStyleClass().add("float-popup-root");
        //borderPane.setStyle("-fx-background-color: aqua");
        //borderPane.setOpacity(0);
        borderPane.setCenter(node);

        //floatingProperty().set(true);

        floatPopup.getScene().setRoot(borderPane);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        floatPopup.setX(stagePosition.getX() - insetsDelta.getLeft());
        floatPopup.setY(stagePosition.getY() - insetsDelta.getTop());

        floatPopup.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        floatPopup.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

//        System.err.println("++++++ CreatePopup node = " + node.getWidth());
//        System.err.println("++++++ CreatePopup node = " + node.getHeight());
//        System.err.println("*** insetsWidth = " + insetsWidth);
        borderPane.setStyle("-fx-background-color: blue");
        //node.setStyle("-fx-background-color: green");
        floatPopup.getStyleClass().clear();
        floatPopup.setOnShown(e -> {
            DockRegistry.register(floatPopup);
        });
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        if (show) {
            floatPopup.show(owner);
        }

        dockable.node().parentProperty().addListener(pcl);

        //addResizer(floatPopup, dockable);
        addResizer();
        //setResizer(new PopupControlResizer(this));
//        System.err.println("FLOAT POPUP CONTROL VIEW");        
        return floatPopup;
    }//make FloatingPopupControl

    @Override
    public void addResizer() {
        //if (getDockable().getContext().isResizable()) {
        removeListeners(getDockable().getContext().dockable());
        addListeners(getFloatingWindow());

        //}
        setResizer(new PopupControlResizer(this));

    }

}
