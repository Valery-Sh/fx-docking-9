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

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
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
public class EditorUtilTest {
    
    public EditorUtilTest() {
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
     * Test of parentOfLevel method, of class EditorUtil.
     */
    @Test
    public void testParentOfLevel() {
        System.out.println("parentOfLevel");
        TreeView treeView = null;
        TreeItem item = null;
        int level = 0;
        TreeItem expResult = null;
//        TreeItem result = EditorUtil.parentOfLevel(treeView, item, level);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getCell method, of class EditorUtil.
     */
    @Test
    public void testGetCell() {
        System.out.println("getCell");
        TreeItem<ItemValue> item = null;
        TreeCell expResult = null;
//        TreeCell result = EditorUtil.getCell(item);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of screenTreeItemBounds method, of class EditorUtil.
     */
    @Test
    public void testScreenTreeItemBounds() {
        System.out.println("screenTreeItemBounds");
        TreeItem<ItemValue> treeItem = null;
        Bounds expResult = null;
//        Bounds result = EditorUtil.screenTreeItemBounds(treeItem);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of screenHorVisibleBounds method, of class EditorUtil.
     */
    @Test
    public void testScreenHorVisibleBounds() {
        System.out.println("screenHorVisibleBounds");
        TreeViewEx treeView = null;
        TreeItem<ItemValue> treeItem = null;
        Bounds expResult = null;
//        Bounds result = EditorUtil.screenHorVisibleBounds(treeView, treeItem);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of screenTopVisibleBounds method, of class EditorUtil.
     */
    @Test
    public void testScreenTopVisibleBounds() {
        System.out.println("screenTopVisibleBounds");
        TreeViewEx treeView = null;
        TreeItem<ItemValue> treeItem = null;
        Bounds expResult = null;
//        Bounds result = EditorUtil.screenTopVisibleBounds(treeView, treeItem);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntersection method, of class EditorUtil.
     */
    @Test
    public void testGetIntersection() {
        System.out.println("getIntersection");
        Bounds b1 = new BoundingBox(10, 20, 50, 30);
        Bounds b2 = new BoundingBox(15, 25, 50, 30);
        Bounds expResult = new BoundingBox(15, 25, 45, 25);
        assertEquals(expResult, EditorUtil.getIntersection(b1, b2));
        assertEquals(expResult, EditorUtil.getIntersection(b2, b1));

        b1 = new BoundingBox(10, 20, 50, 30);
        b2 = new BoundingBox(15, 25, 10, 10);
        expResult = new BoundingBox(15, 25, 10, 10);
        assertEquals(expResult, EditorUtil.getIntersection(b1, b2));
        assertEquals(expResult, EditorUtil.getIntersection(b2, b1));

        b2 = new BoundingBox(15, 25, 10, 30);
        expResult = new BoundingBox(15, 25, 10, 25);
        assertEquals(expResult, EditorUtil.getIntersection(b1, b2));
        assertEquals(expResult, EditorUtil.getIntersection(b2, b1));
        
        
//        Bounds result = EditorUtil.getIntersection(b1, b2);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntersection method, of class EditorUtil.
     */
    @Test
    public void testTranslate() {
        System.out.println("translate");
        Bounds b1 = new BoundingBox(10, 20, 50, 30);
        Bounds expResult = new BoundingBox(10, 15, 50, 30);
        assertEquals(expResult, EditorUtil.translate(b1, 0,-5));
    }
    
    /**
     * Test of screenInsetsFreeBounds method, of class EditorUtil.
     */
    @Test
    public void testScreenInsetsFreeBounds() {
        System.out.println("screenInsetsFreeBounds");
        Region node = null;
        Bounds expResult = null;
//        Bounds result = EditorUtil.screenInsetsFreeBounds(node);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findTreeItemByObject method, of class EditorUtil.
     */
    @Test
    public void testFindTreeItemByObject() {
        System.out.println("findTreeItemByObject");
        TreeView treeView = null;
        Object sourceGesture = null;
        TreeItem<ItemValue> expResult = null;
//        TreeItem<ItemValue> result = EditorUtil.findTreeItemByObject(treeView, sourceGesture);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findChildTreeItem method, of class EditorUtil.
     */
    @Test
    public void testFindTreeItem_TreeItem_Object() {
        System.out.println("findTreeItem");
        TreeItem<ItemValue> item = null;
        Object sourceGesture = null;
        TreeItem<ItemValue> expResult = null;
//        TreeItem<ItemValue> result = EditorUtil.findChildTreeItem(item, sourceGesture);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findChildTreeItem method, of class EditorUtil.
     */
    @Test
    public void testFindTreeItem_3args() {
        System.out.println("findTreeItem");
        TreeView<ItemValue> treeView = null;
        double x = 0.0;
        double y = 0.0;
        TreeItem<ItemValue> expResult = null;
//        TreeItem<ItemValue> result = EditorUtil.findChildTreeItem(treeView, x, y);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getTargetTreeView method, of class EditorUtil.
     */
    @Test
    public void testGetTargetTreeView() {
        System.out.println("getTargetTreeView");
        double x = 0.0;
        double y = 0.0;
        TreeViewEx expResult = null;
//        TreeViewEx result = EditorUtil.getTargetTreeView(x, y);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
}
