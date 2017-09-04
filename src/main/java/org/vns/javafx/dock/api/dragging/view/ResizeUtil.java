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
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class ResizeUtil {

    public static void start(Stage window) {
        Pane pane = (Pane) window.getScene().getRoot();
        Node node = pane.getChildren().get(0);
        Insets insetsDelta = pane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        window.setX(window.getX() - insetsDelta.getLeft());
        window.setY(window.getY() - insetsDelta.getTop());

        window.setMinWidth(pane.minWidth(DockUtil.heightOf(node)) + insetsWidth);
        window.setMinHeight(pane.minHeight(DockUtil.widthOf(node)) + insetsHeight);

        //setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        double prefWidth = pane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        double prefHeight = pane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;

        pane.setPrefWidth(prefWidth);
        pane.setPrefHeight(prefHeight);
        new MouseResizeHandler(window);
    }

    public static void start(Region pane) {
        start(pane, false);
    }

    public static void start(Region pane, boolean applyTranslateXY) {
        Stage window = new Stage();
        window.initStyle(StageStyle.TRANSPARENT);

        //window.setAlwaysOnTop(true);
        StackPane root = new StackPane();
        //root.setMinHeight(50);
        //root.setMinWidth(150);
        //root.setPadding(new Insets(10,10,10,10));
        //root.setStyle("-fx-background-color: transparent");
        //root.setBorder(new Border(5d,5d,5d,5d));
        root.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red;");
        Scene scene = new Scene(root);
        scene.setFill(null);
        window.setScene(scene);
        Bounds b = windowBounds(window, pane);
        //Bounds b = pane.localToScreen(pane.getBoundsInLocal());
/*        Bounds b = pane.localToScreen(pane.getBoundsInLocal());
        System.err.println("BOUNDS b=" + b);
        window.setX(b.getMinX() - 5);
        window.setY(b.getMinY() - 5);
        window.setWidth(b.getWidth() + 10);
        window.setHeight(b.getHeight() + 10);
         */
        //window.sizeToScene();
        //Node node = pane.getChildren().get(0);
        Insets insetsDelta = pane.getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
        pane.setPrefWidth(b.getWidth());
        pane.setPrefHeight(b.getHeight());
        window.setMinWidth(root.minWidth(DockUtil.heightOf(pane)) + insetsWidth);
        //setMinWidth(root.minWidth(node.getHeight()) + insetsWidth);
        //setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        //double prefWidth = pane.prefWidth(DockUtil.heightOf(node)) + insetsWidth;
        //double prefHeight = pane.prefHeight(DockUtil.widthOf(node)) + insetsHeight;
        //pane.setPrefWidth(prefWidth);
        //pane.setPrefHeight(prefHeight);
        window.initOwner(pane.getScene().getWindow());
        window.show();
        //window.sizeToScene();
        new MouseResizeHandler(window, pane, applyTranslateXY);
    }

    protected static Bounds windowBounds(Window window, Region pane) {
        Bounds b = pane.localToScreen(pane.getBoundsInLocal());
        //if (! window.isShowing()) {
        window.setX(b.getMinX() - 5);
        window.setY(b.getMinY() - 5);
        window.setWidth(b.getWidth() + 10);
        window.setHeight(b.getHeight() + 10);
        //} else {

        //}
        return b;
    }

    public static class MouseResizeHandler implements EventHandler<MouseEvent> {

        private Cursor[] supportedCursors = new Cursor[]{
            Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
            Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
        };
        private boolean cursorSupported = false;
        private Window window;
        private WindowResizer resizer;
        private Node node;
        private double translateX;
        private double translateY;
        private boolean applyTranskateXY;

        public MouseResizeHandler(Window window, Node node) {
            this(window, node, false);
        }

        public MouseResizeHandler(Window window, Node node, boolean applyTranslateXY) {
            this.window = window;
            this.node = node;
            this.applyTranskateXY = applyTranslateXY;
            init();
        }

        public MouseResizeHandler(Window window) {
            this.window = window;
            init();
        }

        private void init() {
            translateX = node.getTranslateX();
            translateY = node.getTranslateY();
            System.err.println("translateX = " + translateX);
            resizer = new StageResizer(window, node);
            window.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
            window.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            window.addEventFilter(MouseEvent.MOUSE_MOVED, this);
            window.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        }

        public Region getRresizableNode() {
            return (Region) node;
        }

        public WindowResizer getResizer() {
            return resizer;
        }

        public Cursor[] getSupportedCursors() {
            return supportedCursors;
        }

        public void setSupportedCursors(Cursor[] supportedCursors) {
            this.supportedCursors = supportedCursors;
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Cursor c = StageResizer.cursorBy(ev, (Region) window.getScene().getRoot());
                //Cursor c = StageResizer.cursorBy(ev, (Region) window.getScene().getRoot());

                if (!isCursorSupported(c)) {
                    window.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    window.getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Cursor c = StageResizer.cursorBy(ev, getRresizableNode());
                if (node != null && !applyTranskateXY) {
                    translateX = node.getTranslateX();
                    translateY = node.getTranslateY();
                }
//                System.err.println("2 FloatWindowView MouseHandler. mousepressed cursor = " + c);                
                cursorSupported = isCursorSupported(c);
//                System.err.println("2 FloatWindowView MouseHandler. isCursorSupported = " + cursorSupported);                
                if (!cursorSupported) {
                    window.getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                getResizer().start(ev, window, window.getScene().getCursor(), getSupportedCursors());
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
//                System.err.println("3 FloatWindowView MouseHandler. isCursorSupported = " + cursorSupported);                

                if (!cursorSupported) {
                    return;
                }
                if (!getResizer().isStarted()) {
//                System.err.println("4 FloatWindowView MouseHandler. resizer = " + stage.getResizer());                
//                System.err.println("4 FloatWindowView befor start MouseHandler. cursor = " + stage.getFloatingWindow().getScene().getCursor());                                

                    getResizer().start(ev, window, window.getScene().getCursor(), getSupportedCursors());
                } else {
                    Platform.runLater(() -> {
                        getResizer().resize(ev);
                    });

                }
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (node != null && !isApplyTranskateXY()) {
                    node.setTranslateX(translateX);
                    node.setTranslateY(translateY);
                }
                if (node != null) {
                    windowBounds(window, (Region) node);
                }
                commitResize();
            }
        }

        protected void commitResize() {
            //window.hide();
        }

        public void setCursorSupported(boolean cursorSupported) {
            this.cursorSupported = cursorSupported;
        }

        public boolean isApplyTranskateXY() {
            return applyTranskateXY;
        }

        public void setApplyTranskateXY(boolean applyTranskateXY) {
            this.applyTranskateXY = applyTranskateXY;
        }

        public boolean isCursorSupported(Cursor cursor) {
//            System.err.println("isCursorSupported cursor = " + cursor);            
            if (cursor == null || cursor == Cursor.DEFAULT) {
                return false;
            }
            boolean retval = false;
//            System.err.println("isCursorSupported stage = " + stage);            

//            System.err.println("isCursorSupported stage.getSupportedCursors().len = " + stage.getSupportedCursors().length);
            for (Cursor c : getSupportedCursors()) {
                if (c == cursor) {
                    retval = true;
                    break;
                }
            }
//            System.err.println("isCursorSupported retval = " + retval);            

            return retval;
        }

    }//class MouseResizeHandler

    public static class StageResizer implements WindowResizer {

        private final DoubleProperty mouseX = new SimpleDoubleProperty();
        private final DoubleProperty mouseY = new SimpleDoubleProperty();

        private Cursor cursor;
        private Window window;
        private Node node;

        private final Set<Cursor> cursorTypes = new HashSet<>();

        protected StageResizer() {
        }

        public StageResizer(Window window) {
            this(window, null);
            this.window = window;
        }

        public StageResizer(Window window, Node node) {
            this.window = window;
            this.node = node;
            Collections.addAll(cursorTypes,
                    Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
                    Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE);

        }

        public void setWindowView(Window window) {
            this.window = window;
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
            Border border = ((Region) root).getBorder();

            double borderWidth = 0;
            double borderHeight = 0;

            if (border != null) {
                borderWidth = border.getInsets().getLeft() + border.getInsets().getRight();
                borderHeight = border.getInsets().getTop() + border.getInsets().getBottom();
            }

            if (wDelta + window.getWidth() > ((Stage) window).getMinWidth()) {
                if (node != null && (((Region) node).getWidth() > ((Region) node).minWidth(-1) || xDelta <= 0)) {
                    double nodeNewX = node.getBoundsInParent().getMinX() - node.getLayoutX();
                    ((Region) node).setTranslateX(nodeNewX + xDelta);
                    ((Region) node).setPrefWidth(wDelta + ((Region) node).getPrefWidth());
                    mouseX.set(curX);
                } else if (node == null) {
                    window.setX(xDelta + window.getX());
                    ((Region) node).setLayoutX(xDelta + window.getX());
                    ((Stage) window).setWidth(wDelta + window.getWidth());
                    mouseX.set(curX);
                }
            }

            if (hDelta + window.getHeight() > ((Stage) window).getMinHeight()) {
                if (node != null && (((Region) node).getHeight() > ((Region) node).minHeight(-1) || yDelta <= 0)) {
                    double nodeNewY = node.getBoundsInParent().getMinY() - node.getLayoutY();
                    ((Region) node).setTranslateY(nodeNewY + yDelta);
                    ((Region) node).setPrefHeight(hDelta + ((Region) node).getPrefHeight());
//                    ((Stage) window).setHeight(hDelta + ((Region) node).getPrefHeight() + borderHeight);
                    mouseY.set(curY);
                } else {
                    window.setY(yDelta + window.getY());
                    root.setPrefHeight(hDelta + root.getPrefHeight());
                    mouseY.set(curY);
                }
            }

            if (node != null) {
                windowBounds(window, (Region) node);
            }
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
        }

        public static Cursor cursorBy(MouseEvent ev, Region r) {
            double x, y, w, h;

            Insets ins = r.getInsets();
            //test(ev, r);
            if (ins == Insets.EMPTY) {
                return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
            }
            if (ev.getSource() instanceof Stage) {
                return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
            } else {

            }
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
        }

        public static void test(MouseEvent ev, Region r) {
            Insets ins = r.getInsets();
            Window w = (Window) ev.getSource();
            if (w instanceof PopupControl) {
                PopupControl pc = (PopupControl) w;
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

    }

    public static class ResizePopup extends Popup {

        private Node targetNode;

        /**
         * Creates a new instance for the specified pane handler.
         *
         * @param target the owner of the object to be created
         */
        /*    public IndicatorPopup(DockTarget target) {
        this.targetNode = target.getTargetContext();
        init();
    }
         */
        public ResizePopup(Node target) {
            this.targetNode = target;
            init();
        }

        private void init() {
            initContent();
        }

        @Override
        public void show(Window ownerWindow, double anchorX, double anchorY) {
            super.show(ownerWindow, anchorX, anchorY);
        }

        @Override
        public void show(Node ownerNode, double anchorX, double anchorY) {
            super.show(ownerNode, anchorX, anchorY);
            System.err.println("SHOW 3 " + this.getProperties().get("POPUP"));
        }

        @Override
        public void hide() {
            super.hide();
        }

        /**
         * Returns an object of type {@code Region} which corresponds to the
         * pane handler which used to create this object.
         *
         * @return Returns an object of type {@code Region}
         */
        public Node getTargetNode() {
            return targetNode;
        }

        protected void initContent() {
            setOnShown(e -> {
                StackPane indicatorPane = new StackPane();
                indicatorPane.setStyle("-fx-border-width: 5; -fx-border-color: aqua");
                if (targetNode instanceof Region) {
                    indicatorPane.prefHeightProperty().bind(((Region) getTargetNode()).heightProperty());
                    indicatorPane.prefWidthProperty().bind(((Region) getTargetNode()).widthProperty());

                    indicatorPane.minHeightProperty().bind(((Region) getTargetNode()).heightProperty());
                    indicatorPane.minWidthProperty().bind(((Region) getTargetNode()).widthProperty());
                }
                if (!getContent().contains(indicatorPane)) {
                    getContent().add(indicatorPane);
                }
                new MouseResizeHandler(this, indicatorPane);

            });
        }

        /**
         * Hides the pop up window when some condition are satisfied. If this
         * pop up is hidden returns true. If the mouse cursor is still inside
         * the pane indicator then return true. Otherwise hides the pop up and
         * returns false
         *
         * @param x a screen x coordinate of the mouse cursor
         * @param y a screen y coordinate of the mouse cursor
         *
         * @return If this pop up is hidden returns true. If the mouse cursor is
         * still inside the pane indicator then return true. Otherwise hides the
         * pop up and returns false
         */
        public boolean hideWhenOut(double x, double y) {
            if (!isShowing()) {
                return true;
            }
            boolean retval = false;
            if (isShowing()) {
                hide();
            }
            return retval;
        }

    }

}//ResiserUtil
