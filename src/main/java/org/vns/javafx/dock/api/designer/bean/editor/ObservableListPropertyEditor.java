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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.IndexRange;
import javafx.scene.input.MouseEvent;
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
public class ObservableListPropertyEditor<E> extends StringTextField implements ListPropertyEditor<E> {//, ErrorPointerSupport {

    private ObservableList<E> boundList = FXCollections.observableArrayList();
    private ListContentStringBinding<E> listContentBinding;
    private StringConverter<E> stringConverter;
    //private String emptyListSubstitution;
    //private String singleEmptyItemSubstitution;

    public ObservableListPropertyEditor() {
        init();
    }

    private void init() {
        addValidators();
        addFilterValidators();
    }

    @Override
    protected String applySubstitutions(String txt) {
        if ( true ) {
            return super.applySubstitutions(txt);
        }
        System.err.println("applySubstitutions txt = '" + txt + "'");
        String retval = getNullSubstitution();
        if (txt == null && retval == null) {
            retval = "";
        } else if (retval != null) {

        } else if (getEmptySubstitution() != null && txt != null && txt.equals(getEmptySubstitution())) {
            retval = getEmptySubstitution();
        }
        System.err.println("applySubstitutions retval = '" + retval + "'");
        return retval;
    }

    public ObservableList<E> getBoundList() {
        return boundList;
    }

    @Override
    public void setNullSubstitution(String nullSubstitution) {
        super.setNullSubstitution(nullSubstitution);
    }

    @Override
    public String getEmptySubstitution() {
        return super.getEmptySubstitution();
    }

    @Override
    public void setEmptySubstitution(String emptyListSubstitution) {
        super.setEmptySubstitution(emptyListSubstitution);
    }

    @Override
    public String getSingleEmptyItemSubstitution() {
        return super.getSingleEmptyItemSubstitution();
    }

    @Override
    public void setSingleEmptyItemSubstitution(String singleEmptyItemSubstitution) {
        super.setSingleEmptyItemSubstitution(singleEmptyItemSubstitution);
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

    @Override
    protected boolean testValidators(String item) {

        if (getBoundList() != null && isBound()) {
            String s = getEmptySubstitution();
            if (s != null && s.equals(item) && getBoundList().isEmpty()) {
                return true;
            }
            s = getSingleEmptyItemSubstitution();
            if (s != null && s.equals(item) && getBoundList().size() == 1) {
                return true;
            }
        }
        return super.testValidators(item);
    }

    public boolean isSubstitution(String item) {
        return false;
    }

    @Override
    public void bind(ObservableList<E> property) {
    }

    @Override
    public void bindBidirectional(ObservableList<E> property) {
        boundList = (ObservableList) property;

        this.setEditable(true);
        this.setFocusTraversable(true);
        
        listContentBinding = new ListContentStringBinding(lastValidTextProperty(), boundList, ",", getStringConverter());
        listContentBinding.bind();
    }

    @Override
    public void unbind() {
        if (listContentBinding != null) {
            listContentBinding.unbind();
        }
    }

    /*    public abstract E toListItem(String item);

    public String fromListItem(E obj) {
        return obj.toString();
   }
     */
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public boolean isBound() {
        return getListContentBinding() != null && getListContentBinding().isBound();
    }
    
    protected void addValidators() {
    }

    protected void addFilterValidators() {
    }

}
