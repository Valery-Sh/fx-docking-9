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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class NodeResizer1 {

    private boolean applyFtranslateXY;
    private boolean hidePopup;

    private Options KIND;

    ObjectProperty<Window> window = new SimpleObjectProperty<>();

    Region node;

    public enum Options {
        APPLY_TRANSLATE_TRANSFORS,
        HIDE_POPUP_ON_MOUSE_RELEASE,
        STAGE,
        POPUPCONTROL,
        POPUP
    }

    public NodeResizer1(Region node, Options... options) {
        this.node = node;
        //
        // set defalts 
        //
        this.applyFtranslateXY = false;
        this.hidePopup = false;
        KIND = Options.POPUPCONTROL;
        for (Options opt : options) {
            if (opt == Options.APPLY_TRANSLATE_TRANSFORS) {
                applyFtranslateXY = true;
            } else if (opt == Options.HIDE_POPUP_ON_MOUSE_RELEASE) {
                hidePopup = true;
            } else if (opt == Options.STAGE) {
                KIND = Options.STAGE;
            } else if (opt == Options.POPUPCONTROL) {
                KIND = Options.POPUPCONTROL;
            } else if (opt == Options.POPUPCONTROL) {
                KIND = Options.POPUP;
            }
        }

        init();
    }

    private void init() {
    }

    public double getWindowMinWidth() {
        double retval = -1;
        if (KIND == Options.STAGE) {
            retval = ((Stage) getWindow()).getMinWidth();
        } else if (KIND == Options.POPUPCONTROL) {
            retval = ((PopupControl) getWindow()).getMinWidth();
        }
        return retval;
    }

    public double getWindowMinHeight() {
        double retval = -1;
        if (KIND == Options.STAGE) {
            retval = ((Stage) getWindow()).getMinHeight();
        } else if (KIND == Options.POPUPCONTROL) {
            retval = ((PopupControl) getWindow()).getMinHeight();
        }
        return retval;

    }

    protected void bindWindowPosition(Window owner) {
        owner.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                windowBounds(getWindow(), getNode());
            }
        });
        owner.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //stage.setX(stage.getX() + ( (Double)newValue - (Double)oldValue));
                windowBounds(getWindow(), getNode());
            }
        });

    }

    protected Stage createStage() {
        Stage stage = new Stage();
        setWindow(stage);
        stage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red;");
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
        Window owner = getNode().getScene().getWindow();
        ((Stage) getWindow()).initOwner(owner);

        ((Stage) getWindow()).show();

        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

//        getNode().setPrefWidth(getNode().getWidth());
//        getNode().setPrefHeight(getNode().getHeight());
        stage.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        stage.setMaxWidth(root.maxWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        stage.setMaxHeight(root.maxHeight(DockUtil.widthOf(getNode())) + insetsHeight);

        bindWindowPosition(owner);

        windowBounds(stage, getNode());

        return stage;
    }

    protected PopupControl createPopupControl() {
        PopupControl popup = new PopupControl();
        setWindow(popup);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red;");
        root.applyCss();
        popup.getScene().setRoot(root);
        //window().set(createPopupControl());
        ((PopupControl) getWindow()).show(getNode().getScene().getWindow());

        //popup.setOnShowing(v -> {
        double borderWidth = 0;
        double borderHeight = 0;
        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        System.err.println("IPOPUP INSETS w= " + borderWidth);
        System.err.println("IPOPUP INSETS h= " + borderHeight);
        //popup.getScene().setFill(null);
        //root.prefWidthProperty().bind(getNode().prefWidthProperty().add(20));
        //root.prefHeightProperty().bind(getNode().prefHeightProperty().add(20));
        //popup.widthProperty()
        //});

        Insets insetsDelta = getNode().getInsets();
        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
//        getNode().setPrefWidth(b.getWidth());
//        getNode().setPrefHeight(b.getHeight());
        root.prefWidthProperty().bind(getNode().prefWidthProperty().add(borderWidth));
        root.prefHeightProperty().bind(getNode().prefHeightProperty().add(borderHeight));

        root.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
        root.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        //root.setMaxWidth(getNode().getPrefWidth());
        //root.setMaxHeight(getNode().getPrefHeight());

        getNode().setPrefWidth(getNode().getWidth());
        getNode().setPrefHeight(getNode().getHeight());

        bindWindowPosition(getNode().getScene().getWindow());

        windowBounds(getWindow(), getNode());

        //Bounds b = windowBounds(popup, getNode());
//        popup.setMinWidth(root.minWidth(DockUtil.heightOf(getNode())) + insetsWidth);
//        popup.setMinHeight(root.minHeight(DockUtil.widthOf(getNode())) + insetsHeight);
        return popup;
    }

    public void start() {
        if (KIND == Options.STAGE) {
            createStage();
        } else if (KIND == Options.POPUPCONTROL) {
            createPopupControl();
        }
        System.err.println("START INSETS = " + ((Region) getNode().getScene().getRoot()).getInsets());
        new MouseResizeHandler(getWindow(), getNode(), isApplyFtranslateXY());

    }

    protected static Bounds windowBounds(Window window, Region node) {

        Bounds b = node.localToScreen(node.getBoundsInLocal());

        //System.err.println("windowBounds node.getBoundsInLocal() = " + node.getBoundsInLocal());
        //System.err.println("windowBounds node.etLayoutBounds() = " + node.getLayoutBounds());        
        //System.err.println("windowBounds screen = " + b);        
        //Bounds b1 = node.localToParent(node.getBoundsInLocal());
        //System.err.println("windowBounds parent = " + b1);        
        double borderWidth = 0;
        double borderHeight = 0;
        double borderX = 0;
        double borderY = 0;

        Region root = (Region) window.getScene().getRoot();
        if (root.getInsets() != null) {
            borderX = root.getInsets().getLeft();
            borderY = root.getInsets().getTop();

            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }
        window.setX(b.getMinX() - borderX);
        window.setY(b.getMinY() - borderY);

        if (window instanceof Stage) {
            window.setWidth(b.getWidth() + borderWidth);
            window.setHeight(b.getHeight() + borderHeight);
        }
        //} else {

        //}
        return b;
    }

    public ObjectProperty<Window> window() {
        return window;
    }

    public <T> T getWindow() {
        return (T) window.get();
    }

    public <T> void setWindow(T window) {
        this.window.set((Window) window);
    }

    public boolean isApplyFtranslateXY() {
        return applyFtranslateXY;
    }

    public boolean isHidePopup() {
        return hidePopup;
    }

    public Region getNode() {
        return node;
    }

    public static class MouseResizeHandler implements EventHandler<MouseEvent> {

        private Cursor[] supportedCursors = new Cursor[]{
            Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
            Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
        };
        private boolean cursorSupported = false;
        private Window window;
        private WindowResizer windowResizer;
        private Region node;
        private double translateX;
        private double translateY;
        private boolean applyTranskateXY;

        private boolean managedValue;

        public MouseResizeHandler(Window window, Region node) {
            this(window, node, false);
        }

        public MouseResizeHandler(Window window, Region node, boolean applyTranslateXY) {
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
            managedValue = node.isManaged();
            translateX = node.getTranslateX();
            translateY = node.getTranslateY();

            windowResizer = new StageKindlResizerImpl(window, node);
            window.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
            window.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
            window.addEventFilter(MouseEvent.MOUSE_MOVED, this);
            window.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        }

        public Region getResizableNode() {
            return (Region) node;
        }

        public WindowResizer getResizer() {
            return windowResizer;
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
                Cursor c = StageKindlResizerImpl.cursorBy(ev, (Region) window.getScene().getRoot());

                if (!isCursorSupported(c)) {
                    window.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    window.getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                //Cursor c = StageKindlResizerImpl.cursorBy(ev, getResizableNode());
                Cursor c = StageKindlResizerImpl.cursorBy(ev, (Region) window.getScene().getRoot());
                if (node != null && !applyTranskateXY) {
                    translateX = node.getTranslateX();
                    translateY = node.getTranslateY();
                }
                System.err.println("IS MAN = " + node.isManaged());
                //node.setManaged(false);
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
//                System.err.println("4 FloatWindowView MouseHandler. windowResizer = " + popup.getResizer());                
//                System.err.println("4 FloatWindowView befor start MouseHandler. cursor = " + popup.getFloatingWindow().getScene().getCursor());                                

                    getResizer().start(ev, window, window.getScene().getCursor(), getSupportedCursors());
                } else {
                    Platform.runLater(() -> {
                        getResizer().resize(ev);
                    });

                }
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (!isApplyTranskateXY()) {
                    node.setTranslateX(translateX);
                    node.setTranslateY(translateY);
                }
                if (node != null) {
                    windowBounds(window, (Region) node);
                }
                //node.setManaged(managedValue);
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
//            System.err.println("isCursorSupported popup = " + popup);            

//            System.err.println("isCursorSupported popup.getSupportedCursors().len = " + popup.getSupportedCursors().length);
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

    public static class StageKindlResizerImpl implements WindowResizer {

        private final DoubleProperty mouseX = new SimpleDoubleProperty();
        private final DoubleProperty mouseY = new SimpleDoubleProperty();

        private Cursor cursor;
        //private Window window;
        private Region node;
        private Window window;
        private final Set<Cursor> cursorTypes = new HashSet<>();

        public StageKindlResizerImpl(Window window, Region node) {
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
        static int COUNT = 0;

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
            COUNT++;
//            if ( COUNT == 50)
            //((Region)node).getParent().autosize();
            Bounds oldBounds = node.getBoundsInParent();

            double oldX = oldBounds.getMinX();
            double oldY = oldBounds.getMinY();

            double oldWidth = node.getWidth();
            double oldHeight = node.getHeight();

            double newX = oldX + xDelta;
            //double Y = node.getBoundsInParent().getMinY() - node.getLayoutY();                    
            double newY = oldY + yDelta;
            //((Region) node).setTranslateX(newX + xDelta);
            //((Region) node).setPrefWidth(wDelta + ((Region) node).getPrefWidth());
            //System.err.println("NODE isManaged=" + node.isManaged());
            double newWidth = wDelta + oldWidth;
            double newHeight = hDelta + oldHeight;
            //System.err.println("BEFORE oldWidth = " + oldWidth);
            //System.err.println("BEFORE newWidth = " + node.getWidth());

            if (cursor == Cursor.W_RESIZE) {
                node.relocate(newX, newY);
                //node.getParent().layout();
                System.err.println("BEFORE x = " + (node.getBoundsInParent().getMinX() - newX));
                //Platform.runLater(() -> {
                    //if (Math.abs(node.getBoundsInParent().getMinX() - newX) >= 2) {
System.err.println("AFTER x = " + (node.getBoundsInParent().getMinX() - newX));                        
                        node.setPrefWidth(newWidth);
                        node.setPrefHeight(newHeight);
                        windowBounds(window, node);
                    //}

                //});

                if (false && Math.abs(node.getBoundsInParent().getMinX() - newX) >= 1) {
                    node.setPrefWidth(newWidth);
                    node.setPrefHeight(newHeight);
                    windowBounds(window, node);
                }
            } else if (cursor == Cursor.E_RESIZE) {
                node.setPrefWidth(newWidth);
                node.setPrefHeight(newHeight);
                windowBounds(window, (Region) node);
            }
//            System.err.println("AFTER oldWidth = " + oldWidth);
            //System.err.println("AFTER newWidth = " + node.getWidth());

            //node.relocate(newX, newY);
            mouseX.set(curX);
            mouseY.set(curY);
        }

        protected void relocate(final double newX, final double newY, final double newWidth, final double newHeight) {
            System.err.println("x = " + (node.getBoundsInParent().getMinX() - newX));
            if (Math.abs(node.getBoundsInParent().getMinX() - newX) >= 1) {
                node.setPrefWidth(newWidth);
                node.setPrefHeight(newHeight);
                windowBounds(window, node);
            }

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
            COUNT++;
//            if ( COUNT == 50)
            //((Region)node).getParent().autosize();
            Bounds oldBounds = node.getBoundsInParent();

            double oldX = oldBounds.getMinX();
            double oldY = oldBounds.getMinY();

            double oldWidth = node.getWidth();
            double oldHeight = node.getHeight();

            double newX = oldX + xDelta;
            //double Y = node.getBoundsInParent().getMinY() - node.getLayoutY();                    
            double newY = oldY + yDelta;
            //((Region) node).setTranslateX(newX + xDelta);
            //((Region) node).setPrefWidth(wDelta + ((Region) node).getPrefWidth());
            //System.err.println("NODE isManaged=" + node.isManaged());
            double newWidth = wDelta + oldWidth;
            double newHeight = hDelta + oldHeight;

            node.resizeRelocate(newX, newY, newWidth, newHeight);
            windowBounds(window, (Region) node);
            mouseX.set(curX);
            mouseY.set(curY);


            /*            if (wDelta + getWindow().getWidth() > getMinWidth() || (hDelta + getWindow().getHeight() > getMinHeight())) {
//                if ((((Region) node).getWidth() > ((Region) node).minWidth(-1) || xDelta <= 0)) {
                    

                    //double newX = node.getBoundsInParent().getMinX() - node.getLayoutX();
                    double newX = oldX + xDelta;
                    //double Y = node.getBoundsInParent().getMinY() - node.getLayoutY();                    
                    double newY = oldY + yDelta;
                    //((Region) node).setTranslateX(newX + xDelta);
                    //((Region) node).setPrefWidth(wDelta + ((Region) node).getPrefWidth());
                    //System.err.println("NODE isManaged=" + node.isManaged());
                    double newWidth = wDelta + oldWidth;
                    double newHeight = hDelta + oldHeight;
                    
                    node.resizeRelocate(newX, newY, newWidth , newHeight);
                    windowBounds(window, (Region) node);
                    mouseX.set(curX);
                    mouseY.set(curY);

                    if (cursor == Cursor.W_RESIZE) {
                        //node.relocate(newX, oldY);
                        
                        //node.resizeRelocate(newX, oldY, newWidth , oldHeight);
                        
                        //node.getParent().layout();
                        //Bounds newBounds = node.getBoundsInParent();
                        //                        if (newBounds.getWidth() != oldBounds.getWidth()) {
                        //    window.setWidth(newWidth + 10);
                        //}
                         
                        //System.err.println("oldX = " + oldX);
                        //System.err.println("newX = " + newBounds.getMinX());
                        
                        //if ( oldX != newBounds.getMinX() ) {
                        //if (newBounds.getMinX() != oldBounds.getMinX()) {
                        //windowBounds(window, (Region) node);
//                            window.setX(newX);
//                            window.setWidth(newWidth + 10);
                        //}

                    } else if (false) {
                        newX = oldX;
                        System.err.println("newWidth = " + newWidth);
                        node.resizeRelocate(newX, newY, newWidth, node.getHeight());
                        node.getParent().layout();
                        //node.setPrefWidth(newWidth);

                        Bounds newBounds = node.getBoundsInParent();
                        System.err.println("oldBounds = " + oldBounds);
                        System.err.println("newBounds = " + newBounds);

                        if (oldX != newWidth) {
                            window.setWidth(newWidth + 10);
                        }
                    }

                    mouseX.set(curX);
  //              }
                        
            }

            if (hDelta + getWindow().getHeight() > getMinHeight()) {
                if ((((Region) node).getHeight() > ((Region) node).minHeight(-1) || yDelta <= 0)) {
                    double nodeNewY = node.getBoundsInParent().getMinY() - node.getLayoutY();
                    ((Region) node).setTranslateY(nodeNewY + yDelta);
                    double h = ((Region) node).getHeight();
                    ((Region) node).setPrefHeight(hDelta + ((Region) node).getPrefHeight());
                    //((Region)node).getParent().layout();

                    double h1 = ((Region) node).getHeight();
                    mouseY.set(curY);
                }
            }
             */
            //windowBounds(window, node);
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

        protected double getMaxWidth() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMaxWidth();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMaxWidth();
            }
            return retval;
        }

        protected double getMaxHeight() {
            double retval = 0.0;
            if (window instanceof Stage) {
                retval = ((Stage) window).getMaxHeight();
            } else if (window instanceof PopupControl) {
                retval = ((PopupControl) window).getMaxHeight();
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
//            if (w instanceof Stage) {
            return cursorBy(x, ev.getY(), width, height, left, right, top, bottom);
            /*            } else {
                x -= left;
                y -= top;
            }
            return cursorBy(x, y, width, height, left, right, top, bottom);
             */
        }

        public static Cursor cursorBy(MouseEvent ev, Region r) {
            double x, y, w, h;

            Insets ins = r.getInsets();
            //test(ev, r);
            //System.err.println("INSETS " + ins);
            if (ins == Insets.EMPTY) {
                System.err.println("EMPTY INSETS " + ins);
                return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);
            }
            //if (ev.getSource() instanceof Stage) {
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
            //} else {

            //}
            //return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
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

}
