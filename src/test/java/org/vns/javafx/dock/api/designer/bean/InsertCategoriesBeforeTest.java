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
public class InsertCategoriesBeforeTest {
    
    public InsertCategoriesBeforeTest() {
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
     * Test of getCategories method, of class InsertCategoriesBefore.
     */
    @Test
    public void testGetCategories() {
        System.out.println("getCategories");
        InsertCategoriesBefore instance = new InsertCategoriesBefore();
        ObservableList<Category> expResult = null;
        ObservableList<Category> result = instance.getInsertList();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getInsert method, of class InsertCategoriesBefore.
     */
    @Test
    public void testGetInsert() {
        System.out.println("getInsert");
        InsertCategoriesBefore instance = new InsertCategoriesBefore();
        ObservableList<Category> expResult = null;
        ObservableList<Category> result = instance.getInsertList();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
