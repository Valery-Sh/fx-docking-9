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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import org.vns.javafx.scene.control.editors.PrimitivePropertyEditor.StringPropertyEditor;

/**
 *
 * @author Nastia
 */
public class GridRowColumnSpanPropertyEditor extends StringPropertyEditor {
    public static final String REMAINING = "REMAINING";
    public static final String NULL = "NULL";
    public GridRowColumnSpanPropertyEditor() {
        this(null);
    }

    public GridRowColumnSpanPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getTextField().setNullable(true);
        getTextField().setNullSubstitution(NULL);
        
        getTextField().focusedProperty().addListener((v, ov,nv) -> {
            if ( nv && NULL.equals(getTextField().getText()) ) {
                getTextField().selectAll();
            } else if ( ! nv && "".equals(getTextField().getText()) ) {
                getTextField().setText(NULL);
            }
        });
        Button btn = new Button();
        btn.setGraphic(Util.createDownTriangle());
        getButtons().add(btn);
        MenuItem item1 = new MenuItem(REMAINING);
        MenuItem item2 = new MenuItem(NULL);        
        ContextMenu menu = new ContextMenu(item1,item2);
        item1.setOnAction(a -> {
            getTextField().setText(item1.getText());
        });
        item2.setOnAction(a -> {
            getTextField().setText(item2.getText());
        });
        btn.setContextMenu(menu);

        btn.setOnAction(a -> {
            Bounds bounds = btn.localToScreen(btn.getLayoutBounds());
            menu.show(btn, bounds.getMinX(), bounds.getMinY() + bounds.getHeight());
        });
    }

    @Override
    public void bindConstraint(Parent node, Method... setMethods) {
        unbind();
        setEditable(true);
        ObjectProperty<String> property = new SimpleObjectProperty<>();
        setBoundProperty(property);
        try {

            String getname = "get" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m = node.getParent().getClass().getMethod(getname, new Class[]{Node.class});
            if (m != null) {
                Object value = m.invoke(node.getParent(), new Object[]{node});
                if (value == null) {
                    property.setValue(null);
                } else {
                    Integer intValue = (Integer) value;
                    if ( (Integer)value == GridPane.REMAINING) {
                        property.setValue(REMAINING);
                    } else {
                        property.setValue(String.valueOf((Integer) value));
                    }
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }

        property.addListener((v, ov, nv) -> {
            setConstraint(node, nv);
        });
        getTextField().textProperty().bindBidirectional(property, getStringConverter());
        //btn.setOnAction(a -> { menu.show(btn, btn.getLayoutX(),btn.getLayoutY() );
        //createContextMenu(property);
    }

    @Override
    protected void setConstraint(Parent node, String value) {
        try {

            Integer intValue = null;
            if (REMAINING.equals(value)) {
                intValue = GridPane.REMAINING;
            } else if (value != null && !value.isEmpty()) {
                intValue = Integer.parseInt(value);
            } else {
                intValue = null;
            }

            String setname = "set" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m = node.getParent().getClass().getMethod(setname, new Class[]{Node.class, Integer.class});
            if (m != null) {
                m.invoke(node.getParent(), new Object[]{node, intValue});
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }

    }
        @Override
        protected void addValidators() {
            getTextField().getValidators().add(item -> {
                if ( item.isEmpty() || REMAINING.equals(item) || NULL.equals(item)) {
                    return true;
                }
                return Pattern.matches("0|-?([1-9][0-9]*)+", item.trim());
            });

        }

        @Override
        protected void addFilterValidators() {
            getTextField().getFilterValidators().add(item -> {
                //item = item.trim();
                
                boolean retval = item.isEmpty();
                if ( ! retval ) {
                    retval = Util.startsWith(REMAINING, item);
                }
                
                if (!retval) {
                    String regExp = "-?([1-9][0-9]*)?";
                    retval = Pattern.matches(regExp, item);
                    if (retval && !item.equals("-")) {
                        long l = Long.valueOf(item.trim());
                        retval = (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE);
                    }
                }
                return retval;
            });
        }

}
