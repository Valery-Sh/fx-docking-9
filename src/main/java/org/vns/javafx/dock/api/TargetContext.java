package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.AbstractDockStateLoader;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public abstract class TargetContext {

    private ContextLookup lookup;

    private Node targetNode;
    private String title;
    private PositionIndicator positionIndicator;

    private AbstractDockStateLoader dockLoader;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    protected TargetContext(Node targetNode) {
        this.targetNode = targetNode;
        init();
    }
    protected TargetContext() {
        init();
    }
    

/*    protected TargetContext(Dockable dockable) {
        init();
    }
*/
    public ContextLookup getLookup() {
        if (lookup == null) {
            lookup = new DefaultContextLookup();
            initLookup(lookup);
        }
        return lookup;
    }

    private void init() {
        inititialize();
        getLookup().add(new IndicatorPopup(this));
    }

    protected void initLookup(ContextLookup lookup) {
    }

    protected abstract boolean doDock(Point2D mousePos, Node node);

    public abstract Object getRestorePosition(Dockable dockable);

    public abstract void restore(Dockable dockable, Object restoreposition);

    protected void commitDock(Node node) {
        if (DockRegistry.isDockable(node)) {
            DockableContext dockableContext = Dockable.of(node).getContext();
            if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                dockableContext.setTargetContext(this);
            }
        }
    }

    protected void inititialize() {
        DockRegistry.start();
        initListeners();
    }

    protected void initListeners() {
        if (getTargetNode() == null) {
            return;
        }
        getTargetNode().sceneProperty().addListener((Observable observable) -> {
            if (getTargetNode().getScene() != null) {
                focusedDockNode.bind(getTargetNode().getScene().focusOwnerProperty());
            }
        });

        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {
                return DockRegistry.isDockable(p);
            });
            if (newNode != null) {
                Dockable.of(newNode).getContext().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.isDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                Dockable.of(oldNode).getContext().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !Dockable.of(oldNode).getContext().titleBarProperty().isActiveChoosedPseudoClass()) {
                Dockable.of(oldNode).getContext().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    public AbstractDockStateLoader getDockLoader() {
        return dockLoader;
    }

    public void setDockLoader(AbstractDockStateLoader loader) {
        this.dockLoader = loader;
    }
    /**
     * The method is called by the object {@code DragManager } when the mouse event
     * of type {@code MOUSE_DRAGGED} is handled.
     * May be useful for example when implement animation for scroll bars. 
     * 
     * @param dockable the dragged object 
     * @param ev the object of type {@code MouseEvent }
     * 
     */
    public void mouseDragged(Dockable dockable, MouseEvent ev) {
        
    }
    protected Object getValue(Dockable dockable) {
        Object retval = null;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null ) {
             retval = dc.getValue();
        } else if (dc == null ) {
            retval = dockable;
        }
        return retval;
    }    
    public boolean isAdmissiblePosition(Dockable dockable,Point2D mousePos) {
        return true;
    }    
    public boolean isAcceptable(Dockable dockable) {

        Dockable dragged = dockable;
        Object v  = getValue(dockable);
        if ( Dockable.of(v) != null ) {
            dragged = Dockable.of(v);
        } else {
            return false;
        }
        
        return (dockLoader != null && dockLoader.isRegistered(dragged.node())) || dockLoader == null;
    }

    public void dock(Point2D mousePos, Dockable dockable) {
        Object o = getValue(dockable);
        if ( o == null || Dockable.of(o) == null ) {
            return;
        }
        
        Dockable d = Dockable.of(o);
        //
        // Test is we drag dockable or the value of a dragContainer 
        //
        if (isDocked(d.node()) && d == dockable) {
            return;
        } else if ( isDocked(d.node()) ) {
            TargetContext tc =  d.getContext().getTargetContext();
            if ( tc != null && isDocked(tc, d) ) {
                tc.undock(d.node());
            }
        } 
        
        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, d.node()) && stage != null) {
            //d.getContext().setFloating(false);
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getContext().setTargetContext(this);
        }
    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public PositionIndicator getPositionIndicator() {
        if (positionIndicator == null) {
            positionIndicator = getLookup().lookup(PositionIndicator.class);
        }
        return positionIndicator;
    }
    /**
     * Returns the node for which this context was created
     * The node may throw {@code NullPointerException) in case when 
     * the both conditions below are met:
     * <ul>
     *   <li> ! (this instanceof ScenePaneContext)</li>
     *   <li>targetNode == null</li>
     * </ul>
     * @return the node for which this context was created.
     */
    public final Node getTargetNode() {
        if ( ! (this instanceof ScenePaneContext) && targetNode == null  ) {
            throw new NullPointerException("The property targetNode cannot be null");
        }
        return this.targetNode;
    }

    protected void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    protected boolean isDocked(Node node) {
        return false;
    }

    /**
     * isDocked(Node) returns true even if the node is docked to
     * the given {@code TargetContext}
     *
     * @param tc the object of type {@code TargetContext}
     * @param dockable the object to chack
     * @return true even if the node is docked to the given {@code TargetContext} 
     */
    public static boolean isDocked(TargetContext tc, Dockable dockable) {
        Dockable d = dockable;
        DragContainer dc = dockable.getContext().getDragContainer();
        if (dc != null && dc.getValue() != null && dc.isValueDockable() ) {
             d = Dockable.of(dc.getValue());
        } else if (dc != null && dc.getValue() != null && ! dc.isValueDockable() ) {        
            return false;
        }
        return tc.isDocked(d.node());
    }

    public void undock(Node node) {
        System.err.println("TargetContext: UNDOCK node = "+ node);
        if (DockRegistry.isDockable(node)) {
            DockableContext dc = Dockable.of(node).getContext();
            dc.getTargetContext().remove(node);
            System.err.println("TargetContext: = "+ dc.getTargetContext());
            dc.setTargetContext(null);
        }
    }
    
    public void removeValue(Dockable dockable) {
        
    }
    
    public abstract void remove(Node dockNode);

}//class
