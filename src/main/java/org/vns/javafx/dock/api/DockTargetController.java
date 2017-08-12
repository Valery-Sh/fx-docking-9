package org.vns.javafx.dock.api;

import java.util.List;
import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public abstract class DockTargetController {

    private Region targetNode;
    private String title;
    private PositionIndicator positionIndicator;
    
    private Predicate<Node> acceptableNode;
    
    private AbstractDockStateLoader dockLoader;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    private IndicatorPopup indicatorPopup;
    
    private double resizeMinWidth = -1;
    
    private double resizeMinHeight = -1;

    protected DockTargetController(Region targetNode) {
        this.targetNode = targetNode;
        init();
    }

    protected DockTargetController(Dockable dockable) {
        init();
    }

    private void init() {
        inititialize();
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

    public double getResizeMinWidth() {
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


    /*12.05protected void setIndicatorPopup(IndicatorPopup indicatorPopup) {
        this.indicatorPopup = indicatorPopup;
    }
     */
    protected IndicatorPopup createIndicatorPopup() {
        //if ( indicatorPopup == null ) {
        //    indicatorPopup = new IndicatorPopup(this);
        //}
        //return indicatorPopup;
        return new IndicatorPopup(this);
    }

    public IndicatorPopup getIndicatorPopup() {
        if (indicatorPopup == null) {
            indicatorPopup = createIndicatorPopup();
        }
        return indicatorPopup;
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
                return DockRegistry.instanceOfDockable(p);
            });
            if (newNode != null) {
                DockRegistry.dockable(newNode).dockableController().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.instanceOfDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                DockRegistry.dockable(oldNode).dockableController().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !DockRegistry.dockable(oldNode).dockableController().titleBarProperty().isActiveChoosedPseudoClass()) {
                DockRegistry.dockable(oldNode).dockableController().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }
    

    public AbstractDockStateLoader getDockLoader() {
        return dockLoader;
    }

    public void setDockLoader(AbstractDockStateLoader loader) {
        this.dockLoader = loader;
    }

    
    public boolean isAcceptable(Node node) {
        //if ( dockLoader != null && dockLoader.getEntryName(this.getTargetNode()) != null ) {
        
        return  (dockLoader != null && dockLoader.isRegistered(node)) || dockLoader == null;
    }
    
    protected void dock(Point2D mousePos, Dockable dockable) {
        if (isDocked(dockable.node())) {
            return;
        }
        Node node = dockable.node();
        Window stage = null; 
        if (node.getScene() != null && node.getScene().getWindow() != null ) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, dockable.node()) && stage != null) {
            dockable.dockableController().setFloating(false);
            if ( (stage instanceof Stage)  && stage.getProperties().get(DragManager.DRAG_FLOATING_STAGE) == null) {
                ((Stage)stage).close();
            } else if ( (stage instanceof Stage)&& stage.getProperties().get(DragManager.DRAG_FLOATING_STAGE) != null) {
                dockable.dockableController().getDragManager().hideFloatingStage(stage);
            } else {
                stage.hide();
            }
            dockable.dockableController().setTargetController(this);
        }
    }

//    protected void dock(Dockable dockable, Object pos)  {
//    }
    protected abstract boolean doDock(Point2D mousePos, Node node);

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public PositionIndicator getPositionIndicator() {
        if (positionIndicator == null) {
            positionIndicator = createPositionIndicator();
        }
        return positionIndicator;
    }
    public PositionIndicator getNodeIndicator() {
        return null;
    }

    protected abstract PositionIndicator createPositionIndicator();
    
    public abstract List<Dockable> getDockables();

    public Region getTargetNode() {
        return this.targetNode;
    }

    public void setTargetNode(Region targetNode) {
        this.targetNode = targetNode;
    }

    protected boolean isDocked(Node node) {
        return false;
    }

    public void undock(Node node) {
        if (DockRegistry.instanceOfDockable(node)) {
            DockableController dc = DockRegistry.dockable(node).dockableController();
            dc.getTargetController().remove(node);
            dc.setTargetController(null);
        }
    }

    /*07.05    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.dockableController());
    }
     */
    public abstract void remove(Node dockNode);
    
    public DockTreeItemBuilder getDockTreeTemBuilder() {
        return null;
    }
    
}//class
