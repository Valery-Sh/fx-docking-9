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

import static org.vns.javafx.dock.api.designer.bean.editor.SubstitutionConverter.Substitution.NULL;

/**
 *
 * @author Valery
 */
public interface SubstitutionConverter<E> {

    static enum Substitution {
        NULL, EMPTY_LIST, EMPTY_SINGLE_ITEM
    }

    ObservableListPropertyEditor<E> getEditor();

    default Substitution getSubstitution(String item) {
        return getSubstitution(getEditor(), item);
    }

    default String toSubstitution(E obj) {
        return toSubstitution(getEditor(), obj);
    }

    default boolean isEmptyListSubstitution(String item) {
        if (getEditor().getBoundList().isEmpty() && getEditor().getEmptySubstitution() != null && getEditor().getEmptySubstitution().equals(item)) {
            return true;
        } else {
            return false;
        }       
    }
    default boolean isNullSubstitution(String item) {
        if (getEditor().getNullSubstitution() != null && getEditor().getNullSubstitution().equals(item)) {
            return true;
        } else {
            return false;
        }       
    }    
    default boolean isSingleEmptyItemSubstitution(String item) {
        if (getEditor().getBoundList().size() == 1 && getEditor().getSingleEmptyItemSubstitution() != null && getEditor().getSingleEmptyItemSubstitution().equals(item)) {
            return true;
        } else {
            return false;
        }       
    }

    static <T> Substitution getSubstitution(ObservableListPropertyEditor<T> editor, String item) {
       
        if (editor.getBoundList().isEmpty() && editor.getEmptySubstitution() != null && editor.getEmptySubstitution().equals(item)) {
            return Substitution.EMPTY_LIST;
        }
        if (editor.getBoundList().size() == 1 && editor.getSingleEmptyItemSubstitution() != null && editor.getSingleEmptyItemSubstitution().equals(item)) {
            return Substitution.EMPTY_SINGLE_ITEM;
        }
        if (editor.getNullSubstitution() != null && editor.getNullSubstitution().equals(item)) {
            return Substitution.NULL;
        }
        return null;
    }

    static <T> String toSubstitution(ObservableListPropertyEditor<T> editor, T item) {
        if (editor.getBoundList().isEmpty() && editor.getEmptySubstitution() != null && editor.getBoundList().isEmpty()) {
            return editor.getEmptySubstitution();
        }

        if (item == null && editor.getNullSubstitution() != null && !editor.getBoundList().isEmpty()) {
            return editor.getNullSubstitution();
        }
        if (editor.getBoundList().size() == 1 && editor.getSingleEmptyItemSubstitution() != null && editor.getSingleEmptyItemSubstitution().equals(item)) {
            return editor.getSingleEmptyItemSubstitution();
        }
        //
        // No substitution for the specified item 
        //
        return null;
    }
/*    static <T> T fromSubstitution(ObservableListPropertyEditor<T> editor, String item) {
        if (editor.getBoundList().isEmpty() && editor.getEmptyListSubstitution() != null && editor.getEmptyListSubstitution().equals(item)) {
            return editor.getEmptyListSubstitution();
        }

        if (item == null && editor.getNullSubstitution() != null && !editor.getBoundList().isEmpty()) {
            return editor.getNullSubstitution();
        }
        if (editor.getBoundList().size() == 1 && editor.getSingleEmptyItemSubstitution() != null && editor.getSingleEmptyItemSubstitution().equals(item)) {
            return editor.getSingleEmptyItemSubstitution();
        }
        //
        // No substitution for the specified item 
        //
        return null;
    }
  */  
    
}
