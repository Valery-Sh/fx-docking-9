package org.vns.javafx.dock.api;

import java.util.Properties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 * Allows to monitor the state of objects of {@link Dockable} type 
 * and also provides a means to change the state.
 * Any object that implements the {@link Dockable} interface provides 
 * {@link Dockable#dockableController() } method that returns an object of this type.
 * <p>
 * To describe the state of an object using multiple properties and methods. 
 * An object of type {@code Dockable} is in a <i>docked</i> state, if the method
 * {@link DockableController#isDocked() } returns the true.
 * </p>
 * <p>
 * In the <i>floating</i> state an object of type {@code Dockable} is transformed
 * when the method {@link DockableController#setFloating(boolean) }  
 * is applied to it with the parameter value equals to {#code true}.
 * </p>
 * <p>
 *   The {@literal dockable} node may have a title bar. The title bar may be an 
 * object of any type that extends {@code javafx.scene.layout.Region }. The class
 * provides the method {@link DockableController#createDefaultTitleBar(java.lang.String) }
 * which create a default title bar of type {@link org.vns.javafx.dock.DockTitleBar }.
 * You can replace it at any time by applying the method 
 * {@link DockableController#setTitleBar(javafx.scene.layout.Region) }. 
 * If the parameter of the method equals to {@code null} then the title bar 
 * will be removed.
 * </p>
 * <p>
 *   By default if the {@literal dockable} node has a title bar then the title bar may be 
 * used to perform dragging. You can assign any node that is a children of the
 * the {@literal  dockable} node as a drag node by applying the method 
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

    private DragManager dragManager;

    private DockTargetController scenePaneController;

    /**
     * dock target pane
     */
    private final ObjectProperty<DockTargetController> targetController = new SimpleObjectProperty<>();    


    private Properties properties;
    /**
     * Create a new object for the specified {@code dockable} object.
     * @param dockable  the object to create an instance for 
     */
    public DockableController(Dockable dockable) {
        this.dockable = dockable;
        titleBar = new TitleBarProperty(dockable.node());
        init();
    }

    private void init() {
        dragManager = getDragManager();
        titleBar.addListener(this::titlebarChanged);
        scenePaneController = new ScenePaneController(dockable);
        targetController.set(scenePaneController);
        targetController.addListener(this::targetControllerChanged);
    }

    /**
     * Returns a node which is a children of the {@literal dockable} node and
     * which can be used to perform dragging.
     * 
     * @return a node which can be used to perform dragging. May be null.
     */
    public Node getDragNode() {
        return getDragManager().getDragNode();
    }
    /**
     * Sets the specified which is a children of the {@literal dockable} node and
     * which can be used to perform dragging.
     * 
     * @param dragSource the node which can be used to perform dragging. May be null.
     */
    public void setDragNode(Node dragSource) {
        getDragManager().setDragNode(dragSource);
    }
    /**
     * @return an object used as a manager when drag operation is detected.
     */
    protected DragManager getDragManager() {
        if (dragManager == null) {
            dragManager = new DragManager(dockable);
        }
        return dragManager;
    }
    /**
     * If {@code true} the node specified by the method {@code node()} may be
     * considered as a dock target. This means that an indicator pane
     * which allows to choose a dock place appears. If {@code false} then 
     * the node can't be a dock target.
     * @return true if the {@literal  dockable} cam be used as a dock target
     */
    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }
    /**
     * Sets a boolean value to specify whether the node defined by the method 
     * {@code node()} may be considered as a dock target. If true then an indicator pane
     * which allows to choose a dock place appears. If {@code false} then 
     * the node can't be a dock target.
     * 
     * @param usedAsDockTarget If true then the node may be used as a dock target.
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
     * @return the property of type {@link org.vns.javafx.dock.api.properties.TitleBarProperty}
     */
    public TitleBarProperty<Region> titleBarProperty() {
        return titleBar;
    }
    /**
     * Return a string property which represents a title of the {@literal dockable}.
     * @return the property of type {@code javafx.beans.property.StringProperty}
     */
    public StringProperty titleProperty() {
        return title;
    }
    /**
     * Returns the title of the {@literal dockable} as a String object.
     * @return the title of the {@literal dockable} as a String object.
     */
    public String getTitle() {
        return title.get();
    }
    /**
     * Sets the title of the {@literal dockable} as a String object.
     * @param title a String object 
     */    
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Specifies a DockTargetController currently assigned to this object.
     * The controller is assigned to this object when the last is docked.
     * When this object is created then the value of type {@link ScenePaneController}
     * is assigned. 
     * 
     * @return an object of type {@link DockTargetController }
     */
    public ObjectProperty<DockTargetController> targetControllerProperty() {
        return targetController;
    }
    
    /**
     * Returns an instance of type {@link org.vns.javafx.dock.api.DockTargetController}.
     * The pane controller is assigned to this object when the last is docked.
     * When this object is created then the value of type {@link ScenePaneController}
     * is assigned. 
     * 
     * @return  an instance of type {@link org.vns.javafx.dock.api.DockTargetController}.
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
     * @return Returns the value of {@code titleBar}. May be null.
     */
    public Region getTitleBar() {
        return titleBar.get();
    }
    /**
     * Assigns the specified value of property {@code titleBar}. 
     * @param node the new value to be set
     */
    public void setTitleBar(Region node) {
        titleBar.set(node);
    }
    /**
     * Getter method of the floating property.
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
    /**
     * Transfers the object into the <i>floating</i> state. 
     * If the current value of the property is {@code false}
     * and the specified value is {@code true} then start creating of a new Stage
     * and adds the node to it's root node. If the node is docked
     * then it will be {@literal  undocked}.
     * 
     * @param floating the new value to be set
     */
    public void setFloating(boolean floating) {
        if (!isFloating() && floating) {
            //07.05 FloatStageBuilder t = getStageBuilder();
            FloatStageBuilder t = new FloatStageBuilder(this);
            t.makeFloating();
            this.floating.set(floating);
        } else if (!floating) {
            this.floating.set(floating);
        }
    }
    /**
     * Returns a new instance of the utility class that help to create
     * a new stage which serves as a floating window for the node. This window
     * may be dragged by the mouse.
     * 
     * @return the new instance of type {@link FloatStageBuilder}
     */
    //public FloatStageBuilder getStageBuilder() {
        //07.05 return getTargetController().getStageBuilder(dockable);
    //    return new FloatStageBuilder(this);
    //}

    /**
     * Getter method of the {@code resizable} property
     * @return an instance of the {@code resizable} property
     */
    public BooleanProperty resizableProperty() {
        return resizable;
    }
    /**
     * @return true if a floating window created when the node is transfered
     * to <i>floating</i> state may be resized. Otherwise returns false
     */
    public boolean isResizable() {
        return resizable.get();
    }
    /**
     * Specifies whether a floating window of the node can be resized.
     * The floating window appears when applying the method {@link DockableController#setFloating(boolean) }
     * with the parameter value equals to {@code true}.
     * 
     * @param resizable if true the the window can be resized. false - otherwise.
     */
    public void setResizable(boolean resizable) {
        this.resizable.set(resizable);
    }
    /**
     * The node considers to be in {@code docked} state when both conditions below are {@code true}.
     * <ul>
     *   <li>getTargetController() != null</li>
     *   <li>getTargetController().isDocked(node)</li>
     * </ul>
     * @return true if the node is in docked state 
     */
    public boolean isDocked() {
        if ( getTargetController() == null ) {
            return false;
        }
        return getTargetController().isDocked(dockable().node());
        
    }
   
    /**
     * Creates and returns a new instance of type {@link  org.vns.javafx.dock.DockTitleBar}
     * with the specified title.
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
        getDragManager().titlebarChanged(ov, oldValue, newValue);
    }
}
