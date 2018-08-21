
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.designer.bean.BeanDescriptor;

import org.vns.javafx.dock.api.designer.bean.editor.DoublePropertyEditor;
import org.vns.javafx.dock.api.designer.bean.PropertyDescriptor;

/**
 *
 * @author Valery
 */
public class TestBeanDescriptor extends Application {

    Stage stage;
    Scene scene;
    DoubleProperty value = new SimpleDoubleProperty();
    private ObjectProperty<PropertyDescriptor> propertyDescriptor = new SimpleObjectProperty<>();
    private ObservableList<ObjectProperty<PropertyDescriptor>> list = FXCollections.observableArrayList();
    
    public ObjectProperty<PropertyDescriptor> propertyDescriptorProperty(){
        return propertyDescriptor;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor.get();
    }

    public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor.set(propertyDescriptor);
    }
            
    @Override
    public void start(Stage stage) {
        MyBean mb = new MyBean();
        Button btn1 = new Button("Stage Button");        
        PropertyDescriptor ppd = new PropertyDescriptor();
        
        //propertyDescriptor.set(ppd);
        ppd.setName("myProp");
        mb.setPropertyDescriptor(ppd);
        print(mb);
        //list.add(propertyDescriptor);
        
        BeanDescriptor beanDescr = new BeanDescriptor();
        beanDescr.setType(HBox.class.getName());
        PropertyDescriptor pd = new PropertyDescriptor();
        pd.setName("prefHeight");
        pd.setEditorClass(DoublePropertyEditor.class.getName());
        //beanDescr.getExposedProperties().add(pd);
        
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        Label lb1 = new Label("Text Alignment");
        lb1.setFont(new Font(13));
        System.err.println("font size lb1.getFont().getSize()= " + lb1.getFont().getSize());
        DoublePropertyEditor tf1 = new DoublePropertyEditor();
        tf1.setFont(new Font(13));
        value.bindBidirectional(tf1.valueProperty());
        value.addListener((v,ov,nv) -> {
            System.err.println("1 VALUE " + value.get() + "; TEXT = " + tf1.getText());
        });
        tf1.textProperty().addListener((v,ov,nv) -> {
            System.err.println("2 VALUE " + value.get() + "; TEXT = " + tf1.getText());

        });
        //anchor.setMinWidth(10);
        //grid.setMinWidth(10);
        //tf1.setMinWidth(10);
        //lb1.setMinWidth(10);
        //tf1.prefWidthProperty().bind(grid.widthProperty().subtract(lb1.widthProperty()));
        grid.add(lb1, 0, 0);
        grid.add(tf1, 1, 0);
        Label lb2 = new Label("111111lable 1");
        lb2.setFont(new Font(13));
        TextField tf2 = new TextField();
        lb2.setFont(new Font(13));
        //tf1.setPrefWidth(200);
        grid.add(lb2, 0, 1);
        grid.add(tf2, 1, 1);
        TextField tf3 = new TextField();
        tf3.setFont(new Font(13));
        //grid.add(tf3, 20, 2); 

        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc20 = new ColumnConstraints();

        cc0.setPercentWidth(35);
        cc1.setPercentWidth(65);
        //cc20.setPercentWidth(100);

        //grid.getColumnConstraints().addAll(cc0,cc1, cc20);        
        grid.getColumnConstraints().addAll(cc0, cc1);
        //GridPane.setHalignment(tf1, HPos.RIGHT);
        //GridPane.setHalignment(tf1, HPos.LEFT);
        //GridPane.setFillWidth(tf1, true);
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Stage stage1 = new Stage();
        stage1.initOwner(stage);

        
        System.err.println("BUTTON bean = " + btn1.graphicProperty().getBean());
        VBox vbox = new VBox(btn1);
        vbox.setId("idVbox");
        
        
        VBox propPane = new VBox();
        TilePane tilePane = new TilePane();
        propPane.setStyle("-fx-border-width: 2; -fx-border-color: green");
        vbox.getChildren().add(propPane);
        propPane.getChildren().add(tilePane);

        /*        TabPane tabPane = new TabPane();
        //propPane.getChildren().add(tabPane);
        Tab propTab = new Tab();
        Tab layoutTab = new Tab();
        Tab codeTab = new Tab();
        tabPane.getTabs().addAll(propTab,layoutTab,codeTab);
        
        tabPane.setTabMaxHeight(0);
        propTab.setContent(new Label("P111"));
        layoutTab.setContent(new Label("L111"));
        codeTab.setContent(new Label("C111"));
         */
        StackPane contentPane = new StackPane();
        propPane.getChildren().add(contentPane);
        contentPane.setStyle("-fx-border-width: 2; -fx-border-color: blue");
        Button propBtn = new Button("Properties");
        Button layoutBtn = new Button("Layout");
        Button codeBtn = new Button("Code");
        tilePane.getChildren().addAll(propBtn, layoutBtn, codeBtn);
        //
        // Properties Category
        //
        TitledPane propTitledPane1 = new TitledPane();
        propTitledPane1.setText("Node");

        TitledPane propTitledPane2 = new TitledPane();
        propTitledPane2.setText("JavaFx CSS");
        TitledPane propTitledPane3 = new TitledPane();
        propTitledPane3.setText("Extras");
        VBox propSecBox = new VBox(propTitledPane1, propTitledPane2, propTitledPane3);
        contentPane.getChildren().add(propSecBox);

        TitledPane layoutTitledPane1 = new TitledPane();
        layoutTitledPane1.setText("Content");
        TitledPane layoutTitledPane2 = new TitledPane();
        layoutTitledPane2.setText("Internals");
        VBox layoutSecBox = new VBox(layoutTitledPane1, layoutTitledPane2);
        contentPane.getChildren().add(layoutSecBox);
        layoutSecBox.setVisible(false);

        TitledPane codeTitledPane1 = new TitledPane();
        codeTitledPane1.setText("onAction");
        VBox codeSecBox = new VBox(codeTitledPane1);
        contentPane.getChildren().add(codeSecBox);
        codeSecBox.setVisible(false);

        propBtn.setDisable(true);

        propBtn.setOnAction(e -> {
            propBtn.setDisable(true);
            propSecBox.setVisible(true);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        layoutBtn.setOnAction(e -> {
            layoutBtn.setDisable(true);
            layoutSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            codeBtn.setDisable(false);
            codeSecBox.setVisible(false);
        });
        codeBtn.setOnAction(e -> {
            codeBtn.setDisable(true);
            codeSecBox.setVisible(true);
            propBtn.setDisable(false);
            propSecBox.setVisible(false);
            layoutBtn.setDisable(false);
            layoutSecBox.setVisible(false);
        });

        Scene scene1 = new Scene(vbox);
        stage1.setScene(scene1);

        stage1.show();

        Button btn2 = new Button("Stage1 Button2");
        VBox vbox2 = new VBox(btn2);
        PopupControl pc = new PopupControl();
        pc.getScene().setRoot(vbox2);
        pc.show(stage, 20, 2);
        print(propertyDescriptorProperty());
        //System.err.println("MyProp bean = " + propertyDescriptorProperty().getBean());
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }
    public void print(Property<PropertyDescriptor> p) {
         System.err.println("Print MyProp bean = " + propertyDescriptorProperty().getBean()); 
    }
    public void print(MyBean mb) {
        print(mb.propertyDescriptorProperty());
         //System.err.println("MyBean Print bean = " + propertyDescriptorProperty().getBean()); 
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
