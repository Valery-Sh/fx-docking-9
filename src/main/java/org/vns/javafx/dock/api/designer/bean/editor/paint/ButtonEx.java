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
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Paint;

/**
 *
 * @author Nastia
 */
public class ButtonEx extends Control {
    
    private ContentComboBox combo;/* = new ContentComboBox() {
        @Override
        public void layoutChildren() {
            super.layoutChildren();
        }
    };
    */
    @Override
    protected void layoutChildren() {
         combo.layout();
    }

    
    @Override
    protected Skin<?> createDefaultSkin() {
        
        return new ButtonExSkin(this);
    }
/*    protected double computeMinWidth(double width) {
        
    }
*/
    public static class ButtonExSkin extends SkinBase<ButtonEx> {

        public ButtonExSkin(ButtonEx control) {
            super(control);
            Button b = new Button("Valery");
            control.combo = new ContentComboBox<>();
            
            //StackPane pane = new StackPane(control.combo);
            //StackPane.setAlignment(control.combo, Pos.CENTER_LEFT);
            //pane.setStyle("-fx-background-color: aqua");
            //getChildren().add(new AnchorPane(control.combo));
            getChildren().add(new Group(control.combo));
        }

    }
}
