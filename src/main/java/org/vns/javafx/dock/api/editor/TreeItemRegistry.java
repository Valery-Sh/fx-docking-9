package org.vns.javafx.dock.api.editor;

import java.lang.reflect.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 *
 * @author Valery
 */
public class TreeItemRegistry {
    
    private final ObservableMap<String, DefaultTreeItemBuilder> builders = FXCollections.observableHashMap();
    
    public static TreeItemRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public DefaultTreeItemBuilder getBuilder(Object o) {
        if ( o == null ) {
            return null;
        }
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
    
    protected DefaultTreeItemBuilder find( Class clazz) {
        DefaultTreeItemBuilder retval = null;
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
    
    public void register(Object key, DefaultTreeItemBuilder value)  {
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
        register(Node.class, new DefaultTreeItemBuilder());
        register(Labeled.class, new LabeledItemBuilder());
        register(Pane.class, new PaneItemBuilder());        
        register(Shape.class, new ShapeItemBuilder());        
        register(TabPane.class, new TabPaneItemBuilder());        
        register(Tab.class, new TabItemBuilder());        
        register(BorderPane.class, new BorderPaneItemBuilder());        
        
    }
    private static class SingletonInstance {
        private static final TreeItemRegistry INSTANCE = new TreeItemRegistry();
    }
    
}
