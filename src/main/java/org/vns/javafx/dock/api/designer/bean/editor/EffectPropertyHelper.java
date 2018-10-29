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

import java.util.List;
import java.util.function.BiConsumer;
import javafx.beans.property.Property;
import javafx.geometry.Bounds;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.effect.Shadow;
import javafx.util.StringConverter;
import org.vns.javafx.dock.api.designer.bean.PropertyPaneModelRegistry;
import static org.vns.javafx.dock.api.designer.bean.editor.TreePaneItem.NOTNULL_PSEUDO_CLASS;

/**
 *
 * @author Nastia
 */
public class EffectPropertyHelper {

    public static TreePaneItem createTreePaneItem(String name) {
        TreePaneItem item = new TreePaneItem(name);
        initialize(item);
        return item;
    }

    public static TreePane createTreePane(String name) {
        TreePane root = new TreePane(name);
        initialize(root);
        return root;
    }

    public static StringConverter setStringConverter(TreePaneItem targetItem) {
        StringConverter c = new EffectStringConverter();
        targetItem.setStringConverter(c);
        return c;
    }

    public static Labeled setUpdateButton(TreePaneItem targetItem) {
        ButtonBase updateButton = new ToggleButton();
        updateButton.getStyleClass().add("update-button");
        targetItem.getTextButton().textProperty().addListener((v, oldValue, newValue) -> {
            targetItem.pseudoClassStateChanged(NOTNULL_PSEUDO_CLASS, newValue != null && !newValue.isEmpty());
        });
        targetItem.getButtons().add(updateButton);
        return updateButton;

    }

    public static ContextMenu setContextMenu(TreePaneItem targetItem) {
        Labeled txtButton = targetItem.getTextButton();
        ContextMenu menu = new ContextMenu();
        System.err.println("txtButton text = '" + txtButton.getText() + "'");
        if (txtButton.getText() != null && !txtButton.getText().isEmpty()) {
            menu.getItems().addAll(createResetMenuItems(targetItem));
        } else {
            menu.getItems().addAll(createChoiceMenuItems(targetItem));
        }
        /*        String[] strItems;
        Labeled txtButton = targetItem.getTextButton();
        if (txtButton.getText() != null && !txtButton.getText().isEmpty()) {
            strItems = new String[]{"Reset", "Replace"};

        } else {
            strItems = new String[]{"Blend", "Bloom", "BoxBlur",
                "ColorAdjust", "ColorInput", "DisplacementMap", "DropShadow", "GaussianBlur", "Glow",
                "ImageInput", "InnerShadow", "Lighting", "MotionBlur",
                "PerspectiveTransform", "Reflection", "SepiaTone", "Shadow"};
        }
        ContextMenu menu = new ContextMenu();
        for (String it : strItems) {
            MenuItem mi;// = new MenuItem(it);
            if ("Replace".equals(it)) {
                mi = new Menu(it);
            } else {
                mi = new MenuItem(it);
            }
            menu.getItems().add(mi);

            mi.setOnAction(e -> {
                if ("Reset".equals(mi.getText())) {
                    txtButton.setText(null);
                } else if ("Replace".equals(mi.getText())) {

                } else {
                    txtButton.setText(mi.getText());
                }
            });
        }
         */
        return menu;
    }

    public static MenuItem[] createChoiceMenuItems(TreePaneItem treeItem) {
        String[] strItems = new String[]{"Blend", "Bloom", "BoxBlur",
            "ColorAdjust", "ColorInput", "DisplacementMap", "DropShadow", "GaussianBlur", "Glow",
            "ImageInput", "InnerShadow", "Lighting", "MotionBlur",
            "PerspectiveTransform", "Reflection", "SepiaTone", "Shadow"};
        MenuItem[] retval = new MenuItem[strItems.length];
        for (int i = 0; i < strItems.length; i++) {
            MenuItem mi = new MenuItem(strItems[i]);
            retval[i] = mi;
            mi.setOnAction(e -> {
                treeItem.getTextButton().setText(mi.getText());
            });
        }
        return retval;
    }

    public static MenuItem[] createResetMenuItems(TreePaneItem treeItem) {
        String[] strItems = new String[]{"Reset", "Replace"};
        MenuItem[] retval = new MenuItem[strItems.length + 1];

        MenuItem mi = null;// = new MenuItem(strItems[i]);

        // if ("Reset".equals(strItems[i])) {
        mi = new MenuItem(strItems[0]);
        mi.setOnAction(e -> {
            treeItem.getTextButton().setText(null);
        });
        retval[0] = mi;
        mi = new SeparatorMenuItem();
        retval[1] = mi;
        // if ("Replace".equals(strItems[i])) {
        mi = new Menu("Replace");
        ((Menu) mi).getItems().addAll(createChoiceMenuItems(treeItem));
        //}
        retval[2] = mi;

        return retval;

    }

    public static void initialize(TreePaneItem item) {
        setStringConverter(item);

        ButtonBase updateButton = (ButtonBase) setUpdateButton(item);
        updateButton.setOnAction(e -> {
            ContextMenu menu = setContextMenu(item);
            updateButton.setContextMenu(menu);
            Bounds b = updateButton.localToScreen(updateButton.getBoundsInLocal());
            menu.show(updateButton, b.getMinX(), b.getMinY() + b.getHeight() + 2);
        });
        item.setReplaceBoundPropertyHandler((oldValue, newValue) -> {
            if (oldValue == null || newValue == null) {
                return;
            }
            PropertyPaneModelRegistry.Introspection newIntr = PropertyPaneModelRegistry.introspect(newValue.getClass());
            List<Property> newPropList = newIntr.findProperties(newValue, Effect.class);
            if (newPropList.isEmpty()) {
                return;
            }
            PropertyPaneModelRegistry.Introspection oldIntr = PropertyPaneModelRegistry.introspect(oldValue.getClass());
            List<Property> oldPropList = oldIntr.findProperties(oldValue, Effect.class);
            if (oldPropList.isEmpty()) {
                return;
            }
            for (Property newProp : newPropList) {
                if (oldPropList.isEmpty()) {
                    break;
                }
                Property oldProp = null;
                //
                // Find first with a not null value
                //
                for (Property p : oldPropList) {
                    if (p.getValue() != null) {
                        oldProp = p;
                        break;
                    }
                }

                if (newProp.getValue() != null || oldProp == null) {
                    continue;
                }
                newProp.setValue(oldProp.getValue());
                oldPropList.remove(oldProp);
            }//for
        });

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
    }//EffectStringConverter

}//class 
