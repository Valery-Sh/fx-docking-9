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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TextField;
import org.vns.javafx.dock.api.designer.DesignerLookup;

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
 * {@link ObservableListPropertyEditor#setSeparator(java.lang.String) }
 * or {@link ObservableListPropertyEditor#setSeparator(java.lang.String, java.lang.String)
 * }
 * methods to change the default separator. In the case of using a method with
 * one parameter, the string elements will be placed in the List object as is,
 * that is, without trimming the spaces. When you use a method with two
 * parameters, you can specify a regular expression for the separator. For
 * example, if trimming is required, you can use one of the following
 * expressions
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
public class SeparatedStringtPropertyEditor extends TextField implements PropertyEditor<String> {

    private final Property<String> value = new SimpleStringProperty();

    public SeparatedStringtPropertyEditor() {
        init();
    }

    private void init() {
        getStyleClass().add("string-list-text-field");
    }

    protected boolean isAcceptable(String txt) {
        if (txt == null) {
            return false;
        }
        return true;

    }

    public String toListItem(String item) {
        return item;
    }

    /*    public static class Converter1 extends StringConverter<ObservableList<String>> {

        private final StringListPropertyEditor textField;

        public Converter1(StringListPropertyEditor textField) {
            this.textField = textField;
        }

        @Override
        public String toString(ObservableList<String> v) {
            return textField.toString();
        }

        @Override
        public ObservableList<String> fromString(String tx) {
            return textField.fromString(tx);
        }

    }//class Converter
     */
    public Property<String> valueProperty() {
        return value;
    }

    public String getValue() {
        return value.getValue();
    }

    public void setValue(String value) {
        if (!valueProperty().isBound()) {
            this.value.setValue(value);
        }
    }

    @Override
    public void unbind() {
        value.unbind();
    }

    @Override
    public boolean isBound() {
        return value.isBound();

    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public void bind(Property<String> property) {
        setEditable(false);
        setFocusTraversable(false);
        valueProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<String> property) {
        setEditable(true);
        setFocusTraversable(true);
        valueProperty().bindBidirectional(property);
    }

}//class SeparatedStringPropertyEditor
