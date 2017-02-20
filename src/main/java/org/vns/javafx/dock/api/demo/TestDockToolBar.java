package org.vns.javafx.dock.api.demo;

import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockToolBar;

import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockTabPane2;

/**
 *
 * @author Valery
 */
public class TestDockToolBar extends Application {
    

    @Override
    public void start(Stage stage) throws Exception {
        
        DockPane dockPane = new DockPane();
        Button b1 = new Button("b01");
        Pane p1 = new HBox(b1);

        //dockPane.dock(p1, Side.TOP);

//////////////////////////////////////////////////////////        
        Button b2 = new Button("b02");
        Button bb1 = new Button("bb1");
        Pane p2 = new HBox(bb1);
        p2.setId("HBox p2");
        
        SplitPane sp01 = new SplitPane(new Label("splb01"),b2,new Label("splb01"),p2);        
        sp01.setId("Split sp01");
        VBox vb01 = new VBox(sp01);
        vb01.setId("VBox vb01");
        p2.getChildren().addAll(new Label("lb1"));
//        dockPane.dock(vb01, Side.RIGHT);
///////////////////////////////////////////////////////////////
        Button b3 = new Button("b03");
        Pane p3 = new HBox(b3);
//        dockPane.dock(p3, Side.BOTTOM);

        DockNode dn01 = new DockNode();
        dn01.setId("Dn01");
        dn01.setFocusTraversable(true);
        dn01.setContent(new Label("DOCK NODE IMPL"));
        dockPane.dock(dn01, Side.TOP);

        DockToolBar dtt = new DockToolBar();
        dn01.setId("Dtt");
        dtt.setFocusTraversable(true);
        dockPane.dock(dtt, Side.TOP);

        DockToolBar dtt02 = new DockToolBar();
        dtt02.setId(" Dtt02");
        //dn01.dock(dtt02, Side.BOTTOM);
        Button tb2 = new Button("", new Ellipse(8, 8, 8, 6));
        tb2.setId("Tb2");
        //dtt02.getToolBar().getItems().add(0, tb2);

        DockTabPane2 tabPane01 = new DockTabPane2();
        dockPane.dock(tabPane01, Side.BOTTOM);
        
        DockNode dn03 = new DockNode();
        TextArea ta = new TextArea();
        Button bClick = new Button("testGet");
        VBox vb = new VBox(bClick,ta);
        //tabPane01.dock(0, dn03);
/*        bClick.setOnAction(value -> {
            List<Parent> list = new ArrayList<>();
            Node p = DockUtil.getImmediateParent(dockPane, b2, list);
            System.err.println("FOUNT BUTTON b2 text=" + b2.getText());
            System.err.println("FOUNT BUTTON p=" + p);
            System.err.println("FOUNT BUTTON list.size()=" + list.size());
            list.forEach(el ->{
                System.err.println("CHAIH: el.class=" + el.getClass().getName() + "; id=" + el.getId());
            });
            System.err.println("=======================================");
            list.clear();
            p = DockUtil.getImmediateParent(dockPane, b2, list, (pr) -> {return (pr instanceof VBox ) || (pr instanceof SplitPane );});
            System.err.println("B. FOUNT BUTTON b2 text=" + b2.getText());
            System.err.println("B. FOUNT BUTTON list.size()=" + list.size());
            list.forEach(el ->{
                System.err.println("CHAIH: el.class=" + el.getClass().getName() + "; id=" + el.getId());
            });            
            System.err.println("=======================================");
            list.clear();
            p = DockUtil.getImmediateParent(dockPane, tb2, (pr) -> {return (pr instanceof Dockable );});
            System.err.println("C. FOUNT BUTTON p.id=" + p.getId());
            System.err.println("C. FOUNT BUTTON list.size()=" + list.size());
            list.forEach(el ->{
                System.err.println("C. CHAIH: el.class=" + el.getClass().getName() + "; id=" + el.getId());
            });            
            
            
        });
*/        
        Scene scene = new Scene(dockPane);

        stage.setTitle("Dockable and Toolbar");
        stage.setScene(scene);

        tb2.setFocusTraversable(false);

        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
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
