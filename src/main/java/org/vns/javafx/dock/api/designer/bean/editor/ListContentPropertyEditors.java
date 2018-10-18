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

import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.IntegerPropertyEditor;

/**
 *
 * @author Valery
 */
public abstract class ListContentPropertyEditors {
    
    public static ObservableListPropertyEditor getEditor(Class<?> itemClass) {
        ObservableListPropertyEditor retval = new ObservableListPropertyEditor();
        if ( itemClass.equals(String.class)) {
            retval = new ObservableListPropertyEditor<String>();
            retval.setStringConverter(new ObservableListItemStringConverter(retval,itemClass));  
            retval.getTextField().setEmptySubstitution("<EMPTY>");
            retval.getTextField().setNullSubstitution("<NULL>");
            
        } else  if ( itemClass.equals(Integer.class)) {
            retval = new ObservableListPropertyEditor<Integer>();
            retval.setStringConverter(new ObservableListItemStringConverter(retval,itemClass));            
            IntegerPropertyEditor ipe = new PrimitivePropertyEditor.IntegerPropertyEditor();
            retval.getTextField().getFilterValidators().addAll(ipe.getFilterValidators());
            retval.getTextField().getValidators().addAll(ipe.getValidators());
        }
        return retval;
    }
}
