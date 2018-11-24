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
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
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

      
        windowRoot = createRoot(node);
        setWindowRoot(windowRoot);
        windowRoot.getStyleClass().add("sidebar-popup-root");

        floatPopup.getScene().setRoot(windowRoot);

        node.applyCss();
        
        windowRoot.applyCss();
        
        floatPopup.setAutoFix(false);

        floatPopup.setOnShown(e -> {
           DockRegistry.register(floatPopup);
           
        });

        floatPopup.setOnHidden(e -> {
           DockRegistry.unregister(floatPopup);
        });
        floatPopup.getStyleClass().clear();
        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };
        
        dockable.node().parentProperty().addListener(pcl);

        addResizer();
        
        return floatPopup;
    }//make FloatingPopupControlView2
}
