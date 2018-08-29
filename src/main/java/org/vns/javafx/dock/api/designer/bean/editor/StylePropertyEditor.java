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

import java.util.regex.Pattern;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Valery
 */
public class StylePropertyEditor extends PrimitivePropertyEditor<String> {

    public StylePropertyEditor() {
        init();
    }
    private void init() {
        setSeparator(";");
    }
    @Override
    protected void addValidators() {
        getValidators().add(item -> {
            return Pattern.matches("^(([-]+)([f]+)([x]+)([-]+))([a-z][a-z0-9]*)(-[a-z0-9]+)*(\\s*):(\\s*).+$", item.trim());
        });
    }

    @Override
    protected void addFilterValidators() {
        getFilterValidators().add(item -> {

            String regExp = "^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*$";
            String orCond0 = "|^((-))";
            String orCond1 = "|^((-)(f))";
            String orCond2 = "|^((-)(f)(x))";
            String orCond3 = "|^((-)(f)(x)(-))([a-z])?$";
            String orCond4 = "|^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*(-)$";

            String orCond5 = "|^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*(\\s*)$";
            String orCond6 = "|^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*(\\s*):$";
            String orCond7 = "|^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*(\\s*):(\\s*)$";
            String orCond8 = "|^((-)(f)(x)(-))([a-z][a-z0-9]*)(-[a-z0-9]+)*(\\s*):(\\s*).+$";

            regExp += orCond0 + orCond1 + orCond2 + orCond3 + orCond4 + orCond5 + orCond6 + orCond7 + orCond8;
//            System.err.println("REG EXP = " + regExp);
            boolean retval = item.trim().isEmpty();
            if (!retval) {
                retval = Pattern.matches(regExp, item.trim());
            }

            return retval;
        });
    }

    @Override
    public void setBoundValue(String boundValue ) {
        ((StringProperty) boundValueProperty()).set(boundValue);
    }

}//class DoublePropertyEditor
