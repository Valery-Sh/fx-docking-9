/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SplitDelegate;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;
import org.vns.javafx.dock.DockNode;

/**
 *
 * @author Valery
 */
public class TestCustomControl extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PRIMARY");
        Button btn01 = new Button("sp btn01");
        //DockSplitPane ddd = new DockSplitPane(btn01);
        StackPane root = new StackPane();
        CustomControl cc = new CustomControl();
        DockNode dn1 = new DockNode();
 
        dn1.setId("dn01");
        Button btn1 = new Button("BUTTON 1");
        dn1.setContent(btn1);
        dn1.setTitle("DockNode: dn1");        
        
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
        
        dn3_1.nodeHandler().setDividerPos(0.608);                
        
        dn1.nodeHandler().setDividerPos(0.346);                
        dn2.nodeHandler().setDividerPos(0.713);                
        dn3.nodeHandler().setDividerPos(0.608);                
        
        SplitPane spsp = new SplitPane();
        System.err.println("sp.Orient=" + spsp.getOrientation());
        
        Platform.runLater(() -> {
        SplitPane sp1 = (SplitPane) DockUtil.getImmediateParent(cc, dn2, nd-> {return  (nd instanceof SplitPane);});
        //System.err.println("sp.getDividers().size=" + sp1.getDividers().size());
        //Divider d1 = sp1.getDividers().get(0);
        
        //System.err.println("divider.pos=" + d1.getPosition());        
        });
        //dn2.setPrefHeight(188);
        //dn2.setMinHeight(188);
        Button btn2 = new Button("BOTTON 2");
        
        btn1.setOnAction(a-> {
            SplitPane sp = (SplitPane) DockUtil.getImmediateParent(cc, dn1, nd-> {return  (nd instanceof SplitPane);});
            System.err.println("DN1: sp.getDividers().size=" + sp.getDividers().size());
            Divider d = sp.getDividers().get(0);
            System.err.println("divider.pos=" + d.getPosition());
            sp.getDividers().forEach(di -> {
                System.err.println(" --- pos=" + di.getPosition());
            });
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

        cc.addItem(dn1,Side.TOP);
        //cc.addItem(dn2,Side.TOP);
        //cc.addItem(dn2,Side.BOTTOM);
        cc.addItem(dn3,Side.TOP);
        cc.addItem(dn3_1, Side.RIGHT, dn3);

        
        root.getChildren().add(cc);
        
        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
        

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
