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
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 * The base class is used as an editor for properties of type
 * {@code ObservableList&lt;E&gt;}. The class is a sub class of the
 * {@code TextField} and we assume that by we can put a text as a
 * comma-separated list of items and each item represents a text item. The class
 * has a property named {@code valueIfBlank} which is of type {@code String}. By
 * default it is set to null.
 * <p>
 * We can set the value to any string value. In this case every empty item will
 * be replaced with that value. We can impose restrictions on the items that can
 * be entered. To achieve this goal we must add one or more objects of type
 * Predicate&lt;String&gt; to the list of validators. The field {@code validators
 * } is defined as follows
 * </p>
 * <pre>
 * private ObservableList&lt;Predicate&lt;String&gt;&gt; validators
 * </pre> Let us extend the code which now will look like the text below
 *
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
 * <pre>
 *   \s*;
 *   ;\s+
 *   \s*;\s*
 * </pre> The first expression causes the left trimming of the string element,
 * the second right trimming and the third both left and right trimming.
 * <p>
 * A subclass of this class should, as a rule, override one or more of the
 * methods presented below
 * </p>
 * <pre>
 *   {@link #isAcceptable(java.lang.String) }
 *   {@link #toListItem(java.lang.String) }
 * </pre>
 *
 * @see StringListPropertyEditor
 * @see IntegerListPropertyEditor
 *
 * @author Valery Shyshkin
 */
public abstract class ObservableListPropertyEditor<E> extends StringTextField implements PropertyEditor<ObservableList> {//, ErrorPointerSupport {

    private final ListProperty<E> value = new SimpleListProperty<>(FXCollections.observableArrayList());


    public ObservableListPropertyEditor() {
        init();

    }

    private void init() {
        setSeparator(",", "\\s*,\\s*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (isAcceptable(change.getControlNewText())) {
                return change;
            } else {
                return null;
            }
        };
        setFilter(filter);
    }

    protected abstract boolean isAcceptable(String txt);

    private final ListChangeListener<? super E> valueChangeListener = (c) -> {
        invalidateFormatterValue();
    };

    /*    public ErrorMarkerBuilder getErrorMarkerBuilder() {
        return errorMarkerBuilder;
    }

    public void setErrorMarkerBuilder(ErrorMarkerBuilder errorMarkerBuilder) {
        this.errorMarkerBuilder = errorMarkerBuilder;
    }
     */
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
    private void invalidateFormatterValue() {
        //
        // We must raise Invalidation event for valueProperty() in the formatter 
        // in order to make the formatter to execute the converter method toString().
        // First set formatter'item property to null and then assign again
        // the value from this control
        //

        System.err.println("invalidateFormatterValue  formatter.getValue() " + (getValue() == formatter.getValue()));
        //ObservableList list = FXCollections.observableArrayList();

        //list.addAll(formatter.getValue());
        formatter.setValue(null);

        formatter.setValue(getValue());
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
    
    protected StringConverter createConverter() {
        return new Converter(this);
    }

    protected void initFormatter() {
        formatter = new TextFormatter<>(createConverter(), getValue(), getFilter());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getValue().size(); i++) {
            sb.append(getValue().get(i));
            if (i != getValue().size() - 1) {
                sb.append(getSeparator());
            }
        }
        setText(sb.toString());
        setTextFormatter(formatter);
        getValue().addListener(valueChangeListener);

    }

    /*    public ErrorDialog getErrorDialog() {
        return errorDialog;
    }

    public void setErrorDialog(ErrorDialog errorDialog) {
        this.errorDialog = errorDialog;
    }
     */
    public ListChangeListener<? super E> getValueChangeListener() {
        return valueChangeListener;
    }

    /*    public TextFormatter<ObservableList<E>> getFormatter() {
        return formatter;
    }
     */
 /*    public TextFormatter getFormatter() {
        return formatter;
    }

    public UnaryOperator<TextFormatter.Change> getFilter() {
        return filter;
    }
     */
    /**
     * Binds content of the list specified by the property
     * {@code valueProperty()} to the given list.
     *
     * @param list the list to be bound to
     */
    public void bindContentBidirectional(ObservableList<E> list) {

        this.setEditable(true);
        this.setFocusTraversable(true);
        valueProperty().unbindContentBidirectional(list);
        valueProperty().bindContentBidirectional(list);
        initFormatter();
    }

    public boolean validateListItem(E listItem) {
        String tx = fromListItem(listItem);
        if (isAcceptable(tx) && validateStringListItem(tx)) {
            return true;
        }
        return false;
    }

    public ListProperty<E> valueProperty() {
        return value;
    }

    public ObservableList<E> getValue() {
        return value.getValue();
    }

    public void setValue(ObservableList<E> value) {
        if (!valueProperty().isBound()) {
            this.value.setValue(value);
        }
    }

    @Override
    public void bind(Property property) {
    }

    @Override
    public void bindBidirectional(Property property) {
    }

    @Override
    public void unbind() {
        value.unbind();
    }

    @Override
    public boolean isBound() {
        return value.isBound();

    }

    public abstract E toListItem(String item);

    public String fromListItem(E obj) {
        return obj.toString();
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public String toString(ObservableList v) {
        System.err.println("*---- TO STRING value = " + v);
        StringBuilder text = new StringBuilder();
        if (v != null && !v.isEmpty()) {
            for (int i = 0; i < v.size(); i++) {
                String errorItem = getErrorItems().get(i);
                if (errorItem != null) {
                    text.append(errorItem);
                } else {
                    text.append(fromListItem((E) v.get(i)));
                }
                if (i != v.size() - 1) {
                    text.append(getSeparator());
                }
            }
        }
        String retval = text.toString();

        Platform.runLater(() -> {
            //
            // We put the following two lines in order to fix a problem when 
            // append text programmatically and after pressing Enter key the 
            // cursor moves one character backword. We see this behaivor when use 
            // TextTransformer
            //
            this.backward();
            this.end();
        });

        return retval;
    }

    public String[] split(String tx) {
        return tx.split(getSeparatorRegExp());
    }

    public ObservableList fromString(String tx) {
        System.err.println("*---- FROM STRING text = " + tx);

        String[] items = split(tx);
        List<Integer> errorItemIndexes = getErrorIndexes(items);

        if (errorItemIndexes.isEmpty()) {
            if (getErrorMarkerBuilder().getErrorMarkers() != null && getErrorMarkerBuilder().getErrorMarkers().length > 0) {
                getChildren().removeAll(getErrorMarkerBuilder().getErrorMarkers());
            }
        }
        if (getErrorMarkerBuilder() != null) {
            Platform.runLater(() -> {
                if (!errorItemIndexes.isEmpty()) {
                    Integer[] e = errorItemIndexes.toArray(new Integer[errorItemIndexes.size()]);
                    getErrorMarkerBuilder().showErrorMarkers(e);
                }

            });
        }
        ObservableList<E> retval = FXCollections.observableArrayList();

        retval.clear();

        for (int i = 0; i < items.length; i++) {
            String item = items[i];

            if (item.trim().isEmpty() && getValueIfBlank() != null && !tx.trim().isEmpty()) {
                retval.add(toListItem(getValueIfBlank()));
                continue;
            }
            if (item.trim().isEmpty()) {
                continue;
            }
            retval.add(toListItem(item));
        }
        getValue().removeListener(getValueChangeListener());
        getValue().clear();
        getValue().addAll(retval);

        int d = 0;
        for (int idx : errorItemIndexes) {
            getValue().remove(idx - d);
            d++;
        }

        getValue().addListener(getValueChangeListener());
        System.err.println("   fromString retval=" + retval);
        System.err.println("   fromString value=" + getValue());
        System.err.println("end fromString------------------------------------------");
        return retval;
    }

    public static class Converter extends StringConverter<ObservableList> {

        private final ObservableListPropertyEditor textField;

        public Converter(ObservableListPropertyEditor textField) {
            this.textField = textField;
        }

        /*        @Override
        public String toString(ObservableList<E> v) {
            return textField.toString(v);
        }

        @Override
        public ObservableList<String> fromString(String tx) {
            return textField.fromString(tx);
        }
         */
        @Override
        public String toString(ObservableList list) {
            return textField.toString(list);
        }

        @Override
        public ObservableList fromString(String txt) {
            return textField.fromString(txt);

        }

    }//class Converter

}
