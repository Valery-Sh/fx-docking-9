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
package org.vns.javafx.dock;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockTabPane2Context;
import org.vns.javafx.dock.api.DockTabPane2MouseDragHandler;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane2 extends TabPane {

    public DockTabPane2() {
        init();
    }

    public DockTabPane2(Tab... tabs) {
        super(tabs);
        init();
    }
    private void init() {
        LayoutContext tc = new DockTabPane2Context(this);

        DockRegistry.makeDockLayout(this, tc);
        Dockable d = DockRegistry.makeDockable(this);
        d.getContext().setLayoutContext(tc);
        d.getContext().setDragNode(this);
        DockTabPane2MouseDragHandler dragHandler = new DockTabPane2MouseDragHandler(d.getContext());
        d.getContext().getLookup().putUnique(MouseDragHandler.class, dragHandler);
        getStyleClass().add("dock-tab-pane2");
    }
}
