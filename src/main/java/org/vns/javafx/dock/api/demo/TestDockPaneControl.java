/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode2;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.SplitDelegate;
import org.vns.javafx.dock.api.controls.DockNode;
import org.vns.javafx.dock.api.controls.DockPane;

/**
 *
 * @author Valery
 */
public class TestDockPaneControl extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        Button btn01 = new Button("sp btn01");
        
        StackPane root = new StackPane();
        DockPane cc = new DockPane();
        DockNode2 dn1 = new DockNode2();
        dn1.setId("dn01");
        Button btn1 = new Button("BUTTON 1");
        dn1.getChildren().add(btn1);
        dn1.setTitle("DockNode: dn1");        
        
        DockNode2 dn2 = new DockNode2();
        dn2.setId("dn02");
        
        DockNode2 dn3 = new DockNode2();
        
        dn3.setId("dn03");
        Button btn3 = new Button("BUTTON 3");
        dn3.getChildren().add(btn3);
        dn3.setTitle("DockNode: dn3");        

        DockNode2 dn3_1 = new DockNode2();
        dn3_1.setId("dn03_1");
        Button btn3_1 = new Button("BUTTON 3_1");
        dn3_1.getChildren().add(btn3_1);
        dn3_1.setTitle("DockNode: dn3_1");       
        
        dn3_1.nodeHandler().setDividerPos(0.608);                
        
        dn1.nodeHandler().setDividerPos(0.346);                
        dn2.nodeHandler().setDividerPos(0.713);                
        dn3.nodeHandler().setDividerPos(0.608);                
        
        
        Button btn2 = new Button("BOTTON 2");
        
        btn1.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn1, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN1: sp.getDividers().size=" + sp.getDividers().size());
            //SplitPane.Divider d = sp.getDividers().get(0);
            /*System.err.println("divider.pos=" + d.getPosition());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
            */
            DockUtil.print(root);
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
        
        dn2.getChildren().add(btn2);
        dn2.setTitle("DockNode: dn2");  
        
        DockNode dnc1 = new DockNode("DockNodeControl dnc1");
        DockNode dnc2 = new DockNode("DockNodeControl dnc2");
        DockNode dnc3 = new DockNode("DockNodeControl dnc3");
        
        VPane vs1 = new VPane();
        
        HPane hs1 = new HPane(dnc1,dnc2);
        vs1.getItems().addAll(hs1,dnc3);
        
        cc.setRoot(vs1);
//        cc.dock(dn1, Side.TOP);
//        cc.dock(dn2, Side.RIGHT);
        
        //dn1.nodeHandler().setPaneHandler(cc.getDelegate().paneHandler());
        System.err.println("dn1 isDocked()=" + dn1.nodeHandler().isDocked());
        //cc.getDelegate().paneHandler().i
        //dn1.nodeHandler().setDocked(true);
        //cc.addItem(dn1,Side.RIGHT);
        //hs1.getItems().add(dn3);
        
        //cc.addItem(dn3,Side.RIGHT);
//        cc.addItem(dn2,Side.RIGHT);
//        cc.addItem(dn3,Side.RIGHT);
//        cc.addItem(dn3,Side.TOP);
//        cc.addItem(dn3_1, Side.RIGHT, dn3);

        
        root.getChildren().add(cc);
        
        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        SplitDelegate.DockSplitPane dsp = SplitDelegate.DockSplitPane.getParentSplitPane(dn3);

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
