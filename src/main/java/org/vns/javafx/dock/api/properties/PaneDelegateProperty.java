package org.vns.javafx.dock.api.properties;

import javafx.beans.property.ObjectPropertyBase;
import org.vns.javafx.dock.api.PaneDelegate;

public class PaneDelegateProperty<T extends PaneDelegate> extends ObjectPropertyBase<T> {

    public PaneDelegateProperty() {
    }
    
    public PaneDelegateProperty(T initialValue) {
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
