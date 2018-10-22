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

import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import javax.swing.plaf.metal.MetalBorders;

/**
 *
 * @author Valery Shyshkin
 */
public class EffectChildPropertyEditor extends AbstractPropertyEditor {

    private static final PseudoClass NOTNULL_PSEUDO_CLASS = PseudoClass.getPseudoClass("notnull");

    private ButtonBase updateButton;
    private ButtonBase textButton;

//    private final ObservableMap<ButtonBase, PropertyEditorPane> paneEditors = FXCollections.observableHashMap();
    //
    // !!! FOR TEST PURPOSE ONLY
    //
    public EffectChildPropertyEditor() {
        this(null);
    }

    public EffectChildPropertyEditor(String name) {
        this(name, null);
    }

    public EffectChildPropertyEditor(String name, TreePaneItem parentTreeItem) {
        super(name);

        //updateButton = new ToggleButton();
        //updateButton.getStyleClass().add("update-button");
        init();
        createTreeItem(parentTreeItem);
    }

    private void init() {
        getStyleClass().addAll("composite-editor", "text-button");
        ((Labeled) getTextButton()).textProperty().addListener((v, oldValue, newValue) -> {
            pseudoClassStateChanged(NOTNULL_PSEUDO_CLASS, newValue != null && !newValue.isEmpty());
        });
    }

/*    private ButtonBase createUpdateButton() {
        ButtonBase updateButton = new ToggleButton();
        updateButton.getStyleClass().add("update-button");
        updateButton.textProperty().addListener((v, oldValue, newValue) -> {
            pseudoClassStateChanged(NOTNULL_PSEUDO_CLASS, newValue != null && !newValue.isEmpty());
        });

        return updateButton;
    }
*/
    public static ButtonBase createUpdateButton(Node node, ButtonBase txtButton) {
        ButtonBase updateButton = new ToggleButton();
        updateButton.getStyleClass().add("update-button");
        txtButton.textProperty().addListener((v, oldValue, newValue) -> {
            node.pseudoClassStateChanged(NOTNULL_PSEUDO_CLASS, newValue != null && !newValue.isEmpty());
        });

        return updateButton;
    }

    private TreePaneItem createTreeItem(TreePaneItem parentItem) {
        TreePaneItem item = new TreePaneItem(getName());
        return item;

    }

    public ButtonBase getTextButton() {
        return textButton;
    }

    public ButtonBase getUpdateButton() {
        return updateButton;
    }

    @Override
    protected Node createEditorNode() {
        GridPane grid = new GridPane();
        textButton = new ToggleButton();
        updateButton = createUpdateButton(this,textButton);
        updateButton.setOnAction(e -> {

            ContextMenu menu = createContextMenu(textButton);
            updateButton.setContextMenu(menu);

            Bounds b = updateButton.localToScreen(updateButton.getBoundsInLocal());
            menu.show(updateButton, b.getMinX(), b.getMinY() + b.getHeight() + 2);
        });
        textButton.setMaxWidth(1000);
        textButton.setAlignment(Pos.BASELINE_LEFT);
        grid.add(textButton, 0, 0);
        GridPane.setHgrow(textButton, Priority.ALWAYS);
        grid.add(updateButton, 1, 0);

        return grid;
    }

    public static ContextMenu createContextMenu(ButtonBase txtButton) {
        String[] strItems;
        if (txtButton.getText() != null && !txtButton.getText().isEmpty()) {
            strItems = new String[]{"Reset"};
        } else {
            strItems = new String[]{"Blend", "Bloom", "BoxBlur",
                "ColorAdjust", "ColorInput", "DisplacementMap", "DropShadow", "GaussianBlur", "Glow",
                "ImageInput", "InnerShadow", "Lighting", "MotionBlur",
                "PerspectiveTransform", "Reflection", "SepiaTone", "Shadow"};
        }
        ContextMenu menu = new ContextMenu();
        for (String it : strItems) {
            MenuItem mi = new MenuItem(it);
            menu.getItems().add(mi);
            mi.setOnAction(e -> {
                if ("Reset".equals(mi.getText())) {
                    txtButton.setText(null);
                } else {
                    txtButton.setText(mi.getText());
                }
            });
        }
        return menu;
    }

    @Override
    public void bind(ReadOnlyProperty property) {
         unbind();
        //setEditable(true);
        setBoundProperty(property);
        setEditable(false);
        property.addListener(boundPropertyChangeListener);
    }
    protected ChangeListener boundPropertyChangeListener = (ObservableValue observable, Object oldValue, Object newValue) -> {
            if ( newValue != null ) {
                getTextButton().setText(newValue.getClass().getSimpleName());
            } else {
                getTextButton().setText(null);
            }
        
    };
    
    @Override
    public void bindBidirectional(Property property) {
        unbind();
        setEditable(true);
        setBoundProperty(property);

        getTextButton().textProperty().bindBidirectional(property, new EffectStringConverter());

    }

    @Override
    public void unbind() {
        
        getTextButton().textProperty().unbind();
        if ( getBoundProperty() != null && (getBoundProperty() instanceof Property) ) {
            getTextButton().textProperty().unbindBidirectional(getBoundProperty());
            getBoundProperty().removeListener(boundPropertyChangeListener);
        }
        setBoundProperty(null);
    }

    @Override
    public boolean isBound() {
        return getTextButton().textProperty().isBound() || getBoundProperty() != null;
    }

    public static class EffectStringConverter extends StringConverter<Effect> {

        @Override
        public String toString(Effect effect) {
            if (effect == null) {
                return null;
            }
            return effect.getClass().getSimpleName();
        }

        @Override
        public Effect fromString(String string) {
            if (string == null || string.trim().isEmpty()) {
                return null;
            }
            Effect effect = null;
            switch (string) {
                case "Blend":
                    effect = new Blend();
                    break;
                case "Bloom":
                    effect = new Bloom();
                    break;

                case "BoxBlur":
                    effect = new BoxBlur();
                    break;

                case "ColorAdjust":
                    effect = new ColorAdjust();
                    break;

                case "ColorInput":
                    effect = new ColorInput();
                    break;

                case "DisplacementMap":
                    effect = new DisplacementMap();
                    break;

                case "DropShadow":
                    effect = new DropShadow();
                    break;

                case "GaussianBlur":
                    effect = new GaussianBlur();
                    break;

                case "Glow":
                    effect = new Glow();
                    break;

                case "ImageInput":
                    effect = new ImageInput();
                    break;

                case "InnerShadow":
                    effect = new InnerShadow();
                    break;

                case "Lighting":
                    effect = new Lighting();
                    break;

                case "MotionBlur":
                    effect = new MotionBlur();
                    break;

                case "PerspectiveTransform":
                    effect = new PerspectiveTransform();
                    break;

                case "Reflection":
                    effect = new Reflection();
                    break;

                case "SepiaTone":
                    effect = new SepiaTone();
                    break;

                case "Shadow":
                    effect = new Shadow();
                    break;

            }//switch
            return effect;
        }
    }
}//Class
