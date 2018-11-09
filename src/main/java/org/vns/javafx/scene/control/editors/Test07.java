package org.vns.javafx.scene.control.editors;

import java.net.URL;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.demo.MyButton1;
import org.vns.javafx.designer.DesignerLookup;
import org.vns.javafx.scene.control.editors.beans.PropertyPaneModelRegistry;

/**
 *
 * @author Valery
 */
public class Test07 extends Application {
    int exCount = 0;
    Button mybtn1 = new MyButton1("create Mybutton mybtn1");
    Button mybtn2 = new MyButton1("createMybutton mybtn2");
    Button labelBtn = new Button("create Label");
    Button bloomEffectBtn = new Button("create Bloom Effect");
    Button paneBtn = new Button("create Pane");
    Button hboxBtn = new Button("create HBox");    
    Button vboxBtn = new Button("create VBox");    
    Button stackPaneBtn = new Button("create StackPane");    
    Button gridPaneBtn = new Button("create GridPane");    
    Button anchorPaneBtn = new Button("create AnchorPane");    

    Stage stage;
    Scene scene;
    int top = 1;
    
    PropertyEditorPane editorPane = new PropertyEditorPane();

    @Override
    public void start(Stage stage) {
        PropertyPaneModelRegistry.getPropertyPaneModel();
        GridPane vb = new GridPane();
        

        //StackPane vb = new StackPane();
        TitledPane tp = new TitledPane();
        
        tp.setText("Titled Pane");
        
        Label lb1 = new Label("label lb1");
        //mybtn1.setStyle("-fx-graphic: url(resources/effect-bloom.png)");
        //labelBtn.getStyleClass().add("mybtn1");
        Label t = new Label("Effect");
        String u1 = PropertyEditor.class.getResource("resources/images/effect-bloom-15x15.png").toExternalForm();
        URL u = PropertyEditor.class.getResource("resources/images/effect-bloom-15x15.png");
        System.err.println("URL = " + u);
        t.setStyle("-fx-graphic: url(" + u1 + ")");
        labelBtn.setGraphic(t);
        labelBtn.getStylesheets().add(PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm());
        Reflection reflEffect = new Reflection();
        Bloom bloomEffect = new Bloom();
        reflEffect.setInput(bloomEffect);
        labelBtn.setEffect(reflEffect);
        //String u = getClass().getResource("resources/effect-boom.png").toExternalForm();
        //System.err.println("u = " + u);
                
        vb.add(mybtn1,0,0);
        vb.add(lb1,0,1);
        vb.add(tp,0,2);
        //lb1.setStyle("-fx-background-color: -fx-box-border, -fx-inner-border, -fx-body-color");
        //mybtn1.setStyle("-fx-background-color: aqua, yellow, red");
        //vb.getChildren().add(mybtn1);
        AnchorPane anchorPane = new AnchorPane(mybtn2);
        
        mybtn1.setOnAction(a -> {
            long start = System.currentTimeMillis();
            if ( editorPane.getBean() == mybtn1 && (mybtn1.getParent() instanceof StackPane) ) {
                System.err.println(StackPane.getAlignment(mybtn1));
                return;
            }
            editorPane.setBean(mybtn1);
            
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        mybtn2.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(mybtn2);
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        labelBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            //editorPane.setBean(new Label("Label"));
            editorPane.setBean(labelBtn);
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        bloomEffectBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new Bloom());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        
        paneBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new Pane());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        hboxBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new HBox());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        vboxBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new VBox());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        stackPaneBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new StackPane());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        gridPaneBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new GridPane());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });
        
        anchorPaneBtn.setOnAction(a -> {
            long start = System.currentTimeMillis();
            editorPane.setBean(new AnchorPane());
            long end = System.currentTimeMillis();
            System.err.println("SHOW INTERVAL = " + (end - start));
        });        
        VBox root = new VBox(editorPane);

        VBox root1 = new VBox(vb,anchorPane,labelBtn,bloomEffectBtn,paneBtn,hboxBtn,vboxBtn,stackPaneBtn,gridPaneBtn, anchorPaneBtn );
        
        
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
        Node sp = tp.lookup(".titled-pane");// > .title > .arrow-button .arrow");
        Node n1 = sp.lookup(".arrow");// > .title > .arrow-button .arrow");
        n1.setStyle("-fx-shape: '' ; visibility: hidden");
        tp.setContent(new Label("tp content"));
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        //scene1.getStylesheets().add(getClass().getResource("resources/test.css").toExternalForm());
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
