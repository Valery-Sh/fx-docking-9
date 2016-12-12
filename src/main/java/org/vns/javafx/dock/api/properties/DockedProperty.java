/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.properties;

import javafx.beans.property.BooleanPropertyBase;

/**
 *
 * @author Valery
 */
public class DockedProperty extends BooleanPropertyBase {

    public DockedProperty() {
    }

    public DockedProperty(boolean initialValue) {
        super(initialValue);
    }

    @Override
    public Object getBean() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "docked";
    }
    
}

