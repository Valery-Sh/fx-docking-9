package org.vns.javafx.dock.api;

import com.sun.javafx.css.StyleManager;
import java.net.URL;
import javafx.scene.layout.Region;

/**
 *
 * @author Valery
 */
public interface Dockable {
    public static int DOCKED = 0;
    public static int FLOAT = 2;    
    
    Region node();
    DockNodeHandler nodeHandler();
    
    
    static void initDefaultStylesheet(URL cssURL) {
        URL u = cssURL;
        if ( u == null) {
            u = Dockable.class.getResource("resources/default.css");
        }

        StyleManager.getInstance()
                .addUserAgentStylesheet(u.toExternalForm());
                //.addUserAgentStylesheet(Dockable.class.getResource("/resources/org/vns/javafx/dock/api/default.css").toExternalForm());                
    }
}
