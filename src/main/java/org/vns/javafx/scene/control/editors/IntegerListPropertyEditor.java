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
package org.vns.javafx.scene.control.editors;

import org.vns.javafx.scene.control.editors.PrimitivePropertyEditor.IntegerPropertyEditor;

/**
 * The class is used as an editor for properties of type
 * {@code ObservableList<Integer>}. Suppose some node or any other object has a
 * property of type {@code ObservableList<Integer>}. We want to edit the content
 * of the list. As this class is a sub class of the {@code TextField} we assume
 * that we can put a text as a comma-separated list of items and each item
 * represents an integer number. For example we can put the following text
 * <pre>
 *   1,34,99
 * </pre> and we expect that our observable list will contain three items such
 * as 1 and 34 and 99. If the text property of the text field is an empty
 * string then our list will be empty too.
 *
 * The example below shows how to accomplish the task
 * <pre>
 * <code>
 *    ObservableList &lt;Integer &gt; sourceList = FXCollections.observableArrayList();
 * IntegerListPropertyEditor editor = new IntegerListPropertyEditor();
 * editor.bindContentBidirectional(sourceList);
 * </code> 
 * </pre>There are a number of features that must be taken into account when
 * working with this control. First? when we put the following text
 * <pre>
 *   1,23,,8
 * </pre> the bound list will contain three items such as 1,23 and 8. Therefore,
 * all empty items are ignored. The class has a property named
 * {@code valueIfBlank} which is of type Integer. By default it is set to null.
 * We can set it's value to any integer number. In this case every empty item
 * will be replaced with that value. For instance let's set the value of the
 * property {@code valueIfBlank} by applying the method
 * <pre>
 *   editor.setValueIfBlank(0);
 * </pre> Then the {@code sourceList} will contain four items
 * <pre>
 *   1
 *   23
 *   0
 *   8
 * </pre> The class doesn't allow to put items other the integer numbers. We can
 * impose other restrictions on the items that can be entered. To achieves this
 * goal we must set the value of the property {@code validator}. The field {@code validator
 * }
 * is defined as follows
 * <pre>
 *       private Predicate&lt;String&gt; validator;
 * </pre> Let us extend the code which now will look like the text below
 * <pre>
 *    ObservableList&lt;Integer&gt; sourceList = FXCollections.observableArrayList();
 * IntegerListPropertyEditor editor = new IntegerListPropertyEditor();
 * editor.bindContentBidirectional(sourceList);
 * editor.setValueIfBlank(0);
 * editor.setValidator( item -&gt; {
 *           boolean v = true;
 *           if ( !item.trim().isEmpty() &amp;&amp; ! item.trim().equals("-") ) {
 *               int n = Integer.parseInt(item.trim());
 *               if ( n &lt; 0 || n &gt; 100 ) {
 *                   v = false;
 *               }
 *           }
 *           return v;
 *       });
 * </pre> Now we cannot enter any negative value or value which is greater then
 * 100.
 *
 *
 * @author Valery Shyshkin
 */
public class IntegerListPropertyEditor extends ObservableListPropertyEditor<Integer> {

    public IntegerListPropertyEditor() {
        super();
        init();
    }

    private void init() {
        getTextField().setSeparator(",");

        setStringConverter(new ObservableListItemStringConverter(this,Integer.class));            
        IntegerPropertyEditor e = new PrimitivePropertyEditor.IntegerPropertyEditor();
        getTextField().getValidators().addAll(e.getValidators());
        getTextField().getFilterValidators().addAll(e.getFilterValidators());
        getTextField().setDefaultValue("0");
        getTextField().setEmptySubstitution("");
        getTextField().setNullSubstitution("<NULL>");
        getTextField().setNullable(true);
        
    }

}//class IntegerListPropertyEditor
