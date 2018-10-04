/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.designer.bean;

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
import org.vns.javafx.dock.api.designer.PropertyEditorPane;

/**
 *
 * @author Valery
 */
public class Test03 extends Application {

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
        //PropertyPaneModelRegistry.getInstance().createInternalDescriptors();
        long start1 = System.currentTimeMillis();        
        
        //PropertyPaneModel ppc = PropertyPaneModelRegistry.getInstance().loadDefaultDescriptors();
        //long end1 = System.currentTimeMillis();        
        
        
        PropertyPaneModel propertyPaneModel = PropertyPaneModelRegistry.getPropertyPaneModel();
/*        System.err.println("*** propertyPaneModel.size = " + propertyPaneModel.getBeanModels().size());
        for ( int i=0; i < propertyPaneModel.getBeanModels().size(); i++) {
            System.err.println("   --- " + i + "). " + propertyPaneModel.getBeanModels().get(i).getBeanClassName());
        }
  */      
        //List<BeanModel> descs = oldppc.getBeanModels();
        //PropertyPaneModelRegistry.getInstance().updateBy(ppc);
        long end = System.currentTimeMillis();        
        System.err.println("1) INTERVAL = " + (end-start));
        start = System.currentTimeMillis();        
        PropertyEditorPane editorPane = new PropertyEditorPane();
        Button btn1 = new Button("Button btn1");
        editorPane.setBean(btn1);

        end = System.currentTimeMillis();        
        System.err.println("2) INTERVAL = " + (end-start));
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
