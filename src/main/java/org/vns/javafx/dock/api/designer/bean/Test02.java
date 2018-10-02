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
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Cell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
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
        //PropertyPaneModelRegistry.getInstance().createInternalDescriptors();
        long start1 = System.currentTimeMillis();        
        
        //PropertyPaneModel ppc = PropertyPaneModelRegistry.getInstance().loadDefaultDescriptors();
        long end1 = System.currentTimeMillis();        
        
        
        PropertyPaneModel propertyPaneModel = PropertyPaneModelRegistry.getPropertyPaneModel();
        //List<BeanModel> descs = oldppc.getBeanModels();
        //PropertyPaneModelRegistry.getInstance().updateBy(ppc);
        long end = System.currentTimeMillis();        
        System.err.println("1) INTERVAL = " + (end1-start));
        
        System.err.println("2) INTERVAL = " + (end-start));
        //PropertyPaneModelRegistry.printBeanModel(Button.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(Labeled.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(ButtonBase.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(CheckBox.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(Hyperlink.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(MenuButton.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(ToggleButton.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(TitledPane.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(Cell.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(ButtonBar.class.getName(), true);        
        //PropertyPaneModelRegistry.printBeanModel(ComboBoxBase.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(Separator.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(Slider.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(SplitPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(Spinner.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(TableView.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(TreeTableView.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(TreeView.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(TabPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ToolBar.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(TextInputControl.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ListView.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(MenuBar.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(Pagination.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ProgressIndicator.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ScrollBar.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ScrollPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(ScrollPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(VBox.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(HBox.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(FlowPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(StackPane.class.getName(), true);                
        //PropertyPaneModelRegistry.printBeanModel(GridPane.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(TilePane.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(DialogPane.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(TextFlow.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(TextField.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(TextArea.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(Shape.class.getName(), true);
        //PropertyPaneModelRegistry.printBeanModel(Circle.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Arc.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(CubicCurve.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Ellipse.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Line.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Polyline.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Path.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Polygon.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(QuadCurve.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Rectangle.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(SVGPath.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Text.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Camera.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(PerspectiveCamera.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Canvas.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(LightBase.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Shape3D.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Box.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Cylinder.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Sphere.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Tooltip.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(Chart.class.getName(), true); 
        //PropertyPaneModelRegistry.printBeanModel(PieChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(XYChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(XYChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(AreaChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(LineChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(BarChart.class.getName(), true);         
        
        //PropertyPaneModelRegistry.printBeanModel(StackedAreaChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(StackedBarChart.class.getName(), true);         
        //PropertyPaneModelRegistry.printBeanModel(Group.class.getName(), true);                 
        //PropertyPaneModelRegistry.printBeanModel(WebView.class.getName(), true);                 
        //PropertyPaneModelRegistry.printBeanModel(Axis.class.getName(), true);                 
        //PropertyPaneModelRegistry.printBeanModel(CategoryAxis.class.getName(), true);                 
        //PropertyPaneModelRegistry.printBeanModel(ValueAxis.class.getName(), true);                 
        PropertyPaneModelRegistry.printBeanModel(NumberAxis.class.getName(), true);                 
        
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
