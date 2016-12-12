/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.beans.property.ObjectPropertyBase;

public class DockPaneDelegateProperty<T extends DockPaneDelegate> extends ObjectPropertyBase<T> {

    public DockPaneDelegateProperty() {
    }
    
    public DockPaneDelegateProperty(T initialValue) {
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
