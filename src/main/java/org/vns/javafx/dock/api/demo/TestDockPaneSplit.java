/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.DockNode;

/**
 *
 * @author Valery
 */
public class TestDockPaneSplit extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        Button btn01 = new Button("sp btn01");
        
        StackPane root = new StackPane();
        DockPane cc = new DockPane();
        
        DockNode dn1 = new DockNode();
        dn1.setId("dn01");
        Button btn1 = new Button("BUTTON 1");
        dn1.setContent(btn1);
        dn1.setTitle("DockNode: dn1");        
//        cc.getItems().add(dn1);
        DockNode dn2 = new DockNode();
        dn2.setId("dn02");
        
        DockNode dn3 = new DockNode();
        
        dn3.setId("dn03");
        Button btn3 = new Button("BUTTON 3");
        dn3.setContent(btn3);
        dn3.setTitle("DockNode: dn3");        

        DockNode dn3_1 = new DockNode();
        dn3_1.setId("dn03_1");
        Button btn3_1 = new Button("BUTTON 3_1");
        dn3_1.setContent(btn3_1);
        dn3_1.setTitle("DockNode: dn3_1");       
        
/*        dn3_1.dockableController().setDividerPos(0.608);                
        
        dn1.dockableController().setDividerPos(0.346);                
        dn2.dockableController().setDividerPos(0.713);                
        dn3.dockableController().setDividerPos(0.608);                
*/        
        
        Button btn2 = new Button("BOTTON 2");
        
        btn1.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn1, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN1: sp.getDividers().size=" + sp.getDividers().size());
            //SplitPane.Divider d = sp.getDividers().get(0);
            /*System.err.println("divider.pos=" + d.getPosition());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
            DockUtil.print(root);
*/
        });
        
        btn2.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn2, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN2: sp.getDividers().size=" + sp.getDividers().size());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
            
        });

        btn3.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn3, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN3: sp.getDividers().size=" + sp.getDividers().size());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
            
        });
        
        btn3_1.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn3_1, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN3_1: sp.getDividers().size=" + sp.getDividers().size());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
            
        });
        
        dn2.setContent(btn2);
        dn2.setTitle("DockNode: dn2");  
        
        DockNode dnc1 = new DockNode("DockNodeControl dnc1");
        DockNode dnc2 = new DockNode("DockNodeControl dnc2");
        DockNode dnc3 = new DockNode("DockNodeControl dnc3");
        DockNode dnc4 = new DockNode("DockNodeControl dnc4");
/*        dnc1.dockableController().setDividerPos(0.346);                
        dnc2.dockableController().setDividerPos(0.713);                
        dnc3.dockableController().setDividerPos(0.608);                
*/        
        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        //cc.setRoot(vs1);        
        cc.getItems().add(vs1);
        
        //HPane hs1 = new HPaneOld(dnc1,dnc2);
        HPane hs1 = new HPane(dnc1,dnc2);
        
       // hs1.setDividerPos(0.66);
        hs1.setId("hs1");
        vs1.getItems().addAll(hs1,dnc3);
        //vs1.getItems().add(hs1);
        //vs1.getItems().add(dnc3);
        
        Button b1 = new Button("Items Count");
        Button b2 = new Button("add dnc4");
        Button b3 = new Button("remove dnc4");
        Button b4 = new Button("change dnc1 DividerPos");
        
        VBox content =  new VBox(b1,b2,b3,b4); 
        dnc1.setContent(content);
        b1.setOnAction(a -> {
            System.err.println("hs1.sz=" + hs1.getItems().size());
        });
        b2.setOnAction(a -> {
            System.err.println("(b2)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().add(dnc4);
        });
        b3.setOnAction(a -> {
            System.err.println("(b3)hs1.sz=" + hs1.getItems().size());
            hs1.getItems().remove(dnc4);
        });
        b4.setOnAction(a -> {
        });
        
        //cc.setRoot(vs1);
        //System.err.println("dn1 isDocked()=" + dn1.dockableController().isDocked());
        
        root.getChildren().add(cc);
        
        scene = new Scene(root, 250, 250);
        stage.setOnShown(a -> { 
            cc.handle(new ActionEvent());
        });
        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
System.err.println("SKIN = " + cc.getSkin());        
System.err.println("SKIN.node = " + cc.getSkin().getNode());        
       // SplitDelegate.DockSplitPane dsp = SplitDelegate.DockSplitPane.getParentSplitPane(dn3);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
    
    public static class CustomButton extends Button {

        public CustomButton() {
        }

        public CustomButton(String text) {
            super(text);
        }

        public CustomButton(String text, Node graphic) {
            super(text, graphic);
        }
        
        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
    }
}
