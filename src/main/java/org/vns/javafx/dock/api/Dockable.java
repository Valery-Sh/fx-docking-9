package org.vns.javafx.dock.api;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.beans.property.StringProperty;
import org.vns.javafx.dock.api.properties.StateProperty;

/**
 *
 * @author Valery
 */
public interface Dockable extends DockTarget {
    StringProperty titleProperty();
    StateProperty stateProperty();
    
    public static void initDefaultStylesheet() {
        URL u = Dockable.class.getResource("default.css");

        StyleManager.getInstance()
                .addUserAgentStylesheet(Dockable.class.getResource("default.css").toExternalForm());
    }
    
}
