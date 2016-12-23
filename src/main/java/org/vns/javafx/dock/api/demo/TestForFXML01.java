package org.vns.javafx.dock.api.demo;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.TitledToolBar;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestForFXML01 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DockPane dockPane = new DockPane();
        Button b1 = new Button("b01");
        TitledToolBar dtt = new TitledToolBar();

        b1.setLayoutX(126);
        b1.setLayoutY(90);
        dockPane.dock(dtt, Side.TOP);
        TitledToolBar dtt01 = new TitledToolBar();
        dtt01.getChildren().add(b1);
        dtt.dock(dtt01, Side.TOP);
        List<Parent> chain = new ArrayList<>();
        chain = DockUtil.findNodes(dockPane, p -> {
            return (p instanceof Dockable) || (p instanceof SplitPane);
        });
        
        
        //DockUtil.getImmediateParent(dockPane, b1, chain, p -> {
//            return ( p instanceof Dockable) || ( p instanceof SplitPane);
//        });
        for (Parent p : chain) {
            //System.err.println("1. " + p.getClass().getName());
        }
        TitledToolBar dtt02 = new TitledToolBar();

        //dockPane.getChildren().add(b1);
        Scene scene = new Scene(dockPane);

        stage.setTitle("Dockable and Toolbar");
        stage.setScene(scene);

        stage.show();

        HBox pn01 = new HBox();
        pn01.setId("pn01");
        HBox pn01_pn02 = new HBox();
        pn01_pn02.setId("pn01_pn02");
        pn01.getChildren().add(pn01_pn02);
        
        dockPane.getChildren().add(pn01);
        SplitPane sp01 = new SplitPane();
        sp01.setId("Sp01");
        pn01.getChildren().add(sp01);
        Button bb01 = new Button("BBBBBBBBBBBBBBBBBBBB");
        bb01.setId("bb01");
        Button bb02 = new Button();
        bb02.setId("bb02");
        
        VBox vb01 = new VBox(bb01);
        vb01.setId("VB01");
        //SplitPane sp01_vb01 = new SplitPane(vb01);
        SplitPane sp01_sp02 = new SplitPane(vb01);
        sp01_sp02.setId("sp01_sp02");
        
        sp01.getItems().add(sp01_sp02);
        sp01.getItems().add(bb02);
        
        bb01.setOnAction((ActionEvent aa) -> {
            System.err.println("0**************************");
            
            List list = new ArrayList();

            List<Parent> c = DockUtil.getParentChain(pn01, bb01, p -> {
                //return (p instanceof VBox) || (p instanceof SplitPane);
                return ((p instanceof Control) || (p instanceof Pane)) 
                    && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });

            for ( Object o : c ) {
               Node nn = (Node) o;
               System.err.println("   --- id = " + nn.getId() + "; class=" + nn.getClass().getName());
            }
            c.clear();
            Parent imParent = DockUtil.getImmediateParent(pn01, bb01, p -> {
                //return (p instanceof VBox) || (p instanceof SplitPane);
                return ((p instanceof Control) || (p instanceof Pane)) 
                    && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });
            System.err.println("   --- imPatent id = " + imParent.getId() + "; class=" + imParent.getClass().getName());
            
            DockUtil.print(pn01, 1, " ", p -> {
                return ((p instanceof Control) || (p instanceof Pane)) 
                && ! ( p.getClass().getName().startsWith("com.sun.javafx"));
            });
            
            System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            
            dtt01.dock(dtt02,Side.LEFT);

            
            
        });

        //System.err.println("1. SIZE ====== " + l.size());
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
