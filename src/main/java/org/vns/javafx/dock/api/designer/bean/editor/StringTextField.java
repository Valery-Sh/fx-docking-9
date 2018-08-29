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
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery
 */
public class StringTextField extends TextField {

    private StringProperty lastValidText = new SimpleStringProperty();

    private final ObservableMap<Integer, String> errorItems = FXCollections.observableHashMap();
    private final ObservableList<Predicate<String>> validators = FXCollections.observableArrayList();
    private final ObservableList<Predicate<String>> filterValidators = FXCollections.observableArrayList();

    public ObservableList<Predicate<String>> getFilterValidators() {
        return filterValidators;
    }
    private StringTransformer fromStringTransformer;
    private StringTransformer toStringTransformer;

    private String valueIfBlank;

    private String separator = null;

    protected TextFormatter formatter;

    private ErrorMarkerBuilder errorMarkerBuilder;
    private final ObjectProperty<UnaryOperator<TextFormatter.Change>> filter = new SimpleObjectProperty<>(change -> {
        if (isAcceptable(change.getControlNewText())) {
            return change;
        } else {
            return null;
        }
    }
    );

    private final ChangeListener<UnaryOperator<TextFormatter.Change>> filterChangeListener = (v, ov, nv) -> {
        if (nv != null) {
            formatter = new TextFormatter<>(new Converter(this), getText(), nv);
            this.setTextFormatter(formatter);
        } else {
            formatter = new TextFormatter<>(new Converter(this), getText());
            this.setTextFormatter(formatter);
        }
    };

    private final ChangeListener<? super String> rightValueChangeListener = (v, ov, nv) -> {
        System.err.println("rightValueChangeListener (calls invalidateFormatterValue) ov=" + ov + "; nv=" + nv);
        invalidateFormatterValue();
    };
    private final BooleanProperty errorFound = new BooleanPropertyBase(false) {

        @Override
        protected void invalidated() {
            pseudoClassStateChanged(ERRORFOUND_PSEUDO_CLASS,get());
        }

        @Override
        public Object getBean() {
            return StringTextField.this;
        }

        @Override
        public String getName() {
            return "errorFound";
        }
    };

    private static final PseudoClass ERRORFOUND_PSEUDO_CLASS = PseudoClass.getPseudoClass("errorfound");

    public StringTextField() {
        init();
    }

    private void init() {
        
        getStyleClass().add("string-textfield");
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));
        formatter = new TextFormatter(new Converter(this), getText(), getFilter());
        this.setTextFormatter(formatter);
        filter.addListener(filterChangeListener);
        lastValidText.addListener(rightValueChangeListener);
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }    

    public void setErrorFound(Boolean found) {
        this.errorFound.set(found);
    }
    public ChangeListener<? super String> getRightValueChangeListener() {
        return rightValueChangeListener;
    }

    /**
     * The method is called when the content of the observable list is changed.
     * We must raise Invalidation event for {@link #valueProperty() }
     * in the formatter in order to make the formatter to execute the method {@link Converter#toString(javafx.collections.ObservableList)
     * }. First set the {@code value} property of the {@code TextFormatter} to
     * {@code null} and then assign again the {@link #valueProperty() } of this
     * control.
     * <p>
     * We must enforce the {@code TextFormatter} to invoke the method
     * </p>
     *
     */
    protected void invalidateFormatterValue() {
        //
        // We must raise Invalidation event for valueProperty() in the formatter 
        // in order to make the formatter to execute the converter method toString().
        // First set formatter'item property to null and then assign again
        // the value from this control
        //

        //System.err.println("invalidateFormatterValue  formatter.getValue() " + (getValue() == formatter.getValue()));
        //ObservableList list = FXCollections.observableArrayList();
        //list.addAll(formatter.getValue());
        formatter.setValue(null);

        //formatter.setValue(getLastValidValue());
        formatter.setValue(getLastValidText());
        System.err.println("invalidateFormatterValue formatter getValue()=" + formatter.getValue());
        //formatter.setValue(list);

        //
        // We need the TextFormatter to execute the StriringConverter's method
        // fromString in order to validate items in the observable list and
        // mark errors. 
        //
        Platform.runLater(() -> {
            System.err.println("invalidateFormatterValue formatter before commitValue");
            commitValue();
            System.err.println("invalidateFormatterValue formatter after commitValue");
        });
    }

    /*    protected String convertRightValue(String value) {
        return value;
    }
     */
    public StringProperty lastValidTextProperty() {
        return lastValidText;
    }

    public String getLastValidText() {
        return lastValidText.get();
    }

    public void setLastValidText(String value) {
        this.lastValidText.set(value);
    }

    protected boolean isAcceptable(String txt) {
//        System.err.println("isAcceptable text = " + txt);

        if (getSeparator() == null || getSeparator().isEmpty()) {
            return testFilterValidators(txt);
        }
        if (getFilterValidators().isEmpty()) {
            return true;
        }
        String[] items = txt.split(getSeparator(), txt.length());

        boolean retval = true;
        for (String item : items) {
//            System.err.println("isAcceptable item = " + item);
            if (!testFilterValidators(item)) {
                retval = false;
                break;
            }
        }
        return retval;

    }

    protected boolean testFilterValidators(String txt) {
        boolean retval = true;
        for (Predicate<String> p : getFilterValidators()) {
            if (!p.test(txt)) {
                retval = false;
                break;
            }
        }
        return retval;
    }

    public StringTransformer getFromStringTransformer() {
        return fromStringTransformer;
    }

    public void setFromStringTransformer(StringTransformer fromStringTransformer) {
        this.fromStringTransformer = fromStringTransformer;
    }

    public StringTransformer getToStringTransformer() {
        return toStringTransformer;
    }

    public void setToStringTransformer(StringTransformer toStringTransformer) {
        this.toStringTransformer = toStringTransformer;
    }

    /*    protected ChangeListener<UnaryOperator<TextFormatter.Change>> getFilterChangeListener() {
        return filterChangeListener;
    }
     */
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
            //if (item.trim().isEmpty()) {
            //
            // We skip empty items and must take into account the actual index
            // of the converted lastValidValue in the result ObservableList
            //
            //    d++;
            //    continue;
            //}
            if (validateStringListItem(item)) {
                continue;
            } else {
                errorItemIndexes.add(i - d);
                errorItems.put(i - d, item);
            }
        }
        return errorItemIndexes;
    }

    public boolean hasErrorItems() {
        return !getErrorItems().isEmpty();
    }

    public ObservableMap<Integer, String> getErrorItems() {
        return errorItems;
    }

    public String getSeparator() {
        return separator;
    }

    /*    public String getSeparatorRegExp() {
        return separatorRegExp;
    }
     */
    public void setSeparator(String separator) {
        this.separator = separator;
//        this.separatorRegExp = separator;
    }

    /*    public void setSeparator(String delimiter, String delimiterRegExp) {
        this.separator = delimiter;
        this.separatorRegExp = delimiterRegExp;
    }
     */
    /**
     * Returns the default lastValidValue to replace empty items.
     *
     * @return the default lastValidValue to replace empty items
     * @see #setValueIfBlank(java.lang.String)
     */
    public String getValueIfBlank() {
        return valueIfBlank;
    }

    /**
     * The method is used to set the default lastValidValue of an empty item. For
     * example, when we enter the following text into this control
     * <pre>
     *   21,,55
     * </pre> then if the property {@code valueIfBlank } is null then the bound
 observable list will contain two integer items 21 and 55. If we set the
 not null lastValidValue, for example "77", then the bound list will contain
 three items 21, 77 and 55.
     *
     * @param valueIfBlank the lastValidValue used to replace empty items
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

    protected boolean validateStringListItem(String item) {
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

    protected void setFilter(UnaryOperator<TextFormatter.Change> filter) {
        this.filter.set(filter);
    }

    public static class Converter extends StringConverter<String> {

        private final StringTextField textField;

        public Converter(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String txt) {
//            System.err.println("toString = " + txt);
            if (txt == null) {
                return "";
            }
            return txt;
            //return textField.toString(list);
        }

        @Override
        public String fromString(String txt) {
            if (!textField.isEditable()) {
                return txt;
            }
            System.err.println("!!! fromString txt = " + txt);
            String[] items;

            if (textField.getSeparator() != null) {
                items = txt.split(textField.getSeparator(), txt.length());
            } else {
                items = new String[]{txt};
            }
            for (int i = 0; i < items.length; i++) {
                System.err.println("it = '" + items[i] + "'");
                if (textField.getFromStringTransformer() != null) {
                    items[i] = textField.getFromStringTransformer().transform(items[i]);
                }
            }
            //System.err.println("ERROR 1");
            List<Integer> errorItemIndexes = textField.getErrorIndexes(items);
//System.err.println("ERROR 2");
            if (errorItemIndexes.isEmpty()) {
                if (textField.getErrorMarkerBuilder().getErrorMarkers() != null && textField.getErrorMarkerBuilder().getErrorMarkers().length > 0) {
                    textField.getChildren().removeAll(textField.getErrorMarkerBuilder().getErrorMarkers());
                }
            }
//System.err.println("ERROR 2.1");   
            if (!errorItemIndexes.isEmpty() && (textField.getSeparator() == null || !textField.getSeparator().isEmpty())) {
                textField.setErrorFound(Boolean.TRUE);
            } else {
                textField.setErrorFound(Boolean.FALSE);
            }
            if (textField.getErrorMarkerBuilder() != null) {
                Platform.runLater(() -> {
//                    System.err.println("ERROR 2.2 hasErrors = " + !errorItemIndexes.isEmpty());            
                    if (!errorItemIndexes.isEmpty()) {
                        Integer[] e = errorItemIndexes.toArray(new Integer[errorItemIndexes.size()]);
                        textField.getErrorMarkerBuilder().showErrorMarkers(e);
                    }

                });
            }
//System.err.println("ERROR 3");            
            String retval = "";
            StringBuilder sb = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();

            textField.lastValidTextProperty().removeListener(textField.getRightValueChangeListener());
//System.err.println("ERROR 4");            
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
//                System.err.println("item = '" + item + "'");
                //if (item.trim().isEmpty() && textField.getValueIfBlank() != null && !txt.trim().isEmpty()) {
                if (item.trim().isEmpty() && textField.getValueIfBlank() != null) {
                    sb.append(textField.getValueIfBlank());
                    sbValue.append(textField.getValueIfBlank());
                    continue;
                }

                sb.append(item);
                if (!errorItemIndexes.contains(i)) {
                    sbValue.append(item);
                    if (textField.getSeparator() != null) {
                        sbValue.append(textField.getSeparator());
                    }

                    if (textField.getSeparator() != null && !textField.getSeparator().isEmpty() && i != items.length - 1) {
                        sb.append(textField.getSeparator());
                    }
                }
            }
//            System.err.println("ERROR 5");
            retval = sb.toString();
//            System.err.println("444");
            if (textField.getSeparator() != null && !textField.getSeparator().isEmpty()) {
//                System.err.println("555 sbValue = " + sbValue + "; sbValue.length() = " + sbValue.length());
                if (sbValue.length() > 0 && sbValue.lastIndexOf(textField.getSeparator()) == sbValue.length() - 1) {
//                    System.err.println("666");
                    sbValue.deleteCharAt(sbValue.length() - 1);
                }
            }
            System.err.println("777 == " + sbValue.toString());
            if (errorItemIndexes.isEmpty()) {
                textField.setLastValidText(sbValue.toString());
            }

            textField.lastValidTextProperty().addListener(textField.getRightValueChangeListener());
            System.err.println("StringTextField: fromString retval=" + retval);
            System.err.println("StringTextField: fromString rightValue=" + textField.getLastValidText());
            System.err.println("end fromString------------------------------------------");
            return retval;
        }
    }//class Converter

}
