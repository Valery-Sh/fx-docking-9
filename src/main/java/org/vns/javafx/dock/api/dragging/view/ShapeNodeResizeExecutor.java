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
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Valery
 */
public class ShapeNodeResizeExecutor implements ResizeExecutor {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();

    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private double startHeight;

    private Cursor cursor;

    private final Region node;

    public Region getNode() {
        return node;
    }

    Rectangle indicator;

    private final Set<Cursor> cursorTypes = new HashSet<>();

    public ShapeNodeResizeExecutor(Rectangle indicator, Region node) {
        this.indicator = indicator;
        this.node = node;
        Collections.addAll(cursorTypes,
                Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
                Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE);
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

        double curX = mouseX.get();
        double curY = mouseY.get();
        if (cursor == Cursor.S_RESIZE) {
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

        //if (wDelta + indicator.getWidth() > getMinWidth()) {
        //    if ((node.getWidth() > node.getMinWidth() || xDelta <= 0)) {
        if (wDelta != 0) {
            node.setPrefWidth(wDelta + node.getWidth());
            mouseX.set(curX);
        }
        //node.setPrefWidth(wDelta + node.getWidth());
        //}
        //}

        //if (hDelta + indicator.getHeight() > getMinHeight()) {
        //    if ((node.getHeight() > node.getMinHeight() || yDelta <= 0)) {
        //System.err.println("node.prefHeight = " + node.getPrefHeight() + "; node = " + node);
        //node.setPrefHeight(y - mouseY.get() - startHeight + node.getHeight());
        
            //System.err.println("before node.hetHeight=" + node.getHeight());
            //System.err.println("indicator.getHeight=" + indicator.getHeight());
            
            //node.setPrefHeight(hDelta + node.getHeight());
            indicator.setHeight(hDelta + indicator.getHeight());
            //System.err.println("after node.hetHeight=" + node.getHeight());

            //System.err.println("hDelta = " + hDelta + "; curY=" + curY);
            //System.err.println("--------------------------------------------");            
        

        mouseY.set(curY);
        //    }
        //}
        //windowBounds(window, (Region) node);
    }

    public void resizeUnmanaged(double x, double y) {

    }

    protected double getMinWidth() {
        return node.getMinWidth();
    }

    protected double getMinHeight() {
        return node.getMinHeight();
    }


    @Override
    public boolean isStarted() {
        return indicator != null;
    }

    public void start(MouseEvent ev, ShapeNodeFraming nodeResizer, Cursor cursor, Cursor... supportedCursors) {
        System.err.println("START startHeight = " + startHeight);
        setCursorTypes(supportedCursors);
        this.mouseX.set(ev.getScreenX());
        this.mouseY.set(ev.getScreenY());
        startHeight = node.getHeight();
        System.err.println("START startHeight 1 = " + startHeight);
        System.err.println("startHeight = " + startHeight);
        this.cursor = cursor;
        //node.setPrefWidth(nodeResizer.getWorkWidth());
        //node.setPrefHeight(nodeResizer.getWorkHeight());
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

    public static Cursor cursorBy(MouseEvent ev, double width, double height, double left, double right, double top, double bottom) {
        double x = ev.getX();
        double y = ev.getY();
        return cursorBy(x, y, width, height, left, right, top, bottom);
    }

    public static Cursor cursorBy(MouseEvent ev, Rectangle r) {
        double x, y, w, h;

        double strokeWidth = r.getStrokeWidth();

        return cursorBy(ev, r.getWidth(), r.getHeight(), 5, 5, 5, 5);
    }

    public static Cursor cursorBy(double x, double y, Rectangle r) {
        double strokeWidth = r.getStrokeWidth();

        return cursorBy(x, y, r.getWidth(), r.getHeight(), 5, 5, 5, 5);
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

}
