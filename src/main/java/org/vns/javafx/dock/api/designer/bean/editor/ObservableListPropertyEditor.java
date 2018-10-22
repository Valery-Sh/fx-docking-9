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
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.StringConverter;

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
 *
 * @param <E> name
 * @see StringListPropertyEditor
 * @see IntegerListPropertyEditor
 *
 * @author Valery Shyshkin
 */
public class ObservableListPropertyEditor<E> extends AbstractPropertyEditor<E> implements ListPropertyEditor<E> {//, ErrorPointerSupport {

    private ObservableList<E> boundList = FXCollections.observableArrayList();
    private ListContentStringBinding<E> listContentBinding;
    private StringConverter<E> stringConverter;

    private ObservableListEditor textField;

    public ObservableListPropertyEditor() {
        this(null);
    }

    public ObservableListPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
//        textField = new ObservableListEditor(getName());
        addValidators();
        addFilterValidators();
        addSubstitutionsFilterValidators();

    }

    //@Override
    protected String applySubstitutions(String txt) {
        if (true) {
            return textField.applySubstitutions(txt);
        }
        String retval = textField.getNullSubstitution();
        if (txt == null && retval == null) {
            retval = "";
        } else if (retval != null) {

        } else if (textField.getEmptySubstitution() != null && txt != null && txt.equals(textField.getEmptySubstitution())) {
            retval = textField.getEmptySubstitution();
        }
        return retval;
    }

    public ObservableListEditor getTextField() {
        return textField;
    }

    public ObservableList<E> getBoundList() {
        return boundList;
    }

    public ListContentStringBinding<E> getListContentBinding() {
        return listContentBinding;
    }

    public StringConverter<E> getStringConverter() {
        return stringConverter;
    }

    public void setStringConverter(StringConverter<E> converter) {
        this.stringConverter = converter;
    }

    protected boolean testValidators(String item) {

        if (getBoundList() != null && isBound()) {
            String s = textField.getEmptySubstitution();
            if (s != null && s.equals(item) && getBoundList().isEmpty()) {
                return true;
            }
            s = textField.getSingleEmptyItemSubstitution();
            if (s != null && s.equals(item) && getBoundList().size() == 1) {
                return true;
            }
        }
        return textField.testValidators(item);
    }

    public boolean isSubstitution(String item) {
        return false;
    }

    @Override
    public void bind(ObservableList<E> property) {
        boundList = (ObservableList) property;

        this.setEditable(false);
        this.setFocusTraversable(false);

        listContentBinding = new ListContentStringBinding(textField.lastValidTextProperty(), boundList, ",", getStringConverter());
        listContentBinding.bind();

    }

    @Override
    public void bindBidirectional(ObservableList<E> property) {
        boundList = property;

        this.setEditable(true);
        this.setFocusTraversable(true);

        listContentBinding = new ListContentStringBinding(textField.lastValidTextProperty(), boundList, ",", getStringConverter());
        listContentBinding.bindBidirectional();
    }

    @Override
    public void unbind() {
        if (listContentBinding != null) {
            listContentBinding.unbind();
        }
        boundList = null;
        setBoundProperty(null);
        listContentBinding = null;
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("resources/styles/default.css").toExternalForm();
    }

    @Override
    public boolean isBound() {
        return getListContentBinding() != null && getListContentBinding().isBound() || getBoundList() != null || getBoundProperty() != null;
    }

    protected void addValidators() {
    }

    protected void addFilterValidators() {
    }

    protected void addSubstitutionsFilterValidators() {
        textField.getSubstitutionFilterValidators().add(it -> {

            if (it == null) {
                return false;
            }
            boolean retval = false;
            if (textField.getNullSubstitution() != null) {
                for (int i = 0; i < it.length(); i++) {
                    if (textField.getNullSubstitution().startsWith(it)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Override
    protected Node createEditorNode() {
        textField = new ObservableListEditor();
        textField.setTestBindValidator((item) -> {
            boolean retval = false;
            if (getBoundList() != null && isBound()) {
                String s = textField.getEmptySubstitution();
                if (s != null && s.equals(item) && getBoundList().isEmpty()) {
                    retval = true;
                } else {
                    s = textField.getSingleEmptyItemSubstitution();
                    if (s != null && s.equals(item) && getBoundList().size() == 1) {
                        retval = true;
                    }
                }
            }
            return retval;
        });
        return textField;
    }

    @Override
    public void bind(ReadOnlyProperty<E> property) {
        if (property instanceof ListProperty) {
            setBoundProperty(property);
            bind(((ListProperty) property).getValue());
        }

    }

    @Override
    public void bindBidirectional(Property<E> property) {
        if (property instanceof ListProperty) {
            setBoundProperty(property);
            bindBidirectional(((ListProperty) property).getValue());
        }

    }

}
