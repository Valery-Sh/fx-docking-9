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
package org.vns.javafx.dock.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.dock.api.dragging.view.ShapeNodeFraming;
import org.vns.javafx.dock.api.dragging.view.WindowNodeFraming;

/**
 *
 * @author Valery
 */
public class SelectPane extends Pane {

    public static String ID = "RECT-ID-89528991-bd7a-4792-911b-21bf56660bfb";
    private Rectangle rect;
    double rectStrokeWidth = 1;
    Paint rectFill = Color.YELLOW;
    Paint rectStroke = Color.RED;

    public static final int N_RESIZE = 0;
    public static final int NE_RESIZE = 1;
    public static final int E_RESIZE = 2;
    public static final int SE_RESIZE = 3;
    public static final int S_RESIZE = 4;
    public static final int SW_RESIZE = 5;
    public static final int W_RESIZE = 6;
    public static final int NW_RESIZE = 7;

    private final ReadOnlyObjectWrapper<Node> boundNodeWrapper = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<SideShapes> sideShapes = new SimpleObjectProperty<>();

    //private final ObjectProperty<Node> boundNode = new SimpleObjectProperty<>();
//    private final ObjectProperty<Class<? extends Shape>> shapeClass = new SimpleObjectProperty<>(Circle.class);
    private final ObjectProperty<Shape> tuningShape = new SimpleObjectProperty<>(new Circle(1.5, Color.WHITE));

    private final BooleanProperty useSideShapes = new SimpleBooleanProperty(true);

    private Point2D startMousePos;

    //
    // listeners
    //
    private final ChangeListener<Bounds> boundsInParentListener = (o, ov, nv) -> {
//                    System.err.println("0 boundNode: boundsInParentProperty nv = " + nv);
        adjustBoundsToNode(nv);
//                    System.err.println("1 boundNode: boundsInParentProperty nv = " + nv);
    };
    private final ChangeListener<Boolean> useSideShapesListener = (o, ov, nv) -> {
        if (ov && getSideShapes() != null) {
            getSideShapes().bind(this);
        }
        if (nv && getSideShapes() == null) {
            createDefaultSideShapes();
        }
    };

    private final ChangeListener<SideShapes> sideShapesListener = (o, ov, nv) -> {
        if (ov != null) {
            System.err.println("sideShapesListener ov = " + ov);
            ov.unbind();
        }
        if (nv != null && isUseSideShapes()) {
            nv.bind(this);
        }

    };

    private ChangeListener<Node> boundNodeListener;

//    private ChangeListener<Boolean> visiblePropertyListener;

    public SelectPane() {
        setManaged(false);
        getStyleClass().add("select-pane");
        init();
    }

    private void init() {
        Shape s = getTuningShape();
//        shapeClassProperty().addListener(this::shapeClassChanged);
        s.getStyleClass().add("side-shape");
        s.setStrokeType(StrokeType.OUTSIDE);
        s.setStrokeWidth(1);
        s.setStyle("-fx-offset: 0");

        useSideShapes.addListener(useSideShapesListener);

/*        visiblePropertyListener = (o, ov, nv) -> {
            if (getSideShapes() != null) {
                //getSideShapes().setVisible(nv);
            }
        };
        visibleProperty().addListener(visiblePropertyListener);
*/        
        initBoundNode();
        initRect();
        //createSideShapes();
    }

    @Override
    protected void layoutChildren() {

        super.layoutChildren();

        if (getBoundNode() == null) {
            return;
        }
    }

    private void initBoundNode() {
        boundNodeListener = (v, ov, nv) -> {
            if (ov != null) {
                removeBoundInParentListener(nv);
            }
            if (nv != null) {
                nv.boundsInParentProperty().addListener(boundsInParentListener);

                if (getBoundNode().getScene() != null && getBoundNode().getScene().getWindow() != null) {
                    Bounds curPb = getBoundNode().getBoundsInParent();
                    adjustBoundsToNode(curPb);
                }
            }
            if (nv == null) {
                setVisible(false);
            } else {
                setVisible(true);
            }
            updateSideShapes(ov, nv);
        };
        boundNodeProperty().addListener(boundNodeListener);
    }

    protected void updateSideShapes(Node oldValue, Node newValue) {
        if (getSideShapes() == null && newValue != null) {
            System.err.println("updateSideShapes");
            createDefaultSideShapes();
        }

    }

    protected void adjustBoundsToNode(Bounds boundsInParent) {
        Bounds sb = getBoundNode().localToScene(getBoundNode().getLayoutBounds());

        rect.setY(sb.getMinY());
        rect.setX(sb.getMinX());
        rect.setWidth(boundsInParent.getWidth());
        rect.setHeight(boundsInParent.getHeight());
    }

    private void removeBoundInParentListener(Node node) {
        node.boundsInParentProperty().removeListener(boundsInParentListener);
    }

    private void initRect() {
        rect = new Rectangle(10, 10, 70, 30);
        rect.setId(ID);
        rect.setFill(rectFill);
        rect.setStroke(rectStroke);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStrokeWidth(rectStrokeWidth);
        getChildren().add(rect);
    }

    private void createDefaultSideShapes() {
        if (getSideShapes() != null) {
            sideShapes.removeListener(sideShapesListener);
        }
        if (isUseSideShapes()) {
            setSideShapes(new SideCircles());
            getSideShapes().bind(this);
            sideShapes.addListener(sideShapesListener);
        }
    }

    public ReadOnlyObjectProperty<Node> boundNodeProperty() {
        return boundNodeWrapper.getReadOnlyProperty();
    }

    public void bind(Node node) {
        setBoundNode(node);
    }

    public void unbind() {
        if (getBoundNode() != null) {
            removeBoundInParentListener(getBoundNode());
        }
        setBoundNode(null);
    }

    public Node getBoundNode() {
        return boundNodeWrapper.getReadOnlyProperty().getValue();
    }

    protected void setBoundNode(Node node) {
        boundNodeWrapper.setValue(node);
    }

    /*    public ObjectProperty<Class<? extends Shape>> shapeClassProperty() {
        return shapeClass;
    }
     */
    public ObjectProperty<SideShapes> sideShapeProperty() {
        return sideShapes;
    }

    public SideShapes getSideShapes() {
        return sideShapes.get();
    }

    public void setSideShapes(SideShapes sideShapes) {
        this.sideShapes.set(sideShapes);
    }

    public BooleanProperty useSideShapes() {
        return useSideShapes;
    }

    public boolean isUseSideShapes() {
        return useSideShapes.get();
    }

    public void setUseSideShapes(boolean useSideShapes) {
        this.useSideShapes.set(useSideShapes);
    }

    public ObjectProperty<Shape> tuningShapeProperty() {
        return tuningShape;
    }

    public Shape getTuningShape() {
        return tuningShape.get();
    }

    public void setTuningShape(Shape shape) {
        this.tuningShape.set(shape);
    }

    public void handle(MouseEvent ev) {
        /*        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
            System.err.println("MOUSE MOVE ShapeNodeFraming");
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

        } else 
         */
        if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {

            if (!getScene().getRoot().contains(ev.getX(), ev.getY())) {
                //removeWindowListeners();
                //hide();
//                System.err.println("indicator.getScene().getRoot().contains(ev.getX(), ev.getY()) = " + indicator.getScene().getRoot().contains(ev.getX(), ev.getY()));
                //return;
            }
            /*            Bounds ib = rect.getBoundsInLocal();
            saveCursor = ShapeNodeResizeExecutor.cursorBy(ev.getX() - ib.getMinX(), ev.getY() - ib.getMinY(), indicator);

            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                indicator.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
             */
            startMousePos = new Point2D(ev.getScreenX(), ev.getScreenY());
            //resizeExecutor.start(ev, this, indicator.getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);

            //hide();
            wnf.show(getBoundNode());
            //wnf.redirectMouseEvents(ev, startMousePos, this);
        }
    }

    public abstract class SideShapes implements EventHandler<MouseEvent> {

        private SelectPane selectPane;

        private Rectangle indicator;


        protected Shape nShape;    //north shape
        protected Shape neShape;   //north-east shape
        protected Shape eShape;    //east shape
        protected Shape seShape;   //south-east shape
        protected Shape sShape;    //south shape

        protected Shape swShape;   // south-west shape
        protected Shape wShape;    // west shape
        protected Shape nwShape;   // north-west shape

        private boolean cssApplied;// = true;
        
        private ObservableMap<String,String> defaultStyles = FXCollections.observableHashMap();
        {
            defaultStyles.put("-fx-stroke-type", "outside");
            defaultStyles.put("-fx-stroke", "rgb(255, 148, 40)");
            defaultStyles.put("-fx-stroke-width", "1");
            defaultStyles.put("-fx-fill", "WHITE");
        }
        
        
        public SideShapes() {
        }

    
        public void applyCss() {
            cssApplied = true;
            rebind();
        }

        public boolean isCssApplied() {
            return cssApplied;
        }

        public void setCssApplied(boolean cssApplied) {
            this.cssApplied = cssApplied;
            rebind();
        }

        public final void bind(SelectPane selPane) {
            if (selPane == null && this.selectPane != null) {
                unbind();
                removeShapes();
                selectPane = null;
                return;
            } else if (selPane == null) {
                return;
            }

            this.selectPane = selPane;

            indicator = (Rectangle) selectPane.lookup("#" + ID);

            createShapes();
            bind();
            //setVisible(selectPane.isVisible());
        }
        
        protected void rebind() {
            if ( selectPane != null ) {
                System.err.println("REBIND");
                bind(selectPane);
            }
        }
        protected void removeShapes() {
            remove(nShape);
            remove(neShape);
            remove(eShape);
            remove(seShape);
            remove(sShape);
            remove(swShape);
            remove(wShape);
            remove(nwShape);
        }

        public SelectPane getSelectPane() {
            return selectPane;
        }

        public boolean isBound() {
            return selectPane != null;
        }

        public Shape getShape(Cursor c) {
            Shape retval = null;
            if (c == Cursor.N_RESIZE) {
                retval = nShape;
            }
            if (c == Cursor.NE_RESIZE) {
                retval = neShape;
            }
            if (c == Cursor.E_RESIZE) {
                retval = eShape;
            }
            if (c == Cursor.SE_RESIZE) {
                retval = seShape;
            }

            if (c == Cursor.S_RESIZE) {
                retval = sShape;
            }
            if (c == Cursor.SW_RESIZE) {
                retval = swShape;
            }
            if (c == Cursor.W_RESIZE) {
                retval = wShape;
            }
            if (c == Cursor.NW_RESIZE) {
                retval = nwShape;
            }
            return retval;
        }

        public double getOffset() {
            return 0;
        }

        protected void addShapeMouseEventHandlers(Shape shape) {
            shape.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
            shape.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
            shape.addEventFilter(MouseEvent.MOUSE_MOVED, this);
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);
            shape.addEventFilter(MouseEvent.DRAG_DETECTED, this);
            //shape.setVisible(true);
        }

        protected void removeShapeMouseEventHandlers(Shape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
            shape.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
//            shape.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);

        }

        protected void removeMouseExitedListener(Shape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        protected void addMouseExitedListener(Shape shape) {
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        private void remove(Shape shape) {
            if (shape == null) {
                return;
            }
            ((Pane) indicator.getParent()).getChildren().remove(shape);
        }

        protected void createShapes(Class<?> shapeClass) {
            try {
                
                System.err.println("   --- 1 size = " + getSelectPane().getChildren().size() );
                if ( nShape != null) {
                    unbind(nShape);
                    remove(nShape);
                    nShape = null;
                }
                System.err.println("   --- 2 size = " + getSelectPane().getChildren().size() );
                nShape = (Shape) shapeClass.newInstance(); //north shape
                //nShape.getStyleClass().clear();
                if ( isCssApplied() ) {
                    //nShape.setFill(Color.GREEN);
                    nShape.getStyleClass().add("side-shape");
                }
                nShape.applyCss();
                if ( ! isCssApplied()) {
                    System.err.println("createShapes !!!");
                    initStroke(nShape);
                }

                remove(neShape);
                neShape = (Shape) shapeClass.newInstance(); //north-east shape
                initStroke(neShape);

                remove(eShape);
                eShape = (Shape) shapeClass.newInstance(); //east shape
                initStroke(eShape);

                remove(seShape);
                seShape = (Shape) shapeClass.newInstance(); //south-east shape
                initStroke(seShape);

                remove(sShape);
                sShape = (Shape) shapeClass.newInstance(); //south shape
                initStroke(sShape);

                remove(swShape);
                swShape = (Shape) shapeClass.newInstance(); // south-west shape
                initStroke(swShape);

                remove(wShape);
                wShape = (Shape) shapeClass.newInstance(); // west shape
                initStroke(wShape);

                remove(nwShape);
                nwShape = (Shape) shapeClass.newInstance();   // north-west shape            
                initStroke(nwShape);

            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("EXCEPTION !!!!");
                Logger.getLogger(ShapeNodeFraming.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void initStroke(Shape shape) {

            
            if ( ! isCssApplied()) {
                System.err.println("! isCssApplyed");
/*                shape.setStrokeType(getConfig().getShape().getStrokeType());

                shape.setStroke(getConfig().getShape().getStroke());
                shape.setStrokeWidth(getConfig().getShape().getStrokeWidth());
                shape.setFill(getConfig().getShape().getFill());
*/                
            }
            shape.setManaged(false);
            shape.toFront();
        }

        protected void initialize() {

            initialize(nShape);
            nShape.toFront();
            initialize(neShape);
            neShape.toFront();
            initialize(eShape);
            eShape.toFront();
            initialize(seShape);
            seShape.toFront();
            initialize(sShape);
            sShape.toFront();
            initialize(swShape);
            swShape.toFront();
            initialize(wShape);
            wShape.toFront();
            initialize(nwShape);
            nwShape.toFront();
        }

        protected void setVisible(Shape shape, boolean visible) {
            if (visible) {
                addShapeMouseEventHandlers(shape);
            } else {
                removeShapeMouseEventHandlers(shape);
            }
            shape.setVisible(visible);
        }

        public boolean isVisible() {
            return nShape.isVisible()
                    && eShape.isVisible()
                    && seShape.isVisible()
                    && sShape.isVisible()
                    && swShape.isVisible()
                    && wShape.isVisible()
                    && nwShape.isVisible();

        }

        public void setVisible(boolean visible) {
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
            setVisible(false);
            
        }

        private void bind() {
            unbind(nShape);
            unbind(neShape);
            unbind(eShape);
            unbind(seShape);
            unbind(sShape);
            unbind(swShape);
            unbind(wShape);
            unbind(nwShape);

            initialize();

            bind(nShape);
            bind(neShape);
            bind(eShape);
            bind(seShape);
            bind(sShape);
            bind(swShape);
            bind(wShape);
            bind(nwShape);
//            setVisible(selectPane.getIndicator().isVisible());
        }

        protected abstract void createShapes();

        protected abstract void initialize(Shape shape);

        protected abstract void bind(Shape shape);

//        public abstract Config getConfig();

        protected abstract void unbind(Shape shape);

        protected void addToPane(Pane pane) {
            pane.getChildren().add(nShape);
            if ( ! nShape.getStyleClass().isEmpty()  )
                System.err.println("   --- addToPane css class = " + nShape.getStyleClass().get(0));
            nShape.applyCss();
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
//            setVisible(true);
            nShape.applyCss();            
        }

        /*        protected void removeShapeListeners() {
            removeShapeMouseEventHandlers(nShape);
            removeShapeMouseEventHandlers(neShape);
            removeShapeMouseEventHandlers(eShape);
            removeShapeMouseEventHandlers(seShape);
            removeShapeMouseEventHandlers(sShape);
            removeShapeMouseEventHandlers(swShape);
            removeShapeMouseEventHandlers(wShape);
            removeShapeMouseEventHandlers(nwShape);
        }
         */

 /*        public void handle(MouseEvent ev, Shape shape, Cursor c) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Point2D pt = shape.screenToLocal(ev.getScreenX(), ev.getScreenY());

                if (!selectPane.isCursorSupported(c)) {
                    shape.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    shape.getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                removeMouseExitedListener(shape);
                selectPane.setSaveCursor(c);
                selectPane.setCursorSupported(selectPane.isCursorSupported(selectPane.getSaveCursor()));
                if (!selectPane.isCursorSupported()) {
                    shape.getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                selectPane.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class
                );
                selectPane.hide();
                wnf.addShapeMouseEventHandlers(selectPane.getNode());
                wnf.redirectMouseEvents(ev, selectPane.startMousePos, selectPane);
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
            }
        }
         */
        protected Rectangle getIndicator() {
            return indicator;
        }

        @Override
        public void handle(MouseEvent ev) {
        }

    }//class SidePanes

    public static class SideCircles extends SideShapes {

//        private final CircleConfig config;

        public SideCircles() {
//            config = new CircleConfig(this);
            init();
        }

        private void init() {

        }

/*        @Override
        public CircleConfig getConfig() {
            return config;
        }
*/
        @Override
        protected void initialize(Shape shape) {
            ((Circle) shape).setCenterX(20);
            ((Circle) shape).setCenterY(50);
//            ((Circle) shape).setRadius(getConfig().getShape().getRadius());
/*            if ( isCssApplied() ) {
                shape.setStroke(null);
                shape.setStrokeType(StrokeType.CENTERED);
                shape.setStrokeWidth(-1);
                shape.setFill(null);
                shape.applyCss();
            }
*/
        }

        @Override
        public void bind(Shape shape) {
            Rectangle ind = getIndicator();

            double offset = getOffset();

            double indDeltaWidth = ind.getStrokeWidth();// - 3;

            if (ind.getStrokeType() == StrokeType.INSIDE) {
                indDeltaWidth = 0;
            } else if (ind.getStrokeType() == StrokeType.CENTERED) {
                indDeltaWidth = indDeltaWidth / 2;
            }
            
            if (shape == nShape) {
                ((Circle) nShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).divide(2)));
                ((Circle) nShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));
                nShape.visibleProperty().bind(ind.visibleProperty());
            } else if (shape == neShape) {
                ((Circle) neShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) neShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));
                neShape.visibleProperty().bind(ind.visibleProperty());

            } else if (shape == eShape) {
                ((Circle) eShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) eShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).divide(2)));
                eShape.visibleProperty().bind(ind.visibleProperty());
                
            } else if (shape == seShape) {
                ((Circle) seShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) seShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset + 1)));
                seShape.visibleProperty().bind(ind.visibleProperty());

            } else if (shape == sShape) {
                ((Circle) sShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).divide(2)));
                ((Circle) sShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset)));
                sShape.visibleProperty().bind(ind.visibleProperty());

            } else if (shape == swShape) {
                ((Circle) swShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) swShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset)));
                swShape.visibleProperty().bind(ind.visibleProperty());

            } else if (shape == wShape) {
                ((Circle) wShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) wShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).divide(2)));
                wShape.visibleProperty().bind(ind.visibleProperty());

            } else if (shape == nwShape) {
                ((Circle) nwShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) nwShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));
                nwShape.visibleProperty().bind(ind.visibleProperty());
            }
        }

        protected void setX(Shape shape, double x) {
            ((Circle) shape).setCenterX(x);
        }

        protected void setY(Shape shape, double y) {
            ((Circle) shape).setCenterY(y);
        }

        @Override
        public void unbind(Shape shape) {
            ((Circle) shape).centerXProperty().unbind();
            ((Circle) shape).centerYProperty().unbind();
            shape.visibleProperty().unbind();
        }

        @Override
        protected void createShapes() {
            System.err.println("   --- createShapes()");
            createShapes(Circle.class);
            addToPane((Pane) getIndicator().getParent());
        }

    }//class Circle

    /*public static abstract class Config {

        private double offset = 0;
        private final SideShapes sideShapes;

        public Config(SideShapes sideShapes) {
            this.sideShapes = sideShapes;

        }

        public abstract Shape getShape();

        protected SideShapes getSideShapes() {
            return sideShapes;
        }

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public void apply() {
            sideShapes.bind();
            //sideShapes.get
        }
    }

    public static class CircleConfig extends Config {

        private final Shape shape;

        public CircleConfig(SideShapes sideShapes) {
            super(sideShapes);
            this.shape = new Circle();
            init();
        }

        private void init() {
            shape.setStrokeType(StrokeType.OUTSIDE);
            shape.setStroke(Color.rgb(255, 148, 40));
            shape.setStrokeWidth(1);
            shape.setFill(Color.WHITE);

            getShape().setRadius(3);
        }

        @Override
        public Circle getShape() {
            return (Circle) shape;
        }
    }

    public static class RectangleConfig extends Config {

        private final Shape shape;

        public RectangleConfig(SideShapes sideShapes) {
            super(sideShapes);
            this.shape = new Rectangle(3, 3);
        }

        @Override
        public Rectangle getShape() {
            return (Rectangle) shape;
        }

    }
*/
}//class
