package org.vns.javafx.dock.api.properties;

import javafx.beans.property.BooleanPropertyBase;

/**
 *
 * @author Valery
 */
public class DockResizableProperty extends BooleanPropertyBase {

    public DockResizableProperty() {
    }

    public DockResizableProperty(boolean initialValue) {
        super(initialValue);
    }

    @Override
    public Object getBean() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "resizable";
    }
    
}


