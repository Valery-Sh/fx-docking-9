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
package org.vns.javafx.dock.api.designer.bean.editor;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
public class FontStringConverterTest {
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();        
    public FontStringConverterTest() {
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
     * Test of toString method, of class FontStringConverter.
     */
/*    @Test
    public void testToString() {
        System.out.println("toString");
        Font font = Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 10);
        FontStringConverter instance = new FontStringConverter();
        String expResult = "Arial 10px (Regular)";
        String result = instance.toString(font);
        //assertEquals(expResult, result);
    }
*/
    /**
     * Test of fromString method, of class FontStringConverter.
     */
    @Test
    public void testFromString() {
        System.out.println("fromString");
        String str = "Arial 10px (Regular)";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font("Arial", FontPosture.REGULAR, 10);
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }
    /**
     * Test of fromString method, of class FontStringConverter.
     */
    @Test
    public void testFromString_1() {
        System.out.println("fromString");
        String str = "Arial 10px (Bold Regular)";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font("Arial", FontPosture.REGULAR, 10);
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }  
    /**
     * Test of fromString method, of class FontStringConverter.
     */
/*    @Test
    public void testFromString_2() {
        System.out.println("fromString");
        String str = "Arial";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font("Arial");
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }      
*/    
    /**
     * Test of fromString method, of class FontStringConverter.
     */
/*    @Test
    public void testFromString_3() {
        System.out.println("fromString");
        String str = "10px";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font(10);
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }      
*/    
    /**
     * Test of fromString method, of class FontStringConverter.
     */
    @Test
    public void testFromString_4() {
        System.out.println("fromString");
        String str = "Arial 10px";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font("Arial",10);
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }      
    /**
     * Test of fromString method, of class FontStringConverter.
     */
    @Test
    public void testFromString_5() {
        System.out.println("fromString");
        String str = "Arial 10px (Bold)";
        
        FontStringConverter instance = new FontStringConverter();
        Font expResult = Font.font("Arial",FontWeight.BOLD,10);
        
       Font result = instance.fromString(str);
       assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }      
    
}
