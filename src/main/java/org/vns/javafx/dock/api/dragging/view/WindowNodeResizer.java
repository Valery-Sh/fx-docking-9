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
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
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
public class WindowNodeResizer implements EventHandler<MouseEvent> {

    private Window window;

    private WindowResizer windowResizer;

    private StackPane root;

    private final ObjectProperty<Node> node = new SimpleObjectProperty<>();

    private Window nodeWindow;
    double borderWidth = 0;
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

    protected WindowNodeResizer() {
        super();
        nodeProperty().addListener((v, ov, nv) -> {
            if (ov != null) {
                ov.layoutBoundsProperty().removeListener(this::layoutBoundsChanged);
            }
        });
    }

    protected void setWindow(Window window) {
        this.window = window;
//        init();
    }

    private void init() {
        workHeight.set(-1);
        workHeight.set(-1);
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

    public StackPane getRoot() {
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

        getNode().layoutBoundsProperty().addListener(this::layoutBoundsChanged);

        Bounds screenBounds = getNode().localToScreen(getNode().getLayoutBounds());
        window.setX(screenBounds.getMinX() - insetsLeft);
        window.setY(screenBounds.getMinY() - insetsTop);
        window.setWidth(screenBounds.getWidth() + insetsWidth);
        window.setHeight(screenBounds.getHeight() + insetsHeight);
        //root.setPrefWidth(screenBounds.getWidth() + insetsWidth);
        //root.setPrefHeight(screenBounds.getHeight() + insetsHeight);
        
        getNode().layoutYProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setY(sb.getMinY() - insetsTop);

        });

        getNode().layoutXProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScreen(getNode().getLayoutBounds());
            window.setX(sb.getMinX() - insetsLeft);
        });
        //
        //  bind to widthProperty and heightProperty
        //
        root.prefWidthProperty().bind(workWidth.add(borderWidth));
        root.prefHeightProperty().bind(workHeight.add(borderHeight));

        //root.setMinWidth(root.minWidth(DockUtil.heightOf(region)) + insetsWidth);
        //root.setMinHeight(root.minHeight(DockUtil.widthOf(region)) + insetsHeight);
        screenBounds = getNode().localToScreen(getNode().getLayoutBounds());
        
        setWorkWidth(screenBounds.getWidth());
        setWorkHeight(screenBounds.getHeight());
        
        nodeWindow = region.getScene().getWindow();
        setWindowSize(getNode().getLayoutBounds(), borderWidth, borderHeight);
        
        bindWindowPosition(nodeWindow);
    }

/*    protected void boundsChanged(ObservableValue<? extends Bounds> v, Bounds ov, Bounds nv) {
        if (nv == null) {
            return;
        }
        System.err.println("getLayoutY = " + getNode().getLayoutY());
        //Bounds sb = getNode().localToScreen(getNode().parentToLocal(nv));
        Bounds sb = getNode().localToScreen(getNode().getBoundsInLocal());
        if (sb == null) {
            return;
        }
  
        System.err.println("WindowNodeResizer parentListener: node = " + getNode());
        System.err.println("  --- screen  X = " + sb.getMinX() + "; Y = " + sb.getMinY());
        System.err.println("  --- local   X = " + nv.getMinX() + "; Y = " + nv.getMinY());

        System.err.println("  --- Width = " + sb.getWidth() + "; Height =  + sb.getHeight()");
        //if ( (nv.getMinX()%1) != 0 || (nv.getMinY()%1) != 0 || (nv.getWidth()%1) != 0 || (nv.getHeight()%1) != 0) {
        //return;
        //}
        System.err.println("  --- WindowNodeResizer: boundsInParentPropertybounds=" + nv);
 
        window.setX(sb.getMinX() - insetsLeft);
        window.setY(sb.getMinY() - insetsTop);
 
    }
*/
    protected void layoutBoundsChanged(ObservableValue<? extends Bounds> v, Bounds ov, Bounds nv) {
        if (nv == null) {
            return;
        }

        Bounds sb = getNode().localToScreen(getNode().getBoundsInLocal());
        if (sb == null) {
            return;
        }
        System.err.println("******* ");

        System.err.println("WindowNodeResizer layoutListener: node = " + getNode());
        System.err.println("  --- screen  X = " + sb.getMinX() + "; Y = " + sb.getMinY());
        System.err.println("  --- local   X = " + nv.getMinX() + "; Y = " + nv.getMinY());

        System.err.println("  --- Width = " + sb.getWidth() + "; Height =  + sb.getHeight()");
        System.err.println("  --- WindowNodeResizer: layoutBounds=" + sb);

        /*            System.err.println("  --- floorX = " + Math.floor(nv.getMinX()));
            System.err.println("  --- floorY = " + Math.floor(nv.getMinY()));            
            System.err.println("  --- floorWidth = " + Math.floor(nv.getWidth()));                        
            System.err.println("  --- floorHeight = " + Math.floor(nv.getHeight()));                                    
         */
        System.err.println("-----------------------------------");

        //window.setX(sb.getMinX() - insetsLeft);
        //window.setY(sb.getMinY() - insetsTop );
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
//            System.err.println("b.getWidth() = " + node.getWidth() + "; borderWidth = " + borderWidth);
//            System.err.println("b.getHeight() = " + node.getHeight() + "; borderHeight = " + borderHeight);

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

    public void show(Node node) {
        if (window != null) {
            removeWindowListeners();
        }

        createWindow();

        this.node.set(node);
        init();
        this.show();
    }

    protected void show() {
        Window newOwner = getNode().getScene().getWindow();
        if (nodeWindow == newOwner) {
            window.hide();
        }

        //if (windowResizer == null) {
        windowResizer = new NodeResizeExecutor2(window, (Region) getNode());
        //}
/*        getNode().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        getNode().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        getNode().addEventFilter(MouseEvent.MOUSE_MOVED, this);
        getNode().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
         */
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

        /*        getNode().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        getNode().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        getNode().removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        getNode().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
         */
    }

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
//             System.err.println("MOUSE MOVED 0");

            Cursor c = NodeResizeExecutor2.cursorBy(ev, (Region) window.getScene().getRoot());

            if (!isCursorSupported(c)) {
                window.getScene().setCursor(Cursor.DEFAULT);
            } else {
                window.getScene().setCursor(c);
            }
            if (!c.equals(Cursor.DEFAULT)) {
                ev.consume();
            }

        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
//             System.err.println("MOUSE PRESSED 0");
            if (!window.getScene().getRoot().contains(ev.getX(), ev.getY())) {
//                System.err.println("MOUSE PRESSED");
                removeWindowListeners();
                hide();

                return;
            }
            saveCursor = NodeResizeExecutor2.cursorBy(ev, root);
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
//             System.err.println("MOUSE DRAGGED 0");

            if (!cursorSupported) {
                return;
            }
            if (!windowResizer.isStarted()) {
                windowResizer.start(ev, this, window.getScene().getCursor(), getSupportedCursors());
            } else {
                Platform.runLater(() -> {
                    windowResizer.resize(ev);
                });
            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            System.err.println("MOUSE RELEASED 0");

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

    public void hide() {
        if (window != null) {
            window.hide();
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

    public WindowResizer getResizer() {
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

}
