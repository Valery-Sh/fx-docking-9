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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public class ShapeNodeFraming extends AbstractNodeFraming implements EventHandler<MouseEvent> {

    private ChangeListener<Bounds> boundsInParentListener;
    //private ChangeListener layoutXListener;
    //private ChangeListener layoutYListener;

    private Rectangle indicator;
    private IndicatorShape indicatorShape;

    //private ShapeNodeResizeExecutor resizeExecutor;
//    private Window window;

    private Node root;

    private Window nodeWindow;
    double borderWidth = 0;
    double borderHeight = 0;
    double insetsWidth = 0;
    double insetsHeight = 0;
    double insetsTop = 0;
    double insetsLeft = 0;

    private final Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private boolean cursorSupported = false;
    private Cursor saveCursor;

    protected ShapeNodeFraming() {
        super();
    }

    protected void initWindow() {
    }

    private void init() {

        initWindow();

        if (getNode() instanceof Region) {
            init((Region) getNode());
        }
        initScene();
    }

    protected void setWindowSize(Bounds bounds, double borderWidth, double borderHeight) {

    }

    protected void initScene() {

    }

    protected void doShow(Window owner) {
    }

    public Node getRoot() {
        return root;
    }

    protected void bindWindowPosition(Window owner) {
    }

    private void init(Region region) {
        if (indicator != null) {
            indicator.widthProperty().unbind();
            indicator.heightProperty().unbind();
            
            indicatorShape.unbind();

            if (((Pane) getNode().getScene().getRoot()).getChildren().contains(indicator)) {
//                ((Pane) getNode().getScene().getRoot()).getChildren().remove(indicator);
            }
        } else {
            indicator = new Rectangle(50, 20);
            indicatorShape = new IndicatorShape(this, Rectangle.class);
        }

        indicator.setFill(Color.TRANSPARENT);
        indicator.setStrokeType(StrokeType.OUTSIDE);
        indicator.setStroke(Color.rgb(255, 148, 40));
        indicator.setStrokeWidth(2);
        indicator.setX(20);
        indicator.setY(50);
        indicator.setManaged(false);
        indicator.setMouseTransparent(true);
        indicator.toFront();

        indicatorShape.initShape();

        if (!((Pane) getNode().getScene().getRoot()).getChildren().contains(indicator)) {
            ((Pane) getNode().getScene().getRoot()).getChildren().add(indicator);
            indicatorShape.addToPane((Pane) getNode().getScene().getRoot());

        }

        Insets insetsDelta = ((Region) region).getInsets();
    
    
        insetsWidth = 0;
        insetsHeight = 0;
        insetsTop = 0;
        insetsLeft = 0;
        
/*        if ( insetsDelta != null ) {
            insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
            insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
            insetsRootTop = insetsDelta.getTop();
            insetsRootLeft = insetsDelta.getLeft();
        }
*/        
        indicatorShape.setPosition();

        indicatorShape.bind();

        root = getNode().getScene().getRoot();
        Bounds pb = getNode().getBoundsInParent();
        
        Bounds sceneBounds = getNode().localToScene(getNode().getLayoutBounds());
        getNode().getScene().getRoot().layoutXProperty();
        System.err.println("sceneBounds = " + sceneBounds);

        indicator.setX(sceneBounds.getMinX() - insetsLeft);
        indicator.setY(sceneBounds.getMinY() - insetsTop);
        indicator.setWidth(sceneBounds.getWidth() + insetsWidth);
        indicator.setHeight(sceneBounds.getHeight() + insetsHeight);


        boundsInParentListener =  (o, ov, nv) -> {
            Bounds sb = getNode().localToScene(getNode().getLayoutBounds());

            indicator.setY(sb.getMinY() - insetsTop);  
            indicator.setX(sb.getMinX() - insetsLeft);
            indicator.setWidth(nv.getWidth() + insetsWidth);
            indicator.setHeight(nv.getHeight() + insetsHeight);
            //System.err.println("boundsInParentListener: parentBounds " + nv);
        };
        
        getNode().boundsInParentProperty().addListener(boundsInParentListener);

        nodeWindow = region.getScene().getWindow();

        setWindowSize(getNode().getLayoutBounds(), borderWidth, borderHeight);
        bindWindowPosition(nodeWindow);

    }

    public Rectangle getIndicator() {
        return indicator;
    }

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    @Override
    protected void initializeOnShow(Node node) {
        if (indicator != null) {
            removeWindowListeners();
        }
        if (indicatorShape != null) {
            indicatorShape.removeShapeListeners();
        }
        init();
        show();

    }

    protected void show() {

//        indicator.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
//        indicator.addEventFilter(MouseEvent.MOUSE_MOVED, this);
//        indicator.addEventFilter(MouseEvent.DRAG_DETECTED, this);
        indicator.setVisible(true);

        indicatorShape.show();
    }

    protected void removeWindowListeners() {
        indicator.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        indicator.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
    }

    Point2D startMousePos;

    @Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {

            Point2D pt = indicator.screenToLocal(ev.getScreenX(), ev.getScreenY());
            Bounds ib = indicator.getBoundsInLocal();
            Cursor c = ShapeNodeResizeExecutor.cursorBy(ev.getX() - ib.getMinX(), ev.getY() - ib.getMinY(), indicator);
            if (!isCursorSupported(c)) {
                indicator.getScene().setCursor(Cursor.DEFAULT);
            } else {
                indicator.getScene().setCursor(c);
            }
            if (!c.equals(Cursor.DEFAULT)) {
                ev.consume();
            }

        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {

            if (!indicator.getScene().getRoot().contains(ev.getX(), ev.getY())) {
                //removeWindowListeners();
                //hide();
//                System.err.println("indicator.getScene().getRoot().contains(ev.getX(), ev.getY()) = " + indicator.getScene().getRoot().contains(ev.getX(), ev.getY()));
                //return;
            }
            Bounds ib = indicator.getBoundsInLocal();
            saveCursor = ShapeNodeResizeExecutor.cursorBy(ev.getX() - ib.getMinX(), ev.getY() - ib.getMinY(), indicator);

            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                indicator.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            startMousePos = new Point2D(ev.getScreenX(), ev.getScreenY());
            //resizeExecutor.start(ev, this, indicator.getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);

            hide();
            wnf.show(getNode());
            wnf.redirectMouseEvents(ev, startMousePos, this);
        }

        /*  else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (!cursorSupported) {
                return;
            }
            if (!resizeExecutor.isStarted()) {
                resizeExecutor.start(ev, this, indicator.getScene().getCursor(), getSupportedCursors());
            } else {
                //Platform.runLater(() -> {
//                    System.err.println("MOUSE DRAGGED RESIZE");
                    resizeExecutor.resize(ev);
                //});
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
         */
    }

    @Override
    public void finalizeOnHide(Node node) {
        if (indicator != null) {
            indicator.setVisible(false);
            node.boundsInParentProperty().removeListener(boundsInParentListener);            
            indicatorShape.setVisible(false);
            
        }
    }

    @Override
    public boolean isShowing() {
        return super.isShowing() && indicator != null && indicator.isDisable();
    }

    /*    public ShapeNodeResizeExecutor getResizeExecutor() {
        return resizeExecutor;
    }
     */
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

    public static ShapeNodeFraming getInstance() {
        return ShapeNodeFraming.SingletonInstance.instance;

    }

    private static class SingletonInstance {

        private static final ShapeNodeFraming instance = new ShapeNodeFraming();
    }

    public static class IndicatorShape implements EventHandler<MouseEvent> {

        private final ShapeNodeFraming framing;

        private final Class<?> shapeClass;
        private double shapeWidth = 2;
        private double shapeHeight = 2;
        private double strokeWidth = 1;
        
        private Shape nShape;    //north shape
        private Shape neShape;   //north-east shape
        private Shape eShape;    //east shape
        private Shape seShape;   //south-east shape
        private Shape sShape;    //south shape

        private Shape swShape;   // south-west shape
        private Shape wShape;    // west shape
        private Shape nwShape;   // north-west shape

        public IndicatorShape(ShapeNodeFraming framing, Class<?> shapeClass) {
            this.framing = framing;
            this.shapeClass = shapeClass;
            init();
        }

        private void init() {
            createShapes();
        }

        public void show() {
            show(nShape);
            show(neShape);
            show(eShape);
            show(seShape);
            show(sShape);
            show(swShape);
            show(wShape);
            show(nwShape);
        }

        protected void show(Shape shape) {
            shape.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
            shape.addEventFilter(MouseEvent.MOUSE_MOVED, this);
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);

            shape.addEventFilter(MouseEvent.DRAG_DETECTED, this);
            shape.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
            shape.setVisible(true);
        }

        protected void removeMouseExitedListener(Shape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        protected void addMouseExitedListener(Shape shape) {
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        protected void createShapes() {
            try {
                nShape = (Shape) shapeClass.newInstance(); //north shape
                neShape = (Shape) shapeClass.newInstance(); //north-east shape
                eShape = (Shape) shapeClass.newInstance(); //east shape
                seShape = (Shape) shapeClass.newInstance(); //south-east shape
                sShape = (Shape) shapeClass.newInstance(); //south shape

                swShape = (Shape) shapeClass.newInstance(); // south-west shape
                wShape = (Shape) shapeClass.newInstance(); // west shape
                nwShape = (Shape) shapeClass.newInstance();   // north-west shape            

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ShapeNodeFraming.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        protected void initShape() {
            initShape(nShape);
            initShape(neShape);
            initShape(eShape);
            initShape(seShape);
            initShape(sShape);
            initShape(swShape);
            initShape(wShape);
            initShape(nwShape);

        }

        protected void initShape(Shape shape) {
            //shape.setFill(Color.TRANSPARENT);
            shape.setFill(Color.WHITE);
            shape.setStrokeType(StrokeType.CENTERED);
            shape.setStroke(Color.rgb(255, 148, 40));
            shape.setStrokeWidth(strokeWidth);
            
            ((Rectangle) shape).setX(20);
            ((Rectangle) shape).setY(50);
            shape.setManaged(false);
            shape.toFront();

        }

        protected void setVisible(Shape shape, boolean visible) {
            shape.setVisible(visible);
        }

        protected void setVisible(boolean visible) {
            setVisible(nShape, visible);
            setVisible(neShape, visible);
            setVisible(eShape, visible);
            setVisible(seShape, visible);
            setVisible(sShape, visible);
            setVisible(swShape, visible);
            setVisible(wShape, visible);
            setVisible(nwShape, visible);
        }

        protected void unbind() {
            unbind(nShape);
            unbind(neShape);
            unbind(eShape);
            unbind(seShape);
            unbind(sShape);
            unbind(swShape);
            unbind(wShape);
            unbind(nwShape);
        }

        protected void bind() {
            Rectangle ind = framing.getIndicator();
            double sw = ind.getStrokeWidth();// - 3;
            System.err.println("shape.width = " + shapeWidth + "; sw = " + sw );
            ((Rectangle) nShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().subtract(shapeWidth).divide(2)));
            ((Rectangle) nShape).yProperty().bind(ind.yProperty().subtract(shapeHeight + sw));

            ((Rectangle) neShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(sw)));
            ((Rectangle) neShape).yProperty().bind(ind.yProperty().subtract(shapeHeight + sw));

            ((Rectangle) eShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(sw)));
            ((Rectangle) eShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().subtract(shapeHeight).divide(2)));

            ((Rectangle) seShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(sw)));
            ((Rectangle) seShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(sw)));

            ((Rectangle) sShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().subtract(shapeWidth).divide(2)));
            ((Rectangle) sShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(sw)));

            ((Rectangle) swShape).xProperty().bind(ind.xProperty().subtract(shapeWidth + sw));
            ((Rectangle) swShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(sw)));

            ((Rectangle) wShape).xProperty().bind(ind.xProperty().subtract(shapeWidth + sw));
            ((Rectangle) wShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().subtract(shapeHeight).divide(2)));

            ((Rectangle) nwShape).xProperty().bind(ind.xProperty().subtract(shapeWidth + sw));
            ((Rectangle) nwShape).yProperty().bind(ind.yProperty().subtract(shapeHeight + sw));

        }

        protected void unbind(Shape shape) {
            ((Rectangle) shape).xProperty().unbind();
            ((Rectangle) shape).yProperty().unbind();
        }

        protected void addToPane(Pane pane) {
            pane.getChildren().add(nShape);
            nShape.toFront();
            pane.getChildren().add(neShape);
            neShape.toFront();
            pane.getChildren().add(eShape);
            eShape.toFront();
            pane.getChildren().add(seShape);
            seShape.toFront();
            pane.getChildren().add(sShape);
            sShape.toFront();
            pane.getChildren().add(swShape);
            swShape.toFront();
            pane.getChildren().add(wShape);
            wShape.toFront();
            pane.getChildren().add(nwShape);
            nwShape.toFront();
            setVisible(true);
        }

        protected void removeShapeListeners() {
            removeShapeListeners(nShape);
            removeShapeListeners(neShape);
            removeShapeListeners(eShape);
            removeShapeListeners(seShape);
            removeShapeListeners(sShape);
            removeShapeListeners(swShape);
            removeShapeListeners(wShape);
            removeShapeListeners(nwShape);
        }

        protected void removeShapeListeners(Shape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            shape.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
        }

        protected void setSize(Shape shape) {
            Rectangle r = (Rectangle) shape;
            r.setWidth(shapeWidth);
            r.setHeight(shapeHeight);

        }

        protected void setPosition() {
            Rectangle ind = framing.getIndicator();
            ((Rectangle) nShape).setX(ind.getX() + (ind.getWidth() - shapeWidth) / 2);
            ((Rectangle) nShape).setY(ind.getY() - shapeHeight);
            setSize(nShape);

            ((Rectangle) neShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
            ((Rectangle) neShape).setY(ind.getY() - shapeHeight);
            setSize(neShape);

            ((Rectangle) eShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
            ((Rectangle) eShape).setY(ind.getY() - (ind.getHeight() - shapeHeight) / 2);
            setSize(eShape);

            ((Rectangle) seShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
            ((Rectangle) seShape).setY(ind.getY() - +ind.getHeight());
            setSize(seShape);

            ((Rectangle) sShape).setX(ind.getX() + (ind.getWidth() - shapeWidth) / 2);
            ((Rectangle) sShape).setY(ind.getY() - ind.getHeight());
            setSize(sShape);

            ((Rectangle) swShape).setX(ind.getX() - shapeWidth);
            ((Rectangle) swShape).setY(ind.getY() - +ind.getHeight());
            setSize(swShape);

            ((Rectangle) wShape).setX(ind.getX() - shapeWidth);
            ((Rectangle) wShape).setY(ind.getY() - ind.getHeight());
            setSize(wShape);

            ((Rectangle) nwShape).setX(ind.getX() - shapeWidth);
            ((Rectangle) nwShape).setY(ind.getY() - shapeHeight);
            setSize(nwShape);

        }

        public void handle(MouseEvent ev, Shape shape, Cursor c) {
//            System.err.println("Event.getType = " + ev.getEventType());
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
//                System.err.println("NEW HANDLE(EV,Shape,Cursor) = " + c);
                Point2D pt = shape.screenToLocal(ev.getScreenX(), ev.getScreenY());

                if (!framing.isCursorSupported(c)) {
                    shape.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    shape.getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
//                System.err.println("MOUSE EXITED");
                shape.getScene().setCursor(Cursor.DEFAULT);
            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
//                System.err.println("MOUSE PRESSED");
                removeMouseExitedListener(shape);
                Bounds ib = shape.getBoundsInLocal();
                framing.saveCursor = c;
                framing.cursorSupported = framing.isCursorSupported(framing.saveCursor);
                if (!framing.cursorSupported) {
                    shape.getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                framing.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);
//                System.err.println("MOUSE DRAG_DETECTED");
                framing.hide();
                wnf.show(framing.getNode());
                wnf.redirectMouseEvents(ev, framing.startMousePos, framing);
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                //addMouseExitedListener(shape);
                shape.getScene().setCursor(Cursor.DEFAULT);
            }
        }

        @Override
        public void handle(MouseEvent ev) {
//            System.err.println("NEW HANDLE(EV) " + ev.getSource());
            if (ev.getSource() == nShape) {
//                System.err.println("NEW HANDLE(EV) 1");
                handle(ev, nShape, Cursor.N_RESIZE);

            } else if (ev.getSource() == neShape) {
//                System.err.println("NEW HANDLE(EV) 2");
                handle(ev, neShape, Cursor.NE_RESIZE);

            } else if (ev.getSource() == eShape) {
//                System.err.println("NEW HANDLE(EV) 2.1");
                handle(ev, eShape, Cursor.E_RESIZE);
            } else if (ev.getSource() == seShape) {
//                System.err.println("NEW HANDLE(EV) 3");
                handle(ev, seShape, Cursor.SE_RESIZE);
            } else if (ev.getSource() == sShape) {
//                System.err.println("NEW HANDLE(EV) 4");
                handle(ev, sShape, Cursor.S_RESIZE);
            } else if (ev.getSource() == swShape) {
                handle(ev, swShape, Cursor.SW_RESIZE);
            } else if (ev.getSource() == wShape) {
//                System.err.println("NEW HANDLE(EV) 5");
                handle(ev, wShape, Cursor.W_RESIZE);
            } else if (ev.getSource() == nwShape) {
//                System.err.println("NEW HANDLE(EV) 6");
                handle(ev, nwShape, Cursor.NW_RESIZE);
            }
        }

    } //class 

}// class ShapeNodeFraming
