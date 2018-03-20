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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
//import static org.vns.javafx.dock.api.dragging.view.NodeResizer.windowBounds;

/**
 *
 * @author Valery
 */
public class NodeResizer implements Resizer {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();
    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private Cursor cursor;

    private final Region node;

    public Region getNode() {
        return node;
    }
    private final Window window;
    private final Set<Cursor> cursorTypes = new HashSet<>();

    public NodeResizer(Window window, Region node) {
        this.window = window;
        this.node = node;
        Collections.addAll(cursorTypes,
                Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
                Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE);
    }
    
    protected Window getWindow() {
        return window;
    }

    private void setCursorTypes(Cursor... cursors) {
        cursorTypes.clear();
        Collections.addAll(this.cursorTypes, cursors);
    }

    @Override
    public void resize(double x, double y) {
        if (!node.isManaged()) {
            resizeUnmanaged(x, y);
        } else {
            resizeManaged(x, y);
        }

    }

    public void resizeManaged(double x, double y) {
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;
//        System.err.println("resizeManaged: x=" + x + "; y=" + y + "; cursor=" + cursor);

        double curX = mouseX.get();
        double curY = mouseY.get();
        if (cursor == Cursor.S_RESIZE) {
//            System.err.println("resizeManaged: y=" + y + "; mouseY=" + this.mouseY.get());
            hDelta = y - this.mouseY.get();
            curY = y;
        } else if (cursor == Cursor.E_RESIZE) {
            wDelta = x - this.mouseX.get();
            curX = x;
        } else if (cursor == Cursor.N_RESIZE) {
            hDelta = this.mouseY.get() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (cursor == Cursor.W_RESIZE) {
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
        } else if (cursor == Cursor.SE_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
            wDelta = x - this.mouseX.get();
            curX = x;

        } else if (cursor == Cursor.NE_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = x - this.mouseX.get();
            yDelta = -hDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.SW_RESIZE) {
            hDelta = y - this.mouseY.get();
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.NW_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            yDelta = -hDelta;
            curX = x;
            curY = y;
        }
        Region root = (Region) getWindow().getScene().getRoot();
        root.setMaxWidth(Double.MAX_VALUE);
//        System.err.println("RESIZE === ");
        //Window win = getWindow();
        if (wDelta + getWindow().getWidth() > getMinWidth()) {
            if ((node.getWidth() > node.minWidth(-1) || xDelta <= 0)) {
//                win.setWorkWidth(wDelta + win.getWorkWidth());
                
                node.setPrefWidth(wDelta + node.getPrefWidth());
                mouseX.set(curX);
            }
        }

        if (hDelta + getWindow().getHeight() > getMinHeight()) {
            if ((node.getHeight() > node.minHeight(-1) || yDelta <= 0)) {
//                win.setWorkHeight(hDelta + win.getWorkHeight());
//                System.err.println("hDelta = " + hDelta + "; node.prefH=" + node.getPrefHeight());
                node.setPrefHeight(hDelta + node.getPrefHeight());
                mouseY.set(curY);
            }
        }
        //windowBounds(window, (Region) node);
    }

    public void resizeUnmanaged(double x, double y) {
        
    }
    

    protected double getMinWidth() {
        double retval = 0.0;
        if (window instanceof Stage) {
            //retval = ((Stage) window).getMinWidth();
        } else if (window instanceof PopupControl) {
            retval = ((PopupControl) window).getMinWidth();
        }
        return retval;
    }

    protected double getMinHeight() {
        double retval = 0.0;
        if (window instanceof Stage) {
            retval = ((Stage) window).getMinHeight();
        } else if (window instanceof PopupControl) {
            retval = ((PopupControl) window).getMinHeight();
        }
        return retval;
    }
/*    @Override
    public void resize(MouseEvent ev) {
        resize(ev.getScreenX(), ev.getScreenY());
    }
*/
    @Override
    public boolean isStarted() {
        return getWindow() != null;
    }

    @Override
    public void start(MouseEvent ev, WindowNodeFraming nodeResizer, Cursor cursor, Cursor... supportedCursors) {

        setCursorTypes(supportedCursors);
        this.mouseX.set(ev.getScreenX());
        this.mouseY.set(ev.getScreenY());
//        System.err.println("start: cursor = " + cursor);
        this.cursor = cursor;
        //this.window = window;
        Region r = (Region) window.getScene().getRoot();
//        node.setPrefWidth(nodeResizer.getWorkWidth());
//        node.setPrefHeight(nodeResizer.getWorkHeight());
        node.setPrefWidth(node.getWidth());
        node.setPrefHeight(node.getHeight());
        
    }

    public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
        boolean e, w, n, s;
        Cursor cursor = Cursor.DEFAULT;
        w = nodeX < left;
        e = nodeX > width - right;
        n = nodeY < top;
        s = nodeY > height - bottom;
        if (w) {
            if (n) {
                cursor = Cursor.NW_RESIZE;
            } else if (s) {
                cursor = Cursor.SW_RESIZE;
            } else {
                cursor = Cursor.W_RESIZE;
            }
        } else if (e) {
            if (n) {
                cursor = Cursor.NE_RESIZE;
            } else if (s) {
                cursor = Cursor.SE_RESIZE;
            } else {
                cursor = Cursor.E_RESIZE;
            }
        } else if (n) {
            cursor = Cursor.N_RESIZE;
        } else if (s) {
            cursor = Cursor.S_RESIZE;
        }
        Cursor retval = Cursor.DEFAULT;
        for (Cursor c : supported) {
            if (c.equals(cursor)) {
                retval = cursor;
                break;
            }
        }
        return cursor;
    }


    public static Cursor cursorBy(Point2D mousePos, double width, double height, double left, double right, double top, double bottom) {
        double x = mousePos.getX();
        double y = mousePos.getY();
        return cursorBy(mousePos.getX(), mousePos.getY(), width, height, left, right, top, bottom);
    }


    public static Cursor cursorBy(Point2D mousePos, Region r) {
        double x, y, w, h;

        Insets ins = r.getInsets();

        if (ins == Insets.EMPTY) {
            return cursorBy(mousePos, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
        }
        return cursorBy(mousePos, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
    }
    
    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public DoubleProperty mouseXProperty() {
        return mouseX;
    }

    public DoubleProperty mouseYProperty() {
        return mouseY;
    }

    public Double getMouseX() {
        return mouseX.get();
    }

    public Double getMouseY() {
        return mouseY.get();
    }

    public void setMouseX(Double mX) {
        this.mouseX.set(mX);
    }

    public void setMouseY(Double mY) {
        this.mouseY.set(mY);
    }

    @Override
    public void start(MouseEvent ev, Window window, Cursor cursor, Cursor... supportedCursors) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
