/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.designer.bean;

import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class Test02 extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        ObservableList<Category> cl = FXCollections.observableArrayList();
        Category cat1 = new Category();
        cat1.setName("p1");
        Category cat2 = new Category();
        cat2.setName("p2");
        cl.addAll(cat1,cat2);
        Category cat3 = new Category();
        cat3.setName("p1");
        
        
        System.err.println("cat1 == cat2 = " + cat1.equals(cat2));
        long start = System.currentTimeMillis();
        PropertyPaneModelRegistry.getInstance().createInternalDescriptors();
        PropertyPaneModel ppc = PropertyPaneModelRegistry.getInstance().loadDefaultDescriptors();
        PropertyPaneModel oldppc = PropertyPaneModelRegistry.getPropertyPaneModel();
        List<BeanModel> descs = oldppc.getBeanModels();
        PropertyPaneModelRegistry.getInstance().updateBy(ppc);
        long end = System.currentTimeMillis();        
        System.err.println("INTERVAL1 = " + (end-start));
        PropertyPaneModelRegistry.printBeanModel(Button.class.getName(), true);
        //Checker checker = new Checker(PropertyPaneModelRegistry.getInstance(), Button.class);
        //checker.printPropertyDisplayNames();
        
        //PropertyPaneDescriptorRegistry.getInstance().createInternalDescriptors(true);
        //PropertyPaneDescriptorRegistry.getInstance().printBeanModel(Node.class.getName(), true);
        
        //Checker checker = new Checker(PropertyPaneDescriptorRegistry.getInstance(),Region.class);
        //checker.printIntrospectionCheck();
/*        System.err.println("isUpperCase('1') : " + Character.isUpperCase('1'));
        System.err.println("isUpperCase('_') : " + Character.isUpperCase('_'));
        System.err.println("isUpperCase('v') : " + Character.isUpperCase('v'));
        //checker.printIntrospectionCheck();
        System.err.println("a to '" + FXProperty.toDisplayName("a") + "'") ;
        System.err.println("A to '" + FXProperty.toDisplayName("A") + "'") ;
        System.err.println("Ab to '" + FXProperty.toDisplayName("Ab") + "'") ;
        System.err.println("ABc to '" + FXProperty.toDisplayName("ABc") + "'") ;
        System.err.println("AbcD to '" + FXProperty.toDisplayName("AbcD") + "'") ;
        System.err.println("AbcDe to '" + FXProperty.toDisplayName("AbcDe") + "'") ;
        System.err.println("AbcDeI1 to '" + FXProperty.toDisplayName("AbcDeI1") + "'") ;
        System.err.println("AbcDeIk to '" + FXProperty.toDisplayName("AbcDeIk") + "'") ;
        System.err.println("focusTraversable '" + FXProperty.toDisplayName("focusTraversable") + "'") ;
        
        System.err.println("CODE = " + checker.createCodeCategory());
*/        
        
        //PropertyPaneDescriptorRegistry.printBeanModel(Region.class.getName(), true);
        //checker.printPropertyDisplayNames();
        //PropertyPaneDescriptorRegistry.getInstance().introspect(Node.class);
        VBox root = new VBox();
        root.setOnMouseClicked(e -> {
            System.err.println("MOUSE CLICKED");
        });
        EventHandler eh = root.getOnMouseClicked();
        System.err.println("EventHandler = " + eh);
        //PropertyPaneCollection ppc = PropertyPaneDescriptorRegistry.getInstance().loadDefaultDescriptors();
        //Object bean = ppc.getBeanModels().get(0).getBean();
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
