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

import java.util.ArrayList;
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
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
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
    private ObservableList<StringTransformer> stringTransformers = FXCollections.observableArrayList();

//11.09    private String valueIfBlank;
    private final StringProperty nullSubstitution = new SimpleStringProperty(null);
    private String emptyListSubstitution;
    private String singleEmptyItemSubstitution;

    private String separator = null;

    //protected TextFormatter formatter;
    private ErrorMarkerBuilder errorMarkerBuilder;

    private final ObjectProperty<UnaryOperator<TextFormatter.Change>> filter;

    private final ChangeListener<UnaryOperator<TextFormatter.Change>> filterChangeListener;

    private final ChangeListener<? super String> lastValidValueChangeListener;

    private final BooleanProperty errorFound = new BooleanPropertyBase(false) {

        @Override
        protected void invalidated() {
            pseudoClassStateChanged(ERROR_FOUND_PSEUDO_CLASS, get());
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

    private static final PseudoClass ERROR_FOUND_PSEUDO_CLASS = PseudoClass.getPseudoClass("errorfound");

    public StringTextField() {
        System.err.println("CONSTR getText() = " + getText());
        this.lastValidValueChangeListener = (v, ov, nv) -> {
            invalidateFormatterValue();
        };
        this.filterChangeListener = (v, ov, nv) -> {
            if (nv != null) {
                //formatter = new TextFormatter<>(new FormatterConverter(this), getText(), nv);
                this.setTextFormatter(new TextFormatter<>(new FormatterConverter(this), getText(), nv));
            } else {
                //formatter = new TextFormatter<>(new FormatterConverter(this), getText());
                this.setTextFormatter(new TextFormatter<>(new FormatterConverter(this), getText()));
            }
        };
        this.filter = new SimpleObjectProperty<>(change -> {
            if (((TextField) change.getControl()).getText() == null) {
                return change;
            }
            if (isAcceptable(change.getControlNewText())) {
                return change;
            } else {
                return null;
            }

        });
        init();
    }

    private void init() {

        getStyleClass().add("string-textfield");
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));
        setTextFormatter(createTextFormatter());
        //this.setTextFormatter(formatter);
        filter.addListener(filterChangeListener);
        lastValidText.addListener(lastValidValueChangeListener);
        nullSubstitution.addListener((v, ov, nv) -> {
            if (ov == null) {
                String tx = getText();
                if (tx != null) {
                    setText(null);
                    setText(tx);
                } else {
                    setText("");
                    setText(tx);
                }

                commitValue();
                Platform.runLater(() -> {
                    //commitValue();
                });

            }
        });
        setOnMouseClicked(this::mouseClicked);
    }

    protected TextFormatter createTextFormatter() {
        return new TextFormatter(new FormatterConverter(this), getText(), getFilter());
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    protected void mouseClicked(MouseEvent ev) {
        if ((getNullSubstitution() == null || getNullSubstitution().isEmpty()) && getEmptyListSubstitution() == null && getSingleEmptyItemSubstitution() == null) {
            return;
        }
        IndexRange range = getItemRange();
        if (range == null) {
            return;
        }
        String sub = getNullSubstitution();
        if (sub == null || sub.isEmpty()) {
            sub = null;
        } else {
            sub = getNullSubstitution();
        }
        if ( sub != null && getText() != null && sub.equals(getText())) {
           selectRange(range.getStart(), range.getEnd());
           return;
        } else {
            sub = null;
        }
        if (sub == null && getEmptyListSubstitution() != null) {
            sub = getEmptyListSubstitution();
        } 
        if ( sub != null && getText() != null && sub.equals(getText())) {
           selectRange(range.getStart(), range.getEnd());
           return;
        } else {
            sub = null;
        }
        
        if (sub == null && getSingleEmptyItemSubstitution() != null) {
            sub = getSingleEmptyItemSubstitution();
        }
        System.err.println("MOUSE CLICKED sub = " + sub + "; range.getStart() = " + range.getStart() + "; range.getEnd()=" + range.getEnd());
        if (sub.equals(getText().substring(range.getStart(), range.getEnd()))) {
            selectRange(range.getStart(), range.getEnd());
        }
    }

    public void setErrorFound(Boolean found) {
        this.errorFound.set(found);
    }

    public ChangeListener<? super String> getLastValidValueChangeListener() {
        return lastValidValueChangeListener;
    }

    public IndexRange getItemRange() {
        IndexRange retval = null;
        String[] items = split(getText(), false);
        if (items == null) {
            return null;
        }
        int caretPos = getCaretPosition();
        int itemPos = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (itemPos <= caretPos && itemPos + items[i].length() >= caretPos) {
                retval = new IndexRange(itemPos, itemPos + items[i].length());
                break;
            }
            itemPos += items[i].length() + getSeparator().length();
        }
        return retval;
    }

    public static String[] split(String txt, String separator) {
        return split(txt, separator, true);
    }

    public static String[] split(String txt, String separator, boolean ignoreQuotes) {
        if (separator == null || !txt.contains(separator)) {
            return new String[]{txt};
        }

        String[] retval = null;
        List<String> list = new ArrayList<>();

        StringBuilder sb = new StringBuilder(txt);
        int n = 0;

        while (true) {
            if (n >= sb.length()) {
                list.add(sb.toString());
                break;
            }
            if (!ignoreQuotes && sb.charAt(n) == '"' && n < sb.length() && sb.lastIndexOf("\"", n + 1) >= 0) {
                n = sb.lastIndexOf("\"", n + 1) + 1;
                continue;
            }
            if (separator.equals(sb.substring(n, n + separator.length()))) {
                if (n == 0) {
                    list.add("");
                } else {
                    list.add(sb.substring(0, n));
                }
                sb = sb.delete(0, n + separator.length());
                n = 0;
                continue;
            }
            n++;

        }
        retval = list.toArray(new String[0]);
        return retval;

    }

    public String[] split(String txt) {
        return split(txt, true);
    }

    public String[] split(String txt, boolean ignoreQuotes) {
        return split(txt, getSeparator(), ignoreQuotes);
    }

    /**
     * The method is called when the content of the observable list is changed.
     * We must raise Invalidation event for {@link #lastValidText }
     * in the formatter in order to make the formatter to execute the method {@link FormatterConverter#toString(java.lang.String)
     * }. First set the {@code value} property of the {@code TextFormatter} to
     * {@code null} and then assign again the {@link #lastValidText } of this
     * control.
     * <p>
     * We must enforce the {@code TextFormatter} to invoke the method
     * </p>
     *
     */
    protected void invalidateFormatterValue() {
        //
        // We must raise Invalidation event for lastValidText property in the formatter 
        // in order to make the formatter to execute the converter method toString().
        // First set formatter'item property to null and then assign again
        // the value from this control
        //

        //System.err.println("invalidateFormatterValue  formatter.getValue() " + (getValue() == formatter.getValue()));
        //ObservableList list = FXCollections.observableArrayList();
        //list.addAll(formatter.getValue());
        String txt = getLastValidText();
        System.err.println("invalidateFormatterValue:BEFORE set null getLastValidText " + getLastValidText());
        getTextFormatter().setValue(null);
        System.err.println("invalidateFormatterValue:AFTER set null getLastValidText " + getLastValidText());
        
        System.err.println("invalidateFormatterValue:BEFORE set getLastValidText " + getLastValidText());
        
        ((TextFormatter<String>) getTextFormatter()).setValue(txt);
        System.err.println("invalidateFormatterValue:AFTER set getLastValidText " + getLastValidText());
        
        //
        // We need the TextFormatter to execute the StriringConverter's method
        // fromString in order to validate items in the observable list and
        // mark errors. 
        //
        Platform.runLater(() -> {
            commitValue();
        });
    }

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
        if (getSeparator() == null || getSeparator().isEmpty()) {
            return testFilterValidators(txt);
        }
        if (getFilterValidators().isEmpty()) {
            return true;
        }
        String[] items = split(txt, false);
        boolean retval = true;
        for (String item : items) {
            if (!testFilterValidators(item)) {
                retval = false;
                break;
            }
        }
        return retval;

    }

    public ObservableList<StringTransformer> getStringTransformers() {
        return this.stringTransformers;
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
            //System.err.println("getErrorIndexes item = '" + item + "'");
            /*            if (item.trim().isEmpty() && getValueIfBlank() != null) {
                continue;
            }
            if (getValueIfBlank() != null && getValueIfBlank().equals(item)) {
                continue;
            }
             */
            //
            // We skip empty items and must take into account the actual index
            // of the converted lastValidValue in the result ObservableList
            //
            //    d++;
            //    continue;
            //}
            if (testValidators(item)) {
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

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isNullString(String txt) {
        boolean retval = false;
        if (getNullSubstitution() != null) {
            String t = txt;
            if (!getNullSubstitution().isEmpty()) {
                t = t.trim();
            }
            if (t.equals(getNullSubstitution())) {
                retval = true;
            }
        }
        return retval;
    }

    public StringProperty nullSubstitutionProperty() {
        return nullSubstitution;
    }

    public String getNullSubstitution() {
        return nullSubstitution.get();
    }

    public void setNullSubstitution(String nullSubstitution) {
        this.nullSubstitution.set(nullSubstitution);
    }

    public String getEmptyListSubstitution() {
        return emptyListSubstitution;
    }

    public void setEmptyListSubstitution(String emptyListSubstitution) {
        this.emptyListSubstitution = emptyListSubstitution;
    }

    public String getSingleEmptyItemSubstitution() {
        return singleEmptyItemSubstitution;
    }

    public void setSingleEmptyItemSubstitution(String singleEmptyItemSubstitution) {
        this.singleEmptyItemSubstitution = singleEmptyItemSubstitution;
    }

    /**
     * Returns the default lastValidValue to replace empty items.
     *
     * @return the default lastValidValue to replace empty items
     * @see #setValueIfBlank(java.lang.String)
     */
    /*11.09    public String getValueIfBlank() {
        return valueIfBlank;
    }
     */
    /**
     * The method is used to set the default lastValidValue of an empty item.
     * For example, when we enter the following text into this control
     * <pre>
     *   21,,55
     * </pre> then if the property {@code valueIfBlank } is null then the bound
     * observable list will contain two integer items 21 and 55. If we set the
     * not null lastValidValue, for example "77", then the bound list will
     * contain three items 21, 77 and 55.
     *
     * @param valueIfBlank the lastValidValue used to replace empty items
     */
    /*11.09    public void setValueIfBlank(String valueIfBlank) {
        this.valueIfBlank = valueIfBlank;
    }
     */
    /**
     * Returns a list of validators. May be empty.
     *
     * @return Returns a list of validators.
     */
    public ObservableList<Predicate<String>> getValidators() {
        return validators;
    }

    public ErrorMarkerBuilder getErrorMarkerBuilder() {
        return errorMarkerBuilder;
    }

    public void setErrorMarkerBuilder(ErrorMarkerBuilder errorMarkerBuilder) {
        this.errorMarkerBuilder = errorMarkerBuilder;
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

    protected boolean testValidators(String item) {
        boolean retval = true;
        if (getNullSubstitution() != null && getNullSubstitution().equals(item)) {
            //return true;
        }

        for (Predicate<String> v : getValidators()) {
            if (!v.test(item)) {
                retval = false;
                break;
            }
        }
        return retval;
    }

    public boolean isSameAsNull(String item) {
        return item == null || item.equals(getNullSubstitution());
    }

    public boolean isSameAsEmpty(String item) {
        return item == "" || (getEmptyListSubstitution() != null && getEmptyListSubstitution().equals(item));
    }

    protected String applySubstitutions(String txt) {
        String retval = getNullSubstitution();
        if (txt == null && retval != null) {
            return retval;
        }
        retval = null;
        return retval;
    }

    protected void applyStringTransformers(String[] items, int idx) {
        String item = items[idx];

        for (StringTransformer st : getStringTransformers()) {
            if (item == null && st.transform(item) == null) {
                continue;
            } else if (item == null || st.transform(item) == null) {
                items[idx] = st.transform(item);
                break;
            }
            if (!item.equals(st.transform(item))) {
                items[idx] = st.transform(item);
                break;
            }
        }
    }

    public UnaryOperator<TextFormatter.Change> getFilter() {
        return filter.get();
    }

    protected void setFilter(UnaryOperator<TextFormatter.Change> filter) {
        this.filter.set(filter);
    }

    public static class FormatterConverter extends StringConverter<String> {

        private final StringTextField textField;

        public FormatterConverter(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String txt) {
            System.err.println("!!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getTextFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            //System.err.println("StringTextField: toString = '" + txt + "'" + "; textField.text = " + textField.getText());

            /*            if (txt == null && textField.getNullSubstitution() != null) {
                return textField.getNullSubstitution();
            } else if (txt == null) {
                return "";
            }
             */
            System.err.println("1 !!!!!!!!!!! TO STRING txt = '" + txt + "'");
            String retval = textField.applySubstitutions(txt);

            if (retval != null) {
                txt = retval;
            }
            System.err.println("2 !!!!!!!!!!! TO STRING txt = '" + txt + "'");
            retval = "";
            String[] items;

            if (textField.getSeparator() != null) {
                items = textField.split(txt, false);
            } else {
                items = new String[]{txt};
            }
            for (int i = 0; i < items.length; i++) {
                textField.applyStringTransformers(items, i);
                if (txt == null && textField.getNullSubstitution() != null) {
                    items[i] = textField.getNullSubstitution();
                }
            }

            
            if (textField.getErrorMarkerBuilder().getErrorMarkers() != null ) {
                System.err.println("REMOVE ERROR MARKERS");
                textField.getChildren().removeAll(textField.getErrorMarkerBuilder().getErrorMarkers());
            }
            List<Integer> errorItemIndexes = textField.getErrorIndexes(items);

            if (!errorItemIndexes.isEmpty()) {
                textField.setErrorFound(Boolean.TRUE);
            } else {
                textField.setErrorFound(Boolean.FALSE);
            }
            if (textField.getErrorMarkerBuilder() != null) {
                Platform.runLater(() -> {
                    if (!errorItemIndexes.isEmpty()) {
                        Integer[] e = errorItemIndexes.toArray(new Integer[errorItemIndexes.size()]);
                        textField.getErrorMarkerBuilder().showErrorMarkers(e);
                    }

                });
            }

            StringBuilder sb = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();

            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                /*                if (item.trim().isEmpty() && textField.getValueIfBlank() != null) {
                    sb.append(textField.getValueIfBlank());
                    sbValue.append(textField.getValueIfBlank());
                    continue;
                }
                 */
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
            retval = sb.toString();
            if (textField.getSeparator() != null && !textField.getSeparator().isEmpty()) {
                if (sbValue.length() > 0 && sbValue.lastIndexOf(textField.getSeparator()) == sbValue.length() - 1) {
                    sbValue.deleteCharAt(sbValue.length() - 1);
                }
            }
            if (errorItemIndexes.isEmpty()) {
                System.err.println("before updateLastValidText newvalue=" + sb.toString());
                updateLastValidText(sbValue.toString());
            }
            return retval;

        }

        @Override
        public String fromString(String txt) {

            System.err.println("!!! FROM STRING txt = '" + txt + "'; formatterValue = '" + textField.getTextFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            if (txt == null && textField.getNullSubstitution() != null && textField.getTextFormatter().getValue() == null) {
                //
                // We do it to enforce the invocation of the method toString. Otherwise 
                // the text field will contain a valid value but displayed as an
                // empty string and not as the null substitution
                // value
                //
                return textField.getNullSubstitution();
                /*                Platform.runLater(() -> {
                    textField.getFormatter().setValue("");
                    textField.getFormatter().setValue(null);
                    
                });
                return null;
                 */
            }
            if (txt == null || txt.equals(textField.getNullSubstitution())) {
//                Platform.runLater(() -> {textField.commitValue();});
                return null;
            }
            if (txt.isEmpty() && textField.getEmptyListSubstitution() != null ) {
                return textField.getEmptyListSubstitution();
            }
            if (txt.isEmpty() || txt.equals(textField.getEmptyListSubstitution())) {
                return textField.getEmptyListSubstitution();
            }
            if (txt.isEmpty() || txt.equals(textField.getSingleEmptyItemSubstitution())) {
                //return textField.getSingleEmptyItemSubstitution();
            }

            return txt;
        }

        private void updateLastValidText(String validText) {
            textField.lastValidTextProperty().removeListener(textField.getLastValidValueChangeListener());
            textField.setLastValidText(validText);
            textField.lastValidTextProperty().addListener(textField.getLastValidValueChangeListener());

        }

    }//class FormatterConverter

    /*    public static class FormatterConverter_OLD extends StringConverter<String> {

        private final StringTextField textField;

        public FormatterConverter_OLD(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String txt) {
            System.err.println("!!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());

            if (txt == null && textField.getNullSubstitution() != null) {
                System.err.println("   -- getNullSubstitution() =" + textField.getNullSubstitution());

                return textField.getNullSubstitution();
            } else if (txt == null) {
                return "";
            }
            return txt;
        }

        @Override
        public String fromString(String txt) {

            System.err.println("!!! FROM STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
//            }
            if (txt == null || txt.equals(textField.getNullSubstitution())) {
                return null;
            }

            if (!textField.isEditable()) {
                //return txt;
            }

            String[] items;

            if (textField.getSeparator() != null) {
                items = textField.split(txt, false);
            } else {
                items = new String[]{txt};
            }
            for (int i = 0; i < items.length; i++) {
                if (textField.getToStringTransformer() != null) {
                    items[i] = textField.getToStringTransformer().transform(items[i]);
                }
                if (txt == null && textField.getNullSubstitution() != null) {
                    items[i] = textField.getNullSubstitution();
                }

            }
            List<Integer> errorItemIndexes = textField.getErrorIndexes(items);
            if (errorItemIndexes.isEmpty()) {
                if (textField.getErrorMarkerBuilder().getErrorMarkers() != null && textField.getErrorMarkerBuilder().getErrorMarkers().length > 0) {
                    textField.getChildren().removeAll(textField.getErrorMarkerBuilder().getErrorMarkers());
                }
            }
            if (!errorItemIndexes.isEmpty() && (textField.getSeparator() == null || !textField.getSeparator().isEmpty())) {
                textField.setErrorFound(Boolean.TRUE);
            } else {
                textField.setErrorFound(Boolean.FALSE);
            }
            if (textField.getErrorMarkerBuilder() != null) {
                Platform.runLater(() -> {
                    if (!errorItemIndexes.isEmpty()) {
                        Integer[] e = errorItemIndexes.toArray(new Integer[errorItemIndexes.size()]);
                        textField.getErrorMarkerBuilder().showErrorMarkers(e);
                    }

                });
            }

            String retval = "";
            StringBuilder sb = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();


            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                //System.err.println("StringTextField: fromString item = '" + item + "'" + "; valueIfBlank=" + textField.getValueIfBlank());
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
            retval = sb.toString();
            if (textField.getSeparator() != null && !textField.getSeparator().isEmpty()) {
                if (sbValue.length() > 0 && sbValue.lastIndexOf(textField.getSeparator()) == sbValue.length() - 1) {
                    sbValue.deleteCharAt(sbValue.length() - 1);
                }
            }
            if (errorItemIndexes.isEmpty()) {
                updateLastValidText(sbValue.toString());
            }
            return retval;
        }

        private void updateLastValidText(String validText) {
            textField.lastValidTextProperty().removeListener(textField.getLastValidValueChangeListener());
            textField.setLastValidText(validText);
            textField.lastValidTextProperty().addListener(textField.getLastValidValueChangeListener());

        }

    }//class FormatterConverter
     */
}
