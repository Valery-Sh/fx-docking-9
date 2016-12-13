package org.vns.javafx.dock.api.properties;

import javafx.beans.property.ObjectPropertyBase;
import org.vns.javafx.dock.api.DockPaneDelegate;

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
