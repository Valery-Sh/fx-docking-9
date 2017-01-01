package org.vns.javafx.dock.api;

import javafx.geometry.Side;
import javafx.scene.Node;

/**
 *
 * @author Valery Shyshkin
 */
public interface DockTarget {
    
    default Dockable dock(Node node, Side dockPos) { return null;};
//    default void dock(Node dockable) {};        
    
/*    
    default void dock(Node dockable) {};    

    default void dock(Node dockable, String dockPos) {
        dock(dockable, DockUtil.sideValue(dockPos));
    }
*/    
    
}
