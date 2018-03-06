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
package org.vns.javafx.dock.api.designer;

import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public abstract class Selection {

    private boolean stopped;

    private ObjectProperty selected = new SimpleObjectProperty();


    public abstract void selectTreeItem(Object value);

    public ObjectProperty selectedProperty() {
        return selected;
    }

    
    public void setSelected(Object toSelect) {
        System.err.println("Selection: setSelected: toSelect = " + toSelect);
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
        Platform.runLater(() -> {
            //setSelected(null);
        });
    }

    public static void removeListeners(Dockable dockable) {
        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.removeSelected(dockable.node());
        }
        SelectionListener l = DockRegistry.lookup(SelectionListener.class);
        dockable.node().removeEventHandler(MouseEvent.MOUSE_CLICKED, l);
        dockable.node().removeEventFilter(MouseEvent.MOUSE_CLICKED, l);
    }

    public static interface SelectionListener extends EventHandler<MouseEvent> {

        Object getSource();

        void setSource(Object source);

    }

    public static interface SelectionHandler1 extends SelectionListener {

    }

    public static class SelectionHandler implements SelectionListener {

        private Object source;

        public SelectionHandler() {
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePressed(ev);
            }
            if (ev.getEventType() == MouseEvent.MOUSE_CLICKED) {
                mouseClicked(ev);
            }

        }

        protected void mousePressed(MouseEvent ev) {
            System.err.println("SelectionHandler mausePressed source       = " + source);
            System.err.println("SelectionHandler mausePressed event.source = " + ev.getSource());
        }

        protected void mouseClicked(MouseEvent ev) {
            System.err.println("SelectionHandler mauseClicked source       = " + source);
            System.err.println("SelectionHandler mauseClicked event.source = " + ev.getSource());
            if ((ev.getSource() == getSource() || getSource() == null) && Dockable.of(ev.getSource()) != null) {
                Selection sel = DockRegistry.lookup(Selection.class);
                //if (sel.getSelected() != getSource()) {
                System.err.println("   --- setSelected");
                sel.setSelected(ev.getSource());
                setSource(null);
                ev.consume();
                //}
            }
            
        }

    }

    public static interface SelectionFilter extends SelectionListener {
    }

}
