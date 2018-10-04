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

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Popup;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
public class ComboButton extends Control {

    private final ObjectProperty<Button> button = new SimpleObjectProperty<>();
    private final ObjectProperty<Parent> popupRoot = new SimpleObjectProperty<>();

    public ComboButton() {
        init();
    }

    private void init() {
        getStyleClass().add("combo-button");
    }

    public ObjectProperty<Button> buttonProperty() {
        return button;
    }

    public Button getButton() {
        return button.get();
    }

    public void setButton(Button button) {
        this.button.set(button);
    }

    public ObjectProperty<Parent> popupRootProperty() {
        return popupRoot;
    }

    public Parent getPopupRoot() {
        return popupRoot.get();
    }

    public void setPopupRoot(Parent popupRoot) {
        this.popupRoot.set(popupRoot);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new ComboButtonSkin(this);
    }

    public static void setDefaultButtonGraphic(Button btn) {
        Polygon polygon = (Polygon) createTriangle();
        btn.setGraphic(polygon);
    }

    public static void setDefaultLayout(Button btn) {
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setLeftAnchor(btn, 0d);
        AnchorPane.setRightAnchor(btn, 0d);
    }

    public static Polygon createTriangle() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            0.0, 0.0,
            7.0, 0.0,
            3.5, 4.0});
        return polygon;
    }

    public static class ComboButtonSkin extends SkinBase<ComboButton> {

        private AnchorPane anchor = null;
        private Popup popup;

        public ComboButtonSkin(ComboButton control) {
            super(control);

            anchor = new AnchorPane();

            Button btn = control.getButton();
            if (btn == null) {
                btn = new Button();
                ComboButton.setDefaultLayout(btn);
                ComboButton.setDefaultButtonGraphic(btn);
                
                AnchorPane.setLeftAnchor(btn, 0d);
                AnchorPane.setRightAnchor(btn, 0d);
            }

            anchor.getChildren().add(btn);

            popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            
            Parent root = control.getPopupRoot();
            if (root != null) {
                popup.getScene().setRoot(root);
                btn.setOnAction(buttonActionHandler);
            }

            control.buttonProperty().addListener(buttonChangeListener);
            control.popupRootProperty().addListener(popupRootChangeListener);

            getChildren().add(anchor);
        }
        private final EventHandler<ActionEvent> buttonActionHandler = (ev) -> {
            Button b = (Button) ev.getSource();
            double x = b.localToScreen(b.getBoundsInLocal()).getMinX();
            double y = b.localToScreen(b.getBoundsInLocal()).getMinY();
            popup.show(b, x, y + b.getHeight());

        };
        private final ChangeListener<? super Button> buttonChangeListener = (v, ov, nv) -> {
            if (ov != null) {
                ov.setOnAction(null);
                anchor.getChildren().remove(ov);
          
            }
            if (nv != null) {
                anchor.getChildren().add(nv);
                nv.setOnAction(buttonActionHandler);
                //AnchorPane.setLeftAnchor(nv, 0d);
                //AnchorPane.setRightAnchor(nv, 0d);
            
                
            }
        };

        private final ChangeListener<? super Parent> popupRootChangeListener = (v, ov, nv) -> {
            popup.getScene().setRoot(nv);
        };

    }

}
