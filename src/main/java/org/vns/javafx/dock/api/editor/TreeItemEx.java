package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 * @param <T> specifies value type
 */
public class TreeItemEx<T> extends TreeItem{
    
            
    public TreeItemEx() {
        
    }

    public TreeItemEx(T value) {
        super(value);
    }

    public TreeItemEx(T value, Node graphic) {
        super(value, graphic);
    }

}
