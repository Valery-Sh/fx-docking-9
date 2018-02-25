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

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;

/**
 *
 * @author Valery
 */
public class DockPaneSkin extends SkinBase<DockPane> {

    private StackPane layout;
    //private VBox vboxlayout;
    private DockSplitPane rootLayout;
    //private Node titleNode;

    public DockPaneSkin(DockPane control, DockSplitPane root) {
        super(control);
        this.rootLayout = root;
        DockRegistry.makeDockable(getSkinnable());
        //getSkinnable().
        layout = new StackPane(rootLayout) {

            @Override
            protected void layoutChildren() {
                update(rootLayout);
                super.layoutChildren();
            }
        };

        if (getSkinnable().getTitleBar() != null) {
            Node node = getSkinnable().getTitleBar();
            if (Dockable.of(node) == null) {
                Dockable d = DockRegistry.makeDockable(node);
                d.getContext().setDraggable(false);
            }
            Dockable.of(getSkinnable()).getContext().setDragNode(node);
            
            getSkinnable().dock(node, Side.TOP);
            
        }

        getSkinnable().titleBarProperty().addListener((v, oldValue, newValue) -> {
            TargetContext tc = DockTarget.of(getSkinnable()).getTargetContext();
            if (oldValue != null) {
                tc.undock(oldValue);
            }
            if (newValue != null) {
                if (Dockable.of(newValue) == null) {
                    Dockable d = DockRegistry.makeDockable(newValue);
                    d.getContext().setDraggable(false);
                }
                Dockable.of(getSkinnable()).getContext().setDragNode(newValue);

                getSkinnable().dock(newValue, Side.TOP);
            }
        });
        getChildren().add(layout);
    }

    protected void update(DockSplitPane splitPane) {
        for (Node node : splitPane.getItems()) {
            if (!((node instanceof HPane) || (node instanceof VPane) || Dockable.of(node) != null)) {
                throw new IllegalArgumentException("Unsupported item type (type=" + node.getClass().getName());
            }
            if (node instanceof DockSplitPane) {
                update((DockSplitPane) node);
            } else if (Dockable.of(node) != null) {
                TargetContext tc = DockTarget.of(getSkinnable()).getTargetContext();
                Dockable.of(node).getContext().setTargetContext(tc);
            }
        }
    }

}
