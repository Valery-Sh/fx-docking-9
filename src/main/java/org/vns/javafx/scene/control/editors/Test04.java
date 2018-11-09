package org.vns.javafx.scene.control.editors;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.demo.MyButton1;
import org.vns.javafx.scene.control.editors.beans.PropertyPaneModelRegistry;

/**
 *
 * @author Valery
 */
public class Test04 extends Application {
    int exCount = 0;
    Button mybtn1 = new MyButton1("MyButton mybtn1");
    Button mybtn2 = new MyButton1("MyButton mybtn2");
    Label label = new Label("Label");
    AnchorPane anchorPane = new AnchorPane();
    Stage stage;
    Scene scene;
    int top = 1;
    PropertyEditorPane editorPane = new PropertyEditorPane();

    @Override
    public void start(Stage stage) {
        PropertyPaneModelRegistry.getPropertyPaneModel();
        //PropertyPaneModelRegistry.getInstance().createConstraintMap();
        
        label = new Label("Label label");
        Button btn1 = new Button("Button btn1");

        mybtn1.paddingProperty().addListener((v, ov, nv) -> {
            //System.err.println("nv = " + nv + "; ov=" + ov);

        });

        //System.err.println("UUID = class-" + UUID.randomUUID() );
        //mybtn1.getStyleClass().clear();
        mybtn1.setPadding(new Insets(15, 15, 15, 15));
        mybtn1.getStylesheets().add(Test04.class.getResource("resources/styles/test.css").toExternalForm());
        mybtn1.setPadding(new Insets(15, 15, 15, 15));
        
        //mybtn1.setPadding(new Insets(15, 15, 15, 15));
        //mybtn1.setStyle("-fx-padding: 9");
        btn1.getStyleClass().add("btn1");
        Button btn2 = new Button("Create");
        Button btn3 = new Button("Show btn");
        mybtn2.setOnAction(a -> {
            if (editorPane.getBean() == mybtn1) {
                editorPane.setBean(mybtn2);
                mybtn2.setPadding(new Insets(++top, 3, 3, 3));
            } else if (editorPane.getBean() == mybtn2) {
                editorPane.setBean(label);
                label.setPadding(new Insets(3, ++top, 3, 3));
            } else if (editorPane.getBean() == anchorPane) {
                editorPane.setBean(mybtn1);
                mybtn1.setPadding(new Insets(3, ++top, 3, 3));                
            } else if (editorPane.getBean() == label) {
                editorPane.setBean(anchorPane);
                mybtn1.setPadding(new Insets(3, ++top, 3, 3));                
            } else {
                editorPane.setBean(label);
                mybtn1.setPadding(new Insets(3, ++top, 3, 3));                
                
            }
        });

        
        //btn3.setDefaultButton(true);
        //editorPane.setBean(btn1);
        mybtn1.setPadding(new Insets(3, 8, 3, 3));
        btn3.setOnAction(a -> {
            long start = System.currentTimeMillis();
            if (editorPane.getBean() == mybtn1) {
                editorPane.setBean(btn1);
                btn1.setPadding(new Insets(++top, 3, 3, 3));
            } else {
                //mybtn1.setPadding(new Insets(3, ++top, 3, 3));
                editorPane.setBean(mybtn1);
                //mybtn1.setPadding(new Insets(3, ++top, 3, 3));
            }
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        //btn1.getStyleClass().add("btn2");
        btn1.setPadding(new Insets(10, 10, 10, 10));
        btn2.getStylesheets().add(Test04.class.getResource("resources/styles/test.css").toExternalForm());
        btn2.setOnAction(a -> {
//            System.gc();

            for ( int i=0; i < 100; i++ ) {
//                editorPane.setBean(null);
                Platform.runLater(() -> {
                    
                    execute();
                    //editorPane.wr = new WeakReference(editorPane.getBean());
                    System.err.println("executed i = " + (++exCount));
                });
                
            }
            if (mybtn1.getStylesheets().isEmpty()) {
                //    mybtn1.getStylesheets().add(Test04.class.getResource("resources/styles/test.css").toExternalForm());
            } else {
                //    mybtn1.getStylesheets().clear();
            }
        });
        
        VBox root = new VBox(label, mybtn1, mybtn2, btn1, btn2, btn3, editorPane);

        //System.err.println("**** VVV  ===================================================");
        btn1.setOnAction(a -> {

            long start = System.currentTimeMillis();
            Node bean = null;
            if (editorPane.getBean() == mybtn1) {
                bean = label;
            } else {
                bean = mybtn1;
            }
//            editorPane.setBean(null);
//            root.getChildren().remove(editorPane);
//            editorPane = new PropertyEditorPane();
//            root.getChildren().add(editorPane);

            //root.getChildren().remove(mybtn1);
            //mybtn1 = new Button("new My btn1");
            //root.getChildren().add(0,mybtn1);
            editorPane.setBean(bean);
            long end = System.currentTimeMillis();
//            System.err.println("SHOW INTERVAL = " + (end - start));
//            System.err.println("STYLE: " + mybtn1.getPadding());
            //editorPane.show();
        });

        mybtn1.setOnAction(a -> {
            mybtn1.setPadding(new Insets(5, 5, 5, 5));
            mybtn1.applyCss();
            System.err.println("mybtn1 padding = " + mybtn1.getPadding());
        });

        //editorPane.setBean(btn1);
        DialogPane dialogPane1 = new DialogPane();
        editorPane.setBean(dialogPane1);
        //editorPane.show();

        root.setOnMouseClicked(e -> {
            System.err.println("MOUSE CLICKED");
        });
        EventHandler eh = root.getOnMouseClicked();
        //System.err.println("EventHandler = " + eh);
        //PropertyPaneCollection ppc = PropertyPaneDescriptorRegistry.getInstance().loadDefaultDescriptors();
        //Object bean = ppc.getBeanModels().get(0).getBean();
        VBox root1 = new VBox(btn1,btn2,btn3,mybtn1,mybtn2,label);
        Scene scene1 = new Scene(root1);
        Stage stage1 = new Stage();
        
        stage1.setScene(scene1);
        stage1.setTitle("Controls");
        //stage.setHeight(600);
        //stage.setWidth(300);
        stage1.show();
        stage1.setX(50);
        stage1.setY(50);

        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.setHeight(600);
        stage.setWidth(300);
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

    public void execute() {
        long start = System.currentTimeMillis();
        Node bean = null;
        if (editorPane.getBean() == mybtn1) {
            //bean = label;
            //bean = new Label("LLLL");
            bean = mybtn2;
        } else if ( editorPane.getBean() == label ){
            bean = mybtn1;
        } else if ( editorPane.getBean() == null ) {
            bean = mybtn1;    
        } else {
            bean = mybtn1;
        }
        
//            root.getChildren().remove(editorPane);
//            editorPane = new PropertyEditorPane();
//            root.getChildren().add(editorPane);

        //root.getChildren().remove(mybtn1);
        //mybtn1 = new Button("new My btn1");
        //root.getChildren().add(0,mybtn1);
        //editorPane.wr = new WeakReference(bean);
        editorPane.setBean(bean);

    }
    public void execute1() {
        long start = System.currentTimeMillis();
        Node bean = new Label("label");
        
        editorPane.setBean(bean);

    }
    
}
