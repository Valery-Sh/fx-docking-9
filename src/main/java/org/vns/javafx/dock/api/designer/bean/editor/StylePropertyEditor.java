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

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import org.vns.javafx.dock.api.designer.bean.editor.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Valery
 */
public class StylePropertyEditor extends StringPropertyEditor {

    public StylePropertyEditor() {
        this(null);
    }

    public StylePropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getStyleClass().add("style-class-text-field");
        getTextField().setSeparator(";");
    }

    @Override
    protected void addValidators() {
        getTextField().getValidators().add(item -> {
            //if ( item.ends)
            if ( item.endsWith("?") ) {
                return false;
            }
            return true;
            //return Pattern.matches("", item.trim());
        });

    }

    @Override
    protected void addFilterValidators() {
        getTextField().getFilterValidators().add(item -> {

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
            String orCond9 = "|^((v))|((v)(i))|((v)(i)(s))|((v)(i)(s)(i))|((v)(i)(s)(i)(b))|((v)(i)(s)(i)(b)(i))|((v)(i)(s)(i)(b)(i)(l))|((v)(i)(s)(i)(b)(i)(l)(i))|((v)(i)(s)(i)(b)(i)(l)(i)(t))|((v)(i)(s)(i)(b)(i)(l)(i)(t)(y))";
            String orCond10 = orCond9 + "(\\s*):$";
            String orCond11 = orCond9 + "(\\s*):(\\s*).+$";

            regExp += orCond0 + orCond1 + orCond2 + orCond3 + orCond4 + orCond5 + orCond6 + orCond7 + orCond8 + orCond9 + orCond10 + orCond11;
            boolean retval = item.trim().isEmpty();
            if (!retval) {
                retval = Pattern.matches(regExp, item.trim());
            }

            return retval;
        });

    }

    @Override
    protected void createContextMenu(ReadOnlyProperty property) {
        if (property.getBean() instanceof Styleable) {
            getTextField().setContextMenu(createStyleMenu((Styleable) property.getBean()));
        }
    }

    private ContextMenu createStyleMenu(Styleable bean) {

        SmallContextMenu cm = new SmallContextMenu();
        cm.setContentHeight(400d);

        List<CssMetaData<? extends Styleable, ?>> cssList = bean.getCssMetaData();

        for (CssMetaData<? extends Styleable, ?> md : cssList) {
            MenuItem item = new MenuItem(md.getProperty());
            item.setOnAction( a -> {
                String s = getTextField().getText().isEmpty() ? "" : "; ";
                        
                if ( ! getTextField().getText().contains(item.getText())) {
                    getTextField().setText(getTextField().getText() + s + item.getText() + ":???"); 
                }
            });
            cm.getItems().add(item);
            cm.getItems().sort(new Comparator<MenuItem>() {
                @Override
                public int compare(MenuItem o1, MenuItem o2) {
                    if (o1.equals(o2)) {
                        return 0;
                    }
                    return (o1.getText().compareTo(o2.getText()));
                }
            });
        }
        
        return cm;
    }

}
