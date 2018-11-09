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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Popup;
import org.vns.javafx.scene.control.editors.skin.BaseEditorSkin;

/**
 *
 * @author Valery Shyshkin
 */
public class ComboButtonPropertyEditor<T> extends AbstractPropertyEditor<T> {

    private final ReadOnlyObjectWrapper<Button> buttonWrapper = new ReadOnlyObjectWrapper<>(new Button());
    private final ObjectProperty<Parent> popupRoot = new SimpleObjectProperty<>();

    public ComboButtonPropertyEditor() {
        this(null);
    }

    public ComboButtonPropertyEditor(String name) {
        super(name);
        init();
    }

    private void init() {
        getStyleClass().add("combo-button");
    }

    public ReadOnlyObjectProperty<Button> buttonProperty() {
        return buttonWrapper.getReadOnlyProperty();
    }

    public Button getButton() {
        return buttonWrapper.getValue();
    }

    protected void setButton(Button button) {
        this.buttonWrapper.setValue(button);
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
    public Skin<?> createDefaultSkin() {
        return new ComboButtonPropertyEditorSkin(this);
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
    public void bind(ReadOnlyProperty<T> property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bindBidirectional(Property<T> property) {
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

    @Override
    protected Node createEditorNode() {
        return new AnchorPane();
    }

    public static class ComboButtonPropertyEditorSkin extends BaseEditorSkin {

        private AnchorPane anchor = null;
        private Popup popup;

        public ComboButtonPropertyEditorSkin(ComboButtonPropertyEditor control) {
            super(control);
            Button btnGr = new Button();

            anchor = (AnchorPane) control.getEditorNode();
            
            anchor.getStyleClass().add("button");

            Shape graphic = ComboButtonPropertyEditor.createTriangle();

            Button btn = control.getButton();
            btn.setId("comboButton");
//            btn.setText("Arial");
            //Insets ins = btn.getInsets();
            //Insets insGr = btnGr.getInsets();
            btnGr.getStyleClass().clear();

            btnGr.setGraphic(graphic);

           
            btn.getStyleClass().clear();
            ComboButtonPropertyEditor.setDefaultLayout(btn);

            AnchorPane.setLeftAnchor(btn, 0d);
            AnchorPane.setRightAnchor(btn, 0d);
            AnchorPane.setTopAnchor(btn, 0d);
            AnchorPane.setBottomAnchor(btn, 0d);
            
            AnchorPane.setRightAnchor(btnGr, 0d);
            AnchorPane.setTopAnchor(btnGr, 0d);
            AnchorPane.setBottomAnchor(btnGr, 0d);
            //anchor.set

            anchor.getChildren().addAll(btn, btnGr);

            popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);

            Parent root = control.getPopupRoot();
            if (root != null) {
                popup.getScene().setRoot(root);
                btn.setOnAction(buttonActionHandler);
                btnGr.setOnAction(buttonActionHandler);
            }

            //control.buttonProperty().addListener(buttonChangeListener);
            control.popupRootProperty().addListener(popupRootChangeListener);

            //getChildren().add(anchor);
            control.editableProperty().addListener((ObservableValue<? extends Boolean> v, Boolean ov, Boolean nv) -> {
                anchor.getChildren().get(0).setDisable(!nv);
                anchor.getChildren().get(1).setDisable(!nv);
            });
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
