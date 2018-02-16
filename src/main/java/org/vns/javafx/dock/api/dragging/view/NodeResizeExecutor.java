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
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import static org.vns.javafx.dock.api.dragging.view.NodeResizer.windowBounds;

/**
 *
 * @author Valery
 */
public class NodeResizeExecutor implements WindowResizer {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();
    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private Cursor cursor;

    private NodeResizer nodeResizer;

    private Region node;

    public NodeResizer getNodeResizer() {
        return nodeResizer;
    }

    public Region getNode() {
        return node;
    }
    private Window window;
    private final Set<Cursor> cursorTypes = new HashSet<>();

    public NodeResizeExecutor(NodeResizer nodeResizer) {
        this(nodeResizer.getWindow(), nodeResizer.getNode());
        this.nodeResizer = nodeResizer;
    }

    public NodeResizeExecutor(Window window, Region node) {
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
        Region root = (Region) getWindow().getScene().getRoot();
        root.setMaxWidth(Double.MAX_VALUE);

        if (wDelta + getWindow().getWidth() > getMinWidth()) {
            if ((node.getWidth() > node.minWidth(-1) || xDelta <= 0)) {
                double nodeNewX = node.getBoundsInParent().getMinX() - node.getLayoutX();
                if (cursor == Cursor.W_RESIZE) {
                    node.setTranslateX(nodeNewX + xDelta);
                    node.setPrefWidth(wDelta + node.getPrefWidth());
                } else {
                    node.setPrefWidth(wDelta + node.getPrefWidth());
                }

                mouseX.set(curX);
            }
        }

        if (hDelta + getWindow().getHeight() > getMinHeight()) {
            if ((node.getHeight() > node.minHeight(-1) || yDelta <= 0)) {
                double nodeNewY = node.getBoundsInParent().getMinY() - node.getLayoutY();
                if (cursor == Cursor.N_RESIZE) {
                    node.setTranslateY(nodeNewY + yDelta);
                    node.setPrefHeight(hDelta + node.getPrefHeight());
                } else {
                    node.setPrefHeight(hDelta + node.getPrefHeight());
                }

                mouseY.set(curY);
            }
        }
        windowBounds(window, (Region) node);
    }

    public void resizeUnmanaged(double x, double y) {
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

        Region root = (Region) getWindow().getScene().getRoot();
        root.setMaxWidth(Double.MAX_VALUE);

        Bounds oldBounds = node.getBoundsInParent();

        double oldX = oldBounds.getMinX();
        double oldY = oldBounds.getMinY();

        double oldWidth = node.getWidth();
        double oldHeight = node.getHeight();

        double newX = oldX + xDelta;
        double newY = oldY + yDelta;
        double newWidth = wDelta + oldWidth;
        double newHeight = hDelta + oldHeight;

        node.resizeRelocate(newX, newY, newWidth, newHeight);
        windowBounds(window, (Region) node);
        mouseX.set(curX);
        mouseY.set(curY);
    }

    protected double getMinWidth() {
        double retval = 0.0;
        if (window instanceof Stage) {
            retval = ((Stage) window).getMinWidth();
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
    @Override
    public void resize(MouseEvent ev) {
        resize(ev.getScreenX(), ev.getScreenY());
    }

    @Override
    public boolean isStarted() {
        return getWindow() != null;
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
        return cursorBy(x, ev.getY(), width, height, left, right, top, bottom);
    }

    public static Cursor cursorBy(MouseEvent ev, Region r) {
        double x, y, w, h;

        Insets ins = r.getInsets();

        if (ins == Insets.EMPTY) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
        }
        return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
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
