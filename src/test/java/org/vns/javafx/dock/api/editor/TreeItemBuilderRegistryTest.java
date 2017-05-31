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

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Valery
 */
public class TreeItemBuilderRegistryTest {
    
    public static interface EventTargetEx extends EventTarget {};
    

    public TreeItemBuilderRegistryTest() {
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
     * Test of getBuilder method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testGetBuilder() {
        System.out.println("getBuilder");
        Object o = null;
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = null;
        //TreeItemBuilder result = instance.getBuilder(o);
        //assertEquals(expResult, result);
    }

    /**
     * Test of find method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFind_Class() {
        System.out.println("find");
        
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = instance.find(Node.class);
        assertTrue(expResult instanceof  DefaultTreeItemBuilder);
    }
    /**
     * Test of find method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFind_Class_1() {
        System.out.println("find");
        Class clazz = null;
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = instance.find(Labeled.class);
        //TreeItemBuilder result = instance.find(Parent.class);
        assertTrue(expResult instanceof  LabeledItemBuilder);
    }
    /**
     * Test of find method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFind_Class_2() {
        System.out.println("find");
        Class clazz = null;
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = instance.find(Labeled.class);
        //TreeItemBuilder result = instance.find(Parent.class);
        assertTrue(expResult instanceof  LabeledItemBuilder);
    }
    
    /**
     * Test of find method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFind_Class_3() {
        System.out.println("find");
        Class clazz = EventTargetEx.class;
        
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        instance.register(EventTarget.class, new LabeledItemBuilder());
        TreeItemBuilder expResult = instance.find(clazz);
        assertTrue(expResult instanceof  LabeledItemBuilder);
        
    }
    /**
     * Test of find method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFind_ClassArr() {
        System.out.println("find");
        Class[] interfaces = null;
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = null;
        //TreeItemBuilder result = instance.find(interfaces);
        //assertEquals(expResult, result);
    }

    /**
     * Test of findForInterface method, of class TreeItemBuilderRegistry.
     */
    @Test
    public void testFindForInterface() {
        System.out.println("findForInterface");
        Class clazz = null;
        TreeItemBuilderRegistry instance = TreeItemBuilderRegistry.getInstance();
        TreeItemBuilder expResult = null;
        //TreeItemBuilder result = instance.findForInterface(clazz);
        //assertEquals(expResult, result);
    }

}
