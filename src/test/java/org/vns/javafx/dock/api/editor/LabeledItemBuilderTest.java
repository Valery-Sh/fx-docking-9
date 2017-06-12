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
package org.vns.javafx.dock.api.editor;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.vns.javafx.dock.JavaFXThreadingRule;

/**
 *
 * @author Valery
 */
public class LabeledItemBuilderTest {
    
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();    
    
    public LabeledItemBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    Stage stage;
    Scene scene;
    VBox sceneRoot;
    HBox viewRoot;
    SceneGraphView view;
    
    @Before
    public void setUp() {
        Button btn1 = new Button("btn1");
        Button btn2 = new Button("btn2");        
        viewRoot = new HBox(btn2);
        sceneRoot = new VBox(btn1, viewRoot);
        view = new SceneGraphView(viewRoot);
        
        scene = new Scene(sceneRoot);
        stage = new Stage();
        stage.setScene(scene);
        //stage.show();
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of build method, of class LabeledItemBuilder.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        Object obj = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        TreeItemEx expResult = null;
        TreeItemEx result = instance.build(viewRoot);
        System.err.println("!) root.size() = " + result);
        //System.err.println(" result = " + ((TreeItemEx)result.getChildren().get(0)).getObject());
        
//        TreeItemEx result = instance.build(obj);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of createDefaultContent method, of class LabeledItemBuilder.
     */
    @Test
    public void testCreateDefaultContent() {
        System.out.println("createDefaultContent");
        Object obj = null;
        Object[] others = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        Node expResult = null;
        Node result = instance.createDefaultContent(obj, others);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAcceptable method, of class LabeledItemBuilder.
     */
    @Test
    public void testIsAcceptable() {
        System.out.println("isAcceptable");
        Object obj = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        boolean expResult = false;
        //boolean result = instance.isAcceptable(obj);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAdmissiblePosition method, of class LabeledItemBuilder.
     */
    @Test
    public void testIsAdmissiblePosition() {
        System.out.println("isAdmissiblePosition");
        TreeView treeView = null;
        TreeItemEx target = null;
        TreeItemEx place = null;
        Object dragObject = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        boolean expResult = false;
        boolean result = instance.isAdmissiblePosition(treeView, target, place, dragObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateOnMove method, of class LabeledItemBuilder.
     */
    @Test
    public void testUpdateSourceSceneGraph() {
        System.out.println("updateSourceSceneGraph");
        TreeItemEx parent = null;
        TreeItemEx child = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        //instance.updateOnMove(parent, child);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createPlaceHolders method, of class LabeledItemBuilder.
     */
    @Test
    public void testCreatePlaceHolders() {
        System.out.println("createPlaceHolders");
        Object obj = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        TreeItem[] expResult = null;
        TreeItem[] result = instance.createPlaceHolders(obj);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPlaceHolderBuilder method, of class LabeledItemBuilder.
     */
    @Test
    public void testGetPlaceHolderBuilder() {
        System.out.println("getPlaceHolderBuilder");
        TreeItemEx placeHolder = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        TreeItemBuilder expResult = null;
        TreeItemBuilder result = instance.getPlaceHolderBuilder(placeHolder);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerChangeHandler method, of class LabeledItemBuilder.
     */
    @Test
    public void testRegisterChangeHandler() {
        System.out.println("registerChangeHandler");
        TreeItemEx item = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        instance.registerChangeHandler(item);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class LabeledItemBuilder.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        TreeViewEx treeView = null;
        TreeItemEx target = null;
        TreeItemEx place = null;
        Object sourceObject = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        instance.update(treeView, target, place, sourceObject);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unregisterObjectChangeHandler method, of class LabeledItemBuilder.
     */
    @Test
    public void testUnregisterObjectChangeHandler() {
        System.out.println("unregisterObjectChangeHandler");
        Object obj = null;
        LabeledItemBuilder instance = new LabeledItemBuilder();
        //instance.unregisterObjectChangeHandler(obj);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
