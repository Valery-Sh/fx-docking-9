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

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.effect.Effect;

/**
 *
 * @author Nastia
 */
public class EffectTreePane<E> extends TreePane<E> implements PropertyEditor<Effect> {
    
    private ButtonBase updateButton;

    public EffectTreePane() {
        this(null);
    }
    public EffectTreePane(String name) {
        super(name);
        init();
    }
    private void init(){
        getStyleClass().add("composite-editor");
        updateButton = EffectChildPropertyEditor.createUpdateButton(this,getTextButton());
        updateButton.setOnAction(e -> {

            ContextMenu menu = EffectChildPropertyEditor.createContextMenu(getTextButton());
            updateButton.setContextMenu(menu);

            Bounds b = updateButton.localToScreen(updateButton.getBoundsInLocal());
            menu.show(updateButton, b.getMinX(), b.getMinY() + b.getHeight() + 2);
        });
   
        getButtons().add(updateButton);
    }
    @Override
    public ReadOnlyProperty getBoundProperty() {
        return super.getBoundProperty();
    }

    @Override
    public void bind(ReadOnlyProperty<Effect> property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bindBidirectional(Property<Effect> property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unbind() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
