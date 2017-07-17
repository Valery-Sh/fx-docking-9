/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api;

import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.JavaFXThreadingRule;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.demo.TestDockPaneControl;

/**
 *
 * @author Valery
 */
public class DockPaneControllerTest {
    
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();    
    
    DockPane dockPane1 = null;
    DockPane dockPane2 = null;
    
    public DockPaneControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        StackPane stackPane = new StackPane();
        HBox root = new HBox();
        root.getChildren().add(stackPane);

        DockLoader loader = new DockLoader(TestDockPaneControl.class);
        dockPane1 = (DockPane) loader.register("dockPane1", DockPane.class);
        dockPane2 = (DockPane) loader.register("dockPane2", DockPane.class);
        
        dockPane1.setId("dockPane1");
        DockNode dnc1 = new DockNode();
        DockNode dnc2 = new DockNode();
        DockNode dnc3 = new DockNode();
        DockNode dnc4 = new DockNode("DockNodeControl dnc4");
        
        dnc1.setId("dnc1");
        dnc2.setId("dnc2");
        dnc3.setId("dnc3");
        dnc4.setId("dnc4");

        VPane vs1 = new VPane();
        vs1.setId("vs1");
        dockPane1.getItems().add(vs1);

        HPane hs1 = new HPane(dnc1, dnc2);
        hs1.setId("hs1");
        vs1.getItems().addAll(hs1, dnc3);
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getNodeIndicator method, of class DockPaneController.
     */
    @Test
    public void testGetNodeIndicator() {
        System.out.println("getNodeIndicator");
        DockPaneController instance = null;
        SideIndicator.NodeSideIndicator expResult = null;
        SideIndicator.NodeSideIndicator result = instance.getNodeIndicator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createIndicatorPopup method, of class DockPaneController.
     */
    @Test
    public void testCreateIndicatorPopup() {
        System.out.println("createIndicatorPopup");
        DockPaneController instance = null;
        IndicatorPopup expResult = null;
        IndicatorPopup result = instance.createIndicatorPopup();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createNodeIndicator method, of class DockPaneController.
     */
    @Test
    public void testCreateNodeIndicator() {
        System.out.println("createNodeIndicator");
        DockPaneController instance = null;
        SideIndicator.NodeSideIndicator expResult = null;
        SideIndicator.NodeSideIndicator result = instance.createNodeIndicator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createPositionIndicator method, of class DockPaneController.
     */
    @Test
    public void testCreatePositionIndicator() {
        System.out.println("createPositionIndicator");
        DockPaneController instance = null;
        PositionIndicator expResult = null;
        PositionIndicator result = instance.createPositionIndicator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDockExecutor method, of class DockPaneController.
     */
    @Test
    public void testGetDockExecutor() {
        System.out.println("getDockExecutor");
        DockPaneController instance = null;
        DockPaneController.DockExecutor expResult = null;
        DockPaneController.DockExecutor result = instance.getDockExecutor();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDocked method, of class DockPaneController.
     */
    @Test
    public void testIsDocked() {
        System.out.println("isDocked");
        Node node = null;
        DockPaneController instance = null;
        boolean expResult = false;
        boolean result = instance.isDocked(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dock method, of class DockPaneController.
     */
    @Test
    public void testDock_Point2D_Dockable() {
        System.out.println("dock");
        Point2D mousePos = null;
        Dockable dockable = null;
        DockPaneController instance = null;
        instance.dock(mousePos, dockable);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doDock method, of class DockPaneController.
     */
    @Test
    public void testDoDock() {
        System.out.println("doDock");
        Point2D mousePos = null;
        Node node = null;
        DockPaneController instance = null;
        boolean expResult = false;
        boolean result = instance.doDock(mousePos, node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dock method, of class DockPaneController.
     */
    @Test
    public void testDock_Dockable_Object() {
        System.out.println("dock");
        Dockable dockable = null;
        Object pos = null;
        DockPaneController instance = null;
        //instance.dock(dockable, pos);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dock method, of class DockPaneController.
     */
    @Test
    public void testDock_3args() {
        System.out.println("dock");
        Dockable dockNode = null;
        Side side = null;
        Dockable target = null;
        DockPaneController instance = null;
        instance.dock(dockNode, side, target);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class DockPaneController.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Node dockNode = null;
        DockPaneController instance = null;
        instance.remove(dockNode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDockTreeTemBuilder method, of class DockPaneController.
     */
    @Test
    public void testGetPreferencesBuilder() {
        System.out.println("getPreferencesBuilder");
        DockPaneController instance = null;
        DockTreeItemBuilder expResult = null;
        DockTreeItemBuilder result = instance.getDockTreeTemBuilder();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDockables method, of class DockPaneController.
     */
    @Test
    public void testGetDockables() {
        System.out.println("getDockables");
        DockPaneController instance = null;
        ObservableList<Dockable> expResult = null;
        ObservableList<Dockable> result = instance.getDockables();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    @Test
    public void testPreferencesBuilder_build_1() {
        System.out.println("PreferencesBuilder_build");
        DockPaneController instance = (DockPaneController) dockPane2.targetController();
        TreeItem<Pair<ObjectProperty, Properties>> expResult = null;
/*        TreeItem<Pair<ObjectProperty, Properties>> result = instance.getDockTreeTemBuilder().build(dockPane2);
        assertNotNull(result);
        assertTrue(result.getChildren().isEmpty());
        Pair<ObjectProperty, Properties> pair = result.getValue();
        assertTrue(pair.getKey().get() == dockPane2);
        Properties props = pair.getValue();
        assertFalse(props.isEmpty());
        assertEquals("org.vns.javafx.dock.DockPane",props.getProperty("-ld:className") );
        assertTrue(props.get("-ignore:treeItem") == result);        
*/
    }   
    @Test
    public void testPreferencesBuilder_restoreFrom_1() {
        System.out.println("PreferencesBuilder_restoreFrom");
        DockPaneController instance = (DockPaneController) dockPane2.targetController();
        TreeItem<Pair<ObjectProperty, Properties>> expResult = null;
/*        TreeItem<Pair<ObjectProperty, Properties>> result = instance.getDockTreeTemBuilder().xmlBuild(dockPane2);
        assertNotNull(result);
        assertTrue(result.getChildren().isEmpty());
        Pair<ObjectProperty, Properties> pair = result.getValue();
        assertTrue(pair.getKey().get() == dockPane2);
        Properties props = pair.getValue();
        assertFalse(props.isEmpty());
        assertEquals("org.vns.javafx.dock.DockPane",props.getProperty("-ld:className") );
        assertTrue(props.get("-ignore:treeItem") == result);        
*/        
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }   
    
}
