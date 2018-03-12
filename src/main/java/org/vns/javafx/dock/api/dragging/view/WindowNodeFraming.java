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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public class WindowNodeFraming implements NodeFraming, EventHandler<MouseEvent> {

    //private LBListener layoutBoundsListener;
    private ChangeListener<Bounds> layoutBoundsListener;
    private ChangeListener layoutXListener;    
    private ChangeListener layoutYListener;        
    private Window window;

    private WindowResizeExecutor windowResizer;

    private StackPane root;

    private final ObjectProperty<Node> node = new SimpleObjectProperty<>();

    private Window nodeWindow;
    protected double borderWidth = 0;
    double borderHeight = 0;
    double insetsWidth = 0;
    double insetsHeight = 0;
    double insetsTop = 0;
    double insetsLeft = 0;

    private final DoubleProperty workWidth = new SimpleDoubleProperty(-1);
    private final DoubleProperty workHeight = new SimpleDoubleProperty(-1);
    private final DoubleProperty workX = new SimpleDoubleProperty(-1);
    private final DoubleProperty workY = new SimpleDoubleProperty(-1);

    private final Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private boolean cursorSupported = false;

    private double translateX;
    private double translateY;
    private Cursor saveCursor;
    private boolean applyTranslateXY;

    protected WindowNodeFraming() {
        super();
        nodeProperty().addListener((v, ov, nv) -> {
            if (ov != null) {
                System.err.println("nodeProperty() ov = " + ov + " ; nv = " + nv);
                ov.layoutBoundsProperty().removeListener(this::layoutBoundsChanged);

                if (layoutBoundsListener != null) {
                    ov.layoutBoundsProperty().removeListener(layoutBoundsListener);
                }

            }
        });
    }

    protected void setWindow(Window window) {
        this.window = window;
//        init();
    }

    private void init() {
        workHeight.set(-1);
        workWidth.set(-1);
        workX.set(-1);
        workY.set(-1);

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

    protected void setWindowSize(Bounds bounds, double borderWidth, double borderHeight) {

    }

    protected void initScene() {

    }

    protected void createWindow() {
    }

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

        Border b = new NodeResizerBorder().getBorder();
        root.setBorder(b);
        root.applyCss();

        if (root.getInsets() != null) {
            borderWidth = root.getInsets().getLeft() + root.getInsets().getRight();
            borderHeight = root.getInsets().getTop() + root.getInsets().getBottom();
        }

        Insets insetsDelta = ((Region) region).getInsets();
        insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
        insetsTop = root.getInsets() != null ? root.getInsets().getTop() : 0;
        insetsLeft = root.getInsets() != null ? root.getInsets().getLeft() : 0;

        //getNode().layoutBoundsProperty().addListener(this::layoutBoundsChanged);
        //this.layoutBoundsListener = new LBListener(this);
        
        layoutBoundsListener = this::layoutBoundsChanged;
        getNode().layoutBoundsProperty().addListener(layoutBoundsListener);

        Bounds screenBounds = getNode().localToScreen(getNode().getLayoutBounds());
        window.setX(screenBounds.getMinX() - insetsLeft);
        window.setY(screenBounds.getMinY() - insetsTop);
        window.setWidth(screenBounds.getWidth() + insetsWidth);
        window.setHeight(screenBounds.getHeight() + insetsHeight);
        
        layoutXListener = (o, ov, nv) -> {
            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setX(sb.getMinX() - insetsLeft);
        };
        layoutYListener = (o, ov, nv) -> {
          Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setY(sb.getMinY() - insetsTop);
        };        
        getNode().layoutXProperty().addListener(layoutXListener);
        getNode().layoutYProperty().addListener(layoutYListener);
        
/*        getNode().layoutYProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setY(sb.getMinY() - insetsTop);
        });

        getNode().layoutXProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setX(sb.getMinX() - insetsLeft);
        });
*/
        //
        //  bind to widthProperty and heightProperty
        //
        root.prefWidthProperty().bind(workWidth.add(borderWidth));
        root.prefHeightProperty().bind(workHeight.add(borderHeight));

        screenBounds = getNode().localToScreen(getNode().getLayoutBounds());

        setWorkWidth(screenBounds.getWidth());
        setWorkHeight(screenBounds.getHeight());

        nodeWindow = region.getScene().getWindow();
        setWindowSize(getNode().getLayoutBounds(), borderWidth, borderHeight);

        bindWindowPosition(nodeWindow);
    }

    protected void layoutBoundsChanged(ObservableValue<? extends Bounds> v, Bounds ov, Bounds nv) {
        if (nv == null) {
            return;
        }

        Bounds sb = getNode().localToScreen(getNode().getBoundsInLocal());
        if (sb == null) {
            return;
        }
        setWorkWidth(nv.getWidth());
        setWorkHeight(nv.getHeight());
        setWindowSize(nv, borderWidth, borderHeight);
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

    public DoubleProperty workXProperty() {
        return workX;
    }

    public double getWorkX() {
        return workX.get();
    }

    public void setWorkX(double x) {
        workX.set(x);
    }
///

    public DoubleProperty workYProperty() {
        return workY;
    }

    public double getWorkY() {
        return workY.get();
    }

    public void setWorkY(double y) {
        workY.set(y);
    }

    public ObjectProperty<Node> nodeProperty() {
        return node;
    }

    public Node getNode() {
        return node.get();
    }

    @Override
    public void show(Node node) {
        if ( isShowing() || node == null ) {
            return;
        }
        if (window != null) {
            removeWindowListeners();
        }

        createWindow();
        if ( window == null ) {
            return;
        }
        this.node.set(node);
        init();
        this.show();
    }

    protected void show() {
        Window newOwner = getNode().getScene().getWindow();
        if (nodeWindow == newOwner) {
            window.hide();
        }

        windowResizer = new NodeResizeExecutor(window, (Region) getNode());

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

//            Cursor c = NodeResizeExecutor.cursorBy(ev, (Region) window.getScene().getRoot());
            Cursor c = NodeResizeExecutor.cursorBy(new Point2D(ev.getX(), ev.getY()), (Region) window.getScene().getRoot());

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
            saveCursor = NodeResizeExecutor.cursorBy(new Point2D(ev.getX(), ev.getY()), root);
            //saveCursor = NodeResizeExecutor.cursorBy(new Point2D(ev, root);
            if (!applyTranslateXY) {
                translateX = getNode().getTranslateX();
                translateY = getNode().getTranslateY();
            }

            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                window.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            System.err.println("MOUSE DRAGGED 0");

            if (!cursorSupported) {
                return;
            }
            if (!windowResizer.isStarted()) {
                System.err.println("MOUSE DRAGGED 1");
                windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
            } else {
                Platform.runLater(() -> {
                    System.err.println("MOUSE DRAGGED 2");
                    windowResizer.resize(ev.getScreenX(), ev.getScreenY());
                });
            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (isApplyTranslateXY()) {
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
    private Point2D startMousePos;
    private NodeFraming redirectSource;

    public void redirectMouseEvents(MouseEvent ev, Point2D startMousePos, NodeFraming redirectSource) {
        this.startMousePos = startMousePos;
        this.redirectSource = redirectSource;

        removeWindowListeners();
        System.err.println("redirectMouseEvents: cursor=" + getNode().getScene().getCursor());
        saveCursor = getNode().getScene().getCursor();
        System.err.println("redirectMouseEvents: window.getScene().getCursor()=" + window.getScene().getCursor());
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, this::redirectMouseReleased);
        getNode().getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        redirectMousePressed(ev);
        windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
    }

    protected void redirectMousePressed(MouseEvent ev) {
        if (!applyTranslateXY) {
            translateX = getNode().getTranslateX();
            translateY = getNode().getTranslateY();
        }
        System.err.println("saveCursor = " + saveCursor);
        cursorSupported = isCursorSupported(saveCursor);
        System.err.println("CURSOR SUPPORTED " + cursorSupported);
        if (!cursorSupported) {
            window.getScene().setCursor(Cursor.DEFAULT);
            return;
        }
    }

    protected void redirectMouseReleased(MouseEvent ev) {
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, this::redirectMouseReleased);
        getNode().getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        hide();
        if (redirectSource != null) {
            Platform.runLater(() -> {
                redirectSource.show(getNode());
            });
        }
    }

    @Override
    public void hide() {
        if ( ! isShowing() ) {
            return;
        }
        System.err.println("hide() window = " + window);
        System.err.println("   --- hide() root.id = " + root);
        
        if (root != null) {
            root.prefWidthProperty().unbind();
            root.prefHeightProperty().unbind();
            root.setPrefWidth(-1);
            root.setPrefHeight(-1);
            root = null;
        }
        if (getNode() != null) {
            getNode().layoutBoundsProperty().removeListener(this::layoutBoundsChanged);
            getNode().layoutBoundsProperty().removeListener(layoutBoundsListener);
            getNode().layoutXProperty().removeListener(layoutXListener);
            getNode().layoutYProperty().removeListener(layoutYListener);
            node.set(null);
        }
        if (window != null) {
            removeWindowListeners();
            window.hide();
            window = null;
        }
    }

    public boolean isShowing() {
        if (window != null) {
            return window.isShowing();
        }
        return false;
    }

    public boolean isApplyTranslateXY() {
        return applyTranslateXY;
    }

    public void setApplyFtranslateXY(boolean useTranslateXY) {
        this.applyTranslateXY = useTranslateXY;
    }

    public WindowResizeExecutor getResizer() {
        return windowResizer;
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

    public static class LBListener implements ChangeListener<Bounds> {
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
}
