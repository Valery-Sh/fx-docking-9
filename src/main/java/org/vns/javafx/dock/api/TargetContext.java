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

    //private Node indicatorDelegate
    private AbstractDockStateLoader dockLoader;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    //private IndicatorPopup indicatorPopup;
    // private double resizeMinWidth = -1;
    // private double resizeMinHeight = -1;
    protected TargetContext(Node targetNode) {
        this.targetNode = targetNode;
        init();
    }

    protected TargetContext(Dockable dockable) {
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

    public abstract Object getRestorePosition(Dockable dockable);

    public abstract void restore(Dockable dockable, Object restoreposition);

    protected void commitDock(Node node) {
        if (DockRegistry.instanceOfDockable(node)) {
            DockableContext dockableContext = Dockable.of(node).getDockableContext();
            if (dockableContext.getTargetContext() == null || dockableContext.getTargetContext() != this) {
                dockableContext.setTargetContext(this);
            }
            //dockableContext.setFloating(false);
        }
    }

    /**
     * !!! Used only org.vns.javafx.dock.api.util.NodeTree and
     * org.vns.javafx.dock.api.util.ParentChainPopup !!! I think may be deleted
     * in the future
     *
     * @return the title
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        title = getTargetNode().getId();
        if (title == null) {
            title = getTargetNode().getClass().getName();
        }
        return title;
    }

    /*    public double getResizeMinWidth() {
        return resizeMinWidth;
    }

    protected void setResizeMinWidth(double resizeMinWidth) {
        this.resizeMinWidth = resizeMinWidth;
    }

    public double getResizeMinHeight() {
        return resizeMinHeight;
    }

    protected void setResizeMinHeight(double resizeMinHeight) {
        this.resizeMinHeight = resizeMinHeight;
    }
     */
    //protected void dividerPosChanged(Node node, double oldValue, double newValue) {}
    /**
     * !!! Used only org.vns.javafx.dock.api.util.NodeTree and
     * org.vns.javafx.dock.api.util.ParentChainPopup !!! I think may be deleted
     * in the future
     *
     * @param title the text used as a title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /*    protected IndicatorPopup createIndicatorPopup() {
        return new IndicatorPopup(DockRegistry.dockTarget(getTargetNode()));
    }

    public IndicatorPopup getIndicatorPopup() {
        if (indicatorPopup == null) {
            indicatorPopup = createIndicatorPopup();
        }
        return indicatorPopup;
    }
     */
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
                return DockRegistry.instanceOfDockable(p);
            });
            if (newNode != null) {
                Dockable.of(newNode).getDockableContext().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.instanceOfDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                Dockable.of(oldNode).getDockableContext().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !Dockable.of(oldNode).getDockableContext().titleBarProperty().isActiveChoosedPseudoClass()) {
                Dockable.of(oldNode).getDockableContext().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    public AbstractDockStateLoader getDockLoader() {
        return dockLoader;
    }

    public void setDockLoader(AbstractDockStateLoader loader) {
        this.dockLoader = loader;
    }
    
    protected Dockable getValue(Dockable dockable) {
        Dockable retval = null;
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        if (dc != null && dc.isValueDockable() ) {
             retval = Dockable.of(dc.getValue());
        } else if (dc == null ) {
            retval = dockable;
        }
        return retval;
    }
    public boolean isAcceptable(Dockable dockable) {

        Dockable dragged = getValue(dockable);
        
        if (  dragged == null  ) {
            return false;
        }
        
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        if (dc != null && dc.isValueDockable() ) {
             dragged = Dockable.of(dc.getValue());
        } else if (dc != null && dc.getValue() != null ) {
            return false;
        }
        return (dockLoader != null && dockLoader.isRegistered(dragged.node())) || dockLoader == null;
    }

    public void dock(Point2D mousePos, Dockable dockable) {
        Dockable d = getValue(dockable);
        
/*        DragContainer dc = dockable.getDockableContext().getDragContainer();
        if (dc != null && dc.getValue() != null) {
            if (!dc.isValueDockable()) {
                return;
            }
            d = Dockable.of(dc.getValue());
        }
*/        
        if (d == null || isDocked(d.node())) {
            System.err.println("TargetContext isDocked == false foe node = " + d.node());
            return;
        }
        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, d.node()) && stage != null) {
            //d.getDockableContext().setFloating(false);
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getDockableContext().setTargetContext(this);
        }
    }

//    protected void dock(Dockable dockable, Object pos)  {
//    }
    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public PositionIndicator getPositionIndicator() {
        if (positionIndicator == null) {
            //positionIndicator = createPositionIndicator();
            positionIndicator = getLookup().lookup(PositionIndicator.class);
        }
        return positionIndicator;
    }

    /*    public PositionIndicator getNodeIndicator() {
        return null;
    }
     */
    public Node getTargetNode() {
        return this.targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    protected boolean isDocked(Node node) {
        return false;
    }

    /**
     * isDocked(Node) returns true even if the node is docked to
     * ScenePaneContext
     *
     * @param to ??
     * @param dockable ??
     * @return
     */
    public static boolean isDocked(TargetContext to, Dockable dockable) {
        Dockable d = dockable;
        DragContainer dc = dockable.getDockableContext().getDragContainer();
        if (dc != null && dc.getValue() != null && dc.isValueDockable() ) {
             d = Dockable.of(dc.getValue());
        } else if (dc != null && dc.getValue() != null && ! dc.isValueDockable() ) {        
            return false;
        }
        return to.isDocked(d.node());
    }

    public void undock(Node node) {
        if (DockRegistry.instanceOfDockable(node)) {
            DockableContext dc = Dockable.of(node).getDockableContext();
            dc.getTargetContext().remove(node);
            dc.setTargetContext(null);
        }
    }

    /*07.05    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.getDockableContext());
    }
     */
    public abstract void remove(Node dockNode);

}//class
