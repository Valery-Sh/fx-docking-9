/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.panes;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.IndicatorPopup;
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
        System.err.println("BorderPaneController: Point2D=" + mousePos);
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected PositionIndicator createPositionIndicator() {
        return null;
        //return new BorderPanePositionIndicator(this);
    }

    @Override
    public void remove(Node dockNode) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
