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
import javafx.scene.layout.Region;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class FloatPopupControlView2 extends FloatPopupControlView {

    public FloatPopupControlView2(Dockable dockable) {
        super(dockable);
    }

    @Override
    public Window make(Dockable dockable, boolean show) {
        System.err.println("FLOATPOPUPCONTROLVIEW2");
        Node node = dockable.node();

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.getDockableContext().getTitleBar();
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

        setRootPane(borderPane);

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

        //floatingProperty().set(true);

        floatPopup.getScene().setRoot(borderPane);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        //floatPopup.setX(stagePosition.getX() - insetsDelta.getLeft());
        //floatPopup.setY(stagePosition.getY() - insetsDelta.getTop());
        floatPopup.setMinWidth(borderPane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        floatPopup.setMinHeight(borderPane.minHeight(DockUtil.widthOf(node)) + insetsHeight);
        //
        // We must prevent the window to end up positioning off the screen
        //
        floatPopup.setAutoFix(false);
        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);

        double prefWidth = borderPane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = borderPane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

        /*        System.err.println("DSB ++++++ CreatePopup popup.getWidth = " + floatPopup.getWidth());        
        System.err.println("DSB ++++++ CreatePopup popup.getMinWidth = " + floatPopup.getMinWidth());                
        System.err.println("DSB ++++++ CreatePopup borderPane.prefW = " + borderPane.getPrefWidth());
        System.err.println("   DSB ++++++ CreatePopup node width= " + node.getWidth());
        System.err.println("   DSB ++++++ CreatePopup node minWidth = " + node.getMinWidth());
        System.err.println("   DSB ++++++ CreatePopup node pref Width = " + node.getMinWidth());
        System.err.println("   DSB *** insetsWidth = " + insetsWidth);
        System.err.println("   DSB *** resizePrefWidth = " + prefWidth);
         */
        //borderPane.setStyle("-fx-background-color: aqua");
        //node.setStyle("-fx-background-color: green");
        floatPopup.setOnShown(e -> {
            DockRegistry.register(floatPopup);
        });
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        floatPopup.getStyleClass().clear();
        if (show) {
            //floatPopup.show(owner);
        }
        dockable.node().parentProperty().addListener(pcl);

        //addResizer(floatPopup, dockable);
        addResizer();
        //setResizer(new PopupControlResizer(this));
//        System.err.println("DSB FLOAT POPUP CONTROL VIEW");        
        return floatPopup;
    }//make FloatingPopupControl

}
