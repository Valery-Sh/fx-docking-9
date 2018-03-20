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
package org.vns.javafx.dock.api.dragging.view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public abstract class WindowNodeFraming extends AbstractNodeFraming implements EventHandler<MouseEvent> {


    private ChangeListener<Bounds> boundsInParentListener;

    private Window window;

    private Resizer resizer;

    private StackPane root;


    private Window nodeWindow;
    private Insets nodeInsets;
    protected double borderWidth = 0;
    double borderHeight = 0;
    double insetsWidth = 0;
    double insetsHeight = 0;
    double insetsLeft = 0;
    double insetsTop = 0;

    double insetsRootTop = 0;
    double insetsRootLeft = 0;

    private final DoubleProperty workWidth = new SimpleDoubleProperty(-1);
    private final DoubleProperty workHeight = new SimpleDoubleProperty(-1);
    
    private final Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private boolean cursorSupported = false;

    private Point2D startMousePos;
    
//    private NodeFraming redirectSource;
    
    private ShapeFraming shapeFraming;
    
    
    
    private double translateX;
    private double translateY;
    private Cursor saveCursor;

    protected WindowNodeFraming() {
        super();
    }

    protected void setWindow(Window window) {
        this.window = window;
//        init();
    }

    private void init() {
        workHeight.set(-1);
        workWidth.set(-1);

        window.setOnShown(e -> {
            DockRegistry.register(window, true); // true means exclude when searfor target window
        });
        window.setOnHidden(e -> {
            DockRegistry.unregister(window);
        });

        if (getNode() instanceof Region) {
            init((Region) getNode());
        }
        initScene();
    }

    protected abstract void setWindowSize(Bounds bounds, double borderWidth, double borderHeight);

    protected abstract void initScene();


    protected abstract void createWindow();

    protected void doShow(Window owner) {
    }

    public Node getRoot() {
        return root;
    }

    public Window getWindow() {
        return window;
    }

    private void init(Region region) {

        root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        //Border b = new NodeResizerBorder().getBorder();
        //root.setBorder(b);
        root.setStyle("-fx-border-width: 6; -fx-border-color: red; -fx-opacity: 0.3");
        root.applyCss();

        borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
        borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();

        nodeInsets = ((Region) region).getInsets();
        if (nodeInsets != null) {
            insetsWidth = nodeInsets.getLeft() + nodeInsets.getRight();
            insetsHeight = nodeInsets.getTop() + nodeInsets.getBottom();
            insetsTop = nodeInsets.getTop();
            insetsLeft = nodeInsets.getLeft();
        }
        insetsRootTop = root.getInsets() != null ? root.getInsets().getTop() : 0;
        insetsRootLeft = root.getInsets() != null ? root.getInsets().getLeft() : 0;

        Bounds screenBounds = getNode().localToScreen(getNode().getLayoutBounds());

        window.setX(screenBounds.getMinX() - insetsRootLeft);
        window.setY(screenBounds.getMinY() - insetsRootTop);

        window.setWidth(getNode().getLayoutBounds().getWidth() + borderWidth);
        window.setHeight(getNode().getLayoutBounds().getHeight() + borderHeight);

        ((Region) getNode()).setPrefWidth(((Region) getNode()).getWidth());
        ((Region) getNode()).setPrefHeight(((Region) getNode()).getHeight());
        
        setWorkWidth(getNode().getLayoutBounds().getWidth());
        setWorkHeight(getNode().getLayoutBounds().getHeight());

        
        boundsInParentListener = (o, ov, nv) -> {

            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();

            Insets nodeInsets = ((Region) region).getInsets();
            if (nodeInsets != null) {
                insetsWidth = nodeInsets.getLeft() + nodeInsets.getRight();
                insetsHeight = nodeInsets.getTop() + nodeInsets.getBottom();
                insetsTop = nodeInsets.getTop();
                insetsLeft = nodeInsets.getLeft();
            }
            insetsRootTop = root.getInsets() != null ? root.getInsets().getTop() : 0;
            insetsRootLeft = root.getInsets() != null ? root.getInsets().getLeft() : 0;

            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());

            window.setX(sb.getMinX() - insetsRootLeft);
            window.setY(sb.getMinY() - insetsRootTop);
            
            setWorkWidth(sb.getWidth());
            setWorkHeight(sb.getHeight());            
            root.setPrefWidth(sb.getWidth() + borderWidth);
            root.setPrefHeight(sb.getHeight() + borderHeight);

            setWindowSize(sb, borderWidth, borderHeight);
        };

        getNode().boundsInParentProperty().addListener(boundsInParentListener);

        //
        //  show to widthProperty and heightProperty
        //
        //screenBounds = getNode().localToScreen(getNode().getLayoutBounds());

        setWorkWidth(getNode().getLayoutBounds().getWidth());
        setWorkHeight(getNode().getLayoutBounds().getHeight());

        nodeWindow = region.getScene().getWindow();
        setWindowSize(getNode().getLayoutBounds(), borderWidth, borderHeight);

        bindWindowPosition(nodeWindow);
    }


    protected void bindWindowPosition(Window owner) {
        owner.xProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });
        owner.yProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            windowBounds(getWindow(), getNode());
        });

    }

    protected static Bounds windowBounds(Window window, Node node) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        double borderWidth = 0;
        double borderHeight = 0;
        double borderX = 0;
        double borderY = 0;
        if (window == null) {
            return b;
        }
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
        return b;
    }

    public DoubleProperty workWidthProperty() {
        return workWidth;
    }

    public double getWorkWidth() {
        return workWidth.get();
    }

    public void setWorkWidth(double width) {
        workWidth.set(width);
    }

    public DoubleProperty workHeightProperty() {
        return workHeight;
    }

    public double getWorkHeight() {
        return workHeight.get();
    }

    public void setWorkHeight(double height) {
        workHeight.set(height);
    }

    @Override
    protected void initializeOnShow(Node node) {
        if (window != null) {
            removeWindowListeners();
        }

        createWindow();
        if (window == null) {
            return;
        }

        init();
        show();

    }

    protected void show() {
        Window newOwner = getNode().getScene().getWindow();
        if (nodeWindow == newOwner) {
            window.hide();
        }

        resizer = new NodeResizer(window, (Region) getNode());

        window.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        window.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        window.addEventFilter(MouseEvent.MOUSE_MOVED, this);
        window.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        doShow(newOwner);
    }

    protected void removeWindowListeners() {
        window.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        window.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        window.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        window.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
    }

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
            Cursor c = NodeResizer.cursorBy(new Point2D(ev.getX(), ev.getY()), (Region) window.getScene().getRoot());

            if (!isCursorSupported(c)) {
                window.getScene().setCursor(Cursor.DEFAULT);
            } else {
                window.getScene().setCursor(c);
            }
            if (!c.equals(Cursor.DEFAULT)) {
                ev.consume();
            }

        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (!window.getScene().getRoot().contains(ev.getX(), ev.getY())) {
                removeWindowListeners();
                hide();

                return;
            }
            saveCursor = NodeResizer.cursorBy(new Point2D(ev.getX(), ev.getY()), root);
            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                window.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            resizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (!cursorSupported) {
                return;
            }

            if (!resizer.isStarted()) {
                resizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
            } else {
                Platform.runLater(() -> {
                    resizer.resize(ev.getScreenX(), ev.getScreenY());
                });
            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (false) {
                double tX = getNode().getTranslateX();
                double tY = getNode().getTranslateY();

                getNode().setTranslateX(translateX);
                getNode().setTranslateY(translateY);

                double oldLX = getNode().getLayoutX();
                double oldLY = getNode().getLayoutY();
                //
                // Do layoutXY. the node can be relocated (for instans it resides in a Pane)
                // it will be relocated
                //
                getNode().setLayoutX(getNode().getLayoutX() + tX);
                getNode().setLayoutY(getNode().getLayoutY() + tY);

                ((Region) getNode()).setPrefWidth(((Region) getNode()).getWidth());
                //commitResize();
            } else {
                //commitResize();
            }

        }
    }
    

    private EventHandler<MouseEvent> redirectMouseReleasedHandler = ev -> {
        redirectMouseReleased(ev);
    };

/*    public void redirectMouseEvents(MouseEvent ev, Point2D startMousePos, NodeFraming redirectSource) {
        this.startMousePos = startMousePos;
        this.redirectSource = redirectSource;

        removeWindowListeners();

        saveCursor = getNode().getScene().getCursor();
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        redirectMousePressed(ev);
       //windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        resizer.start(ev, this, saveCursor, getSupportedCursors());
    }
*/    
    public void redirectMouseEvents(MouseEvent ev, Point2D startMousePos, ShapeFraming redirectSource) {
        this.startMousePos = startMousePos;
        this.shapeFraming = redirectSource;

        removeWindowListeners();

        saveCursor = getNode().getScene().getCursor();
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        redirectMousePressed(ev);
       //windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        resizer.start(ev, this, saveCursor, getSupportedCursors());
    }

    
    protected void redirectMousePressed(MouseEvent ev) {
        cursorSupported = isCursorSupported(saveCursor);
        if (!cursorSupported) {
            window.getScene().setCursor(Cursor.DEFAULT);
            return;
        }
    }

    protected void redirectMouseReleased(MouseEvent ev) {
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, redirectMouseReleasedHandler);
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        hide();

/*        if (redirectSource != null) {
            Platform.runLater(() -> {
                redirectSource.show(getNode());
            });
        }
*/        
        if ( shapeFraming != null ) {
            Platform.runLater(() -> {
                shapeFraming.setVisible(true);
            });
            
        }
    }

    @Override
    protected void finalizeOnHide(Node node) {
        if (root != null) {
            root.prefWidthProperty().unbind();
            root.prefHeightProperty().unbind();
            root.setPrefWidth(-1);
            root.setPrefHeight(-1);
            root = null;
        }
        if (node != null) {
            node.boundsInParentProperty().removeListener(boundsInParentListener);
        }
        if (window != null) {
            removeWindowListeners();
            window.hide();
            window = null;
        }

    }

    public Resizer getResizer() {
        return resizer;
    }

    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }

    public boolean isCursorSupported(Cursor cursor) {
        if (cursor == null || cursor == Cursor.DEFAULT) {
            return false;
        }
        boolean retval = false;
        for (Cursor c : getSupportedCursors()) {
            if (c == cursor) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    /*    public static class LBListener implements ChangeListener<Bounds> {

        private WindowNodeFraming wnf;

        public LBListener(WindowNodeFraming wnf) {
            this.wnf = wnf;
        }

        @Override
        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
            if (newValue == null) {
                return;
            }

            Bounds sb = wnf.getNode().localToScreen(wnf.getNode().getBoundsInLocal());
            if (sb == null) {
                return;
            }
            wnf.setWorkWidth(newValue.getWidth());
            wnf.setWorkHeight(newValue.getHeight());
            wnf.setWindowSize(newValue, wnf.borderWidth, wnf.borderHeight);

        }

    }
     */
}
