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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery
 */
@DefaultProperty("bounds")
public class BoundsPropertyEditor extends ContentComboBox implements PropertyEditor<Bounds> {

    private final ObjectProperty<Bounds> bounds = new SimpleObjectProperty<>(new BoundingBox(0, 0, 0, 0, 0, 0));

    public BoundsPropertyEditor() {
        getStyleClass().add("bounds-property-editor");
    }

    @Override
    public void bind(Property<Bounds> property) {
        setEditable(false);
        unbind();
        bounds.bind(property);
    }

    public void bind(ReadOnlyObjectProperty<Bounds> property) {
        setEditable(false);
        unbind();
        bounds.bind(property);
    }

    @Override
    public void bindBidirectional(Property<Bounds> property) {
        setEditable(false);
        unbind();
        bounds.bind(property);
    }

    @Override
    public void unbind() {
        bounds.unbind();
    }

    @Override
    public boolean isBound() {
        return bounds.isBound();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new BoundsPropertyEditorSkin(this);
    }

    public ObjectProperty<Bounds> boundsProperty() {
        return bounds;
    }

    public Bounds getBounds() {
        return bounds.get();
    }

    public void setBounds(Bounds bounds) {
        this.bounds.set(bounds);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

}
