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
 * See the License for the specific language governing permissions and * limitations under the License.
 */
package org.vns.javafx.scene.control.editors;

import org.vns.javafx.scene.control.editors.binding.EnumBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import org.vns.javafx.dock.api.ConditionalEventDispatcher;
import org.vns.javafx.dock.api.ConditionalEventDispatcher.RejectMouseReleasedDispatcher;

/**
 *
 * @author Olga
 */
//ChoiceBox<String>
public class EnumPropertyEditor<T extends Enum<T>> extends AbstractPropertyEditor<T> implements StaticConstraintPropertyEditor {
    
    boolean saveEditableOnBind;
    boolean saveNullableOnBind;
    
    public static final String NULL_VALUE = "INHERITED";

    private BooleanProperty nullable = new SimpleBooleanProperty();

    private EnumBinding binding;

    private static final PseudoClass EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("readonly");

    private T[] objects;

    private Class<T> enumType;

    private final ObjectProperty<T> enumValue = new SimpleObjectProperty<>();

    private final ChangeListener<Number> selectedIndexListener = ((v, ov, nv) -> {
        Integer newValue = (Integer) nv;
        if (newValue >= 0 && newValue < objects.length) {
            enumValue.set((T) objects[(Integer) nv]);
        } else if (isNullable()) {
            enumValue.set(null);
        }
    });
    private ChangeListener<T> enumValueListener = ((v, ov, nv) -> {
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getEditorNode();
        if (nv == null && isNullable()) {
            choiceBox.getSelectionModel().select(NULL_VALUE);
        } else {
            for (Object o : objects) {
                if (nv != null && o.equals(nv)) {
                    choiceBox.getSelectionModel().select(o.toString());
                    break;
                }
            }
        }
    });

    public EnumPropertyEditor(Class<T> enumType) {
        this(null, enumType);
    }

    public EnumPropertyEditor(String name, Class<T> enumType) {
        super(name);
        this.enumType = enumType;
        objects = enumType.getEnumConstants();
        init();
    }

    //private Predicate<T> filter = o -> {return true;};
    private Predicate<T> filter = null;

    public T[] filter() {
        T[] retval = null;
        if (filter == null) {
            retval = enumType.getEnumConstants();
        } else {
            List<T> list = new ArrayList<>();
            for (T t : enumType.getEnumConstants()) {
                if (filter.test(t)) {
                    list.add(t);
                }
            }
            retval = list.toArray(objects);
        }
        return retval;
    }

    public Predicate<T> getFilter() {
        return filter;
    }

    public void setFilter(Predicate<T> filter) {
        this.filter = filter;
    }

    ConditionalEventDispatcher preventEdit = new RejectMouseReleasedDispatcher(n -> {
        return true;
    });

    private void init() {
        getStyleClass().add("enum-field-editor");
        disableProperty().addListener((v, oldValue, newValue) -> {
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, newValue);
            setEditable(!newValue);
        });
        
        /*        editable.addListener((v, oldValue, newValue) -> {
            setDisable(!newValue);
        });
         */
        for (Object o : objects) {
            ((ChoiceBox<String>) getEditorNode()).getItems().add(o.toString());
        }
        if (isNullable()) {
            ((ChoiceBox<String>) getEditorNode()).getItems().add(NULL_VALUE);
        }
        setMaxWidth(1000);
        nullable.addListener((v,ov,nv) -> {
            ChoiceBox<String> choiceBox = (ChoiceBox<String>) getEditorNode();
            if ( nv ) {
                choiceBox.getItems().add(NULL_VALUE);
            } else {
                int sel = choiceBox.getSelectionModel().getSelectedIndex();
                if ( ! choiceBox.getSelectionModel().isSelected(choiceBox.getItems().indexOf(NULL_VALUE))) {
                    sel = -1;
                }
                choiceBox.getItems().remove(NULL_VALUE);
                if ( sel >= 0 ) {
                    enumValue.set(null);
                }
            }
            
        });
    }


    public BooleanProperty nullableProperty() {
        return nullable;
    }
    
    public boolean isNullable() {
        return nullable.get();
    }

    public void setNullable(boolean nullable) {
        this.nullable.set(nullable);
    }

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    public void bind(ReadOnlyProperty<T> property) {
        saveEditableOnBind = isEditable();
        saveNullableOnBind = isNullable();
        setNullable(false);
        enumValue.unbind();
        setBoundProperty(property);
        setEditable(false);
        enumValue.removeListener(enumValueListener);
        ChoiceBox<String> cb = (ChoiceBox<String>) getEditorNode();
        cb.selectionModelProperty().get().selectedIndexProperty().removeListener(selectedIndexListener);

        enumValue.addListener(enumValueListener);
        setFocusTraversable(false);

        cb.getSelectionModel().select(property.getValue().toString());
        enumValue.bind(property);
    }

    @Override
    public void bindBidirectional(Property<T> property) {
        saveEditableOnBind = isEditable();
        saveNullableOnBind = isNullable();
        
        setNullable(false);
        enumValue.unbind();
        setBoundProperty(property);
        setEditable(true);

        enumValue.removeListener(enumValueListener);

        ((ChoiceBox<String>) getEditorNode()).selectionModelProperty().get().selectedIndexProperty().removeListener(selectedIndexListener);

        ((ChoiceBox<String>) getEditorNode()).selectionModelProperty().get().selectedIndexProperty().addListener(selectedIndexListener);
        enumValue.addListener(enumValueListener);
        this.setFocusTraversable(true);
//        getSelectionModel().select(property.getValue().toString());
        enumValue.bindBidirectional(property);
    }
    @Override
    public void bindConstraint(Parent node, Method... setMethods) {
        saveEditableOnBind = isEditable();
        saveNullableOnBind = isNullable();        
        setNullable(true);
        enumValue.unbind();
        setBoundProperty(null);
        setEditable(true);

        enumValue.removeListener(enumValueListener);

        ((ChoiceBox<String>) getEditorNode()).selectionModelProperty().get().selectedIndexProperty().removeListener(selectedIndexListener);

        ((ChoiceBox<String>) getEditorNode()).selectionModelProperty().get().selectedIndexProperty().addListener(selectedIndexListener);
        enumValue.addListener(enumValueListener);
        this.setFocusTraversable(true);

        //cb.getSelectionModel().select(property.getValue().toString());
        EnumBinding binding = new EnumBinding(getName(), node, enumValue, enumType);
        try {
            
            String getname = "get" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            Method m  = node.getParent().getClass().getMethod(getname, new Class[]{Node.class});
            if (m != null) {
                T value = (T) m.invoke(node.getParent(), new Object[]{node});
                ChoiceBox<String> choiceBox = (ChoiceBox<String>) getEditorNode();
                String item = value == null ? NULL_VALUE : choiceBox.getItems().get(choiceBox.getItems().indexOf(value.toString()));
                choiceBox.getSelectionModel().select(item);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
        
        this.binding = binding;
        binding.bind();

    }

    @Override
    public boolean isBound() {
        return enumValue.isBound() || getBoundProperty() != null || (binding != null && binding.isBound());
    }

    @Override
    public void unbind() {
        setEditable(saveEditableOnBind);
        setNullable(saveNullableOnBind);
        
        enumValue.unbind();
        if (getBoundProperty() != null && (getBoundProperty() instanceof Property)) {
            enumValue.unbindBidirectional((Property) getBoundProperty());
        }
        setBoundProperty(null);
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    protected Node createEditorNode() {
        return new ChoiceBox<>();
    }


}
