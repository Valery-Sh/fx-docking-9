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
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class StageResizer implements Resizer {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();
    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private Cursor cursor;
    private Window window;

    private final Set<Cursor> cursorTypes = new HashSet<>();

    private FloatWindowView windowView;

    protected StageResizer() {
        Collections.addAll(cursorTypes,
                Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
                Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE);
    }

    public StageResizer(FloatWindowView windowView) {
        this();
        this.windowView = windowView;
    }

    public void setWindowView(FloatWindowView windowView) {
        this.windowView = windowView;
    }

    private void setCursorTypes(Cursor... cursors) {
        cursorTypes.clear();
        Collections.addAll(this.cursorTypes, cursors);
    }

    @Override
    public void resize(double x, double y) {
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
        Region root = (Region) window.getScene().getRoot();
        root.setMaxWidth(Double.MAX_VALUE);

        if (wDelta + window.getWidth() > ((Stage) window).getMinWidth()) {
            Region child = (Region) ((Pane) root).getChildren().get(0);
            double childMin = child.minWidth(-1);

            if (child.getWidth() > childMin || wDelta > 0 && child.getWidth() == childMin) {
                window.setX(xDelta + window.getX());
                root.setPrefWidth(wDelta + root.getPrefWidth());
                window.setWidth(wDelta + window.getWidth());
                mouseX.set(curX);
                root.setMinWidth(window.getWidth());
            }
        }
        if (hDelta + window.getHeight() > ((Stage) window).getMinHeight()) {
            Region child = (Region) ((Pane) root).getChildren().get(0);
            double childMin = child.minHeight(-1);
            if (child.getHeight() > childMin || hDelta > 0 && child.getHeight() == childMin) {
                window.setY(yDelta + window.getY());
                root.setPrefHeight(hDelta + root.getPrefHeight());
                root.setMinHeight(hDelta + window.getHeight());
                window.setHeight(hDelta + window.getHeight());
                mouseY.set(curY);
                //root.setMinHeight(window.getHeight());
            }

        }

        //((Stage) window).sizeToScene();
    }

    protected double getMinWidth() {
        double retval = 50.0;
        if (window instanceof Stage) {
            retval = ((Stage) window).getMinWidth();
        }
        return retval;
    }

    protected double getMinHeight() {
        double retval = 50.0;
        if (window instanceof Stage) {
            retval = ((Stage) window).getMinHeight();
        }
        return retval;
    }


    @Override
    public boolean isStarted() {
        return getWindow() != null;
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public void start(MouseEvent ev, Window stage, Cursor cursor, Cursor... supportedCursors) {
        setCursorTypes(supportedCursors);
        this.mouseX.set(ev.getScreenX());
        this.mouseY.set(ev.getScreenY());

        this.cursor = cursor;
        this.window = stage;
        Region r = (Region) window.getScene().getRoot();
//        System.err.println("START: minH = " + r.getMinHeight());
//        System.err.println("START: prefH = " + r.getPrefHeight());
//        System.err.println("START:    docNode.prefH = " + ((Region)((Pane)r).getChildren().get(0)).getPrefHeight());
    }

    public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
        boolean e, w, n, s;
        Cursor cursor = Cursor.DEFAULT;
        w = nodeX < left;
        e = nodeX > width - right;
        n = nodeY < top;
        s = nodeY > height - bottom;
//        System.err.println("w  = " + w);
//        System.err.println("e  = " + e);
//        System.err.println("n  = " + n);
//        System.err.println("s  = " + s);
//        System.err.println("---------------------------------");        
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
        Cursor retval;//
        for (Cursor c : supported) {
            if (c.equals(cursor)) {
                retval = cursor;
                break;
            }
        }
        return cursor;
    }

    public static Cursor cursorBy(MouseEvent ev, double width, double height, double left, double right, double top, double bottom) {
        Window w = (Window) ev.getSource();
        double x = ev.getX();
        double y = ev.getY();
        if (w instanceof Stage) {
            return cursorBy(x, ev.getY(), width, height, left, right, top, bottom);
        } else {
            //x -= left;
            //y -= top;
        }
        return cursorBy(x, y, width, height, left, right, top, bottom);
    }

    public static Cursor cursorBy(MouseEvent ev, Region r) {
        double x, y, w, h;

        Insets nodeIns = r.getInsets();
        Insets ins = new Insets(Math.max(nodeIns.getTop(), 5), Math.max(nodeIns.getRight(), 5), Math.max(nodeIns.getBottom(), 5), Math.max(nodeIns.getLeft(), 5));
        //Insets ins = r.getInsets();
        //System.err.println("Resiser: INSETS = " + ins);
        if (ins == Insets.EMPTY) {
            //return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, ins.getLeft() + 5, 5, 5);
        }

        //if (ev.getSource() instanceof Stage) {
        return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
        //}
        //return     cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
    }

    public static void test(MouseEvent ev, Region r) {
        Insets ins = r.getInsets();
        Window w = (Window) ev.getSource();
        if (w instanceof PopupControl) {
            PopupControl pc = (PopupControl) w;
        }
    }

    public static void testStage(MouseEvent ev, Region r) {
        Insets ins;// = r.getInsets();
        Stage pc = (Stage) ev.getSource();
        Bounds rootB = r.localToScreen(r.getBoundsInLocal());
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

    public FloatWindowView getWindowView() {
        return windowView;
    }

    @Override
    public void start(MouseEvent ev, WindowNodeFraming resizer, Cursor cursor, Cursor... supportedCursors) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
