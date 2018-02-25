package org.vns.javafx.dock.api.indicator;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.TargetContext;

public abstract class PositionIndicator {

    private final TargetContext targetContext;
    
    private IndicatorPopup indicatorPopup;
    
    private Node dockPlace;
    private Pane indicatorPane;


    protected PositionIndicator(TargetContext targetContext) {
        this.targetContext = targetContext;
        init();
    }

    private void init() {
        indicatorPane = createIndicatorPane();
        dockPlace = new Rectangle();
        dockPlace.setId("dockPlace");
        dockPlace.getStyleClass().add("dock-place");
        addDockPlace();
    }
    
    protected void addDockPlace() {
       indicatorPane.getChildren().add(dockPlace);        
    }
    
    protected Boolean intersects(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
        Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
        return b1.intersects(b2);

    }

    protected void showIndicatorPopup(Node ownerNode,double screenX, double screenY ) {
        getIndicatorPopup().show(ownerNode, screenX, screenY);
    }
    
/*    protected void showSideIndicator(double screenX, double screenY, Node targetNode) {
    }
*/    
    
/*    public void showIndicatorPopup(double screenX, double screenY) {
        getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
    }   
*/
    public Node getDockPlace() {
        return dockPlace;
    }
    
    public void showDockPlace(double x, double y) {
        getDockPlace().setVisible(true);
    }

    public TargetContext getTargetContext() {
        return targetContext;
    }

    protected abstract Pane createIndicatorPane();

    public IndicatorPopup getIndicatorPopup() {
        if ( indicatorPopup == null ) {
            indicatorPopup = (IndicatorPopup)targetContext.getLookup().lookup(IndicatorManager.class);
        }
        return indicatorPopup;
    }
    
    public Pane getIndicatorPane() {
        return indicatorPane;
    }


    //protected abstract String getStylePrefix();


    public void hideDockPlace() {
        getDockPlace().setVisible(false);
    }

}//PositionIndicator
