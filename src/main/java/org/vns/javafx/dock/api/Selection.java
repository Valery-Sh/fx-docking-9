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
import javafx.scene.input.MouseEvent;

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
//        System.err.println("Selection removeListeners");
        if (sel != null) {
//            System.err.println("1 Selection removeListeners");
            sel.removeSelected();
        }
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
//            System.err.println("SelectionHandler mausePressed source       = " + source);
//            System.err.println("SelectionHandler mausePressed event.source = " + ev.getSource());
            Selection sel = DockRegistry.lookup(Selection.class);
            setSource(ev.getSource());
            sel.notifySelected(ev.getSource());
            sel.setSelected(ev.getSource());
            ev.consume();

        }

        protected void mouseRelesed(MouseEvent ev) {
//            System.err.println("SelectionHandler mouseRelesed source       = " + source);
//            System.err.println("SelectionHandler mouseRelesed event.source = " + ev.getSource());
            if ((ev.getSource() == getSource() || getSource() == null) && Dockable.of(ev.getSource()) != null) {
                Selection sel = DockRegistry.lookup(Selection.class);
                //if (sel.getSelected() != getSource()) {
//                System.err.println("   --- setSelected");
                setSource(null);
                ev.consume();
                //}
            }

        }

    }
}
