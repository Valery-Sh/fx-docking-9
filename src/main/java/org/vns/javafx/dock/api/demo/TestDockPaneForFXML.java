/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.Dockable;

/*
<StackPane id="rootPane" prefHeight="200" prefWidth="320" xmlns:fx="http://javafx.com/fxml/1" fx:controller="org.me.fxml.FXMLDocumentController01">
    <DockPane fx:id="dockPane" prefHeight="200" prefWidth="320" >
        <HPane fx:id="hpaneRoot" >
            <VPane fx:id="vpane1">
                <HPane fx:id="hpid" dividerPos="0.66">
                    <DockNode fx:id="dn01" title="Dn01" dividerPos="0.3">
                        <VBox>
                            <Button text="Btn01: Click Me!" onAction="#handleButtonAction" fx:id="btn01" />
                            <Button text="Btn02: Click Me!" fx:id="btn02" />
                        </VBox>
                    </DockNode>    
                    <VPane fx:id="vpane2" >
                        <DockNode fx:id="dn02" title="Dn02">
                            <titleBar>
                                <ToolBar>
                                    <Button text="ToolBar Button"/>
                                </ToolBar>    
                            </titleBar> 
                        </DockNode>    
                        <DockNode fx:id="dn03" title="Dn03"  />
                    </VPane>
                </HPane>
                <DockNode fx:id="dn04" title="Dn04" removeTitleBar="true" dragNode="$dn04" />
            </VPane>                 
            <!--DockNode fx:id="lastNode" title="RRR" dividerPos="0.5" >
            </DockNode>
            <DockSideBar fx:id="sb1" orientation="VERTICAL" hideOnExit="true" rotation="UP_DOWN" side="RIGHT" >
                <DockNode title="Side Bar Btn" />
            </DockSideBar-->
        </HPane>    

    </DockPane>
</StackPane>

*/


/**
 *
 * @author Valery
 */
public class TestDockPaneForFXML extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) {
        
        //SplitPane sp1 = new SplitPane();
        stage.setTitle("PRIMARY");
        DockNode dn01 = new DockNode("dn01");    
        DockNode dn02 = new DockNode("dn02");                
        DockNode dn03 = new DockNode("dn03");                
        DockNode dn04 = new DockNode("dn04");                
        DockNode dn05 = new DockNode("dn05");                
        
        VPane vp01 = new VPane();
        VPane vp02 = new VPane();
        
        HPane hp01 = new HPane();        
        HPane hp02 = new HPane();        
        hp01.setId("hp01");        
        hp02.setId("hp02");
        vp01.setId("vp01");        
        vp02.setId("vp02");        
        StackPane root = new StackPane();
        DockPane dockPane = new DockPane();
        root.getChildren().add(dockPane);
        HPane hpRoot = new HPane();
        hpRoot.setId("hpRoot");
        dockPane.setRoot(hpRoot);                                
        hpRoot.getItems().add(vp01);
        //hpRoot.getItems().addAll(vp01,dn05);
        vp01.getItems().addAll(hp01,dn04);
        DockUtil.print(hpRoot);
        
        hp01.setDividerPos(0.66);
        hp01.getItems().addAll(dn01,vp02);

        
        vp02.getItems().addAll(dn02,dn03);
        //dockPane.setRoot(hpRoot); 
        
        //stage.setOnShown(e->{hp01.setDividerPos(0.66);});
        scene = new Scene(root, 250, 250);

        scene.getRoot().setStyle("-fx-background-color: rgb(223,223,223)");

        stage.setScene(scene);
        stage.show();
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
