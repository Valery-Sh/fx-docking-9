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


import org.vns.javafx.scene.control.editors.beans.NamedItem;
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
public class NamedItemTest {

    public NamedItemTest() {
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
     * Test of getName method, of class NamedItem.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        NamedItem instance = new NamedItemImpl("name01","sisplayName01");

        String expResult = "name01";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setName method, of class NamedItem.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "name01";
        NamedItem instance = new NamedItemImpl();
        instance.setName(name);
        assertEquals(name, instance.getName());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class NamedItem.
     */
    @Test
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        NamedItem instance = new NamedItemImpl();
        instance.setDisplayName("displayName01");
        String expResult = "displayName01";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setDisplayName method, of class NamedItem.
     */
    @Test
    public void testSetDisplayName() {
        System.out.println("setDisplayName");
        String displayName = "displayName01";
        NamedItem instance = new NamedItemImpl();
        instance.setDisplayName(displayName);
        assertEquals("displayName01", instance.getDisplayName());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    public class NamedItemImpl implements NamedItem {

        private String name;
        private String displayName;

        public NamedItemImpl() {
        }

        public NamedItemImpl(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

}
