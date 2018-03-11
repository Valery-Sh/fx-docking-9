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
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class ShapeNodeFraming implements NodeFraming {//, EventHandler<MouseEvent> {

    private Rectangle indicator;
    private Line topLine;
    private Line rightLine;
    private Line bottomLine;
    private Line leftLine;

    private ShapeNodeResizeExecutor resizeExecutor;

    private Node root;

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

    protected ShapeNodeFraming() {
        super();
        nodeProperty().addListener((v, ov, nv) -> {
            if (ov != null) {
                ov.layoutBoundsProperty().removeListener(this::layoutBoundsChanged);
            }
        });
    }

    protected void initWindow() {
    }

    private void init() {
        workHeight.set(-1);
        workHeight.set(-1);
        workX.set(-1);
        workY.set(-1);

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

    protected void createWindow() {
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
            if (!((Pane) getNode().getScene().getRoot()).getChildren().contains(indicator)) {
                //((Pane) getNode().getScene().getRoot()).getChildren().remove(indicator);
            }
        } else {
            indicator = new Rectangle(50, 20);    
            topLine = new Line(50,20,60,20 );
        }

        
        indicator.setFill(Color.TRANSPARENT);
        indicator.setStrokeType(StrokeType.OUTSIDE);
        indicator.setStroke(Color.rgb(255,148,40));
        indicator.setStrokeWidth(3);
        indicator.setX(20);
        indicator.setY(50);
        indicator.setManaged(false);
        //indicator.setMouseTransparent(true);
        
        if (!((Pane) getNode().getScene().getRoot()).getChildren().contains(indicator)) {
            ((Pane) getNode().getScene().getRoot()).getChildren().add(indicator);
        }

        indicator.toFront();
        Insets insetsDelta = ((Region) region).getInsets();
        insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
        insetsTop = 0;
        insetsLeft = 0;

        root = getNode().getScene().getRoot();
        getNode().layoutBoundsProperty().addListener(this::layoutBoundsChanged);

        Bounds sceneBounds = getNode().localToScene(getNode().getLayoutBounds());

        indicator.setX(sceneBounds.getMinX() - insetsLeft);
        indicator.setY(sceneBounds.getMinY() - insetsTop);
        indicator.setWidth(sceneBounds.getWidth() + insetsWidth);
        indicator.setHeight(sceneBounds.getHeight() + insetsHeight);
        topLine.startXProperty().bind(indicator.xProperty());
        topLine.startYProperty().bind(indicator.yProperty());
        topLine.endXProperty().bind(indicator.xProperty().add(indicator.widthProperty().subtract(1)));
        topLine.startYProperty().bind(indicator.yProperty());
        
        getNode().layoutYProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScene(getNode().getLayoutBounds());
            indicator.setY(sb.getMinY() - insetsTop);
        }
        );

        getNode().layoutXProperty().addListener((o, ov, nv) -> {
            Bounds sb = getNode().localToScene(getNode().getLayoutBounds());
            indicator.setX(sb.getMinX() - insetsLeft);
        });

        indicator.widthProperty()
                .bind(workWidth.add(borderWidth));
        indicator.heightProperty()
                .bind(workHeight.add(borderHeight));

        sceneBounds = getNode().localToScene(getNode().getLayoutBounds());

        setWorkWidth(sceneBounds.getWidth());
        setWorkHeight(sceneBounds.getHeight());

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
        if (indicator != null) {
            removeWindowListeners();
        }

        createWindow();

        this.node.set(node);
        init();
        this.show();
    }

    protected void show() {

        resizeExecutor = new ShapeNodeResizeExecutor(indicator, (Region) getNode());

/*        indicator.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
        indicator.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        indicator.addEventFilter(MouseEvent.MOUSE_MOVED, this);
        indicator.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
*/        
        indicator.setVisible(true);
        topLine.setVisible(true);
    }

    protected void removeWindowListeners() {
/*        indicator.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        indicator.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
*/
    }

    //@Override
    public void handle(MouseEvent ev) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {

            Point2D pt = indicator.screenToLocal(ev.getScreenX(), ev.getScreenY());
            System.err.println("");
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
            if ( true) {
                return;
            }
            if (!indicator.getScene().getRoot().contains(ev.getX(), ev.getY())) {
                removeWindowListeners();
                hide();
//                System.err.println("indicator.getScene().getRoot().contains(ev.getX(), ev.getY()) = " + indicator.getScene().getRoot().contains(ev.getX(), ev.getY()));
                return;
            }
            Bounds ib = indicator.getBoundsInLocal();
            saveCursor = ShapeNodeResizeExecutor.cursorBy(ev.getX() - ib.getMinX(), ev.getY() - ib.getMinY(), indicator);
            if (!applyTranslateXY) {
                translateX = getNode().getTranslateX();
                translateY = getNode().getTranslateY();
            }

            cursorSupported = isCursorSupported(saveCursor);
            if (!cursorSupported) {
                indicator.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            resizeExecutor.start(ev, this, indicator.getScene().getCursor(), getSupportedCursors());
        } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (!cursorSupported) {
                return;
            }
            if (!resizeExecutor.isStarted()) {
                resizeExecutor.start(ev, this, indicator.getScene().getCursor(), getSupportedCursors());
            } else {
                Platform.runLater(() -> {
//                    System.err.println("MOUSE DRAGGED RESIZE");
                    resizeExecutor.resize(ev);
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

    public void hide() {
        if (indicator != null) {
            indicator.setVisible(false);
            topLine.setVisible(true);
        }
    }

    public boolean isShowing() {
        return indicator != null && indicator.isDisable();
    }

    public boolean isApplyTranslateXY() {
        return applyTranslateXY;
    }

    public void setApplyFtranslateXY(boolean useTranslateXY) {
        this.applyTranslateXY = useTranslateXY;
    }

    public ShapeNodeResizeExecutor getResizeExecutor() {
        return resizeExecutor;
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

    public static ShapeNodeFraming getInstance() {
        return ShapeNodeFraming.SingletonInstance.instance;

    }

    private static class SingletonInstance {
        private static final ShapeNodeFraming instance = new ShapeNodeFraming();
    }

}
