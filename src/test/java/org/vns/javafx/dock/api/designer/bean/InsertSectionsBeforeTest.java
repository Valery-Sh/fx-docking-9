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

import org.vns.javafx.scene.control.editors.beans.Category;
import org.vns.javafx.scene.control.editors.beans.BeanProperty;
import org.vns.javafx.scene.control.editors.beans.Section;
import org.vns.javafx.scene.control.editors.beans.InsertSectionsBefore;
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
public class InsertSectionsBeforeTest {
    
    public InsertSectionsBeforeTest() {
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
     * Test of getInsertList method, of class InsertSectionsBefore.
     */
    @Test
    public void testGetInsertList() {
        System.out.println("getInsertList");
        InsertSectionsBefore instance = new InsertSectionsBefore();
        ObservableList<Section> expResult = null;
        ObservableList<Section> result = instance.getInsertList();
    }

    @Test
    public void test_InsertBefore_01() {
        System.out.println("updateBy(propertyItem prop)");
        Section section01 = new Section("secName01","secDisplayName01");
        Section section02 = new Section("secName02","secDisplayName02");

        BeanProperty item01_01 = new BeanProperty("name01_01","displayName01_01");
        BeanProperty item01_02 = new BeanProperty("name01_02","displayName01_02");
        section01.getItems().addAll(item01_01,item01_02);
        
        BeanProperty item02_01 = new BeanProperty("name02_01","displayName02_01");
        BeanProperty item02_02 = new BeanProperty("name02_02","displayName02_02");
        section02.getItems().addAll(item02_01,item02_02);        
        
        Category category01 = new Category("catName01","catDisplayName01");
        category01.getItems().add(section01);
        
        InsertSectionsBefore instance = new InsertSectionsBefore("secName01");
        Section insert01 = new Section("secName01","secDisplayName01");
        instance.getInsertList().add(insert01);
        
        category01.updateBy(instance);
        assertEquals(1, category01.getItems().size());
        assertEquals(section01,category01.getItems().get(0) );
        assertEquals(2, section01.getItems().size());
        assertEquals(item01_01, section01.getItems().get(0));
        assertEquals(item01_02, section01.getItems().get(1));
        
        category01.getItems().add(section02);
        
        instance = new InsertSectionsBefore("secName01");
        insert01 = new Section("secName02","secDisplayName02");
        instance.getInsertList().add(insert01);
        
        category01.updateBy(instance);
        assertEquals(2, category01.getItems().size());
        assertEquals(section02,category01.getItems().get(0) );
//        assertEquals(insert01,category01.getItems().get(0) );
        assertEquals(section01,category01.getItems().get(1) );
/*        assertEquals(2, section01.getItems().size());
        assertEquals(item01_01, section01.getItems().get(0));
        assertEquals(item01_02, section01.getItems().get(1));        
*/                
                
                
        
        

    }
    
}
