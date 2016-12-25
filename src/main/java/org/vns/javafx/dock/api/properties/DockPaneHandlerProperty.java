/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.properties;

import javafx.beans.property.ObjectPropertyBase;
import org.vns.javafx.dock.api.DockPaneHandler;

/**
 *
 * @author Valery
 */
public class DockPaneHandlerProperty<T extends DockPaneHandler> extends ObjectPropertyBase<T> {

    public DockPaneHandlerProperty() {
    }
    
    public DockPaneHandlerProperty(T initialValue) {
        super(initialValue);
        //initialValue
    }

    
    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return "dock-pane-delegate";
    }

}
