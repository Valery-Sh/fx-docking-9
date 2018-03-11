package org.vns.javafx.dock.api;

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
public abstract class LayoutContext {

    private ContextLookup lookup;

    private Node layoutNode;
    private String title;
    private PositionIndicator positionIndicator;


    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockLayout = true;

    protected LayoutContext(Node layoutNode) {
        this.layoutNode = layoutNode;
        init();
    }
    protected LayoutContext() {
        init();
    }

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

    protected boolean restore(Dockable dockable) {
        return false;
    }

    protected void commitDock(Node node) {
        if (node != null && DockRegistry.isDockable(node)) {
            DockableContext dockableContext = Dockable.of(node).getContext();
            //if (dockableContext.getLayoutContext() == null || dockableContext.getLayoutContext() != this) {
//            System.err.println("COMMIT DOCK TO" + this);
            if (dockableContext.getLayoutContext() != this) {
                dockableContext.setLayoutContext(this);
            }
        }
    }

    protected void inititialize() {
        DockRegistry.start();
        initListeners();
    }

    protected void initListeners() {
        if (getLayoutNode() == null) {
            return;
        }
        getLayoutNode().sceneProperty().addListener((Observable observable) -> {
            if (getLayoutNode().getScene() != null) {
                focusedDockNode.bind(getLayoutNode().getScene().focusOwnerProperty());
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
    public static Object getValue(Dockable dockable) {
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
        
        return true;
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
            LayoutContext tc =  d.getContext().getLayoutContext();
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
            d.getContext().setLayoutContext(this);
        }
    }

    public boolean isUsedAsDockLayout() {
        return usedAsDockLayout;
    }

    public void setUsedAsDockLayout(boolean usedAsDockLayout) {
        this.usedAsDockLayout = usedAsDockLayout;
    }

    public PositionIndicator getPositionIndicator() {
        if (positionIndicator == null) {
            positionIndicator = getLookup().lookup(PositionIndicator.class);
        }
        return positionIndicator;
    }
    /**
     * Returns the node for which this context was created
     * The node may throw {@code NullPointerException} in case when  the 
     * both conditions below are met:
     * <ul>
     * <li> ! (this instanceof ScenePaneContext)</li>
     * <li>layoutNode == null</li>
     * </ul>
     * @return the node for which this context was created.
    */
    public final Node getLayoutNode() {
        if ( ! (this instanceof ScenePaneContext) && layoutNode == null  ) {
            throw new NullPointerException("The property layoutNode cannot be null");
        }
        return this.layoutNode;
    }

    protected void setLayoutNode(Node layoutNode) {
        this.layoutNode = layoutNode;
    }

    protected boolean isDocked(Node node) {
        return false;
    }

    /**
     * isDocked(Node) returns true even if the node is docked to
     * the given {@code LayoutContext}
     *
     * @param tc the object of type {@code LayoutContext}
     * @param dockable the object to chack
     * @return true even if the node is docked to the given {@code LayoutContext} 
     */
    public static boolean isDocked(LayoutContext tc, Dockable dockable) {
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
        if (node != null && DockRegistry.isDockable(node)) {
            DockableContext dc = Dockable.of(node).getContext();
            
            dc.getLayoutContext().remove(node);
            dc.setLayoutContext(null);
            LayoutContext tc = dc.getLayoutContext();
            if ( tc instanceof ScenePaneContext ) {
                ((ScenePaneContext)tc).setRestoreContext(this);
            }            
            
        }
    }
    
    public void removeValue(Dockable dockable) {
        
    }
    
    public abstract void remove(Node dockNode);

}//class
