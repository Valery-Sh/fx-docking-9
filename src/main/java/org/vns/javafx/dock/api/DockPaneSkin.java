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
    private DockSplitPane rootLayout;

    public DockPaneSkin(DockPane control, DockSplitPane root) {
        super(control);
        this.rootLayout = root;
        //getSkinnable().

        layout = new StackPane(rootLayout) {
            @Override
            protected void layoutChildren() {
                //layout.setVbarPolicy(getSkinnable().getScrollPaneVbarPolicy());
                //resizeLabels();
                update(rootLayout);
                super.layoutChildren();
            }
        };
        getChildren().add(layout);
    }
    protected void update(DockSplitPane splitPane) {
        for ( Node node : splitPane.getItems()) {
           if ( ! ( (node instanceof HPane)  || (node instanceof VPane) || Dockable.of(node) != null)  ) {
               throw new IllegalArgumentException("Unsupported item type (type=" + node.getClass().getName() ); 
           }
            System.err.println("NODE: " + node);
           if ( node instanceof DockSplitPane ) {
               update((DockSplitPane) node);
           } else if ( Dockable.of(node) != null ) {
                   TargetContext tc = DockTarget.of(getSkinnable()).getTargetContext();
                   Dockable.of(node).getContext().setTargetContext(tc);
           }
        }
    }
    
}
