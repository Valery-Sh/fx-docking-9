package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.dragging.DragType;
import org.vns.javafx.dock.api.event.DockEvent;

/**
 *
 * @author Valery
 */
public class TestDockNodeControl extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DockPane dockPane = new DockPane();
        dockPane.addEventFilter(DockEvent.NODE_DOCKED, ev -> {
            System.err.println("DockPane eventFilter: getSource()       = " + ev.getSource());
            System.err.println("DockPane eventFilter: getDockedNode()   = " + ev.getDockedNode());            
            System.err.println("DockPane eventFilter: getTarget() = " + ev.getTarget());                        
            System.err.println("DockPane eventFilter: getTargetNode() = " + ev.getTargetNode());                                    
            System.err.println("DockPane eventFilter: getDockPosition()  0 = " + ev.getDockPosition()[0] + "; 1 = " + ev.getDockPosition()[1] );                                                
        });
        dockPane.addEventFilter(DockEvent.NODE_UNDOCKED, ev -> {
            System.err.println("DockPane eventFilter: getDockedNode()   = " + ev.getDockedNode());            
            System.err.println("DockPane eventFilter: getTargetNode() = " + ev.getTargetNode());                                    
            System.err.println("DockPane eventFilter: getDockPosition().length = " + ev.getDockPosition().length);
        });
        
        //dockPane.getTargetContext().setDragType(DragType.DRAG_AND_DROP);
        //dockPane.addEventHandler( new );
        dockPane.setId("DOCK PANE");
        //DockEvent ev = new DockEvent(null, dockPane,DockEvent.NODE_DOCKED);
        Button b1 = new Button("Add or Remove TitleBar");
        Button b2 = new Button("b2r");
        //b1.setGraphic(b2);
        Pane p1 = new HBox(b1);
        DockNode custom = new DockNode();
        dockPane.getItems().add(custom);
        custom.setId("custom");
        DockNode custom1 = new DockNode();
        custom1.setTitle("CUSTOM 1");
        dockPane.getItems().add(custom1);
        custom1.setId("custom1");        
        //custom1.setContent(b2);                
        b1.setOnAction(a->{
            custom1.setContent(b2);
            if ( custom.getTitleBar() == null ) {
                custom1.setContent(b2);                
                //custom.getContext().createDefaultTitleBar("Now Not Null");
            } else {
                //custom.setTitleBar(null);
                //custom.setRemoveTitleBar(true);
            }
            //b1.getScene().getWindow().setX(40);
            //b1.getScene().getWindow().setY(40);
            DockUtil.print(dockPane);
            
        });
        
        //TitledPane tp = new TitledPane();
        //dockPane.getChildren().add(tp);
        //tp.setContent(p1);
//        p1.getChildren().add(b1);
        //custom.setPrefSize(100, 100);
        custom.setContent(p1);
        
        p1.setId("pane p1");
        DockUtil.print(dockPane);
        //dockPane.dock(p1, Side.TOP).getContext().setTitle("Pane p1");
        Scene scene = new Scene(dockPane);
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        
        stage.setOnShown(s -> {
            //((Pane)custom.getContent()).getChildren().forEach(n -> {System.err.println("custom node=" + n);});
            //System.err.println("tp.lookup(arrowRegion)" + tp.);
            DockUtil.print(b1);
        });
        stage.setAlwaysOnTop(true);
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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
