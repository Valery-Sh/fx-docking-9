package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 * @param <T>
 */
public class TreeItemEx<T> extends TreeItem{
    
    /* 10.04 private EventHandler eventHandler;*/
            
    public TreeItemEx() {
    }

    public TreeItemEx(T value) {
        super(value);
    }

    public TreeItemEx(T value, Node graphic) {
        super(value, graphic);
    }

/*10.04    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
*/
/*    public TreeCell getCell() {
        return cell;
    }

    public void setCell(TreeCell cell) {
        this.cell = cell;
    }
*/

/* 10.04    public boolean isAcceptable(Object obj) {
        return TreeItemRegistry.getInstance().getBuilder(obj).isAcceptable(obj);
    }
*/    
/*    public TreeItemEx accept(Object obj) {
        Node node = ((ItemValue)getValue()).getTreeItemNode();
        return TreeItemRegistry.getInstance().getBuilder(node).accept(this,obj);
    }  
    public TreeItemEx release(Object obj) {
        Node node = ((ItemValue)getValue()).getTreeItemNode();
        return TreeItemRegistry.getInstance().getBuilder(node).release(this,obj);
    }  
*/    
}
