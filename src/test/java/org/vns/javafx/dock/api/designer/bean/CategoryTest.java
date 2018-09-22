/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api.designer.bean;

import javafx.collections.ObservableList;
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
public class CategoryTest {

    public CategoryTest() {
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
     * Test of getCopyFor method, of class Category.
     */
    @Test
    public void testGetCopyFor() {
        System.out.println("getCopyFor");
        Class clazz = null;
        BeanModel ppd = null;
        Category instance = new Category();
        Category expResult = null;
        Category result = instance.getCopyFor(clazz, ppd);
        /*       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
         */
    }

    /**
     * Test of indexByName method, of class Category.
     */
    @Test
    public void testIndexByName() {
        System.out.println("indexByName");
        String sectionName = "";
        Category instance = new Category();
        int expResult = 0;
        int result = instance.indexByName(sectionName);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getByName method, of class Category.
     */
    @Test
    public void testGetByName() {
        System.out.println("getByName");
        String sectionName = "";
        Category instance = new Category();
        Section expResult = null;
        Section result = instance.getByName(sectionName);
//        assertEquals(expResult, result);
    }

    /**
     * Test of merge method, of class Category.
     */
    @Test
    public void testMerge() {
        System.out.println("merge");
        ObservableList<Section> secs = null;
        Category instance = new Category();
        //instance.merge(secs);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of updateBy method, of class Category.
     */
    @Test
    public void testUpdateBy() {
        System.out.println("updateBy");
        Section sec = null;
        Category instance = new Category();
        //instance.updateBy(sec);
    }


}
