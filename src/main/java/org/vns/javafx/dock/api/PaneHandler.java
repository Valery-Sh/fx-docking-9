package org.vns.javafx.dock.api;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class PaneHandler {

    //private final ObjectProperty<Region> dockPaneProperty = new SimpleObjectProperty<>();
    private Region dockPane;
    
    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();
    private int zorder = 0;
    private boolean usedAsDockTarget = true;

    private DragPopup dragPopup;

    private final ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    private SidePointerModifier sidePointerModifier;

    protected PaneHandler(Region dockPane) {
        this.dockPane = dockPane;
        init();
    }
    protected PaneHandler(Dockable dockable) {
        //dockPaneProperty.set(dockPane);
        init();
    }

    private void init() {
        setSidePointerModifier(this::modifyNodeSidePointer);
        dragPopup = new DragPopup();
        inititialize();
    }

    protected ObservableMap<Node, Dockable> notDockableItemsProperty() {
        return notDockableItemsProperty;
    }

    public DragPopup getDragPopup() {
        return dragPopup;
    }

    protected void initSplitDelegate() {
    }

    protected void inititialize() {
        DockRegistry.start();
        initSplitDelegate();
        initListeners();

    }

    protected void initListeners() {
        if ( getDockPane() == null ) {
            return;
        }
        getDockPane().sceneProperty().addListener((Observable observable) -> {
            if (getDockPane().getScene() != null) {
                focusedDockNode.bind(getDockPane().getScene().focusOwnerProperty());
            }
        });

        focusedDockNode.addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            Node newNode = DockUtil.getImmediateParent(newValue, (p) -> {
                return DockRegistry.isDockable(p);
            });
            if (newNode != null) {
                Dockable n = DockRegistry.dockable(newNode).nodeHandler().getImmediateParent(newValue);
                if (n != null && n != newNode) {
                    newNode = (Node) n;
                }
                DockRegistry.dockable(newNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.isDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().isActiveChoosedPseudoClass()) {
                DockRegistry.dockable(oldNode).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

    protected ObservableMap<Node, Dockable> getNotDockableItems() {
        return this.notDockableItemsProperty;
    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    public Point2D modifyNodeSidePointer(DragPopup popup, Dockable target, double mouseX, double mouseY) {
        return null;
    }

    public SidePointerModifier getSidePointerModifier() {
        return sidePointerModifier;
    }

    public void setSidePointerModifier(SidePointerModifier sidePointerModifier) {
        this.sidePointerModifier = sidePointerModifier;
    }

    public Region getDockPane() {
        return this.dockPane;
    }

    public void setDockPane(Region dockPane) {
        this.dockPane = dockPane;
    }

    protected boolean isDocked(Node node) {
        return false;
    }
    protected void changeDockedState(Dockable dockable, boolean docked) {
        dockable.nodeHandler().setDocked(docked);
    }

    public void undock(Node node) {
        if (!isDocked(node)) {
            return;
        }
        if (DockRegistry.isDockable(node)) {
            DockRegistry.dockable(node).nodeHandler().setDocked(false);
        }
    }

    protected Dockable dock(Point2D mousePos, Node node, Side nodeDockPos, Side paneDockPos, Node target) {
        Dockable retval = null;
        if ( paneDockPos != null) {
            dock(mousePos, DockRegistry.dockable(node), paneDockPos);
        } else if ( nodeDockPos != null ) {
           Dockable t = target == null ? null : DockRegistry.dockable(target);
           dock(mousePos, DockRegistry.dockable(node), nodeDockPos, t);
        }
        return retval;
    }

    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos, Dockable target) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        if (!(dockable instanceof Node) && !DockRegistry.getDockables().containsKey(dockable.node())) {
            DockRegistry.getDockables().put(dockable.node(), dockable);
        }
        dockable.nodeHandler().setFloating(false);

        doDock(mousePos,dockable.node(), dockPos, target);
        changeDockedState(dockable, true);
        return dockable;
    }        
    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        dockable.nodeHandler().setFloating(false);
        dockable = convert(dockable, DockConverter.BEFORE_DOCK);

        doDock(mousePos, dockable.node(), dockPos);
        return dockable;
    }

    protected Dockable convert(Dockable source, int when) {
        Dockable retval = source;
        if (source instanceof DockConverter) {
            retval = ((DockConverter) source).convert(source, when);
        }
        return retval;
    }

    public Dockable dock(Dockable dockable, Side dockPos) {
        return dock(null, dockable, dockPos);
    }

    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        return dock(null,dockable, dockPos, target);
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos) {
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
    }

    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.nodeHandler());
    }

    public int zorder() {
        return zorder;
    }

    public void setZorder(int zorder) {
        this.zorder = zorder;

    }

    @FunctionalInterface
    public interface SidePointerModifier {

        /**
         *
         * @param mouseX
         * @param mouseY
         * @param target
         * @return null than a default position of node indicator is used or a
         * new position of node indicator
         */
        Point2D modify(DragPopup popup, Dockable target, double mouseX, double mouseY);
    }

    public interface DockConverter {

        public static final int BEFORE_DOCK = 0;
        public static final int AFTER_DOCK = 0;

        Dockable convert(Dockable source, int when);
    }//interface DockConverter

    public void remove(Node dockNode) {
    }
}//class
