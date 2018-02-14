package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.dragging.DragManager;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.dragging.DragManagerFactory;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 * Allows to monitor the state of objects of {@link Dockable} type and also
 * provides a means to change the state. Any object that implements the
 * {@link Dockable} interface provides {@link Dockable#getContext() }
 * method that returns an object of this type.
 * <p>
 * To describe the state of an object using multiple properties and methods. An
 * object of type {@code Dockable} is in a <i>docked</i> state, if the method
 * {@link DockableContext#isDocked() } returns the true.
 * </p>
 * <p>
 * In the <i>floating</i> state an object of type {@code Dockable} is
 * transformed when the method {@link DockableContext#setFloating(boolean)
 * }
 * is applied to it with the parameter value equals to {#code true}.
 * </p>
 * <p>
 * The {@literal dockable} node may have a title bar. The title bar may be an
 * object of any type that extends {@code javafx.scene.layout.Region }. The
 * class provides the method {@link DockableContext#createDefaultTitleBar(java.lang.String)
 * }
 * which create a default title bar of type {@link org.vns.javafx.dock.DockTitleBar
 * }. You can replace it at any time by applying the method 
 * {@link DockableContext#setTitleBar(javafx.scene.layout.Region) }. If the
 * parameter of the method equals to {@code null} then the title bar will be
 * removed.
 * </p>
 * <p>
 * By default if the {@literal dockable} node has a title bar then the title bar
 * may be used to perform dragging. You can assign any node that is a children
 * of the the {@literal  dockable} node as a drag node by applying the method 
 * {@link DockableContext#setDragNode(javafx.scene.Node) }
 * </p>
 *
 * @author Valery Shyshkin
 */
public class DockableContext {

    private DragManager dragManager;

    private ContextLookup lookup;

    private final TitleBarProperty<Region> titleBar;

    private final StringProperty title = new SimpleStringProperty("");
    private final Dockable dockable;

    private DragContainer dragContainer;

    private final BooleanProperty floating = new SimpleBooleanProperty(false);
    private final BooleanProperty resizable = new SimpleBooleanProperty(true);

    private boolean usedAsDockTarget = true;

    private DragDetector dragDetector;
    //private Node dragNode;
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    private TargetContext scenePaneContext;

    private boolean draggable;

    /**
     * dock target pane
     */
    private final ObjectProperty<TargetContext> targetContext = new SimpleObjectProperty<>();

    private Properties properties;

    /**
     * Create a new object for the specified {@code dockable} object.
     *
     * @param dockable the object to create an instance for
     */
    public DockableContext(Dockable dockable) {
        this.dockable = dockable;
        titleBar = new TitleBarProperty(dockable.node());
        lookup = new DefaultContextLookup();
        init();
    }

    private void init() {
        draggable = true;
        dragDetector = new DragDetector(this);
        getLookup().putUnique(MouseDragHandler.class, new DefaultMouseDragHandler(this));

        addShowingListeners();

        scenePaneContext = new ScenePaneContext(dockable);
        targetContext.set(scenePaneContext);
        targetContext.addListener(this::targetContextChanged);
        getLookup().add(new FloatViewFactory());
        getLookup().putUnique(DragManagerFactory.class, new DragManagerFactory());

    }

    public ContextLookup getLookup() {
        if (lookup == null) {
            lookup = new DefaultContextLookup();
            initLookup(lookup);
        }
        return lookup;
    }

    protected void initLookup(ContextLookup lookup) {

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
    }

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

    protected void targetContextChanged(ObservableValue<? extends TargetContext> observable, TargetContext oldValue, TargetContext newValue) {
        if (newValue == null) {
            targetContext.set(scenePaneContext);
        } else {
            //
            // The drag manager may be changed 
            //
            DragManagerFactory dmf = newValue.getLookup().lookup(DragManagerFactory.class);
            if (dmf != null) {
                dragManager = dmf.getDragManager(dockable);
            }
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
     * Specifies a TargetContext currently assigned to this object. The context
     * is assigned to this object when the last is docked. When this object is
     * created then the value of type {@link ScenePaneContext} is assigned.
     *
     * @return an object of type {@link TargetContext }
     */
    public ObjectProperty<TargetContext> targetContextProperty() {
        return targetContext;
    }

    /**
     * Returns an instance of type
     * {@link org.vns.javafx.dock.api.TargetContext}. The pane context is
     * assigned to this object when the last is docked. When this object is
     * created then the value of type {@link ScenePaneContext} is assigned.
     *
     * @return an instance of type
     * {@link org.vns.javafx.dock.api.TargetContext}.
     */
    public TargetContext getTargetContext() {
        return targetContext.get();
    }

    /**
     * Assigns the specified instance of type {@link TargetContext }
     * to this object.
     *
     * @param targetContext the value to be assigned
     */
    public void setTargetContext(TargetContext targetContext) {
        this.targetContext.set(targetContext);
    }

    /**
     * Returns the instance of type {@link Dockable} that this context belongs
     * to. The value is the same that was used to create this object.
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

    /*    public MouseDragHandler getMouseDragHandler() {
        return dragDetector.getDragHandler();
    }
     */
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
        boolean retval = false;
        DragContainer dc = getDragContainer();
        if (dc != null && dc.getPlaceholder() != null && FloatView.isFloating(dc.getPlaceholder())) {
            retval = true;
        } else {
            retval = FloatView.isFloating(dockable().node());
        }
        return retval;
    }

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
    }

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
     * floating window appears when applying the method {@link DockableContext#setFloating(boolean)}
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
     * <li>getTargetContext() != null</li>
     * <li>getTargetContext().isDocked(node)</li>
     * </ul>
     *
     * @return true if the node is in docked state
     */
    public boolean isDocked() {
        if (getTargetContext() == null) {
            return false;
        }
        if (dockable instanceof DragContainer) {
            return false;
        }

        Dockable d = dockable;
        if (getDragContainer() != null && getDragContainer().getValue() != null) {
            if (!getDragContainer().isValueDockable()) {
                return false;
            }
            d = Dockable.of(getDragContainer().getValue());
        }

        return getTargetContext().isDocked(d.node());

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
    }

    /**
     * Returns the object which manages a dragging execution. Can't be changed
     * during dragging.
     *
     * @return the object which manages a dragging execution
     */
    public DragManager getDragManager() {
        return getDragManager(true);
    }

    protected DragManager getDragManager(boolean create) {
        DragManager retval = dragManager;
        if (create || retval == null) {
            createDragManager();
            retval = dragManager;
        }
        return retval;
    }

    /**
     * Creates the object which manages a dragging execution. Can't be changed
     * during dragging.
     */
    public void createDragManager() {

        DragManagerFactory dmf = null;

        TargetContext tc = dockable.getContext().getTargetContext();

        if (tc != null) {
            dmf = tc.getLookup().lookup(DragManagerFactory.class);
            if (dmf == null) {
                dmf = dockable.getContext().getLookup().lookup(DragManagerFactory.class);
            }
        } else {
            dmf = dockable.getContext().getLookup().lookup(DragManagerFactory.class);
        }
        dragManager = dmf.getDragManager(dockable);
    }

    /**
     * Returns the object which is an actual object to be docked
     *
     * @return the object which is an actual object to be docked
     */
    public DragContainer getDragContainer() {
        return dragContainer;
    }

    /**
     * Sets the object which is an actual object to be docked
     *
     * @param dragContainer the actual object to be docked
     */
    public void setDragContainer(DragContainer dragContainer) {
        this.dragContainer = dragContainer;

    }

    public Object getDragValue() {
        Object retval = null;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null) {
            retval = dc.getValue();
        } else {
            retval = dockable;
        }
        Dockable dd = Dockable.of(retval);
        return retval;
    }

    /*    public void setDragValue(Object value) {
        this.dragValue = value;
    }
     */
    public class DragDetector implements EventHandler<MouseEvent> {

        private final DockableContext dockableContext;

        private MouseDragHandler dragHandler;

        public DragDetector(DockableContext dockableContext) {
            this.dockableContext = dockableContext;
            init();
        }

        private void init() {
            dockableContext.titleBarProperty().addListener((ov, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    oldValue.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
                if (newValue != null) {
                    newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    newValue.addEventHandler(MouseEvent.DRAG_DETECTED, this);
                }
            });
            dockableContext.dragNodeProperty().addListener((ov, oldValue, newValue) -> {
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

        @Override
        public void handle(MouseEvent event) {
            dragHandler = getLookup().lookup(MouseDragHandler.class);
            if (dragHandler == null) {
                dragHandler = new DefaultMouseDragHandler(dockableContext);
            }
            dragHandler.handle(event);
        }

    }//DragDetector

}//DockableContext
