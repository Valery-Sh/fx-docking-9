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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery
 */
public class FontPropertyEditor extends Control implements PropertyEditor<Font> {

    private Button editorButton;

    public FontPropertyEditor() {
        init();
    }

    private void init() {
        editorButton = new Button("Arial");
    }

    public Button getEditorButton() {
        return editorButton;
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
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

    @Override
    public Skin<?> createDefaultSkin() {
        return new FontPropertyEditorSkin(this);
    }

    public static class FontPropertyEditorSkin extends SkinBase<FontPropertyEditor> {
        private AnchorPane anchor; 
        public FontPropertyEditorSkin(FontPropertyEditor control) {
            super(control);
            
            anchor = new AnchorPane();
            
            Button btn = control.getEditorButton();
            btn.setTextOverrun(OverrunStyle.CLIP);
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(new Double[]{
                0.0, 0.0,
                7.0, 0.0,
                3.5, 4.0});

            btn.setGraphic(polygon);
            btn.setContentDisplay(ContentDisplay.RIGHT);
            btn.setAlignment(Pos.CENTER_RIGHT);
            AnchorPane.setLeftAnchor(btn, 0d);
            AnchorPane.setRightAnchor(btn, 0d);
            anchor.getChildren().add(btn);
            getChildren().add(anchor);
            
            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.getScene().setRoot(new FontPane());
            btn.setOnAction(a -> {
                System.err.println("INSETS = " + btn.getInsets());
                double x = btn.localToScreen(btn.getBoundsInLocal()).getMinX();
                double y = btn.localToScreen(btn.getBoundsInLocal()).getMinY();
                popup.show(btn, x, y + btn.getHeight());
            });
            
        }

    }
}
