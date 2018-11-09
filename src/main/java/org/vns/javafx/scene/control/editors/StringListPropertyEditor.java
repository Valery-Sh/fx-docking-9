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

/**
 * The class is used as an editor for properties of type
 * {@code ObservableList&lt;String&gt;}. Suppose some node or any other object
 * has a property of type {@code ObservableList&lt;String&gt;}. Recap that the
 * object of type {@code Node} have the property named {@code styleClass} which
 * is of type {@code ObservableList&lt;String&gt; }. We want to edit the content
 * of the list. As this class is a sub class of the {@code TextField} we assume
 * that we can put a text as a comma-separated list of items and each item
 * represents a text item. For example we can put the following text
 * <pre>
 *  s1,s34,s99
 * </pre> and we expect that our observable list will contain three items such
 * as "s1" and "s34" and "s99". If the text property of the text field is an
 * empty string then the our list will be empty too.
 *
 * The example below shows how to accomplish the task
 * <pre>
 * Label label = new Label("Demo");
 * StringListPropertyEditor editor = new StringListPropertyEditor();
 * editor.bindContentBidirectional(label.getStyleClass);
 * </pre> There are a number of features that must be taken into account when
 * working with this control. First, when we put the following text
 * <pre>
 * label, label1,,  label2
 * </pre> the bound list will contain three items such as label, label1 and
 * label2. Therefore, all empty items are ignored. The class has a property
 * named {@code valueIfBlank} which is of type {@code String}. By default it is
 * set to null. We can set it'item value to any string value. In this case every
 * empty item will be replaced with that value. For instance let'item set the
 * value of the property {@code valueIfBlank} by applying the method
 * <pre>
 * editor.setValueIfBlank("blank");
 * StringListPropertyEditor editor = new StringListPropertyEditor();
 * editor.bindContentBidirectional(label.getStyleClass());
 * editor.setValueIfBlank("blank);
 * editor.setValidator( item -&gt; {
 *    boolean v = true;
 *    if ( !item.trim().isEmpty() ) {
 *      if ( ! item.trim().startsWith("label"} ) {
 *        v = false;
 *      }
 *    }
 *    return v;
 * });
 * </pre> Then the {@code styleClass} will contain four items
 *
 * <pre>
 * label
 * label1
 * blank
 * label2
 * </pre> We can impose restrictions on the items that can be entered. To
 * achieve this goal we must add one or more objects of type
 * Predicate&lt;String&gt; to the list of validators. The field {@code validators
 * } is defined as follows
 * <pre>
 * private ObservableList&lt;Predicate&lt;String&gt;&gt; validators
 * </pre> Let us extend the code which now will look like the text below
 *
 * <pre>
 * Label label = new Label("Demo");
 * </pre>
 *
 * Now we cannot enter any string value which doesn't start with the symbols
 * {@code "label"}.
 * <p>
 * It is not necessary to use a comma as the separator of string elements. For
 * this purpose, any sequence of characters can be applied. Use the 
 * {@link ObservableListEditor#setSeparator(java.lang.String) }
 * </p>
 * <pre>
 *   \s*;
 *   ;\s+
 *   \s*;\s*
 * </pre> The first expression causes the left trimming of the string element,
 * the second right trimming and the third both left and right trimming.
 *
 *
 * @author Valery Shyshkin
 */
public class StringListPropertyEditor extends ObservableListPropertyEditor<String> {

    public StringListPropertyEditor() {
        this(null);
    }

    public StringListPropertyEditor(String name) {
        super(name);
        init();
    }


    private void init() {
        createBindingStringConverter();
        ((StringTextField)getEditorNode()).setSeparator(",");

    }

    public void createBindingStringConverter() {
        setStringConverter(new ObservableListItemStringConverter(this, String.class));
    }
}//class StringListPropertyEditor
