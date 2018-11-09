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
package org.vns.javafx.dock.api.demo;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.vns.javafx.scene.control.editors.ButtonTypeComboBoxPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestCssCustomClass extends HBox {

    public TestCssCustomClass() {
        
        Button b1 = new Button("Button1");
        b1.setId("b1");
        b1.getStyleClass().add("btn1");
        Button b2 = new Button("Button2");
        b2.setId("b2");
        b2.getStyleClass().add("btn2");
        MyButton myBtn1 = new MyButton("My Button"); 
        myBtn1.getStyleClass().add("my-btn1");
        getChildren().addAll(b1, b2, myBtn1);
        ButtonTypeComboBoxPropertyEditor editor = new ButtonTypeComboBoxPropertyEditor();
        getChildren().add(editor);
        getStyleClass().add("my-hbox");
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("resources/demo.css").toExternalForm();
    }

    public static class MyButton extends Button {

        public MyButton() {
        }

        public MyButton(String text) {
            super(text);
            getStyleClass().add("my-button");
        }

        
        public String getUserAgentStylesheet() {
            sceneProperty().addListener((v,ov,nv) -> {
                
            });
            return getClass().getResource("resources/demo_1.css").toExternalForm();
        }
    }
}
