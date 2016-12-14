package org.vns.javafx.dock.api;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public interface Dockable extends DockTarget {
    StringProperty titleProperty();
    StateProperty stateProperty();
    
    @Override
    default void dock(Node node, Side dockPos) {
        stateProperty().getParent().dock(node, dockPos, this);
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
