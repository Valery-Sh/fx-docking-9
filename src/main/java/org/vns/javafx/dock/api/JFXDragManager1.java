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
package org.vns.javafx.dock.api;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class JFXDragManager1 extends FxDragManager {

    public static String DRAG_PANE_KEY = "JFXPANEL:drag-source-pane";
    public static String DRAG_FLOATING_STAGE = "JFXPANEL:drag-floating-stage";

    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     */
    public JFXDragManager1(Dockable dockNode) {
        super(dockNode);
    }

    @Override
    protected void setFloating(boolean floating) {
        getDockable().dockableController().setFloatingAsPopup(floating);    
    }
    @Override
    protected Node getFloatingWindowRoot() {
        Popup popup = (Popup) getDockable().node().getScene().getWindow();
        return popup == null ? null : popup.getContent().get(0);   
    }

}
