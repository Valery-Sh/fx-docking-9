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

import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.dragging.view.WindowNodeFraming;

/**
 *
 * @author Valery
 */
public class ShapeFraming extends Rectangle { //implements NodeFraming{

    public static String ID = "STYLE-AS-ID-89528991-bd7a-4792-911b-21bf56660bfb";
    //private Rectangle indicator;

    public static final int CENTER = 0;
    public static final int N_RESIZE = 1;
    public static final int NE_RESIZE = 2;
    public static final int E_RESIZE = 3;
    public static final int SE_RESIZE = 4;
    public static final int S_RESIZE = 5;
    public static final int SW_RESIZE = 6;
    public static final int W_RESIZE = 7;
    public static final int NW_RESIZE = 8;

    private final ReadOnlyObjectWrapper<Node> boundNodeWrapper = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<SideShapes> sideShapes = new SimpleObjectProperty<>();

    private Point2D startMousePos;

    //
    // listeners
    //
    private final ChangeListener<Bounds> boundsInParentListener = (o, ov, nv) -> {
        adjustBoundsToNode(nv);
    };

    private final ChangeListener<SideShapes> sideShapesListener = (o, ov, nv) -> {
        if (ov != null && getParent() != null) {
            ov.removeShapes();
        }
        if (nv != null && getParent() != null) {
            nv.bind(this);
        }

    };

    private ChangeListener<Node> boundNodeListener;

//    private ChangeListener<Boolean> visiblePropertyListener;
    public ShapeFraming() {
        setManaged(false);
        setMouseTransparent(true);
        getStyleClass().add("shape-framing");

        init();
    }

    private void init() {
        sideShapes.addListener(sideShapesListener);
        initBoundNode();
        initRect();
    }

    /*    @Override
    protected void layoutChildren() {

        super.layoutChildren();

        if (getBoundNode() == null) {
            return;
        }
    }
     */
 /*    public Rectangle getIndicator() {
        return indicator;
    }
     */
    public void setDefaultStyles() {
        setStyle("-fx-stroke-type: inside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 2; -fx-fill: transparent");
    }

    private void initBoundNode() {
        boundNodeListener = (v, ov, nv) -> {
            if (ov != null) {
                removeBoundInParentListener(ov);
                if (getSideShapes() != null) {
                    getSideShapes().unbind(); // to remove mouseEventListeners
                }
            }
            if (nv != null) {
                if (getSideShapes() != null) {
                    getSideShapes().bind(); // to remove mouseEventListeners
                }

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
        };
        boundNodeProperty().addListener(boundNodeListener);
    }

    protected void adjustBoundsToNode(Bounds boundsInParent) {
        Bounds sb = getBoundNode().localToScene(getBoundNode().getLayoutBounds());

        setY(sb.getMinY());
        setX(sb.getMinX());
        setWidth(boundsInParent.getWidth());
        setHeight(boundsInParent.getHeight());
    }

    private void removeBoundInParentListener(Node node) {
        node.boundsInParentProperty().removeListener(boundsInParentListener);
    }

    private void initRect() {
        setManaged(false);
        getStyleClass().add(ID);
        setStrokeType(StrokeType.INSIDE);
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

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public ObjectProperty<SideShapes> sideShapeProperty() {
        return sideShapes;
    }

    public SideShapes getSideShapes() {
        return sideShapes.get();
    }

    public void setSideShapes(SideShapes sideShapes) {
        this.sideShapes.set(sideShapes);
    }

    public abstract class SideShapes implements EventHandler<MouseEvent> {

        private ShapeFraming shapeFraming;

        private Rectangle indicator;

        protected Shape nShape;    //north indicator
        protected Shape neShape;   //north-east indicator
        protected Shape eShape;    //east indicator
        protected Shape seShape;   //south-east indicator
        protected Shape sShape;    //south indicator

        protected Shape swShape;   // south-west indicator
        protected Shape wShape;    // west indicator
        protected Shape nwShape;   // north-west indicator

        private final ObservableList<String> styleClass = FXCollections.observableArrayList();

        private final ObjectProperty<String> style = new SimpleObjectProperty<>();

        public SideShapes() {
            createShapes();
        }

        public void setDefaultStyle() {
            setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 1; -fx-fill: white");
        }


        public ObservableList<String> getStyleClass() {
            return styleClass;
        }

        public ObjectProperty<String> styleProperty() {
            return style;
        }

        public String getStyle() {
            return style.get();
        }

        public void setStyle(String style) {
            this.style.set(style);
        }

        public Shape[] getShapes() {
            if (nShape == null) {
                return new Shape[0];
            }
            return new Shape[]{nShape, neShape, eShape, seShape, sShape, swShape, wShape, nwShape};
        }

        public final void bind(ShapeFraming selPane) {
            if (selPane == null && this.shapeFraming != null) {
                removeShapes();
                shapeFraming = null;
                return;
            } else if (selPane == null) {
                return;
            }

            this.shapeFraming = selPane;

            indicator = shapeFraming;

            addToPane((Pane) indicator.getParent());
            
            if (getStyle() == null && getStyleClass().isEmpty()) {
                setDefaultStyle();
                for (Shape s : getShapes()) {
                    s.setStyle(getStyle());
                }
            } else if (getStyle() != null) {
                for (Shape s : getShapes()) {
                    s.setStyle(getStyle());
                }

            } else {
                for (Shape sh : getShapes()) {
                    getStyleClass().forEach(s -> {
                        sh.getStyleClass().add(s);
                    });
                }
                

            }

            bind();
        }

        public void removeShapes() {
            remove(nShape);
            remove(neShape);
            remove(eShape);
            remove(seShape);
            remove(sShape);
            remove(swShape);
            remove(wShape);
            remove(nwShape);
        }

        public ShapeFraming getShapeFraming() {
            return shapeFraming;
        }

        public boolean isBound() {
            return shapeFraming != null;
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
            //System.err.println("removeShapeMouseEventHandlers " + shapeFraming.getBoundNode());            
            shape.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
            shape.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
//            System.err.println("---------------------------------------------------------------------");            
//            indicator.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);

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
            unbind(shape);
            if (shape.getParent() != null) {
                ((Pane) shape.getParent()).getChildren().remove(shape);
            }
        }

        protected void createShapes(Class<?> shapeClass) {
            try {

                remove(nShape);
                nShape = (Shape) shapeClass.newInstance(); //north indicator

                remove(neShape);
                neShape = (Shape) shapeClass.newInstance(); //north-east indicator

                remove(eShape);
                eShape = (Shape) shapeClass.newInstance(); //east indicator

                remove(seShape);
                seShape = (Shape) shapeClass.newInstance(); //south-east indicator

                remove(sShape);
                sShape = (Shape) shapeClass.newInstance(); //south indicator

                remove(swShape);
                swShape = (Shape) shapeClass.newInstance(); // south-west indicator

                remove(wShape);
                wShape = (Shape) shapeClass.newInstance(); // west indicator

                remove(nwShape);
                nwShape = (Shape) shapeClass.newInstance();   // north-west indicator            

                initialize();

            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("EXCEPTION !!!!");
                Logger.getLogger(SideShapes.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        protected void initialize() {

            initialize(nShape);
            nShape.setManaged(false);
            nShape.toFront();

            initialize(neShape);

            neShape.toFront();
            neShape.setManaged(false);

            initialize(eShape);
            eShape.toFront();
            eShape.setManaged(false);

            initialize(seShape);
            seShape.toFront();
            seShape.setManaged(false);

            initialize(sShape);
            sShape.toFront();
            sShape.setManaged(false);

            initialize(swShape);
            swShape.toFront();
            swShape.setManaged(false);

            initialize(wShape);
            wShape.toFront();
            wShape.setManaged(false);

            initialize(nwShape);
            nwShape.toFront();
            nwShape.setManaged(false);
        }

        protected void addShapeMouseEventHandlers() {
            addShapeMouseEventHandlers(nShape);
            addShapeMouseEventHandlers(neShape);
            addShapeMouseEventHandlers(eShape);
            addShapeMouseEventHandlers(seShape);
            addShapeMouseEventHandlers(sShape);
            addShapeMouseEventHandlers(swShape);
            addShapeMouseEventHandlers(wShape);
            addShapeMouseEventHandlers(nwShape);
        }

        protected void removeShapeMouseEventHandlers() {
            removeShapeMouseEventHandlers(nShape);
            removeShapeMouseEventHandlers(neShape);
            removeShapeMouseEventHandlers(eShape);
            removeShapeMouseEventHandlers(seShape);
            removeShapeMouseEventHandlers(sShape);
            removeShapeMouseEventHandlers(swShape);
            removeShapeMouseEventHandlers(wShape);
            removeShapeMouseEventHandlers(nwShape);

        }

        private void setVisible(Shape shape, boolean visible) {
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

        private void setVisible(boolean visible) {
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
            removeShapeMouseEventHandlers();

        }

        private void bind() {
            //System.err.println("BIND");
            //System.err.println("   --- REMOVE LISTENERS");
            unbind(nShape);
            unbind(neShape);
            unbind(eShape);
            unbind(seShape);
            unbind(sShape);
            unbind(swShape);
            unbind(wShape);
            unbind(nwShape);
            removeShapeMouseEventHandlers();

            initialize();

            bind(nShape);
            bind(neShape);
            bind(eShape);
            bind(seShape);
            bind(sShape);
            bind(swShape);
            bind(wShape);
            bind(nwShape);

            addShapeMouseEventHandlers();
        }

        protected abstract void createShapes();

        protected abstract void initialize(Shape shape);

        protected abstract void bind(Shape shape);

        protected abstract void unbind(Shape shape);

        protected void addToPane(Pane pane) {
            removeShapes();
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
        }

        protected Rectangle getIndicator() {
            return indicator;
        }

        public void handle(MouseEvent ev, Shape shape, Cursor c) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Point2D pt = shape.screenToLocal(ev.getScreenX(), ev.getScreenY());

                shape.getScene().setCursor(c);
            } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                removeMouseExitedListener(shape);
                shapeFraming.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);
                shapeFraming.setVisible(false);
                wnf.show(shapeFraming.getBoundNode());
                wnf.redirectMouseEvents(ev, shapeFraming.getStartMousePos(), shapeFraming);
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
                addMouseExitedListener(shape);
            }
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getSource() == nShape) {
                handle(ev, nShape, Cursor.N_RESIZE);

            } else if (ev.getSource() == neShape) {
                handle(ev, neShape, Cursor.NE_RESIZE);
            } else if (ev.getSource() == eShape) {
                handle(ev, eShape, Cursor.E_RESIZE);
            } else if (ev.getSource() == seShape) {
                handle(ev, seShape, Cursor.SE_RESIZE);
            } else if (ev.getSource() == sShape) {
                handle(ev, sShape, Cursor.S_RESIZE);
            } else if (ev.getSource() == swShape) {
                handle(ev, swShape, Cursor.SW_RESIZE);
            } else if (ev.getSource() == wShape) {
                handle(ev, wShape, Cursor.W_RESIZE);
            } else if (ev.getSource() == nwShape) {
                handle(ev, nwShape, Cursor.NW_RESIZE);
            }
        }

    }//class SidePanes

    public static class SideCircles extends SideShapes {

        private DoubleProperty radius = new SimpleDoubleProperty(0);

        public SideCircles() {
            init();
        }

        private void init() {
            radius.addListener((v, ov, nv) -> {
                for (Shape s : getShapes()) {
                    Circle c = (Circle) s;
                    if (c.getRadius() <= 0) {
                        c.setRadius((double) nv);
                    }
                }
            });
        }

        @Override
        protected void initialize(Shape shape) {
            Circle c = (Circle) shape;
            c.setCenterX(20);
            c.setCenterY(50);
        }

        public DoubleProperty radiusPropert() {
            return radius;
        }

        public Double getRadius() {
            return radius.get();
        }

        public void setRadius(double radius) {
            this.radius.set(radius);
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
            createShapes(Circle.class);
        }

    }//class Circle

}//class
