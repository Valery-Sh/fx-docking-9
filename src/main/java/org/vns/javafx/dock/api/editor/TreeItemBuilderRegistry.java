package org.vns.javafx.dock.api.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.vns.javafx.dock.api.editor.AbstractContentBasedTreeItemBuilder.NodeContentBasedItemBuilder;

/**
 *
 * @author Valery
 */
public class TreeItemBuilderRegistry {

    private final ObservableMap<String, TreeItemBuilder> builders = FXCollections.observableHashMap();

    public static TreeItemBuilderRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public TreeItemBuilder getBuilder(Object o) {
        if (o == null) {
            return null;
        }
        if (builders.isEmpty()) {
            createDefaultBuilder();
        }
        if (o instanceof String) {
            return builders.get((String) o);
        } else if (o instanceof Class) {
            return find((Class) o);
        }
        TreeItemBuilder retval = find(o.getClass());
        return retval;
    }


    protected TreeItemBuilder find(Class clazz) {
        if (builders.isEmpty()) {
            createDefaultBuilder();
        }

        TreeItemBuilder retval = null;
        Class c = clazz;

        while (c != null && ! c.isPrimitive() ) {
            if (builders.get(c.getName()) != null) {
                retval = builders.get(c.getName());
                break;
            }
            c = c.getSuperclass();
        }
        
        if (retval == null ) {
            c = clazz;
            while (c != null) {
                retval = find(c.getInterfaces());
                if ( retval != null ) {
                    break;
                }
                c = c.getSuperclass();
            }
        }
        if (retval == null) {
            retval = builders.get("javafx.scene.Node");
            register(clazz, retval);
        }
        
        return retval;
    }
    protected TreeItemBuilder find(Class[] interfaces) {
        TreeItemBuilder retval = null;
        for ( Class c : interfaces) {
            retval = findForInterface(c);
            if ( retval != null ) {
                break;
            }
        }
        
        return retval;
    }
    protected TreeItemBuilder findForInterface(Class clazz) {
        return builders.get(clazz.getName());
    }

    public void register(Object key, TreeItemBuilder value) {
        String clazz = key.getClass().getName();
        if (key instanceof String) {
            clazz = (String) key;
        } else if (key instanceof Class) {
            clazz = ((Class) key).getName();
        }
        builders.put(clazz, value);
    }
    public void register(Class clazz, TreeItemBuilder value) {
        builders.put(clazz.getName(), value);
    }

    public void uregister(Object key) {
        String clazz = key.getClass().getName();
        if (key instanceof String) {
            clazz = (String) key;
        } else if (key instanceof Class) {
            clazz = ((Class) key).getName();
        }
        builders.remove(clazz);
    }

    public boolean exists(Object obj) {
        return getBuilder(obj) != null;
    }

    protected void createDefaultBuilder() {
        register(Node.class, new DefaultTreeItemBuilder());
        register(Labeled.class, new LabeledItemBuilder());
        register(TabPane.class, new TabPaneItemBuilder());
        register(Pane.class, new PaneItemBuilder());
        register(Shape.class, new ShapeItemBuilder());
        register(Tab.class, new TabItemBuilder());
        register(BorderPane.class, new BorderPaneItemBuilder());
        register(TextInputControl.class, new TextInputControlItemBuilder());
        register(Text.class, new TextBasedTreeItemBuilder());
        register(TitledPane.class, new NodeContentBasedItemBuilder());
    }

    private static class SingletonInstance {
        private static final TreeItemBuilderRegistry INSTANCE = new TreeItemBuilderRegistry();
    }

}
