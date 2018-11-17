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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;

/**
 *
 * @author Valery
 */
public abstract class Selection {

    private final ObjectProperty selected = new SimpleObjectProperty();

    public abstract void notifySelected(Object value);

    public ObjectProperty selectedProperty() {
        return selected;
    }

    public void setSelected(Object toSelect) {
        this.selected.set(toSelect);
    }

    protected boolean doSelect(Object toSelect) {
        boolean retval = false;
        return retval;
    }

    public Object getSelected() {
        return selected.get();
    }

    public void removeSelected() {
        setSelected(null);
    }

    public void removeSelected(Object obj) {
        if (getSelected() == obj) {
            setSelected(null);
        }
    }

    public static void removeListeners(Dockable dockable) {
        Selection sel = DockRegistry.lookup(Selection.class);
        SelectionListener l = DockRegistry.lookup(SelectionListener.class);
        if (l != null) {
            dockable.node().removeEventHandler(MouseEvent.MOUSE_PRESSED, l);
            dockable.node().removeEventFilter(MouseEvent.MOUSE_PRESSED, l);
            dockable.node().removeEventHandler(MouseEvent.MOUSE_RELEASED, l);
            dockable.node().removeEventFilter(MouseEvent.MOUSE_RELEASED, l);
        }

    }

    public static interface SelectionListener extends EventHandler<MouseEvent> {

        Object getSource();

        void setSource(Object source);

    }

    public static class SelectionHandler implements SelectionListener {

        private Object source;

        public SelectionHandler() {
        }

        @Override
        public Object getSource() {
            return source;
        }

        @Override
        public void setSource(Object source) {
            this.source = source;
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePressed(ev);

            }
            if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseRelesed(ev);
            }
        }

        protected void mousePressed(MouseEvent ev) {
            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            if (nf != null && (ev.getSource() instanceof Node)) {
                nf.show((Node) ev.getSource());
            }
            ev.consume();

        }

        protected void mouseRelesed(MouseEvent ev) {
            if ((ev.getSource() == getSource() || getSource() == null) && Dockable.of(ev.getSource()) != null) {

                Selection sel = DockRegistry.lookup(Selection.class);

                ev.consume();
            }
        }

    }
}
