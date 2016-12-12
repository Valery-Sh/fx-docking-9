/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import org.vns.javafx.dock.DockPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.vns.javafx.dock.JavaFXThreadingRule;
import org.vns.javafx.dock.api.DockSplitDelegate.DockSplitPane;

/**
 *
 * @author Valery
 */
public class DockPaneTest {
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
     * Test of getDelegate method, of class DockPane.
     */
    @Test
    public void testGetDelegate() {
        System.out.println("getDelegate");
        DockPane instance = new DockPane();
        DockPaneDelegate expResult = null;
    }

    /**
     * Test of dock method, of class DockPane.
     */
    @Test
    public void testDock() {
        System.out.println("dock");
        Node dockable = new Button("b1-top");
        Side dockPos = Side.TOP;
        DockPane instance = new DockPane();
        instance.dock(dockable, dockPos);
        
        assertEquals(1,instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof DockSplitPane);
        DockSplitPane dsp = (DockSplitPane) instance.getChildren().get(0);
        assertEquals(1,dsp.getItems().size());
        
        dockable = new Button("b2-left");
        dockPos = Side.LEFT;
        
        instance.dock(dockable, dockPos);
        assertEquals(1,instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof DockSplitPane);
        dsp = (DockSplitPane) instance.getChildren().get(0);
        assertEquals(2,dsp.getItems().size());
        

        dockable = new Button("b3-right");
        dockPos = Side.RIGHT;
        
        instance.dock(dockable, dockPos);
        assertEquals(1,instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof DockSplitPane);
        dsp = (DockSplitPane) instance.getChildren().get(0);
        assertEquals(3,dsp.getItems().size());

        dockable = new Button("b4");
        dockPos = Side.BOTTOM;
        
        
        
        instance.dock(dockable, dockPos);
        //
        // [
        //    [ [b1,b2,b3] ]
        //    b4
        // ]
        //
        assertEquals(1,instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof DockSplitPane);
        dsp = (DockSplitPane) instance.getChildren().get(0);
        
        assertEquals(2,dsp.getItems().size());
        //
        // dsp01 = [b1,b2,b3]
        //
        DockSplitPane dsp01 = (DockSplitPane) dsp.getItems().get(0);
        
        assertEquals(3,dsp01.getItems().size());
        
        assertTrue(dsp.getItems().get(1) instanceof Button);
        assertEquals("b4",((Button)dsp.getItems().get(1)).getText());
    }
    @Test
    public void testDock_1() {
        System.out.println("dock");
        Node dockable = new DockNodeImpl();
        
        Side dockPos = Side.TOP;
        DockPane instance = new DockPane();
        instance.dock(dockable, dockPos);
        
        assertEquals(1,instance.getChildren().size());
        
    }
}
