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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
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
public class SectionTest {
    
    public SectionTest() {
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
     * Test of nameProperty method, of class Section.
     */
    @Test
    public void testNameProperty() {
        System.out.println("nameProperty");
        Section instance = new Section();
        StringProperty expResult = null;
        StringProperty result = instance.nameProperty();
//        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class Section.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Section instance = new Section();
        String expResult = "";
        String result = instance.getName();
//        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Section.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String id = "testName";
        Section instance = new Section();
        instance.setName(id);
    }

    /**
     * Test of displayNameProperty method, of class Section.
     */
    @Test
    public void testDisplayNameProperty() {
        System.out.println("displayNameProperty");
        Section instance = new Section();
        StringProperty expResult = null;
        StringProperty result = instance.displayNameProperty();
//        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayName method, of class Section.
     */
    @Test
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        Section instance = new Section();
        String expResult = "";
        String result = instance.getDisplayName();
//        assertEquals(expResult, result);
    }

    /**
     * Test of setDisplayName method, of class Section.
     */
    @Test
    public void testSetDisplayName() {
        System.out.println("setDisplayName");
        String displayName = "";
        Section instance = new Section();
        instance.setDisplayName(displayName);
    }

    /**
     * Test of getItems method, of class Section.
     */
    @Test
    public void testGetItems() {
        System.out.println("getItems");
        Section instance = new Section();
        ObservableList<PropertyItem> expResult = null;
        ObservableList<PropertyItem> result = instance.getItems();
//        assertEquals(expResult, result);
    }

    /**
     * Test of merge method, of class Section.
     */
    @Test
    public void testMerge() {
        System.out.println("merge");
        ObservableList<PropertyItem> props = null;
        Section instance = new Section();
//        instance.merge(props);
    }

    /**
     * Test of updateBy method, of class Section.
     */
    @Test
    public void testUpdateBy_InsertBefore() {
        System.out.println("updateBy(propertyItem prop)");
        PropertyItem item01 = new PropertyItem("name01","displayName01");
        Section instance = new Section("secName01","secDisplayName01");
        instance.addOrUpdateItems(item01);
        
        //
        // Try to add a single item
        //
        PropertyItem updateProp = new PropertyItem("name02","displayName02");
        instance.updateBy(updateProp);
        assertEquals(2,instance.getItems().size());
        
        //
        // Try to add an item when the there is an item with the same name.
        // We expect that a new item will just update the existing item but not add the new one.
        //
        PropertyItem updateProp01 = new PropertyItem("name02","updated_displayName02");
        instance.updateBy(updateProp01);
        assertEquals(2,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        assertEquals(updateProp, instance.getItems().get(1));
        assertEquals("name02", updateProp.getName());
        assertEquals("updated_displayName02", updateProp.getDisplayName());

        //
        // Try add items from the object of type InsertBefore. We expect that
        // two new items will be added.
        //
        InsertBefore insBefore01 = new InsertBefore("name01");
        PropertyItem ins01 = new PropertyItem("name03","displayName03");
        PropertyItem ins02 = new PropertyItem("name04","displayName04");
        insBefore01.addOrUpdateItems(ins01,ins02);
        instance.updateBy(insBefore01);
        assertEquals(4,instance.getItems().size());
        assertEquals(ins01, instance.getItems().get(0));
        assertEquals(ins02, instance.getItems().get(1));
        assertEquals(item01, instance.getItems().get(2));
        assertEquals(updateProp, instance.getItems().get(3));
        
        //
        // Try update 
        //
/*        instance.updateBy(ins01);
        assertEquals(4,instance.getItems().size());
        assertEquals(ins01, instance.getItems().get(0));
        assertEquals(ins02, instance.getItems().get(1));
        assertEquals(prop, instance.getItems().get(2));
        assertEquals(updateProp, instance.getItems().get(3));
*/
        //
        // Try update by the same InsertBefore. We expect that no change will be applied. 
        //
        instance.updateBy(insBefore01);
        assertEquals(4,instance.getItems().size());
        assertEquals(ins01, instance.getItems().get(0));
        assertEquals(ins02, instance.getItems().get(1));
        assertEquals(item01, instance.getItems().get(2));
        assertEquals(updateProp, instance.getItems().get(3));
        
        //
        // Try update by the InsertBefore with the property name which doesn't
        // exists. We expect that no change will be applied.
        //        
        insBefore01 = new InsertBefore("unknown");
        PropertyItem ins05 = new PropertyItem("name05","displayName05");
        PropertyItem ins06 = new PropertyItem("name06","displayName06");
        insBefore01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insBefore01);
        assertEquals(4,instance.getItems().size());
        
        
        //
        // Try update by the InsertBefore with the name value equals to null. 
        // We expect that the items will be added at the beginning of the section.
        // 
        insBefore01 = new InsertBefore();
        insBefore01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insBefore01);
        assertEquals(6,instance.getItems().size());
        
        assertEquals(ins05, instance.getItems().get(0));
        assertEquals(ins06, instance.getItems().get(1));
        assertEquals(ins01, instance.getItems().get(2));
        assertEquals(ins02, instance.getItems().get(3));
        assertEquals(item01, instance.getItems().get(4));
        assertEquals(updateProp, instance.getItems().get(5));        
        
    }
    @Test
    public void testUpdateBy_InsertAfter() {
        System.out.println("updateBy(propertyItem prop)");
        PropertyItem item01 = new PropertyItem("name01","displayName01");
        Section instance = new Section("secName01","secDisplayName01");
        instance.addOrUpdateItems(item01);
        
        //
        // Try to add a single item
        //
        PropertyItem updateProp = new PropertyItem("name02","displayName02");
        instance.updateBy(updateProp);
        assertEquals(2,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        assertEquals(updateProp, instance.getItems().get(1));        
        //
        // Try to add an item when the there is an item with the same name.
        // We expect that a new item will just update the existing item but not add the new one.
        //
        PropertyItem updateProp01 = new PropertyItem("name02","updated_displayName02");
        instance.updateBy(updateProp01);
        assertEquals(2,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        assertEquals(updateProp, instance.getItems().get(1));
        assertEquals("name02", updateProp.getName());
        assertEquals("updated_displayName02", updateProp.getDisplayName());

        //
        // Try add items from the object of type InsertBefore. We expect that
        // two new items will be added after the item with the specified name.
        //
        InsertAfter insAfter01 = new InsertAfter("name01");
        PropertyItem ins01 = new PropertyItem("name03","displayName03");
        PropertyItem ins02 = new PropertyItem("name04","displayName04");
        insAfter01.addOrUpdateItems(ins01,ins02);
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));
        
 
        //
        // Try update by the same InsertBefore. We expect that no change will be applied/ 
        //
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));
        
        //
        // Try update by the InsertBefore with the property name which doesn't
        // exists. We expect that no change will be applied.
        //        
        insAfter01 = new InsertAfter("unknown");
        PropertyItem ins05 = new PropertyItem("name05","displayName05");
        PropertyItem ins06 = new PropertyItem("name06","displayName06");
        insAfter01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());

        
        //
        // Try update by the InsertBefore with the name value equals to null. 
        // We expect that the items will be inserted at the end of the section.
        // 
        insAfter01 = new InsertAfter();
        insAfter01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insAfter01);
        assertEquals(6,instance.getItems().size());
        assertEquals(item01, instance.getItems().get(0));
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));        
        assertEquals(ins05, instance.getItems().get(4));
        assertEquals(ins06, instance.getItems().get(5));
        

    }

    
    /**
     * Test of getCopyFor method, of class Section.
     */
    @Test
    public void testGetCopyFor() {
        System.out.println("getCopyFor");
        Class clazz = null;
        BeanModel ppd = null;
        Category cat = null;
        Section instance = new Section();
        Section expResult = null;
//        Section result = instance.getCopyFor(clazz, ppd, cat);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getByName method, of class Section.
     */
    @Test
    public void testGetByName() {
        System.out.println("getByName");
        String propertyName = "";
        Section instance = new Section();
        PropertyItem expResult = null;
//        PropertyItem result = instance.getByName(propertyName);
//        assertEquals(expResult, result);
    }

    /**
     * Test of indexByName method, of class Section.
     */
    @Test
    public void testIndexByName() {
        System.out.println("indexByName");
        String propertyName = "";
        Section instance = new Section();
        int expResult = 0;
//        int result = instance.indexByName(propertyName);
//        assertEquals(expResult, result);
    }

    /**
     * Test of mergeChilds method, of class Section.
     */
    @Test
    public void testMergeChilds() {
        System.out.println("mergeChilds(Section sec");
        System.out.println("updateBy(propertyItem prop)");
        PropertyItem prop = new PropertyItem("name01","displayName01");
        Section instance = new Section("secName01","secDisplayName01");
        instance.addOrUpdateItems(prop);
        
        //
        // Try to add a single item
        //
        PropertyItem updateProp = new PropertyItem("name02","displayName02");
        instance.updateBy(updateProp);
        assertEquals(2,instance.getItems().size());
        assertEquals(prop, instance.getItems().get(0));
        assertEquals(updateProp, instance.getItems().get(1));        
        //
        // Try to add an item when the there is an item with the same name.
        // We expect that a new item will just update the existing item but not add the new one.
        //
        PropertyItem updateProp01 = new PropertyItem("name02","updated_displayName02");
        instance.updateBy(updateProp01);
        assertEquals(2,instance.getItems().size());
        assertEquals(prop, instance.getItems().get(0));
        assertEquals(updateProp, instance.getItems().get(1));
        assertEquals("name02", updateProp.getName());
        assertEquals("updated_displayName02", updateProp.getDisplayName());

        //
        // Try add items from the object of type InsertBefore. We expect that
        // two new items will be added after the item with the specified name.
        //
        InsertAfter insAfter01 = new InsertAfter("name01");
        PropertyItem ins01 = new PropertyItem("name03","displayName03");
        PropertyItem ins02 = new PropertyItem("name04","displayName04");
        insAfter01.addOrUpdateItems(ins01,ins02);
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());
        assertEquals(prop, instance.getItems().get(0));
        
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));
        
 
        //
        // Try update by the same InsertBefore. We expect that no change will be applied/ 
        //
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());
        assertEquals(prop, instance.getItems().get(0));
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));
        
        //
        // Try update by the InsertBefore with the property name which doesn't
        // exists. We expect that no change will be applied.
        //        
        insAfter01 = new InsertAfter("unknown");
        PropertyItem ins05 = new PropertyItem("name05","displayName05");
        PropertyItem ins06 = new PropertyItem("name06","displayName06");
        insAfter01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insAfter01);
        assertEquals(4,instance.getItems().size());

        
        //
        // Try update by the InsertBefore with the name value equals to null. 
        // We expect that the items will be inserted at the end of the section.
        // 
        insAfter01 = new InsertAfter();
        insAfter01.addOrUpdateItems(ins05, ins06);
        instance.updateBy(insAfter01);
        assertEquals(6,instance.getItems().size());
        assertEquals(prop, instance.getItems().get(0));
        assertEquals(ins01, instance.getItems().get(1));
        assertEquals(ins02, instance.getItems().get(2));  
        assertEquals(updateProp, instance.getItems().get(3));        
        assertEquals(ins05, instance.getItems().get(4));
        assertEquals(ins06, instance.getItems().get(5));
        


    }
    
}
