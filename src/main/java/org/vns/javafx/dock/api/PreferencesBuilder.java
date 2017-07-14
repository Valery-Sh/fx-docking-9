package org.vns.javafx.dock.api;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import static org.vns.javafx.dock.api.AbstractDockLoader.*;

/**
 *
 * @author Valery
 */
public interface PreferencesBuilder {
    public static int REGISTERED_OPTION = 0;
    public static int ISDOCKABLE_OPTION = 2;
    public static int ISDOCKTARGET_OPTION = 4;
    
    TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName);
    Node restore(TreeItem<Pair<ObjectProperty, Properties>> targetRoot);
    Map<String, String> getProperties(Object node);

    static TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName, Object obj, boolean... options ) {
        TreeItem<Pair<ObjectProperty, Properties>> retval = new TreeItem<>();
        Node node = null;
        if (obj instanceof Node) {
            node = (Node) obj;
        }
        Pair<ObjectProperty, Properties> pair = new Pair(new SimpleObjectProperty(obj), new Properties());
        pair.getValue().put(IGNORE_ATTR, retval);
        if ( fieldName != null ) {
            pair.getValue().put(FIELD_NAME_ATTR, fieldName);
        }
        pair.getValue().put(CLASS_NAME_ATTR, node.getClass().getName());
        pair.getValue().put(TAG_NAME_ATTR, node.getClass().getSimpleName());
        retval.setValue(pair);

        if (node != null && node.getId() != null) {
            pair.getValue().put("id", node.getId());
        }
        
        if (node != null && (DockRegistry.isDockable(node) || DockRegistry.isDockTarget(node))) {

            pair.getValue().put(REGSTERED_ATTR, "yes");
            if (DockRegistry.isDockable(node)) {
                pair.getValue().put(ISDOCKABLE_ATTR, "yes");
            }
            if (DockRegistry.isDockTarget(node)) {
                pair.getValue().put(ISDOCKTARGET_ATTR, "yes");
            }
        } else if ( options.length > 0 && options[REGISTERED_OPTION]) {
            System.err.println("PreferencesBuilder fieldName = " + fieldName);
            pair.getValue().put(REGSTERED_ATTR, "yes");
        }
        return retval;
    }
}
