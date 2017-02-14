package org.vns.javafx.dock.api;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
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

    private DockIndicator paneIndicator;
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
    

    protected void dock(Point2D mousePos, Dockable dockable) {
        if (isDocked(dockable.node())) {
            return;
        }
        
        if ( doDock(mousePos, dockable.node()) ) {
            dockable.nodeController().setFloating(false);
        }
        
    }


    protected void dock(Dockable dockable, Object pos)  {
    }
    
    protected boolean doDock(Point2D mousePos, Node node) {
        return false;
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

/*    public PaneSideIndicator getPaneIndicator() {
        if (paneIndicator == null) {
            paneIndicator = createDockIndicator();
        }
        return paneIndicator;
    }
*/
    public DockIndicator getDockIndicator() {
        if (paneIndicator == null) {
            paneIndicator = createDockIndicator();
        }
        return paneIndicator;
    }
    
//    public DockIndicator getDockIndicator() {
//        return getPaneIndicator();
//    }

    protected DockIndicator createDockIndicator() {
        return new PaneSideIndicator(this);
    }
    
//    protected PaneSideIndicator createPaneIndicator() {
//        return new PaneSideIndicator(this);
//    }

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
/*    public Dockable dock(Dockable dockable, Side dockPos) {
        return dock(null, dockable, dockPos);
    }
    protected Dockable dock(Point2D mousePos, Dockable dockable, Side dockPos) {
        if (isDocked(dockable.node())) {
            return dockable;
        }
        
        if ( doDock(mousePos, dockable.node(), dockPos) ) {
            dockable.nodeController().setFloating(false);
        }
        return dockable;
    }
  */  
    /////////////////////////
/*    public void dock(Dockable dockable)  {
        getDockExecutor().dock(dockable);
    }
*/    
///////////////////////////////////////////////////////////////////////
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
