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

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 * The class is used as an editor for properties of type {@code ObservableList<Integer>}.
 * Suppose some node or  any other object has a property of type {@code ObservableList<Integer>}.
 * We want to edit the content of the list. As this class is a sub class of the 
 * {@code TextField} we assume that we can put a text as a comma-separated 
 * list of items and each item represents an integer number. For example we can put the following text 
 * <pre>
 *   1,34,99
 * </pre>
 * and we expect that our observable list will contain three items such as 1 and 34 and 99.
 * If the text property of the text field is an empty string? the our list will be empty too.
 *
 *   The example below shows how to accomplish the task
 * <pre>
 *    ObservableList &lt;Integer &gt; sourceList = FXCollections.observableArrayList();
 *    IntegerListTextField editor = new IntegerListTextField();
 *    editor.bindContentBidirectional(sourceList);
 * </pre>
 * There are a number of features that must be taken into account when working with
 * this control. First? when we put the folloing text
 * <pre>
 *   1,23,,8
 * </pre>
 * the bound list will contain three items such as 1,23 and 8. Therefore, 
 * all empty items are ignored. The class has a property named {@code valueIfBlank} 
 * which is of type Integer. By default it is set to null. We can set it's value to any 
 * integer number. In this case every empty item will be replaced with that value. 
 * For instance let's set the value of the property {@code valueIfBlank} by applying
 * the method
 * <pre>
 *   editor.setValueIfBlank(0);
 * </pre>
 * Then the {@code sourceList} will contain four items 
 * <pre>
 *   1
 *   23
 *   0
 *   8
 * </pre>
 * The class doesn't allow to put items other the integer numbers. We can impose 
 * other restrictions on the items that can be entered. To achieves this goal
 * we must set the value of the property {@code validator}. The field {@code validator }
 * is defined as follows
 * <pre>
 *       private Predicate&lt;String&gt; validator;
 * </pre>
 * Let us extend the code which now will look like the text below
 * <pre>
 *    ObservableList&lt;Integer&gt; sourceList = FXCollections.observableArrayList();
 *    IntegerListTextField editor = new IntegerListTextField();
 *    editor.bindContentBidirectional(sourceList);
 *    editor.setValueIfBlank(0);
 *    editor.setValidator( item -&gt; {
 *           boolean v = true;
 *           if ( !item.trim().isEmpty() &amp;&amp; ! item.trim().equals("-") ) {
 *               int n = Integer.parseInt(item.trim());
 *               if ( n &lt; 0 || n &gt; 100 ) {
 *                   v = false;
 *               }
 *           }
 *           return v;
 *       });
 * </pre>
 * Now we cannot enter any negative value or value which is greater then 100.
 *
 * 
 * @author Valery Shyshkin
 */
public class IntegerListTextField extends PrimitivesListTextField<Integer> {

    private Integer valueIfBlank;

    protected TextFormatter<ObservableList<Integer>> formatter;

    private final ListChangeListener<? super Integer> valueChangeListener = (c) -> {
        changeValue();
    };

    public IntegerListTextField() {
        init();
    }

    private void init() {
        getStyleClass().add("integer-text-field");
        IntegerListTextField.Converter c = new IntegerListTextField.Converter(this);

        getValue().addListener(valueChangeListener);
        formatter = new TextFormatter<ObservableList<Integer>>(c, getValue(), filter);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getValue().size(); i++) {
            sb.append(getValue().get(i).toString());
            if (i != getValue().size() - 1) {
                sb.append(",");
            }
        }
        setText(sb.toString());
        setTextFormatter(formatter);
    }

    private void changeValue() {
        //
        // We must raise Invalidation event for valueProperty() inthe formatter 
        // and just first set formatter's property to null and the assign again
        // the value from this control
        //
        formatter.setValue(null);
        formatter.setValue(getValue());
    }
    /**
     * Binds content of the list specified by the property {@code valueProperty()}
     * to the given list.
     * @param list the list to be bound to
     */
    public void bindContentBidirectional(ObservableList<Integer> list) {
        this.setEditable(true);
        this.setFocusTraversable(true);
        ((ListProperty) valueProperty()).unbindContentBidirectional(list);
        ((ListProperty) valueProperty()).bindContentBidirectional(list);
    }

    @Override
    protected boolean isAcceptable(String txt) {
        if (txt == null) {
            return false;
        }
        if (txt.isEmpty() || "-".equals(txt)) {
            return true;
        }
        String[] items = txt.split(",");
        boolean retval = true;

        for (String item : items) {
            if (item.matches(getPattern()) && (item.trim().isEmpty() || item.trim().equals("-") || Long.parseLong(item) <= Integer.MAX_VALUE && Long.parseLong(item) >= Integer.MIN_VALUE)) {
                if (!(getValidator() == null || getValidator() != null && getValidator().test(item))) {
                    retval = false;
                    break;
                }
            } else {
                retval = false;
                break;
            }
        }
        return retval;

    }
    /**
     * Returns the default value to replace empty items.
     * @return the default value to replace empty items
     * @see #setValueIfBlank(java.lang.Integer) 
     */
    public Integer getValueIfBlank() {
        return valueIfBlank;
    }
    /**
     * The method is used to set the default value of an empty item.
     * For example, when we enter the following text into this control
     * <pre>
     *   21,,55
     * </pre>
     * then if the property {@code valueIfBlank } is null then the bound 
     * observable list will contain two integer items 21 and 55. 
     * If we set the not null value, for example 77, then the bound list will 
     * contain three items 21, 77 and 55.
     * 
     * @param valueIfBlank the value to replace an empty items
     */
    public void setValueIfBlank(Integer valueIfBlank) {
        this.valueIfBlank = valueIfBlank;
    }

    protected String getPattern() {
        return "0|-?([1-9][0-9]*)?";
    }

    @Override
    protected Property<ObservableList<Integer>> initValueProperty() {
        return new SimpleListProperty(FXCollections.observableArrayList());
    }

    public static class Converter extends StringConverter<ObservableList<Integer>> {

        private final IntegerListTextField textField;

        public Converter(IntegerListTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(ObservableList<Integer> v) {
            StringBuilder retval = new StringBuilder();
            if (v != null && !v.isEmpty()) {
                for (int i = 0; i < v.size(); i++) {
                    retval.append(v.get(i).toString());
                    if (i != v.size() - 1) {
                        retval.append(",");
                    }
                }
            }
            return retval.toString();
        }

        @Override
        public ObservableList<Integer> fromString(String tx) {
            String[] a = tx.split(",");
            ObservableList<Integer> retval = textField.getValue();
            retval.clear();
            for (String s : a) {
                if (s.trim().isEmpty() && textField.getValueIfBlank() != null && !tx.trim().isEmpty()) {
                    retval.add(textField.getValueIfBlank());
                    continue;
                }
                if (s.trim().isEmpty() || s.trim().equals("-") || s.trim().equals("+")) {
                    continue;
                }
                retval.add(Integer.parseInt(s));
            }
            return retval;
        }
    }//class Converter

}//class IntegerListTextField
