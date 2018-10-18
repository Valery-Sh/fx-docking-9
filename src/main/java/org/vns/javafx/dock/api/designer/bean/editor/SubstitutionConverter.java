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
        ObservableListEditor editorNode = (ObservableListEditor) getEditor().getTextField();        
        if (editorNode.getEmptySubstitution() != null && editorNode.getEmptySubstitution().equals(item)) {        
            return true;
        } else {
            return false;
        }       
    }
    default boolean isNullSubstitution(String item) {
        ObservableListEditor editorNode = (ObservableListEditor) getEditor().getTextField();
        if (editorNode.getNullSubstitution() != null && editorNode.getNullSubstitution().equals(item)) {
            return true;
        } else {
            return false;
        }       
    }    
    default boolean isSingleEmptyItemSubstitution(String item) {
        ObservableListEditor editorNode = (ObservableListEditor) getEditor().getTextField();
        //if (getEditor().getBoundList().size() == 1 && getEditor().getSingleEmptyItemSubstitution() != null && getEditor().getSingleEmptyItemSubstitution().equals(item)) {
        if (editorNode.getSingleEmptyItemSubstitution() != null && editorNode.getSingleEmptyItemSubstitution().equals(item)) {            
            return true;
        } else {
            return false;
        }       
    }

    static <T> Substitution getSubstitution(ObservableListPropertyEditor<T> editor, String item) {
        ObservableListEditor editorNode = (ObservableListEditor) editor.getTextField();
        if (editor.getBoundList().isEmpty() && editorNode.getEmptySubstitution() != null && editorNode.getEmptySubstitution().equals(item)) {
            return Substitution.EMPTY_LIST;
        }
        if (editor.getBoundList().size() == 1 && editorNode.getSingleEmptyItemSubstitution() != null && editorNode.getSingleEmptyItemSubstitution().equals(item)) {
            return Substitution.EMPTY_SINGLE_ITEM;
        }
        if (editorNode.getNullSubstitution() != null && editorNode.getNullSubstitution().equals(item)) {
            return Substitution.NULL;
        }
        return null;
    }

    static <T> String toSubstitution(ObservableListPropertyEditor<T> editor, T item) {
        ObservableListEditor editorNode = (ObservableListEditor) editor.getTextField();
        if (editor.getBoundList().isEmpty() && editorNode.getEmptySubstitution() != null && editor.getBoundList().isEmpty()) {
            return editorNode.getEmptySubstitution();
        }

        if (item == null && editorNode.getNullSubstitution() != null && !editor.getBoundList().isEmpty()) {
            return editorNode.getNullSubstitution();
        }
        if (editor.getBoundList().size() == 1 && editorNode.getSingleEmptyItemSubstitution() != null && "".equals(item)) {            
            return editorNode.getSingleEmptyItemSubstitution();
        }
        //
        // No substitution for the specified item 
        //
        return null;
    }

    
}
