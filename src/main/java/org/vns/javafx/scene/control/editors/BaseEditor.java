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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import org.vns.javafx.scene.control.editors.skin.BaseEditorSkin;

/**
 *
 * @author Olga
 */
public abstract class BaseEditor<T> extends Control {

    private static final PseudoClass EDITABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("readonly");

    public static enum SidePos {
        LEFT,
        RIGHT,
        NO
    }
    private Button menuButton;
    private final StackPane valuePane;
    private StackPane externalValuePane;
    
    private final ObjectProperty<SidePos> menuButtonAllignment = new SimpleObjectProperty(SidePos.RIGHT);

    private String name;

    private HyperlinkTitle title;
    private Node editorNode;

    private ReadOnlyProperty boundProperty;
    private final BooleanProperty editable = new SimpleBooleanProperty(true);

    public BaseEditor() {
        this(null);
    }

    public BaseEditor(String name) {
        this.name = name;
        valuePane = new StackPane();
        valuePane.getStyleClass().add("value-pane");
        init();
    }

    private void init() {
        editorNode = createEditorNode();
        if (getName() != null) {
            setId(getName());
        }
        disableProperty().addListener((v, oldValue, newValue) -> {
            pseudoClassStateChanged(EDITABLE_PSEUDO_CLASS, newValue);
        });
        title = new HyperlinkTitle(this,getName() == null ? "" : getName() );
        editable.addListener(this::editableChangeListener);
    }

    public StackPane getValuePane() {
        return valuePane;
    }
    public void setExternalValuePane(StackPane pane) {
        externalValuePane = pane;
    }
    public StackPane getExternalValuePane() {
        return externalValuePane;
    }

    public ObjectProperty<SidePos> menuButtonAllignmentProperty() {
        return menuButtonAllignment;
    }

    public SidePos getMenuButtonAllignment() {
        return menuButtonAllignment.get();
    }

    public void setMenuButtonAllignment(SidePos pos) {
        menuButtonAllignment.set(pos);
    }

    protected void editableChangeListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        setDisable(!newValue);
    }

    protected ReadOnlyProperty getBoundProperty() {
        return boundProperty;
    }

    protected void setBoundProperty(ReadOnlyProperty boundProperty) {
        this.boundProperty = boundProperty;
    }

    public Node getEditorNode() {
        return editorNode;
    }

    protected abstract Node createEditorNode();

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    public String getName() {
        return name;
    }

    /*    @Override
    public void bind(ReadOnlyProperty<Boolean> property) {

        setEditable(false);

        this.setFocusTraversable(false);
        editorNode.selectedProperty().bind(property);
    }

    @Override
    public void bindBidirectional(Property<Boolean> property) {
        setEditable(true);
        this.setFocusTraversable(true);
        editorNode.selectedProperty().bindBidirectional(property);

    }
     */
    public HyperlinkTitle getTitle() {
        return title;
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    public boolean isEditable() {
        return editable.get();
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }
    public Button getMenuButton() {
        return menuButton;
    }

    public void setMenuButton(Button menuButton) {
        this.menuButton = menuButton;
    }
    
 
    @Override
    public Skin<?> createDefaultSkin() {
        return new BaseEditorSkin(this);
    }


}//BooleaPropertyEditor
