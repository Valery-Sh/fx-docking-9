/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.geometry.Side;
import javafx.scene.Node;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public interface DockTarget {
    
    default void dock(Node dockable, Side dockPos) {};
    default void dock(Node dockable) {};    

    default void dock(Node dockable, String dockPos) {
        dock(dockable, DockUtil.sideValue(dockPos));
    }
    
    
}
