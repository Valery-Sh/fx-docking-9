/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Valery
 */
public class TextInputControlItemBuilder extends DefaultTreeItemBuilder {

    @Override
    public boolean isAcceptable(Object obj) {
        return false;
    }
    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if ( obj instanceof TextInputControl ) {
            text = ((TextInputControl) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }
    
}
