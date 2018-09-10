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
    private StringTransformer fromStringTransformer;
    private StringTransformer toStringTransformer;

    private String valueIfBlank;

    private final StringProperty nullSubstitution = new SimpleStringProperty(null);

    private String separator = null;

    protected TextFormatter formatter;

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
            System.err.println("lastValidValueChangeListener (calls invalidateFormatterValue) ov=" + ov + "; nv=" + nv);
            invalidateFormatterValue();
        };
        this.filterChangeListener = (v, ov, nv) -> {
            if (nv != null) {
                formatter = new TextFormatter<>(new FormatterConverter(this), getText(), nv);
                this.setTextFormatter(formatter);
            } else {
                formatter = new TextFormatter<>(new FormatterConverter(this), getText());
                this.setTextFormatter(formatter);
            }
        };
        this.filter = new SimpleObjectProperty<>(change -> {
            System.err.println("!!! FILTER change = " + change.getClass().getName());
            if (((TextField) change.getControl()).getText() == null) {
                return change;
            }
            if (isAcceptable(change.getControlNewText())) {
                System.err.println("ACCEPTABLE=TRUE getText() = '" + getText() + "'" );
                return change;
            } else {
                System.err.println("ACCEPTABLE=FALSE getText() = '" + getText() + "'" );
                return null;
            }

        });
        init();
    }

    private void init() {

        getStyleClass().add("string-textfield");
        setErrorMarkerBuilder(new ErrorMarkerBuilder(this));
        formatter = new TextFormatter(new FormatterConverter(this), getText(), getFilter());
        this.setTextFormatter(formatter);
        filter.addListener(filterChangeListener);
        lastValidText.addListener(lastValidValueChangeListener);
        nullSubstitution.addListener((v, ov, nv) -> {
            if (ov == null) {
                System.err.println("nullSubstitution listener '" + getText() + "'");
                String tx = getText();
                if ( tx != null ) {
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

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    protected void mouseClicked(MouseEvent ev) {

        if (getNullSubstitution() == null || getNullSubstitution().isEmpty()) {
            return;
        }
        IndexRange range = getItemRange();
        if (range == null) {
            return;
        }
        if (getNullSubstitution().equals(getText().substring(range.getStart(), range.getEnd()))) {
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

        int caretPos = getCaretPosition();
        int itemPos = 0;
        for (int i = 0; i < items.length; i++) {
            if (itemPos <= caretPos && itemPos + items[i].length() >= caretPos) {
                retval = new IndexRange(itemPos, itemPos + items[i].length());
                break;
            }
            itemPos += items[i].length() + getSeparator().length();
        }
        return retval;
    }

    public String[] split(String txt) {
        return split(txt, true);
    }

    public String[] split(String txt, boolean ignoreQuotes) {
        if (getSeparator() == null || !txt.contains(getSeparator())) {
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
            if (getSeparator().equals(sb.substring(n, n + getSeparator().length()))) {
                if (n == 0) {
                    list.add("");
                } else {
                    list.add(sb.substring(0, n));
                }
                sb = sb.delete(0, n + getSeparator().length());
                n = 0;
                continue;
            }
            n++;

        }
        retval = list.toArray(new String[0]);
        return retval;
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
//        System.err.println("1 invalidateFormatterValue formatter getLastValitText()=" + getLastValidText());
        formatter.setValue(null);

        formatter.setValue(getLastValidText());
        //formatter.setValue(getText());
        //System.err.println("invalidateFormatterValue formatter getValue()=" + formatter.getValue());
        //formatter.setValue(list);

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
//        System.err.println("isAcceptable text = " + txt);

        if (getSeparator() == null || getSeparator().isEmpty()) {
            return testFilterValidators(txt);
        }
        if (getFilterValidators().isEmpty()) {
            return true;
        }
        //!!!!String[] items = txt.split(getSeparator(), txt.length());
        String[] items = split(txt, false);
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
        if (getNullSubstitution() != null && getNullSubstitution().equals(item)) {
            return true;
        }
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

    public static class FormatterConverter extends StringConverter<String> {

        private final StringTextField textField;

        public FormatterConverter(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String txt) {
            System.err.println("!!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            //System.err.println("StringTextField: toString = '" + txt + "'" + "; textField.text = " + textField.getText());

            if (txt == null && textField.getNullSubstitution() != null) {
                System.err.println("   -- getNullSubstitution() =" + textField.getNullSubstitution());

                return textField.getNullSubstitution();
            } else if (txt == null) {
                return "";
            }
            String[] items;

            if (textField.getSeparator() != null) {
                items = textField.split(txt, false);
            } else {
                items = new String[]{txt};
            }
            for (int i = 0; i < items.length; i++) {
                if (textField.getFromStringTransformer() != null) {
                    items[i] = textField.getFromStringTransformer().transform(items[i]);
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

        @Override
        public String fromString(String txt) {

            System.err.println("!!! FROM STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            if (txt == null && textField.getNullSubstitution() != null && textField.getFormatter().getValue() == null  ) {
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
            return txt;
        }

        private void updateLastValidText(String validText) {
            textField.lastValidTextProperty().removeListener(textField.getLastValidValueChangeListener());
            textField.setLastValidText(validText);
            textField.lastValidTextProperty().addListener(textField.getLastValidValueChangeListener());

        }

    }//class FormatterConverter

    public static class FormatterConverter_OLD extends StringConverter<String> {

        private final StringTextField textField;

        public FormatterConverter_OLD(StringTextField textField) {
            this.textField = textField;
        }

        @Override
        public String toString(String txt) {
            System.err.println("!!! TO STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            //System.err.println("StringTextField: toString = '" + txt + "'" + "; textField.text = " + textField.getText());

            if (txt == null && textField.getNullSubstitution() != null) {
                System.err.println("   -- getNullSubstitution() =" + textField.getNullSubstitution());

                return textField.getNullSubstitution();
            } else if (txt == null) {
                return "";
            }
            return txt;
            //return textField.toString(list);
        }

        @Override
        public String fromString(String txt) {

            System.err.println("!!! FROM STRING txt = '" + txt + "'; formatterValue = '" + textField.getFormatter().getValue() + "'");
            System.err.println("   -- text =" + textField.getText());
            //   if ( true ) return (String) textField.getFormatter().getValue();
//            if ( txt == null && textField.getNullSubstitution() == null ) {
//                return "";
//            }
            if (txt == null || txt.equals(textField.getNullSubstitution())) {
                //textField.getFormatter().setValue(null);
                return null;
            }

            if (!textField.isEditable()) {
                //return txt;
            }

            //System.err.println("!!! StringTextField: fromString txt = '" + txt + "'");
            String[] items;

            if (textField.getSeparator() != null) {
                //!!!!items = txt.split(textField.getSeparator(), txt.length());
                items = textField.split(txt, false);
            } else {
                items = new String[]{txt};
            }
            for (int i = 0; i < items.length; i++) {
                //System.err.println("it = '" + items[i] + "'");
                if (textField.getFromStringTransformer() != null) {
                    //      System.err.println("!!! StringTextField: fromString getFromStringTransformer != null");
                    items[i] = textField.getFromStringTransformer().transform(items[i]);
                }
                if (txt == null && textField.getNullSubstitution() != null) {
                    //      System.err.println("!!! StringTextField: fromString getFromStringTransformer != null");
                    items[i] = textField.getNullSubstitution();
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

//System.err.println("ERROR 4");            
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
//            System.err.println("ERROR 5");
            // System.err.println(" 444 StringTextField: fromString sb.len = " + sb.length());
            retval = sb.toString();
//            System.err.println("444");
            if (textField.getSeparator() != null && !textField.getSeparator().isEmpty()) {
                //   System.err.println("555 sbValue = " + sbValue + "; sbValue.length() = " + sbValue.length());
                if (sbValue.length() > 0 && sbValue.lastIndexOf(textField.getSeparator()) == sbValue.length() - 1) {
//                    System.err.println("666");
                    sbValue.deleteCharAt(sbValue.length() - 1);
                }
            }
            //System.err.println("777 == '" + sbValue.toString() + "'");
            if (errorItemIndexes.isEmpty()) {
                updateLastValidText(sbValue.toString());
            }
            //System.err.println("StringTextField: fromString retval='" + retval + "'");
//            System.err.println("StringTextField: fromString rightValue='" + textField.getLastValidText() + "'");
            //System.err.println("end fromString------------------------------------------");
            return retval;
        }

        private void updateLastValidText(String validText) {
            textField.lastValidTextProperty().removeListener(textField.getLastValidValueChangeListener());
            textField.setLastValidText(validText);
            textField.lastValidTextProperty().addListener(textField.getLastValidValueChangeListener());

        }

    }//class FormatterConverter

}
