/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.geometry.Side;
import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public interface DockTarget {
    
    default void dock(Node dockable, Side dockPos) {};
    default void dock(Node dockable) {};    

    default void dock(Node dockable, String dockPos) {
        Side s = null;
        if ( dockPos == null ) {
            s = Side.BOTTOM;
        } else {
            switch(dockPos) {
                case "TOP" :
                    s = Side.TOP;
                    break;
                case "BOTTOM" :
                    s = Side.BOTTOM;
                    break;                    
                case "LEFT" :
                    s = Side.LEFT;
                    break;                    
                case "RIGHT" :
                    s = Side.RIGHT;                    
                    break;                    
            }
        }
        dock(dockable, s);
    }
    
    
}
