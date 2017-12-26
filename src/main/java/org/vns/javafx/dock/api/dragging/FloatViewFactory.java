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
package org.vns.javafx.dock.api.dragging;

import com.sun.javafx.stage.EmbeddedWindow;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.stage.Window;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.dragging.view.FloatPopupControlView;
import org.vns.javafx.dock.api.dragging.view.FloatStageView;
import org.vns.javafx.dock.api.dragging.view.FloatView;

/**
 *
 * @author Valery
 */
public class FloatViewFactory {

    private FloatViewFactory() {

    }
    public static FloatViewFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }
    
    public FloatView getFloatView(DragManager dragManager) {
        FloatView v = null;
        Node node = dragManager.getDockable().node();
        Window w = null;
        if ( node.getScene() != null && node.getScene().getWindow() != null) {
            w = node.getScene().getWindow();
        }
        if ( w == null || !(w instanceof EmbeddedWindow)) {
            v = new FloatStageView(dragManager.getDockable());
        } else if ( w instanceof EmbeddedWindow ) {
            v = new FloatPopupControlView(dragManager.getDockable());
        }
    
        return v;
    }
    private static class SingletonInstance {
        private static final FloatViewFactory INSTANCE = new FloatViewFactory();
    }
}
