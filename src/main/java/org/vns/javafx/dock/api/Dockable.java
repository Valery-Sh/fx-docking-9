package org.vns.javafx.dock.api;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.scene.layout.Region;

/**
 * The interface comprises the minimal requirements for an object to 
 * be docked to any object which implements {@link DockPaneTarget}.
 * The classes which implement the interface are not forced to be of type
 * {@code javafx.scene.Node} and  rather are wrappers around the nodes.
 * @see DockNodeBox
 * @see org.vns.javafx.dock.DockNode
 * @see org.vns.javafx.dock.DockSideBar
 * 
 * @author Valery Shyshkin
 */
public interface Dockable {
    /**
     * Returns a node of type {@code Region} that implements this interface or
     * another node wrapped by a class that implements this interface.
     * 
     * @return an object of type {@code javafx.scene.layout.Region}
     */
    Region node();
    /**
     * Returns an object which provides a state and behavior during 
     * docking process.
     * 
     * @return an object of type {@link DockableController}
     */
    DockableController dockableController();
    
    /**
     * Initialize default {@code css} styles for all components of the docking api.
     * 
     * @param cssURL an {@literal url} which points to a a file containing
     *  style sheets
     */
    static void initDefaultStylesheet(URL cssURL) {
        URL u = cssURL;
        if ( u == null) {
            u = Dockable.class.getResource("resources/default.css");
        }
        StyleManager.getInstance()
                .addUserAgentStylesheet(u.toExternalForm());
    }
    
}
