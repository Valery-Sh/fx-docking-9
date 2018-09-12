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
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 * The base class is used as an editor for properties of type
 * {@code ObservableList&lt;E&gt;}. The class is a sub class of the
 * {@code TextField} and we assume that by we can put a sb as a comma-separated
 * list of items and each item represents a sb item. The class has a property
 * named {@code valueIfBlank} which is of type {@code String}. By default it is
 * set to null.
 * <p>
 * We can set the value to any string value. In this case every empty item will
 * be replaced with that value. We can impose restrictions on the items that can
 * be entered. To achieve this goal we must add one or more objects of type
 * Predicate&lt;String&gt; to the list of validators. The field {@code validators
 * } is defined as follows
 * </p>
 * <pre>
 * private ObservableList&lt;Predicate&lt;String&gt;&gt; validators
 * </pre> Let us extend the code which now will look like the sb below
 *
 * It is not necessary to use a comma as the separator of string elements. For
 * this purpose, any sequence of characters can be applied. Use the 
 {@link StringTextField#setSeparator(java.lang.String) }.
 *
 *
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
public abstract class ObservableListPropertyEditor_OLD<E> extends StringTextField implements PropertyEditor<ObservableList> {//, ErrorPointerSupport {

    private final ListProperty<E> value = new SimpleListProperty<>(FXCollections.observableArrayList());
    
    
    private ObservableList<E> boundValue = FXCollections.observableArrayList();

    public ObservableListPropertyEditor_OLD() {
        init();

    }

    private void init() {
        setSeparator(",");
        addValidators();
        addFilterValidators();
        System.err.println("OL getErrorItems.isEmpty = " + getErrorItems().isEmpty());
    }
    private final ListChangeListener<? super E> valueChangeListener = (c) -> {
        invalidateFormatterValue();
    };

    /**
     * The method is called when the content of the observable list is changed.
     * We must raise Invalidation event for {@link #valueProperty() }
     * in the formatter in order to make the formatter to execute the method {@link FormatterConverter#toString(javafx.collections.ObservableList)
     * }. First set the {@code value} property of the {@code TextFormatter} to
     * {@code null} and then assign again the {@link #valueProperty() } of this
     * control.
     * <p>
     * We must enforce the {@code TextFormatter} to invoke the method
     * </p>
     *
     */
    //@Override
    
    @Override
    protected void invalidateFormatterValue() {
        System.err.println("INVALIDATE LIST FORMATTER VAUE");
        //if ( true ) return;
        //
        // We must raise Invalidation event for valueProperty() in the formatter 
        // in order to make the formatter to execute the converter method toString().
        // First set formatter'item property to null and then assign again
        // the value from this control
        //

        getTextFormatter().setValue(null);
        //formatter.setValue(getValue());
        ((TextFormatter<ObservableList<E>>)getTextFormatter()).setValue(getValue());

        //
        // We need the TextFormatter to execute the StriringConverter's method
        // fromString in order to validate items in the observable list and
        // mark errors. 
        //
        Platform.runLater(() -> {
            commitValue();
        });
    }

    @Override
    protected TextFormatter createTextFormatter() {
        return null;
    }
    protected StringConverter createFormatterConverter() {
        return new FormatterConverter(this);
    }
    protected void initFormatter() {
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getValue().size(); i++) {
            sb.append(getValue().get(i));
            if (i != getValue().size() - 1) {
                sb.append(getSeparator());
            }
        }
        setText(sb.toString());
        setTextFormatter(new TextFormatter<>(createFormatterConverter(), getValue(), getFilter()));        
        
        //setTextFormatter(formatter);
        //setTextFormatter(new TextFormatter<>(createFormatterConverter(), getValue(), getFilter()));        
        getValue().addListener(valueChangeListener);

    }

    public ListChangeListener<? super E> getValueChangeListener() {
        return valueChangeListener;
    }

    /**
     * Binds content of the list specified by the property
     * {@code valueProperty()} to the given list.
     *
     * @param list the list to be bound to
     */
    public void bindContentBidirectional(ObservableList<E> list) {
        if ( boundValue != null ) {
            valueProperty().unbindContentBidirectional(list);            
        }
        this.boundValue = list;
        this.setEditable(true);
        this.setFocusTraversable(true);
        
        valueProperty().bindContentBidirectional(list);
        initFormatter();
        //
        // We need the TextFormatter to execute the StriringConverter's method
        // fromString in order to validate items in the observable list and
        // mark errors. 
        //
        Platform.runLater(() -> {
            commitValue();
        });
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
        ObservableList l = (ObservableList)property;
        //valueProperty().bindContentBidirectional(l);        
    }

    @Override
    public void unbind() {
        if (boundValue != null) {
            value.unbindContentBidirectional(boundValue);
        }
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

    protected void addValidators() {

    }

    protected void addFilterValidators() {

    }

    public String toString(ObservableList v) {
        System.err.println("*---- TO STRING value = " + v + "; getErrorItems().isEmpty()=" + getErrorItems().isEmpty() + "; text = " + getText());
        if (hasErrorItems()) {
            return getText();
        }
/*        StringBuilder sb = new StringBuilder();
        if (v != null && !v.isEmpty()) {
            for (int i = 0; i < v.size(); i++) {
                String errorItem = getErrorItems().get(i);
//                System.err.println("toString errorItem='" + errorItem + "'");
                if (errorItem != null) {
                    sb.append(errorItem);
                } else {
                    sb.append(fromListItem((E) v.get(i)));
                }
                if (i != v.size() - 1) {
                    sb.append(getSeparator());
                }
            }
        }
*/
        StringBuilder sb = new StringBuilder();
        if (v != null && !v.isEmpty()) {
            for (int i = 0; i < v.size(); i++) {
                    sb.append(fromListItem((E) v.get(i)));
                if (i != v.size() - 1) {
                    sb.append(getSeparator());
                }
            }
        }
        String retval = sb.toString();
//!!!        System.err.println("toString retval=" + retval);
        Platform.runLater(() -> {
            //
            // We put the following two lines in order to fix a problem when 
            // append sb programmatically and after pressing Enter key the 
            // cursor moves one character backword. We see this behaivor when use 
            // TextTransformer
            //
            this.backward();
            this.end();
        });

        return retval;
    }

    public ObservableList fromString(String tx) {
        System.err.println("*---- FROM STRING text = " + tx);

        String[] items = split(tx);

        for (int i = 0; i < items.length; i++) {
            applyStringTransformers(items, i);
        }
        List<Integer> errorItemIndexes = null;
        if ( items.length == 1 && items[0].isEmpty() ) {
            errorItemIndexes = new ArrayList<>();
        } else {
            errorItemIndexes = getErrorIndexes(items);
        }
        if (errorItemIndexes.isEmpty()) {
            if (getErrorMarkerBuilder().getErrorMarkers() != null && getErrorMarkerBuilder().getErrorMarkers().length > 0) {
                getChildren().removeAll(getErrorMarkerBuilder().getErrorMarkers());
            }
            setErrorFound(Boolean.FALSE);
        } else if (getSeparator() == null || !getSeparator().isEmpty()) {
            setErrorFound(Boolean.TRUE);
        }

        if (getErrorMarkerBuilder() != null) {
            List<Integer> idxs = new ArrayList<>(errorItemIndexes);
            Platform.runLater(() -> {
                if (!idxs.isEmpty()) {
                    Integer[] e = idxs.toArray(new Integer[idxs.size()]);
                    getErrorMarkerBuilder().showErrorMarkers(e);
                }
            });
        }
        ObservableList<E> retval = FXCollections.observableArrayList();

        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (errorItemIndexes.contains(i)) {
                continue;
            }
//11.09            if (item.trim().isEmpty() && getValueIfBlank() != null && !tx.trim().isEmpty()) {

//                retval.add(toListItem(getValueIfBlank()));
//                continue;
//            }

            if ( items.length > 1 || items.length == 1 && ! items[i].isEmpty() ) {
                System.err.println("ADD TO LIST ITEM");
                retval.add(toListItem(item));
            }
        }
        getValue().removeListener(getValueChangeListener());
        getValue().clear();
        getValue().addAll(retval);

        getValue().addListener(getValueChangeListener());
//        System.err.println("   fromString retval=" + retval);
//        System.err.println("   fromString value=" + getValue());
//        System.err.println("end fromString------------------------------------------");

        return retval;
    }

    public static class FormatterConverter extends StringConverter<ObservableList> {

        private final ObservableListPropertyEditor_OLD textField;

        public FormatterConverter(ObservableListPropertyEditor_OLD textField) {
            this.textField = textField;
        }

        @Override
        public String toString(ObservableList list) {
            return textField.toString(list);
        }

        @Override
        public ObservableList fromString(String txt) {
            return textField.fromString(txt);

        }

    }//class FormatterConverter

}
