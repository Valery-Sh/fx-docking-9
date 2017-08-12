package org.vns.javafx.dock.api;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class FloatWindowBuilder {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private ObjectProperty<Window> floatingWindow = new SimpleObjectProperty<>();

    private Pane rootPane;

    private final DockableController dockableController;

    private FloatWindowResizer resizer;

    private MouseResizeHandler mouseResizeHanler;

    private DragManager priorDragmanager;
    
    private double minWidth = -1;
    private double minHeight = -1;
    
    private Cursor[] defaultCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    
        
    public FloatWindowBuilder(DockableController nodeController) {
        this.dockableController = nodeController;
        mouseResizeHanler = new MouseResizeHandler();
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }


    public MouseResizeHandler getMouseResizeHanler() {
        return mouseResizeHanler;
    }
    
    public void setDefaultCursors() {
        supportedCursors = defaultCursors;
    }

    public StageStyle getStageStyle() {
        return stageStyle;
    }

    public Pane getRootPane() {
        return rootPane;
    }

    protected void setRootPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public DockableController getDockableController() {
        return dockableController;
    }

    public FloatWindowResizer getResizer() {
        return resizer;
    }

    public void setResizer(FloatWindowResizer resizer) {
        this.resizer = resizer;
    }

    public Cursor[] getSupportedCursors() {
        return supportedCursors;
    }

    public void setStageStyle(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
    }

    public void setSupportedCursors(Cursor[] supportedCursors) {
        this.supportedCursors = supportedCursors;
    }

    public ObjectProperty<Window> stageProperty() {
        return this.floatingWindow;
    }
    
    protected ObjectProperty<Window> floatingWindowProperty() {
        return floatingWindow;
    }
    
    public Window getFloatingWindow() {
        return floatingWindow.get();
    }
    protected void markFloating(Window toMark) {
        floatingWindow.set(toMark);
    }

    protected Region node() {
        return dockableController.dockable().node();
    }

    protected Dockable dockable() {
        return dockableController.dockable();
    }

    //==========================
    //
    //==========================
    public void makeFloating() {
        if (node() == null) {
            return;
        }
        makeFloating(dockable());
    }


    public Window makeFloatingPopupControl() {
        if (node() == null) {
            return null;
        }
        return createPopupControl(dockable());
    }

/*    public void makeFloating(Stage window) {
        if (node() == null) {
            return;
        }
        if (window == null) {
            makeFloating(dockable());
        } else {
            makeFloating(dockable(), window);
        }
    }
*/
    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }


    protected void makeFloating(Dockable dockable, boolean show) {

        Region node = dockable.node();
        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.dockableController().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.dockableController().isDocked() && dockable.dockableController().getTargetController().getTargetNode() != null) {
            Window w = dockable.dockableController().getTargetController().getTargetNode().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating(dockable.node().getScene().getWindow());
                addResizer(dockable.node().getScene().getWindow(), dockable);
                dockable.dockableController().getTargetController().undock(dockable.node());
                return;
            }
        }
        if (dockable.dockableController().isDocked()) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }

        Stage newStage = new Stage();
        DockRegistry.register(newStage);
        markFloating(newStage);

        newStage.setTitle("FLOATING STAGE");
        Region lastDockPane = dockable.dockableController().getTargetController().getTargetNode();
        if (lastDockPane != null && lastDockPane.getScene() != null
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }

        newStage.initStyle(stageStyle);

        // offset the new floatingWindow to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;
        /*            if (isDecorated()) {
                Window owner = floatingWindow.getOwner();
                stagePosition = scenePoint.add(new Point2D(owner.getX(), owner.getY()));
            } else {
                stagePosition = screenPoint;
            }
            if (translation != null) {
                stagePosition = stagePosition.add(translation);
            }
         */

        BorderPane borderPane = new BorderPane();
        this.rootPane = borderPane;

        DockPane dockPane = new DockPane();
        borderPane.setStyle("-fx-background-color: aqua");
        dockPane.setStyle("-fx-background-color: blue");
        node.setStyle("-fx-background-color: green");
        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (newStage != null) {
                    newStage.close();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        //
        // Prohibit to use as a dock target
        //
        dockPane.setUsedAsDockTarget(false);
        dockPane.getItems().add(dockable.node());
        borderPane.getStyleClass().add("dock-node-border");
        borderPane.setCenter(dockPane);

        Scene scene = new Scene(borderPane);

        floatingProperty.set(true);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        newStage.setX(stagePosition.getX() - insetsDelta.getLeft());
        newStage.setY(stagePosition.getY() - insetsDelta.getTop());

        newStage.setMinWidth(borderPane.minWidth(node.getWidth()) + insetsWidth);
        newStage.setMinHeight( borderPane.minHeight(node.getHeight()) + insetsHeight);

        borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);
        System.err.println("****** node.getWidth()=" + node.getWidth());
        newStage.setScene(scene);
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer(newStage, dockable);
        newStage.sizeToScene();
        newStage.setAlwaysOnTop(true);
        if ( show ) {
            newStage.show();
        }
        dockable.node().parentProperty().addListener(pcl);
    }
    protected void makeFloating(Dockable dockable) {
        makeFloating(dockable, true);
    }
    public Stage toFloatingStage(Popup popup) {
        if (popup.getContent() == null || popup.getContent().isEmpty()) {
            return null; // ?? TO DO
        }
        if (!(popup.getContent().get(0) instanceof Region)) {
            return null;
        }

        Region node = (Region) popup.getContent().get(0);
        if (!DockRegistry.instanceOfDockable(node)) {
            return null;
        }
        Dockable dockable = DockRegistry.dockable(node);
        makeFloating(dockable);
        getFloatingWindow().setX(popup.getX());
        getFloatingWindow().setY(popup.getY());
        return (Stage) getFloatingWindow();
    }

    ///////////////
    ///////////////
    protected Window createPopupControl(Dockable dockable) {

        Region node = dockable.node();
        if (node.getScene() == null || node.getScene().getWindow() == null) {
            return null;
        }

        Window owner = node.getScene().getWindow();

        Point2D screenPoint = node.localToScreen(0, 0);
        if (screenPoint == null) {
            screenPoint = new Point2D(400, 400);
        }
        Node titleBar = dockable.dockableController().getTitleBar();
        if (titleBar != null) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.dockableController().isDocked() && dockable.dockableController().getTargetController().getTargetNode() != null) {
            Window w = dockable.dockableController().getTargetController().getTargetNode().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                markFloating((Stage) dockable.node().getScene().getWindow());
                addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
                dockable.dockableController().getTargetController().undock(dockable.node());
                return getFloatingWindow();
            }
        }

        if (dockable.dockableController().isDocked()) {
            dockable.dockableController().getTargetController().undock(dockable.node());
        }

        final PopupControl floatPopup = new PopupControl();
        
        markFloating(floatPopup);

        Point2D stagePosition = screenPoint;

        BorderPane borderPane = new BorderPane();
        this.rootPane = borderPane;

        DockPane dockPane = new DockPane();

        ChangeListener<Parent> pcl = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                if (floatPopup != null) {
                    floatPopup.hide();
                }
                dockable.node().parentProperty().removeListener(this);
            }
        };

        //
        // Prohibit to use as a dock target
        //
        dockPane.setUsedAsDockTarget(false);
        dockPane.getItems().add(dockable.node());
        borderPane.getStyleClass().add("dock-node-border");
        borderPane.setCenter(dockPane);


        floatingProperty.set(true);

        node.applyCss();
        borderPane.applyCss();

        Insets insetsDelta = borderPane.getInsets();

        double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
        double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();

        floatPopup.setX(stagePosition.getX() - insetsDelta.getLeft());
        floatPopup.setY(stagePosition.getY() - insetsDelta.getTop());

        floatPopup.setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        floatPopup.setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);
        borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);


        floatPopup.getScene().setRoot(borderPane);

        borderPane.setStyle("-fx-background-color: aqua");
        dockPane.setStyle("-fx-background-color: blue");
        node.setStyle("-fx-background-color: green");
        floatPopup.setOnShown( e -> {
            DockRegistry.register(floatPopup);
        });
        floatPopup.setOnHidden(e -> {
            DockRegistry.unregister(floatPopup);
        });
        
        floatPopup.show(owner);
        dockable.node().parentProperty().addListener(pcl);
        addResizer(floatPopup, dockable);
        System.err.println("************** +++++++++++ *************");
        return floatPopup;
    }//makeFloatingPopupControl

    protected void addResizer(Stage stage, Dockable dockable) {
        stage.setResizable(dockable.dockableController().isResizable());
        if (stage.isResizable()) {
            removeListeners(dockable);
            addListeners(stage);
        }
        resizer = new FloatWindowResizer(this);
        
    }

    public void addResizer(Window window, Dockable dockable) {
        if ( dockable.dockableController().isResizable()) {
            removeListeners(dockable);
            addListeners(window);
            
        }
        //resizer = new FloatWindowResizer();
        resizer = new FloatWindowResizer(this);
    }

    protected void addListeners(Window stage) {
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
    }

    public void removeListeners(Dockable dockable) {
        if ( dockable.node().getScene() == null ||dockable.node().getScene().getWindow() == null ) {
            return;
        }
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);

        dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);

    }

    /*    public void pressedHandle(MouseEvent ev) {
        resizer.start(ev, getFloatingWindow(), getFloatingWindow().getScene().getCursor(), supportedCursors);
    }

    public void movedHandle(MouseEvent ev) {
        Cursor c = FloatWindowResizer.cursorBy(ev, rootPane,supportedCursors);
        getFloatingWindow().getScene().setCursor(c);

        if (!c.equals(Cursor.DEFAULT)) {
            ev.consume();
        }
    }

    public void draggedHandle(MouseEvent ev) {
        resizer.resize(ev);
    }
     */
    protected void dock(Pane dockPane, Dockable dockable) {
        Node node = dockable.node();
        DockSplitPane rootSplitPane = null;
        rootSplitPane = new DockSplitPane();
        //-rootSplitPane = new HSplit();
        //!!!!!! old dockPane.setRootSplitPane(rootSplitPane);
        rootSplitPane.getItems().add(node);

        dockPane.getChildren().add(rootSplitPane);
        //
        // Actually it's docken/ Return later. May be implement 
        // docked state extended
        //
        // >>> node.getDockableState().setDocked(true);
    }

    private BooleanProperty floatingProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            Node node = node();
            node.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            if (rootPane != null) {
                rootPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("floating"), get());
            }
        }

        @Override
        public String getName() {
            return "floating";
        }
    };

    public class MouseResizeHandler implements EventHandler<MouseEvent> {

        private boolean cursorSupported = false;

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                Cursor c = FloatWindowResizer.cursorBy(ev, getRootPane());
                if (!isCursorSupported(c)) {
                    getFloatingWindow().getScene().setCursor(Cursor.DEFAULT);
                } else {
                    getFloatingWindow().getScene().setCursor(c);
                }
                if (!c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Cursor c = FloatWindowResizer.cursorBy(ev, getRootPane());
                cursorSupported = isCursorSupported(c);
                if (!cursorSupported) {
                    getFloatingWindow().getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                getResizer().start(ev, getFloatingWindow(), getFloatingWindow().getScene().getCursor(), getSupportedCursors() );
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (!cursorSupported) {
                    return;
                }
                if (getResizer().getStage() == null) {
                    getResizer().start(ev, getFloatingWindow(), getFloatingWindow().getScene().getCursor(), getSupportedCursors() );
                } else {
                    Platform.runLater(() -> {
                        getResizer().resize(ev);
                    });
                    
                }
            }
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

    }//class MouseResizeHandler

}//class FloatWindowBuilder
