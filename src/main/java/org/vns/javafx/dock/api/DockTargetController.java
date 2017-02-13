package org.vns.javafx.dock.api;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
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
public class DockTargetController {

    private Region dockPane;
    private String title;
    private PaneIndicatorTransformer paneTransformer;
    private NodeIndicatorTransformer nodeTransformer;

    private PaneSideIndicator paneIndicator;
    private NodeSideIndicator nodeIndicator;

    private final ObjectProperty<Node> focusedDockNode = new SimpleObjectProperty<>();

    private boolean usedAsDockTarget = true;

    //private DragPopup dragPopup;
    private IndicatorPopup dragPopup;

    //09.02private final ObservableMap<Node, Dockable> notDockableItemsProperty = FXCollections.observableHashMap();

    protected DockTargetController(Region dockPane) {
        this.dockPane = dockPane;
        init();
    }

    protected DockTargetController(Dockable dockable) {
        init();
    }

    private void init() {
        inititialize();
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
    
    //protected void dividerPosChanged(Node node, double oldValue, double newValue) {}
            
    public void setTitle(String title) {
        this.title = title;
    }


    protected void setDragPopup(IndicatorPopup dragPopup) {
        this.dragPopup = dragPopup;
    }

    public IndicatorPopup getDragPopup() {
        if (dragPopup != null) {
            return dragPopup;
        }
        if (getDockPane() != null) {
            dragPopup = new DragPopup(this);
        }
        return dragPopup;
    }


    protected void inititialize() {
        DockRegistry.start();
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
                DockRegistry.dockable(newNode).nodeController().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
            Node oldNode = DockUtil.getImmediateParent(oldValue, (p) -> {
                return DockRegistry.isDockable(p);
            });

            if (oldNode != null && oldNode != newNode) {
                DockRegistry.dockable(oldNode).nodeController().titleBarProperty().setActiveChoosedPseudoClass(false);
            } else if (oldNode != null && !DockRegistry.dockable(oldNode).nodeController().titleBarProperty().isActiveChoosedPseudoClass()) {
                DockRegistry.dockable(oldNode).nodeController().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        });

    }

/*09.02    protected ObservableMap<Node, Dockable> getNotDockableItems() {
        return this.notDockableItemsProperty;
    }
    protected ObservableMap<Node, Dockable> notDockableItemsProperty() {
        return notDockableItemsProperty;
    }
    
*/
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
    
    public DockIndicator getDockIndicator() {
        return getPaneIndicator();
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

    public void undock(Node node) {
        if (DockRegistry.isDockable(node)) {
            DockNodeController dc = DockRegistry.dockable(node).nodeController();
            dc.getPaneController().remove(node);
            dc.setPaneController(null);
        }
    }
    //popup.getPaneController().dock(pt, dockable.node(), popup.getTargetNodeSidePos(), popup.getTargetPaneSidePos(), popup.getDragTarget());
    protected void dock(Point2D mousePos, Node node, IndicatorPopup popup) {    
        
    }
    protected Dockable dock(Point2D mousePos, Node node, Side nodeDockPos, Side paneDockPos, Node target) {
        Dockable retval = null;
        if (paneDockPos != null) {
            System.err.println("1 aaaaaaaaaaa");
            dock(mousePos, DockRegistry.dockable(node), paneDockPos);
        } else if (nodeDockPos != null) {
            System.err.println("2 aaaaaaaaaaa");
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
        dockable.nodeController().setFloating(false);

        doDock(mousePos, dockable.node(), dockPos, target);
        //09.02changeDockedState(dockable, true);
        return dockable;
    }

    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        
//        dockable = convert(dockable, DockConverter.BEFORE_DOCK);
System.err.println("44 aaaaaaaaaaa " + isDocked(dockable.node()));
        if ( doDock(mousePos, dockable.node(), dockPos) ) {
            System.err.println("set floatinfg FALSE");
            dockable.nodeController().setFloating(false);
        }
        return dockable;
    }

/*    protected Dockable convert(Dockable source, int when) {
        Dockable retval = source;
        if (source instanceof DockConverter) {
            retval = ((DockConverter) source).convert(source, when);
        }
        return retval;
    }
*/    

    public Dockable dock(Dockable dockable, Side dockPos) {
        System.err.println("3 aaaaaaaaaaa");
        return dock(null, dockable, dockPos);
    }

    public Dockable dock(Dockable dockable, Side dockPos, Dockable target) {
        return dock(null, dockable, dockPos, target);
    }

    protected boolean doDock(Point2D mousePos, Node node, Side dockPos) {
        return false;
    }

    protected boolean doDock(Point2D mousePos, Node node, Side dockPos, Dockable targetDockable) {
        return false;
    }

    public FloatStageBuilder getStageBuilder(Dockable dockable) {
        return new FloatStageBuilder(dockable.nodeController());
    }

  /*  public interface DockConverter {

        public static final int BEFORE_DOCK = 0;
        public static final int AFTER_DOCK = 0;

        Dockable convert(Dockable source, int when);
    }//interface DockConverter
*/
    public void remove(Node dockNode) {
        Region r;
    }

}//class
