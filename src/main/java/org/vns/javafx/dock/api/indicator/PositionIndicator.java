package org.vns.javafx.dock.api.indicator;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.TargetContext;

public abstract class PositionIndicator {

    private final TargetContext targetContext;

    private Node dockPlace;
    private Pane indicatorPane;


    protected PositionIndicator(TargetContext targetContext) {
        this.targetContext = targetContext;
        init();
    }

    private void init() {
        indicatorPane = createIndicatorPane();
        dockPlace = new Rectangle();
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

    protected void showIndicator(double screenX, double screenY, Node targetNode) {
        //showIndicator(screenX, screenY);
    }
    
    //public abstract void showIndicator(double screenX, double screenY);
    public void showIndicator(double screenX, double screenY) {
        getTargetContext().getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
    }   

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

    public Pane getIndicatorPane() {
        return indicatorPane;
    }


    //protected abstract String getStylePrefix();


    public void hideDockPlace() {
        getDockPlace().setVisible(false);
    }

}//PositionIndicator
