package org.vns.javafx.dock.api.properties;

import javafx.beans.property.ObjectPropertyBase;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery
 * @param <T> ???
 */
public class DockPaneHandlerProperty<T extends TargetContext> extends ObjectPropertyBase<T> {

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
