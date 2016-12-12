package org.vns.javafx.dock.api.demo;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockToolBarTitled;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

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
        
        dockPane.dock(p1, Side.TOP);

        Button b2 = new Button("b02");
        Pane p2 = new HBox(b2);
        dockPane.dock(p2, Side.RIGHT);

        Button b3 = new Button("b03");
        Pane p3 = new HBox(b3);
        dockPane.dock(p3, Side.BOTTOM);
        
        DockNodeImpl dn01 = new DockNodeImpl();
        dn01.setId("Dn01");
        dn01.setFocusTraversable(true);
        dn01.getChildren().add(new Label("DOCK NODE IMPL"));
        dockPane.dock(dn01, Side.TOP);

        DockToolBarTitled dtt = new DockToolBarTitled();
        dn01.setId("Dtt");
        dtt.setFocusTraversable(true);        
        dockPane.dock(dtt, Side.TOP);

        DockToolBarTitled dtt02 =  new DockToolBarTitled();
        dtt02.setId(" Dtt02");
        dockPane.dock(dtt02, Side.BOTTOM, dn01);
        Button tb2 = new Button("", new Ellipse(8, 8, 8, 6));
        tb2.setId("Tb2");
        dtt02.getToolBar().getItems().add(0,tb2);
        
        Scene scene = new Scene(dockPane);

        stage.setTitle("Dockable and Toolbar");
        stage.setScene(scene);
        
        tb2.setFocusTraversable(false);
        tb2.setOnAction(value -> {
            Parent pp1 = (Parent) dockPane.getChildrenUnmodifiable().get(0);
            int sz = dockPane.getChildrenUnmodifiable().size();
            System.err.println("1. dockPane  sz=" + sz + "; class=" + pp1.getClass().getName());
            sz = pp1.getChildrenUnmodifiable().size();
            System.err.println("2. pp1  sz=" + sz);
            int i = 0;
            int j = 0;
            for (Node n : pp1.getChildrenUnmodifiable()) {
                System.err.println("   1." + (i++) + "); class=" + n.getClass().getName());
                for (Node n1 : ((Parent) n).getChildrenUnmodifiable()) {
                    System.err.println("      2.1." + (j++) + "); class=" + n1.getClass().getName());
                }

            }
            List<Node> list = new ArrayList<>();
            DockUtil.addAllDockable(pp1, list);
            Node focused = scene.getFocusOwner();
            String id = focused.getId();
            Node f = DockUtil.getFocusedDockable(focused);
            String id1 = f.getId();
            
            List<Node> list00 = getAllNodes(pp1);
            
            list00.forEach( n -> {
                if ( n.isFocused() ) {
                    System.err.println( "  --- list00 Dockable class=" + n.getClass().getName());
                }
            });
            
            List<Node> list01 = getAllNodes(pp1);
            List<Node> list02 = new ArrayList<>();
            i = 0;
            System.err.println( "  --- list size==" + list01.size());
            list01.forEach( n -> {
                if ( n instanceof Dockable) {
                    System.err.println( "  --- list01 Dockable class=" + n.getClass().getName());
                    list02.add(n);
                }
            });
            
            System.err.println( "     ---  list02 size==" + list02.size());
            
            list02.forEach( n -> {
                System.err.println( "        --- ; class=" + n.getClass().getName());
            });
            
            
            //Parent pp2 = (Parent) pp1.getChildrenUnmodifiable().get(0);
            //sz = pp2.getChildrenUnmodifiable().size();
            //System.err.println("2. sz=" + sz);
            //System.err.println("SIZE = " + ((Parent)dockPane.getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable().size());            
        });

        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet();

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
