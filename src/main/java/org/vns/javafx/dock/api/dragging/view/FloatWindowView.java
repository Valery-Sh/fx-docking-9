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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public interface FloatWindowView extends FloatView<Window> {

    Dockable getDockable();

    Region getRootPane();
    
    WindowResizer getResizer();
    
    void addResizer();

    ObjectProperty<Window> floatingWindowProperty();

    Window getFloatingWindow();
    
    public Cursor[] getSupportedCursors();
    
    

/*    double getMinWidth();

    double getMinHeight();
*/
    void initialize();
    
    public void setSupportedCursors(Cursor[] supportedCursors);

    default BooleanProperty createFloatingProperty() {
        BooleanProperty bp = new SimpleBooleanProperty(false) {
            @Override
            protected void invalidated() {
                getDockable().node().pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
                if (getRootPane() != null) {
                    getRootPane().pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
                }
            }

            @Override
            public String getName() {
                return "floating";
            }
        };
        return bp;
    }
    
    public static class MouseResizeHandler implements EventHandler<MouseEvent> {

        private boolean cursorSupported = false;
        private FloatWindowView windowView;

        public MouseResizeHandler(FloatWindowView windowView) {
            this.windowView = windowView;
        }
        
        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Cursor c = StageResizer.cursorBy(ev, windowView.getRootPane());
                System.err.println("MouseResizer: cursor = " + c);
                if (!isCursorSupported(c)) {
                    windowView.getFloatingWindow().getScene().setCursor(Cursor.DEFAULT);
                } else {
                    windowView.getFloatingWindow().getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Cursor c = StageResizer.cursorBy(ev, windowView.getRootPane());
                cursorSupported = isCursorSupported(c);
                if (!cursorSupported) {
                    windowView.getFloatingWindow().getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                windowView.getResizer().start(ev, windowView.getFloatingWindow(), windowView.getFloatingWindow().getScene().getCursor(), windowView.getSupportedCursors() );
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (!cursorSupported) {
                    return;
                }
                if ( ! windowView.getResizer().isStarted()) {
                   windowView. getResizer().start(ev, windowView.getFloatingWindow(), windowView.getFloatingWindow().getScene().getCursor(), windowView.getSupportedCursors() );
                } else {
                    Platform.runLater(() -> {
                        windowView.getResizer().resize(ev);
                    });
                    
                }
            }
        }

        public boolean isCursorSupported(Cursor cursor) {
            if (cursor == null || cursor == Cursor.DEFAULT) {
                return false;
            }
            boolean retval = false;
            
            for (Cursor c : windowView.getSupportedCursors()) {
                if (c == cursor) {
                    retval = true;
                    break;
                }
            }
            
            return retval;
        }

    }//class MouseResizeHandler
    
}
