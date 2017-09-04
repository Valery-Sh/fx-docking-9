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
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
public class StageResizer implements WindowResizer {

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
            window.setX(xDelta + window.getX());
            //window.setWidth(wDelta + window.getWidth());
            root.setPrefWidth(wDelta + root.getPrefWidth());
            mouseX.set(curX);
        }

/*        System.err.println("root isresisable " + root.isResizable());
        if (wDelta + window.getWidth() > ((Stage) window).getMinWidth()) {
            window.setX(xDelta + window.getX());
            root.resize(wDelta + root.getPrefWidth(), root.getPrefHeight());
            mouseX.set(curX);
            root.setPrefWidth(wDelta + root.getPrefWidth());

        }
*/
        if (hDelta + window.getHeight() > ((Stage) window).getMinHeight()) {
            window.setY(yDelta + window.getY());
            root.setPrefHeight(hDelta + root.getPrefHeight());
            mouseY.set(curY);
        }

        ((Stage) window).sizeToScene();
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
    public void resize(MouseEvent ev) {
        resize(ev.getScreenX(), ev.getScreenY());
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
    }

    public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
        boolean e, w, n, s;
        Cursor cursor = Cursor.DEFAULT;
        w = nodeX < left;
        e = nodeX > width - right;
        n = nodeY < top;
        s = nodeY > height - bottom;
        /*        System.err.println("*************************************");
        System.err.println("w=" + w + "; noseX=" + nodeX + "; left=" + left);
        if (w) {
            System.err.println("   ---------- WEST");
        } else {
            System.err.println("   --- w=" + w + "; noseX=" + nodeX + "; left=" + left);
        }
        System.err.println("e=" + e);
        System.err.println("n=" + n);
        System.err.println("s=" + s);
        System.err.println("*************************************");
         */
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
        Window w = (Window) ev.getSource();
        double x = ev.getX();
        double y = ev.getY();
        if (w instanceof Stage) {
            return cursorBy(x, ev.getY(), width, height, left, right, top, bottom);
        } else {
            x -= left;
            y -= top;
        }
        return cursorBy(x, y, width, height, left, right, top, bottom);
        //return cursorBy(ev.getX(), ev.getY(), width, height, left, right, top, bottom);
        //double x = ((PopupControl)ev.getSource()).getAnchorX();
        //Region root = (Region) ((Window)ev.getSource()).getScene().getRoot();
        //System.err.println("cursorBy win screenX =" + ev.getScreenX());
        //System.err.println("cursorBy root screenX =" + root.localToScreen(x, bottom));
        //System.err.println("cursorBy win screenX =" + ((PopupControl)ev.getSource()).getAnchorLocation());
        //return cursorBy(ev.getSceneX() - left, ev.getSceneY() - top, width, height, left, right, top, bottom);
    }

    public static Cursor cursorBy(MouseEvent ev, Region r) {
        double x, y, w, h;

        Insets ins = r.getInsets();
        //test(ev, r);
        if (ins == Insets.EMPTY) {
//            if (ins == Insets.EMPTY) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
        }
        //return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5,  5, ins.getTop() + 5, 5);
        if (ev.getSource() instanceof Stage) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
        } else {

        }
        return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
    }

    public static void test(MouseEvent ev, Region r) {
        Insets ins = r.getInsets();
        Window w = (Window) ev.getSource();
        //if (ev.getSource() instanceof PopupControl) {
        if (w instanceof PopupControl) {
            PopupControl pc = (PopupControl) w;
            /*            System.err.println("INSETS: " + ins.toString());
            System.err.println("popup.getY() = " + pc.getY());
            System.err.println("popup.getAnchorY() = " + pc.getAnchorY());
            System.err.println("ev.getY() = " + ev.getY());
            System.err.println("ev.getScreenY() = " + ev.getScreenY());
            Bounds rootB = r.localToScreen(r.getBoundsInLocal());
            System.err.println("root.getScreenY() = " + rootB.getMinY());
            System.err.println("root.getHeight() = " + r.getHeight());
            System.err.println("root.bounds.getHeight() = " + rootB.getHeight());
            System.err.println("popup.getHeight() = " + pc.getHeight());
           System.err.println("root.getScreenY() = " + rootB.getMinY());
            System.err.println("root.getHeight() = " + r.getHeight());
            System.err.println("root.bounds.getHeight() = " + rootB.getHeight());
            System.err.println("popup.getHeight() = " + pc.getHeight());
             */
        }
    }

    public static void testStage(MouseEvent ev, Region r) {
        Insets ins = r.getInsets();
        Stage pc = (Stage) ev.getSource();
        System.err.println("INSETS: " + ins.toString());
        System.err.println("popup.getX() = " + pc.getX());
        System.err.println("ev.getX() = " + ev.getX());
        System.err.println("ev.getScreenX() = " + ev.getScreenX());
        Bounds rootB = r.localToScreen(r.getBoundsInLocal());
        System.err.println("root.getScreenX() = " + rootB.getMinX());
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

}
