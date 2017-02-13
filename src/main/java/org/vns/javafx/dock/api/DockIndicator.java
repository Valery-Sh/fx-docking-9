package org.vns.javafx.dock.api;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public abstract class DockIndicator {

    private final DockTargetController paneController;

    private Node dockPlace;
    private Pane indicatorPane;

    //private final Map<Node, DockTargetController> sideButtonMap = new HashMap<>();
    private SideIndicatorTransformer transformer;

    protected DockIndicator(DockTargetController paneController) {
        this.paneController = paneController;
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

    public abstract void showIndicator(double screenX, double screenY, Region targetNode);
    
    public void showIndicator(double screenX, double screenY) {
        showIndicator(screenX, screenY, null);
    }


    public Node getDockPlace() {
        return dockPlace;
    }
    
    public void showDockPlace(double x, double y) {
        getDockPlace().setVisible(true);
    }

    public DockTargetController getPaneController() {
        return paneController;
    }

    protected abstract Pane createIndicatorPane();

    public Pane getIndicatorPane() {
        return indicatorPane;
    }


    protected abstract String getStylePrefix();


    public void hideDockPlace() {
        getDockPlace().setVisible(false);
    }

}//DockIndicator
