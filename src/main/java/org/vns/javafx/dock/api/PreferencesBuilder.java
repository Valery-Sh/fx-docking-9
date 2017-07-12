package org.vns.javafx.dock.api;

import java.util.Map;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

/**
 *
 * @author Valery
 */
public interface PreferencesBuilder {

    TreeItem<Pair<ObjectProperty, Properties>> build();

    void restore(TreeItem<Pair<ObjectProperty, Properties>> targetRoot);

    Map<String, String> getProperties(Object node);

    static TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName, Node node) {
        TreeItem<Pair<ObjectProperty, Properties>> retval = new TreeItem<>();       
        if (DockRegistry.isDockable(node) || DockRegistry.isDockTarget(node)) {
            Pair<ObjectProperty, Properties> pair = new Pair(new SimpleObjectProperty(node), new Properties());
            pair.getValue().put("-ignore:treeItem", retval);
            if (node.getId() != null) {
                pair.getValue().put("id", node.getId());
            }
            
            pair.getValue().put("-ld:registered", "yes");        
            if ( DockRegistry.isDockable(node) ) {
                pair.getValue().put("-ld:isdockable", "yes");
            }
            if ( DockRegistry.isDockTarget(node) ) {
                pair.getValue().put("-ld:isdocktarget", "yes");
            }
            pair.getValue().put("-ld:fieldName", fieldName);
            pair.getValue().put("-ld:className", node.getClass().getName());
            pair.getValue().put("-ld:tagName", node.getClass().getSimpleName());
            retval.setValue(pair);
        }
        return retval;

    }
}
