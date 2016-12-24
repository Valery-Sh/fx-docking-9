package org.vns.javafx.dock.api;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public interface Dockable extends DockTarget {
    public static int DOCKED = 0;
    public static int FLOAT = 2;    
    
    StringProperty titleProperty();
    DockableState getDockState();
    
    @Override
    default void dock(Node node, Side dockPos) {
        getDockState().getPaneDelegate().dock(node, dockPos, this);
    }
    
    default String getDockPos() {
        return getDockState().getDockPos();
    }
    default void setDockPos(String dockPos) {
        getDockState().setDockPos(dockPos);
    }
    
    static void initDefaultStylesheet(URL cssURL) {
        URL u = cssURL;
        if ( u == null) {
            u = Dockable.class.getResource("default.css");
        }

        StyleManager.getInstance()
                .addUserAgentStylesheet(Dockable.class.getResource("default.css").toExternalForm());
    }
}
