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

import org.vns.javafx.scene.control.editors.skin.BoundsPropertyEditorSkin;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.vns.javafx.scene.control.ContentComboBox;

/**
 *
 * @author Valery
 */
@DefaultProperty("bounds")
public class BoundsPropertyEditor extends AbstractPropertyEditor<Bounds> {

    private final ObjectProperty<Bounds> bounds = new SimpleObjectProperty<>(new BoundingBox(0, 0, 0, 0, 0, 0));

    public BoundsPropertyEditor() {
       this(null);
    }
    public BoundsPropertyEditor(String name) {
        super(name);
        getStyleClass().add("bounds-property-editor");
    }

    
    @Override
    public void bind(ReadOnlyProperty<Bounds> property) {
        unbind();
        setBoundProperty(property);
        bounds.bind(property);
    }


    @Override
    public void bindBidirectional(Property<Bounds> property) {
        unbind();
        setBoundProperty(property);
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
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    protected Node createEditorNode() {
        return new ContentComboBox();
    }

}
