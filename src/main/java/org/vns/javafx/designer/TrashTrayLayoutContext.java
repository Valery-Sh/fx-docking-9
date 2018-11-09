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
package org.vns.javafx.designer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.vns.javafx.ContextLookup;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.indicator.DefaultPositionIndicator;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Olga
 */
public class TrashTrayLayoutContext extends LayoutContext {

    public TrashTrayLayoutContext(Node layoutNode) {
        super(layoutNode);
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        lookup.putUnique(PositionIndicator.class, new DefaultPositionIndicator(this));
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
     
        Dockable d = dockable;
        DragContainer dc = dockable.getContext().getDragContainer();
        Object obj = null;

        if (dc != null && dc.getValue() != null) {
            if (!dc.isValueDockable()) {
                obj = dc.getValue();
            } else {
                obj = Dockable.of(dc.getValue()).node();
            }
        } else {
            obj = dockable.node();
        }
        if (contains(obj)) {
            return;
        }
        ((TrashTray) getLayoutNode()).add(obj);
        
        commitDock(obj);
    }

    @Override
    public void remove(Object obj) {
        ((TrashTray) getLayoutNode()).remove(obj);
    }

    @Override
    public boolean contains(Object obj) {
        return ((TrashTray) getLayoutNode()).contains(obj);
    }

}
