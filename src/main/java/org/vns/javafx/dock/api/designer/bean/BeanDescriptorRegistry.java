package org.vns.javafx.dock.api.designer.bean;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Valery
 */
public class BeanDescriptorRegistry {

  
    private BeanGraphDescriptor graphDescriptor;
    
    public static BeanDescriptorRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public static BeanGraphDescriptor getGraphDescriptor() {
        BeanGraphDescriptor gd = getInstance().graphDescriptor;
        if (gd == null) {
            gd = getInstance().loadDefaultDescriptors();
            getInstance().graphDescriptor = gd;
        }
        
        return gd;
    }

    protected BeanGraphDescriptor loadDefaultDescriptors() {
        FXMLLoader loader = new FXMLLoader();
        BeanGraphDescriptor root = null;
        try {
            root = loader.load(getClass().getResourceAsStream("/org/vns/javafx/dock/api/designer/resources/DefaultBeanDescriptors.fxml"));
            /*            root.getBeanDescriptors().forEach(d -> {
                String className = d.getType();
                Class clazz;//
                try {
                    clazz = Class.forName(className);
                    register(clazz, d);
                } catch (ClassNotFoundException ex) {
                    System.err.println("ClassNotFoundException EXCEPTION: " + ex.getMessage());
                    Logger.getLogger(BeanDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
             */
        } catch (IOException ex) {
            Logger.getLogger(BeanDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

        return root;
    }

    private static class SingletonInstance {

        private static final BeanDescriptorRegistry INSTANCE = new BeanDescriptorRegistry();
    }

}
