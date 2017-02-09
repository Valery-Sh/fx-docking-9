/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.controls;

import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;

/**
 *
 * @author Valery
 */
public class CustomControlSkin extends SkinBase<CustomControl> {

        public CustomControlSkin(CustomControl control) {
            super(control);
            getChildren().add(control.getDelegate());
            Button b = new Button("Title button");
            control.getDelegate().getChildren().addAll(b,control.getContent());
            System.err.println("control.getContent()=" + control.getContent());
        }
    }
