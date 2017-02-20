/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor.tmp;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public class TreeItemEx<T> extends TreeItem{
    
    private TreeCell cell;
    private Node node;
    private EventHandler eventHandler;
    //private TreeItemEx dragSource;
            
    public TreeItemEx() {
    }

    public TreeItemEx(T value) {
        super(value);
    }

    public TreeItemEx(T value, Node graphic) {
        super(value, graphic);
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public TreeCell getCell() {
        return cell;
    }

    public void setCell(TreeCell cell) {
        this.cell = cell;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    public boolean isAcceptable(Object obj) {
        return TreeItemRegistry.getInstance().getBuilder(obj).isAcceptable(obj);
    }
    public TreeItemEx accept(Object obj) {
        return TreeItemRegistry.getInstance().getBuilder(getNode()).accept(this,obj);
    }  
    public TreeItemEx release(Object obj) {
        return TreeItemRegistry.getInstance().getBuilder(getNode()).release(this,obj);
    }  
    
}
