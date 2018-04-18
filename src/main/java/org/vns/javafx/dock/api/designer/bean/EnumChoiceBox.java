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
package org.vns.javafx.dock.api.designer.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.ChoiceBox;
import org.vns.javafx.dock.api.ConditionalEventDispatcher;
import org.vns.javafx.dock.api.ConditionalEventDispatcher.RejectMouseReleasedDispatcher;

/**
 *
 * @author Olga
 */
public class EnumChoiceBox<T extends Enum<T>> extends ChoiceBox<String> implements PropertyEditor<T> {
    
    private static final PseudoClass EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("readonly");

    private final BooleanProperty editable = new SimpleBooleanProperty(true);
    //private final EventDispatcher eventDispatcher;
    private T[] objects;

    private final ObjectProperty<T> enumValue = new SimpleObjectProperty<>();
    private final ChangeListener<Number> selectedIndexListener = ((v, ov, nv) -> {
        if ((Integer) nv >= 0) {
            enumValue.set((T) objects[(Integer) nv]);
        }
    });
    private ChangeListener<T> enumValueListener = ((v, ov, nv) -> {
        System.err.println("enumValue = " + enumValue);
        for (Object o : objects) {
            if (nv != null && o.equals(nv)) {
                getSelectionModel().select(o.toString());
                break;
            }
        }
    });

    public EnumChoiceBox(Class<T> enumType) {
        objects = enumType.getEnumConstants();
        //eventDispatcher = getEventDispatcher();
        //this.enumObj = T.valueOf(enumType, name);

        init();
    }

    ConditionalEventDispatcher preventEdit = new RejectMouseReleasedDispatcher(n -> {
        return true;
    });

    private void init() {
        //disableProperty().bindBidirectional();
        getStyleClass().add("enum-field-editor");
        disableProperty().addListener((v, oldValue, newValue) -> {
            System.err.println("disableProperty changed: " + newValue);
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, newValue);
            setEditable(!newValue);
        });
        editable.addListener((v, oldValue, newValue) -> {
             setDisable( !newValue);
        });

 /*       editable.addListener((v, ov, nv) -> {
            if (!nv) {
                preventEdit.start(this);
                setEventDispatcher(preventEdit);
            } else {
                //setEventDispatcher(eventDispatcher);
                preventEdit.finish(this);
            }
        });
*/
        for (Object o : objects) {
            getItems().add(o.toString());
        }
    }

    @Override
    public void bind(Property<T> property) {

        enumValue.unbind();
        setEditable(false); 
        enumValue.removeListener(enumValueListener);
        selectionModelProperty().get().selectedIndexProperty().removeListener(selectedIndexListener);

        enumValue.addListener(enumValueListener);
        setFocusTraversable(false);

        getSelectionModel().select(property.getValue().toString());
        enumValue.bind(property);
    }

    @Override
    public void bindBidirectional(Property<T> property) {
        enumValue.unbind();
        setEditable(true); 
        
        enumValue.removeListener(enumValueListener);

        selectionModelProperty().get().selectedIndexProperty().removeListener(selectedIndexListener);

        selectionModelProperty().get().selectedIndexProperty().addListener(selectedIndexListener);
        enumValue.addListener(enumValueListener);
        this.setFocusTraversable(true);
        getSelectionModel().select(property.getValue().toString());
        enumValue.bindBidirectional(property);
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    @Override
    public boolean isEditable() {
        return editable.get();
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }


    @Override
    public boolean isBound() {
        return enumValue.isBound();
    }
    public void unbind() {
        enumValue.unbind();
    }
}
