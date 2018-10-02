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

import javafx.collections.FXCollections;
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
public class NamedItemListTest {

    public NamedItemListTest() {
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
     * Test of getItems method, of class NamedItemList.
     */
    @Test
    public void testGetItems() {
        System.out.println("getItems()");
        NamedItemList instance = new NamedItemListImpl();
        assertNotNull(instance.getItems());
    }

    /**
     * Test of addItemsAfter method, of class NamedItemList.
     */
    @Test
    public void testAddItemsAfter() {
        System.out.println("addItemsAfter(NamedItemImpl after, NamedItemImpl... scs)");
        NamedItemImpl after = new NamedItemImpl("after", "displayName01");
        //
        // Try test empty array
        //
        NamedItemImpl[] scs = new NamedItemImpl[0];
        NamedItemListImpl instance = new NamedItemListImpl();
        instance.addItemsAfter(after, scs);
        assertEquals(0, instance.getItems().size());

        //
        // Add first item
        //
        scs = new NamedItemImpl[]{after};
        instance.addItemsAfter(after, scs);
        assertEquals(1, instance.getItems().size());
        //
        // Try add two items after the item with the index 0
        //
        NamedItemImpl sec01 = new NamedItemImpl("name01", "displayName01");
        NamedItemImpl sec02 = new NamedItemImpl("name02", "displayName02");
        scs = new NamedItemImpl[]{sec01, sec02};

        instance.addItemsAfter(after, scs);

        assertEquals(after, instance.getItems().get(0));
        assertEquals(sec01, instance.getItems().get(1));
        assertEquals(sec02, instance.getItems().get(2));
        //
        // Try add items when after parameter is null  doesn't exist
        //
        NamedItemImpl sec03 = new NamedItemImpl("name03", "displayName03");
        scs = new NamedItemImpl[]{sec03};
        instance.addItemsAfter(null, scs);

        assertEquals(after, instance.getItems().get(0));
        assertEquals(sec01, instance.getItems().get(1));
        assertEquals(sec02, instance.getItems().get(2));
        assertEquals(sec03, instance.getItems().get(3));
        //
        // Try add after the last item
        //        
        NamedItemImpl sec04 = new NamedItemImpl("name04", "displayName04");
        scs = new NamedItemImpl[]{sec04};
        instance.addItemsAfter(sec03, scs);

        assertEquals(after, instance.getItems().get(0));
        assertEquals(sec01, instance.getItems().get(1));
        assertEquals(sec02, instance.getItems().get(2));
        assertEquals(sec03, instance.getItems().get(3));
        assertEquals(sec04, instance.getItems().get(4));
        //
        // Try add after the item 0
        //        
        NamedItemImpl sec05 = new NamedItemImpl("name05", "displayName05");
        NamedItemImpl sec06 = new NamedItemImpl("name01", "displayName06");
        NamedItemImpl sec07 = new NamedItemImpl("name05", "displayName07");
        NamedItemImpl sec08 = new NamedItemImpl("name08", "displayName08");
        scs = new NamedItemImpl[]{sec05, sec06, sec07, sec08};
        instance.addItemsAfter(after, scs);

        assertEquals(after, instance.getItems().get(0));
        assertEquals(sec05, instance.getItems().get(1));
        assertEquals(sec08, instance.getItems().get(2));

        assertEquals(sec01, instance.getItems().get(3));
        assertEquals(sec02, instance.getItems().get(4));
        assertEquals(sec03, instance.getItems().get(5));
        assertEquals(sec04, instance.getItems().get(6));
    }

    /**
     * Test of addItemsBefore method, of class NamedItemList.
     */
    @Test
    public void testAddItemsBefore() {
        System.out.println("addItemsBefore(NamedItemImpl before, NamedItemImpl... items)");
        NamedItemImpl before = new NamedItemImpl("before", "displayName01");
        //
        // Try test empty array
        //
        NamedItemImpl[] scs = new NamedItemImpl[0];
        NamedItemListImpl instance = new NamedItemListImpl();
        instance.addItemsBefore(before, scs);
        assertEquals(0, instance.getItems().size());
        scs = new NamedItemImpl[]{before};

        instance.addItemsBefore(before, scs);
        assertEquals(1, instance.getItems().size());
        //
        // Try add two itemss before the item with the index 0
        //
        NamedItemImpl sec01 = new NamedItemImpl("name01", "displayName01");
        NamedItemImpl sec02 = new NamedItemImpl("name02", "displayName02");
        scs = new NamedItemImpl[]{sec01, sec02};

        instance.addItemsBefore(before, scs);

        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        assertEquals(before, instance.getItems().get(2));

        //
        // Try add itemss when before doesn't exist
        //
        NamedItemImpl sec03 = new NamedItemImpl("name03", "displayName03");
        scs = new NamedItemImpl[]{sec03};
        instance.addItemsBefore(null, scs);

        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        assertEquals(before, instance.getItems().get(2));
        assertEquals(sec03, instance.getItems().get(3));
        //
        // Try add before last
        //        
        NamedItemImpl sec04 = new NamedItemImpl("name04", "displayName04");
        scs = new NamedItemImpl[]{sec04};
        instance.addItemsBefore(sec03, scs);

        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        assertEquals(before, instance.getItems().get(2));
        assertEquals(sec04, instance.getItems().get(3));
        assertEquals(sec03, instance.getItems().get(4));


    }

    /**
     * Test of addItem method, of class NamedItemList.
     */
    @Test
    public void testAddOrUpdateItem() {
        System.out.println("addItem(int idx,NamedItemImpl)");
        int idx = 0;
        NamedItemImpl sec = new NamedItemImpl("secName01", "dispalyName01");
        NamedItemListImpl instance = new NamedItemListImpl();
        NamedItemImpl expResult = sec;
        NamedItemImpl result = instance.addOrUpdateItem(idx, sec);
        assertEquals(expResult, result);
        //
        // Try add the same item with the same name and another displayName
        //
        expResult = sec;
        result = instance.addOrUpdateItem(idx, sec);
        assertEquals(expResult, result);
        //
        // We expect that we cannot add the same object of type NamedItemImpl
        //
        assertEquals(1, instance.getItems().size());
        //
        // Try add new items with the same name and displayName
        // We expect that we cannot add anobject of type NamedItemImpl with the same 
        // name
        //
        NamedItemImpl sec01 = new NamedItemImpl("secName01", "secDispalyName01");
        expResult = sec01;
        result = instance.addOrUpdateItem(idx, sec01);
        assertNotEquals(expResult, result);
        assertEquals(1, instance.getItems().size());

        //
        // Try add new item with the same name and another displayName.
        // We expect that we cannot add an object of type NamedItemImpl with the same 
        // name, but the displayName will change
        //
        NamedItemImpl sec02 = new NamedItemImpl("secName01", "secDispalyName02");
        expResult = sec02;
        result = instance.addOrUpdateItem(idx, sec02);
        assertNotEquals(expResult, result);
        assertEquals(1, instance.getItems().size());
        assertEquals("secDispalyName02", sec.getDisplayName());
        //
        // Try add new item with the same name and another displayName.
        // We expect that we cannot add an object of type NamedItemImpl with not 
        // the name that is unique among other itemss the the object wil be added
        //
        NamedItemImpl sec03 = new NamedItemImpl("secName03", "secDispalyName03");
        expResult = sec03;
        result = instance.addOrUpdateItem(idx, sec03);
        assertEquals(expResult, result);
        assertEquals(2, instance.getItems().size());
        assertEquals("secDispalyName03", sec03.getDisplayName());
        assertEquals("secName03", sec03.getName());
        assertEquals(instance.getItems().get(0), result);

    }

    /**
     * Test of addItems method, of class NamedItemList.
     */
    @Test
    public void testAddOrUpdateItems_GenericType() {
        System.out.println("addItems(NamedItemImpl... scs");
        NamedItemImpl sec01 = new NamedItemImpl("name01", "displayName01");
        NamedItemImpl sec02 = new NamedItemImpl("name02", "displayName02");

        NamedItemImpl[] scs = new NamedItemImpl[]{sec01};
        NamedItemListImpl instance = new NamedItemListImpl();
        instance.addOrUpdateItems(scs);
        assertEquals(1, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));

        instance.getItems().clear();
        scs = new NamedItemImpl[]{sec01, sec02};
        instance.addOrUpdateItems(scs);
        assertEquals(2, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));

        //
        // Try to add the same array. We expect that no items will be added
        //
        instance.addOrUpdateItems(scs);
        assertEquals(2, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        //
        // Try to add the same array plus a new item. We expect that only
        // the new item will be added
        //
        NamedItemImpl sec03 = new NamedItemImpl("name03", "displayName03");
        scs = new NamedItemImpl[]{sec01, sec03, sec02};
        instance.addOrUpdateItems(scs);
        assertEquals(3, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        assertEquals(sec03, instance.getItems().get(2));
        //
        // Try to add several items which contains the items with the 
        // same names as have some existing items
        //
        NamedItemImpl sec04 = new NamedItemImpl("name01", "displayName03");
        NamedItemImpl sec05 = new NamedItemImpl("name05", "displayName05");
        NamedItemImpl sec06 = new NamedItemImpl("name05", "displayName06");
        NamedItemImpl sec07 = new NamedItemImpl("name07", "displayName07");

        scs = new NamedItemImpl[]{sec04, sec05, sec06, sec07};
        //
        // 1. sec04 will not be added but change the displayNmae of the sec01
        // 2. sec05 will be added 
        // 3. sec06 will not be added as the name05 already exists. It just 
        //    change the displayName of the sec05
        // 4. sec07 will be added

        instance.addOrUpdateItems(scs);
        assertEquals(5, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));
        assertEquals(sec03, instance.getItems().get(2));
        assertEquals(sec05, instance.getItems().get(3));
        assertEquals("displayName06", sec05.getDisplayName());
        assertEquals(sec07, instance.getItems().get(4));

    }

    /**
     * Test of addItems method, of class NamedItemList.
     */
    @Test
    public void testAddOrUpdateItems_int_GenericType() {
        System.out.println("addItems(int idx,NamedItemImpl... scs)");
        int idx = 0;

        NamedItemImpl sec01 = new NamedItemImpl("name01", "displayName01");
        NamedItemImpl sec02 = new NamedItemImpl("name02", "displayName02");

        NamedItemImpl[] scs = new NamedItemImpl[]{sec01};
        NamedItemListImpl instance = new NamedItemListImpl();
        
        instance.addOrUpdateItems(idx, scs);
        assertEquals(1, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));

        instance.getItems().clear();

        scs = new NamedItemImpl[]{sec01, sec02};
        instance.addOrUpdateItems(idx, scs);
        assertEquals(2, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));

        //
        // Try to add the same array. We expect that no items will be added
        //
        instance.addOrUpdateItems(idx, scs);
        assertEquals(2, instance.getItems().size());
        assertEquals(sec01, instance.getItems().get(0));
        assertEquals(sec02, instance.getItems().get(1));

        //
        // Try to add the same array plus a new item. We expect that only
        // the new item will be added  with 0 index
        //
        NamedItemImpl sec03 = new NamedItemImpl("name03", "displayName03");
        scs = new NamedItemImpl[]{sec01, sec03, sec02};
        instance.addOrUpdateItems(idx, scs);
        assertEquals(3, instance.getItems().size());
        assertEquals(sec03, instance.getItems().get(0));
        assertEquals(sec01, instance.getItems().get(1));
        assertEquals(sec02, instance.getItems().get(2));

        //
        // Try to add the two item at the index 1
        //        
        NamedItemImpl sec04 = new NamedItemImpl("name04", "displayName04");
        NamedItemImpl sec05 = new NamedItemImpl("name05", "displayName05");
        scs = new NamedItemImpl[]{sec04, sec05};
        instance.addOrUpdateItems(++idx, scs);
        assertEquals(5, instance.getItems().size());
        assertEquals(sec03, instance.getItems().get(0));
        assertEquals(sec04, instance.getItems().get(1));
        assertEquals(sec05, instance.getItems().get(2));

        assertEquals(sec01, instance.getItems().get(3));
        assertEquals(sec02, instance.getItems().get(4));

        //
        // Try to add the two items at the end of the list
        //        
        NamedItemImpl sec06 = new NamedItemImpl("name06", "displayName06");
        NamedItemImpl sec07 = new NamedItemImpl("name07", "displayName07");
        scs = new NamedItemImpl[]{sec06, sec07};
        instance.addOrUpdateItems(instance.getItems().size(), scs);
        assertEquals(7, instance.getItems().size());
        assertEquals(sec03, instance.getItems().get(0));
        assertEquals(sec04, instance.getItems().get(1));
        assertEquals(sec05, instance.getItems().get(2));

        assertEquals(sec01, instance.getItems().get(3));
        assertEquals(sec02, instance.getItems().get(4));

        assertEquals(sec06, instance.getItems().get(5));
        assertEquals(sec07, instance.getItems().get(6));
    }


    public static class NamedItemListImpl implements NamedItemList<NamedItemImpl> {
        
        private ObservableList<NamedItemImpl> items = FXCollections.observableArrayList();
        
        private String name;
        private String displayName;

        @Override
        public ObservableList<NamedItemImpl> getItems() {
            return items;
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
