package org.vns.javafx.dock.api;

import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.dragging.DragManagerFactory;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 * Allows to monitor the state of objects of {@link Dockable} type and also
 * provides a means to change the state. Any object that implements the
 * {@link Dockable} interface provides {@link Dockable#dockableController() }
 * method that returns an object of this type.
 * <p>
 * To describe the state of an object using multiple properties and methods. An
 * object of type {@code Dockable} is in a <i>docked</i> state, if the method
 * {@link DockableController#isDocked() } returns the true.
 * </p>
 * <p>
 * In the <i>floating</i> state an object of type {@code Dockable} is
 * transformed when the method {@link DockableController#setFloating(boolean)
 * }
 * is applied to it with the parameter value equals to {#code true}.
 * </p>
 * <p>
 * The {@literal dockable} node may have a title bar. The title bar may be an
 * object of any type that extends {@code javafx.scene.layout.Region }. The
 * class provides the method {@link DockableController#createDefaultTitleBar(java.lang.String)
 * }
 * which create a default title bar of type {@link org.vns.javafx.dock.DockTitleBar
 * }. You can replace it at any time by applying the method 
 * {@link DockableController#setTitleBar(javafx.scene.layout.Region) }. If the
 * parameter of the method equals to {@code null} then the title bar will be
 * removed.
 * </p>
 * <p>
 * By default if the {@literal dockable} node has a title bar then the title bar
 * may be used to perform dragging. You can assign any node that is a children
 * of the the {@literal  dockable} node as a drag node by applying the method 
 * {@link DockableController#setDragNode(javafx.scene.Node) }
 * </p>
 *
 * @author Valery Shyshkin
 */
public class DockableController {

    private final TitleBarProperty<Region> titleBar;

    private final StringProperty title = new SimpleStringProperty("");
    private final Dockable dockable;
    private final BooleanProperty floating = new SimpleBooleanProperty(false);
    private final BooleanProperty resizable = new SimpleBooleanProperty(true);

    private boolean usedAsDockTarget = true;

    //private DragManager dragManager;
    private DragDetector dragDetector;
    //private Node dragNode;
    private ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    private DockTargetController scenePaneController;

    private boolean draggable;

    /**
     * dock target pane
     */
    private final ObjectProperty<DockTargetController> targetController = new SimpleObjectProperty<>();

    private Properties properties;

    /**
     * Create a new object for the specified {@code dockable} object.
     *
     * @param dockable the object to create an instance for
     */
    public DockableController(Dockable dockable) {
        this.dockable = dockable;
        titleBar = new TitleBarProperty(dockable.node());
        init();
    }

    private void init() {
        draggable = true;
        dragDetector = new DragDetector(this);
        //dragManager = initDragManager();
        addShowingListeners();

        scenePaneController = new ScenePaneController(dockable);
        targetController.set(scenePaneController);
        targetController.addListener(this::targetControllerChanged);
    }

    protected void addShowingListeners() {
        dockable().node().sceneProperty().addListener(this::sceneChanged);
    }

    private void sceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (newValue != null) {
            newValue.windowProperty().addListener(this::windowChanged);
        }
        if (newValue != null && newValue.getWindow() != null) {
            Platform.runLater(() -> {
                titleBar.removeListener(this::titlebarChanged);
                titleBar.addListener(this::titlebarChanged);
                //initDragManager();
            });
        }
    }

    private void windowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        if (newValue != null) {
            Platform.runLater(() -> {
                titleBar.removeListener(this::titlebarChanged);
                titleBar.addListener(this::titlebarChanged);
                //initDragManager();
            });
        }
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    /**
     * Returns a node which is a children of the {@literal dockable} node and
     * which can be used to perform dragging.
     *
     * @return a node which can be used to perform dragging. May be null.
     */
    public Node getDragNode() {
        return dragNode.get();
    }

    /**
     * Sets the specified which is a children of the {@literal dockable} node
     * and which can be used to perform dragging.
     *
     * @param dragSource the node which can be used to perform dragging. May be
     * null.
     */
    public void setDragNode(Node dragSource) {
        this.dragNode.set(dragSource);
/*        if (dragManager != null) {
            dragManager.setDragNode(dragSource);
        }
*/
    }

/*    public DragManager getDragManager() {
        return dragManager;
    }
*/
    /**
     * @return an object used as a manager when drag operation is detected.
     */
/*    protected DragManager initDragManager() {
        Window w = null;//
        if (dockable().node().getScene() != null && dockable().node().getScene().getWindow() != null) {
            w = dockable().node().getScene().getWindow();
        }
        if (w == null) {
            return null;
        }
        if (dragManager != null) {
            //17.08dragManager.removeEventHandlers(getTitleBar());
            //17.08dragManager.removeEventHandlers(getDragNode());
        }

        if ((w instanceof Stage) || (w instanceof PopupWindow)) {
            dragManager = new FxDragManager(dockable);
        } else {
            dragManager = new JFXDragManager(dockable);
        }
        System.err.println("INIT DRAG MANAGER");
        dragManager.addEventHandlers(getTitleBar());
        dragManager.addEventHandlers(getDragNode());
        return dragManager;
    }
*/
    /**
     * If {@code true} the node specified by the method {@code node()} may be
     * considered as a dock target. This means that an indicator pane which
     * allows to choose a dock place appears. If {@code false} then the node
     * can't be a dock target.
     *
     * @return true if the {@literal  dockable} cam be used as a dock target
     */
    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    /**
     * Sets a boolean value to specify whether the node defined by the method
     * {@code node()} may be considered as a dock target. If true then an
     * indicator pane which allows to choose a dock place appears. If
     * {@code false} then the node can't be a dock target.
     *
     * @param usedAsDockTarget If true then the node may be used as a dock
     * target.
     */
    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    protected void targetControllerChanged(ObservableValue<? extends DockTargetController> observable, DockTargetController oldValue, DockTargetController newValue) {
        if (newValue == null) {
            targetController.set(scenePaneController);
        }
    }

    /**
     * May be used for developer purpose.
     *
     * @return the properties collection.
     */
    public Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }

    /**
     * Return an object property which represents a title bar as a node.
     *
     * @return the property of type
     * {@link org.vns.javafx.dock.api.properties.TitleBarProperty}
     */
    public TitleBarProperty<Region> titleBarProperty() {
        return titleBar;
    }

    /**
     * Return a string property which represents a title of the
     * {@literal dockable}.
     *
     * @return the property of type {@code javafx.beans.property.StringProperty}
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Returns the title of the {@literal dockable} as a String object.
     *
     * @return the title of the {@literal dockable} as a String object.
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Sets the title of the {@literal dockable} as a String object.
     *
     * @param title a String object
     */
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Specifies a DockTargetController currently assigned to this object. The
     * controller is assigned to this object when the last is docked. When this
     * object is created then the value of type {@link ScenePaneController} is
     * assigned.
     *
     * @return an object of type {@link DockTargetController }
     */
    public ObjectProperty<DockTargetController> targetControllerProperty() {
        return targetController;
    }

    /**
     * Returns an instance of type
     * {@link org.vns.javafx.dock.api.DockTargetController}. The pane controller
     * is assigned to this object when the last is docked. When this object is
     * created then the value of type {@link ScenePaneController} is assigned.
     *
     * @return an instance of type
     * {@link org.vns.javafx.dock.api.DockTargetController}.
     */
    public DockTargetController getTargetController() {
        return targetController.get();
    }

    /**
     * Assigns the specified instance of type {@link DockTargetController }
     * to this object.
     *
     * @param targetController the value to be assigned
     */
    public void setTargetController(DockTargetController targetController) {
        this.targetController.set(targetController);
    }

    /**
     * Returns the instance of type {@link Dockable} that this controller
     * belongs to. The value is the same that was used to create this object.
     *
     * @return the instance of type {@link Dockable}
     */
    public Dockable dockable() {
        return this.dockable;
    }

    /**
     * Returns the value of {@code titleBar}.
     *
     * @return Returns the value of {@code titleBar}. May be null.
     */
    public Region getTitleBar() {
        return titleBar.get();
    }

    /**
     * Assigns the specified value of property {@code titleBar}.
     *
     * @param node the new value to be set
     */
    public void setTitleBar(Region node) {
        titleBar.set(node);
    }

    /**
     * Getter method of the floating property.
     *
     * @return an instance of floating property
     */
    public BooleanProperty floatingProperty() {
        return floating;
    }

    /**
     * Return the boolean value that indicates whether the object is in
     * <i>floating</i> state.
     *
     * @return true if the object is in <i>floating</i> state. false otherwise.
     */
    public boolean isFloating() {
        return this.floating.get();
    }

    /*    public void markFloating(boolean floating) {
        if (!isFloating() && floating) {
            //07.05 FloatWindowBuilder t = getStageBuilder();
            FloatWindowBuilder t = new FloatWindowBuilder(this);
            if (options.length > 0 && options[0]) {
                this.floating.set(floating);
                return;
            }
            t.makeFloating();
            this.floating.set(floating);
        } else if (!floating) {
            this.floating.set(floating);
        }
    }
     */
 /*    public void setFloating(boolean floating, Stage floatStage) {
        if (!isFloating() && floating) {
            //07.05 FloatWindowBuilder t = getStageBuilder();
            FloatWindowBuilder t = new FloatWindowBuilder(this);
            t.makeFloating(floatStage);
            this.floating.set(floating);
        } else if (!floating) {
            this.floating.set(floating);
        }
    }
     */
    /**
     * Transfers the object into the <i>floating</i> state. If the current value
     * of the property is {@code false} and the specified value is {@code true}
     * then start creating of a new Stage and adds the node to it's root node.
     * If the node is docked then it will be {@literal  undocked}.
     *
     * @param floating the new value to be set
     */
    public void setFloating(boolean floating) {
        this.floating.set(floating);
        /*        if (!isFloating() && floating) {
            FloatView t = FloatViewFactory.getInstance().getFloatView(dockable);
            t.make(dockable);
            this.floating.set(floating);
        } else if (!floating) {
            this.floating.set(floating);
        }
         */
    }

    /*    public void setFloatingAsPopupControl(boolean floating) {
        if (!isFloating() && floating) {
            //FloatWindowBuilder t = new FloatWindowBuilder(dockable());
            FloatView t= new FloatPopupControlView(dockable);
            t.make(dockable);
            this.floating.set(floating);
            //t.makeFloatingPopupControl();
        } else if (!floating) {
            this.floating.set(floating);
        }
    }
     */
    /**
     * Returns a new instance of the utility class that help to create a new
     * stage which serves as a floating window for the node. This window may be
     * dragged by the mouse.
     *
     * @return the new instance of type {@link FloatStageBuilder}
     */
    //public FloatWindowBuilder getStageBuilder() {
    //07.05 return getTargetController().getStageBuilder(dockable);
    //    return new FloatWindowBuilder(this);
    //}
    /**
     * Getter method of the {@code resizable} property
     *
     * @return an instance of the {@code resizable} property
     */
    public BooleanProperty resizableProperty() {
        return resizable;
    }

    /**
     * @return true if a floating window created when the node is transfered to
     * <i>floating</i> state may be resized. Otherwise returns false
     */
    public boolean isResizable() {
        return resizable.get();
    }

    /**
     * Specifies whether a floating window of the node can be resized. The
     * floating window appears when applying the method {@link DockableController#setFloating(boolean)}
     * }
     * with the parameter value equals to {@code true}.
     *
     * @param resizable if true the the window can be resized. false -
     * otherwise.
     */
    public void setResizable(boolean resizable) {
        this.resizable.set(resizable);
    }

    /**
     * The node considers to be in {@code docked} state when both conditions
     * below are {@code true}.
     * <ul>
     * <li>getTargetController() != null</li>
     * <li>getTargetController().isDocked(node)</li>
     * </ul>
     *
     * @return true if the node is in docked state
     */
    public boolean isDocked() {
        if (getTargetController() == null) {
            return false;
        }
        return getTargetController().isDocked(dockable().node());

    }

    /**
     * Creates and returns a new instance of type
     * {@link  org.vns.javafx.dock.DockTitleBar} with the specified title.
     *
     * @param title the string value of the title
     * @return a default instance of the title bar
     */
    public Region createDefaultTitleBar(String title) {
        DockTitleBar tb = new DockTitleBar(dockable());
        tb.setId("titleBar");
        tb.getLabel().textProperty().bind(this.title);
        this.title.set(title);
        titleBarProperty().set(tb);
        return tb;
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        getProperties().remove("nodeController-titlebar-minheight");
        getProperties().remove("nodeController-titlebar-minwidth");

/*        if (dragManager != null) {
            if (oldValue != null) {
                dragManager.removeEventHandlers(oldValue);
            }
            if (newValue != null) {
                dragManager.addEventHandlers(newValue);
            }
//            dragManager.titlebarChanged(ov, oldValue, newValue);
        }
*/
    }

    public class DragDetector implements EventHandler<MouseEvent> {

        private final DockableController dockableController;

        public Point2D startMousePos;
        //private Parent targetDockPane; 

        public DragDetector(DockableController dockableController) {
            this.dockableController = dockableController;
            init();
        }

        private void init() {

            dockableController.titleBarProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    oldValue.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
                if (newValue != null) {
                    newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    newValue.addEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
            });
            dockableController.dragNodeProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    oldValue.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
                if (newValue != null) {
                    newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    newValue.addEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
            });

        }

        public void mousePressed(MouseEvent ev) {
            if (!ev.isPrimaryButtonDown()) {
                return;
            }
            startMousePos = new Point2D(ev.getX(), ev.getY());
            System.err.println("DragDetected startMousePos " + startMousePos);
        }

        public void mouseDragDetected(MouseEvent ev) {
            System.err.println("DragDetector MOUSE DRAG_DETECTED");
            if (!ev.isPrimaryButtonDown()) {
                ev.consume();
                return;
            }
            if (!dockable.dockableController().isDraggable()) {
                ev.consume();
                return;
            }
            
            //targetDockPane = ((Node) ev.getSource()).getScene().getRoot();
            
            if (!dockable.dockableController().isFloating()) {
                DragManager dm = DragManagerFactory.getInstance().getDragManager(dockable);
                //dm.setStartMousePos(startMousePos);
                dm.dragDetected(ev, startMousePos);
//                targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, dm::mouseDragged );
//                targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, dm::mouseReleased);
                dockable.dockableController().setFloating(true);
                
            } else {
                System.err.println("FLOATING !!!");
                DragManager dm = DragManagerFactory.getInstance().getDragManager(dockable);
                dm.dragDetected(ev, startMousePos);                
                //dm.setStartMousePos(startMousePos);
                
//                ((Node)ev.getSource()).addEventFilter(MouseEvent.MOUSE_DRAGGED, dm::mouseDragged );
//                ((Node)ev.getSource()).addEventFilter(MouseEvent.MOUSE_RELEASED, dm::mouseReleased);
                
            }

        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePressed(ev);
            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                mouseDragDetected(ev);
            }
        }
    }//DragDetector

}//DockableController
