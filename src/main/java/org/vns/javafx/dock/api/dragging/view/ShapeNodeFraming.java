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

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
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
import javafx.scene.shape.StrokeType;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.dragging.view.IndicatorShape.IndicatorCircle;
import org.vns.javafx.dock.api.dragging.view.IndicatorShape.IndicatorRectangle;

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
            indicatorShape = new IndicatorCircle(this);
        }

        indicator.setFill(Color.TRANSPARENT);
        indicator.setStrokeType(StrokeType.INSIDE);
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
        //indicatorShape.setPosition();

        indicatorShape.bind();

        root = getNode().getScene().getRoot();
        Bounds pb = getNode().getBoundsInParent();
        
        Bounds sceneBounds = getNode().localToScene(getNode().getLayoutBounds());
        getNode().getScene().getRoot().layoutXProperty();
//        System.err.println("sceneBounds = " + sceneBounds);

        indicator.setX(sceneBounds.getMinX() - insetsLeft);
        indicator.setY(sceneBounds.getMinY() - insetsTop);
        indicator.setWidth(sceneBounds.getWidth() + insetsWidth);
        indicator.setHeight(sceneBounds.getHeight() + insetsHeight);


        boundsInParentListener =  (o, ov, nv) -> {
            //System.err.println("ShapeNodeFraming: boundsInParentListener");
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

    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }
    
    public Cursor getSaveCursor() {
        return saveCursor;
    }
    public void setSaveCursor(Cursor cursor) {
        this.saveCursor = cursor;
    }

    public boolean isCursorSupported() {
        return cursorSupported;
    }

    public void setCursorSupported(boolean cursorSupported) {
        this.cursorSupported = cursorSupported;
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


    
}// class ShapeNodeFraming
