package org.vns.javafx.dock.api;

import javafx.scene.Node;

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
    
    public static final String LOOKUP_SELECTOR = "docktarget-e651abfa-c321-4249-b78a-120db404b641";
    public static final String DOCKTARGETS_KEY = "docktarget-marker-e651abfa-c321-4249-b78a-120db404b641";
    
    /**
     * Returns a node of type {@code Region} that implements this interface or
     * another node wrapped by a class that implements this interface.
     * 
     * @return an object of type {@code javafx.scene.layout.Region}
     */
    Node target();
    /**
     * Returns an object which provides a state and behavior of the target panel 
     * during a docking process. In contrast to the {@link Dockable} interface where
     * a single class of type {@link DockableContext} may be used for various
     * implementations of the {@code Dockable} the method as a rule returns a 
     * an instance of specific  class depending on the target functionality.
     * For example, all such classes as {@code DockPane, DockSideBar, 
     * DockTabpane, DockTabPane2} provide their  own implementations of the class
     * {@code DockableContext}.
     * 
     * @return an object of type {@link TargetContext}
     */    
    TargetContext getTargetContext();
    
    static DockTarget of(Node obj) {
        return DockRegistry.dockTarget(obj);
    }
}
