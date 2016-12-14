/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.vns.javafx.dock.api.SplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneTest {//extends Application{
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();    
    public DockPaneTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * 
     */
    @org.junit.Test //(expected = IllegalArgumentException.class)
    public void testSomeMethod() {
        // TODO review the generated test code and remove the default call to fail.
        VBox vb = new VBox();
        Button b1 = new Button("b1");
        Button b0 = new Button("b0");
        vb.getChildren().add(b0);
        Node p = b0.getParent();  
        
        DockSplitPane dsp = new DockSplitPane(b1,new Button("b2"));
        
        SplitPane sp = new SplitPane();        
        DockPane pane = new DockPane();
        Scene scene = new Scene(pane);
        pane.getChildren().add(dsp);
        Node p1 = b1.getParent();  
        
    }

    
}
