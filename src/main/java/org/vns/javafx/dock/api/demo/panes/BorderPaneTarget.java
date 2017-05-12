/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.panes;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.DockTargetController;

/**
 *
 * @author Valery
 */
public class BorderPaneTarget implements DockTarget {
    
    private BorderPane targetPane;
    private DockTargetController targetController;

    public BorderPaneTarget(BorderPane targetPane) {
        this.targetPane = targetPane;
    }
            
    @Override
    public Region target() {
        return targetPane;
    }

    @Override
    public DockTargetController targetController() {
        if ( targetController == null ) {
            targetController = new BorderPaneTargetController(targetPane);
        }
        return targetController;
    }
    
}
