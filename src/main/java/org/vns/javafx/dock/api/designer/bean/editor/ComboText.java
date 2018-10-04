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
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
public class ComboText extends Control implements PropertyEditor<Font>{

    private final ObjectProperty<Button> button = new SimpleObjectProperty<>();
    private final ObjectProperty<Parent> popupRoot = new SimpleObjectProperty<>();

    public ComboText() {
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
        return new ComboTextSkin(this);
    }

    public static void setDefaultButtonGraphic(Button btn) {
        Polygon polygon = (Polygon) createTriangle();
        btn.setGraphic(polygon);
    }

    public static void setDefaultLayout(Button btn) {
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setAlignment(Pos.CENTER_LEFT);
    
    }

    public static Polygon createTriangle() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            0.0, 0.0,
            7.0, 0.0,
            3.5, 4.0});
        return polygon;
    }

    @Override
    public void bind(Property<Font> property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bindBidirectional(Property<Font> property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unbind() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEditable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEditable(boolean editable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class ComboTextSkin extends SkinBase<ComboText> {

        private AnchorPane anchor = null;
        private Popup popup;

        public ComboTextSkin(ComboText control) {
            super(control);
            Button btnGr = new Button();
            
            anchor = new AnchorPane() {
                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                }
            };
            anchor.getStyleClass().add("button");

            Shape graphic = ComboText.createTriangle();
            //StackPane sp = new StackPane(graphic);
            //sp.setStyle("-fx-background-color: aqua");
            Button btn = control.getButton();
            

            //Insets ins = btn.getInsets();
            Insets insGr = btnGr.getInsets();

            btnGr.getStyleClass().clear();

            btnGr.setGraphic(graphic);
            if (btn == null) {
                btn = new Button();
                btn.getStyleClass().clear();
                ComboText.setDefaultLayout(btn);
                //ComboButton.setDefaultButtonGraphic(btn);
                control.setButton(btn);

                AnchorPane.setLeftAnchor(btn, 0d);
                AnchorPane.setRightAnchor(btn, 0d);
                AnchorPane.setTopAnchor(btn, 0d);
                AnchorPane.setBottomAnchor(btn, 0d);
                AnchorPane.setRightAnchor(btnGr, 0d);
                AnchorPane.setTopAnchor(btnGr, 0d);
                AnchorPane.setBottomAnchor(btnGr, 0d);
                //anchor.set
            }

            anchor.getChildren().addAll(btn, btnGr);

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
            //btnGr.getInsets();
            //anchor.setPadding(new Insets(4, 4, 4, 8));
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
