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
 * The class is used as an editor for properties of type
 * {@code ObservableList&lt;String&gt;}. Suppose some node or any other object
 * has a property of type {@code ObservableList&lt;String&gt;}. Recap that the
 * object of type {@code Node} have the property named {@code styleClass} which
 * is of type  * {@code ObservableList&lt;String&gt;.
 We want to edit the content of the list. As this class is a sub class of
 * the {@code TextField} we assume that we can put a text as a comma-separated
 * list of items and each item represents an integer number.
 * For example we can put the following text
 * <pre>
 * 1,34,99
 * </pre> and we expect that our observable list will contain three items such
 * as 1 and 34 and 99. If the text property of the text field is an empty
 * string? the our list will be empty too.
 *
 * The example below shows how to accomplish the task
 * <pre>
 * Label label = new Label("Demo");
 * IntegerListTextField editor = new IntegerListTextField();
 * editor.bindContentBidirectional(label.getStyleClass);
 * </pre> There are a number of features that must be taken into account when
 * working with this control. First? when we put the following text
 * <pre>
 * label, label1,,  label2
 * </pre> the bound list will contain three items such as label, label1 and
 * label2. Therefore, all empty items are ignored. The class has a property
 * named  * {@code valueIfBlank} which is of type {@code String}. By default it is set to null.
 We can set it'item value to any string value. In this case every empty item
 * will be replaced with that value. For instance let'item set the value of the
 * property {@code valueIfBlank} by applying the method
 * <pre>
 * editor.setValueIfBlank("blank");
 * </pre> Then the {@code styleClass} will contain four items
 * <pre>
 * label
 * label1
 * blank
 * label2
 * </pre> We can impose restrictions on the items that can be entered. To
 * achieve this goal we must set the value of the property {@code validator}. The field {@code validator
 * }
 * is defined as follows
 * <pre>
 * private Predicate&lt;String&gt; validator;
 * </pre> Let us extend the code which now will look like the text below
 * <pre>
 * Label label = new Label("Demo");
 * StringListTextField editor = new StringListTextField();
 * editor.bindContentBidirectional(label.getStyleClass());
 * editor.setValueIfBlank("blank);
 * editor.setValidator( item -&gt; {
 * boolean v = true;
 * if ( !item.trim().isEmpty() ) {
 *
 * if ( ! item.trim().startsWith("l"} ) {
 * v = false;
 * }
 * }
 * return v;
 * });
 * </pre> Now we cannot enter any string value which doesn't start with the
 * symbol {@code "l"}.
 *
 *
 * @author Valery Shyshkin
 */
public class StringListTextField extends PrimitivesListTextField<String> {

    private String valueIfBlank;
    private boolean keepItemTrimmed = true;

    protected TextFormatter<ObservableList<String>> formatter;

    private final ListChangeListener<? super String> valueChangeListener = (c) -> {
        changeValue();
    };

    public StringListTextField() {
        init();
    }

    private void init() {
        getStyleClass().add("string-list-text-field");
        StringListTextField.Converter c = new StringListTextField.Converter(this);

        getValue().addListener(valueChangeListener);
        formatter = new TextFormatter<ObservableList<String>>(c, getValue(), filter);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getValue().size(); i++) {
            sb.append(getValue().get(i));
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
        // and just first set formatter'item property to null and the assign again
        // the value from this control
        //
        formatter.setValue(null);
        formatter.setValue(getValue());
    }

    /**
     * Binds content of the list specified by the property
     * {@code valueProperty()} to the given list.
     *
     * @param list the list to be bound to
     */
    public void bindContentBidirectional(ObservableList<String> list) {
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
        return true;

    }

    public boolean isKeepItemTrimmed() {
        return keepItemTrimmed;
    }

    public void setKeepItemTrimmed(boolean keepItemTrimmed) {
        this.keepItemTrimmed = keepItemTrimmed;
    }

    /**
     * Returns the default value to replace empty items.
     *
     * @return the default value to replace empty items
     * @see #setValueIfBlank(java.lang.StringListTextField)
     */
    public String getValueIfBlank() {
        return valueIfBlank;
    }

    /**
     * The method is used to set the default value of an empty item. For
     * example, when we enter the following text into this control
     * <pre>
     *   21,,55
     * </pre> then if the property {@code valueIfBlank } is null then the bound
     * observable list will contain two integer items 21 and 55. If we set the
     * not null value, for example 77, then the bound list will contain three
     * items 21, 77 and 55.
     *
     * @param valueIfBlank the value to replace an empty items
     */
    public void setValueIfBlank(String valueIfBlank) {
        this.valueIfBlank = valueIfBlank;
    }

    @Override
    protected Property<ObservableList<String>> initValueProperty() {
        return new SimpleListProperty(FXCollections.observableArrayList());
    }

    public static class Converter extends StringConverter<ObservableList<String>> {

        private final StringListTextField textField;

        public Converter(StringListTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(ObservableList<String> v) {
            String[] items = textField.getText().split(",");
            for (String item : items) {
                if (item.trim().isEmpty() && textField.getValueIfBlank() != null && !textField.getText().trim().isEmpty()) {
                    continue;
                }
                if (item.trim().isEmpty()) {
                    continue;
                }
                if (!(textField.getValidator() == null || textField.getValidator() != null && textField.getValidator().test(item))) {
                    continue;
                } else {
                    return textField.getText();
                }

            }            
            StringBuilder retval = new StringBuilder();
            if (v != null && !v.isEmpty()) {
                for (int i = 0; i < v.size(); i++) {
                    retval.append(v.get(i));
                    if (i != v.size() - 1) {
                        retval.append(",");
                    }
                }
            }
            return retval.toString();
        }

        @Override
        public ObservableList<String> fromString(String tx) {
            String[] items = tx.split(",");
            for (String item : items) {
                if (item.trim().isEmpty() && textField.getValueIfBlank() != null && !tx.trim().isEmpty()) {
                    continue;
                }
                if (item.trim().isEmpty()) {
                    continue;
                }
                if (!(textField.getValidator() == null || textField.getValidator() != null && textField.getValidator().test(item))) {
                    continue;
                } else {
                    return textField.getValue();
                }

            }
            
            
            ObservableList<String> retval = textField.getValue();
            retval.clear();
            for (String item : items) {
                if (item.trim().isEmpty() && textField.getValueIfBlank() != null && !tx.trim().isEmpty()) {
                    retval.add(textField.getValueIfBlank());
                    continue;
                }
                if (item.trim().isEmpty()) {
                    continue;
                }
                if (!(textField.getValidator() == null || textField.getValidator() != null && textField.getValidator().test(item))) {
                    continue;
                }

                if (textField.isKeepItemTrimmed()) {
                    retval.add(item.trim());
                } else {
                    retval.add(item);
                }
            }
            return retval;
        }
    }//class Converter

}//class IntegerListTextField
