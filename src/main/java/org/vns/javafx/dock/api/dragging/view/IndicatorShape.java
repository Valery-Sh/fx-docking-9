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
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.vns.javafx.dock.api.DockRegistry;

/**
 *
 * @author Valery
 */
public abstract class IndicatorShape implements EventHandler<MouseEvent> {

    protected final ShapeNodeFraming framing;

    protected double strokeWidth = 1;
    protected double offset = -4;
    protected StrokeType strokeType = StrokeType.OUTSIDE;

    //protected final Class<?> shapeClass;
    protected Shape nShape;    //north shape
    protected Shape neShape;   //north-east shape
    protected Shape eShape;    //east shape
    protected Shape seShape;   //south-east shape
    protected Shape sShape;    //south shape

    protected Shape swShape;   // south-west shape
    protected Shape wShape;    // west shape
    protected Shape nwShape;   // north-west shape

    private double[] dimention;

    public IndicatorShape(ShapeNodeFraming framing, double... dimention) {
        this.framing = framing;
        this.dimention = dimention;
        //this.shapeClass = shapeClass;
        init();
    }

    private void init() {
        createShapes();
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

    public double[] getDimention() {
        return dimention;
    }

    public void setDimention(double[] dimention) {
        this.dimention = dimention;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public StrokeType getStrokeType() {
        return strokeType;
    }

    public void setStrokeType(StrokeType strokeType) {
        this.strokeType = strokeType;
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

    protected void createShapes(Class<?> shapeClass) {
        try {
            nShape = (Shape) shapeClass.newInstance(); //north shape
            initStroke(nShape);

            neShape = (Shape) shapeClass.newInstance(); //north-east shape
            initStroke(neShape);

            eShape = (Shape) shapeClass.newInstance(); //east shape
            initStroke(eShape);

            seShape = (Shape) shapeClass.newInstance(); //south-east shape
            initStroke(seShape);

            sShape = (Shape) shapeClass.newInstance(); //south shape
            initStroke(sShape);

            swShape = (Shape) shapeClass.newInstance(); // south-west shape
            initStroke(swShape);

            wShape = (Shape) shapeClass.newInstance(); // west shape
            initStroke(wShape);

            nwShape = (Shape) shapeClass.newInstance();   // north-west shape            
            initStroke(nwShape);

        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ShapeNodeFraming.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initStroke(Shape shape) {
        shape.setFill(Color.WHITE);
        shape.setStrokeType(strokeType);
        shape.setStroke(Color.rgb(255, 148, 40));
        shape.setStrokeWidth(strokeWidth);

        initShape(shape);

        shape.setManaged(false);
        shape.toFront();
    }

    public void initShape() {
        initShape(nShape);
        nShape.toFront();
        initShape(neShape);
        neShape.toFront();
        initShape(eShape);
        eShape.toFront();
        initShape(seShape);
        seShape.toFront();
        initShape(sShape);
        sShape.toFront();
        initShape(swShape);
        swShape.toFront();
        initShape(wShape);
        wShape.toFront();
        initShape(nwShape);
        nwShape.toFront();
    }

    /*    protected void setSize(Shape shape) {
        Rectangle r = (Rectangle) shape;
        r.setWidth(shapeWidth);
        r.setHeight(shapeHeight);

    }
     */
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
        bind(nShape);
        bind(neShape);
        bind(eShape);
        bind(seShape);
        bind(sShape);
        bind(swShape);
        bind(wShape);
        bind(nwShape);
    }

    protected abstract void createShapes();

    protected abstract void initShape(Shape shape);

    protected abstract void setSize(Shape shape);

    protected abstract void bind(Shape shape);

    protected abstract void unbind(Shape shape);
    //protected abstract void setPosition(Shape shape);

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
        //shape.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        shape.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
    }

    public void handle(MouseEvent ev, Shape shape, Cursor c) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
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
            shape.getScene().setCursor(Cursor.DEFAULT);
        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            removeMouseExitedListener(shape);
            framing.setSaveCursor(c);
            framing.setCursorSupported(framing.isCursorSupported(framing.getSaveCursor()));
            if (!framing.isCursorSupported()) {
                shape.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            framing.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class
            );
            framing.hide();
            wnf.show(framing.getNode());
            wnf.redirectMouseEvents(ev, framing.startMousePos, framing);
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            shape.getScene().setCursor(Cursor.DEFAULT);
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

    public ShapeNodeFraming getNodeFraming() {
        return framing;

    }

    public static class IndicatorRectangle extends IndicatorShape {

        private double shapeWidth;
        private double shapeHeight;

        public IndicatorRectangle(ShapeNodeFraming framing, double shapeWidth, double shapeHeight) {
            super(framing, shapeWidth, shapeHeight);
            this.shapeWidth = shapeWidth;
            this.shapeHeight = shapeHeight;

        }

        public IndicatorRectangle(ShapeNodeFraming framing) {
            this(framing, 3, 3);

        }

        @Override
        protected void initShape(Shape shape) {
            ((Rectangle) shape).setX(20);
            ((Rectangle) shape).setY(50);
            setSize(shape);
        }

        @Override
        protected void setSize(Shape shape) {
            Rectangle r = (Rectangle) shape;
            r.setWidth(shapeWidth);
            r.setHeight(shapeHeight);
//            System.err.println("setSize w = " + r.getWidth() + "; h = " + r.getHeight());

        }

        @Override
        protected void bind(Shape shape) {
            Rectangle ind = framing.getIndicator();

            double indDeltaWidth = ind.getStrokeWidth();// - 3;
            //double indWidth = ind.getStrokeWidth() + 2;// - 3;

            if (ind.getStrokeType() == StrokeType.INSIDE) {
                indDeltaWidth = 0;
            } else if (ind.getStrokeType() == StrokeType.CENTERED) {
                indDeltaWidth = indDeltaWidth / 2;
            }
            double width = shapeWidth;
            double height = shapeHeight;

//            System.err.println("ShapeNodeFraming: shapeHeight = " + shapeHeight);
//            System.err.println("ShapeNodeFraming: shapeWidth = " + shapeWidth);

            if (strokeType == StrokeType.OUTSIDE) {
                width += strokeWidth * 2;
                height += strokeWidth * 2;
            }
            if (strokeType == StrokeType.CENTERED) {
                width += (strokeWidth / 2);
                height += (strokeWidth / 2);
            }

            ((Rectangle) nShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).subtract(width).divide(2)));
            ((Rectangle) nShape).yProperty().bind(ind.yProperty().subtract(height + offset - 1));

            if (shape == nShape) {
                ((Rectangle) nShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).subtract(width).divide(2)));
                ((Rectangle) nShape).yProperty().bind(ind.yProperty().subtract(height + offset - 1));
            } else if (shape == neShape) {
                ((Rectangle) neShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset + 1)));
                ((Rectangle) neShape).yProperty().bind(ind.yProperty().subtract(height + offset - 1));

            } else if (shape == eShape) {
                ((Rectangle) eShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset + 1)));
                ((Rectangle) eShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).subtract(height).divide(2)));
            } else if (shape == seShape) {
                ((Rectangle) seShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset + 1)));
                ((Rectangle) seShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset + 1)));

            } else if (shape == sShape) {
                ((Rectangle) sShape).xProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).subtract(width).divide(2)));
                ((Rectangle) sShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset + 1)));

            } else if (shape == swShape) {
                ((Rectangle) swShape).xProperty().bind(ind.xProperty().subtract(width + indDeltaWidth + offset - 1));
                ((Rectangle) swShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset + 1)));

            } else if (shape == wShape) {
                ((Rectangle) wShape).xProperty().bind(ind.xProperty().subtract(width + indDeltaWidth + offset - 1));
                ((Rectangle) wShape).yProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).subtract(shapeHeight).divide(2)));

            } else if (shape == nwShape) {
                ((Rectangle) nwShape).xProperty().bind(ind.xProperty().subtract(width + indDeltaWidth + offset - 1));
                ((Rectangle) nwShape).yProperty().bind(ind.yProperty().subtract(height + offset - 1));

            }
        }

        /*        @Override
        protected void setPosition(Shape shape) {
            Rectangle ind = framing.getIndicator();
            if (shape == nShape) {
                ((Rectangle) nShape).setX(ind.getX() + (ind.getWidth() - shapeWidth) / 2);
                ((Rectangle) nShape).setY(ind.getY() - shapeHeight);
                setSize(nShape);
            } else if (shape == neShape) {
                ((Rectangle) neShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
                ((Rectangle) neShape).setY(ind.getY() - shapeHeight);
                setSize(neShape);

            } else if (shape == eShape) {
                ((Rectangle) eShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
                ((Rectangle) eShape).setY(ind.getY() - (ind.getHeight() - shapeHeight) / 2);
                setSize(eShape);
            } else if (shape == seShape) {
                ((Rectangle) seShape).setX(ind.getX() + ind.getWidth() - shapeWidth);
                ((Rectangle) seShape).setY(ind.getY() - +ind.getHeight());
                setSize(seShape);

            } else if (shape == sShape) {
                ((Rectangle) sShape).setX(ind.getX() + (ind.getWidth() - shapeWidth) / 2);
                ((Rectangle) sShape).setY(ind.getY() - ind.getHeight());
                setSize(sShape);

            } else if (shape == swShape) {
                ((Rectangle) swShape).setX(ind.getX() - shapeWidth);
                ((Rectangle) swShape).setY(ind.getY() - +ind.getHeight());
                setSize(swShape);
            } else if (shape == wShape) {
                ((Rectangle) wShape).setX(ind.getX() - shapeWidth);
                ((Rectangle) wShape).setY(ind.getY() - ind.getHeight());
                setSize(wShape);
            } else if (shape == nwShape) {
                ((Rectangle) nwShape).setX(ind.getX() - shapeWidth);
                ((Rectangle) nwShape).setY(ind.getY() - shapeHeight);
                setSize(nwShape);

            }

        }
         */
        @Override
        protected void unbind(Shape shape) {
            ((Rectangle) shape).xProperty().unbind();
            ((Rectangle) shape).yProperty().unbind();
        }

        @Override
        protected void createShapes() {
            createShapes(Rectangle.class);
        }

    }//class rectangleShape

    public static class IndicatorCircle extends IndicatorShape {

        // protected double shapeWidth = 3;
        // protected double shapeHeight = 3;
        protected double radius = 1.5;

        public IndicatorCircle(ShapeNodeFraming framing, double radius) {
            super(framing, radius);
            this.radius = radius;
            setOffset(0);

        }

        public IndicatorCircle(ShapeNodeFraming framing) {
            this(framing, 1.5);
            this.radius = radius;

        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        @Override
        protected void initShape(Shape shape) {
            ((Circle) shape).setCenterX(20);
            ((Circle) shape).setCenterY(50);
            setSize(shape);
        }

        @Override
        protected void setSize(Shape shape) {
            ((Circle) shape).setRadius(radius);

        }

        @Override
        protected void bind(Shape shape) {
            Rectangle ind = framing.getIndicator();

            double indDeltaWidth = ind.getStrokeWidth();// - 3;

            if (ind.getStrokeType() == StrokeType.INSIDE) {
                indDeltaWidth = 0;
            } else if (ind.getStrokeType() == StrokeType.CENTERED) {
                indDeltaWidth = indDeltaWidth / 2;
            }
            double width = radius * 2;
            double height = radius * 2;

            if (strokeType == StrokeType.OUTSIDE) {
                width += strokeWidth * 2;
                height += strokeWidth * 2;
            } else if (strokeType == StrokeType.CENTERED) {
                width += (strokeWidth / 2);
                height += (strokeWidth / 2);
            }
//            System.err.println("ShapeNodeFraming: height = " + height);
//            System.err.println("ShapeNodeFraming: width = " + width);
//            System.err.println("ShapeNodeFraming: indDeltaWidth = " + indDeltaWidth);
//            System.err.println("ShapeNodeFraming: offset = " + offset);

            if (shape == nShape) {
                ((Circle) nShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).divide(2)));
                ((Circle) nShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));
            } else if (shape == neShape) {
                ((Circle) neShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) neShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));

            } else if (shape == eShape) {
                ((Circle) eShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) eShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).divide(2)));
            } else if (shape == seShape) {
                ((Circle) seShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth + offset - 1)));
                ((Circle) seShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset + 1)));

            } else if (shape == sShape) {
                ((Circle) sShape).centerXProperty().bind(ind.xProperty().add(ind.widthProperty().add(indDeltaWidth).divide(2)));
                ((Circle) sShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset)));

            } else if (shape == swShape) {
                ((Circle) swShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) swShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth + offset)));

            } else if (shape == wShape) {
                ((Circle) wShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) wShape).centerYProperty().bind(ind.yProperty().add(ind.heightProperty().add(indDeltaWidth).divide(2)));

            } else if (shape == nwShape) {
                ((Circle) nwShape).centerXProperty().bind(ind.xProperty().subtract(indDeltaWidth + offset - 1));
                ((Circle) nwShape).centerYProperty().bind(ind.yProperty().subtract(indDeltaWidth + offset));

            }
        }

        protected void setX(Shape shape, double x) {
            ((Circle) shape).setCenterX(x);
        }

        protected void setY(Shape shape, double y) {
            ((Circle) shape).setCenterY(y);
        }

        /*        @Override
        protected void setPosition(Shape shape) {
            //double shapeWidth = radius * 2;
            //double shapeHeight = radius * 2;
            //Rectangle ind = framing.getIndicator();
            if (shape == nShape) {
//                setX(nShape,ind.getX() + (ind.getWidth() - shapeWidth) / 2);
//                setY(nShape,ind.getY() - shapeHeight);
//                setX(nShape,-1);
//                setY(nShape,-1);
                setSize(nShape);
            } else if (shape == neShape) {
//                setX(neShape,ind.getX() + ind.getWidth() - shapeWidth);
//                setY(neShape,ind.getY() - shapeHeight);
                setSize(neShape);

            } else if (shape == eShape) {
//                setX(eShape,ind.getX() + ind.getWidth() - shapeWidth);
//                setY(eShape,ind.getY() - (ind.getHeight() - shapeHeight) / 2);
                setSize(eShape);
            } else if (shape == seShape) {
//                setX(seShape,ind.getX() + ind.getWidth() - shapeWidth);
//                setY(seShape,ind.getY() - +ind.getHeight());
                setSize(seShape);

            } else if (shape == sShape) {
//                setX(sShape,ind.getX() + (ind.getWidth() - shapeWidth) / 2);
//                setY(sShape,ind.getY() - ind.getHeight());
                setSize(sShape);

            } else if (shape == swShape) {
//                setX(swShape,ind.getX() - shapeWidth);
//                setY(sShape,ind.getY() - +ind.getHeight());
                setSize(swShape);
            } else if (shape == wShape) {
//                setX(wShape,ind.getX() - shapeWidth);
//                setY(wShape,ind.getY() - ind.getHeight());
                setSize(wShape);
            } else if (shape == nwShape) {
//                setX(nwShape,ind.getX() - shapeWidth);
//                setY(nwShape,ind.getY() - shapeHeight);
                setSize(nwShape);

            }
        }
         */
        @Override
        protected void unbind(Shape shape) {
            ((Circle) shape).centerXProperty().unbind();
            ((Circle) shape).centerYProperty().unbind();
        }

        @Override
        protected void createShapes() {
            createShapes(Circle.class);
        }

    }//class Circle

} //class 
