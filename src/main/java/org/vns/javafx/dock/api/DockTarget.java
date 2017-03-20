package org.vns.javafx.dock.api;

import javafx.scene.layout.Region;

/**
 * The interface comprises the minimal requirements for an object which
 * serves as a target for {@code dockable} nodes.
 * 
 * The classes which implement the interface are not forced to be of type
 * {@code javafx.scene.Node} and  rather are wrappers around the nodes.
 * 
 * @see org.vns.javafx.dock.DockPane
 * @see org.vns.javafx.dock.DockSideBar
 * @see org.vns.javafx.dock.DockTabPane
 * @see org.vns.javafx.dock.DockTabPane2
 * 
 * @author Valery Shyshkin
 */
public interface DockTarget {
    /**
     * Returns a node of type {@code Region} that implements this interface or
     * another node wrapped by a class that implements this interface.
     * 
     * @return an object of type {@code javafx.scene.layout.Region}
     */
    Region target();
    /**
     * Returns an object which provides a state and behavior of the target panel 
     * during a docking process. In contrast to the {@link Dockable} interface where
     * a single class of type {@link DockNodeController} may be used for various
     * implementations of the {@xode Dockable} the method as a rule returns a 
     * an instance of specific  class depending on the target functionality.
     * For example, all such classes as {@code DockPane, DockSideBar, 
     * DockTabpane, DockTabPane2} provide their  own implementations of the class
     * {@code DockTargetController}.
     * 
     * @return an object of type {@link DockTargetController}
     */    
    DockTargetController targetController();
}
