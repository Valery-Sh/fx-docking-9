package org.vns.javafx.dock.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.vns.javafx.dock.DockPane;

/**
 *
 * @author Valery
 */
public class FloatStageBuilder {

    private StageStyle stageStyle = StageStyle.TRANSPARENT;

    private ObjectProperty<Stage> stageProperty = new SimpleObjectProperty<>();

    private Pane rootPane;

    private final DockNodeHandler nodeHandler;

    private ResizeTransformer resizer;

    private MouseResizeHandler mouseResizeHanler;

    private Cursor[] defaultCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    private Cursor[] supportedCursors = new Cursor[]{
        Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE,
        Cursor.SE_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.NW_RESIZE
    };

    public FloatStageBuilder(DockNodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
        mouseResizeHanler = new MouseResizeHandler();
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

    public void setRootPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public DockNodeHandler getNodeHandler() {
        return nodeHandler;
    }

    public ResizeTransformer getResizer() {
        return resizer;
    }

    public void setResizer(ResizeTransformer resizer) {
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

    public ObjectProperty<Stage> stageProperty() {
        return this.stageProperty;
    }

    public Stage getStage() {
        return stageProperty.get();
    }

    protected Region node() {
        return nodeHandler.node();
    }

    protected Dockable dockable() {
        return nodeHandler.dockable();
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

    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }

    protected void makeFloating(Dockable dockable) {

        Region node = dockable.node();
        Point2D screenPoint = node.localToScreen(0, 0);
        Node titleBar = dockable.nodeHandler().getTitleBar();
        if ( titleBar != null ) {
            titleBar.setVisible(true);
            titleBar.setManaged(true);
        }

        if (dockable.nodeHandler().isDocked() && dockable.nodeHandler().getPaneHandler().getDockPane() != null) {
            Window w = dockable.nodeHandler().getPaneHandler().getDockPane().getScene().getWindow();
            if (dockable.node().getScene().getWindow() != w) {
                rootPane = (Pane) dockable.node().getScene().getRoot();
                stageProperty.set((Stage) dockable.node().getScene().getWindow());
                addResizer((Stage) dockable.node().getScene().getWindow(), dockable);
                dockable.nodeHandler().getPaneHandler().undock(dockable.node());
                return;
            }
        }
        if (dockable.nodeHandler().isDocked()) {
            dockable.nodeHandler().getPaneHandler().undock(dockable.node());
        }
        
        Stage newStage = new Stage();
        DockRegistry.register(newStage);
        stageProperty.set(newStage);

        newStage.setTitle("FLOATING STAGE");
        Region lastDockPane = dockable.nodeHandler().getPaneHandler().getDockPane();
        if (lastDockPane != null && lastDockPane.getScene() != null        
                && lastDockPane.getScene().getWindow() != null) {
            newStage.initOwner(lastDockPane.getScene().getWindow());
        }
        
        newStage.initStyle(stageStyle);

        // offset the new stage to cover exactly the area the dock was local to the scene
        // this is useful for when the user presses the + sign and we have no information
        // on where the mouse was clicked
        Point2D stagePosition = screenPoint;
        /*            if (isDecorated()) {
                Window owner = stage.getOwner();
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
        //
        // Prohibit to use as a dock target
        //
        dockPane.paneHandler().setUsedAsDockTarget(false);

        dockPane.dock(dockable, Side.TOP);
        borderPane.getStyleClass().add("dock-node-border");

        dock(dockPane, dockable);
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

        newStage.setMinWidth(borderPane.minWidth(node.getHeight()) + insetsWidth);
        newStage.setMinHeight(borderPane.minHeight(node.getWidth()) + insetsHeight);

        borderPane.setPrefSize(node.getWidth() + insetsWidth, node.getHeight() + insetsHeight);
        //borderPane.setMouseTransparent(true);
        newStage.setScene(scene);
        if (stageStyle == StageStyle.TRANSPARENT) {
            scene.setFill(null);
        }
        addResizer(newStage, dockable);
        newStage.sizeToScene();
        newStage.setAlwaysOnTop(true);
        newStage.show();
    }

    protected void addResizer(Stage stage, Dockable dockable) {
        stage.setResizable(dockable.nodeHandler().isResizable());
        if (stage.isResizable()) {
            addListeners(stage);        
        }
        resizer = new ResizeTransformer();
    }

    protected void addListeners(Stage stage) {
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
    }

    public void removeListeners(Dockable dockable) {
        ((Stage) dockable.node().getScene().getWindow()).removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);
        ((Stage) dockable.node().getScene().getWindow()).removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseResizeHanler);        
        ((Stage) dockable.node().getScene().getWindow()).removeEventFilter(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        ((Stage) dockable.node().getScene().getWindow()).removeEventHandler(MouseEvent.MOUSE_MOVED, mouseResizeHanler);
        
        ((Stage) dockable.node().getScene().getWindow()).removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        ((Stage) dockable.node().getScene().getWindow()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseResizeHanler);
        
    }

    /*    public void pressedHandle(MouseEvent ev) {
        resizer.start(ev, getStage(), getStage().getScene().getCursor(), supportedCursors);
    }

    public void movedHandle(MouseEvent ev) {
        Cursor c = ResizeTransformer.cursorBy(ev, rootPane,supportedCursors);
        getStage().getScene().setCursor(c);

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
        SplitDelegate.DockSplitPane rootSplitPane = null;
        rootSplitPane = new SplitDelegate.DockSplitPane();

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
                Cursor c = ResizeTransformer.cursorBy(ev, getRootPane());                
                if ( ! isCursorSupported(c) ) {
                    getStage().getScene().setCursor(Cursor.DEFAULT);
                } else {
                    getStage().getScene().setCursor(c);
                }
                if ( !c.equals(Cursor.DEFAULT)) {
                    ev.consume();
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                /*System.err.println("FloatStage pressed");            
                System.err.println("Mouse pressed stage=" + getStage());            
                System.err.println("Mouse pressed tb.width =" + nodeHandler.getTitleBar().getWidth());                
                System.err.println("Mouse pressed tb.getX,Y =" + nodeHandler.getTitleBar().localToScene(0, 0));                                
*/              
                //nodeHandler.getTitleBar().getWidth();
                Cursor c = ResizeTransformer.cursorBy(ev, getRootPane());                
/*                System.err.println("Mouse pressed stage.width =" + getStage().getWidth());
                System.err.println("Mouse pressed stage.height=" + getStage().getHeight());                            
                System.err.println("==============================================");                            
*/                
                cursorSupported = isCursorSupported(c);
                if ( ! cursorSupported ) {
                    getStage().getScene().setCursor(Cursor.DEFAULT);
                    return;
                }
                
                getResizer().start(ev, getStage(), getStage().getScene().getCursor(), getSupportedCursors());
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                //System.err.println("FloatStage dragged");
                //Cursor c = ResizeTransformer.cursorBy(ev, getRootPane());                
                if ( ! cursorSupported) {
                    return;
                }
                if ( getResizer().getStage() == null ) {
                    getResizer().start(ev, getStage(), getStage().getScene().getCursor(), getSupportedCursors());                    
                } else {
                    getResizer().resize(ev);
                }
            }
        }
        public boolean isCursorSupported(Cursor cursor) {
            if ( cursor == null || cursor == Cursor.DEFAULT ) {
                return false;
            }
            boolean retval = false;
            for ( Cursor c : getSupportedCursors() ) {
                if ( c == cursor ) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

    }//class MouseResizeHandler

}//class FloatStageBuilder
