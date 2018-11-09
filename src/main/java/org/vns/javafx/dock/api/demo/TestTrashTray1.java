/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.designer.TrashTray;
import org.vns.javafx.designer.TrashTray.TrayItem;

/**
 *
 * @author Valery
 */
public class TestTrashTray1 extends Application {
    
    Stage stage;
    Scene scene;
    Button saveBtn;
    
    @Override
    public void start(Stage stage) {
        
        Button testBtn = new Button("textBtn");
        testBtn.getStyleClass().add(DockTitleBar.StyleClasses.PIN_BUTTON.cssClass());
        
        Button removeBtn = new Button("remove testBtn");
        Button addTestBtn = new Button("add testBtn");
        Button createTestBtn = new Button("createBtn");
        HBox hbox = new HBox();
        VBox root = new VBox();
        //StackPane root = new StackPane(createTestBtn, removeBtn, addTestBtn);
        //StackPane root = new StackPane();
        //root.getChildren().add(treeView);

        removeBtn.setOnAction(a -> {
            saveBtn = testBtn;
            root.getChildren().remove(testBtn);
        });
        
        addTestBtn.setOnAction(a -> {
            //hbox.getChildren().add(testBtn);
        });
        createTestBtn.setOnAction(a -> {
            
            testBtn.graphicProperty().addListener((v, ov, nv) -> {
                System.err.println("oldValue = " + ov + "; newValue = " + nv);
            });
            root.getChildren().add(testBtn);
        });
        
        root.setPrefSize(500, 100);
        scene = new Scene(root);
//        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        //root.getStyleClass().add("test-css");
        //root.setStyle("-fx-background-color: blue");
        root.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");
        
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        /*        PopupControl pc = new PopupControl();
        StackPane pcRoot = new StackPane();
        TrashTray tray = new TrashTray();
        //Button b = new Button("A");
        pcRoot.getChildren().add(tray);
        pcRoot.setPrefSize(100,100);
        //pcRoot.setStyle("-fx-background-color: green");
        pcRoot.setStyle("-fx-background-color: transparent;-fx-border-width: 3; -fx-border-color: black; -fx-border-style: dashed");                
        pc.getScene().setRoot(pcRoot);
        //pc.getScene().setFill(Color.RED);
         */
        //Stage trayStage = TrashTray.showStage(stage);
        
        ///TrashTray tray = (TrashTray) ((StackPane) trayStage.getScene().getRoot()).getChildren().get(0);
        TrashTray tray = new TrashTray();
        TableView<TrayItem> tv = new TableView(tray.getItems());
        
        TableColumn<TrayItem, ImageView> graphicColumn = new TableColumn<>();
        graphicColumn.setCellValueFactory(new PropertyValueFactory("graphic"));
        tv.getColumns().add(graphicColumn);
/*        graphicColumn.setCellFactory(col -> {
            TableCell<TrayItem, ImageView> cell = new TableCell<TrayItem, ImageView>() {
                
                @Override
                public void updateItem(ImageView iv, boolean empty) {
                    super.updateItem(iv, empty);
                    this.setGraphic(null);
                    
                    if (!empty) {
                        this.setGraphic(iv);
                    }
                }
            };
            return cell;
        });
*/        
        TableColumn<TrayItem, String> classNameColumn = new TableColumn<>("Class Name");
        classNameColumn.setCellValueFactory(new PropertyValueFactory("className"));
        tv.getColumns().add(classNameColumn);
        
        TableColumn<TrayItem, String> varNameColumn = new TableColumn<>("Variable Name");
        varNameColumn.setCellValueFactory(new PropertyValueFactory("varName"));
        tv.getColumns().add(varNameColumn);
        
        TableColumn<TrayItem, Long> saveTimeColumn = new TableColumn<>("Save Time");
        saveTimeColumn.setCellValueFactory(new PropertyValueFactory("saveTime"));
        tv.getColumns().add(saveTimeColumn);
        
        BeanInfo info;
        Label mylb = new Label("My test Label");
        try {
            info = Introspector.getBeanInfo(Node.class);
            System.err.println("info.getBeanDescriptor().getName()="+ info.getBeanDescriptor().getName());
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for ( PropertyDescriptor pd : pds ) {
                
                Method method = pd.getReadMethod();
                Object obj = method.invoke(mylb, new Object[0]);
                System.err.println("   --- name = " + pd.getName() + "; v = " + obj);
            }
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TestTrashTray1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        Button btn1 = new Button("Display Column");
        btn1.setOnAction(e -> {
            System.err.println("[3,0] = " + tv.getColumns().get(3).getCellData(tv.getItems().get(0)) );
        });
        //root.getChildren().add(tv);        
        root.getChildren().add(btn1);
        Label lb = new Label("lb1");
        TrayItem it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        
        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);
        
        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb3");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb4");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb5");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        
        lb = new Label("lb2");
        it = new TrayItem(tray,lb);
        tray.getItems().add(it);

        
        /*        pc.show(stage,50,50);
        pc.setWidth(100);
        pc.setHeight(100);
         */
 /* Set up a Timeline animation */
// Get the scene width and the text width
/*        double sceneWidth = scene.getWidth();
        double msgWidth = msg.getLayoutBounds().getWidth();
// Create the initial and final key frames
        KeyValue initKeyValue
                = new KeyValue(msg.translateXProperty(), sceneWidth);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);
        KeyValue endKeyValue
                = new KeyValue(msg.translateXProperty(), -1.0 * msgWidth);
        //= new KeyValue(msg.translateXProperty(), 0);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(3), endKeyValue);
// Create a Timeline object
        Timeline timeline = new Timeline(initFrame, endFrame);
        timeline.setRate(0.5);
// Let the animation run forever
        timeline.setCycleCount(Timeline.INDEFINITE);
// Start the animation
        timeline.play();
         */
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
