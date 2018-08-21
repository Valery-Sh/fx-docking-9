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

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class StringTextField extends TextField {

    private final ObservableMap<Integer, String> errorItems = FXCollections.observableHashMap();
    private final ObservableList<Predicate<String>> validators = FXCollections.observableArrayList();
    private String valueIfBlank;

    private String separator = null;
    private String separatorRegExp = null;

    protected TextFormatter formatter;
    private ErrorMarkerBuilder errorMarkerBuilder;

    //private UnaryOperator<TextFormatter.Change> filter;
    private ObjectProperty<UnaryOperator<TextFormatter.Change>> filter = new SimpleObjectProperty<>();

    private final ChangeListener<UnaryOperator<TextFormatter.Change>> filterChangeListener = (v, ov, nv) -> {
        if (nv != null) {
            formatter = new TextFormatter<>(new Converter(this), getText(), nv);
            this.setTextFormatter(formatter);
        } else {
            formatter = new TextFormatter<>(new Converter(this),getText());
            this.setTextFormatter(formatter);
        }
    };

    public StringTextField() {
        init();
    }

    private void init() {
        formatter = new TextFormatter(new Converter(this),getText());
        this.setTextFormatter(formatter);        
    }


    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    protected List<Integer> getErrorIndexes(String[] items) {
        List<Integer> errorItemIndexes = FXCollections.observableArrayList();
        errorItems.clear();
        int d = 0;
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.trim().isEmpty() && getValueIfBlank() != null) {
                continue;
            }
            if (getValueIfBlank() != null && getValueIfBlank().equals(item)) {
                continue;
            }
            if (item.trim().isEmpty()) {
                //
                // We skip empty items and must take into account the actual index
                // of the converted value in the result ObservableList
                //
                d++;
                continue;
            }
            if (validateStringListItem(item)) {
                continue;
            } else {
                errorItemIndexes.add(i - d);
                errorItems.put(i - d, item);
            }
        }
        return errorItemIndexes;
    }

    public ObservableMap<Integer, String> getErrorItems() {
        return errorItems;
    }

    public String getSeparator() {
        return separator;
    }

    public String getSeparatorRegExp() {
        return separatorRegExp;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        this.separatorRegExp = separator;
    }

    public void setSeparator(String delimiter, String delimiterRegExp) {
        this.separator = delimiter;
        this.separatorRegExp = delimiterRegExp;
    }

    /**
     * Returns the default value to replace empty items.
     *
     * @return the default value to replace empty items
     * @see #setValueIfBlank(java.lang.String)
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
     * not null value, for example "77", then the bound list will contain three
     * items 21, 77 and 55.
     *
     * @param valueIfBlank the value used to replace empty items
     */
    public void setValueIfBlank(String valueIfBlank) {
        this.valueIfBlank = valueIfBlank;
    }

    public ObservableList<Predicate<String>> getValidators() {
        return validators;
    }

    public ErrorMarkerBuilder getErrorMarkerBuilder() {
        return errorMarkerBuilder;
    }

    public void setErrorMarkerBuilder(ErrorMarkerBuilder errorMarkerBuilder) {
        this.errorMarkerBuilder = errorMarkerBuilder;
    }

    public boolean validateStringListItem(String item) {
        boolean retval = true;
        for (Predicate<String> v : getValidators()) {
            if (!v.test(item)) {
                retval = false;
                break;
            }
        }
        return retval;
    }

    public TextFormatter getFormatter() {
        return formatter;
    }

    public UnaryOperator<TextFormatter.Change> getFilter() {
        return filter.get();
    }

    public void setFilter(UnaryOperator<TextFormatter.Change> filter) {
        this.filter.set(filter);
    }

    public static class Converter extends StringConverter<String> {

        private final StringTextField textField;

        public Converter(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String list) {
            return list;
            //return textField.toString(list);
        }

        @Override
        public String fromString(String txt) {
            //return textField.fromString(txt);
            return txt;

        }

    }//class Converter

}
