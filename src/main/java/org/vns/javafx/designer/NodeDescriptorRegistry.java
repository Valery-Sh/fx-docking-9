package org.vns.javafx.designer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author Valery
 */
public class NodeDescriptorRegistry {

    //private final ObservableMap<String, TreeItemBuilder> descriptors = FXCollections.observableHashMap();
    private final ObservableMap<Class, NodeDescriptor> descriptors = FXCollections.observableHashMap();

    public static NodeDescriptorRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public NodeDescriptor getDescriptor(Object o) {
        if (o == null) {
            return null;
        }
        if (descriptors.isEmpty()) {
            //createDefaultDescriptors();
            loadDefaultDescriptors();
        }
        NodeDescriptor retval;
        if (o instanceof Class) {
            retval = find((Class) o);
        } else {
            retval = find(o.getClass());
        }
        if (retval == null && !(o instanceof Class)) {
            //
            // try to find DefaultProperty
            //
            Annotation annotation = o.getClass().getDeclaredAnnotation(DefaultProperty.class);
            if (annotation != null) {
                String name = ((DefaultProperty) annotation).value();
                retval = new NodeDescriptor();
                retval.setType(o.getClass().getName());
                //retval.setStyleClass("tree-item-node-unknownnode");
                //20.01ContentProperty cp = new NodeContent(retval);
                NodeContent cp = new NodeContent();
                cp.setName(name);
                retval.getProperties().add(cp);
            }
        }
        if (retval == null && !(o instanceof Class)) {
            retval = new NodeDescriptor();
            retval.setType(o.getClass().getName());
            retval.setStyleClass("tree-item-node-unknownnode");
        }
        return retval;
    }

    protected NodeDescriptor find(Class clazz) {
        if (descriptors.isEmpty()) {
            //createDefaultDescriptors();
            loadDefaultDescriptors();
        }

        NodeDescriptor retval = null;
        Class c = clazz;

        while (c != null && !c.isPrimitive()) {
            if (descriptors.get(c) != null) {
                retval = descriptors.get(c);
                break;
            }
            c = c.getSuperclass();
        }

        if (retval == null) {
            c = clazz;
            while (c != null) {
                retval = find(c.getInterfaces());
                if (retval != null) {
                    break;
                }
                c = c.getSuperclass();
            }
        }
        if (retval == null) {
            retval = descriptors.get(Node.class);
            register(clazz, retval);
        }

        return retval;
    }

    protected NodeDescriptor find(Class[] interfaces) {
        NodeDescriptor retval = null;
        for (Class c : interfaces) {
            retval = findForInterface(c);
            if (retval != null) {
                break;
            }
        }

        return retval;
    }

    protected NodeDescriptor findForInterface(Class clazz) {
        return descriptors.get(clazz);
    }

    public void register(Class clazz, NodeDescriptor value) {
        descriptors.put(clazz, value);
    }

    /*    public void uregister(Object key) {
        String clazz = key.getClass().getName();
        if (key instanceof String) {
            clazz = (String) key;
        } else if (key instanceof Class) {
            clazz = ((Class) key).getName();
        }
        descriptors.remove(clazz);
    }
     */
    public void uregister(Object key) {
        descriptors.remove(key.getClass());
    }

    public boolean exists(Object obj) {
        return getDescriptor(obj) != null;
    }

    protected void loadDefaultDescriptors() {
        FXMLLoader loader = new FXMLLoader();
        GraphDescriptor root;
        try {
            //loader.impl_setLoadListener(new DesignLoadListener());
            //System.err.println("L = " + loader.impl_getLoadListener());
            root = loader.load(getClass().getResourceAsStream("/org/vns/javafx/designer/resources/DesignFXML01.fxml"));
            //root = loader.load(getClass().getClassLoader().getResourceAsStream("org/vns/javafx/designer/resources/DesignFXML01.fxml"));
            root.getDescriptors().forEach(d -> {
                String className = d.getType();
                Class clazz;//
                try {
                    clazz = Class.forName(className);
                    register(clazz, d);
                } catch (ClassNotFoundException ex) {
                    System.err.println("ClassNotFoundException EXCEPTION: " + ex.getMessage());
                    Logger.getLogger(NodeDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IOException ex) {
            System.err.println("IOException EXCEPTION: " + ex.getMessage());
            Logger.getLogger(NodeDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    private static class SingletonInstance {

        private static final NodeDescriptorRegistry INSTANCE = new NodeDescriptorRegistry();
    }

}
