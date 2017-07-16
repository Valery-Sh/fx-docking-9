package org.vns.javafx.dock.api;

import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

/**
 *
 * @author Valery Shyshkin
 */
public interface PreferencesBuilder {
    
    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String TREEITEM_ATTR = "ignore:treeItem";
    public static final String PARENT_DOCKTARGET_ATTR = "ignore:parentDockTarget";
    
    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    /**
     * Returns a tree of objects of type {@code javafx.scene.control.TreeItem}, 
     * each node of which  corresponds to some object from the Scene Graph of the 
     * {@link org.vns.javafx.dock.api.DockTarget } node. 
     * The root node corresponds to an object of the {@code DockTarget} 
     * type. For which objects the {@code TreeItem} nodes are created is 
     * determined by the specific implementation of the corresponding 
     * {@code DockTarget}.
     * 
     * @param fieldName the String name used to register an object of type
     * {@code org.vns.javafx.dock.api.DockTarget }.
     * 
     * @return an object of type {@code javafx.scene.control.TreeItem}
     * @see #build() 
     */
    TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName);
    
    Node restore(TreeItem<Pair<ObjectProperty, Properties>> root);
    
    default TreeItem<Pair<ObjectProperty, Properties>> build() {
        return build(null);
    }
    
    void setOnBuildItem(Consumer<TreeItem<Pair<ObjectProperty, Properties>>> consumer);
    Consumer<TreeItem<Pair<ObjectProperty, Properties>>> getOnBuildItem();

    //Map<String, String> getProperties(Object obj);
    
    static TreeItem<Pair<ObjectProperty, Properties>> build(Object obj) {
        return build(null,obj);
    }    
    static TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName, Object obj) {
        if ( obj == null ) {
            return null;
        }
        TreeItem<Pair<ObjectProperty, Properties>> retval = new TreeItem<>();
        Node node = null;
        if (obj instanceof Node) {
            node = (Node) obj;
        }
        Pair<ObjectProperty, Properties> pair = new Pair(new SimpleObjectProperty(obj), new Properties());
        pair.getValue().put(TREEITEM_ATTR, retval);
        if ( fieldName != null ) {
            pair.getValue().put(FIELD_NAME_ATTR, fieldName);
        }
        pair.getValue().put(CLASS_NAME_ATTR, obj.getClass().getName());
        pair.getValue().put(TAG_NAME_ATTR, obj.getClass().getSimpleName());
        retval.setValue(pair);

        if (node != null && node.getId() != null) {
            pair.getValue().put("id", node.getId());
        }
        
        if (node != null && (DockRegistry.isDockable(node) || DockRegistry.isDockTarget(node))) {
            if (DockRegistry.isDockable(node)) {
                pair.getValue().put(ISDOCKABLE_ATTR, "yes");
            }
            if (DockRegistry.isDockTarget(node)) {
                pair.getValue().put(ISDOCKTARGET_ATTR, "yes");
            }
        }
        return retval;
    }
    
    
}
