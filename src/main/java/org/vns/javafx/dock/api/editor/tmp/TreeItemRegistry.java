package org.vns.javafx.dock.api.editor.tmp;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 *
 * @author Valery
 */
public class TreeItemRegistry {
    
    private final ObservableMap<String, TreeItemBuilder> builders = FXCollections.observableHashMap();
    
    public static TreeItemRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public TreeItemBuilder getBuilder(Object o) {
        if ( builders.isEmpty() ) {
            createDefaultBuilders();
        }
        if ( o instanceof String )  {
            return builders.get((String)o);
        } else if ( o instanceof Class) {
            return find((Class)o); 
        }
        return find(o.getClass());
    }
    
    protected TreeItemBuilder find( Class clazz) {
        TreeItemBuilder retval = null;
        Class c = clazz;
        String name = c.getName();
        
        while ( c != null ) {
            //Type t = c.getGenericSuperclass();
            
            if ( builders.get(name) != null ) {
                retval = builders.get(name);
                break;
            }
            try {
                c = Class.forName(name);
            } catch (ClassNotFoundException ex) {
                System.err.println("Exception. " + ex.getMessage());
                return null;
            }
            Type t = c.getGenericSuperclass();
            if ( t == null ) {
                return null;
            }
            name = t.getTypeName();
            
        }
        return retval;
    }
    
    public void register(Object key, TreeItemBuilder value)  {
        String clazz = key.getClass().getName();
        if ( key instanceof String )  {
            clazz = (String) key;
        } else if ( key instanceof Class) {
            clazz = ((Class)key).getName();
        }
        builders.put(clazz, value);
    }
    public void uregister(Object key)  {
        String clazz = key.getClass().getName();
        if ( key instanceof String )  {
            clazz = (String) key;
        } else if ( key instanceof Class) {
            clazz = ((Class)key).getName();
        }
        builders.remove(clazz);
    }
    public boolean exists(Object obj) {
        return getBuilder(obj) != null;
    }
    protected void createDefaultBuilders() {
        register(Node.class, new TreeItemBuilder());
        register(Labeled.class, new LabeledItemBuilder());
        register(Pane.class, new PaneItemBuilder());        
        register(Shape.class, new ShapeItemBuilder());        
        
    }
    private static class SingletonInstance {
        private static final TreeItemRegistry INSTANCE = new TreeItemRegistry();
    }
    
}
