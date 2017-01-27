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
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.SideIndicator.NodeSideIndicator;
import org.vns.javafx.dock.api.SideIndicator.PaneSideIndicator;
import org.vns.javafx.dock.api.SideIndicatorTransformer.NodeIndicatorTransformer;
import org.vns.javafx.dock.api.SideIndicatorTransformer.PaneIndicatorTransformer;

/**
 *
 * @author Valery
 */
public class PaneHandler {

    private Region dockPane;
    private String title;
    private PaneIndicatorTransformer paneTransformer;
    private NodeIndicatorTransformer nodeTransformer;

    private PaneSideIndicator paneIndicator;
    private NodeSideIndicator nodeIndicator;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    private DragPopup dragPopup;

    private final ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    protected PaneHandler(Region dockPane) {
        this.dockPane = dockPane;
        init();
    }

    protected PaneHandler(Dockable dockable) {
        init();
    }

    private void init() {
        inititialize();
    }
    /**
     * Does nothing. 
     * Subclasses can change behavior.
     * @param dividerPos the position of the divider
     * @param divIndex the position of the node in the specified parent
     * @param dockable a dock node witch divider pos is to be set
     * @param parent a parent the given dock node of th
     */
    public void updateDividers(double dividerPos, int divIndex, Dockable dockable, Parent parent) {
        System.err.println("PaneHandler setDividerPos() paneHandler = " + this);
    }
    
    public String getTitle() {
        if (title != null) {
            return title;
        }
        title = getDockPane().getId();
        if (title == null) {
            title = getDockPane().getClass().getName();
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected ObservableMap<Node, Dockable> notDockableItemsProperty() {
        return notDockableItemsProperty;
    }

    protected void setDragPopup(DragPopup dragPopup) {
        this.dragPopup = dragPopup;
    }

    public DragPopup getDragPopup() {
        if (dragPopup != null) {
            return dragPopup;
        }
        if (getDockPane() != null) {
            dragPopup = new DragPopup(this);
        }
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
        if (getDockPane() == null) {
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

    protected void setPaneTransformer(PaneIndicatorTransformer paneTransformer) {
        this.paneTransformer = paneTransformer;
    }

    protected void setNodeTransformer(NodeIndicatorTransformer nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    public NodeIndicatorTransformer getNodeTransformer() {
        if (nodeTransformer == null) {
            nodeTransformer = createNodeIndicatorTransformer();
        }
        return nodeTransformer;
    }

    protected NodeIndicatorTransformer createNodeIndicatorTransformer() {
        return new NodeIndicatorTransformer();
    }

    public PaneIndicatorTransformer getPaneTransformer() {
        if (paneTransformer == null) {
            paneTransformer = createPaneIndicatorTransformer();
        }
        return paneTransformer;
    }

    protected PaneIndicatorTransformer createPaneIndicatorTransformer() {
        return new PaneIndicatorTransformer();
    }

    ///
    public NodeSideIndicator getNodeIndicator() {
        if (nodeIndicator == null) {
            nodeIndicator = createNodeIndicator();
        }
        return nodeIndicator;
    }

    protected NodeSideIndicator createNodeIndicator() {
        return new NodeSideIndicator(this);
    }

    public PaneSideIndicator getPaneIndicator() {
        if (paneIndicator == null) {
            paneIndicator = createPaneIndicator();
        }
        return paneIndicator;
    }

    protected PaneSideIndicator createPaneIndicator() {
        return new PaneSideIndicator(this);
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
        if (paneDockPos != null) {
            dock(mousePos, DockRegistry.dockable(node), paneDockPos);
        } else if (nodeDockPos != null) {
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

        doDock(mousePos, dockable.node(), dockPos, target);
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
        return dock(null, dockable, dockPos, target);
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos) {
    }

    protected void doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
    }

    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.nodeHandler());
    }

    public interface DockConverter {

        public static final int BEFORE_DOCK = 0;
        public static final int AFTER_DOCK = 0;

        Dockable convert(Dockable source, int when);
    }//interface DockConverter

    public void remove(Node dockNode) {
    }

}//class
