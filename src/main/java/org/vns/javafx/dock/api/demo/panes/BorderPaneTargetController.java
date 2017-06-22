/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.panes;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.PositionIndicator;

/**
 *
 * @author Valery
 */
public class BorderPaneTargetController extends DockTargetController {

    public BorderPaneTargetController(Region dockPane) {
        super(dockPane);
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        boolean retval = true;
        BorderPane target = (BorderPane) getTargetNode();
        BorderPane bp = (BorderPane) getPositionIndicator().getIndicatorPane();
        
        if ( target.getTop() == null && DockUtil.contains(bp.getTop(), mousePos.getX(), mousePos.getY())) {
            target.setTop(node);
        } else if ( target.getRight() == null && DockUtil.contains(bp.getRight(), mousePos.getX(), mousePos.getY())) {
            target.setRight(node);
        } else if ( target.getBottom() == null && DockUtil.contains(bp.getBottom(), mousePos.getX(), mousePos.getY())) {
            target.setBottom(node);
        } else if ( target.getLeft() == null && DockUtil.contains(bp.getLeft(), mousePos.getX(), mousePos.getY())) {
          target.setLeft(node);
        } else if ( target.getCenter() == null && DockUtil.contains(bp.getCenter(), mousePos.getX(), mousePos.getY())) {
            target.setCenter(node);
        } else {
            retval = false;
        }
        return retval;
    }
    
/*    private boolean satisfies(Point2D mousePos, Node node, Pos pos) {
        DockUtil.contains(node, mousePos.getX(), mousePos.getY());
    }
*/    
    @Override
    protected PositionIndicator createPositionIndicator() {
        //return null;
        return new BorderPanePositionIndicator(this);
    }

    @Override
    public void remove(Node dockNode) {
        System.err.println("REMOVE ____________________");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Dockable> getDockables() {
        BorderPane bp = (BorderPane) getTargetNode();
        List<Dockable> list = FXCollections.observableArrayList();
        bp.getChildren().forEach(node -> {
            if ( DockRegistry.isDockable(node)) {
                list.add(DockRegistry.dockable(node));
            }
        });
        return list;
    }

}
