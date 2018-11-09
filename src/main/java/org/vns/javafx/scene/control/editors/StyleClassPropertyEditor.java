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
package org.vns.javafx.scene.control.editors;

import java.util.regex.Pattern;

/**
 *
 * @author Valery
 */
public class StyleClassPropertyEditor extends StringListPropertyEditor {

    public StyleClassPropertyEditor() {
        this(null);
    }
    public StyleClassPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getTextField().setSeparator(",");
        getTextField().setEmptySubstitution("[]");
        getTextField().setNullSubstitution("<NULL>");
        getTextField().setSingleEmptyItemSubstitution("[\"\"]");
        getTextField().setNullable(true);
        
        getTextField().getFilterValidators().add(item -> {
            String regExp = "^([a-zA-Z][a-zA-Z0-9]*)(-[a-zA-Z0-9]+)*$";
            boolean retval = item.trim().isEmpty();
            if (!retval) {
                retval = Pattern.matches(regExp, item.trim());
            }

            return retval;
        });
      
    }

    @Override
    protected void addValidators() {
         getTextField().getValidators().add(item -> {
            if ( item.trim().isEmpty() ) {
                return true;
            }
            return Pattern.matches("^([a-zA-Z][a-zA-Z0-9]*)(-[a-zA-Z0-9]+)*$", item.trim());
        });     
    }

    @Override
    protected void addFilterValidators() {
        getTextField().getFilterValidators().add(item -> {
            String regExp = "^([a-zA-Z][a-zA-Z0-9]*)(-[a-zA-Z0-9]+)*$";
            boolean retval = item.trim().isEmpty();
            if (!retval) {
                retval = Pattern.matches(regExp, item.trim());
            }

            return retval;
        });
    }

}//class StyleClassPropertyEditor
